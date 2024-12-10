package com.message.ir_messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    ImageView splogoimg;
    TextView  sptxtIR, sptxtF, sptxtFD;
    Animation topAnim, bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        splogoimg = findViewById(R.id.splogoimg);
        sptxtIR = findViewById(R.id.sptxtIR);
        sptxtF = findViewById(R.id.sptxtF);
        sptxtFD = findViewById(R.id.sptxtFD);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        splogoimg.setAnimation(topAnim);
        sptxtIR.setAnimation(bottomAnim);
        sptxtF.setAnimation(bottomAnim);
        sptxtFD.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent SpsIntent = new Intent(SplashScreen.this, LoginScreen.class);
                startActivity(SpsIntent);
                finish();
            }
        }, 2500);

    }
}