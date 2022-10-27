package net.gudenau.jusb.descriptor;

import net.gudenau.jusb.internal.libusb.LibUsbDeviceDescriptor;

public sealed interface UsbDeviceDescriptor permits LibUsbDeviceDescriptor {
    byte bLength();
    byte bDescriptorType();
    short bcdUSB();
    byte bDeviceClass();
    byte bDeviceSubClass();
    byte bDeviceProtocol();
    byte bMaxPacketSize0();
    int idVendor();
    int idProduct();
    short bcdDevice();
    byte iManufacturer();
    byte iProduct();
    byte iSerialNumber();
    byte bNumConfigurations();
}
