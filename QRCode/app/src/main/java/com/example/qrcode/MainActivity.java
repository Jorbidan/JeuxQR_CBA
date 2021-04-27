package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrcode.authentication.AuthenticationFactory;
import com.example.qrcode.authentication.AuthenticationService;
import com.example.qrcode.gameManager.GameFactory;
import com.example.qrcode.gameManager.GameService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int TAG_SCAN = 3;
    Button btnScanBarcode;
    Button buttonAdmin;
    TextView textGameCode;
    TextView textPlayerName;
    Button btnPlay;
    GameService gameService;
    AuthenticationService authenticationService;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TAG_SCAN)
        {
            Toast.makeText(this, data.getStringExtra("QRCodeID"), Toast.LENGTH_SHORT ).show();

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
        gameService = GameFactory.getInstance();
        authenticationService = AuthenticationFactory.getInstance();

        btnScanBarcode = findViewById(R.id.btn_main_scanBarcode);
        btnScanBarcode.setOnClickListener(this);
        buttonAdmin = findViewById(R.id.btn_main_adminLogin);
        buttonAdmin.setOnClickListener(this);
        btnPlay = findViewById(R.id.btn_main_play);
        btnPlay.setOnClickListener(this);
        textGameCode = findViewById(R.id.text_main_GameCode);
        textPlayerName = findViewById(R.id.text_main_playerName);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_main_scanBarcode:
             Intent intentScanBarCode =  new Intent(MainActivity.this, ScannedBarcodeActivity.class);
             startActivityForResult(intentScanBarCode, TAG_SCAN);
                break;
            case R.id.btn_main_adminLogin:
                Intent intentLoginAdmin = new Intent(MainActivity.this,AdminLoginActivity.class);
                startActivity(intentLoginAdmin);
                finish();
                break;
            case R.id.btn_main_play:
                String playerName = textPlayerName.getText().toString();
                String gameCode = textGameCode.getText().toString();
                if (!gameCode.isEmpty() && !playerName.isEmpty()) {
                    gameService.checkGameExist(gameCode).addOnCompleteListener(new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {
                            if (task.getResult()){
                                gameService.joinGame(gameCode,playerName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Log.e("joinGame","player "+playerName+" has joined game "+gameCode);
                                        }else{
                                            Log.e("joinGame",task.getException().getMessage());
                                        }
                                    }
                                });
                                authenticationService.anonymousLogin();
                                goToLobbyActivity();
                            }
                            else{
                                Toast.makeText(MainActivity.this,"Le code entrée n'est pas valide.",Toast.LENGTH_SHORT).show();
                                Log.e("CHECKGAMECODE", "game " + gameCode + " doesn't exist");
                            }
                        }
                    });
                }
                else{
                    if (gameCode.isEmpty() && !playerName.isEmpty()){
                        Toast.makeText(MainActivity.this,"Vous n'avez pas entrer de code.",Toast.LENGTH_SHORT).show();
                    }
                    else if (!gameCode.isEmpty() && playerName.isEmpty()){
                        Toast.makeText(MainActivity.this,"Vous n'avez pas entrer de nom.",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Vous n'avez pas entrer de nom et de code.",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

    }

    private void goToLobbyActivity() {
            Intent goToLobbyActivity = new Intent(this,LobbyActivity.class);
            startActivity(goToLobbyActivity);
            finish();
    }

}