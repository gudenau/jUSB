package net.gudenau.jusb.internal.libusb;

import net.gudenau.jusb.internal.ForeignUtils;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

@FunctionalInterface
public interface LibUsbHotplugCallback {
    boolean invoke(LibUsbContext ctx, LibUsbDevice device, int event, MemoryAddress user_data);
    
    default int invoke(MemoryAddress ctx, MemoryAddress device, int event, MemoryAddress user_data) {
        return invoke(new LibUsbContext().set(ctx), new LibUsbDevice(device), event, user_data) ? 1 : 0;
    }
    
    static MemorySegment allocate(LibUsbHotplugCallback callback, MemorySession session) {
        return ForeignUtils.upcall(OfAddress.BASE_HANDLE.bindTo(callback), OfAddress.DESCRIPTOR, session);
    }
    
    static LibUsbHotplugCallback ofAddress(MemoryAddress address) {
        return new LibUsbHotplugCallback.OfAddress(address);
    }
    
    final class OfAddress implements LibUsbHotplugCallback {
        private static final FunctionDescriptor DESCRIPTOR = FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS, JAVA_INT, ADDRESS);
        private static final MethodHandle DOWNCALL = ForeignUtils.downcall(DESCRIPTOR);
        private static final MethodHandle BASE_HANDLE = ForeignUtils.findBaseHandle(LibUsbHotplugCallback.class, DESCRIPTOR);
        
        private final MethodHandle handle;
        
        private OfAddress(MemoryAddress address) {
            handle = BASE_HANDLE.bindTo(address);
        }
    
        @Override
        public boolean invoke(LibUsbContext ctx, LibUsbDevice device, int event, MemoryAddress user_data) {
            return invoke(
                ctx == null ? MemoryAddress.NULL : ctx.address(),
                device == null ? MemoryAddress.NULL : device.address(),
                event,
                user_data
            ) != 0;
        }
    
        @Override
        public int invoke(MemoryAddress ctx, MemoryAddress device, int event, MemoryAddress user_data) {
            try {
                return (int) handle.invokeExact((Addressable) ctx, (Addressable) device, event, (Addressable) user_data);
            } catch(Throwable e) {
                throw new RuntimeException("Failed to invoke LibUsbHotplugCallback", e);
            }
        }
    }
}
