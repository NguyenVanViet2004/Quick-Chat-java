package com.example.pro1121_gr.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityLoginWithOtpBinding;
import com.example.pro1121_gr.util.AndroidUlti;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivityWithOTP extends AppCompatActivity {

    private ActivityLoginWithOtpBinding binding;
    private String phoneNumber;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Long timeoutSeconds = 60L;
    String verificationCode;
    Button btnLoginNextOTP;
    TextView resendOTP;
    PhoneAuthProvider.ForceResendingToken reResendingToken;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginWithOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        phoneNumber = getIntent().getStringExtra("phone");
        binding.titleLoginOTP.setText("VUI LÒNG NHẬP MÃ OTP ĐÃ ĐƯỢC GỬI ĐẾN : " + phoneNumber);

        sendOTP(phoneNumber,false);
        binding.btnLoginNextOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivityWithOTP.this, CreateProfile.class);
                intent.putExtra("phone", phoneNumber);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
            }
        });
//

    }
    void sendOTP( String phoneNumber,boolean isResend){

        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUlti.showToast(getApplicationContext(),"OTP verification failed");
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                reResendingToken = forceResendingToken;
                                AndroidUlti.showToast(getApplicationContext(),"OTP verification successfully!");
                            }
                        });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(reResendingToken).build());
        }else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());

        }
    }
    void signIn(PhoneAuthCredential phoneAuthCredential){

    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            btnLoginNextOTP.setVisibility(View.GONE);
        }else{
            btnLoginNextOTP.setVisibility(View.VISIBLE);
        }
    }


}