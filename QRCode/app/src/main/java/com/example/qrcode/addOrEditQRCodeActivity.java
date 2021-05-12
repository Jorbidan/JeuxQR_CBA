package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qrcode.ImageManager.ImageFactory;
import com.example.qrcode.ImageManager.ImageService;
import com.example.qrcode.gameManager.GameFactory;
import com.example.qrcode.gameManager.GameService;
import com.example.qrcode.gameManager.QRCodeInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class addOrEditQRCodeActivity extends AppCompatActivity implements View.OnClickListener{
    final static int TAG_ADD_OR_EDIT = 6;
    final static int TAG_SCAN = 3;
    final static int TAG_IMAGE_CHOSEN = 7;
    Button btn_scan, btn_imageChoose, btn_save, btn_cancel;
    EditText editText_QRCodeId, editText_title, editText_description, editText_question, editText_answer, editText_hint;
    ImageView imageView_imageChosen;
    Intent returnIntent = new Intent();
    private QRCodeInfo qrCodeInfo = new QRCodeInfo();
    Boolean isNew;
    ImageService imageService;
    GameService gameService;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_q_r_code);
        imageService = ImageFactory.getInstance();
        gameService = GameFactory.getInstance();
        initView();

        isNew = true;
        if(getIntent().getExtras() != null){
            isNew = false;
        }
        
        if(!isNew){
            setValues();
        }

    }

    private void setValues() {
        Bundle bundle = getIntent().getExtras();
        if(bundle.containsKey("QRCodeInfo") == true){
            qrCodeInfo = (QRCodeInfo) getIntent().getSerializableExtra("QRCodeInfo");
            editText_QRCodeId.setText(qrCodeInfo.getQrCode());
            editText_title.setText(qrCodeInfo.getTitle());
            editText_question.setText(qrCodeInfo.getQuestion());
            editText_answer.setText(qrCodeInfo.getAnswer());
            editText_description.setText(qrCodeInfo.getDescription());
            editText_hint.setText(qrCodeInfo.getHint());
        }

        if (qrCodeInfo.getImageRef() != null ){
            imageService.downloadImage(qrCodeInfo.getImageRef()).addOnCompleteListener(new OnCompleteListener<Bitmap>() {
                @Override
                public void onComplete(@NonNull Task<Bitmap> task) {
                    Bitmap image;
                    if (task.isSuccessful()) {
                        image = task.getResult();
                        imageView_imageChosen.setImageBitmap(image);

                    }else{
                        Log.d("addOrEditQRCodeActivity", "N'a pas pus receuillir l'image");
                    }

                }
            });
        }

        editText_QRCodeId.setEnabled(false);
        btn_scan.setVisibility(View.GONE);
        btn_scan.setEnabled(false);

    }

    @Override
    public void finish() {
        setResult(TAG_ADD_OR_EDIT, returnIntent);
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
        editText_hint = findViewById(R.id.editText_addOrEditQRCode_hint);
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
                Intent intentScanBarCode =  new Intent(addOrEditQRCodeActivity.this, ScannedBarcodeActivity.class);
                startActivityForResult(intentScanBarCode, TAG_SCAN);
                break;
            case R.id.btn_addOrEditQRCode_image:
                Intent intentImageChoose= new Intent(addOrEditQRCodeActivity.this, ImageChooseActivity.class);
                startActivityForResult(intentImageChoose,TAG_IMAGE_CHOSEN);
                break;
            case R.id.btn_addOrEditQRCode_AddOrEdit:
                prepareSave();
                break;
            case R.id.btn_addOrEditQRCode_Cancel:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle;
        switch(requestCode) {
            case TAG_SCAN:
                bundle = data.getExtras();
                if (bundle.containsKey("QRCodeID") && bundle.get("QRCodeID") != "") {
                    editText_QRCodeId.setText(data.getExtras().get("QRCodeID").toString());
                }
                break;
            case TAG_IMAGE_CHOSEN:
                bundle = data.getExtras();
                if (bundle != null) {
                    if (bundle.containsKey("imageRef")) {
                        String imageRef = bundle.getString("imageRef");
                        qrCodeInfo.setImageRef(imageRef);
                        imageService.downloadImage(imageRef).addOnCompleteListener(new OnCompleteListener<Bitmap>() {
                            @Override
                            public void onComplete(@NonNull Task<Bitmap> task) {
                                if (task.isSuccessful()) {
                                    imageView_imageChosen.setImageBitmap(task.getResult());
                                }
                            }
                        });

                    }
                }
                break;
        }
    }


    private void prepareSave() {
        qrCodeInfo.setQrCode(editText_QRCodeId.getText().toString());
        qrCodeInfo.setTitle(editText_title.getText().toString());
        qrCodeInfo.setDescription(editText_description.getText().toString());
        qrCodeInfo.setQuestion(editText_question.getText().toString());
        qrCodeInfo.setAnswer(editText_answer.getText().toString());
        qrCodeInfo.setHint(editText_hint.getText().toString());

        if(isNew){
            gameService.CheckQRCodeExist(qrCodeInfo.getQrCode()).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                    Boolean exists = (Boolean) task.getResult();
                    if(exists)
                    Toast.makeText(getApplicationContext(), "L'id code QR est déjà utilisé par un autre code QR", Toast.LENGTH_LONG).show();
                    else
                        saveQRCode();
                    }
                }
            });
        }else{
            saveQRCode();
        }
    }

    private void saveQRCode() {

        gameService.setQRCode(qrCodeInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    returnIntent.putExtra("QRCodeInfo", qrCodeInfo);
                    returnIntent.putExtra("isNew", isNew);
                    finish();
                }
            }
        });
    }

}