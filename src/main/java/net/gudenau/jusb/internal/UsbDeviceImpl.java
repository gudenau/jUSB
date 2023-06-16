package net.gudenau.jusb.internal;

import net.gudenau.jusb.UsbDevice;
import net.gudenau.jusb.UsbDeviceHandle;
import net.gudenau.jusb.UsbException;
import net.gudenau.jusb.descriptor.UsbDeviceDescriptor;
import net.gudenau.jusb.internal.libusb.LibUsb;
import net.gudenau.jusb.internal.libusb.LibUsbDevice;
import net.gudenau.jusb.internal.libusb.LibUsbDeviceDescriptor;
import net.gudenau.jusb.internal.libusb.LibUsbDeviceHandle;

import java.lang.foreign.Arena;
import java.lang.foreign.ValueLayout;

public final class UsbDeviceImpl implements UsbDevice {
    private final JUsbImpl usb;
    private final LibUsbDevice device;
    private final Arena session;
    private final LibUsbDeviceDescriptor descriptor;
    
    public UsbDeviceImpl(JUsbImpl usb, LibUsbDevice device) {
        this.usb = usb;
        this.device = device;
        session = Arena.openShared();
        descriptor = new LibUsbDeviceDescriptor(session);
        // Doesn't fail anymore
        LibUsb.libusb_get_device_descriptor(device, descriptor);
    }
    
    @Override
    public UsbDeviceDescriptor descriptor() {
        return descriptor;
    }
    
    @Override
    public UsbDeviceHandle open() throws UsbException {
        try(var session = Arena.openConfined()) {
            var pointer = session.allocate(ValueLayout.ADDRESS);
            var result = LibUsb.libusb_open(device, pointer);
            if(result != LibUsb.LIBUSB_SUCCESS) {
                throw new UsbException("Failed to open device: " + LibUsb.libusb_error_name(result));
            }
            
            return new UsbDeviceHandleImpl(usb, new LibUsbDeviceHandle(pointer.get(ValueLayout.ADDRESS, 0)));
        }
    }
    
    @Override
    public void close() {
        LibUsb.libusb_unref_device(device);
        session.close();
    }
}
