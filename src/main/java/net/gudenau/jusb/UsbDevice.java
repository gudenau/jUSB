package net.gudenau.jusb;

import net.gudenau.jusb.descriptor.UsbConfigDescriptor;
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
     * Gets the active configuration descriptor of this USB device.
     *
     * @return The active configuration descriptor
     */
    UsbConfigDescriptor configDescriptor() throws UsbException;

    /**
     * Gets the configuration descriptor at the provided index.
     *
     * @return The configuration descriptor
     */
    UsbConfigDescriptor configDescriptor(int index) throws UsbException;

    /**
     * Gets the configuration descriptor at the provided value.
     *
     * @return The configuration descriptor
     */
    UsbConfigDescriptor configDescriptorByValue(int value) throws UsbException;

    /**
     * Gets the path for this USB device.
     *
     * @return The device path
     */
    byte[] path();

    /**
     * Attempts to open this device.
     *
     * @return The new device handle
     * @throws UsbException if there was en error opening the device
     */
    UsbDeviceHandle open() throws UsbException;
    
    @Override void close();
}
