package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Thread.sleep;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView splash_image = findViewById(R.id.splash_image);

        Animation myanim= AnimationUtils.loadAnimation(this,R.anim.myanimation);
        splash_image.startAnimation(myanim);

        Thread mythread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(7000);

                    Intent i=new Intent(splash.this,login.class);
                    startActivity(i);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mythread.start();

        ImageView profile = findViewById(R.id.profile);
        Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
       profile.startAnimation(fadein);

        TextView welcome = findViewById(R.id.welcome);
        Animation translate = AnimationUtils.loadAnimation(this, R.anim.bottomup);
       welcome.startAnimation(translate);

    }
}
