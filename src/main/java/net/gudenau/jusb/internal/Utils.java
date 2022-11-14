package net.gudenau.jusb.internal;

import net.gudenau.jusb.UsbDirection;
import net.gudenau.jusb.internal.libusb.LibUsb;

final class Utils {
    public static byte endpoint(int endpoint, UsbDirection direction) {
        return (byte)(endpoint | (direction == UsbDirection.IN ? LibUsb.LIBUSB_ENDPOINT_IN : LibUsb.LIBUSB_ENDPOINT_OUT));
    }
    
    public static int clampToUnsignedInt(long value) {
        return (int) Math.max(0, Math.min(value, 0xFFFFFFFFL));
    }
    
    private Utils() {
        throw new AssertionError();
    }
}
