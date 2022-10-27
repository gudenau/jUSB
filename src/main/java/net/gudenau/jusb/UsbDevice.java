package net.gudenau.jusb;

import net.gudenau.jusb.descriptor.UsbDeviceDescriptor;
import net.gudenau.jusb.internal.UsbDeviceImpl;

public sealed interface UsbDevice extends AutoCloseable permits UsbDeviceImpl {
    UsbDeviceDescriptor descriptor();
    
    UsbDeviceHandle open() throws UsbException;
    
    @Override void close();
}
