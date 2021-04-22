package com.example.qrcode.gameManager;

import com.google.android.gms.tasks.Task;

public interface GameService {
    Task<String> getQRCodeReference(String qrCode);
    Task<Void> createGame(OnGameCreate onGameCreate);
    /*
    Task<Void> joinGame(String gameCode, String playerName);
    Task<Void> startGame(String gameCode);
    Task<Void> endGame(String gameCode);
    Task<Boolean> verifyGameExist(String gameCode);
    */

    public interface OnGameCreate{
        void createGame();
    }

}
