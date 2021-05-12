package com.example.qrcode.gameManager;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public interface GameService {
    //Task<QRCodeInfo> getQRCodeReference(String qrCode);
    //Task<DocumentSnapshot> getQRCodeFromGame(String gameCode, String qrCodeID);
    Task<List<QRCodeInfo>> getQueryQRCode();
    Task<List<QRCodeInfo>> getQueryQRCodeFromGame(String gameCode);
    Task<Boolean> CheckQRCodeExist(String QRCodeID);
    Task<Void> setQRCode(QRCodeInfo QRCodeInfo);
    Task<Void> deleteQRCode(String QRCodeID);
    Task<String> createGame(OnGameCreate onGameCreate);
    Task<Boolean> checkGameExist(String gameCode);
    Task<Void> joinGame(String gameCode, String playerName);
    Task<String> getCurrentGameCodeOfPlayer(String playerName);
    Task<Void> endGame(String gameCode);
    Void subscribeToPlayerList(String gameCode, OnPlayerInGameChange onPlayerInGameChange);
    Task<Void> deletePlayerInGame(String playerName, String gameCode);
    Subscription subscribeToGame(String gameCode, OnGameChange onGameChange);
    Task<Void> startGame(String gameCode);
    Task<Void> setQRCodesToGame(String gameCode, List<QRCodeInfo> qrCodesInfo);


    public interface OnPlayerInGameChange{
        void joinGame(String playerName);
        void leaveGame(String playerName);
    }

    public interface OnGameCreate{
        void OnCreateGame();
    }
    public interface OnGameChange{
        void adminStopGame();
        void gameLaunch();
    }
    public interface Subscription{
        void unsubscribe();
    }

}
