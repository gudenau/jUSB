package net.gudenau.jusb.internal.libusb;

import java.lang.foreign.MemorySegment;

public record LibUsbDevice(MemorySegment address) {}
