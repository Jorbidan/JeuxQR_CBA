package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qrcode.ImageManager.ImageFactory;
import com.example.qrcode.ImageManager.ImageService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageChooseActivity extends AppCompatActivity implements View.OnClickListener, ImageRecyclerAdapter.ImageAdapterInterface {
    private final int IMAGE_CHOSEN_TAG = 7;
    private final int PICK_IMAGE = 1;
    Button btnAddImage, btnCancel;
    ImageService imageService;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<StorageReference> storageReferenceList = new ArrayList<>();
    Intent returnIntent;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case PICK_IMAGE:
                retrieveImage(data);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_choose);
        imageService = ImageFactory.getInstance();
        initViews();

    }

    @Override
    public void finish() {
        setResult(IMAGE_CHOSEN_TAG, returnIntent);
        super.finish();
    }

    private void initViews() {
        btnAddImage = findViewById(R.id.btn_imageChoose_addImage);
        btnAddImage.setOnClickListener(this);
        btnCancel = findViewById(R.id.btn_imageChoose_cancel);
        btnCancel.setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerView_imageChoose);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new ImageRecyclerAdapter(storageReferenceList);
        recyclerView.setAdapter(recyclerViewAdapter);

        imageService.listAllImages().addOnCompleteListener(new OnCompleteListener<List<StorageReference>>() {
            @Override
            public void onComplete(@NonNull Task<List<StorageReference>> task) {
                if(task.isSuccessful()) {
                    storageReferenceList.addAll(task.getResult());
                    recyclerViewAdapter.notifyDataSetChanged();
                }else{
                    Log.d("ImageChooseActivity", "Erreur lors de l'appel des noms d'images");
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_imageChoose_addImage:
                openGalleryToAddImage();
                break;
            case R.id.btn_imageChoose_cancel:
                finish();
        }
    }

    private void openGalleryToAddImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void retrieveImage(Intent data) {
        if(data != null){
            try{
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                addBitmapToDatabase(bmp);
            } catch (FileNotFoundException e){
                Toast.makeText(this, "Erreur lors du téléchargement de l'image", Toast.LENGTH_SHORT).show();
                Log.e("retrieveImageError", e.getMessage());
            }
        }else{
            Log.e("PICK_IMAGE", "NOT WORKING");
        }
    }

    private void addBitmapToDatabase(Bitmap bmp) {

        Dialog dialogSetNameAndConfirm = new Dialog(this, R.style.ThemeOverlay_AppCompat);
        dialogSetNameAndConfirm.setContentView(R.layout.dialog_add_image);
        dialogSetNameAndConfirm.setTitle("Ajout d'image");

        Button btnAddImage = dialogSetNameAndConfirm.findViewById(R.id.btn_dialogAddImage_addImage);
        EditText editTextImageName = dialogSetNameAndConfirm.findViewById(R.id.editText_dialogAddImage_imageName);
        ImageView imageView = dialogSetNameAndConfirm.findViewById(R.id.imageView_dialogAddImage_selectedImage);
        imageView.setImageBitmap(bmp);
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageName = editTextImageName.getText().toString();
                if(imageName == ""){
                    Toast.makeText(getApplicationContext(), "L'image doit avoir un nom", Toast.LENGTH_SHORT).show();
                }else{
                    imageService.uploadImage(bmp, imageName+".jpg");
                    dialogSetNameAndConfirm.dismiss();
                }
            }
        });
        dialogSetNameAndConfirm.show();
    }

    @Override
    public void imageChosen(Bitmap chosenImage, String imageRef) {
        returnIntent.putExtra("imageChosen", chosenImage);
        returnIntent.putExtra("imageRef", imageRef);
        finish();
    }
}