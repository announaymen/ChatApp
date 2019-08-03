package com.example.chat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private static final int GALLERY_PICK = 1;
    DatabaseReference mUserDatabase;
    FirebaseUser mCurrentUser;
    TextView mDisplayName , mSatus,mProgressbarTv;
    CircleImageView mImage;
    LinearLayout linearLayout;
    Button mStatusBtn,mImageBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mProgressbarTv=findViewById(R.id.settings_progressbar_tv);
        mDisplayName =findViewById(R.id.settings_Name);
        mSatus =findViewById(R.id.settings_status);
        mImage=findViewById(R.id.settings_image);
        linearLayout=findViewById(R.id.settings_relative_layout);
        linearLayout.setVisibility(View.VISIBLE);
        mProgressbarTv.setText("fetching data....");
        mStatusBtn=findViewById(R.id.settings_status_btn);
        mImageBtn=findViewById(R.id.settings_image_btn);
        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent galleryIntent =new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE") , GALLERY_PICK);*/
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON).setAllowRotation(true).setSnapRadius(50).setAllowFlipping(true).setAllowCounterRotation(true)
                        .start(SettingsActivity.this);
            }
        });

        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             String statusValue=mSatus.getText().toString();
             Intent StatusIntent=new Intent(SettingsActivity.this,StatusActivity.class);
             StatusIntent.putExtra("statusValue",statusValue);
                startActivity(StatusIntent);
            }
        });

        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("user").child(current_uid);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name=dataSnapshot.child("name").getValue().toString();
                    String status=dataSnapshot.child("status").getValue().toString();
                    String image=dataSnapshot.child("image").getValue().toString();
                    String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();
                    mDisplayName.setText(name);
                    mSatus.setText(status);
                Picasso.get().load(image).into(mImage);
                linearLayout.setVisibility(View.GONE);
                mDisplayName.setVisibility(View.VISIBLE);
                mSatus.setVisibility(View.VISIBLE);
               // Toast.makeText(getApplicationContext(),dataSnapshot.toString(),Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                linearLayout.setVisibility(View.GONE);
                mDisplayName.setVisibility(View.VISIBLE);
                mSatus.setVisibility(View.VISIBLE);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                // mImage.setImageURI(resultUri);
                mProgressbarTv.setText("Uploading image....");
                linearLayout.setVisibility(View.VISIBLE);
                mDisplayName.setVisibility(View.GONE);
                mSatus.setVisibility(View.GONE);
                mStatusBtn=findViewById(R.id.settings_status_btn);
                mImageBtn=findViewById(R.id.settings_image_btn);
                String uid=mCurrentUser.getUid();
                //uploading
                final StorageReference filePath = FirebaseStorage.getInstance().getReference()
                        .child("profile_images").child(uid+".jpg");
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri uri1=uri;
                                mUserDatabase.child("image").setValue(uri1.toString());


                            }
                        });
                    }
                });


            /*
                        addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Task<Uri> downloadUrl=filePath.getDownloadUrl();
                            String url="https://firebasestorage.googleapis.com/v0/b/chat-290f8.appspot.com/o/profile_images%2F"+mCurrentUser.getUid().toString()+".jpg?alt=media&token=a9cc462a-b901-40ff-92ba-5227c98072d0";
                           // Uri DownloadUri=   downloadUrl.getResult().getDow
                            linearLayout.setVisibility(View.GONE);
                            mDisplayName.setVisibility(View.VISIBLE);
                            mSatus.setVisibility(View.VISIBLE);
                        // Toast.makeText(getApplicationContext(),downloadUri.toString(),Toast.LENGTH_LONG).show();
                           mSatus.setText("uid="+ mCurrentUser.getUid().toString());
                           mUserDatabase.child("image").setValue(url.toString());
                        }else{

                            linearLayout.setVisibility(View.GONE);
                            mDisplayName.setVisibility(View.VISIBLE);
                            mSatus.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                        }
                    }
                })*/;
// or (prefer using uri for performance and better user experience)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
