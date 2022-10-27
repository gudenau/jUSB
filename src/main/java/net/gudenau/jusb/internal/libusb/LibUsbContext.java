package net.gudenau.jusb.internal.libusb;

import java.lang.foreign.*;

public final class LibUsbContext {
    private MemoryAddress address;
    
    LibUsbContext set(MemoryAddress address) {
        this.address = address;
        return this;
    }
    
    public MemoryAddress address() {
        return address;
    }
}
