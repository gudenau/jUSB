# JUsb

A Java library that attempts to create a simple, modern and reasonably fast library for accessing USB devices.

---

### Basic example

```Java
import java.nio.ByteBuffer;

public final class JUsbExample {
    private static final int VENDOR = 0xDEAD;
    private static final int PRODUCT = 0xBEEF;
    
    public static void main(String[] args) throws Throwable {
        try(var usb = JUsb.init()) {
            var devices = usb.devices();
            System.out.println("Found " + devices.size() + " USB devices");
            
            var target = devices.stream()
                .filter((device) -> {
                    var descriptor = device.descriptor();
                    return descriptor.idVendor() == VENDOR && descriptor.idProduct() == PRODUCT;
                })
                .orElseThrow(() -> new RuntimeException("Failed to find device"));
            
            try(var handle = target.open()) {
                handle.claimInterface(0);
                var buffer = ByteBuffer.allocateDirect(64);
                handle.bulkTransfer(1, UsbDirection.IN, buffer);
                
                buffer.flip();
                while(buffer.hasRemaining()) {
                    System.out.printf("%02X ", buffer.get());
                }
                System.out.println();
            }
        }
    }
}
```
