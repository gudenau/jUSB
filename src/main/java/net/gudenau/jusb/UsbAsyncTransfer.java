package net.gudenau.jusb;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

/**
 * An asynchronous USB transfer.
 * <p>
 * While this transfer is in-flight all methods will throw a {@link IllegalStateException}.
 */
public interface UsbAsyncTransfer extends AutoCloseable {
    /**
     * Configures this transfer as a bulk transfer.
     *
     * @param endpoint The endpoint to transfer data from/to
     * @param direction The direction of the transfer
     * @param timeout The timeout of the transfer
     */
    void bulkTransfer(int endpoint, UsbDirection direction, long timeout);
    
    /**
     * Configures this transfer as an interrupt transfer.
     *
     * @param endpoint The endpoint to transfer data from/to
     * @param direction The direction of the transfer
     * @param timeout The timeout of the transfer
     */
    void interruptTransfer(int endpoint, UsbDirection direction, long timeout);
    
    /**
     * Configures this transfer as a control transfer.
     *
     * @param timeout The timeout of the transfer
     */
    void controlTransfer(long timeout);
    
    /**
     * Configures this transfer as a bulk transfer.
     *
     * @param endpoint The endpoint to transfer data from/to
     * @param direction The direction of the transfer
     * @param buffer The data buffer of the transfer
     * @param timeout The timeout of the transfer
     */
    default void bulkTransfer(int endpoint, UsbDirection direction, ByteBuffer buffer, long timeout) {
        buffer(buffer);
        bulkTransfer(endpoint, direction, timeout);
    }
    
    /**
     * Configures this transfer as an interrupt transfer.
     *
     * @param endpoint The endpoint to transfer data from/to
     * @param direction The direction of the transfer
     * @param buffer The data buffer of the transfer
     * @param timeout The timeout of the transfer
     */
    default void interruptTransfer(int endpoint, UsbDirection direction, ByteBuffer buffer, long timeout) {
        buffer(buffer);
        interruptTransfer(endpoint, direction, timeout);
    }
    
    /**
     * Configures this transfer as a control transfer.
     *
     * @param buffer The data buffer of the transfer
     * @param timeout The timeout of the transfer
     */
    default void controlTransfer(ByteBuffer buffer, long timeout) {
        buffer(buffer);
        controlTransfer(timeout);
    }
    
    /**
     * Sets the buffer of this transfer.
     *
     * @param buffer The buffer to use
     */
    void buffer(ByteBuffer buffer);
    
    /**
     * Submits this transfer to the USB subsystem.
     *
     * @return A future for this transfer
     * @throws UsbException if there was a failure submitting this transfer
     */
    CompletableFuture<Result> submit() throws UsbException;
    
    @Override void close() throws UsbException;
    
    /**
     * A result of a transfer. If the buffer is present it will be a slice of the provided one with the length set to
     * the amount of data actually transferred. stall will be set if there was a stall condition or a control request
     * was not supported by the device.
     *
     * @param buffer The buffer of a transfer, if present
     * @param stall True if the transfer stalled
     */
    record Result(ByteBuffer buffer, boolean stall) {}
}
