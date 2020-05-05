package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.Objects;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class singup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView tv_already_reg,tv_prog;
    private EditText et_email,et_pass,et_cnfpass;
    private LinearLayout linearprog;
    private ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        mAuth=FirebaseAuth.getInstance();

        tv_already_reg= findViewById(R.id.tv_alreadyRegistered);
        tv_prog= findViewById(R.id.tv_progress);
        et_email= findViewById(R.id.et_Emails);
        et_pass= findViewById(R.id.et_pass);
        et_cnfpass= findViewById(R.id.et_cnfpass);
        Button btn_reg = findViewById(R.id.btn_reg);
        linearprog= findViewById(R.id.linearprogress);
        progress= findViewById(R.id.progress);

       btn_reg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               linearprog.setVisibility(VISIBLE);
               tv_prog.setText("Processing.....");
               loadUserInfo();

           }
       });


    }

    private void loadUserInfo() {

        final String Email=et_email.toString().trim();
        String Password=et_pass.toString();
        String confirmPassword=et_cnfpass.toString();

        if(Email.isEmpty()){
            et_email.setError("Email is Required");
            et_email.requestFocus();
            return;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            et_email.setError("Please Enter a Valid Email");
            et_email.requestFocus();
            return;
        }
        if (Password.isEmpty()){
            et_pass.setError("Password is Required");
            et_pass.requestFocus();
            return;
        }
        if(Password.length()<6){
            et_pass.setError("Minimum length should be 6");
            et_pass.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()){
            et_cnfpass.setError("Confirm Password is Required");
            et_cnfpass.requestFocus();
            return;
        }
        if(!confirmPassword.matches(Password)){
            et_cnfpass.setError("Passwords not matched");
            et_cnfpass.requestFocus();
            return;
        }

        tv_prog.setText("Please wait....");

        mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                              progress.setVisibility(GONE);
                                tv_prog.setText("Please Verify and proceed to Login!!");
                                tv_prog.setTextColor(GREEN);
                                tv_already_reg.setVisibility(VISIBLE);
                                et_email.setText("");
                                et_pass.setText("");
                                et_cnfpass.setText("");
                            }else {
                                progress.setVisibility(GONE);
                                tv_prog.setText("Unable to send mail Try again");
                                tv_prog.setTextColor(RED);
                            }
                        }
                    });

                }else {
                    if(task.getException()instanceof FirebaseAuthUserCollisionException){
                        progress.setVisibility(GONE);
                        tv_prog.setTextColor(BLUE);
                        tv_prog.setText("User already registered, Proceed to Login  ");
                        tv_already_reg.setVisibility(VISIBLE);
                    }else {
                        progress.setVisibility(GONE);
                        tv_prog.setTextColor(RED);
                        tv_prog.setText("Regesrtion Unsuccessful ");
                    }
                }
            }
        });



    }

}
