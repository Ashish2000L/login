package com.example.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class profile_email extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final int CHOOSE_IMAGE =101 ;
    private ImageView profile_img;
    private EditText Disp_name;
    private Button Upload;
    private String profileImageUrl;
    private ProgressBar progress;
    private Uri uriprofileimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_email);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        profile_img= findViewById(R.id.profile_img);
        Disp_name= findViewById(R.id.display_name);
        Upload= findViewById(R.id.btn_upload);
        progress= findViewById(R.id.progress_bar);

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                SaveUserInfo();

            }
        });

    }

    private void SaveUserInfo() {
        String Displayname=Disp_name.getText().toString();
        if(Displayname.isEmpty()){

            Disp_name.setError("Please Enter your Name");
            Disp_name.requestFocus();
            return;
        }

        FirebaseUser User=mAuth.getCurrentUser();       //by this way we can access info of user and write it back

        if(User!=null){
            UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder()        //getting users profile image url
                    .setDisplayName(Displayname)                                  //
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            User.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progress.setVisibility(View.GONE);
                                Toast.makeText(profile_email.this, "Profile Name updated", Toast.LENGTH_SHORT).show();
                                Upload.setVisibility(View.GONE);
                                startActivity(new Intent(profile_email.this, profile_email.class));
                                finish();
                            }else{
                                progress.setVisibility(View.GONE);
                                Toast.makeText(profile_email.this, "Uploading Unsuccessful!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CHOOSE_IMAGE    &&  resultCode==RESULT_OK  &&  data!=null  && data.getData()!=null){
            uriprofileimage=data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriprofileimage);
                profile_img.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void uploadImageToFirebaseStorage() {

        final StorageReference profileImageRef= FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis()+".jpg");
        if(uriprofileimage!=null){
            progress.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriprofileimage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progress.setVisibility(View.GONE);
                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profileImageUrl = uri.toString();
                                    Toast.makeText(profile_email.this, "Profile Image Uploaded", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    }
                }}).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progress.setVisibility(View.GONE);
                    Toast.makeText(profile_email.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void showImageChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select profile Image"),CHOOSE_IMAGE);
    }
}
