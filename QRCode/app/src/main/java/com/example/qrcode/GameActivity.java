package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrcode.gameManager.GameFactory;
import com.example.qrcode.gameManager.GameService;
import com.example.qrcode.gameManager.QRCodeInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    Button btnScan;
    TextView txtIndice, txtInfo,txtDisplayName, txtCurrentGame, txtTime, txtQuestion,txtAnswer;
    ImageView imageView;
    GameService gameService;
    String displayName,gameCode;
    GameService.Subscription subscriptionToGame = null;
    final static int TAG_SCAN = 3;
    QRCodeInfo currentCodeScanned;
    Integer currentlyLookingFor, timeSecondes;
    Boolean finished = false;
    List<QRCodeInfo> qrCodeInfos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameService = GameFactory.getInstance();
        btnScan = findViewById(R.id.btn_game_scan);
        txtQuestion = findViewById(R.id.textView_game_question);
        txtAnswer = findViewById(R.id.editText_game_answer);
        imageView = findViewById(R.id.imageView_game_image);
        txtTime = findViewById(R.id.textView_game_time);
        txtIndice = findViewById(R.id.textView_game_indice);
        txtCurrentGame = findViewById(R.id.textView_game_gameCode);
        txtDisplayName = findViewById(R.id.textView_game_displayName);
        txtInfo = findViewById(R.id.textView_game_description);
        currentCodeScanned = new QRCodeInfo();
        currentlyLookingFor = 0;
        timeSecondes = 0;
        setData();
        setListeners();
    }

    @Override
    public void onBackPressed() {
        alertQuittingGame();
    }

    private void setData() {
        txtAnswer.setVisibility(View.INVISIBLE);
        txtQuestion.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        try {
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences("playerName",MODE_PRIVATE);
            displayName = sharedPreferences.getString("playerName","Aucun nom trouvé");
            txtDisplayName.setText(displayName);
        }catch (Exception e){
            Log.e("setDataLobby","displayName :"+e.getMessage());
        }
        gameService.getCurrentGameCodeOfPlayer(displayName).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()){
                    Log.e("getGameCodeLobby","gameCode :"+task.getException().getMessage());
                }else{
                    Log.e("gameCodeFound",task.getResult());
                    gameCode = task.getResult();
                    txtCurrentGame.setText("Code de partie : " + gameCode);
                    subscribeToGame(gameCode);
                    gameService.getQueryQRCodeFromGame(gameCode).addOnCompleteListener(new OnCompleteListener<List<QRCodeInfo>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<QRCodeInfo>> task) {
                            if (task.isSuccessful()){
                                qrCodeInfos.clear();
                                qrCodeInfos.addAll(task.getResult());
                                setQRCodeInformation();
                            }
                        }
                    });

                }
            }
        });
        startTimer();
    }

    private void setQRCodeInformation() {
        txtIndice.setText(qrCodeInfos.get(currentlyLookingFor).getHint());
    }

    private void subscribeToGame(String gameCode) {
        subscriptionToGame = gameService.subscribeToGame(gameCode, new GameService.OnGameChange() {
            @Override
            public void adminStopGame() {
                alertgameStop("La partie est terminer! Retourner au point de départ pour savoir le résultat!");
            }

            @Override
            public void gameLaunch() {

            }
        });
    }

    private void alertQuittingGame() {
        new AlertDialog.Builder(GameActivity.this)
                .setTitle("Déconnexion")
                .setMessage("Ëtes-vous certain de vouloir quitter la partie?")
                .setPositiveButton("OK !", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        gameService.deletePlayerInGame(displayName,gameCode);
                        subscriptionToGame.unsubscribe();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void setListeners() {
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentScanBarCode =  new Intent(GameActivity.this, ScannedBarcodeActivity.class);
                startActivityForResult(intentScanBarCode, TAG_SCAN);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle;
        switch(requestCode) {
            case TAG_SCAN:
                bundle = data.getExtras();
                if (bundle.containsKey("QRCodeID") && bundle.get("QRCodeID") != "") {
                    if (qrCodeInfos.get(currentlyLookingFor).getQrCode().equals(bundle.get("QRCodeID"))){
                        Toast.makeText(GameActivity.this,"Bravo vous avez trouvé le bon code QR!",Toast.LENGTH_SHORT).show();
                        CheckQuestion();
                    }
                    else{
                        Toast.makeText(GameActivity.this,"Ce n'est pas le bon code QR...",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void CheckQuestion() {
        currentCodeScanned = qrCodeInfos.get(currentlyLookingFor);
        if (!currentCodeScanned.getQuestion().equals("")){
            txtAnswer.setVisibility(View.VISIBLE);
            txtQuestion.setVisibility(View.VISIBLE);
            txtQuestion.setText(currentCodeScanned.getQuestion());
            if (!currentCodeScanned.getImageRef().isEmpty()){
                imageView.setVisibility(View.VISIBLE);
                //set image
            }
            if (true){
                Toast.makeText(GameActivity.this,"Bonne réponse!",Toast.LENGTH_SHORT).show();
                nextQRCode();
            }
        }
        else{
            nextQRCode();
        }

    }

    private void nextQRCode() {
        currentlyLookingFor ++;
        if (currentlyLookingFor < qrCodeInfos.size()){
            setQRCodeInformation();
        }
        else{
            Toast.makeText(GameActivity.this,"BRAVO! Votre temps est de " + timeSecondes + " secondes!",Toast.LENGTH_SHORT).show();
            txtIndice.setText("Votre temps final est de "+timeSecondes+" secondes! Retourner au point de départ pour savoir votre position!");
            txtIndice.setTextSize(28);
            btnScan.setVisibility(View.INVISIBLE);
            finished = true;
        }
    }

    private void alertgameStop(String message) {
        new AlertDialog.Builder(GameActivity.this)
                .setTitle("Déconnexion")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK !", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        onBackPressed();
                    }
                }).show();
    }
    private void startTimer(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!finished){
                    timeSecondes ++;
                    txtTime.setText(timeSecondes.toString());
                }
                handler.postDelayed(this,1000);
            }
        },1000);
    }
}