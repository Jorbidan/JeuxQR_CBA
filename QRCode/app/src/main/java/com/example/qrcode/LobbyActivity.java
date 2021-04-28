package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.qrcode.authentication.AuthenticationFactory;
import com.example.qrcode.authentication.AuthenticationService;
import com.example.qrcode.gameManager.GameFactory;
import com.example.qrcode.gameManager.GameService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LobbyActivity extends AppCompatActivity {
    TextView textCurrentGame;
    TextView textDisplayName;
    String displayName;
    String gameCode;
    GameService gameService;
    AuthenticationService authenticationService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        gameService = GameFactory.getInstance();
        authenticationService = AuthenticationFactory.getInstance();
        displayName = "";
        gameCode = "";
        textCurrentGame = findViewById(R.id.text_lobby_currentGame);
        textDisplayName = findViewById(R.id.text_lobby_displayName);
        setData();
        setListeners();
    }

    private void setListeners() {
    }

    private void setData() {
        try {
            displayName = authenticationService.getCurrentUserDisplayName();
            textDisplayName.setText(displayName);
        }catch (Exception e){
            Log.e("setDataLobby","displayName :"+e.getMessage());
        }
        gameService.getCurrentGameCodeOfPlayer(displayName).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()){
                    Log.e("getGameCodeLobby","gameCode :"+task.getException().getMessage());
                }else{
                    Log.e("gameCOdeFOund",task.getResult());
                    gameCode = task.getResult();
                    textCurrentGame.setText("Code de partie : " + gameCode);
                }
            }
        });
    }
}