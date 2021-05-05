package com.example.qrcode.qrCodeManager;

import com.example.qrcode.QRCodeInfo;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FireStoreQRCodeService implements QRCodeService {
    FirebaseFirestore qrCodeDatabase;
    final String TAG = "QRCodeService";

    public FireStoreQRCodeService(FirebaseFirestore qrCodeDatabase) {this.qrCodeDatabase = qrCodeDatabase;}
    
    @Override
    public Task<List<QRCodeInfo>> listQRCodes() {
        return null;
    }
}
