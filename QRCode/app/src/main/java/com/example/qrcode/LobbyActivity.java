package com.example.qrcode;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.qrcode.gameManager.GameFactory;
import com.example.qrcode.gameManager.GameService;

public class LobbyActivity extends AppCompatActivity {
    TextView textCurrentGame;
    GameService gameService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        gameService = GameFactory.getInstance();

        textCurrentGame = findViewById(R.id.text_lobby_currentGame);
        //textCurrentGame.setText(gameService.getCurrentGameCodeOfPlayer());
    }
}