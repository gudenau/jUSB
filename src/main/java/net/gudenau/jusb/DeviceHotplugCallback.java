package net.gudenau.jusb;

/**
 * An event that is fired when a USB device is attached or removed from the host, must be registered via
 * {@link JUsb.Builder#hotplugCallback(DeviceHotplugCallback)}.
 * <p>
 * This will be fired for all connected devices on initialization.
 * <p>
 * May not be supported on all platforms, verify with {@link JUsb#supportedOption(JUsb.Option)}.
 */
@FunctionalInterface
public interface DeviceHotplugCallback {
    /**
     * Invoked when a device is attached or removed from the host.
     *
     * @param device The device that triggered this event
     * @param event The event that occurred
     */
    void invoke(UsbDevice device, Event event);
    
    /**
     * The type of hot-plug event that was fired.
     */
    enum Event {
        /**
         * A USB device was attached to the host.
         */
        ARRIVED,
        /**
         * A USB device was removed from the host.
         */
        LEFT,
    }
}
