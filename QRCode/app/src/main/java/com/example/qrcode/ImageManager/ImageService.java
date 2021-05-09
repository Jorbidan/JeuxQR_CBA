package com.example.qrcode.ImageManager;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public interface ImageService {
    Task<StorageReference> uploadImage(Bitmap imageBitmap, String imageName);
    Task<Bitmap> downloadImage(String imageName);
    Task<List<StorageReference>> listAllImages();
    Task<Void> deleteImage(String imageName);
}

