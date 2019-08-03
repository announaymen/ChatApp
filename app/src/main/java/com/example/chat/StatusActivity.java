package com.example.chat;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    TextInputEditText mStatus;
    Button mSaveBtn;
    DatabaseReference mUserDatabase;
    FirebaseUser mCurrentUser;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        linearLayout = findViewById(R.id.status_relative_layout);
        mToolBar = findViewById(R.id.status_appbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Account status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String statusValue=getIntent().getStringExtra("statusValue");
        mStatus = findViewById(R.id.status_input);
        mStatus.setText(statusValue);
        mSaveBtn = findViewById(R.id.status_save_btn);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.setVisibility(View.VISIBLE);
                mSaveBtn.setEnabled(false);
                mSaveBtn.setAlpha(0);
                String status = mStatus.getText().toString();
                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                String uid = mCurrentUser.getUid();
                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("status");
                mUserDatabase.setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            linearLayout.setVisibility(View.GONE);
                            mSaveBtn.setEnabled(true);
                            mSaveBtn.setAlpha(1);
                            Toast.makeText(getApplicationContext(), "updated",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "updating failed.",
                                    Toast.LENGTH_SHORT).show();
                            linearLayout.setVisibility(View.GONE);
                            mSaveBtn.setEnabled(true);
                            mSaveBtn.setAlpha(1);
                        }
                    }
                });
            }
        });
    }
}
