package com.infosec.rhino;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.infosec.rhino.databinding.ActivityPhoneVerifierBinding;

public class PhoneVerifierActivity extends AppCompatActivity {

    ActivityPhoneVerifierBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneVerifierBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneVerifierActivity.this,OTPActivity.class);
                String phoneNum = binding.phoneBox.getText().toString();
                Log.d("Phone Num => ",phoneNum);
                intent.putExtra("phoneNumber",phoneNum);
                startActivity(intent);
            }
        });
    }
}