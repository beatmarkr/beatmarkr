package com.cs130.beatmarkr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        preload();
    }

    private void preload() {
        // Do some preloading, for now it's just a timer (3 seconds)
        new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                // Launch song list activity
                Intent intent = new Intent(SplashScreenActivity.this, SongListActivity.class);
                startActivity(intent);

                // Close splash screen activity
                finish();
            }
        }.start();
    }
}
