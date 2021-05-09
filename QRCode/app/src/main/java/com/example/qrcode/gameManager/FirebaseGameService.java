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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
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
    public Task<DocumentSnapshot> getQRCodeFromGame(String gameCode, String qrCodeID) {
        CollectionReference QRCodes = gameDatabase.collection("Games").document(gameCode).collection("QRCodes");
        Continuation<DocumentSnapshot,DocumentSnapshot> QRCodeFromGameContinuation = new Continuation<DocumentSnapshot, DocumentSnapshot>() {
            @Override
            public DocumentSnapshot then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                DocumentSnapshot qrCodeMap = null;
                if (!task.isSuccessful()){
                    Log.e(TAG,"getQRCodeFromGame : " + task.getException().getMessage());
                }
                else{
                    qrCodeMap = task.getResult();
                }
                Log.e(TAG,"getQRCodeFromGame : " +task.getResult().get("indice"));
                return qrCodeMap;
            }
        };
        return QRCodes.document(qrCodeID).get().continueWith(QRCodeFromGameContinuation);
    }


    @Override
    public Task<String> createGame(OnGameCreate onGameCreate) {
        Map<String, Object> game = new HashMap<>();
        game.put("joinable", true);
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
                    if(task.getResult().exists()){
                        if(task.getResult().get("joinable").equals(false)){
                            exists = false;
                        }
                        else{
                            exists = true;
                        }
                    }
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

    @Override
    public Task<Void> deletePlayerInGame(String playerName, String gameCode) {
        CollectionReference Players = gameDatabase.collection("Games").document(gameCode).collection("Players");
        Continuation<Void, Void> deletePlayerContinuation = new Continuation<Void, Void>()
        {
            @Override
            public Void then(@NonNull Task<Void> task) throws Exception {
                if (!task.isSuccessful()){
                    FirebaseFirestoreException.Code errorCode =((FirebaseFirestoreException) task.getException()).getCode();
                    switch (errorCode){
                        case CANCELLED:
                            throw new Exception("L'opération a été arrêtée ");
                        case PERMISSION_DENIED:
                            throw new Exception("Vous ne pouvez pas faire cette opération");
                        case UNAVAILABLE:
                            throw new Exception("Le service est indisponible");
                        default:
                            throw new Exception("Une erreur est survenue");
                    }
                }
                return null;
            }
        };
        return Players.document(playerName).delete().continueWith(deletePlayerContinuation);
    }

    @Override
    public Subscription subscribeToGame(String gameCode, OnGameChange onGameChange) {
        ListenerRegistration subscribe = gameDatabase.collection("Games").document(gameCode).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("TAG", "Listening to game document "+ gameCode +" failed.", e);
                    return;
                }
                if (snapshot != null && !snapshot.exists()) {
                    onGameChange.adminStopGame();
                    Log.e("game state changed : ", "Game was deleted because the admin stopped the game.");
                }
                else{
                    if (snapshot.get("joinable").equals(false)){
                        onGameChange.gameLaunch();
                        Log.e("game state changed : ","game has launched.");
                    }
                }
            }
        });
        return new Subscription() {
            @Override
            public void unsubscribe() {
                subscribe.remove();
            }
        };
    }

    @Override
    public Task<Void> startGame(String gameCode) {
        Continuation<Void,Void> startGameContinuation = new Continuation<Void, Void>() {
            @Override
            public Void then(@NonNull Task<Void> task) throws Exception {
                if (!task.isSuccessful()){
                    Log.e(TAG, "startGame : "+ task.getException().getMessage());
                }
                return null;
            }
        };
        return gameDatabase.collection("Games").document(gameCode).update("joinable",false).continueWith(startGameContinuation);
    }

    @Override
    public Task<List<QRCodeInfo>> getQueryQRCode() {
         CollectionReference QRCodes = gameDatabase.collection("QRCodes");
         Continuation<QuerySnapshot,List<QRCodeInfo>> getQRCodesContinuation = new Continuation<QuerySnapshot, List<QRCodeInfo>>() {
             @Override
             public List<QRCodeInfo> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                 if(!task.isSuccessful()){
                     Log.e(TAG,"getQueryQRCode : "+ task.getException().getMessage());
                 }else{
                     for(int i = 0;i < task.getResult().getDocuments().size();i++){
                         Log.e(TAG,"GetQueryQRCode : document : "+task.getResult().getDocuments().get(i).getId());
                     }
                 }
                 return task.getResult().toObjects(QRCodeInfo.class);
             }
         };
        return QRCodes.get().continueWith(getQRCodesContinuation);
    }

    @Override
    public Task<QRCodeInfo> getQRCode(String QRCodeID) {
        CollectionReference QRCodes = gameDatabase.collection("QRCodes");
        Continuation<DocumentSnapshot,QRCodeInfo> getQRCodeContinuation = new Continuation<DocumentSnapshot,QRCodeInfo>() {
            @Override
            public QRCodeInfo then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                QRCodeInfo taskResult = null;
                if(!task.isSuccessful()){
                    Log.e(TAG,"getQueryQRCode : "+ task.getException().getMessage());
                }else{
                    taskResult = task.getResult().toObject(QRCodeInfo.class);
                   Log.e(TAG,"GetQueryQRCode : document : "+task.getResult());

                }
                return taskResult;
            }
        };
        return QRCodes.document(QRCodeID).get().continueWith(getQRCodeContinuation);
    }

    @Override
    public Task<Void> setQRCode(String QRCodeID, QRCodeInfo QRCodeInfo) {
        CollectionReference QRCodes = gameDatabase.collection("QRCodes");
        Continuation<Void,Void> getQRCodeContinuation = new Continuation<Void,Void>() {
            @Override
            public Void then(@NonNull Task<Void> task) throws Exception {
                if(!task.isSuccessful()){
                    Log.e(TAG,"getQueryQRCode : "+ task.getException().getMessage());
                }else{
                    //taskResult = task.getResult().toObject(QRCodeInfo.class);
                    Log.e(TAG,"GetQueryQRCode : document : "+task.getResult());
                }
                return null;
            }
        };
        return QRCodes.document(QRCodeID).set(QRCodeInfo).continueWith(getQRCodeContinuation);
    }

    @Override
    public Task<Void> deleteQRCode(String QRCodeID) {
        CollectionReference QRCodes = gameDatabase.collection("QRCodes");
        Continuation<Void,Void> getQRCodeContinuation = new Continuation<Void,Void>() {
            @Override
            public Void then(@NonNull Task<Void> task) throws Exception {
                if(!task.isSuccessful()){
                    Log.e(TAG,"getQueryQRCode : "+ task.getException().getMessage());
                }
                return null;
            }
        };
        return QRCodes.document(QRCodeID).delete().continueWith(getQRCodeContinuation);
    }

    private String generateGameCode() {
        Random rand = new Random();
        int randomCode = rand.nextInt(899999) + 100000;
        String gameCode = String.valueOf(randomCode);
        return gameCode;
    }


}
