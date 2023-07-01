package net.gudenau.jusb.internal.libusb.descriptor;

import net.gudenau.jusb.descriptor.UsbConfigDescriptor;
import net.gudenau.jusb.descriptor.UsbInterface;
import net.gudenau.jusb.internal.ForeignUtils;
import net.gudenau.jusb.internal.libusb.LibUsb;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;
import java.util.List;

import static net.gudenau.jusb.internal.ForeignUtils.*;

public record LibUsbConfigDescriptor(MemorySegment segment) implements UsbConfigDescriptor {
    public static final GroupLayout LAYOUT = structure(
        U8.withName("bLength"),
        U8.withName("bDescriptorType"),
        U16.withName("wTotalLength"),
        U8.withName("bNumInterfaces"),
        U8.withName("bConfigurationValue"),
        U8.withName("iConfiguration"),
        U8.withName("bmAttributes"),
        U8.withName("MaxPower"),
        UNBOUND_ADDRESS.withName("interface"),
        UNBOUND_ADDRESS.withName("extra"),
        U32.withName("extra_length")
    );

    private static final VarHandle bLength = layoutHandle(LAYOUT, "bLength");
    private static final VarHandle bDescriptorType = layoutHandle(LAYOUT, "bDescriptorType");
    private static final VarHandle wTotalLength = layoutHandle(LAYOUT, "wTotalLength");
    private static final VarHandle bNumInterfaces = layoutHandle(LAYOUT, "bNumInterfaces");
    private static final VarHandle bConfigurationValue = layoutHandle(LAYOUT, "bConfigurationValue");
    private static final VarHandle iConfiguration = layoutHandle(LAYOUT, "iConfiguration");
    private static final VarHandle bmAttributes = layoutHandle(LAYOUT, "bmAttributes");
    private static final VarHandle MaxPower = layoutHandle(LAYOUT, "MaxPower");
    private static final VarHandle interfaces = layoutHandle(LAYOUT, "interface");
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
    public short wTotalLength() {
        return (short) wTotalLength.get(segment);
    }

    @Override
    public byte bNumInterfaces() {
        return (byte) bNumInterfaces.get(segment);
    }

    @Override
    public byte bConfigurationValue() {
        return (byte) bConfigurationValue.get(segment);
    }

    @Override
    public byte iConfiguration() {
        return (byte) iConfiguration.get(segment);
    }

    @Override
    public byte bmAttributes() {
        return (byte) bmAttributes.get(segment);
    }

    @Override
    public byte maxPower() {
        return (byte) MaxPower.get(segment);
    }

    @Override
    public List<UsbInterface> interfaces() {
        return ForeignUtils.array(bNumInterfaces() & 0xFF, (MemorySegment) interfaces.get(segment), LibUsbInterface.LAYOUT, LibUsbInterface::new);
    }

    public MemorySegment extra() {
        return ((MemorySegment) extra.get(segment)).asReadOnly().asSlice(0, extra_length());
    }

    public int extra_length() {
        return (int) extra_length.get(segment);
    }

    @Override
    public void close() {
        LibUsb.libusb_free_config_descriptor(this);
    }
}
