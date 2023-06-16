package net.gudenau.jusb.internal;

import java.io.IOException;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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
    
    public static GroupLayout structure(MemoryLayout... members) {
        return MemoryLayout.structLayout(members);
    }
    
    public static VarHandle layoutHandle(MemoryLayout layout, String name) {
        return layout.varHandle(MemoryLayout.PathElement.groupElement(name));
    }
    
    private static final Linker LINKER = Linker.nativeLinker();
    private static final Map<String, Binder> BINDERS = new HashMap<>();
    
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
    
    public static MethodHandle downcall(FunctionDescriptor descriptor) {
        return LINKER.downcallHandle(descriptor);
    }
    
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
