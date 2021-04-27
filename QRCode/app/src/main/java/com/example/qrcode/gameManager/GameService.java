package com.example.qrcode.gameManager;

import com.google.android.gms.tasks.Task;

public interface GameService {
    Task<String> getQRCodeReference(String qrCode);
    Task<String> createGame(OnGameCreate onGameCreate);
    Task<Boolean> checkGameExist(String gameCode);
    Task<Void> joinGame(String gameCode, String playerName);
    Task<String> getCurrentGameCodeOfPlayer(String player_email);
    /*
    Task<Void> startGame(String gameCode);
    Task<Void> endGame(String gameCode);
    */

    public interface OnGameCreate{
        void OnCreateGame();
    }

}
