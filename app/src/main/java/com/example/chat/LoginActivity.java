package com.example.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {
   Toolbar mToolbar;
   EditText mLoginEmail, mLoginPassword;
   Button mLoginBtn;
   LinearLayout linearLayout;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mToolbar=findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        linearLayout=findViewById(R.id.register_relative_layout2);
        linearLayout.setAlpha(0);
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLoginEmail =findViewById(R.id.login_email);
        mLoginPassword =findViewById(R.id.login_password);
        mLoginBtn =findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mLoginEmail.getText().toString();
                String password=mLoginPassword.getText().toString();
                if(!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password)){
                    linearLayout.setAlpha(1);
                    mLoginBtn.setVisibility(View.GONE);
                    loginUser(email,password);

                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            linearLayout.setAlpha(0);
                            mLoginBtn.setVisibility(View.GONE);
                            sendToMain();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            linearLayout.setAlpha(0);
                            mLoginBtn.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private void sendToMain() {
        Intent mainIntent =new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
