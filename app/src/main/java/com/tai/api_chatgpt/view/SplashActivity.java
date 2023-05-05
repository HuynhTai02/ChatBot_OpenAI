package com.tai.api_chatgpt.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.app.AppCompatActivity;

import com.tai.api_chatgpt.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    private static final long TIME_DELAY = 2100;
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        animationSplash();
        gotoMainAct();
    }

    private void animationSplash() {
        // Tạo ObjectAnimator để quay ImageView 360 độ
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(binding.ivChatgpt, "rotation", 0f, 360f);
        rotateAnimator.setDuration(1000);
        // LinearInterpolator -> tốc độ di chuyển của animation được duy trì một cách đều nhau.
        rotateAnimator.setInterpolator(new LinearInterpolator());

        // Tạo ObjectAnimator để di chuyển ImageView xuống dưới
        ObjectAnimator translateYAnimator = ObjectAnimator.ofFloat(binding.ivChatgpt, "translationY", 0f, 900f);
        translateYAnimator.setDuration(1000);
        //AccelerateInterpolator -> animation sẽ di chuyển chậm ở đầu, sau đó tăng tốc dần lên và trở nên nhanh hơn ở cuối.
        translateYAnimator.setInterpolator(new AccelerateInterpolator());

        // Tạo ObjectAnimator để phóng to và thu nhỏ TextView
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(binding.tvName, "scaleX", 0f, 1.1f);
        scaleXAnimator.setDuration(1000);
        scaleXAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(binding.tvName, "scaleY", 0f, 1.1f);
        scaleYAnimator.setDuration(1000);
        scaleYAnimator.setInterpolator(new AccelerateInterpolator());

        // Kết hợp các ObjectAnimator lại để chạy cùng lúc
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotateAnimator, translateYAnimator, scaleXAnimator, scaleYAnimator);

        animatorSet.start();
    }

    private void gotoMainAct() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, TIME_DELAY);
    }
}
