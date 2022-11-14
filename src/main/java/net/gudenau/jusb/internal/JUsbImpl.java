package net.gudenau.jusb.internal;

import net.gudenau.jusb.DeviceHotplugCallback;
import net.gudenau.jusb.JUsb;
import net.gudenau.jusb.UsbDevice;
import net.gudenau.jusb.UsbException;
import net.gudenau.jusb.internal.libusb.*;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class JUsbImpl implements JUsb {
    public static JUsbImpl init(Set<Option> options, Set<DeviceHotplugCallback> hotplugCallbacks) throws UsbException {
        var context = new LibUsbContext();
        var result = LibUsb.libusb_init(context);
        if(result != LibUsb.LIBUSB_SUCCESS) {
            throw new UsbException("Failed to initialize libusb: " + LibUsb.libusb_error_name(result));
        }
        
        try {
            return new JUsbImpl(context, options, hotplugCallbacks);
        } catch(Throwable t) {
            LibUsb.libusb_exit(context);
            throw t;
        }
    }
    
    public static boolean supportedOption(Option option) {
        return switch(option) {
            case ENABLE_ASYNC -> true;
            case ENABLE_HOTPLUG -> LibUsb.libusb_has_capability(LibUsb.LIBUSB_CAP_HAS_HOTPLUG);
            case ENABLE_DETATCH -> LibUsb.libusb_has_capability(LibUsb.LIBUSB_CAP_SUPPORTS_DETACH_KERNEL_DRIVER);
        };
    }
    
    private final LibUsbContext context;
    private final boolean enableAsync;
    private final boolean enableHotplug;
    private final boolean enableDetach;
    private final Set<DeviceHotplugCallback> hotplugCallbacks;
    private final OptionalSession session = new OptionalSession();
    private final MemorySegment hotplugCallback;
    private final int hotplugHandle;
    private final Thread eventThread;
    private final MemorySegment eventThreadRunning;
    
    private JUsbImpl(LibUsbContext context, Set<Option> options, Set<DeviceHotplugCallback> hotplugCallbacks) {
        this.context = context;
    
        enableAsync = options.contains(Option.ENABLE_ASYNC);
        enableHotplug = options.contains(Option.ENABLE_HOTPLUG);
        enableDetach = options.contains(Option.ENABLE_DETATCH);
        
        if(enableHotplug && !hotplugCallbacks.isEmpty()) {
            this.hotplugCallbacks = hotplugCallbacks;
            this.hotplugCallback = LibUsbHotplugCallback.allocate((ctx, device, event, user_data) -> {
                for(var callback : this.hotplugCallbacks) {
                    callback.invoke(new UsbDeviceImpl(this, device), switch(event) {
                        case LibUsb.LIBUSB_HOTPLUG_EVENT_DEVICE_ARRIVED -> DeviceHotplugCallback.Event.ARRIVED;
                        case LibUsb.LIBUSB_HOTPLUG_EVENT_DEVICE_LEFT -> DeviceHotplugCallback.Event.LEFT;
                        default -> throw new IllegalStateException("libusb passed an unexpected event value to the hotplug callback handler: " + event);
                    });
                }
                return false;
            }, session.get());
            
            try(var session = MemorySession.openConfined()) {
                var handlePointer = session.allocate(ValueLayout.JAVA_INT);
                LibUsb.libusb_hotplug_register_callback(
                    context,
                    LibUsb.LIBUSB_HOTPLUG_EVENT_DEVICE_ARRIVED | LibUsb.LIBUSB_HOTPLUG_EVENT_DEVICE_LEFT,
                    LibUsb.LIBUSB_HOTPLUG_ENUMERATE,
                    LibUsb.LIBUSB_HOTPLUG_MATCH_ANY,
                    LibUsb.LIBUSB_HOTPLUG_MATCH_ANY,
                    LibUsb.LIBUSB_HOTPLUG_MATCH_ANY,
                    hotplugCallback,
                    MemoryAddress.NULL,
                    handlePointer
                );
                hotplugHandle = handlePointer.get(ValueLayout.JAVA_INT, 0);
            }
        } else {
            this.hotplugCallbacks = Set.of();
            this.hotplugCallback = null;
            this.hotplugHandle = -1;
        }
        
        if(enableHotplug || enableAsync) {
            eventThreadRunning = session.get().allocate(ValueLayout.JAVA_INT);
            eventThreadRunning.set(ValueLayout.JAVA_INT, 0, 0);
            
            eventThread = new Thread(this::eventThread, "JUsb Event Thread");
            eventThread.setDaemon(true);
            eventThread.start();
        } else {
            eventThreadRunning = null;
            eventThread = null;
        }
    }
    
    private void eventThread() {
        try(var session = MemorySession.openConfined()) {
            var timeout = new Timeval(session);
            while(eventThreadRunning.get(ValueLayout.JAVA_INT, 0) == 0) {
                timeout.tv_sec(-1).tv_usec(-1);
                LibUsb.libusb_handle_events_timeout_completed(context, timeout.segment(), eventThreadRunning);
            }
        }
    }
    
    @Override
    public List<UsbDevice> devices() throws UsbException {
        try(var session = MemorySession.openConfined()) {
            var pointer = session.allocate(ValueLayout.ADDRESS).address();
            var result = LibUsb.libusb_get_device_list(context, pointer);
            if(result <= 0) {
                throw new UsbException("Failed to get device list: " + LibUsb.libusb_error_name((int) result));
            }
            pointer = pointer.get(ValueLayout.ADDRESS, 0);
    
            List<UsbDevice> devices;
            try {
                int deviceCount = (int) Math.min(Integer.MAX_VALUE, result);
                devices = new ArrayList<>(deviceCount);
                for(int i = 0; i < deviceCount; i++) {
                    devices.add(new UsbDeviceImpl(this, new LibUsbDevice(pointer.getAtIndex(ValueLayout.ADDRESS, i))));
                }
            } catch(Throwable t) {
                LibUsb.libusb_free_device_list(pointer, true);
                throw t;
            }
            LibUsb.libusb_free_device_list(pointer, false);
            return devices;
        }
    }
    
    @Override
    public void close() throws UsbException {
        if(eventThread != null) {
            eventThreadRunning.set(ValueLayout.JAVA_INT, 0, 1);
        }
        if(enableHotplug) {
            LibUsb.libusb_hotplug_deregister_callback(context, hotplugHandle);
        }else{
            //FIXME Wake up event handling thread
        }
        if(eventThread != null) {
            try {
                eventThread.join();
            } catch(InterruptedException ignored) {}
        }
        LibUsb.libusb_exit(context);
        session.close();
    }
    
    public boolean enableDetach() {
        return enableDetach;
    }
    
    public boolean enableAsync() {
        return enableAsync;
    }
}
