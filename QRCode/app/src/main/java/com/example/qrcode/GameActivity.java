package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.qrcode.gameManager.GameFactory;
import com.example.qrcode.gameManager.GameService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class GameActivity extends AppCompatActivity {
    Button btnScan;
    TextView txtIndice, txtInfo,txtDisplayName, txtCurrentGame;
    GameService gameService;
    String displayName,gameCode;
    GameService.Subscription subscriptionToGame = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameService = GameFactory.getInstance();
        btnScan = findViewById(R.id.btn_game_scan);
        txtIndice = findViewById(R.id.textView_game_indice);
        txtCurrentGame = findViewById(R.id.textView_game_gameCode);
        txtDisplayName = findViewById(R.id.textView_game_displayName);
        txtInfo = findViewById(R.id.textView_game_info);
        setData();
        setListeners();
    }

    @Override
    public void onBackPressed() {
        alertQuittingGame();
    }

    private void setData() {
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
                    gameService.getQRCodeFromGame(gameCode,"1").addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            txtIndice.setText(task.getResult().get("indice").toString());
                        }
                    });
                }
            }
        });
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
                // start scan
            }
        });
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

}