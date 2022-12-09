package com.app.kumase_getupdo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIMER = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SessionManager.getAutoLogin(SplashActivity.this)) {

                    startActivity(new Intent(SplashActivity.this, DashboardActivity.class));

                } else {

                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                }
                finish();
            }
        }, SPLASH_TIMER);
    }
}