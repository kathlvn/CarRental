// com/mobcom/carrental/WelcomeActivity.java
package com.mobcom.carrental;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.mobcom.carrental.utils.SessionManager;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        MaterialButton btnLogin  = findViewById(R.id.btnLogin);
        MaterialButton btnSignUp = findViewById(R.id.btnSignUp);
        MaterialButton btnGuest  = findViewById(R.id.btnGuest);

        btnLogin.setOnClickListener(v -> {
            // TODO: startActivity(new Intent(this, LoginActivity.class));
        });

        btnSignUp.setOnClickListener(v -> {
            // TODO: navigate to LoginActivity with showRegister = true
        });


        btnGuest.setOnClickListener(v -> {
            // Save guest session and go to customer browse
            new SessionManager(this).setGuest();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}