package net.gudenau.jusb.internal;

import net.gudenau.jusb.UsbDeviceHandle;
import net.gudenau.jusb.UsbDirection;
import net.gudenau.jusb.UsbException;
import net.gudenau.jusb.internal.libusb.LibUsb;
import net.gudenau.jusb.internal.libusb.LibUsbDeviceHandle;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;

public final class UsbDeviceHandleImpl implements UsbDeviceHandle {
    private final LibUsbDeviceHandle handle;
    
    public UsbDeviceHandleImpl(LibUsbDeviceHandle handle) {
        this.handle = handle;
        
        if(LibUsb.libusb_has_capability(LibUsb.LIBUSB_CAP_SUPPORTS_DETACH_KERNEL_DRIVER)) {
            LibUsb.libusb_set_auto_detach_kernel_driver(handle, true);
        }
    }
    
    @Override
    public void setConfiguration(int configuration) throws UsbException {
        var result = LibUsb.libusb_set_configuration(handle, configuration);
        if(result != LibUsb.LIBUSB_SUCCESS) {
            throw new UsbException("Failed to set device configuration: " + LibUsb.libusb_error_name(result));
        }
    }
    
    @Override
    public void claimInterface(int iface) throws UsbException {
        var result = LibUsb.libusb_claim_interface(handle, iface);
        if(result != LibUsb.LIBUSB_SUCCESS) {
            throw new UsbException("Failed to claim interface " + iface + ": " + LibUsb.libusb_error_name(result));
        }
    }
    
    @Override
    public int controlTransfer(int requestType, int request, int value, int index, ByteBuffer buffer, long timeout) throws UsbException, TimeoutException {
        var result = LibUsb.libusb_control_transfer(handle, (byte) requestType, (byte) request, (short) value, (short) index, MemorySegment.ofBuffer(buffer), (short) Math.min(buffer.remaining(), 0xFFFF), (int) Math.min(Integer.MAX_VALUE, timeout));
        if(result < 0) {
            if(result == LibUsb.LIBUSB_ERROR_TIMEOUT) {
                throw new TimeoutException("Failed to perform control transfer in time");
            } else {
                throw new UsbException("Failed to perform control transfer: " + LibUsb.libusb_error_name(result));
            }
        }
        buffer.position(buffer.position() + result);
        return result;
    }
    
    private static byte endpoint(int endpoint, UsbDirection direction) {
        return (byte)(endpoint | (direction == UsbDirection.IN ? LibUsb.LIBUSB_ENDPOINT_IN : LibUsb.LIBUSB_ENDPOINT_OUT));
    }
    
    @Override
    public int bulkTransfer(int endpoint, UsbDirection direction, ByteBuffer buffer, long timeout) throws UsbException, TimeoutException {
        if(direction == UsbDirection.IN && buffer.isReadOnly()) {
            throw new IllegalArgumentException("Buffer can't be read-only for an in transfer");
        }
        
        try(var session = MemorySession.openConfined()) {
            var segment = MemorySegment.ofBuffer(buffer);
            var transferredPointer = session.allocate(ValueLayout.JAVA_INT);
            var result = LibUsb.libusb_bulk_transfer(handle, endpoint(endpoint, direction), segment, buffer.remaining(), transferredPointer, (int) Math.min(Integer.MAX_VALUE, timeout));
            if(result != 0) {
                if(result == LibUsb.LIBUSB_ERROR_TIMEOUT) {
                    throw new TimeoutException("Failed to perform bulk transfer in time");
                } else {
                    throw new UsbException("Failed to perform bulk transfer: " + LibUsb.libusb_error_name(result));
                }
            }
            var transferred = transferredPointer.get(ValueLayout.JAVA_INT, 0);
            buffer.position(buffer.position() + transferred);
            return transferred;
        }
    }
    
    @Override
    public int interruptTransfer(int endpoint, UsbDirection direction, ByteBuffer buffer, long timeout) throws UsbException, TimeoutException {
        if(direction == UsbDirection.IN && buffer.isReadOnly()) {
            throw new IllegalArgumentException("Buffer can't be read-only for an in transfer");
        }
    
        try(var session = MemorySession.openConfined()) {
            var segment = MemorySegment.ofBuffer(buffer);
            var transferredPointer = session.allocate(ValueLayout.JAVA_INT);
            var result = LibUsb.libusb_interrupt_transfer(handle, endpoint(endpoint, direction), segment, buffer.remaining(), transferredPointer, (int) Math.min(Integer.MAX_VALUE, timeout));
            if(result != 0) {
                if(result == LibUsb.LIBUSB_ERROR_TIMEOUT) {
                    throw new TimeoutException("Failed to perform interrupt transfer in time");
                } else {
                    throw new UsbException("Failed to perform interrupt transfer: " + LibUsb.libusb_error_name(result));
                }
            }
            var transferred = transferredPointer.get(ValueLayout.JAVA_INT, 0);
            buffer.position(buffer.position() + transferred);
            return transferred;
        }
    }
    
    @Override
    public void close() {
        LibUsb.libusb_close(handle);
    }
}
