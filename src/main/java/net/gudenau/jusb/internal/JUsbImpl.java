package net.gudenau.jusb.internal;

import net.gudenau.jusb.JUsb;
import net.gudenau.jusb.UsbDevice;
import net.gudenau.jusb.UsbException;
import net.gudenau.jusb.internal.libusb.LibUsb;
import net.gudenau.jusb.internal.libusb.LibUsbContext;
import net.gudenau.jusb.internal.libusb.LibUsbDevice;

import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.List;

public final class JUsbImpl implements JUsb {
    public static JUsbImpl init() throws UsbException {
        var context = new LibUsbContext();
        var result = LibUsb.libusb_init(context);
        if(result != LibUsb.LIBUSB_SUCCESS) {
            throw new UsbException("Failed to initialize libusb: " + LibUsb.libusb_error_name(result));
        }
        
        return new JUsbImpl(context);
    }
    
    private final LibUsbContext context;
    
    public JUsbImpl(LibUsbContext context) {
        this.context = context;
    }
    
    @Override
    public List<UsbDevice> devices() throws UsbException {
        try(var session = MemorySession.openConfined()) {
            var pointer = session.allocate(ValueLayout.ADDRESS).address();
            var result = LibUsb.libusb_get_device_list(context, pointer);
            if(result <= 0) {
                throw new UsbException("Failed to get device list: " + LibUsb.libusb_error_name((int) result));
            }
            pointer = pointer.get(ValueLayout.ADDRESS, 0);
    
            List<UsbDevice> devices;
            try {
                int deviceCount = (int) Math.min(Integer.MAX_VALUE, result);
                devices = new ArrayList<>(deviceCount);
                for(int i = 0; i < deviceCount; i++) {
                    devices.add(new UsbDeviceImpl(new LibUsbDevice(pointer.getAtIndex(ValueLayout.ADDRESS, i))));
                }
            } catch(Throwable t) {
                LibUsb.libusb_free_device_list(pointer, true);
                throw t;
            }
            LibUsb.libusb_free_device_list(pointer, false);
            return devices;
        }
    }
    
    @Override
    public void close() throws UsbException {
        LibUsb.libusb_exit(context);
    }
}
