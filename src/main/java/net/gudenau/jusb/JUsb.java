package net.gudenau.jusb;

import net.gudenau.jusb.internal.JUsbImpl;

import java.util.List;

public sealed interface JUsb extends AutoCloseable permits JUsbImpl {
    static JUsbImpl init() throws UsbException {
        return JUsbImpl.init();
    }
    
    List<UsbDevice> devices() throws UsbException;
    
    @Override void close() throws UsbException;
}
