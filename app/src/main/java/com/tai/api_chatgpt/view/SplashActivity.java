package com.tai.api_chatgpt.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.tai.api_chatgpt.R;

public class SplashActivity extends AppCompatActivity {
    private static final long TIME_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoMainAct();
            }
        }, TIME_DELAY);
    }

    private void gotoMainAct() {
       startActivity(new Intent(SplashActivity.this,MainActivity.class));
    }
}
