package net.gudenau.jusb.descriptor;

import net.gudenau.jusb.internal.libusb.descriptor.LibUsbDeviceDescriptor;

/**
 * A USB device descriptor.
 */
public sealed interface UsbDeviceDescriptor extends UsbDescriptor permits LibUsbDeviceDescriptor {
    /**
     * The USB specification number that this device complies with.
     *
     * @return the USB version
     */
    short bcdUSB();

    /**
     * The USB device class of this device, 0xFF is vendor specific.
     *
     * @return device class
     */
    byte bDeviceClass();

    /**
     * The USB device subclass of this device, 0xFF is vendor specific.
     *
     * @return device subclass
     */
    byte bDeviceSubClass();

    /**
     * The USB device protocol of this device.
     *
     * @return device protocol
     */
    byte bDeviceProtocol();

    /**
     * The max packet size of this device, valid sizes are 8, 16, 32 and 64.
     *
     * @return the max packet size
     */
    byte bMaxPacketSize0();

    /**
     * The vendor ID of this device. The underlying implementation uses an unsigned 16 bit value, this implementation
     * uses an int for convenience.
     *
     * @return vendor ID
     */
    int idVendor();

    /**
     * The product ID of this device. The underlying implementation uses an unsigned 16 bit value, this implementation
     * uses an int for convenience.
     *
     * @return product ID
     */
    int idProduct();

    /**
     * The version of this device.
     *
     * @return the device version
     */
    short bcdDevice();

    /**
     * The string descriptor index of the manufacturer string.
     *
     * @return the manufacturer string index
     */
    byte iManufacturer();

    /**
     * The string product index of the manufacturer string.
     *
     * @return the product string index
     */
    byte iProduct();

    /**
     * The string serial index of the manufacturer string.
     *
     * @return the serial string index
     */
    byte iSerialNumber();

    /**
     * The number of different configurations that this device supports.
     *
     * @return supported configuration count
     */
    byte bNumConfigurations();
}
