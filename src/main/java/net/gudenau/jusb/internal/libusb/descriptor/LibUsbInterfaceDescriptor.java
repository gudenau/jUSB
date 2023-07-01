package net.gudenau.jusb.internal.libusb.descriptor;

import net.gudenau.jusb.descriptor.UsbEndpointDescriptor;
import net.gudenau.jusb.descriptor.UsbInterfaceDescriptor;
import net.gudenau.jusb.internal.ForeignUtils;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;
import java.util.List;

import static net.gudenau.jusb.internal.ForeignUtils.*;

public record LibUsbInterfaceDescriptor(MemorySegment segment) implements UsbInterfaceDescriptor {
    static final GroupLayout LAYOUT = structure(
        U8.withName("bLength"),
        U8.withName("bDescriptorType"),
        U8.withName("bInterfaceNumber"),
        U8.withName("bAlternateSetting"),
        U8.withName("bNumEndpoints"),
        U8.withName("bInterfaceClass"),
        U8.withName("bInterfaceSubClass"),
        U8.withName("bInterfaceProtocol"),
        U8.withName("iInterface"),
        UNBOUND_ADDRESS.withName("endpoint"),
        UNBOUND_ADDRESS.withName("extra"),
        S32.withName("extra_length")
    );

    private static final VarHandle bLength = layoutHandle(LAYOUT, "bLength");
    private static final VarHandle bDescriptorType = layoutHandle(LAYOUT, "bDescriptorType");
    private static final VarHandle bInterfaceNumber = layoutHandle(LAYOUT, "bInterfaceNumber");
    private static final VarHandle bAlternateSetting = layoutHandle(LAYOUT, "bAlternateSetting");
    private static final VarHandle bNumEndpoints = layoutHandle(LAYOUT, "bNumEndpoints");
    private static final VarHandle bInterfaceClass = layoutHandle(LAYOUT, "bInterfaceClass");
    private static final VarHandle bInterfaceSubClass = layoutHandle(LAYOUT, "bInterfaceSubClass");
    private static final VarHandle bInterfaceProtocol = layoutHandle(LAYOUT, "bInterfaceProtocol");
    private static final VarHandle iInterface = layoutHandle(LAYOUT, "iInterface");
    private static final VarHandle endpoint = layoutHandle(LAYOUT, "endpoint");
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
    public byte bInterfaceNumber() {
        return (byte) bInterfaceNumber.get(segment);
    }

    @Override
    public byte bAlternateSetting() {
        return (byte) bAlternateSetting.get(segment);
    }

    @Override
    public byte bNumEndpoints() {
        return (byte) bNumEndpoints.get(segment);
    }

    @Override
    public byte bInterfaceClass() {
        return (byte) bInterfaceClass.get(segment);
    }

    @Override
    public byte bInterfaceSubClass() {
        return (byte) bInterfaceSubClass.get(segment);
    }

    @Override
    public byte bInterfaceProtocol() {
        return (byte) bInterfaceProtocol.get(segment);
    }

    @Override
    public byte iInterface() {
        return (byte) iInterface.get(segment);
    }

    @Override
    public List<UsbEndpointDescriptor> endpoints() {
        return ForeignUtils.array(bNumEndpoints(), (MemorySegment) endpoint.get(segment), LibUsbEndpointDescriptor.LAYOUT, LibUsbEndpointDescriptor::new);
    }

    public MemorySegment extra() {
        return ((MemorySegment) extra.get(segment)).asReadOnly().asSlice(0, extra_length());
    }

    public int extra_length() {
        return (int) extra_length.get(segment);
    }
}
