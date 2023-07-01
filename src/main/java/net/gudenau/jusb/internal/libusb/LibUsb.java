package net.gudenau.jusb.internal.libusb;

import net.gudenau.jusb.internal.ForeignUtils;
import net.gudenau.jusb.internal.libusb.descriptor.LibUsbConfigDescriptor;
import net.gudenau.jusb.internal.libusb.descriptor.LibUsbDeviceDescriptor;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.*;
import static net.gudenau.jusb.internal.ForeignUtils.UNBOUND_ADDRESS;

@SuppressWarnings("PointlessBitwiseExpression")
public final class LibUsb {
    public static final int LIBUSB_SUCCESS = 0;
    public static final int LIBUSB_ERROR_IO = -1;
    public static final int LIBUSB_ERROR_INVALID_PARAM = -2;
    public static final int LIBUSB_ERROR_ACCESS = -3;
    public static final int LIBUSB_ERROR_NO_DEVICE = -4;
    public static final int LIBUSB_ERROR_NOT_FOUND = -5;
    public static final int LIBUSB_ERROR_BUSY = -6;
    public static final int LIBUSB_ERROR_TIMEOUT = -7;
    public static final int LIBUSB_ERROR_OVERFLOW = -8;
    public static final int LIBUSB_ERROR_PIPE = -9;
    public static final int LIBUSB_ERROR_INTERRUPTED = -10;
    public static final int LIBUSB_ERROR_NO_MEM = -11;
    public static final int LIBUSB_ERROR_NOT_SUPPORTED = -12;
    public static final int LIBUSB_ERROR_OTHER = -99;
    
    public static final int LIBUSB_OPTION_LOG_LEVEL = 0;
    public static final int LIBUSB_OPTION_USE_USBDK = 1;
    public static final int LIBUSB_OPTION_NO_DEVICE_DISCOVERY = 2;
    public static final int LIBUSB_OPTION_MAX = 3;
    
    public static final int LIBUSB_LOG_LEVEL_NONE = 0;
    public static final int LIBUSB_LOG_LEVEL_ERROR = 1;
    public static final int LIBUSB_LOG_LEVEL_WARNING = 2;
    public static final int LIBUSB_LOG_LEVEL_INFO = 3;
    public static final int LIBUSB_LOG_LEVEL_DEBUG = 4;
    
    public static final int LIBUSB_LOG_CB_GLOBAL = (1 << 0);
    public static final int LIBUSB_LOG_CB_CONTEXT = (1 << 1);
    
    public static final int LIBUSB_CAP_HAS_CAPABILITY = 0x0000;
    public static final int LIBUSB_CAP_HAS_HOTPLUG = 0x0001;
    public static final int LIBUSB_CAP_HAS_HID_ACCESS = 0x0100;
    public static final int LIBUSB_CAP_SUPPORTS_DETACH_KERNEL_DRIVER = 0x0101;
    
    public static final int LIBUSB_ENDPOINT_OUT = 0x00;
    public static final int LIBUSB_ENDPOINT_IN = 0x80;
    
    public static final int LIBUSB_HOTPLUG_EVENT_DEVICE_ARRIVED = 1 << 0;
    public static final int LIBUSB_HOTPLUG_EVENT_DEVICE_LEFT = 1 << 1;
    
    public static final int LIBUSB_HOTPLUG_ENUMERATE = 1 << 0;
    
    public static final int LIBUSB_HOTPLUG_MATCH_ANY = -1;
    
    public static final int LIBUSB_TRANSFER_COMPLETED = 0;
    public static final int LIBUSB_TRANSFER_ERROR = 1;
    public static final int LIBUSB_TRANSFER_TIMED_OUT = 2;
    public static final int LIBUSB_TRANSFER_CANCELLED = 3;
    public static final int LIBUSB_TRANSFER_STALL = 4;
    public static final int LIBUSB_TRANSFER_NO_DEVICE = 5;
    public static final int LIBUSB_TRANSFER_OVERFLOW = 6;
    
    public static final int LIBUSB_TRANSFER_TYPE_CONTROL = 0;
    public static final int LIBUSB_TRANSFER_TYPE_ISOCHRONOUS = 1;
    public static final int LIBUSB_TRANSFER_TYPE_BULK = 2;
    public static final int LIBUSB_TRANSFER_TYPE_INTERRUPT = 3;
    public static final int LIBUSB_TRANSFER_TYPE_BULK_STREAM = 4;
    
    private static final MethodHandle libusb_init;
    public static int libusb_init(LibUsbContext ctx) {
        try {
            if(ctx == null) {
                return (int) libusb_init.invokeExact(MemorySegment.NULL);
            }
            
            try(var session = Arena.openConfined()) {
                var pointer = session.allocate(ADDRESS);
                var result = (int) libusb_init.invokeExact(pointer);
                if(result == LIBUSB_SUCCESS) {
                    ctx.set(pointer.get(ADDRESS, 0));
                }
                return result;
            }
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_init", e);
        }
    }
    
    private static final MethodHandle libusb_exit;
    public static void libusb_exit(LibUsbContext ctx) {
        try {
            libusb_exit.invokeExact(context(ctx));
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_exit", e);
        }
    }
    
    private static final MethodHandle libusb_set_debug;
    @Deprecated
    public static void libusb_set_debug(LibUsbContext ctx, int level) {
        try {
            libusb_set_debug.invokeExact(context(ctx), level);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_set_debug", e);
        }
    }
    
    private static final MethodHandle libusb_set_option$I;
    public static int libusb_set_option(LibUsbContext ctx, int option, int value) {
        try {
            return (int) libusb_set_option$I.invokeExact(context(ctx), option, value);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_set_option$I", e);
        }
    }
    
    private static final MethodHandle libusb_set_log_cb;
    public static void libusb_set_log_cb(LibUsbContext ctx, MemorySegment cb, int mode) {
        try {
            libusb_set_log_cb.invokeExact(context(ctx), cb, mode);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_set_log_cb", e);
        }
    }
    
    private static final MethodHandle libusb_get_device_list;
    public static long libusb_get_device_list(LibUsbContext ctx, MemorySegment list) {
        try {
            return (long) libusb_get_device_list.invokeExact(context(ctx), list);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_get_device_list", e);
        }
    }
    
    private static final MethodHandle libusb_free_device_list;
    public static void libusb_free_device_list(MemorySegment list, boolean unref_devices) {
        try {
            libusb_free_device_list.invokeExact(list, unref_devices ? 1 : 0);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_free_device_list", e);
        }
    }
    
    private static final MethodHandle libusb_get_device_descriptor;
    public static int libusb_get_device_descriptor(LibUsbDevice device, LibUsbDeviceDescriptor desc) {
        try {
            return (int) libusb_get_device_descriptor.invokeExact(device.address(), desc.segment());
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_get_device_descriptor", e);
        }
    }
    
    private static final MethodHandle libusb_error_name;
    public static String libusb_error_name(int error_code) {
        try {
            var address = (MemorySegment) libusb_error_name.invokeExact(error_code);
            return address.equals(MemorySegment.NULL) ? null : address.getUtf8String(0);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_error_name", e);
        }
    }
    
    private static final MethodHandle libusb_has_capability;
    public static boolean libusb_has_capability(int capability) {
        try {
            return (int) libusb_has_capability.invokeExact(capability) != 0;
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_has_capability", e);
        }
    }
    
    private static final MethodHandle libusb_ref_device;
    public static LibUsbDevice libusb_ref_device(LibUsbDevice device) {
        try {
            var ignored = (MemorySegment) libusb_ref_device.invokeExact(device.address());
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_ref_device", e);
        }
        return device;
    }
    
    private static final MethodHandle libusb_unref_device;
    public static void libusb_unref_device(LibUsbDevice device) {
        try {
            libusb_unref_device.invokeExact(device.address());
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_ref_device", e);
        }
    }
    
    private static final MethodHandle libusb_open;
    public static int libusb_open(LibUsbDevice dev, MemorySegment dev_handle) {
        try {
            return (int) libusb_open.invokeExact(dev.address(), dev_handle);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_open", e);
        }
    }
    
    private static final MethodHandle libusb_close;
    public static void libusb_close(LibUsbDeviceHandle dev_handle) {
        try {
            libusb_close.invokeExact(dev_handle.address());
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_close", e);
        }
    }
    
    private static final MethodHandle libusb_set_auto_detach_kernel_driver;
    public static int libusb_set_auto_detach_kernel_driver(LibUsbDeviceHandle dev_handle, boolean enable) {
        try {
            return (int) libusb_set_auto_detach_kernel_driver.invokeExact(dev_handle.address(), enable ? 1 : 0);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_set_auto_detach_kernel_driver", e);
        }
    }
    
    private static final MethodHandle libusb_set_configuration;
    public static int libusb_set_configuration(LibUsbDeviceHandle handle, int configuration) {
        try {
            return (int) libusb_set_configuration.invokeExact(handle.address(), configuration);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_set_configuration", e);
        }
    }

    private static final MethodHandle libusb_get_configuration;
    public static int libusb_get_configuration(LibUsbDeviceHandle handle, MemorySegment config) {
        try {
            return (int) libusb_get_configuration.invokeExact(handle.address(), config);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_get_configuration", e);
        }
    }
    
    private static final MethodHandle libusb_control_transfer;
    public static int libusb_control_transfer(LibUsbDeviceHandle dev_handle, byte bmRequestType, byte bRequest, short wValue, short wIndex, MemorySegment data, short wLength, int timeout) {
        try {
            return (int) libusb_control_transfer.invokeExact(dev_handle.address(), bmRequestType, bRequest, wValue, wIndex, data, wLength, timeout);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_bulk_transfer", e);
        }
    }
    
    private static final MethodHandle libusb_bulk_transfer;
    public static int libusb_bulk_transfer(LibUsbDeviceHandle dev_handle, byte endpoint, MemorySegment data, int length, MemorySegment transferred, int timeout) {
        try {
            return (int) libusb_bulk_transfer.invokeExact(dev_handle.address(), endpoint, data, length, transferred, timeout);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_bulk_transfer", e);
        }
    }
    
    private static final MethodHandle libusb_interrupt_transfer;
    public static int libusb_interrupt_transfer(LibUsbDeviceHandle dev_handle, byte endpoint, MemorySegment data, int length, MemorySegment transferred, int timeout) {
        try {
            return (int) libusb_interrupt_transfer.invokeExact(dev_handle.address(), endpoint, data, length, transferred, timeout);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_interrupt_transfer", e);
        }
    }
    
    private static final MethodHandle libusb_claim_interface;
    public static int libusb_claim_interface(LibUsbDeviceHandle dev_handle, int interface_number) {
        try {
            return (int) libusb_claim_interface.invokeExact(dev_handle.address(), interface_number);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_claim_interface", e);
        }
    }
    
    private static final MethodHandle libusb_hotplug_register_callback;
    public static int libusb_hotplug_register_callback(LibUsbContext ctx, int events, int flags, int vendor_id, int product_id, int dev_class, MemorySegment cb_fn, MemorySegment user_data, MemorySegment callback_handle) {
        try {
            return (int) libusb_hotplug_register_callback.invokeExact(context(ctx), events, flags, vendor_id, product_id, dev_class, cb_fn, user_data, callback_handle);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_hotplug_register_callback", e);
        }
    }
    
    private static final MethodHandle libusb_handle_events_timeout_completed;
    public static int libusb_handle_events_timeout_completed(LibUsbContext ctx, MemorySegment tv, MemorySegment completed) {
        try {
            return (int) libusb_handle_events_timeout_completed.invokeExact(context(ctx), tv, completed);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_handle_events_timeout_completed", e);
        }
    }
    
    private static final MethodHandle libusb_hotplug_deregister_callback;
    public static void libusb_hotplug_deregister_callback(LibUsbContext ctx, int callback_handle) {
        try {
            libusb_hotplug_deregister_callback.invokeExact(context(ctx), callback_handle);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_hotplug_deregister_callback", e);
        }
    }
    
    private static final MethodHandle libusb_alloc_transfer;
    public static MemorySegment libusb_alloc_transfer(int iso_packets) {
        try {
            return (MemorySegment) libusb_alloc_transfer.invokeExact(iso_packets);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_alloc_transfer", e);
        }
    }
    
    private static final MethodHandle libusb_free_transfer;
    public static void libusb_free_transfer(LibUsbTransfer transfer) {
        try {
            libusb_free_transfer.invokeExact(transfer.segment());
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_free_transfer", e);
        }
    }
    
    private static final MethodHandle libusb_submit_transfer;
    public static int libusb_submit_transfer(LibUsbTransfer transfer) {
        try {
            return (int) libusb_submit_transfer.invokeExact(transfer.segment());
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_submit_transfer", e);
        }
    }

    private static final MethodHandle libusb_get_active_config_descriptor;
    public static int libusb_get_active_config_descriptor(LibUsbDevice device, MemorySegment config) {
        try {
            return (int) libusb_get_active_config_descriptor.invokeExact(device.address(), config);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_get_active_config_descriptor", e);
        }
    }

    private static final MethodHandle libusb_get_config_descriptor;
    public static int libusb_get_config_descriptor(LibUsbDevice dev, byte config_index, MemorySegment config) {
        try {
            return (int) libusb_get_config_descriptor.invokeExact(dev.address(), config_index, config);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_get_config_descriptor", e);
        }
    }

    private static final MethodHandle libusb_get_config_descriptor_by_value;
    public static int libusb_get_config_descriptor_by_value(LibUsbDevice dev, byte bConfigurationValue, MemorySegment config) {
        try {
            return (int) libusb_get_config_descriptor_by_value.invokeExact(dev.address(), bConfigurationValue, config);
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_get_config_descriptor_by_value", e);
        }
    }

    private static final MethodHandle libusb_free_config_descriptor;
    public static void libusb_free_config_descriptor(LibUsbConfigDescriptor config) {
        try {
            libusb_free_config_descriptor.invokeExact(config.segment());
        } catch(Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_free_config_descriptor", e);
        }
    }

    public static final int LIBUSB_REQUEST_GET_DESCRIPTOR = 0x06;
    public static final int LIBUSB_DT_STRING = 0x03;
    public static int libusb_get_string_descriptor(LibUsbDeviceHandle dev_handle, byte desc_index, short langid, MemorySegment data) {
        return libusb_control_transfer(dev_handle, (byte) LIBUSB_ENDPOINT_IN, (byte) LIBUSB_REQUEST_GET_DESCRIPTOR, (short)((LIBUSB_DT_STRING << 8) | desc_index), langid, data, (short) data.byteSize(), 1000);
    }

    private static final MethodHandle libusb_get_port_numbers;
    public static int libusb_get_port_numbers(LibUsbDevice dev, MemorySegment port_numbers) {
        try {
            return (int) libusb_get_port_numbers.invokeExact(dev.address(), port_numbers, (int) port_numbers.byteSize());
        } catch (Throwable e) {
            throw new RuntimeException("Failed to invoke libusb_get_port_numbers", e);
        }
    }

    private static MemorySegment context(LibUsbContext ctx) {
        return ctx == null ? MemorySegment.NULL : ctx.address();
    }
    
    static {
        var binder = ForeignUtils.binder("libusb-1.0");
        libusb_init = binder.bind("libusb_init", JAVA_INT, ADDRESS);
        libusb_exit = binder.bind("libusb_exit", null, ADDRESS);
        libusb_set_debug = binder.bind("libusb_set_debug", null, ADDRESS, JAVA_INT);
        var libusb_set_option = FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT, JAVA_INT);
        libusb_set_option$I = binder.bind("libusb_set_option", libusb_set_option, Linker.Option.firstVariadicArg(3));
        libusb_set_log_cb = binder.bind("libusb_set_log_cb", null, ADDRESS, ADDRESS, JAVA_INT);
        libusb_get_device_list = binder.bind("libusb_get_device_list", JAVA_LONG, ADDRESS, ADDRESS);
        libusb_free_device_list = binder.bind("libusb_free_device_list", null, ADDRESS, JAVA_INT);
        libusb_get_device_descriptor = binder.bind("libusb_get_device_descriptor", JAVA_INT, ADDRESS, ADDRESS);
        libusb_error_name = binder.bind("libusb_error_name", UNBOUND_ADDRESS, JAVA_INT);
        libusb_has_capability = binder.bind("libusb_has_capability", JAVA_INT, JAVA_INT);
        libusb_ref_device = binder.bind("libusb_ref_device", ADDRESS, ADDRESS);
        libusb_unref_device = binder.bind("libusb_unref_device", null, ADDRESS);
        libusb_open = binder.bind("libusb_open", JAVA_INT, ADDRESS, ADDRESS);
        libusb_close = binder.bind("libusb_close", null, ADDRESS);
        libusb_set_auto_detach_kernel_driver = binder.bind("libusb_set_auto_detach_kernel_driver", JAVA_INT, ADDRESS, JAVA_INT);
        libusb_set_configuration = binder.bind("libusb_set_configuration", JAVA_INT, ADDRESS, JAVA_INT);
        libusb_get_configuration = binder.bind("libusb_get_configuration", JAVA_INT, ADDRESS, ADDRESS);
        libusb_control_transfer = binder.bind("libusb_control_transfer",JAVA_INT, ADDRESS, JAVA_BYTE, JAVA_BYTE, JAVA_SHORT, JAVA_SHORT, ADDRESS, JAVA_SHORT, JAVA_INT);
        libusb_bulk_transfer = binder.bind("libusb_bulk_transfer", JAVA_INT, ADDRESS, JAVA_BYTE, ADDRESS, JAVA_INT, ADDRESS, JAVA_INT);
        libusb_interrupt_transfer = binder.bind("libusb_interrupt_transfer", JAVA_INT, ADDRESS, JAVA_BYTE, ADDRESS, JAVA_INT, ADDRESS, JAVA_INT);
        libusb_claim_interface = binder.bind("libusb_claim_interface", JAVA_INT, ADDRESS, JAVA_INT);
        libusb_hotplug_register_callback = binder.bind("libusb_hotplug_register_callback", JAVA_INT, ADDRESS, JAVA_INT, JAVA_INT, JAVA_INT, JAVA_INT, JAVA_INT, ADDRESS, ADDRESS, ADDRESS);
        libusb_handle_events_timeout_completed = binder.bind("libusb_handle_events_timeout_completed", JAVA_INT, ADDRESS, ADDRESS, ADDRESS);
        libusb_hotplug_deregister_callback = binder.bind("libusb_hotplug_deregister_callback", null, ADDRESS, JAVA_INT);
        libusb_alloc_transfer = binder.bind("libusb_alloc_transfer", ADDRESS, JAVA_INT);
        libusb_free_transfer = binder.bind("libusb_free_transfer", null, ADDRESS);
        libusb_submit_transfer = binder.bind("libusb_submit_transfer", JAVA_INT, ADDRESS);
        libusb_get_active_config_descriptor = binder.bind("libusb_get_active_config_descriptor", JAVA_INT, ADDRESS, ADDRESS);
        libusb_get_config_descriptor = binder.bind("libusb_get_config_descriptor", JAVA_INT, ADDRESS, JAVA_BYTE, ADDRESS);
        libusb_get_config_descriptor_by_value = binder.bind("libusb_get_config_descriptor_by_value", JAVA_INT, ADDRESS, JAVA_BYTE, ADDRESS);
        libusb_free_config_descriptor = binder.bind("libusb_free_config_descriptor", null, ADDRESS);
        libusb_get_port_numbers = binder.bind("libusb_get_port_numbers", JAVA_INT, ADDRESS, ADDRESS, JAVA_INT);
    }
    
    private LibUsb() {
        throw new AssertionError();
    }
}
