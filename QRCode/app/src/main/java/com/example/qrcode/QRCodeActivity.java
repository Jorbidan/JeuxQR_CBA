package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.qrcode.ImageManager.ImageService;
import com.example.qrcode.gameManager.GameFactory;
import com.example.qrcode.gameManager.GameService;
import com.example.qrcode.gameManager.QRCodeInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class QRCodeActivity extends AppCompatActivity implements View.OnClickListener {
    ImageService imageService;
    GameService gameService;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerviewAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<QRCodeInfo> qrCodeInfos = new ArrayList<>();
    final static String TAG = "QRCodeActivity";

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
        recyclerviewAdapter = new QRCodeRecyclerAdapter(qrCodeInfos);
        recyclerView.setAdapter(recyclerviewAdapter);

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

    }
}