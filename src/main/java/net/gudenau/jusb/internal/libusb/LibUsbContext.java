package net.gudenau.jusb.internal.libusb;

import java.lang.foreign.*;

public final class LibUsbContext {
    private MemorySegment address;
    
    LibUsbContext set(MemorySegment address) {
        this.address = address;
        return this;
    }
    
    public MemorySegment address() {
        return address;
    }
}
