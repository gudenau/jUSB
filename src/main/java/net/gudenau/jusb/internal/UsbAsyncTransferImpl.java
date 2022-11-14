package net.gudenau.jusb.internal;

import net.gudenau.jusb.UsbAsyncTransfer;
import net.gudenau.jusb.UsbDirection;
import net.gudenau.jusb.UsbException;
import net.gudenau.jusb.internal.libusb.LibUsb;
import net.gudenau.jusb.internal.libusb.LibUsbTransfer;
import net.gudenau.jusb.internal.libusb.LibUsbTransferCallback;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static net.gudenau.jusb.internal.Utils.clampToUnsignedInt;
import static net.gudenau.jusb.internal.Utils.endpoint;

public final class UsbAsyncTransferImpl implements UsbAsyncTransfer {
    private final LibUsbTransfer transfer;
    private final MemorySession session;
    private volatile MemorySegment buffer;
    private volatile CompletableFuture<Result> future;
    private volatile boolean closed;
    
    public UsbAsyncTransferImpl(UsbDeviceHandleImpl handle, LibUsbTransfer transfer) {
        this.transfer = transfer;
        
        session = MemorySession.openShared();
        var callback = LibUsbTransferCallback.allocate((callbackTransfer) -> {
            synchronized(this) {
                var status = callbackTransfer.status();
                
                switch(status) {
                    case LibUsb.LIBUSB_TRANSFER_COMPLETED -> {
                        var segment = buffer.asSlice(0, transfer.actual_length());
                        future.complete(new Result(segment, false));
                    }
                    
                    case LibUsb.LIBUSB_TRANSFER_TIMED_OUT -> future.completeExceptionally(new TimeoutException("Usb transfer timed out"));
                    
                    case LibUsb.LIBUSB_TRANSFER_CANCELLED -> future.cancel(true);
                    
                    case LibUsb.LIBUSB_TRANSFER_STALL -> future.complete(new Result(null, true));
                    
                    default -> future.completeExceptionally(new UsbException("Failed to complete transfer: " + LibUsb.libusb_error_name(status)));
                }
                
                future = null;
            }
        }, session);
        
        transfer.segment().fill((byte) 0);
        transfer.dev_handle(handle.handle())
            .callback(callback);
    }
    
    @Override
    public void bulkTransfer(int endpoint, UsbDirection direction, long timeout) {
        Objects.requireNonNull(direction, "direction can't be null");
        synchronized(this) {
            validateMutableState();
            transfer.endpoint(endpoint(endpoint, direction))
                .type((byte) LibUsb.LIBUSB_TRANSFER_TYPE_BULK)
                .timeout(clampToUnsignedInt(timeout));
        }
    }
    
    @Override
    public void interruptTransfer(int endpoint, UsbDirection direction, long timeout) {
        Objects.requireNonNull(direction, "direction can't be null");
        synchronized(this) {
            validateMutableState();
            transfer.endpoint(endpoint(endpoint, direction))
                .type((byte) LibUsb.LIBUSB_TRANSFER_TYPE_INTERRUPT)
                .timeout(clampToUnsignedInt(timeout));
        }
    }
    
    @Override
    public void controlTransfer(long timeout) {
        synchronized(this) {
            validateMutableState();
            transfer.endpoint((byte) 0)
                .type((byte) LibUsb.LIBUSB_TRANSFER_TYPE_CONTROL)
                .timeout(clampToUnsignedInt(timeout));
        }
    }
    
    @Override
    public void buffer(MemorySegment buffer) {
        Objects.requireNonNull(buffer, "buffer can't be null");
        synchronized(this) {
            validateMutableState();
            this.buffer = buffer;
            transfer.buffer(buffer)
                .length(clampToUnsignedInt(buffer.byteSize()));
        }
    }
    
    // Only call this in a `synchronized` block
    private void validateMutableState() {
        if(closed) {
            throw new IllegalStateException("Can not modify a closed transfer");
        }
        if(future != null) {
            throw new IllegalStateException("Can not modify an in-flight transfer");
        }
    }
    
    @Override
    public CompletableFuture<Result> submit() throws UsbException {
        synchronized(this) {
            validateMutableState();
            
            int result = LibUsb.libusb_submit_transfer(transfer);
            if(result != LibUsb.LIBUSB_SUCCESS) {
                throw new UsbException("Failed to submit transfer: " + LibUsb.libusb_error_name(result));
            }
    
            future = new CompletableFuture<>();
            return future;
        }
    }
    
    @Override
    public void close() throws UsbException {
        synchronized(this) {
            validateMutableState();
            closed = true;
            LibUsb.libusb_free_transfer(transfer);
            session.close();
        }
    }
}
