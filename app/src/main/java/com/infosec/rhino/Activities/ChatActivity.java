package com.infosec.rhino.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infosec.rhino.Adapters.MessagesAdapter;
import com.infosec.rhino.Models.Message;
import com.infosec.rhino.Security.Cryptography;
import com.infosec.rhino.databinding.ActivityChatBinding;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    private static final String DATABASE_URL = "https://rhino-fa5bd-default-rtdb.europe-west1.firebasedatabase.app/";
    private static final String MESSAGE_HISTORY_LOCATION = "messagehistory.txt";

    private ActivityChatBinding binding;
    private MessagesAdapter adapter;
    private ArrayList<Message> messages;
    private HashMap<String, String> messageHistory;

    private String senderRoom, receiverRoom;

    private FirebaseDatabase database;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages = new ArrayList<>();
        try {
            messageHistory = getMessageHistory(getApplicationContext());
        } catch (Exception e) {
            messageHistory = new HashMap<>();
        }
        adapter = new MessagesAdapter(this, messages);
        binding.chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecycler.setAdapter(adapter);

        String name = getIntent().getStringExtra("name");
        String receiverUid = getIntent().getStringExtra("uid");
        String receiverPublicKey = getIntent().getStringExtra("publicKey");
        String senderUid = FirebaseAuth.getInstance().getUid();

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        database = FirebaseDatabase.getInstance(DATABASE_URL);

        database.getReference("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            Message message = snapshot1.getValue(Message.class);
                            assert message != null;
                            if (!message.getSenderId().equals(senderUid)) {
                                if (message.isEncrypted()) {
                                    Cryptography.getInstance().decryptMessage(message);
                                }
                            } else {
                                if (message.isEncrypted()) {
                                    message.setText(messageHistory.get(message.getText()));
                                }
                            }
                            messages.add(message);
                        }
                        try {
                            saveMessageHistory(messageHistory, getApplicationContext());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

        binding.sendBtn.setOnClickListener(v -> {
            String messageText = binding.messageBox.getText().toString();

            Date date = new Date();
            Message message = new Message(messageText,senderUid, date.getTime());
            Cryptography.getInstance().encryptMessage(message, receiverPublicKey);
            messageHistory.put(message.getText(), messageText);
            try {
                Cryptography.getInstance().updateAESKey(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            binding.messageBox.setText("");

            database.getReference("chats")
                    .child(senderRoom)
                    .child("messages")
                    .push()
                    .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    database.getReference().child("chats")
                            .child(receiverRoom)
                            .child("messages")
                            .push()
                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    });
                }
            });
        });

        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // to display title
    }

    // for back btn on supportActionBar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private static void saveMessageHistory(HashMap<String, String> messageHistory, Context context) throws IOException {
        FileOutputStream outputStream = context.openFileOutput(MESSAGE_HISTORY_LOCATION, Context.MODE_PRIVATE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(messageHistory);
        objectOutputStream.close();
    }

    private HashMap<String, String> getMessageHistory(Context context) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = context.openFileInput(MESSAGE_HISTORY_LOCATION);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        @SuppressWarnings("unchecked") HashMap<String, String> result = (HashMap<String, String>) objectInputStream.readObject();
        objectInputStream.close();
        return result;
    }
}