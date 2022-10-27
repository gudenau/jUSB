package net.gudenau.jusb.internal.libusb;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.invoke.VarHandle;

import static net.gudenau.jusb.internal.ForeignUtils.*;

public record LibUsbDeviceDescriptor(MemorySegment segment) implements net.gudenau.jusb.descriptor.UsbDeviceDescriptor {
    static final GroupLayout LAYOUT = structure(
        S8.withName("bLength"),
        S8.withName("bDescriptorType"),
        S16.withName("bcdUSB"),
        S8.withName("bDeviceClass"),
        S8.withName("bDeviceSubClass"),
        S8.withName("bDeviceProtocol"),
        S8.withName("bMaxPacketSize0"),
        S16.withName("idVendor"),
        S16.withName("idProduct"),
        S16.withName("bcdDevice"),
        S8.withName("iManufacturer"),
        S8.withName("iProduct"),
        S8.withName("iSerialNumber"),
        S8.withName("bNumConfigurations")
    );
    
    private static final VarHandle bLength = layoutHandle(LAYOUT, "bLength");
    private static final VarHandle bDescriptorType = layoutHandle(LAYOUT, "bDescriptorType");
    private static final VarHandle bcdUSB = layoutHandle(LAYOUT, "bcdUSB");
    private static final VarHandle bDeviceClass = layoutHandle(LAYOUT, "bDeviceClass");
    private static final VarHandle bDeviceSubClass = layoutHandle(LAYOUT, "bDeviceSubClass");
    private static final VarHandle bDeviceProtocol = layoutHandle(LAYOUT, "bDeviceProtocol");
    private static final VarHandle bMaxPacketSize0 = layoutHandle(LAYOUT, "bMaxPacketSize0");
    private static final VarHandle idVendor = layoutHandle(LAYOUT, "idVendor");
    private static final VarHandle idProduct = layoutHandle(LAYOUT, "idProduct");
    private static final VarHandle bcdDevice = layoutHandle(LAYOUT, "bcdDevice");
    private static final VarHandle iManufacturer = layoutHandle(LAYOUT, "iManufacturer");
    private static final VarHandle iProduct = layoutHandle(LAYOUT, "iProduct");
    private static final VarHandle iSerialNumber = layoutHandle(LAYOUT, "iSerialNumber");
    private static final VarHandle bNumConfigurations = layoutHandle(LAYOUT, "bNumConfigurations");
    
    public LibUsbDeviceDescriptor(SegmentAllocator allocator) {
        this(allocator.allocate(LAYOUT));
    }
    
    public byte bLength() {
        return (byte) bLength.get(segment);
    }
    
    public byte bDescriptorType() {
        return (byte) bDescriptorType.get(segment);
    }
    
    public short bcdUSB() {
        return (short) bcdUSB.get(segment);
    }
    
    public byte bDeviceClass() {
        return (byte) bDeviceClass.get(segment);
    }
    
    public byte bDeviceSubClass() {
        return (byte) bDeviceSubClass.get(segment);
    }
    
    public byte bDeviceProtocol() {
        return (byte) bDeviceProtocol.get(segment);
    }
    
    public byte bMaxPacketSize0() {
        return (byte) bMaxPacketSize0.get(segment);
    }
    
    public int idVendor() {
        return Short.toUnsignedInt((short) idVendor.get(segment));
    }
    
    public int idProduct() {
        return Short.toUnsignedInt((short) idProduct.get(segment));
    }
    
    public short bcdDevice() {
        return (short) bcdDevice.get(segment);
    }
    
    public byte iManufacturer() {
        return (byte) iManufacturer.get(segment);
    }
    
    public byte iProduct() {
        return (byte) iProduct.get(segment);
    }
    
    public byte iSerialNumber() {
        return (byte) iSerialNumber.get(segment);
    }
    
    public byte bNumConfigurations() {
        return (byte) bNumConfigurations.get(segment);
    }
    
    public LibUsbDeviceDescriptor bLength(byte value) {
        bLength.set(segment, value);
        return this;
    }
    
    public LibUsbDeviceDescriptor bDescriptorType(byte value) {
        bDescriptorType.set(segment, value);
        return this;
    }
    
    public LibUsbDeviceDescriptor bcdUSB(short value) {
        bcdUSB.set(segment, value);
        return this;
    }
    
    public LibUsbDeviceDescriptor bDeviceClass(byte value) {
        bDeviceClass.set(segment, value);
        return this;
    }
    
    public LibUsbDeviceDescriptor bDeviceSubClass(byte value) {
        bDeviceSubClass.set(segment, value);
        return this;
    }
    
    public LibUsbDeviceDescriptor bDeviceProtocol(byte value) {
        bDeviceProtocol.set(segment, value);
        return this;
    }
    
    public LibUsbDeviceDescriptor bMaxPacketSize0(byte value) {
        bMaxPacketSize0.set(segment, value);
        return this;
    }
    
    public LibUsbDeviceDescriptor idVendor(short value) {
        idVendor.set(segment, value);
        return this;
    }
    
    public LibUsbDeviceDescriptor idProduct(short value) {
        idProduct.set(segment, value);
        return this;
    }
    
    public LibUsbDeviceDescriptor bcdDevice(short value) {
        bcdDevice.set(segment, value);
        return this;
    }
    
    public LibUsbDeviceDescriptor iManufacturer(byte value) {
        iManufacturer.set(segment, value);
        return this;
    }
    
    public LibUsbDeviceDescriptor iProduct(byte value) {
        iProduct.set(segment, value);
        return this;
    }
    
    public LibUsbDeviceDescriptor iSerialNumber(byte value) {
        iSerialNumber.set(segment, value);
        return this;
    }
    
    public LibUsbDeviceDescriptor bNumConfigurations(byte value) {
        bNumConfigurations.set(segment, value);
        return this;
    }
    
    @Override
    public String toString() {
        return "LibUsbDeviceDescriptor[vid=" + Integer.toHexString(idVendor() & 0xFFFF) + ",pid=" + Integer.toHexString(idProduct() & 0xFFFF) + "]";
    }
}
