package com.example.qrcode.gameManager;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FirebaseGameService implements GameService {
    FirebaseFirestore gameDatabase;

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
    public Task<Void> createGame(OnGameCreate onGameCreate) {
        Map<String, Object> game = new HashMap<>();
        game.put("isStarted", false);
        String gameCode = generateGameCode();

        CollectionReference Games = gameDatabase.collection("Games");
        Continuation<Void,Void> GameCreationContinuation = new Continuation<Void, Void>() {
            @Override
            public Void then(@NonNull Task<Void> task) throws Exception {
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
                return null;
            }
        };
        return Games.document(gameCode).set(game).continueWith(GameCreationContinuation);
    }

    private String generateGameCode() {
        Random rand = new Random();
        int randomCode = rand.nextInt(899999) + 100000;
        String gameCode = String.valueOf(randomCode);
        return gameCode;
    }


}
