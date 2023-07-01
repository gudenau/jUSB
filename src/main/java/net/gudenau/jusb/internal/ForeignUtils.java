package net.gudenau.jusb.internal;

import java.io.IOException;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public final class ForeignUtils {
    public static final ValueLayout.OfByte S8 = ValueLayout.JAVA_BYTE;
    public static final ValueLayout.OfShort S16 = ValueLayout.JAVA_SHORT;
    public static final ValueLayout.OfInt S32 = ValueLayout.JAVA_INT;
    public static final ValueLayout.OfLong S64 = ValueLayout.JAVA_LONG;
    
    //TODO Is there an unsigned representation of primitives?
    public static final ValueLayout.OfByte U8 = ValueLayout.JAVA_BYTE;
    public static final ValueLayout.OfShort U16 = ValueLayout.JAVA_SHORT;
    public static final ValueLayout.OfInt U32 = ValueLayout.JAVA_INT;
    public static final ValueLayout.OfLong U64 = ValueLayout.JAVA_LONG;

    public static final ValueLayout.OfAddress ADDRESS = ValueLayout.ADDRESS;
    public static final ValueLayout.OfAddress UNBOUND_ADDRESS = ADDRESS.asUnbounded();

    /**
     * Creates a {@link GroupLayout} with the provided elements and inserts padding to ensure that correct alignment is
     * retained.
     *
     * @param members The members of the structure
     * @return The padded layout
     */
    public static GroupLayout structure(MemoryLayout... members) {
        var memberList = new ArrayList<>(List.of(members));

        long currentAlignment = 1;
        long maxAlignment = 1;
        long size = 0;
        for (int i = 0; i < memberList.size(); i++) {
            var member = memberList.get(i);

            var alignment = member.byteAlignment();
            if (alignment > currentAlignment) {
                // When already aligned this computes the alignment value
                long padding = alignment - (size & (alignment - 1));
                if(padding != alignment) {
                    size += padding;
                    memberList.add(i++, MemoryLayout.paddingLayout(padding << 3));
                }
            }
            currentAlignment = alignment;
            maxAlignment = Math.max(maxAlignment, alignment);

            size += member.byteSize();
        }

        long padding = maxAlignment - (size & (maxAlignment - 1));
        if(padding != maxAlignment) {
            memberList.add(MemoryLayout.paddingLayout(padding << 3));
        }

        return MemoryLayout.structLayout(memberList.toArray(MemoryLayout[]::new));
    }

    /**
     * Gets a {@link VarHandle} for a named element inside the provided {@link MemoryLayout}.
     *
     * @param layout The layout that contains the member
     * @param name The name of the member
     * @return The handle to the member
     */
    public static VarHandle layoutHandle(MemoryLayout layout, String name) {
        return layout.varHandle(MemoryLayout.PathElement.groupElement(name));
    }
    
    private static final Linker LINKER = Linker.nativeLinker();
    private static final Map<String, Binder> BINDERS = new HashMap<>();

    /**
     * Either returns an existing {@link Binder} for a library or loads a library and creates a new {@link Binder} for
     * it.
     *
     * @param library The name of the library
     * @return The binder for the library
     */
    public static Binder binder(String library) {
        synchronized(BINDERS) {
            return BINDERS.computeIfAbsent(library, ForeignUtils::createBinder);
        }
    }
    
    private static Binder createBinder(String library) {
        var os = OperatingSystem.OPERATING_SYSTEM;
        
        if(os == OperatingSystem.UNKNOWN) {
            throw new RuntimeException("Unsupported OS: " + System.getProperty("os.name"));
        }
        
        boolean delete = false;
        Path path;
        if(os == OperatingSystem.LINUX) {
            path = Path.of("/usr/lib/" + library + os.extension());
        }else{
            delete = os != OperatingSystem.WINDOWS;
            var arch = Architecture.ARCHITECTURE;
            if(arch == Architecture.UNKNOWN) {
                throw new RuntimeException("Unsupported architecture: " + System.getProperty("os.arch"));
            }
    
            try(var input = ForeignUtils.class.getResourceAsStream("/res/jusb/natives/" + arch + "/" + os + "/" + library + os.extension())) {
                if(input == null) {
                    throw new RuntimeException("Unsupported OS/architecture combo: " + System.getProperty("os.name") + "/" + System.getProperty("os.arch"));
                }
                path = Files.createTempFile("jUSB", os.extension());
                try(var output = Files.newOutputStream(path)) {
                    input.transferTo(output);
                }
            } catch(IOException e) {
                throw new RuntimeException("Failed to extract natives", e);
            }
        }
        
        var session = Arena.openShared();
        SymbolLookup lookup;
        try {
            lookup = SymbolLookup.libraryLookup(path, session.scope());
        } catch(Throwable e) {
            throw new RuntimeException("Failed to load " + library, e);
        }

        // Windows is quirky and won't let you delete an open file.
        if(delete) {
            try {
                Files.deleteIfExists(path);
            } catch(IOException ignored) {}
        }
        
        return new Binder() {
            @Override
            public MethodHandle bind(String name, FunctionDescriptor descriptor, Linker.Option... options) {
                return LINKER.downcallHandle(lookup(name), descriptor, options);
            }
    
            @Override
            public MemorySegment lookup(String name) {
                return lookup.find(name).orElseThrow(()-> new RuntimeException("Failed to find symbol " + name + " in " + library));
            }
        };
    }

    /**
     * Creates an unbound {@link MethodHandle} from the provided {@link FunctionDescriptor}. The argument list is
     * prepended with a pointer to the native function to call.
     *
     * @param descriptor The function descriptor
     * @return The method handle
     */
    public static MethodHandle downcall(FunctionDescriptor descriptor) {
        return LINKER.downcallHandle(descriptor);
    }

    /**
     * Finds the {@link MethodHandle} for the `invoke` method that corresponds to the provided
     * {@link FunctionDescriptor}. The returned handle is not bound and is prepended with an argument for `this`.
     *
     * @param owner The owner of the method
     * @param descriptor The descriptor of the function
     * @return The unbound method handle
     */
    public static MethodHandle findBaseHandle(Class<?> owner, FunctionDescriptor descriptor) {
        try {
            return MethodHandles.lookup()
                .findVirtual(owner, "invoke", descriptor.toMethodType());
        } catch(NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("Failed to find base handle for " + owner.getName(), e);
        }
    }
    
    public static MemorySegment upcall(MethodHandle handle, FunctionDescriptor descriptpr, SegmentScope scope) {
        return LINKER.upcallStub(handle, descriptpr, scope);
    }

    public static void verifySize(MemorySegment segment, MemoryLayout layout) {
        if(segment.byteSize() < layout.byteSize()) {
            throw new IllegalArgumentException("segment was too small for layout of size " + layout.byteSize());
        }
    }

    /**
     * Creates a list of structures from a pointer.
     *
     * @param count The size of the list
     * @param segment The native memory of the list
     * @param layout The native layout of the list elements
     * @param factory The factory for the element container
     * @return The list of elements
     * @param <T> The type of element
     */
    public static <T> List<T> array(int count, MemorySegment segment, GroupLayout layout, Function<MemorySegment, T> factory) {
        var size = layout.byteSize();
        segment = segment.asReadOnly();

        var list = new ArrayList<T>(count);
        for(int i = 0; i < count; i++) {
            list.add(factory.apply(segment.asSlice(i * size, size)));
        }

        return Collections.unmodifiableList(list);
    }

    /**
     * A simple FFI binder helper.
     */
    public interface Binder {
        default MethodHandle bind(String name, MemoryLayout result, MemoryLayout... args) {
            var descriptor = result == null ? FunctionDescriptor.ofVoid(args) : FunctionDescriptor.of(result, args);
            return bind(name, descriptor);
        }
    
        MethodHandle bind(String name, FunctionDescriptor descriptor, Linker.Option... options);
        MemorySegment lookup(String name);
    }
    
    private ForeignUtils() {
        throw new AssertionError();
    }
}
