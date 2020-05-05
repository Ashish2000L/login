package com.example.application;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
//import android.view.WindowManager;
import android.widget.RelativeLayout;
//import android.widget.TextView;

//import android.widget.Toolbar;

//import com.spark.submitbutton.SubmitButton;

import com.spark.submitbutton.SubmitButton;

import static java.lang.Thread.sleep;

public class test extends AppCompatActivity {


    private Intent main;
    private Thread newthread;

    private AnimationDrawable animationDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //THIS IS ONW OF THE WAY, ANOTHER WAY IS IN THE XML FILE BY ADDING KEEPSCREENON="TRUE"
        RelativeLayout layout = findViewById(R.id.mylayout);
        animationDrawable=(AnimationDrawable) layout.getBackground(); //THIS IS FOR THE BACKGROUND GRADEL ANIMATIONS
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.start();

        SubmitButton submit = findViewById(R.id.btnsubmit);
        /*image.setOnClickListener(new View.OnClickListener() {     NOT WORKING PROPERLY HERE , MAKING ACTIVITY TO CHASHED
            @Override
            public void onClick(View v) {
                Toast.makeText(test.this, "My name is Ashish Kumar :)", Toast.LENGTH_LONG).show();
            }
        });*/



         submit.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View view){
                 newthread.start();
             }
         });
         
        newthread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    main=new Intent(test.this,profile_email.class);
                    startActivity(main);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
