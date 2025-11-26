package com.example.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.main.AdminOperation.LoginActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

//    Animation anime;
//TextView splashName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);

    }
}