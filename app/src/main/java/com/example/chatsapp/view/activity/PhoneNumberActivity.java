package com.example.chatsapp.view.activity;//package com.example.chatsapp.view.activity;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Toast;
//
//import com.example.chatsapp.databinding.ActivityPhoneNumberBinding;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseException;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.PhoneAuthCredential;
//import com.google.firebase.auth.PhoneAuthOptions;
//import com.google.firebase.auth.PhoneAuthProvider;
//
//import java.util.concurrent.TimeUnit;
//
//
//public class PhoneNumberActivity extends AppCompatActivity {
//
//    ActivityPhoneNumberBinding binding;
//    FirebaseAuth auth;
//    private boolean otpSent = false;
//    private String id;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        FirebaseApp.initializeApp(this);
//
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//
//
//        binding.editTextPhoneNumber.requestFocus();
//
//
//        binding.verifyBtn.setOnClickListener(b->{
//
//
//            if(otpSent){
//                final String getOtp = binding.OTPEditText.getText().toString().trim();
//
//            if(id.isEmpty()){
//                Toast.makeText(this, "unable to verify otp", Toast.LENGTH_SHORT).show();
//            }
//            else{
//
//                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id,getOtp);
//                firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()){
//                            Toast.makeText(PhoneNumberActivity.this, "Verified", Toast.LENGTH_SHORT).show();
//                        }else{
//                            Toast.makeText(PhoneNumberActivity.this, "someThing went wrong!!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//            }
//
//            } else{
//                final String getNumber = binding.editTextPhoneNumber.getText().toString().trim();
//
//                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
//                        .setPhoneNumber("+212"+""+getNumber)
//                        .setTimeout(60l, TimeUnit.SECONDS)
//                        .setActivity(this)
//                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                            @Override
//                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                                Toast.makeText(PhoneNumberActivity.this, "otp sent successfully", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onVerificationFailed(@NonNull FirebaseException e) {
//                                Toast.makeText(PhoneNumberActivity.this, "Something went wrong "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                super.onCodeSent(s, forceResendingToken);
//                                binding.OTPEditText.setVisibility(View.VISIBLE);
//                                binding.verifyBtn.setText("continue");
//                                id = s;
//                                otpSent = true;
//                            }
//                        }).build();
//
//                PhoneAuthProvider.verifyPhoneNumber(options);
//            }
//
//
//        });
//
//
//    }
//}























import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chatsapp.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;


public class PhoneNumberActivity extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        getSupportActionBar().hide();

        // check if the user already sign in or not to go directly MainActivity;
        if(auth.getCurrentUser() != null){
            Intent intent = new Intent(PhoneNumberActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

//        getSupportActionBar().hide();

        binding.editTextPhoneNumber.requestFocus();

        binding.verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneNumberActivity.this,OTPActivity.class);
                intent.putExtra("phoneNumber", binding.editTextPhoneNumber.getText().toString());
                startActivity(intent);
            }
        });

    }
}




