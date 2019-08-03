package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText mDisplayName, mEmail, mPassword;
    private Button mCreateBtn;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    LinearLayout linearLayout;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        linearLayout = findViewById(R.id.register_relative_layout);
        linearLayout.setAlpha(0);
        getSupportActionBar().setTitle("Create account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mCreateBtn = findViewById(R.id.login_btn);
        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);
        mDisplayName = findViewById(R.id.reg_display_name);
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display_name = mDisplayName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if (!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mCreateBtn.setEnabled(false);
                    mCreateBtn.setAlpha(0);
                    linearLayout.setAlpha(1);
                    register_user(display_name, email, password);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        sendToMain();
    }

    private void register_user(final String display_name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser current_user=FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();
                            mDatabase=FirebaseDatabase.getInstance().getReference().child("user").child(uid);
                            HashMap<String,String> userMap =new HashMap<>();
                            userMap.put("name",display_name);
                            userMap.put("status","hi there im using app chat");
                            userMap.put("image","image_link");
                            userMap.put("thumb_image","default");
                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        linearLayout.setAlpha(0);
                                        mCreateBtn.setEnabled(true);
                                        mCreateBtn.setAlpha(1);
                                        sendToMain();

                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        linearLayout.setAlpha(0);
                                        mCreateBtn.setEnabled(true);
                                        mCreateBtn.setAlpha(1);
                                    }
                                }
                            });
                        }

                    }
                });
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
