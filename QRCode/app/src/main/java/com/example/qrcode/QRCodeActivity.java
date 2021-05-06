package com.example.qrcode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.qrcode.ImageManager.ImageService;
import com.example.qrcode.gameManager.GameFactory;
import com.example.qrcode.gameManager.GameService;
import com.example.qrcode.gameManager.QRCodeInfo;

import java.util.ArrayList;
import java.util.List;

public class QRCodeActivity extends AppCompatActivity implements View.OnClickListener {
    ImageService imageService;
    GameService gameService;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerviewAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<QRCodeInfo> qrCodeInfos = new ArrayList<>();

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
    }

    @Override
    public void onClick(View v) {

    }
}