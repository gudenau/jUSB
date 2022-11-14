package net.gudenau.jusb;

import net.gudenau.jusb.descriptor.UsbDeviceDescriptor;
import net.gudenau.jusb.internal.UsbDeviceImpl;

/**
 * A USB device.
 */
public sealed interface UsbDevice extends AutoCloseable permits UsbDeviceImpl {
    /**
     * Gets the device descriptor of this USB device.
     *
     * @return The device descriptor
     */
    UsbDeviceDescriptor descriptor();
    
    /**
     * Attempts to open this device.
     *
     * @return The new device handle
     * @throws UsbException if there was en error opening the device
     */
    UsbDeviceHandle open() throws UsbException;
    
    @Override void close();
}
