package net.gudenau.jusb;

import net.gudenau.jusb.internal.JUsbImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public sealed interface JUsb extends AutoCloseable permits JUsbImpl {
    static JUsb init() throws UsbException {
        return builder()
            .option(Option.ENABLE_DETATCH)
            .build();
    }
    
    static Builder builder() {
        return new Builder();
    }
    
    static boolean supportedOption(Option option) {
        Objects.requireNonNull(option, "option can't be null");
        return JUsbImpl.supportedOption(option);
    }
    
    List<UsbDevice> devices() throws UsbException;
    
    @Override void close() throws UsbException;
    
    final class Builder {
        private final Set<Option> options = new HashSet<>();
        private final Set<DeviceHotplugCallback> hotplugCallbacks = new HashSet<>();
        
        private Builder() {}
        
        public Builder option(Option option) {
            Objects.requireNonNull(option, "option can't be null");
            if(JUsbImpl.supportedOption(option)) {
                options.add(option);
            }
            return this;
        }
        
        public Builder hotplugCallback(DeviceHotplugCallback callback) {
            Objects.requireNonNull(callback, "callback can't be null");
            hotplugCallbacks.add(callback);
            return this;
        }
        
        public JUsb build() throws UsbException {
            return JUsbImpl.init(options, hotplugCallbacks);
        }
    }
    
    enum Option {
        ENABLE_ASYNC,
        ENABLE_HOTPLUG,
        ENABLE_DETATCH,
    }
}
