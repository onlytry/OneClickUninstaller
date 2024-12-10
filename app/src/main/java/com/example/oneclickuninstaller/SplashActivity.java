package com.example.oneclickuninstaller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DISPLAY_LENGTH = 1000; // 1秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting SplashActivity");
        try {
            setContentView(R.layout.activity_splash);
            Log.d(TAG, "onCreate: setContentView successful");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error setting content view", e);
            finish();
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: Launching MainActivity");
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
