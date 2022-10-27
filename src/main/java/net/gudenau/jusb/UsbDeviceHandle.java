package net.gudenau.jusb;

import net.gudenau.jusb.internal.UsbDeviceHandleImpl;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;

public sealed interface UsbDeviceHandle extends AutoCloseable permits UsbDeviceHandleImpl {
    void setConfiguration(int configuration) throws UsbException;
    
    void claimInterface(int iface) throws UsbException;
    
    int controlTransfer(int requestType, int request, int value, int index, ByteBuffer buffer, long timeout) throws UsbException, TimeoutException;
    int bulkTransfer(int endpoint, UsbDirection direction, ByteBuffer buffer, long timeout) throws UsbException, TimeoutException;
    int interruptTransfer(int endpoint, UsbDirection direction, ByteBuffer buffer, long timeout) throws UsbException, TimeoutException;
    
    default int controlTransfer(int requestType, int request, int value, int index, ByteBuffer buffer) throws UsbException {
        try {
            return controlTransfer(requestType, request, value, index, buffer, 0);
        } catch(TimeoutException e) {
            throw new UsbException("Timed out during unlimited timeout control transfer?", e);
        }
    }
    
    default int bulkTransfer(int endpoint, UsbDirection direction, ByteBuffer buffer) throws UsbException {
        try {
            return bulkTransfer(endpoint, direction, buffer, 0);
        } catch(TimeoutException e) {
            throw new UsbException("Timed out during unlimited timeout bulk transfer?", e);
        }
    }
    
    default int interruptTransfer(int endpoint, UsbDirection direction, ByteBuffer buffer) throws UsbException {
        try {
            return interruptTransfer(endpoint, direction, buffer, 0);
        } catch(TimeoutException e) {
            throw new UsbException("Timed out during unlimited timeout interrupt transfer?", e);
        }
    }
    
    @Override void close() throws UsbException;
}
