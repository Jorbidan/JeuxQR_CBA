package com.example.qrcode.authentication;

import android.util.Log;
import android.view.textclassifier.TextClassification;
import android.view.textclassifier.TextClassifierEvent;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.lang.reflect.Executable;


public class FirebaseAuthenticationService implements AuthenticationService {
    FirebaseAuth firebaseAuth;

    public FirebaseAuthenticationService() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }


    @Override
    public Task<Void> login(String email, String password) {

        Continuation<AuthResult, Void> authResultVoidContinuation =
                new Continuation<AuthResult, Void>() {
                    @Override
                    public Void then(@NonNull Task<AuthResult> task) throws Exception {
                        if (!task.isSuccessful()){
                            String errorCode =((FirebaseAuthException) task.getException()).getErrorCode();
                            switch (errorCode) {
                                case "ERROR_INVALID_CREDENTIAL":
                                    throw new Exception("Les informations ne sont pas exactes");
                                case "ERROR_WRONG_PASSWORD":
                                    throw new Exception("Mauvais mot de passe");
                                default:
                                    throw new Exception("une erreur est survenu lors du login");
                            }
                        }
                        return null;
                    }
                };
        return firebaseAuth.signInWithEmailAndPassword(email,password).continueWith(authResultVoidContinuation);
    }

    @Override
    public void logoff() {
        firebaseAuth.signOut();
    }

    @Override
    public Task<Void> signUp(String email, String password) {
        Continuation<AuthResult, Void> authResultVoidContinuation = new Continuation<AuthResult, Void>()
        {
            @Override
            public Void then(@NonNull Task<AuthResult> task) throws Exception {
                if (!task.isSuccessful()){
                    String errorCode =((FirebaseAuthException) task.getException()).getErrorCode();
                    switch (errorCode){
                        case "ERROR_EMAIL_ALREADY_IN_USE":
                            throw new Exception("Cette email est deja utilisé");
                        case "ERROR_WEAK_PASSWORD":
                            throw new Exception("Ce mot de passe est trop faible");
                        case "ERROR_INVALID_EMAIL":
                            throw new Exception("Cet email n'est pas valide");
                        default:
                            throw new Exception("Une erreur est survenue");
                    }
                }
                return null;
            }
        };
        return firebaseAuth.createUserWithEmailAndPassword(email,password).continueWith(authResultVoidContinuation);
    }

    @Override
    public Boolean isLogIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    @Override
    public String getCurrentUserEmail() throws Exception {
        if (!isLogIn()){
            throw new Exception("Usager non connecté");
        }
        return firebaseAuth.getCurrentUser().getEmail();
    }
}