package net.gudenau.jusb;

import java.io.IOException;

public class UsbException extends IOException {
    public UsbException(String message) {
        super(message);
    }
    
    public UsbException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UsbException(Throwable cause) {
        super(cause);
    }
}
