package com.example.testapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    public final String BACKGROUNDCOLOR = "#0DCCB5";

    private LinearLayout linearLayout;
    private EditText phoneInput;
    private String phonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        linearLayout = (LinearLayout) findViewById(R.id.login_linearLayout);
        linearLayout.setBackgroundColor(Color.parseColor(BACKGROUNDCOLOR));


        phoneInput = (EditText) findViewById(R.id.login_phoneInput);
        phonenumber = getIntent().getStringExtra("phonenumber");

        if(!phonenumber.isEmpty()){
            phoneInput.setText(phonenumber);
        }
        findViewById(R.id.login_submitButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String code = "82";
                String number = phoneInput.getText().toString().trim();

                if(number.isEmpty() || number.length()<10){
                    phoneInput.setError("Valid number is required");
                    phoneInput.requestFocus();
                    return;
                }

                String phoneNumber = "+"+code+number;
                Intent intent = new Intent( LoginActivity.this, VerifyActivity.class);
                intent.putExtra("phonenumber", phoneNumber);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
