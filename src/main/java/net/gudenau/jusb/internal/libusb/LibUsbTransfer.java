package net.gudenau.jusb.internal.libusb;

import java.lang.foreign.*;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static net.gudenau.jusb.internal.ForeignUtils.*;

public record LibUsbTransfer(MemorySegment segment) {
    static final MemoryLayout LAYOUT = structure(
        ADDRESS.withName("dev_handle"),
        U8.withName("flags"),
        U8.withName("endpoint"),
        U8.withName("type"),
        U32.withName("timeout"),
        U32.withName("status"),
        S32.withName("length"),
        S32.withName("actual_length"),
        ADDRESS.withName("callback"),
        ADDRESS.withName("user_data"),
        ADDRESS.withName("buffer"),
        S32.withName("num_iso_packets")
        // libusb_iso_packet_descriptor iso_packet_desc[0]
    );
    
    private static final VarHandle dev_handle = layoutHandle(LAYOUT, "dev_handle");
    private static final VarHandle flags = layoutHandle(LAYOUT, "flags");
    private static final VarHandle endpoint = layoutHandle(LAYOUT, "endpoint");
    private static final VarHandle type = layoutHandle(LAYOUT, "type");
    private static final VarHandle timeout = layoutHandle(LAYOUT, "timeout");
    private static final VarHandle status = layoutHandle(LAYOUT, "status");
    private static final VarHandle length = layoutHandle(LAYOUT, "length");
    private static final VarHandle actual_length = layoutHandle(LAYOUT, "actual_length");
    private static final VarHandle callback = layoutHandle(LAYOUT, "callback");
    private static final VarHandle user_data = layoutHandle(LAYOUT, "user_data");
    private static final VarHandle buffer = layoutHandle(LAYOUT, "buffer");
    private static final VarHandle num_iso_packets = layoutHandle(LAYOUT, "num_iso_packets");
    
    public LibUsbTransfer(Addressable address) {
        this(MemorySegment.ofAddress(address.address(), LAYOUT.byteSize(), MemorySession.global()));
    }
    
    public LibUsbDeviceHandle dev_handle() {
        return new LibUsbDeviceHandle((MemoryAddress) dev_handle.get(segment));
    }
    
    public byte flags() {
        return (byte) flags.get(segment);
    }
    
    public byte endpoint() {
        return (byte) endpoint.get(segment);
    }
    
    public byte type() {
        return (byte) type.get(segment);
    }
    
    public int timeout() {
        return (int) timeout.get(segment);
    }
    
    public int status() {
        return (int) status.get(segment);
    }
    
    public int length() {
        return (int) length.get(segment);
    }
    
    public int actual_length() {
        return (int) actual_length.get(segment);
    }
    
    public Addressable callback() {
        return (MemoryAddress) callback.get(segment);
    }
    
    public Addressable user_data() {
        return (MemoryAddress) user_data.get(segment);
    }
    
    public Addressable buffer() {
        return (MemoryAddress) buffer.get(segment);
    }
    
    public int num_iso_packets() {
        return (int) num_iso_packets.get(segment);
    }
    
    public LibUsbTransfer dev_handle(LibUsbDeviceHandle value) {
        dev_handle.set(segment, (Addressable) value.address());
        return this;
    }
    
    public LibUsbTransfer flags(byte value) {
        flags.set(segment, value);
        return this;
    }
    
    public LibUsbTransfer endpoint(byte value) {
        endpoint.set(segment, value);
        return this;
    }
    
    public LibUsbTransfer type(byte value) {
        type.set(segment, value);
        return this;
    }
    
    public LibUsbTransfer timeout(int value) {
        timeout.set(segment, value);
        return this;
    }
    
    public LibUsbTransfer status(int value) {
        status.set(segment, value);
        return this;
    }
    
    public LibUsbTransfer length(int value) {
        length.set(segment, value);
        return this;
    }
    
    public LibUsbTransfer actual_length(int value) {
        actual_length.set(segment, value);
        return this;
    }
    
    public LibUsbTransfer callback(Addressable value) {
        callback.set(segment, value);
        return this;
    }
    
    public LibUsbTransfer user_data(Addressable value) {
        user_data.set(segment, value);
        return this;
    }
    
    public LibUsbTransfer buffer(Addressable value) {
        buffer.set(segment, value);
        return this;
    }
    
    public LibUsbTransfer num_iso_packets(int value) {
        num_iso_packets.set(segment, value);
        return this;
    }
}
