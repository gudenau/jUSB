package net.gudenau.jusb.descriptor;

import net.gudenau.jusb.internal.libusb.descriptor.LibUsbInterface;

import java.util.List;

/**
 * A container for interface descriptor alt settings.
 */
public sealed interface UsbInterface permits LibUsbInterface {
    /**
     * The alt settings for this interface.
     *
     * @return the alt settings
     */
    List<UsbInterfaceDescriptor> altsetting();
}
