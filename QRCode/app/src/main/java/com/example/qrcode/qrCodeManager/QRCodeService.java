package com.example.qrcode.qrCodeManager;

import com.example.qrcode.QRCodeInfo;
import com.google.android.gms.tasks.Task;

import java.util.List;

public interface QRCodeService {
    Task<List<QRCodeInfo>> listQRCodes();
}
