package com.example.qrcode.gameManager;

public class GameFactory {
    public static GameService getInstance(){
        return new FirebaseGameService();
    }
}
