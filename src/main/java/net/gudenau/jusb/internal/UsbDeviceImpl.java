package net.gudenau.jusb.internal;

import net.gudenau.jusb.UsbDevice;
import net.gudenau.jusb.UsbDeviceHandle;
import net.gudenau.jusb.UsbException;
import net.gudenau.jusb.descriptor.UsbConfigDescriptor;
import net.gudenau.jusb.descriptor.UsbDeviceDescriptor;
import net.gudenau.jusb.internal.libusb.LibUsb;
import net.gudenau.jusb.internal.libusb.LibUsbDevice;
import net.gudenau.jusb.internal.libusb.descriptor.LibUsbConfigDescriptor;
import net.gudenau.jusb.internal.libusb.descriptor.LibUsbDeviceDescriptor;
import net.gudenau.jusb.internal.libusb.LibUsbDeviceHandle;

import java.lang.foreign.Arena;
import java.lang.foreign.ValueLayout;

import static net.gudenau.jusb.internal.ForeignUtils.UNBOUND_ADDRESS;

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
    public UsbConfigDescriptor configDescriptor() throws UsbException {
        try(var arena = Arena.openConfined()) {
            var pointer = arena.allocate(UNBOUND_ADDRESS);
            var result = LibUsb.libusb_get_active_config_descriptor(device, pointer);
            if(result != LibUsb.LIBUSB_SUCCESS) {
                throw new UsbException("Failed to get active configuration descriptor: " + LibUsb.libusb_error_name(result));
            }
            return new LibUsbConfigDescriptor(pointer.get(UNBOUND_ADDRESS, 0).asSlice(0, LibUsbConfigDescriptor.LAYOUT.byteSize()));
        }
    }

    @Override
    public UsbConfigDescriptor configDescriptor(int index) throws UsbException {
        try(var arena = Arena.openConfined()) {
            var pointer = arena.allocate(UNBOUND_ADDRESS);
            var result = LibUsb.libusb_get_config_descriptor(device, (byte) index, pointer);
            if(result != LibUsb.LIBUSB_SUCCESS) {
                throw new UsbException("Failed to get configuration descriptor: " + LibUsb.libusb_error_name(result));
            }
            return new LibUsbConfigDescriptor(pointer.get(UNBOUND_ADDRESS, 0).asSlice(0, LibUsbConfigDescriptor.LAYOUT.byteSize()));
        }
    }

    @Override
    public UsbConfigDescriptor configDescriptorByValue(int value) throws UsbException {
        try(var arena = Arena.openConfined()) {
            var pointer = arena.allocate(UNBOUND_ADDRESS);
            var result = LibUsb.libusb_get_config_descriptor_by_value(device, (byte) value, pointer);
            if(result != LibUsb.LIBUSB_SUCCESS) {
                throw new UsbException("Failed to get configuration descriptor by value: " + LibUsb.libusb_error_name(result));
            }
            return new LibUsbConfigDescriptor(pointer.get(UNBOUND_ADDRESS, 0).asSlice(0, LibUsbConfigDescriptor.LAYOUT.byteSize()));
        }
    }

    @Override
    public byte[] path() {
        try(var arena = Arena.openConfined()) {
            var data = arena.allocate(16);
            var result = LibUsb.libusb_get_port_numbers(device, data);
            if(result < LibUsb.LIBUSB_SUCCESS) {
                throw new RuntimeException("Failed to get device path: " + LibUsb.libusb_error_name(result));
            }

            var path = new byte[result];
            data.asByteBuffer().get(path);
            return path;
        }
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
