package com.example.qrcode.gameManager;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.SharedPreferences;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class FirebaseGameService implements GameService {
    FirebaseFirestore gameDatabase;
    final String TAG = "GameService";

    public FirebaseGameService(){
        this.gameDatabase = FirebaseFirestore.getInstance();
    }

    @Override
    public Task<String> getQRCodeReference(String qrCode) {
        CollectionReference QRCodes = gameDatabase.collection("QRCodes");
        Continuation<DocumentSnapshot, String> QRCodeReferenceContinuation = new Continuation<DocumentSnapshot, String>() {
            @Override
            public String then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                String qrCodeReturn = "";
                if (task.isSuccessful()){
                    qrCodeReturn = task.getResult().get("indice").toString();
                }
                return qrCodeReturn;
            }
        };
        return QRCodes.document(qrCode).get().continueWith(QRCodeReferenceContinuation);
    }

    @Override
    public Task<String> createGame(OnGameCreate onGameCreate) {
        Map<String, Object> game = new HashMap<>();
        game.put("isStarted", false);
        String gameCode = generateGameCode();

        CollectionReference Games = gameDatabase.collection("Games");
        Continuation<DocumentSnapshot,String> GameCreationContinuation = new Continuation<DocumentSnapshot, String>() {
            @Override
            public String then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    FirebaseFirestoreException.Code errorCode = ((FirebaseFirestoreException) task.getException()).getCode();
                    switch (errorCode){
                        case CANCELLED:
                            throw new Exception("La création de partie à été annulée.");
                        case PERMISSION_DENIED:
                            throw new Exception("Vous n'avez pas les permissions requise pour créer une partie.");
                        case UNAVAILABLE:
                            throw new Exception("Le service de base de données FirebaseFirestore est indisponible.");
                        default:
                            throw new Exception("Une erreur est survenue, veuillez contacter un administrateur.");
                    }
                }
                return gameCode;
            }
        };
        Games.document(gameCode).set(game);
        return Games.document(gameCode).get().continueWith(GameCreationContinuation);
    }

    @Override
    public Task<Boolean> checkGameExist(String gameCode) {
        Continuation<DocumentSnapshot, Boolean> resultContinuation = new Continuation<DocumentSnapshot, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                boolean exists = false;
                if (task.isSuccessful()){
                    exists = task.getResult().exists();
                }
                return exists;
            }
        };
        return gameDatabase.collection("Games").document(gameCode).get().continueWith(resultContinuation);
    }

    @Override
    public Task<Void> joinGame(String gameCode, String playerName) {
       Map<String,Object> playerInGame = new HashMap<>();
       playerInGame.put("currentQR",1);
       playerInGame.put("tempsFinal",0);

       CollectionReference Players = gameDatabase.collection("Games").document(gameCode).collection("Players");
       Continuation<Void,Void> PlayerJoinGameContinuation = new Continuation<Void, Void>() {
           @Override
           public Void then(@NonNull Task<Void> task) throws Exception {
               if(!task.isSuccessful()){
                   FirebaseFirestoreException.Code errorCode =((FirebaseFirestoreException) task.getException()).getCode();
                   String tagJoinGame = "JOINGAME continuation ";
                   switch (errorCode){
                       case CANCELLED:
                           throw new Exception(tagJoinGame +"L'opération a été arrêtée ");
                       case PERMISSION_DENIED:
                           throw new Exception(tagJoinGame +"Vous ne pouvez pas faire cette opération");
                       case UNAVAILABLE:
                           throw new Exception(tagJoinGame +"Le service est indisponible");
                       default:
                           throw new Exception(tagJoinGame +"Une erreur exeptionnel est survenue");
                   }
               }
               return null;
           }
       };
        Map<String,Object> currentGame = new HashMap<>();
        currentGame.put("Game",gameCode);
        gameDatabase.collection("Players").document(playerName).set(currentGame);
       return Players.document(playerName).set(playerInGame);
    }

    @Override
    public Task<String> getCurrentGameCodeOfPlayer(String playerName) {
        Continuation<DocumentSnapshot, String> resultContinuation = new Continuation<DocumentSnapshot, String>() {
            @Override
            public String then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                String gameCode = "";
                if (task.isSuccessful()){
                    gameCode = task.getResult().get("Game").toString();
                }
                Log.e(TAG,"getGameCode :" + gameCode);
                return gameCode;
            }
        };
        return gameDatabase.collection("Players").document(playerName).get().continueWith(resultContinuation);
    }

    @Override
    public Task<Void> endGame(String gameCode) {
        Continuation<Void,Void> endGameContinuation = new Continuation<Void, Void>() {
            @Override
            public Void then(@NonNull Task<Void> task) throws Exception {
                if(!task.isSuccessful()){
                    Log.e(TAG,task.getException().getMessage());
                }
                return null;
            }
        };
        return gameDatabase.collection("Games").document(gameCode).delete().continueWith(endGameContinuation);
    }

    @Override
    public Void subscribeToPlayerList(String gameCode, OnPlayerInGameChange onPlayerInGameChange) {
        gameDatabase.collection("Games").document(gameCode).collection("Players").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            onPlayerInGameChange.joinGame(dc.getDocument().getId());
                            Log.e("PlayerList changed", "Added : " + dc.getDocument().getId());
                            break;
                        case REMOVED:
                            onPlayerInGameChange.leaveGame(dc.getDocument().getId());
                            Log.e("PlayerList changed", "Removed : " + dc.getDocument().getId());
                            break;
                    }
                }
            }
        });
        return null;
    }


    private String generateGameCode() {
        Random rand = new Random();
        int randomCode = rand.nextInt(899999) + 100000;
        String gameCode = String.valueOf(randomCode);
        return gameCode;
    }


}
