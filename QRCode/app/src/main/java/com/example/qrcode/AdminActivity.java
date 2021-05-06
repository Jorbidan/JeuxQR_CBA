package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.qrcode.authentication.AuthenticationFactory;
import com.example.qrcode.authentication.AuthenticationService;
import com.example.qrcode.gameManager.GameFactory;
import com.example.qrcode.gameManager.GameService;
import com.example.qrcode.gameManager.QRCodeInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class AdminActivity extends AppCompatActivity {

    Button btnTest;

    Button btnMenuPrincipale;
    Button btnCreateGame;
    TextView textPartieEnCours;
    GameService gameService;
    AuthenticationService authenticationService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        gameService = GameFactory.getInstance();
        authenticationService = AuthenticationFactory.getInstance();
        btnMenuPrincipale = findViewById(R.id.btn_admin_menuPrincipale);
        btnCreateGame = findViewById(R.id.btn_admin_createGame);
        textPartieEnCours = findViewById(R.id.text_admin_partieEnCours);
        btnTest = findViewById(R.id.button_testingGetQRCode);
        setListeners();
    }

    private void setListeners() {
        btnMenuPrincipale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticationService.logoff();
                goToMainActivity();
            }
        });
        btnCreateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameService.createGame(new GameService.OnGameCreate() {
                    @Override
                    public void OnCreateGame() {
                        StartTimer();
                    }
                }).addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()){
                            Log.e("gameCREATED","error occured");
                        }
                        else{
                            textPartieEnCours.setText(task.getResult());
                        }
                    }
                });
            }
        });
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameService.getQueryQRCode().addOnCompleteListener(new OnCompleteListener<List<QRCodeInfo>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<QRCodeInfo>> task) {
                        if (!task.isSuccessful()){
                            Log.e("TAG",task.getException().getMessage());
                        }
                    }
                });
            }
        });
    }

    private void StartTimer() {
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
               //on each seconds
            }

            public void onFinish() {
                //timer end
            }
        }.start();

    }

    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this,MainActivity.class);
        startActivity(goToMainActivityIntent);
        finish();
    }
}