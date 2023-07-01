package net.gudenau.jusb.internal.libusb.descriptor;

import net.gudenau.jusb.descriptor.UsbInterface;
import net.gudenau.jusb.descriptor.UsbInterfaceDescriptor;
import net.gudenau.jusb.internal.ForeignUtils;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;
import java.util.List;

import static net.gudenau.jusb.internal.ForeignUtils.*;

public record LibUsbInterface(MemorySegment segment) implements UsbInterface {
    static final GroupLayout LAYOUT = structure(
        UNBOUND_ADDRESS.withName("altsetting"),
        S32.withName("num_altsetting")
    );

    private static final VarHandle altsetting = layoutHandle(LAYOUT, "altsetting");
    private static final VarHandle num_altsetting = layoutHandle(LAYOUT, "num_altsetting");

    @Override
    public List<UsbInterfaceDescriptor> altsetting() {
        return ForeignUtils.array(num_altsetting(), (MemorySegment) altsetting.get(segment), LibUsbInterfaceDescriptor.LAYOUT, LibUsbInterfaceDescriptor::new);
    }

    public int num_altsetting() {
        return (int) num_altsetting.get(segment);
    }
}
