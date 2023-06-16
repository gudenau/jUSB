package net.gudenau.jusb.internal.libusb;

import net.gudenau.jusb.internal.ForeignUtils;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

@FunctionalInterface
public interface LibUsbHotplugCallback {
    boolean invoke(LibUsbContext ctx, LibUsbDevice device, int event, MemorySegment user_data);
    
    default int invoke(MemorySegment ctx, MemorySegment device, int event, MemorySegment user_data) {
        return invoke(new LibUsbContext().set(ctx), new LibUsbDevice(device), event, user_data) ? 1 : 0;
    }
    
    static MemorySegment allocate(LibUsbHotplugCallback callback, SegmentScope scope) {
        return ForeignUtils.upcall(OfAddress.BASE_HANDLE.bindTo(callback), OfAddress.DESCRIPTOR, scope);
    }
    
    static LibUsbHotplugCallback ofAddress(MemorySegment address) {
        return new LibUsbHotplugCallback.OfAddress(address);
    }
    
    final class OfAddress implements LibUsbHotplugCallback {
        private static final FunctionDescriptor DESCRIPTOR = FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS, JAVA_INT, ADDRESS);
        private static final MethodHandle DOWNCALL = ForeignUtils.downcall(DESCRIPTOR);
        private static final MethodHandle BASE_HANDLE = ForeignUtils.findBaseHandle(LibUsbHotplugCallback.class, DESCRIPTOR);
        
        private final MethodHandle handle;
        
        private OfAddress(MemorySegment address) {
            handle = BASE_HANDLE.bindTo(address);
        }
    
        @Override
        public boolean invoke(LibUsbContext ctx, LibUsbDevice device, int event, MemorySegment user_data) {
            return invoke(
                ctx == null ? MemorySegment.NULL : ctx.address(),
                device == null ? MemorySegment.NULL : device.address(),
                event,
                user_data
            ) != 0;
        }
    
        @Override
        public int invoke(MemorySegment ctx, MemorySegment device, int event, MemorySegment user_data) {
            try {
                return (int) handle.invokeExact(ctx, device, event, user_data);
            } catch(Throwable e) {
                throw new RuntimeException("Failed to invoke LibUsbHotplugCallback", e);
            }
        }
    }
}
