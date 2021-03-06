package com.infosec.rhino.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.infosec.rhino.Models.User;
import com.infosec.rhino.Security.Cryptography;
import com.infosec.rhino.databinding.ActivityInitialBinding;

public class InitialActivity extends AppCompatActivity {

    private ActivityInitialBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mDatabaseReference;

    private String mPhone;
    public static final String DATABASE_URL = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInitialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        try {
            Cryptography.loadInstance(getApplicationContext());
            Cryptography.getInstance().updateRSAKeyPair(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
            Cryptography.newInstance(getApplicationContext());
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        try {
            mPhone = firebaseUser.getPhoneNumber();
        }catch (Exception e){
            Intent intent = new Intent(InitialActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        // send user to messaging page if user is enrolled else send to enroll
        mDatabaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference("users/");
        Query checkUser = mDatabaseReference.orderByChild("phoneNumber").equalTo(mPhone);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    DataSnapshot child =  snapshot.getChildren().iterator().next();
                    Log.d("DB", child.toString());
                    // to show app icon
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    User user = child.getValue(User.class);
                    assert user != null;
                    user.setPublicKey(Cryptography.getInstance().getPublicKeyString());
                    mDatabaseReference.child(user.getuid()).setValue(user);

                    Intent intent = new Intent(InitialActivity.this,UserMainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(InitialActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InitialActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
