package com.example.qrcode.ImageManager;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.Task;

public interface ImageService {
    Task<Void> uploadImage(Bitmap imageBitmap, String imageName);
}

