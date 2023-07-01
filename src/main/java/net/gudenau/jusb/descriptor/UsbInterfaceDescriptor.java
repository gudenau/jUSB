package net.gudenau.jusb.descriptor;

import net.gudenau.jusb.internal.libusb.descriptor.LibUsbInterfaceDescriptor;

import java.util.List;

/**
 * A USB interface descriptor.
 */
public sealed interface UsbInterfaceDescriptor extends UsbDescriptor permits LibUsbInterfaceDescriptor {
    /**
     * Gets the number of this interface.
     *
     * @return the interface number
     */
    byte bInterfaceNumber();

    /**
     * Gets the alternate setting for this interface descriptor.
     *
     * @return the alt setting
     */
    byte bAlternateSetting();

    /**
     * Gets the count of endpoints for this interface descriptor.
     *
     * @return the endpoint count
     */
    byte bNumEndpoints();

    /**
     * Gets the class of this interface.
     *
     * @return the interface class
     */
    byte bInterfaceClass();

    /**
     * Gets the subclass of this interface.
     *
     * @return the interface subclass
     */
    byte bInterfaceSubClass();

    /**
     * Gets the protocol of this interface.
     *
     * @return the protocol of this interface
     */
    byte bInterfaceProtocol();

    /**
     * Gets the string index that describes this interface.
     *
     * @return the string index of this interface
     */
    byte iInterface();

    /**
     * Gets the list of the endpoint descriptors for this interface.
     *
     * @return the endpoint descriptors
     */
    List<UsbEndpointDescriptor> endpoints();
}
