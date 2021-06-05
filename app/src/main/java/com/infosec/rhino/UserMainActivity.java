package com.infosec.rhino;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.infosec.rhino.databinding.ActivityUserMainBinding;

public class UserMainActivity extends AppCompatActivity {
    private ActivityUserMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String phoneee = intent.getStringExtra("phonee");
        String deneme ="Welcome "+phoneee;
        binding.deneme.setText(deneme);
    }
}