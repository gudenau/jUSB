package net.gudenau.jusb.descriptor;

/**
 * The base USB descriptor type.
 */
public sealed interface UsbDescriptor permits UsbConfigDescriptor, UsbDeviceDescriptor, UsbEndpointDescriptor, UsbInterfaceDescriptor {
    /**
     * The descriptor type for {@link UsbDeviceDescriptor}s.
     */
    byte DESCRIPTOR_DEVICE = 0x01;

    /**
     * The descriptor type for string descriptors.
     */
    byte DESCRIPTOR_STRING = 0x03;

    /**
     * The descriptor type for {@link UsbConfigDescriptor}s.
     */
    byte DESCRIPTOR_CONFIGURATION = 0x02;

    /**
     * The descriptor type for {@link UsbInterfaceDescriptor}s.
     */
    byte DESCRIPTOR_INTERFACE = 0x04;

    /**
     * The descriptor type for {@link UsbEndpointDescriptor}s.
     */
    byte DESCRIPTOR_ENDPOINT = 0x05;

    /**
     * The size of the descriptor in bytes.
     *
     * @return the descriptor size
     */
    byte bLength();

    /**
     * The type of this descriptor.
     *
     * @return the descriptor type
     */
    byte bDescriptorType();
}
