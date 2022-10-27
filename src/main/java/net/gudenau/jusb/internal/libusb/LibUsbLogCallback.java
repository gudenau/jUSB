package net.gudenau.jusb.internal.libusb;

import net.gudenau.jusb.internal.ForeignUtils;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

@FunctionalInterface
public interface LibUsbLogCallback {
    void invoke(LibUsbContext ctx, int level, String str);
    
    default void invoke(MemoryAddress ctx, int level, MemoryAddress str) {
        invoke(ctx.equals(MemoryAddress.NULL) ? null : new LibUsbContext().set(ctx), level, str == null ? null : str.getUtf8String(0));
    }
    
    static MemorySegment allocate(LibUsbLogCallback callback, MemorySession session) {
        return ForeignUtils.upcall(OfAddress.BASE_HANDLE.bindTo(callback), OfAddress.DESCRIPTOR, session);
    }
    
    static LibUsbLogCallback ofAddress(MemoryAddress address) {
        return new OfAddress(address);
    }
    
    final class OfAddress implements LibUsbLogCallback {
        private static final FunctionDescriptor DESCRIPTOR = FunctionDescriptor.ofVoid(ADDRESS, JAVA_INT, ADDRESS);
        private static final MethodHandle DOWNCALL = ForeignUtils.downcall(DESCRIPTOR);
        private static final MethodHandle BASE_HANDLE = ForeignUtils.findBaseHandle(LibUsbLogCallback.class, DESCRIPTOR);
        
        private final MethodHandle handle;
    
        public OfAddress(MemoryAddress address) {
            handle = DOWNCALL.bindTo(address);
        }
    
        @Override
        public void invoke(MemoryAddress ctx, int level, MemoryAddress str) {
            try {
                handle.invokeExact((Addressable) ctx, level, (Addressable) str);
            } catch(Throwable e) {
                throw new RuntimeException("Failed to invoke LibUsbLogCallback", e);
            }
        }
    
        @Override
        public void invoke(LibUsbContext ctx, int level, String str) {
            try(var session = MemorySession.openConfined()) {
                invoke(
                    ctx == null ? MemoryAddress.NULL : ctx.address(),
                    level,
                    str == null ? MemoryAddress.NULL : session.allocateUtf8String(str).address()
                );
            }
        }
    }
}
