package com.example.the_commoners_guinness;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreenActivity extends AppCompatActivity {
    TextView tvAppName;
    LottieAnimationView lottie;
    ImageView ivAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //tvAppName = findViewById(R.id.tvRecordSetters);
        lottie = findViewById(R.id.lottieAnimation);
        ivAppName = findViewById(R.id.ivAppName);
        //tvAppName.animate().translationY(-1200).setDuration(1500).setStartDelay(0);

        ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flipping);
        anim.setTarget(ivAppName);
        anim.setDuration(2500);
        anim.start();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(splashIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                //finish();
            }
        }, 3000);
    }
}