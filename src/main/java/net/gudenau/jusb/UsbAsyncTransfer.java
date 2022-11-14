package net.gudenau.jusb;

import java.lang.foreign.MemorySegment;
import java.util.concurrent.CompletableFuture;

public interface UsbAsyncTransfer extends AutoCloseable {
    void bulkTransfer(int endpoint, UsbDirection direction, long timeout);
    void interruptTransfer(int endpoint, UsbDirection direction, long timeout);
    void controlTransfer(long timeout);
    
    default void bulkTransfer(int endpoint, UsbDirection direction, MemorySegment buffer, long timeout) {
        buffer(buffer);
        bulkTransfer(endpoint, direction, timeout);
    }
    
    default void interruptTransfer(int endpoint, UsbDirection direction, MemorySegment buffer, long timeout) {
        buffer(buffer);
        interruptTransfer(endpoint, direction, timeout);
    }
    
    default void controlTransfer(MemorySegment buffer, long timeout) {
        buffer(buffer);
        controlTransfer(timeout);
    }
    
    void buffer(MemorySegment segment);
    
    CompletableFuture<Result> submit() throws UsbException;
    
    @Override void close() throws UsbException;
    
    record Result(MemorySegment segment, boolean stall) {}
}
