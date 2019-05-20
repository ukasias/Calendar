package com.ukasias.android.calendar;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler hd = new Handler();
        hd.postDelayed(new SplashHandler(), 1300);
    }

    private class SplashHandler implements Runnable {
        @Override
        public void run() {
            startActivity(new Intent(getApplicationContext(),
                    CalendarActivity.class));
            Splash.this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
