package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qrcode.authentication.AuthenticationFactory;
import com.example.qrcode.authentication.AuthenticationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AdminLoginActivity extends AppCompatActivity {
    EditText textEmail;
    EditText textPassword;
    Button buttonConnexion;
    Button buttonRetour;
    AuthenticationService authenticationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        textEmail = findViewById(R.id.editText_login_email);
        textPassword = findViewById(R.id.editText_login_password);
        buttonConnexion = findViewById(R.id.button_connection);
        buttonRetour = findViewById(R.id.btn_adminLogin_retour);
        authenticationService = AuthenticationFactory.getInstance();
        setListeners();
    }

    private void setListeners() {
        buttonConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        buttonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });
    }

    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this,MainActivity.class);
        startActivity(goToMainActivityIntent);
        finish();
    }

    private void attemptLogin() {
        String email = textEmail.getText().toString();
        String password = textPassword.getText().toString();
        Log.e("attemptLogin",email +" "+password);
        authenticationService.login(email,password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    goToAdminActivity();
                }else{
                    showMessage(task.getException().toString());
                }
            }
        });
    }
    private void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    private void goToAdminActivity() {
        Intent goToAdminActivityIntent = new Intent(this,AdminActivity.class);
        startActivity(goToAdminActivityIntent);
        finish();
    }
}

