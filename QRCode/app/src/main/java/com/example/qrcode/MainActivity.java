package com.example.qrcode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int TAG_SCAN = 3;
    Button btnScanBarcode;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TAG_SCAN)
        {
           return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {

        btnScanBarcode = findViewById(R.id.btn_main_scanBarcode);

        btnScanBarcode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_main_scanBarcode:
             Intent intentScanBarCode =  new Intent(MainActivity.this, ScannedBarcodeActivity.class);
             startActivityForResult(intentScanBarCode, TAG_SCAN);
                break;
        }

    }
    
}