package com.example.qrcode.ImageManager;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.grpc.Context;

public class FireStoreImageService implements ImageService {
    FirebaseStorage firebaseStorage;

    public  FireStoreImageService() { this.firebaseStorage = FirebaseStorage.getInstance();}

    @Override
    public Task<StorageReference> uploadImage(Bitmap imageBitmap, String imageName) {
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference imageRef = storageRef.child("images/"+imageName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        Continuation<UploadTask.TaskSnapshot, StorageReference> uploadTaskContinuation = new Continuation<UploadTask.TaskSnapshot, StorageReference>() {
            @Override
            public StorageReference then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return imageRef;
            }
        };


        return imageRef.putBytes(data).continueWith(uploadTaskContinuation);
    }

    @Override
    public Task<Bitmap> downloadImage(String imageName) {
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference imageReference = storageRef.child("images/"+imageName);
        long TWO_MEGABYTE = 1024 * 1024 * 2;
       Continuation<byte[], Bitmap> downloadImageContinuation = new Continuation<byte[], Bitmap>() {
           @Override
           public Bitmap then(@NonNull Task<byte[]> task) throws Exception {
               if(task.isSuccessful()){
                   byte[] data = task.getResult();
                   return BitmapFactory.decodeByteArray(data, 0, data.length);
               }
               return null;
           }
       };

        return imageReference.getBytes(TWO_MEGABYTE).continueWith(downloadImageContinuation);
    }

    @Override
    public Task<List<StorageReference>> listAllImages() {
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference imagesRef = storageRef.child("images");

        Continuation<ListResult, List<StorageReference>> listAllImagesContinuation = new Continuation<ListResult, List<StorageReference>>() {
            @Override
            public List<StorageReference> then(@NonNull Task<ListResult> task) throws Exception {
                if(task.isSuccessful()){
                    Log.d("FireStoreImageService", task.getResult().getItems().toString());
                    return task.getResult().getItems();
                }
                return null;
            }
        };

        return imagesRef.listAll().continueWith(listAllImagesContinuation);
    }

    @Override
    public Task<Void> deleteImage(String imageName) {
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference imageReference = storageRef.child("images/"+imageName);
        return imageReference.delete();
    }


    //DownloadFile https://firebase.google.com/docs/storage/android/download-files
}
