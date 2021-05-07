package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.qrcode.authentication.AuthenticationFactory;
import com.example.qrcode.authentication.AuthenticationService;
import com.example.qrcode.gameManager.GameFactory;
import com.example.qrcode.gameManager.GameService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends AppCompatActivity {
    TextView textCurrentGame;
    TextView textDisplayName;
    String displayName;
    String gameCode;
    GameService gameService;
    GameService.Subscription subscriptionToGame = null;
    AuthenticationService authenticationService;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<String> playersInLobby = new ArrayList<>();
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
        recyclerView = findViewById(R.id.recyclerView_lobby_playerRecycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new PlayerRecyclerAdapter(playersInLobby);
        recyclerView.setAdapter(recyclerViewAdapter);
        setData();
        setListeners();
    }

    private void setListeners() {
    }

    private void setData() {
        try {
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences("playerName",MODE_PRIVATE);
            displayName = sharedPreferences.getString("playerName","Aucun nom trouvé");
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
                    showPlayersInLobby();
                    subscribeToGame();
                }
            }
        });
    }
    private void showPlayersInLobby(){
        gameService.subscribeToPlayerList(gameCode, new GameService.OnPlayerInGameChange() {
            @Override
            public void joinGame(String player) {
                playersInLobby.add(player);
                updateRecycler();
            }

            @Override
            public void leaveGame(String player) {
                playersInLobby.remove(player);
                updateRecycler();
            }
        });
    }

    private void updateRecycler() {
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void subscribeToGame(){
        subscriptionToGame = gameService.subscribeToGame(gameCode, new GameService.OnGameChange() {
            @Override
            public void adminStopGame() {
                alertgameStop("La partie est terminer! Retourner au point de départ pour savoir le résultat!");
            }

            @Override
            public void gameLaunch() {

            }

            @Override
            public void finishRound() {

            }

            @Override
            public void nextRound() {

            }
        });
    }

    private void alertgameStop(String message) {
        new AlertDialog.Builder(LobbyActivity.this)
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

    @Override
    public void onBackPressed() {
        gameService.deletePlayerInGame(displayName,gameCode);
        finish();
    }
}