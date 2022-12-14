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

        new Handler().postDelayed(() -> {
            // Check if user has token (logged in)
            String token = Preferences.getToken(getBaseContext());
            Intent intent;
            if (token != null) {
                intent = new Intent(getBaseContext(), HomeActivity.class);
            } else {
                intent = new Intent(getBaseContext(), LoginActivity.class);
            }
            nextPage(intent);
        }, 2000);
    }

    private void nextPage(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}