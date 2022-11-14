package net.gudenau.jusb.internal.libusb;

import net.gudenau.jusb.internal.ForeignUtils;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

@FunctionalInterface
public interface LibUsbTransferCallback {
    void invoke(LibUsbTransfer transfer);
    
    default void invoke(MemoryAddress transfer) {
        invoke(new LibUsbTransfer(transfer));
    }
    
    static MemorySegment allocate(LibUsbTransferCallback callback, MemorySession session) {
        return ForeignUtils.upcall(OfAddress.BASE_HANDLE.bindTo(callback), OfAddress.DESCRIPTOR, session);
    }
    
    static LibUsbTransferCallback ofAddress(MemoryAddress address) {
        return new OfAddress(address);
    }
    
    final class OfAddress implements LibUsbTransferCallback {
        private static final FunctionDescriptor DESCRIPTOR = FunctionDescriptor.ofVoid(ADDRESS);
        private static final MethodHandle DOWNCALL = ForeignUtils.downcall(DESCRIPTOR);
        private static final MethodHandle BASE_HANDLE = ForeignUtils.findBaseHandle(LibUsbTransferCallback.class, DESCRIPTOR);
        
        private final MethodHandle handle;
    
        public OfAddress(MemoryAddress address) {
            handle = DOWNCALL.bindTo(address);
        }
    
        @Override
        public void invoke(MemoryAddress transfer) {
            try {
                handle.invokeExact((Addressable) transfer);
            } catch(Throwable e) {
                throw new RuntimeException("Failed to invoke LibUsbTransferCallback", e);
            }
        }
    
        @Override
        public void invoke(LibUsbTransfer transfer) {
            invoke(transfer == null ? MemoryAddress.NULL : transfer.segment().address());
        }
    }
}
