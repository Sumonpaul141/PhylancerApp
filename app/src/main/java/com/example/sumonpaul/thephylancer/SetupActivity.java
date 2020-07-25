package com.example.sumonpaul.thephylancer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private Toolbar setupToolbar;
    private CircleImageView setupImage;
    private Uri mainImageUri = null;

    private EditText setupName;
    private EditText setupAge;
    private EditText setupSex;
    private EditText setupLocation;
    private ProgressBar setupProgress;
    private Button setupButton;
    private String user_id;
    private boolean isChanged = false;


    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        setupToolbar = (Toolbar) findViewById(R.id.setup_toolbar);
        setSupportActionBar(setupToolbar);

        getSupportActionBar().setTitle("Account SetUp");

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        setupImage = (CircleImageView) findViewById(R.id.setup_image);
        setupName = (EditText) findViewById(R.id.setup_name);
        setupAge = (EditText) findViewById(R.id.setup_age);
        setupSex = (EditText) findViewById(R.id.setup_sex);
        setupLocation = (EditText) findViewById(R.id.setup_location);
        setupButton = (Button) findViewById(R.id.setup_button);
        setupProgress = (ProgressBar) findViewById(R.id.setup_progressbar);

        setupProgress.setVisibility(View.VISIBLE);
        setupButton.setEnabled(false);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                        String name = task.getResult().getString("name");
                        String sex = task.getResult().getString("sex");
                        String location = task.getResult().getString("location");
                        String age = task.getResult().getString("age");
                        String image = task.getResult().getString("image");

                        mainImageUri = Uri.parse(image);


                        setupName.setText(name);
                        setupAge.setText(age);
                        setupSex.setText(sex);
                        setupLocation.setText(location);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.dpp);


                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);



                    }else{
                        Toast.makeText(SetupActivity.this, "Data Doesnot exists", Toast.LENGTH_LONG).show();

                    }

                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "FireStore Retreive Error : " + error, Toast.LENGTH_LONG).show();

                }

                setupProgress.setVisibility(View.INVISIBLE);
                setupButton.setEnabled(true);
            }
        });


        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String userName = setupName.getText().toString();
                final String location = setupLocation.getText().toString();
                final String age = setupAge.getText().toString();
                final String sex = setupSex.getText().toString();

                if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(location) && !TextUtils.isEmpty(age) && !TextUtils.isEmpty(sex) && mainImageUri != null) {

                    setupProgress.setVisibility(View.VISIBLE);

                    if (isChanged) {




                        StorageReference image_path = storageReference.child("profile_Images").child(user_id + ".jpg");


                        image_path.putFile(mainImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    //                                                    Uri downLoadUri = task.getResult().getDownloadUrl();
                                    //
                                    //
                                    //                                                    Map<String,String> userMap = new HashMap<>();
                                    //                                                    userMap.put("name", userName);
                                    //                                                    userMap.put("location", location);
                                    //                                                    userMap.put("age", age);
                                    //                                                    userMap.put("sex", sex);
                                    //                                                    userMap.put("image", downLoadUri.toString() );
                                    //
                                    //                                                    firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    //                                                        @Override
                                    //                                                        public void onComplete(@NonNull Task<Void> task) {
                                    //                                                            if (task.isSuccessful()){
                                    //                                                                Toast.makeText(SetupActivity.this, "Settings Updated", Toast.LENGTH_LONG).show();
                                    //                                                                Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                                    //                                                                startActivity(mainIntent);
                                    //                                                                finish();
                                    //
                                    //
                                    //                                                            }else{
                                    //                                                                    String error = task.getException().getMessage();
                                    //                                                                    Toast.makeText(SetupActivity.this, "FireStore Error : " + error, Toast.LENGTH_LONG).show();
                                    //
                                    //                                                             }
                                    //                                                            setupProgress.setVisibility(View.INVISIBLE);
                                    //                                                        }
                                    //                                                     });


                                    storeFireStore(task, userName, location, age, sex);


                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this, "Image Error : " + error, Toast.LENGTH_LONG).show();

                                    setupProgress.setVisibility(View.INVISIBLE);
                                }

                            }

                        });


                    } else {
                        storeFireStore(null, userName, location, age, sex);

                    }

                }else{
                    Toast.makeText(SetupActivity.this, "Some fields are Blank", Toast.LENGTH_LONG).show();

                }
            }
        });





        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {
                        imagePicker();

                    }

                } else {
                    imagePicker();

                }

            }
        });


    }


    //Modification of fireStrore

    private void storeFireStore(@NonNull Task<UploadTask.TaskSnapshot> task, String userName, String location, String age, String sex) {

        Uri downLoadUri;
       if (task != null) {

           downLoadUri = task.getResult().getDownloadUrl();

       }else{
           downLoadUri = mainImageUri;

       }


        Map<String,String> userMap = new HashMap<>();
        userMap.put("name", userName);
        userMap.put("location", location);
        userMap.put("age", age);
        userMap.put("sex", sex);
        userMap.put("image", downLoadUri.toString() );

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SetupActivity.this, "Profile Updated", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();


                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "FireStore Error : " + error, Toast.LENGTH_LONG).show();

                }
                setupProgress.setVisibility(View.INVISIBLE);
            }
        });
    }


    //Modification of fireStrore end

    private void imagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetupActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                mainImageUri = result.getUri();
                setupImage.setImageURI(mainImageUri);
                isChanged =true;


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

