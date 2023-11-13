package com.example.pro1121_gr.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityLoginWithOtpBinding;
import com.example.pro1121_gr.util.AndroidUlti;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class LoginActivityWithOTP extends AppCompatActivity {

    private ActivityLoginWithOtpBinding binding;
    private String phoneNumber;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken reResendingToken;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @SuppressLint({"SetTextI18n", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginWithOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Bật chế độ tối nếu được kích hoạt
        MyApplication.applyNightMode();


        EditText otpInput = findViewById(R.id.edt_loginOTP);
//        Button btnLoginNextOTP = findViewById(R.id.btn_loginNextOTP);
//        TextView resendOTP = findViewById(R.id.resendOTP);

        phoneNumber = getIntent().getStringExtra("phone");
        binding.titleLoginOTP.setText("VUI LÒNG NHẬP MÃ OTP ĐÃ ĐƯỢC GỬI ĐẾN : " + phoneNumber);

        FirebaseFirestore.getInstance().collection("phoneNumber");
        Toast.makeText(getApplicationContext(), phoneNumber, Toast.LENGTH_SHORT).show();


        sendOTP(phoneNumber,false);
        binding.btnLoginNextOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivityWithOTP.this, CreateProfile.class);
//                intent.putExtra("phone", phoneNumber);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//
                 String enteredOTP = otpInput.getText().toString();
                 PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationCode,enteredOTP);
                 signIn(credential);
                 setInProgress(true);

//                startActivity(intent);
//                finish();
            }
        });

        binding.resendOTP.setOnClickListener((v) ->{
            sendOTP(phoneNumber,true);
        });
    }
    void sendOTP( String phoneNumber,boolean isResend){

        startResendTimer();

        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toasty.error(LoginActivityWithOTP.this, "OTP verification failed!", Toast.LENGTH_SHORT, true).show();
                                setInProgress(false);

                            }


                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                reResendingToken = forceResendingToken;
                                Toasty.success(LoginActivityWithOTP.this, "OTP verification successfully!", Toast.LENGTH_SHORT, true).show();

                                setInProgress(false);

                            }
                        });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(reResendingToken).build());
        }else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());

        }
    }
    void signIn(PhoneAuthCredential phoneAuthCredential){
//        //dang nhap va chuyen sang man hinh tiep theo
//
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivityWithOTP.this,CreateProfile.class);
                    intent.putExtra("phone",phoneNumber);
                    startActivity(intent);
                }else {
                    Toasty.error(LoginActivityWithOTP.this, "OTP verification failed!", Toast.LENGTH_SHORT, true).show();

                }
            }
        });
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            binding.btnLoginNextOTP.setVisibility(View.GONE);
        }else{
            binding.btnLoginNextOTP.setVisibility(View.VISIBLE);
        }
    }


    //thời gian đếm ngược mã OTP
    void startResendTimer(){
        binding.resendOTP.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                runOnUiThread(() -> {
                    binding.resendOTP.setText("Gửi lại mã OTP trong: " + timeoutSeconds + "s");
                });
                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        binding.resendOTP.setEnabled(true);
                        binding.resendOTP.setText("Gửi lại mã OTP");
                    });
                }
            }
        }, 0, 1000);
    }


}