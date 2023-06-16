package net.gudenau.jusb.internal.libusb;

import net.gudenau.jusb.internal.ForeignUtils;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

@FunctionalInterface
public interface LibUsbLogCallback {
    void invoke(LibUsbContext ctx, int level, String str);
    
    default void invoke(MemorySegment ctx, int level, MemorySegment str) {
        invoke(ctx.equals(MemorySegment.NULL) ? null : new LibUsbContext().set(ctx), level, str == null ? null : str.getUtf8String(0));
    }
    
    static MemorySegment allocate(LibUsbLogCallback callback, SegmentScope scope) {
        return ForeignUtils.upcall(OfAddress.BASE_HANDLE.bindTo(callback), OfAddress.DESCRIPTOR, scope);
    }
    
    static LibUsbLogCallback ofAddress(MemorySegment address) {
        return new OfAddress(address);
    }
    
    final class OfAddress implements LibUsbLogCallback {
        private static final FunctionDescriptor DESCRIPTOR = FunctionDescriptor.ofVoid(ADDRESS, JAVA_INT, ADDRESS);
        private static final MethodHandle DOWNCALL = ForeignUtils.downcall(DESCRIPTOR);
        private static final MethodHandle BASE_HANDLE = ForeignUtils.findBaseHandle(LibUsbLogCallback.class, DESCRIPTOR);
        
        private final MethodHandle handle;
    
        public OfAddress(MemorySegment address) {
            handle = DOWNCALL.bindTo(address);
        }
    
        @Override
        public void invoke(MemorySegment ctx, int level, MemorySegment str) {
            try {
                handle.invokeExact(ctx, level, str);
            } catch(Throwable e) {
                throw new RuntimeException("Failed to invoke LibUsbLogCallback", e);
            }
        }
    
        @Override
        public void invoke(LibUsbContext ctx, int level, String str) {
            try(var session = Arena.openConfined()) {
                invoke(
                    ctx == null ? MemorySegment.NULL : ctx.address(),
                    level,
                    str == null ? MemorySegment.NULL : session.allocateUtf8String(str)
                );
            }
        }
    }
}
