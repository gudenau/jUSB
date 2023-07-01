package net.gudenau.jusb.descriptor;

import net.gudenau.jusb.internal.libusb.descriptor.LibUsbConfigDescriptor;

import java.util.List;

/**
 * A USB configuration descriptor.
 */
public sealed interface UsbConfigDescriptor extends AutoCloseable, UsbDescriptor permits LibUsbConfigDescriptor {
    /**
     * A reserved attribute from {@link #bmAttributes()}, it should always be set.
     */
    byte ATTRIBUTE_SHOULD_BE_SET = (byte)(1 << 7);

    /**
     * The device is self powered.
     */
    byte ATTRIBUTE_SELF_POWERED = 1 << 6;

    /**
     * The device supports remote wakeup.
     */
    byte ATTRIBUTE_REMOTE_WAKEUP = 1 << 5;

    /**
     * The total length of the returned data.
     *
     * @return returned data length
     */
    short wTotalLength();

    /**
     * The number of interfaces.
     *
     * @return interface count
     */
    byte bNumInterfaces();

    /**
     * The value to use to select this configuration.
     *
     * @return the configuration value
     */
    byte bConfigurationValue();

    /**
     * The string index to describe this configuration.
     *
     * @return string index
     */
    byte iConfiguration();

    /**
     * A bitmap of attributes for this configuration. The flags are {@link #ATTRIBUTE_SHOULD_BE_SET},
     * {@link #ATTRIBUTE_SELF_POWERED} and {@link #ATTRIBUTE_REMOTE_WAKEUP}.
     *
     * @return the bitmask of attributes
     */
    byte bmAttributes();

    /**
     * The maximum power consumption of this device in 2mA units.
     *
     * @return the maximum power consumption
     */
    byte maxPower();

    /**
     * A list of {@link UsbInterface}s of this configuration.
     *
     * @return The list of interfaces
     */
    List<UsbInterface> interfaces();

    /**
     * Frees the native memory used by this interface.
     */
    @Override void close();
}
