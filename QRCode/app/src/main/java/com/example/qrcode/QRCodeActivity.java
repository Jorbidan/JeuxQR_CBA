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

import com.example.qrcode.gameManager.GameFactory;
import com.example.qrcode.gameManager.GameService;
import com.example.qrcode.gameManager.QRCodeInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class QRCodeActivity extends AppCompatActivity implements View.OnClickListener, QRCodeRecyclerAdapter.QRCodeAdapterInterface {
    final int TAG_ADD_OR_EDIT = 6;
    GameService gameService;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerviewAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<QRCodeInfo> qrCodeInfos = new ArrayList<>();
    Button btn_addQRCode;
    final String TAG = "QRCodeActivity";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == TAG_ADD_OR_EDIT){
            if(data !=  null){
                QRCodeInfo resultQRCodeInfo = (QRCodeInfo) data.getSerializableExtra("QRCodeInfo");
                if(data.hasExtra("isNew")){
                    Boolean isNew = data.getBooleanExtra("isNew", true);
                    if(isNew){
                        qrCodeInfos.add(resultQRCodeInfo);
                        recyclerviewAdapter.notifyDataSetChanged();
                    }else{
                        updateQRCodeList(resultQRCodeInfo);
                    }
                }



            }
        }
    }

    private void updateQRCodeList(QRCodeInfo resultQRCodeInfo) {
        ListIterator<QRCodeInfo> it = qrCodeInfos.listIterator();
        while(it.hasNext()){
            //int i = it.nextIndex();
            QRCodeInfo comparedQRCode = it.next();
            if(comparedQRCode.getQrCode().equals(resultQRCodeInfo.getQrCode())){
                    it.set(resultQRCodeInfo);
            }
        }
        recyclerviewAdapter.notifyDataSetChanged();
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
        Intent editQRCodeIntent = new Intent(QRCodeActivity.this, addOrEditQRCodeActivity.class);
        editQRCodeIntent.putExtra("QRCodeInfo", qrCodeInfo);
        startActivityForResult(editQRCodeIntent, TAG_ADD_OR_EDIT);
    }

    @Override
    public void deleteQRCode(int position) {
        QRCodeInfo qrCodeInfo = qrCodeInfos.get(position);
        gameService.deleteQRCode(qrCodeInfo.getQrCode()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    qrCodeInfos.remove(position);
                    recyclerviewAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}