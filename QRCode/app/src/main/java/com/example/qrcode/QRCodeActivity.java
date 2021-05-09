package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.qrcode.ImageManager.ImageService;
import com.example.qrcode.gameManager.GameFactory;
import com.example.qrcode.gameManager.GameService;
import com.example.qrcode.gameManager.QRCodeInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class QRCodeActivity extends AppCompatActivity implements View.OnClickListener, QRCodeRecyclerAdapter.QRCodeAdapterInterface {
    final int TAG_ADD_OR_EDIT = 6;
    ImageService imageService;
    GameService gameService;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerviewAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<QRCodeInfo> qrCodeInfos = new ArrayList<>();
    Button btn_addQRCode;
    final static String TAG = "QRCodeActivity";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == R.integer.TAG_ADD_OR_EDIT){
            Toast.makeText(QRCodeActivity.this, "THIS WORKS?", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_code);
        gameService = GameFactory.getInstance();
        initViews();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView_qrCode);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerviewAdapter = new QRCodeRecyclerAdapter(qrCodeInfos, this);
        recyclerView.setAdapter(recyclerviewAdapter);
        btn_addQRCode= findViewById(R.id.btn_qrCode_addQRCode);
        btn_addQRCode.setOnClickListener(this);
        gameService.getQueryQRCode().addOnCompleteListener(new OnCompleteListener<List<QRCodeInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<QRCodeInfo>> task) {
                if(task.isSuccessful()){
                    qrCodeInfos.addAll(task.getResult());
                    recyclerviewAdapter.notifyDataSetChanged();
                }else{
                    Log.d(TAG, "Erreur lors de l'appel des QRCodes");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_qrCode_addQRCode:
                Intent addQRCodeIntent = new Intent(QRCodeActivity.this, addOrEditQRCodeActivity.class);
                startActivityForResult(addQRCodeIntent, TAG_ADD_OR_EDIT);
                break;
        }
    }

    @Override
    public void editQRCode(int position) {
        QRCodeInfo qrCodeInfo = qrCodeInfos.get(position);
            imageService.downloadImage(qrCodeInfo.getImageRef()).addOnCompleteListener(new OnCompleteListener<Bitmap>() {
                @Override
                public void onComplete(@NonNull Task<Bitmap> task) {
                    Bitmap image;
                    if (task.isSuccessful()) {
                        image = task.getResult();
                        Intent editQRcodeIntent = new Intent(QRCodeActivity.this, addOrEditQRCodeActivity.class);
                        editQRcodeIntent.putExtra("QRCodeInfo", String.valueOf(qrCodeInfo));
                        editQRcodeIntent.putExtra("Image", image);
                        startActivityForResult(editQRcodeIntent, TAG_ADD_OR_EDIT);
                    }else{
                        Log.d(TAG, "N'a pas pus receuillir l'image");
                    }

                }
            });
    }

    @Override
    public void deleteQRCode(int position) {

    }
}