package com.example.chatsapp.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatsapp.R;
import com.example.chatsapp.databinding.ItemStatusBinding;
import com.example.chatsapp.view.activity.MainActivity;
import com.example.chatsapp.view.models.Status;
import com.example.chatsapp.view.models.UserStatus;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TopStatusAdapter extends  RecyclerView.Adapter<TopStatusAdapter.TopStatusViewHolder>{

    Context context;
    ArrayList<UserStatus> userStatuses;


    public TopStatusAdapter(Context context,ArrayList<UserStatus>userStatuses){
        this.context = context;
        this.userStatuses = userStatuses;
    }


    @NonNull
    @Override
    public TopStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status,parent,false);
        return new TopStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusViewHolder holder, int position) {

        UserStatus userStatus = userStatuses.get(position);

        Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size() -1);

            Glide.with(context).load(lastStatus.getImageUrl())
                    .into(holder.binding.imageStatus);
            holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());

        holder.binding.imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(Status status : userStatus.getStatuses()){
                    myStories.add(new MyStory(status.getImageUrl()));

                }

                new StoryView.Builder(((MainActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStatus.getName()) // Default is Hidden
                        .setSubtitleText("Damascus") // Default is Hidden
                        .setTitleLogoUrl(!"No Image".equals(userStatus.getProfileImage()) ? userStatus.getProfileImage() : "android.resource://" + context.getPackageName() + "/" + R.drawable.profile_img)
                        // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class TopStatusViewHolder extends RecyclerView.ViewHolder {

        ItemStatusBinding binding;

        public TopStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStatusBinding.bind(itemView);
        }
    }
}
