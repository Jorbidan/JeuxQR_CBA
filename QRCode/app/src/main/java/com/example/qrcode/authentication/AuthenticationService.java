package com.example.qrcode.authentication;

import com.google.android.gms.tasks.Task;

public interface AuthenticationService {
    Task<Void> login(String email, String password);//task au cas ou il y a  un fail
    void logoff(); //pas une task car impossible a fail (just supprimer le token cote client)
    Task<Void> signUp(String email, String password);//task au cas ou il y a un fail
    Boolean isLogIn();
    Task<Void> anonymousLogin();
    String getCurrentUserEmail() throws Exception;
}
