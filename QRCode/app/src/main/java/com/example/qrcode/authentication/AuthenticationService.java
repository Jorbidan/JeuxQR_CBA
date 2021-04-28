package com.example.qrcode.authentication;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.auth.User;

public interface AuthenticationService {
    Task<Void> login(String email, String password);//task au cas ou il y a  un fail
    void logoff(); //pas une task car impossible a fail (just supprimer le token cote client)
    Task<Void> signUp(String email, String password);//task au cas ou il y a un fail
    Boolean isLogIn();
    /*Task<Void> anonymousLogin();
    Task<Void> setUserDisplayName(UserProfileChangeRequest profile);*/
    //enlever car incappable de creer autant d'utilisateur anonyme qu'on veut sur la meme addresse ip. aulieu creer un compte pour chaque utilisateur et le supprime a la fin de la partie.
    String getCurrentUserDisplayName() throws Exception;
}
