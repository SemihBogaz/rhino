package com.infosec.rhino.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infosec.rhino.Models.User;
import com.infosec.rhino.databinding.ActivityProfileBinding;


public class ProfileActivity extends AppCompatActivity {

    // gave db url as below because settings are different in the firefise console.
    // can be removed via deleting project and creating again
    public static final String DATABASE_URL = "https://rhino-fa5bd-default-rtdb.europe-west1.firebasedatabase.app/";
    private ActivityProfileBinding binding;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference("users");
        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        binding.saveBtn.setOnClickListener(v ->  {
            String phoneNumber = binding.userPhoneNumber.getText().toString();
            String userName = binding.userName.getText().toString();
            User user = new User(phoneNumber, userName);
            Log.d("VERİLER => ", mUid+" "+user.getName()+" "+user.getPhoneNumber()); // TODO sil bunu
            mDatabaseReference.child(mUid).setValue(user);
            Toast.makeText(ProfileActivity.this,"User is saved",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ProfileActivity.this, UserMainActivity.class);
            intent.putExtra("phonee",phoneNumber);
            startActivity(intent);
            finish();
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
            mUid = firebaseUser.getUid();
            binding.userPhoneNumber.setText(phone);
        }
        else{
            finish();
        }
    }
}