package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrcode.authentication.AuthenticationFactory;
import com.example.qrcode.authentication.AuthenticationService;
import com.example.qrcode.gameManager.GameFactory;
import com.example.qrcode.gameManager.GameService;
import com.example.qrcode.gameManager.QRCodeInfo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    final String TAG = "AdminActivity";

    Button btnMenuPrincipale, btnCreateGame, btnManageQrCode, btnEndGame, btnStartGame;
    TextView textPartieEnCours;
    GameService gameService;
    List<QRCodeInfo> qrCodeInfos = new ArrayList<>();
    AuthenticationService authenticationService;
    String gameCode = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        gameService = GameFactory.getInstance();
        authenticationService = AuthenticationFactory.getInstance();
        btnStartGame = findViewById(R.id.button_admin_startGame);
        btnEndGame = findViewById(R.id.btn_admin_endgame);
        btnMenuPrincipale = findViewById(R.id.btn_admin_menuPrincipale);
        btnCreateGame = findViewById(R.id.btn_admin_createGame);
        btnManageQrCode = findViewById(R.id.btn_admin_manageQrCodes);
        textPartieEnCours = findViewById(R.id.text_admin_partieEnCours);
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
                            Log.e("gameCREATED",task.getException().getMessage());
                        }
                        else{
                            gameCode = task.getResult();
                            textPartieEnCours.setText(gameCode);
                            btnCreateGame.setVisibility(View.INVISIBLE);
                            btnEndGame.setVisibility(View.VISIBLE);
                            btnStartGame.setVisibility(View.VISIBLE);
                            gameService.getQueryQRCode().addOnCompleteListener(new OnCompleteListener<List<QRCodeInfo>>() {
                                @Override
                                public void onComplete(@NonNull Task<List<QRCodeInfo>> task) {
                                    if (task.isSuccessful()){
                                        qrCodeInfos.clear();
                                        qrCodeInfos.addAll(task.getResult());
                                        for(int i = 0;i<qrCodeInfos.size();i++){
                                            Log.e(TAG,qrCodeInfos.get(i).getQrCode());
                                        }
                                    }
                                    gameService.setQRCodesToGame(gameCode, qrCodeInfos);
                                }
                            });
                        }
                    }
                });
            }
        });
        btnManageQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToQRCodeActivityIntent = new Intent(getApplicationContext(),QRCodeActivity.class);
                startActivity(goToQRCodeActivityIntent);
            }
        });
        btnEndGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AdminActivity.this)
                        .setTitle("Partie "+gameCode)
                        .setMessage("Êtes-vous certain de vouloir terminer cette partie ?")
                        .setPositiveButton("Oui !", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                gameService.endGame(gameCode);
                                textPartieEnCours.setText("Aucune partie en cours");
                                btnCreateGame.setVisibility(View.VISIBLE);
                                btnEndGame.setVisibility(View.INVISIBLE);
                                btnStartGame.setVisibility(View.INVISIBLE);
                                Toast.makeText(AdminActivity.this,"La partie est terminer.",Toast.LENGTH_SHORT).show();
                            }}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(AdminActivity.this,"Aucune action prise",Toast.LENGTH_SHORT).show();
                    }
                }).show();

            }
        });
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameService.startGame(gameCode).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG,gameCode +" has started.");
                        btnStartGame.setVisibility(View.INVISIBLE);
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

    @Override
    public void onBackPressed() {
        goToMainActivity();
    }

    private void goToMainActivity() {
        if (!gameCode.equals("")) {
            gameService.endGame(gameCode);
            Toast.makeText(this, "Votre partie c'est terminé puisque vous êtes retourné au menu principal.", Toast.LENGTH_SHORT).show();
        }
        Intent goToMainActivityIntent = new Intent(this,MainActivity.class);
        startActivity(goToMainActivityIntent);
        finish();
    }
}