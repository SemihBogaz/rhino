package com.infosec.rhino.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.infosec.rhino.databinding.ActivityMainBinding;

import java.util.concurrent.TimeUnit;

import com.infosec.rhino.databinding.ActivityProfileBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String mVerificationId;

    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.phoneLl.setVisibility(View.VISIBLE);
        binding.codeLl.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(this);
        pd.setTitle("Please wait ...");
        pd.setCanceledOnTouchOutside(false);


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();
                Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, forceResendingToken);

                mVerificationId = verificationId;
                forceResendingToken = token;
                pd.dismiss();

                binding.phoneLl.setVisibility(View.GONE);
                binding.codeLl.setVisibility(View.VISIBLE);

                Toast.makeText(MainActivity.this, "Verification code is sent ", Toast.LENGTH_LONG).show();
                String phoneNumber = binding.phoneEt.getText().toString().trim();
                binding.codeSentDescription.setText("Please enter the verification code we sent \nto " + phoneNumber);
            }
        };

        binding.phoneContinueBtn.setOnClickListener(v -> {
            String phone = binding.phoneEt.getText().toString().trim();
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(MainActivity.this, "Please enter phone number ...", Toast.LENGTH_LONG).show();
            } else {
                startPhoneNumberVerification(phone);
            }
        });

        binding.resentCodeTv.setOnClickListener(v -> {
            binding.codeEt.setText("");
            String phone = binding.phoneEt.getText().toString();
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(MainActivity.this, "Please enter phone number ...", Toast.LENGTH_LONG).show();
            } else {
                resendVerificationCode(phone, forceResendingToken);
            }
        });

        binding.codeSubmitBtn.setOnClickListener(v -> {
            String code = binding.codeEt.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(MainActivity.this, "Please enter verification code ...", Toast.LENGTH_LONG).show();
            } else {
                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });
    }

    private void startPhoneNumberVerification(String phone) {
        pd.setMessage("Verifying your phone number ...");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phone, PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Resending code");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        pd.setMessage("Verifying code");
        pd.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredentials(credential);
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential credential) {
        pd.setMessage("Logging In");

        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(authResult -> {
            pd.dismiss();
            String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
            Toast.makeText(MainActivity.this, "Logged as " + phone, Toast.LENGTH_LONG).show();

            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}