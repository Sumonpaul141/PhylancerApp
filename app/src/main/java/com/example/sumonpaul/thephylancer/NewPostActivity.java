package com.example.sumonpaul.thephylancer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {
    private Toolbar newPostToolbar;
    private ImageView newPostImage;
    private EditText newPostDesc;
    private EditText newPostLocation;
    private EditText newPostContactNo;
    private EditText newPostAmount;
    private Button newPostButton;
    private ProgressBar newPostProgressBar;
    private Uri postImageUri = null;
    private String currentUser_id;


    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;



    private Bitmap compressedImageFile;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        currentUser_id = firebaseAuth.getCurrentUser().getUid();


        newPostToolbar = (Toolbar) findViewById(R.id.new_post_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Post A Job here");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newPostImage = (ImageView) findViewById(R.id.new_post_image);
        newPostDesc = (EditText) findViewById(R.id.new_post_description);
        newPostLocation = (EditText) findViewById(R.id.new_post_area);
        newPostContactNo = (EditText)findViewById(R.id.new_post_contactno);
        newPostAmount = (EditText) findViewById(R.id.new_post_amount);
        newPostButton = (Button) findViewById(R.id.new_post_job_button);
        newPostProgressBar = (ProgressBar) findViewById(R.id.new_post_progress);


        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1, 1)
                        .start(NewPostActivity.this);

            }
        });

        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String desc = newPostDesc.getText().toString();
                final String location = newPostLocation.getText().toString();
                final String contact = newPostContactNo.getText().toString();
                final String amount =newPostAmount.getText().toString();

                if (!TextUtils.isEmpty(desc)&& !TextUtils.isEmpty(location) && !TextUtils.isEmpty(contact) && !TextUtils.isEmpty(amount) && postImageUri != null){

                    newPostProgressBar.setVisibility(View.VISIBLE);
                    final String randomName = UUID.randomUUID().toString();

                    StorageReference filePath = storageReference.child("post_images").child(randomName + ".jpg");
                    filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            final String downloadUri = task.getResult().getDownloadUrl().toString();

                            if (task.isSuccessful()){

                                File newImageFile = new File(postImageUri.getPath());
                                try {

                                    compressedImageFile = new Compressor(NewPostActivity.this)
                                            .setMaxWidth(100)
                                            .setMaxHeight(100)
                                            .setQuality(5)
                                            .compressToBitmap(newImageFile);


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData = baos.toByteArray();

                                UploadTask uploadTask = storageReference.child("post_images/thumbs").child(randomName + ".jpg").putBytes(thumbData);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        String downloadThumbUri = taskSnapshot.getDownloadUrl().toString();

                                        Map<String, Object> postMap = new HashMap<>();
                                        postMap.put("image_url", downloadUri);
                                        postMap.put("thumb",downloadThumbUri);
                                        postMap.put("description", desc);
                                        postMap.put("location", location);
                                        postMap.put("contact", contact);
                                        postMap.put("amount", amount);
                                        postMap.put("user_id", currentUser_id);
                                        postMap.put("timestamp", FieldValue.serverTimestamp());

                                        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()){

                                                    Toast.makeText(NewPostActivity.this, "Post was Added", Toast.LENGTH_LONG).show();
                                                    Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                                                    startActivity(mainIntent);
                                                    finish();

                                                }else{

                                                }
                                                newPostProgressBar.setVisibility(View.INVISIBLE);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

//
//
//                                String downloadUri = task.getResult().getDownloadUrl().toString();
//
//                                Map<String, Object> postMap = new HashMap<>();
//                                postMap.put("image_url", downloadUri);
//                                postMap.put("description", desc);
//                                postMap.put("location", location);
//                                postMap.put("contact", contact);
//                                postMap.put("amount", amount);
//                                postMap.put("user_id", currentUser_id);
//                                postMap.put("timestamp", FieldValue.serverTimestamp());
//
//                                firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                                        if (task.isSuccessful()){
//
//                                            Toast.makeText(NewPostActivity.this, "Post was Added", Toast.LENGTH_LONG).show();
//                                            Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
//                                            startActivity(mainIntent);
//                                            finish();
//
//                                        }else{
//
//                                        }
//                                        newPostProgressBar.setVisibility(View.INVISIBLE);
                                    }
                                });

                            }else{

                                newPostProgressBar.setVisibility(View.INVISIBLE);

                            }

                        }
                    });


                }else{

                    Toast.makeText(NewPostActivity.this, "Fill Up correctly", Toast.LENGTH_LONG).show();
                }

            }
        });






    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
