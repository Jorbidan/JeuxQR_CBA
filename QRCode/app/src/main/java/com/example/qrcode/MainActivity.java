package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrcode.authentication.AuthenticationFactory;
import com.example.qrcode.authentication.AuthenticationService;
import com.example.qrcode.gameManager.GameFactory;
import com.example.qrcode.gameManager.GameService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.auth.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    Bitmap bmp;
    private final int TAG_SCAN = 3;
    private final int PICK_IMAGE = 4;;
    Button buttonAdmin;
    TextView textGameCode;
    TextView textPlayerName;
    Button btnPlay;
    GameService gameService;
    AuthenticationService authenticationService;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case TAG_SCAN:
                Toast.makeText(this, data.getStringExtra("QRCodeID"), Toast.LENGTH_SHORT).show();
                break;
            case PICK_IMAGE:
                if(data == null){
                    Log.e("PICK_IMAGE", "error while getting data");
                }

                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
                    bmp = BitmapFactory.decodeStream(inputStream);
                    Log.d("PICK_IMAGE", "BitMap got");
                } catch (FileNotFoundException e) {
                    Log.e("PICK_IMAGE", "error while trying to get data");
                    e.printStackTrace();
                }
                break;

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initViews();
        //authenticationService.logoff();
    }

    private void initViews() {
        gameService = GameFactory.getInstance();
        authenticationService = AuthenticationFactory.getInstance();
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
                                gameService.joinGame(gameCode,playerName.toLowerCase()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            if(authenticationService.isLogIn()){
                                                authenticationService.logoff();
                                            }
                                            Log.e("joinGame",playerName+" a rejoin la partie "+gameCode);
                                        }else{
                                            Log.e("joinGame",task.getException().getMessage());
                                        }
                                    }
                                });
                                try {
                                    SharedPreferences sharedPreferences = getSharedPreferences("playerName",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("playerName", playerName);
                                    editor.commit();
                                }
                                catch(Exception e){
                                    Log.e("CachedFile","Could noty create cached file due to : "+e.getMessage());
                                }
                                goToLobbyActivity();
                            }
                            else {
                                Toast.makeText(MainActivity.this,"Le code entr??e n'est pas valide ou la partie est d??j?? d??marrer.",Toast.LENGTH_SHORT).show();
                                Log.e("CHECKGAMECODE", "game " + gameCode + " doesn't exist/is started");
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
    }

}