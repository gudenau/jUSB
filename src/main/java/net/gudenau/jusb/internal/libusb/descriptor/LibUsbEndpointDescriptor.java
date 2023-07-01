package net.gudenau.jusb.internal.libusb.descriptor;

import net.gudenau.jusb.descriptor.UsbEndpointDescriptor;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;

import static net.gudenau.jusb.internal.ForeignUtils.*;

public record LibUsbEndpointDescriptor(MemorySegment segment) implements UsbEndpointDescriptor {
    static final GroupLayout LAYOUT = structure(
        U8.withName("bLength"),
        U8.withName("bDescriptorType"),
        U8.withName("bEndpointAddress"),
        U8.withName("bmAttributes"),
        U16.withName("wMaxPacketSize"),
        U8.withName("bInterval"),
        U8.withName("bSynchAddress"),
        UNBOUND_ADDRESS.withName("extra"),
        S32.withName("extra_length")
    );

    private static final VarHandle bLength = layoutHandle(LAYOUT, "bLength");
    private static final VarHandle bDescriptorType = layoutHandle(LAYOUT, "bDescriptorType");
    private static final VarHandle bEndpointAddress = layoutHandle(LAYOUT, "bEndpointAddress");
    private static final VarHandle bmAttributes = layoutHandle(LAYOUT, "bmAttributes");
    private static final VarHandle wMaxPacketSize = layoutHandle(LAYOUT, "wMaxPacketSize");
    private static final VarHandle bInterval = layoutHandle(LAYOUT, "bInterval");
    private static final VarHandle bSynchAddress = layoutHandle(LAYOUT, "bSynchAddress");
    private static final VarHandle extra = layoutHandle(LAYOUT, "extra");
    private static final VarHandle extra_length = layoutHandle(LAYOUT, "extra_length");

    @Override
    public byte bLength() {
        return (byte) bLength.get(segment);
    }

    @Override
    public byte bDescriptorType() {
        return (byte) bDescriptorType.get(segment);
    }

    @Override
    public byte bEndpointAddress() {
        return (byte) bEndpointAddress.get(segment);
    }

    @Override
    public byte bmAttributes() {
        return (byte) bmAttributes.get(segment);
    }

    @Override
    public short wMaxPacketSize() {
        return (short) wMaxPacketSize.get(segment);
    }

    @Override
    public byte bInterval() {
        return (byte) bInterval.get(segment);
    }

    @Override
    public byte bSynchAddress() {
        return (byte) bSynchAddress.get(segment);
    }

    public MemorySegment extra() {
        return ((MemorySegment) extra.get(segment)).asSlice(0, extra_length());
    }

    public int extra_length() {
        return (int) extra_length.get(segment);
    }
}
