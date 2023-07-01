package net.gudenau.jusb;

import net.gudenau.jusb.internal.UsbDeviceHandleImpl;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;

/**
 * A USB device handle.
 */
public sealed interface UsbDeviceHandle extends AutoCloseable permits UsbDeviceHandleImpl {
    /**
     * Sets the configuration the device should use.
     *
     * @param configuration the desired configuration
     * @throws UsbException if the configuration could not be set
     */
    void setConfiguration(int configuration) throws UsbException;

    /**
     * Gets the current configuration of the device.
     *
     * @return The current configuration
     * @throws UsbException if the configuration could not be read
     */
    int getConfiguration() throws UsbException;
    
    /**
     * Claims an interface on the device for this application to use.
     *
     * @param iface the interface to claim
     * @throws UsbException if the interface could not be claimed
     */
    void claimInterface(int iface) throws UsbException;
    
    /**
     * Attempts to perform a synchronous control transfer with the device.
     *
     * @param requestType The type field
     * @param request The request field
     * @param value The value field
     * @param index The index field
     * @param buffer The buffer of data to transfer
     * @param timeout The timeout (in milliseconds) of this transfer
     * @return The amount of data transferred
     * @throws UsbException if there was a failure completing this request
     * @throws TimeoutException if the request timed out
     */
    int controlTransfer(int requestType, int request, int value, int index, ByteBuffer buffer, long timeout) throws UsbException, TimeoutException;
    
    /**
     * Attempts to perform a synchronous bulk transfer with the device.
     *
     * @param endpoint The endpoint to transfer data to/from
     * @param direction The direction of the transfer
     * @param buffer The buffer of data to read/write
     * @param timeout The timeout (in milliseconds) of this transfer
     * @return The amount of data transferred
     * @throws UsbException if there was a failure completing this request
     * @throws TimeoutException if the request timed out
     */
    int bulkTransfer(int endpoint, UsbDirection direction, ByteBuffer buffer, long timeout) throws UsbException, TimeoutException;
    
    /**
     * Attempts to perform a synchronous interrupt transfer with the device.
     *
     * @param endpoint The endpoint to transfer data to/from
     * @param direction The direction of the transfer
     * @param buffer The buffer of data to read/write
     * @param timeout The timeout (in milliseconds) of this transfer
     * @return The amount of data transferred
     * @throws UsbException if there was a failure completing this request
     * @throws TimeoutException if the request timed out
     */
    int interruptTransfer(int endpoint, UsbDirection direction, ByteBuffer buffer, long timeout) throws UsbException, TimeoutException;
    
    /**
     * Attempts to perform a synchronous control transfer with the device.
     *
     * @param requestType The type field
     * @param request The request field
     * @param value The value field
     * @param index The index field
     * @param buffer The buffer of data to transfer
     * @return The amount of data transferred
     * @throws UsbException if there was a failure completing this request
     */
    default int controlTransfer(int requestType, int request, int value, int index, ByteBuffer buffer) throws UsbException {
        try {
            return controlTransfer(requestType, request, value, index, buffer, 0);
        } catch(TimeoutException e) {
            throw new UsbException("Timed out during unlimited timeout control transfer?", e);
        }
    }
    
    /**
     * Attempts to perform a synchronous bulk transfer with the device.
     *
     * @param endpoint The endpoint to transfer data to/from
     * @param direction The direction of the transfer
     * @param buffer The buffer of data to read/write
     * @return The amount of data transferred
     * @throws UsbException if there was a failure completing this request
     */
    default int bulkTransfer(int endpoint, UsbDirection direction, ByteBuffer buffer) throws UsbException {
        try {
            return bulkTransfer(endpoint, direction, buffer, 0);
        } catch(TimeoutException e) {
            throw new UsbException("Timed out during unlimited timeout bulk transfer?", e);
        }
    }
    
    /**
     * Attempts to perform a synchronous interrupt transfer with the device.
     *
     * @param endpoint The endpoint to transfer data to/from
     * @param direction The direction of the transfer
     * @param buffer The buffer of data to read/write
     * @return The amount of data transferred
     * @throws UsbException if there was a failure completing this request
     */
    default int interruptTransfer(int endpoint, UsbDirection direction, ByteBuffer buffer) throws UsbException {
        try {
            return interruptTransfer(endpoint, direction, buffer, 0);
        } catch(TimeoutException e) {
            throw new UsbException("Timed out during unlimited timeout interrupt transfer?", e);
        }
    }
    
    /**
     * Creates a new {@link UsbAsyncTransfer} instance.
     *
     * @return a new {@link UsbAsyncTransfer} instance
     * @throws UsbException If async transfers are not enabled or the transfer could not be created
     */
    UsbAsyncTransfer createTransfer() throws UsbException;

    String stringDescriptor(byte index) throws UsbException;

    @Override void close() throws UsbException;
}
