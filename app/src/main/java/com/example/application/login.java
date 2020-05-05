package com.example.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;
import static java.lang.Thread.sleep;

public class login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mAuth;
    private TextView tv_status;
    private EditText email,password;
    private GoogleApiClient googleApiClient;
    private ProgressBar progress;
    private FirebaseUser users;
    private boolean emailVerified=false;
    private static final int Sigin=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        users=mAuth.getCurrentUser();

        progress= findViewById(R.id.progressBar);
        email= findViewById(R.id.et_email);
        password= findViewById(R.id.et_password);
        Button btn_login = findViewById(R.id.btn_login);
        TextView notreg = findViewById(R.id.tv_notRegistered);
        SignInButton google_sigin = findViewById(R.id.google_sigin);
        tv_status= findViewById(R.id.tv_status);

        notreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    progress.setVisibility(View.VISIBLE);
                    sleep(2000);
                    startActivity(new Intent(login.this,singup.class));
                    progress.setVisibility(View.GONE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        google_sigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,Sigin);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo();
            }
        });


    }

    private void UserInfo()
    {
        String Email=email.getText().toString().trim();
        String Password=password.getText().toString();

        if(Email.isEmpty()){
            email.setError("Email is Required");
            email.requestFocus();
            return;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            email.setError("Please Enter a Valid Email");
            email.requestFocus();
            return;
        }
        if (Password.isEmpty()){
            password.setError("Password is Required");
            password.requestFocus();
            return;
        }
        if(Password.length()<6){
            password.setError("Minimum length should be 6");
            password.requestFocus();
            return;
        }

        int count=0;
        tv_status.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
        tv_status.setText("Email is not null");
        do { count++;
            if(count>1){
                tv_status.setText("Login Unsuccesful, working on it Again....");
                tv_status.setTextColor(BLUE);

                if(count==3){
                    progress.setVisibility(View.GONE);
                    tv_status.setText("Can't login now, Try again later");
                    tv_status.setTextColor(RED);
                    return;
                }
            }
            mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (mAuth.getCurrentUser()!= null) {
                            tv_status.setText("user not null");
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                tv_status.setText("Verified");
                                emailVerified = true;
                            } else {
                                tv_status.setText("Not verified");
                            }
                            if (mAuth.getCurrentUser() != null) {

                                tv_status.setText("user not null");
                                if (emailVerified==true && (users.getDisplayName() != null || users.getPhotoUrl() != null)) {
                                    //This is to send user to main activity
                                    tv_status.setText("User Email verified");


                                    startActivity(new Intent(login.this,profile_email.class));
                                    finish();


                                } else if (emailVerified && (users.getDisplayName() == null || users.getPhotoUrl() == null)) {
                                    //This is to send user to upload profile activity
                                    tv_status.setText("profile not updated");


                                    startActivity(new Intent(login.this, profile_email.class));
                                    finish();


                                } else if (!emailVerified) {
                                    //This is to tell user that he has not verified his mail

                                    progress.setVisibility(View.GONE);
                                    tv_status.setText("Please Verify your Email for login!!");
                                    tv_status.setTextColor(RED);
                                }
                            } else {
                                //User has not found
                                progress.setVisibility(View.GONE);
                                tv_status.setText("An error Occur, Please login again!!");
                                tv_status.setTextColor(RED);
                            }
                        }
                    }else {
                        //User Unsuccessful in login
                        progress.setVisibility(View.GONE);
                        tv_status.setText("Login Unsuccessful!!");
                        tv_status.setTextColor(RED);
                    }
                }

            });
        } while(users!=null);
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Sigin)
        {
            try{
                Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account=task.getResult(ApiException.class);
                onLogin();
            } catch (ApiException e) {
                Toast.makeText(this, "singin failed: "+e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
            /*GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                startActivity(new Intent(login.this,profile.class));
                finish();
            }else{
                Toast.makeText(this, "Login Failed ", Toast.LENGTH_LONG).show();
            }*/
        }
    }

    private void onLogin() {
        Intent intent=new Intent(login.this,profile.class);
       // intent=intent.putExtra(drawable.ACCOUNT_SERVICE,account);
        startActivity(intent);
        //startActivity(intent1);
        finish();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount alreadyLoggedInaccount= GoogleSignIn.getLastSignedInAccount(this);

        if(alreadyLoggedInaccount!=null)
        {
            onLogin();
        }else{
            if(mAuth.getCurrentUser()!=null ){
                if(users.isEmailVerified()&&(users.getDisplayName()!=null || users.getPhotoUrl()!=null)) {
                    startActivity(new Intent(login.this,profile_email.class));
                    finish();
                }
                else if(users.isEmailVerified() && (Objects.requireNonNull(users.getDisplayName()).isEmpty() || users.getPhotoUrl()==null)) {
                    startActivity(new Intent(login.this,profile_email.class));
                    finish();
                }
                else if(!users.isEmailVerified()){
                    tv_status.setText("Please Verify your Email for login!!");
                    tv_status.setTextColor(RED);
                }
            }
        }
    }
}
