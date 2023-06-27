package net.gudenau.jusb;

import net.gudenau.jusb.internal.JUsbImpl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The primary interface for JUsb.
 */
public sealed interface JUsb extends AutoCloseable permits JUsbImpl {
    /**
     * Attempts to initialize the native USB subsystem for the current platform with automatic kernel detaching enabled
     * (if supported).
     *
     * @return A new {@link JUsb} instance
     * @throws UsbException if there was a failure initializing the USB subsystem
     */
    static JUsb init() throws UsbException {
        return builder()
            .option(Option.ENABLE_DETACH)
            .build();
    }
    
    /**
     * Constructs a new {@link JUsb.Builder} instance for providing more control over initialization.
     *
     * @return A new {@link JUsb.Builder} instance
     */
    static Builder builder() {
        return new Builder();
    }
    
    /**
     * Checks if an {@link Option} is supported on the current platform.
     *
     * @param option The option to check for
     * @return True if the option is supported, false otherwise
     */
    static boolean supportedOption(Option option) {
        Objects.requireNonNull(option, "option can't be null");
        return JUsbImpl.supportedOption(option);
    }
    
    /**
     * Retrieves a list of USB devices attached to the host.
     *
     * @return An immutable list of attached USB devices
     * @throws UsbException if there was an error listing USB devices
     */
    List<UsbDevice> devices() throws UsbException;

    /**
     * A fake vendor ID that matches all vendor IDs in {@link #devices(int, int)}
     */
    int VID_ANY = -1;

    /**
     * A fake product ID that matches all product IDs in {@link #devices(int, int)}
     */
    int PID_ANY = -1;

    /**
     * Retrieves a list of USB devices with the matching vendor and product IDs. Use {@link #VID_ANY} for any vendor ID
     * or {@link #PID_ANY} for any product ID.
     *
     * @return An immutable list of attached USB devices
     * @throws UsbException if there was an error listing USB devices
     */
    default List<UsbDevice> devices(int vid, int pid) throws IOException {
        if(vid == VID_ANY && pid == PID_ANY) {
            return devices();
        }

        return devices().stream()
            .filter((device) -> {
                var descriptor = device.descriptor();
                if(
                    (vid == VID_ANY || descriptor.idVendor() == vid) &&
                    (pid == PID_ANY || descriptor.idProduct() == pid)
                ) {
                    return true;
                }

                device.close();
                return false;
            })
            .toList();
    }

    @Override void close() throws UsbException;
    
    /**
     * A builder used for constructing a {@link JUsb} instance.
     */
    final class Builder {
        /**
         * The set of current options.
         */
        private final Set<Option> options = new HashSet<>();
        /**
         * The set of USB hotplug callbacks.
         */
        private final Set<DeviceHotplugCallback> hotplugCallbacks = new HashSet<>();
        
        private Builder() {}
    
        /**
         * Requests an {@link Option} to be enabled. Silently fails if the platform does not support the option,
         * manually check with {@link JUsb#supportedOption(Option)} if you require an {@link Option}.
         *
         * @param option The option to enable
         * @return The current builder instance
         */
        public Builder option(Option option) {
            Objects.requireNonNull(option, "option can't be null");
            if(JUsbImpl.supportedOption(option)) {
                options.add(option);
            }
            return this;
        }
    
        /**
         * Adds a new {@link DeviceHotplugCallback} to this builder.
         *
         * @param callback The callback to register
         * @return The current builder instance
         */
        public Builder hotplugCallback(DeviceHotplugCallback callback) {
            Objects.requireNonNull(callback, "callback can't be null");
            hotplugCallbacks.add(callback);
            return this;
        }
    
        /**
         * Constructs a new {@link JUsb} instance.
         *
         * @return A new {@link JUsb} instance
         * @throws UsbException if there was an exception initializing the native USB subsystem
         */
        public JUsb build() throws UsbException {
            return JUsbImpl.init(options, hotplugCallbacks);
        }
    }
    
    /**
     * All supported options.
     */
    enum Option {
        /**
         * Enable asynchronous USB transfer support.
         * <p>
         * This causes a worker thread to be created.
         */
        ENABLE_ASYNC,
        /**
         * Enable USB hotplug support.
         * <p>
         * This causes a worker thread to be created.
         */
        ENABLE_HOTPLUG,
        /**
         * Enable automatic kernel driver detach support.
         */
        ENABLE_DETACH,
    }
}
