package net.gudenau.jusb.descriptor;

import net.gudenau.jusb.UsbDirection;
import net.gudenau.jusb.internal.libusb.LibUsb;
import net.gudenau.jusb.internal.libusb.descriptor.LibUsbEndpointDescriptor;

/**
 * A USB endpoint descriptor.
 */
public sealed interface UsbEndpointDescriptor extends UsbDescriptor permits LibUsbEndpointDescriptor {
    //TODO iso flags
    /**
     * The mask used to extract the transfer from the attributes.
     */
    byte ATTRIBUTE_TRANSFER_MASK = 0x03;

    /**
     * This endpoint uses control transfers.
     */
    byte ATTRIBUTE_TRANSFER_CONTROL = 0x00;

    /**
     * This endpoint uses isochronous transfers.
     */
    byte ATTRIBUTE_TRANSFER_ISOCHRONOUS = 0x01;

    /**
     * This endpoint uses bulk transfers.
     */
    byte ATTRIBUTE_TRANSFER_BULK = 0x02;

    /**
     * This endpoint uses interrupt transfers.
     */
    byte ATTRIBUTE_TRANSFER_INTERRUPT = 0x03;

    /**
     * Gets the raw address of this endpoint.
     *
     * @return the address of this endpoint
     */
    byte bEndpointAddress();

    /**
     * Gets the attributes of this endpoint as a bitmask.
     *
     * @return the attributes
     */
    byte bmAttributes();

    /**
     * The max packet size that this endpoint can send or receive.
     *
     * @return the max packet size
     */
    short wMaxPacketSize();

    /**
     * The interval to poll for data transfers, ignored for bulk and control endpoints.
     *
     * @return the poll interval
     */
    byte bInterval();

    /**
     * The sync address for an audio device.
     *
     * @return the sync address
     */
    byte bSynchAddress();

    /**
     * Gets the transfer direction for this endpoint.
     *
     * @return the transfer direction
     */
    default UsbDirection direction() {
        return (bEndpointAddress() & LibUsb.LIBUSB_ENDPOINT_IN) != 0 ? UsbDirection.IN : UsbDirection.OUT;
    }

    /**
     * Gets the address of this endpoint without the direction mask.
     *
     * @return the endpoint address
     */
    default byte endpointAddress() {
        return (byte) (bEndpointAddress() & ~LibUsb.LIBUSB_ENDPOINT_IN);
    }
}
