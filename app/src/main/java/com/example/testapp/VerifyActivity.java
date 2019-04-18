package com.example.testapp;

import android.app.AlertDialog;
import android.arch.core.executor.TaskExecutor;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {


    private String phonenumber;
    private String verificationId;
    private FirebaseAuth mAuth;
    private TextView verify_timer;
    private EditText authCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        mAuth = FirebaseAuth.getInstance();

        authCode = findViewById(R.id.verify_authcode);
        verify_timer = findViewById(R.id.verify_timer);
        phonenumber = getIntent().getStringExtra("phonenumber");

        sendVerificationCode(phonenumber);

        findViewById(R.id.verify_submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = authCode.getText().toString().trim();

                if(code.isEmpty() || code.length() < 6){
                    authCode.setError("invalid authCode");
                    authCode.requestFocus();
                    return;
                }

                verifyCode(code);
            }
        });
    }

    //인증번호 전송 및 제한시간 타이머 설정
    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                verify_timer.setText(String.format(Locale.getDefault(), "%d sec.", millisUntilFinished / 1000L));
            }
            public void onFinish() {
                new AlertDialog.Builder(VerifyActivity.this)
                        .setTitle("제한시간 초과")
                        .setMessage("제한 시간이 초과되었습니다. 다시 시도하세요")
                        .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int sumthin) {
                                Intent intent = new Intent(VerifyActivity.this, LoginActivity.class);
                                intent.putExtra("phonenumber", phonenumber);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(intent);
                            }
                        }) .show(); // 팝업창 보여줌
            }
        }.start();

        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(VerifyActivity.this, RealMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);
                        }else{
                            Toast.makeText(VerifyActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number, 120, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallback
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationId = s;

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code != null){
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(VerifyActivity.this)
                    .setTitle("Exception!")
                    .setMessage(e.getMessage()+"\n"+e.getStackTrace().toString())
                    .setNeutralButton("닫기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dlg, int sumthin) {
                        }
                    }) .show(); // 팝업창 보여줌

        }
    };
}
