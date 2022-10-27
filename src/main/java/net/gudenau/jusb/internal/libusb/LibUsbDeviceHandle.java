package net.gudenau.jusb.internal.libusb;

import java.lang.foreign.MemoryAddress;

public record LibUsbDeviceHandle(MemoryAddress address) {}
