package com.example.chatsapp.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chatsapp.R;
import com.example.chatsapp.databinding.ActivityMainBinding;
import com.example.chatsapp.view.adapters.ConversationAdapter;
import com.example.chatsapp.view.adapters.TopStatusAdapter;
import com.example.chatsapp.view.models.Message;
import com.example.chatsapp.view.models.Status;
import com.example.chatsapp.view.models.User;
import com.example.chatsapp.view.models.UserStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    List<User> users;
    ConversationAdapter conversationAdapter;
    TopStatusAdapter topStatusAdapter;
    ArrayList<UserStatus> userStatuses;
    ProgressDialog dialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("image uploading...");

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        users = new ArrayList<>();
        userStatuses = new ArrayList<>();

        // select a currentUser from db;
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            user = snapshot.getValue(User.class);

                            // Now you can safely access user.getName() and other properties
                        } else {
                            // Handle the case where the data doesn't exist
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error if needed
                    }
                });


        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    // for not make a lot of stories
                    userStatuses.clear();
                    for (DataSnapshot storySnapshot : snapshot.getChildren()){
                        UserStatus userStatus = new UserStatus();
                        userStatus.setName(storySnapshot.child("name").getValue(String.class));
                        userStatus.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        userStatus.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status> statuses = new ArrayList<>();

                        for(DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()){
                            Status sampleStatus = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }

                        userStatus.setStatuses(statuses);
                        userStatuses.add(userStatus);

                    }

                    Log.d("userStatuses", String.valueOf(userStatuses));

                    topStatusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        conversationAdapter = new ConversationAdapter(users,this);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(conversationAdapter);
        conversationAdapter.notifyDataSetChanged();

        topStatusAdapter = new TopStatusAdapter(this,userStatuses);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusList.setLayoutManager(layoutManager);
        binding.statusList.setAdapter(topStatusAdapter);


        binding.recyclerView.showShimmer();     // to start showing shimmer
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // this clear more importance to avoid repetition data;
                users.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    if(!user.getUid().equals(FirebaseAuth.getInstance().getUid())){
                        users.add(user);
                    }

                }
                binding.recyclerView.hideShimmer();
                conversationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

               if(item.getItemId() == R.id.status){
                   Intent intent = new Intent();
                   intent.setType("image/*");
                   intent.setAction(Intent.ACTION_GET_CONTENT);
                   startActivityForResult(intent,75);
               }
                return false;
            }
        });
    }


    // when click on status of navigationBar;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            if(data.getData() != null){
                dialog.show();

                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = firebaseStorage.getReference().child("status").child(date.getTime() + "");

                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    UserStatus userStatus = new UserStatus();
                                    userStatus.setName(user.getName());
                                    userStatus.setProfileImage(user.getProfileImage());
                                    userStatus.setLastUpdated(date.getTime());

                                    // make a object data;
                                    HashMap<String,Object> obj = new HashMap<>();
                                    obj.put("name",userStatus.getName());
                                    obj.put("profileImage",userStatus.getProfileImage());
                                    obj.put("lastUpdated",userStatus.getLastUpdated());

                                    String imageUrl = uri.toString();
                                    Status status = new Status(imageUrl,userStatus.getLastUpdated());

                                    database.getReference()
                                                    .child("stories")
                                                    .child(FirebaseAuth.getInstance().getUid())
                                                    .updateChildren(obj);

                                    database.getReference().child("stories")
                                                    .child(FirebaseAuth.getInstance().getUid())
                                                            .child("statuses")
                                                                    .push()
                                                                            .setValue(status);
                                    dialog.dismiss();



                                }
                            });
                        }
                    }
                });
            }
        }
    }

    // handle the click on top menu;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.search){
            Toast.makeText(this, "search clicked.", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.setting){
            Toast.makeText(this, "setting clicked.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        // Notify the adapter about the data change
//        conversationAdapter.notifyDataSetChanged();
//    }


    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();

        // Go online when the activity is resumed
        database.getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Go offline when the activity is paused
        database.goOffline();
    }


}