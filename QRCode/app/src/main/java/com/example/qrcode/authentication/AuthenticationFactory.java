package com.example.qrcode.authentication;

public class AuthenticationFactory {
    public static AuthenticationService getInstance(){
        return new FirebaseAuthenticationService();
    }
}
