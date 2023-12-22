package com.example.chatsapp.view.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatsapp.R;
import com.example.chatsapp.databinding.ItemReceiveBinding;
import com.example.chatsapp.databinding.ItemSentBinding;
import com.example.chatsapp.view.models.Message;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messages;
    String senderRoom,receiverRoom;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;

    public MessagesAdapter (ArrayList<Message> messages, Context context,String senderRoom,String receiverRoom){
        this.messages = messages;
        this.context  = context;
        this.senderRoom = senderRoom;
        this.receiverRoom= receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent,parent,false);
            return new SentViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive,parent,false);
            return new ReceiveViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return ITEM_SENT;
        }else{
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message = messages.get(position);
        int reactions[] = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (positionn) -> {
            if (positionn >= 0 && positionn < reactions.length) {
                if (holder.getClass() == SentViewHolder.class) {
                    SentViewHolder viewHolder = (SentViewHolder) holder;
                    viewHolder.binding.iconType.setImageResource(reactions[positionn]);
                    viewHolder.binding.iconType.setVisibility(View.VISIBLE);
                } else {
                    ReceiveViewHolder viewHolder = (ReceiveViewHolder) holder;
                    viewHolder.binding.iconType.setImageResource(reactions[positionn]);
                    viewHolder.binding.iconType.setVisibility(View.VISIBLE);
                }

            }

            message.setFeeling(positionn);

            // change the target message in both seeds(in senderRoom & receiverRoom);
            FirebaseDatabase.getInstance().getReference()
                    .child("Chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            FirebaseDatabase.getInstance().getReference()
                    .child("Chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);


            return true; // true is closing popup, false is requesting a new selection
        });



//        ReactionPopup popup = new ReactionPopup(context, config, (positionn) -> {
//            if(holder.getClass() == SentViewHolder.class){
//                SentViewHolder viewHolder = (SentViewHolder)holder;
//                viewHolder.binding.iconType.setImageResource(reactions[positionn]);
//                viewHolder.binding.iconType.setVisibility(View.VISIBLE);
//            }else{
//                ReceiveViewHolder viewHolder = (ReceiveViewHolder)holder;
//                viewHolder.binding.iconType.setImageResource(reactions[positionn]);
//                viewHolder.binding.iconType.setVisibility(View.VISIBLE);
//            }
//            return true; // true is closing popup, false is requesting a new selection
//        });

        if(holder.getClass() == SentViewHolder.class){
            SentViewHolder viewHolder = (SentViewHolder)holder;
            viewHolder.binding.messageText.setText(message.getMessage());
            if(message.getFeeling() >= 0){
                viewHolder.binding.iconType.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.iconType.setVisibility(View.VISIBLE);
                Log.d("messageSender", message.getMessage());
            }else{
                viewHolder.binding.iconType.setVisibility(View.GONE);

            }
            viewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });



        }else {
            ReceiveViewHolder viewHolder = (ReceiveViewHolder)holder;
            viewHolder.binding.messageTextReceive.setText(message.getMessage());
            if(message.getFeeling() >= 0){
                viewHolder.binding.iconType.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.iconType.setVisibility(View.VISIBLE);
                Log.d("messageReceiver", message.getMessage());
            }else{
                viewHolder.binding.iconType.setVisibility(View.GONE);
            }
            viewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });
//

        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }



    public class SentViewHolder extends RecyclerView.ViewHolder {

        ItemSentBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentBinding.bind(itemView);
        }
    }

    public class ReceiveViewHolder extends RecyclerView.ViewHolder {

        ItemReceiveBinding binding;
        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }

}
