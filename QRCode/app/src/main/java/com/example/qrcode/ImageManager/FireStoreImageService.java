package com.example.qrcode.ImageManager;


import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class FireStoreImageService implements ImageService {
    FirebaseStorage firebaseStorage;

    public  FireStoreImageService() { this.firebaseStorage = FirebaseStorage.getInstance();}

    @Override
    public Task<Void> uploadImage(Bitmap imageBitmap, String imageName) {
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference imageRef = storageRef.child("images/"+imageName+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

        return null;
    }



    //DownloadFile https://firebase.google.com/docs/storage/android/download-files
}
