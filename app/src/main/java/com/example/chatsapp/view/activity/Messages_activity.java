package com.example.chatsapp.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.example.chatsapp.R;
import com.example.chatsapp.databinding.ActivityMessagesBinding;
import com.example.chatsapp.view.adapters.MessagesAdapter;
import com.example.chatsapp.view.models.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class Messages_activity extends AppCompatActivity {

    ActivityMessagesBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;
    String senderUid,senderRoom,receiveRoom;
    FirebaseDatabase database;
    LinearLayoutManager layoutManager;
    public static String lastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages = new ArrayList<>();

        String name = getIntent().getStringExtra("name");
        String receiveUid  = getIntent().getStringExtra("uid");
         senderUid  = FirebaseAuth.getInstance().getUid();

         senderRoom = senderUid + receiveUid;
         receiveRoom = receiveUid + senderUid;

         database = FirebaseDatabase.getInstance();
        // Assuming you have a LinearLayoutManager set up for your RecyclerView
         layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewMessages.setLayoutManager(layoutManager);

// Set the adapter for your RecyclerView
        adapter = new MessagesAdapter(messages, this,senderRoom,receiveRoom);
        binding.recyclerViewMessages.setAdapter(adapter);


        // Add an OnGlobalLayoutListener to detect layout changes
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Scroll to the bottom when the layout changes (e.g., keyboard opens)
                layoutManager.scrollToPosition(messages.size() - 1);

                // Remove the listener to avoid multiple calls
                binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

         database.getReference().child("Chats")
                         .child(senderRoom)
                                 .child("messages")
                                         .addValueEventListener(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                 messages.clear();
                                                 for(DataSnapshot snapshot1:snapshot.getChildren()){
                                                     Message message = snapshot1.getValue(Message.class);
                                                     message.setMessageId(snapshot1.getKey());
                                                     messages.add(message);
                                                 }


                                                 // Notify the adapter about the data change for the last item
                                                 adapter.notifyDataSetChanged();
                                                 // Scroll to the bottom
                                                 layoutManager.scrollToPosition(messages.size()-1);
                                             }

                                             @Override
                                             public void onCancelled(@NonNull DatabaseError error) {

                                             }
                                         });
         // using randomKey than push because i want to put an id in both node(sender & receiver);


//        String randomKey = database.getReference().child("Chats").child(senderRoom).child("messages").push().getKey();

        binding.btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.textFieldMessage.getText().toString();
                Date date = new Date();
// Scroll to the bottom
                layoutManager.scrollToPosition(messages.size()-1);
                // Generate a new unique key for the message
                String messageKey = database.getReference().child("Chats").child(senderRoom).child("messages").push().getKey();

                Message message = new Message(text, senderUid, date.getTime());

                Log.d("uid", messageKey);
                // Scroll to the bottom
                layoutManager.scrollToPosition(messages.size() - 1);
                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMsg", message.getMessage());
                lastMsgObj.put("lastMsgTime", date.getTime());

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiveRoom).updateChildren(lastMsgObj);

                // Use the generated key to set the message in the sender's room
                database.getReference().child("Chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(messageKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                binding.textFieldMessage.setText("");

                                // Use the same key to set the message in the receiver's room
                                database.getReference().child("Chats")
                                        .child(receiveRoom)
                                        .child("messages")
                                        .child(messageKey)
                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // If needed, you can perform additional actions after the message is sent
                                            }
                                        });
                                HashMap<String, Object> lastMsgObj = new HashMap<>();
                                lastMsgObj.put("lastMsg",message);
                                lastMsgObj.put("lastMsgTime", date.getTime());

                                database.getReference().child("Chats").child(senderRoom).updateChildren(lastMsgObj);
                                database.getReference().child("Chats").child(receiveRoom).updateChildren(lastMsgObj);
                            }
                        });


            }
        });


        getSupportActionBar().setTitle(name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }




}