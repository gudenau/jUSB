package net.gudenau.jusb;

@FunctionalInterface
public interface DeviceHotplugCallback {
    void invoke(UsbDevice device, Event event);
    
    enum Event {
        ARRIVED,
        LEFT,
    }
}
