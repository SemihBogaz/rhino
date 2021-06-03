package com.infosec.rhino;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infosec.rhino.databinding.ActivityProfileBinding;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("test").setValue("Hello World");
        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        super.onResume();
        binding.saveBtn.setOnClickListener(v -> {
            User user = new User(binding.userPhoneNumber.getText().toString(), binding.userName.getText().toString());
            saveUser(user);
        });

        binding.logOutBtn.setOnClickListener(v -> {
            firebaseAuth.signOut();
            checkUserStatus();
        });
    }


    private void checkUserStatus() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            String phone = firebaseUser.getPhoneNumber();
            //User user = getUser(phone);
            binding.userPhoneNumber.setText(phone);
           //binding.userName.setText(user.getName());
        }
        else{
            finish();
        }
    }

    private void saveUser(User user) {
        mDatabase.child("users").child(user.getPhoneNumber()).setValue(user.getName());
    }

    private User getUser(String phone) {
        User result = new User(phone);
        Task<DataSnapshot> snapshot = mDatabase.child("users").child(phone).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String name = String.valueOf(Objects.requireNonNull(task.getResult()).getValue());
                result.setName(name);
            }
        });
        return  result;
    }
}