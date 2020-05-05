package com.example.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.OptionalPendingResultImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import static android.view.View.GONE;

public class profile extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ImageView Profile_img;
    private TextView Email,Disp_name,id;
    private GoogleApiClient googleApiClient;
    private ProgressBar progress;
    GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Profile_img= findViewById(R.id.profile_image);
        Email= findViewById(R.id.tv_email);
        Disp_name= findViewById(R.id.Disp_name);
        id= findViewById(R.id.Id);
        Button signout = findViewById(R.id.btnSignout);
        Button btn_Delete = findViewById(R.id.btnDelete);
        progress= findViewById(R.id.progress_bars);

       // googleSignInClient= GoogleSignIn.getClient(this,gso);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {

                            startActivity(new Intent(profile.this,login.class));
                            finish();
                        }else{
                            Toast.makeText(profile.this, "Logout Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String name,email,photo;
            name=Disp_name.getText().toString();
            email=Email.getText().toString();
            photo=id.getText().toString();
            Intent intent= new Intent(profile.this,drawable.class);
            intent.putExtra("name",name);
            intent.putExtra("Email",email);
            intent.putExtra("url",photo);
            startActivity(intent);
            finish();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void DisplaySiginResult(GoogleSignInResult result)
    {
        progress.setVisibility(View.VISIBLE);
        if(result.isSuccess())  //login is successful
        {
            GoogleSignInAccount account= result.getSignInAccount(); //to get account info
            Disp_name.setText(Objects.requireNonNull(account).getDisplayName());     //these are getting account info and displaying to user
            Email.setText(account.getEmail());
            id.setText(Objects.requireNonNull(account.getPhotoUrl()).toString());

            Glide.with(this)
                    .load(account.getPhotoUrl().toString())
                    .centerCrop()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progress.setVisibility(GONE);
                            Log.e("TAG","Error loading Image :"+ Objects.requireNonNull(e).getMessage(),e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.e("TAG","successfully Loaded ");
                            progress.setVisibility(GONE);
                            return false;
                        }
                    })
                    .into(Profile_img); //A diff method is used to load image fom internet..

            /*Picasso.get()
                    .load(account.getPhotoUrl())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(Profile_img);*/
        }else{
            startActivity(new Intent(profile.this,login.class));
            finish();
        }
    }

    @Override
    protected void onStart() {      //to update the details of user on each login...
        super.onStart();

        OptionalPendingResultImpl<GoogleSignInResult> opr= (OptionalPendingResultImpl<GoogleSignInResult>) Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        if(opr.isDone())
        {
            GoogleSignInResult result=opr.get();
            DisplaySiginResult(result);
        }else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    DisplaySiginResult(result);
                }
            });

        }

    }
}
