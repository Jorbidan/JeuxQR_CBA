package com.example.qrcode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qrcode.ImageManager.ImageFactory;
import com.example.qrcode.ImageManager.ImageService;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageChooseActivity extends AppCompatActivity implements View.OnClickListener{
    private final int PICK_IMAGE = 1;
    Button btnAddImage;
    ImageService imageService;
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
        initViews();

    }

    private void initViews() {
        imageService = ImageFactory.getInstance();
        btnAddImage = findViewById(R.id.btn_imageChoose_addImage);
        btnAddImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_imageChoose_addImage:
                openGalleryToAddImage();
                break;
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

        Dialog dialogSetNameAndConfirm = new Dialog(this);
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
                    imageService.uploadImage(bmp, imageName);
                    dialogSetNameAndConfirm.dismiss();
                }
            }
        });
        dialogSetNameAndConfirm.show();
    }
}