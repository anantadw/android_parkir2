package com.example.aplikasiparkirpayment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if user has token (logged in)
                String token = Preferences.getToken(getBaseContext());
                if (token != null) {
                    Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                    nextPage(intent);
                } else {
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    nextPage(intent);
                }
            }
        }, 2000);
    }

    private void nextPage(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}