package com.example.qrcode.qrCodeManager;

public class QRCodeFactory {
    public static QRCodeService getInstance() {return new FireStoreQRCodeService();}
}
