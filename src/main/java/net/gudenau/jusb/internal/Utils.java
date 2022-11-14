package net.gudenau.jusb.internal;

import net.gudenau.jusb.UsbDirection;
import net.gudenau.jusb.internal.libusb.LibUsb;

import java.util.Objects;

final class Utils {
    public static byte endpoint(int endpoint, UsbDirection direction) {
        Objects.requireNonNull(direction, "direction can't be null");
        return (byte)((endpoint & 0x7F) | (direction == UsbDirection.IN ? LibUsb.LIBUSB_ENDPOINT_IN : LibUsb.LIBUSB_ENDPOINT_OUT));
    }
    
    public static int clampToUnsignedInt(long value) {
        return (int) Math.max(0, Math.min(value, 0xFFFFFFFFL));
    }
    
    private Utils() {
        throw new AssertionError();
    }
}
