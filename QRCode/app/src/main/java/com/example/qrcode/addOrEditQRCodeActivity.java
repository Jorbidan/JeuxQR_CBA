package com.example.qrcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class addOrEditQRCodeActivity extends AppCompatActivity implements View.OnClickListener {
    final static int ADD_OR_EDIT = 6;
    Button btn_scan, btn_imageChoose, btn_save, btn_cancel;
    EditText editText_QRCodeId, editText_title, editText_description, editText_question, editText_answer;
    ImageView imageView_imageChosen;
    Intent returnIntent;
    Bundle bundle;
    Boolean isNew = true;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_q_r_code);
        bundle = getIntent().getExtras();
        initView();

        if(bundle.get("QRCodeInfo") != null){
            isNew = false;
        }
        
        if(!isNew){
            setValues();
        }
    }

    private void setValues() {
    }

    @Override
    public void finish() {
        setResult(ADD_OR_EDIT, returnIntent);
        super.finish();
    }

    private void initView() {
        btn_scan = findViewById(R.id.btn_addOrEditQRCode_ScanQRCodeId);
        btn_imageChoose = findViewById(R.id.btn_addOrEditQRCode_image);
        btn_save = findViewById(R.id.btn_addOrEditQRCode_AddOrEdit);
        btn_cancel = findViewById(R.id.btn_addOrEditQRCode_Cancel);
        editText_answer = findViewById(R.id.editText_addOrEditQRCode_answer);
        editText_QRCodeId = findViewById(R.id.editText_addOrEditQRCode_QRCodeId);
        editText_description = findViewById(R.id.editText_addOrEditQRCode_description);
        editText_question = findViewById(R.id.editText_addOrEditQRCode_question);
        editText_title = findViewById(R.id.editText_addOrEditQRCode_Title);
        imageView_imageChosen = findViewById(R.id.imageView_addOrEditQRCode_imageChosen);

        btn_scan.setOnClickListener(this);
        btn_imageChoose.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_addOrEditQRCode_ScanQRCodeId:
                break;
            case R.id.btn_addOrEditQRCode_image:
                break;
            case R.id.btn_addOrEditQRCode_AddOrEdit:
                prepareSave();
                break;
            case R.id.btn_addOrEditQRCode_Cancel:
                finish();
                break;
        }
    }

    private void prepareSave() {
        //TODO: Check if l'id existe déjà
    }
}