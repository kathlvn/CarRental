// com/mobcom/carrental/SplashActivity.java
package com.mobcom.carrental;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.mobcom.carrental.utils.SessionManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager session = new SessionManager(this);

            if (session.isLoggedIn()) {
                // Returning logged-in user — route by role
                routeByRole(session.getRole());
            } else if (session.isGuest()) {
                // Returning guest — go straight to customer browse
                goToCustomer();
            } else {
                // First time or logged out — show welcome screen
                goToWelcome();
            }

            finish(); // remove splash from back stack
        }, SPLASH_DELAY);
    }

    private void routeByRole(String role) {
        switch (role) {
            case SessionManager.ROLE_PROVIDER:
                // TODO: startActivity(new Intent(this, ProviderMainActivity.class));
                startActivity(new Intent(this, ProviderMainActivity.class));
                break;
            case SessionManager.ROLE_ADMIN:
                // TODO: startActivity(new Intent(this, AdminMainActivity.class));
                goToCustomer(); // temporary fallback
                break;
            default:
                goToCustomer();
                break;
        }
    }

    private void goToCustomer() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void goToWelcome() {
        startActivity(new Intent(this, WelcomeActivity.class));
    }
}