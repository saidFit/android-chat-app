package com.example.chatsapp.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatsapp.R;
import com.example.chatsapp.databinding.ActivitySetUpProfileBinding;
import com.example.chatsapp.view.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

import javax.annotation.Nullable;

public class SetUpProfileActivity extends AppCompatActivity {

    ActivitySetUpProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectImage;

    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetUpProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage  = FirebaseStorage.getInstance();

        getSupportActionBar().hide();

        dialog = new ProgressDialog(this);
        dialog.setMessage("set up profile...");
        dialog.setCancelable(false);

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,45);
            }
        });

        binding.setUpProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = binding.userName.getText().toString().trim();
                if(userName.isEmpty()){
                    binding.userName.setError("the name is required!");
                    return;
                }

                dialog.show();
                if(selectImage != null){
                    // Create a folder into storage of firebaseStorage;
                    StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
                    reference.putFile(selectImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imgUrl = uri.toString();
                                        String uid = auth.getUid();
                                        String phone = auth.getCurrentUser().getPhoneNumber();
                                        String name  = binding.userName.getText().toString().trim();

                                        User user = new User(uid,name,phone,imgUrl);

                                        // post a user with name of collection;
                                        database.getReference()
                                                .child("Users")
                                                .child(uid)
                                                .setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
//
                                                        Toast.makeText(SetUpProfileActivity.this, "create a new user successfully.", Toast.LENGTH_SHORT).show();
                                                        binding.userName.setText("");
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(SetUpProfileActivity.this,MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }else{
                    String uid = auth.getUid();
                    String phone = auth.getCurrentUser().getPhoneNumber();
                    String name  = binding.userName.getText().toString().trim();
                    User user = new User(uid,name,phone,"No Image");

                    // post a user with name of node;
                    database.getReference()
                            .child("Users") // name of node
                            .child(uid) // Allows you to maintain individual user records under the "Users" node without overwriting each other.
                            .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
//                                                        Intent intent = new Intent(SetUpProfileActivity.this,MainActivity.class);
//                                                        startActivity(intent);
//                                                        finish();
                                    Toast.makeText(SetUpProfileActivity.this, "create a new user successfully. without image profile", Toast.LENGTH_SHORT).show();
                                    binding.userName.setText("");
                                    dialog.dismiss();
                                    Intent intent = new Intent(SetUpProfileActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                }

            }
        });



    }

    @Override
    protected void onActivityResult(int r, int resultCode, @Nullable Intent data) {

        super.onActivityResult(r, resultCode, data);

        if(data != null && data.getData()!=null){
            binding.profileImage.setImageURI(data.getData());
            selectImage = data.getData();
            binding.cameraChoose.setVisibility(View.GONE);
        }else{
            Toast.makeText(this, "Select image has been Failed!", Toast.LENGTH_SHORT).show();
        }
    }

}