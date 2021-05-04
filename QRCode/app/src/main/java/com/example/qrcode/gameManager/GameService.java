package com.example.qrcode.gameManager;

import com.google.android.gms.tasks.Task;

public interface GameService {
    Task<String> getQRCodeReference(String qrCode);
    Task<String> createGame(OnGameCreate onGameCreate);
    Task<Boolean> checkGameExist(String gameCode);
    Task<Void> joinGame(String gameCode, String playerName);
    Task<String> getCurrentGameCodeOfPlayer(String playerName);
    Task<Void> endGame(String gameCode);
    Void subscribeToPlayerList(String gameCode, OnPlayerInGameChange onPlayerInGameChange);
    /*
    Task<Void> startGame(String gameCode);
    Task<Void> endGame(String gameCode);
    */
    public interface OnPlayerInGameChange{
        void joinGame(String playerName);
        void leaveGame(String playerName);
    }

    public interface OnGameCreate{
        void OnCreateGame();
    }

}
