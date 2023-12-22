package com.example.chatsapp.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatsapp.R;
import com.example.chatsapp.databinding.ConversationsItemsBinding;
import com.example.chatsapp.view.activity.Messages_activity;
import com.example.chatsapp.view.models.Message;
import com.example.chatsapp.view.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>{


    Context context;
    List<User> users;
    FirebaseDatabase database;
    ArrayList<Message> messages;

   public ConversationAdapter(){

   }

   public ConversationAdapter(List<User> users, Context context){
       this.context = context;
       this.users   = users;
   }


    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(LayoutInflater.from(context).inflate(R.layout.conversations_items,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {

       User user = users.get(position);
       String senderRoom = FirebaseAuth.getInstance().getUid() + user.getUid();


//        FirebaseDatabase.getInstance().getReference().child("Chats")
//                .child(senderRoom)
//                .child("messages")
//                .orderByChild("timestamp") // Assuming "timestamp" is the field representing the message timestamp
//                .limitToLast(1) // Get the last message
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
//                                Message lastMessage = messageSnapshot.getValue(Message.class);
//                                if (lastMessage != null) {
//                                    String messageText = lastMessage.getMessage();
//                                    long timestamp = lastMessage.getTimestamp();
//                                    Date date = new Date(timestamp);
//
//                                    // Create a SimpleDateFormat object with the desired time format
//                                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
//
//                                    // Format the date to a string
//                                    String formattedTime = sdf.format(date);
//
//                                    // Use the retrieved message and timestamp as needed
//                                    Log.d("LastMessage", messageText);
//                                    Log.d("Timestamp", String.valueOf(timestamp));
//
//                                    // Update your UI or do other actions with the last message
//                                    holder.binding.lastMessage.setText(messageText);
//                                    holder.binding.timeOline.setText(formattedTime);
//                                }
//                            }
//                        } else {
//                            holder.binding.lastMessage.setText("Tap to chat.");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Handle error
//                    }
//                });



        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            long time = snapshot.child("lastMsgTime").getValue(Long.class);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                            holder.binding.timeOline.setText(dateFormat.format(new Date(time)));
                            holder.binding.lastMessage.setText(lastMsg);
                        } else {
                            holder.binding.lastMessage.setText("Tap to chat");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });






        holder.binding.nameOfUser.setText(user.getName());

           if (!"No Image".equals(user.getProfileImage())) {
               Glide.with(context).load(user.getProfileImage())
                       .into(holder.binding.imgProfile);
           }

           holder.itemView.setOnClickListener(i->{
               Intent intent = new Intent(context, Messages_activity.class);
               intent.putExtra("name",user.getName());
               intent.putExtra("uid",user.getUid());
               context.startActivity(intent);
           });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder {

        ConversationsItemsBinding binding;
        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ConversationsItemsBinding.bind(itemView);
        }
    }
}
