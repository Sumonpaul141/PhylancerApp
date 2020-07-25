package com.example.sumonpaul.thephylancer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText regEmailField;
    private EditText regPasswordField;
    private EditText regPasswordConfirm;
    private Button regButton;
    private Button regLoginButton;
    private ProgressBar regProgressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        regEmailField = (EditText) findViewById(R.id.reg_email);
        regPasswordField = (EditText) findViewById(R.id.reg_password);
        regPasswordConfirm= (EditText) findViewById(R.id.reg_confirm_password);
        regButton = (Button)findViewById(R.id.reg_button);
        regLoginButton = (Button) findViewById(R.id.reg_login_button);
        regProgressBar = (ProgressBar) findViewById(R.id.reg_progress);

        regLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });



        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  email = regEmailField.getText().toString();
                String password = regPasswordField.getText().toString();
                String confirmPassword = regPasswordConfirm.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)){
                    if (password.equals(confirmPassword)){

                        regProgressBar.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                    startActivity(setupIntent);
                                    finish();

                                }else{
                                    String error = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error : " + error, Toast.LENGTH_LONG).show();

                                }

                                regProgressBar.setVisibility(View.INVISIBLE);

                            }
                        });
                    }else{
                        Toast.makeText(RegisterActivity.this, "Password and Confirm Password didn't match", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            sendToMain();

        }
    }

    private void sendToMain() {
        Intent mainIntent= new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
