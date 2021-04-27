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

import com.example.qrcode.gameManager.GameFactory;
import com.example.qrcode.gameManager.GameService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AdminActivity extends AppCompatActivity {
    Button btnMenuPrincipale;
    Button btnCreateGame;
    TextView textPartieEnCours;
    GameService gameService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        gameService = GameFactory.getInstance();
        btnMenuPrincipale = findViewById(R.id.btn_admin_menuPrincipale);
        btnCreateGame = findViewById(R.id.btn_admin_createGame);
        textPartieEnCours = findViewById(R.id.text_admin_partieEnCours);
        setListeners();
    }

    private void setListeners() {
        btnMenuPrincipale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            textPartieEnCours.append(task.getResult());
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