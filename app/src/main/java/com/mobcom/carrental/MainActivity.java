package com.mobcom.carrental;

import com.mobcom.carrental.database.DatabaseInitializer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobcom.carrental.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseInitializer.initializeDatabase(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        SessionManager sessionManager = new SessionManager(this);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            boolean isProtected = itemId == R.id.myRentalsFragment || itemId == R.id.messagesFragment;

            if (sessionManager.isGuest() && isProtected) {
                GuestLoginWallBottomSheet
                        .newInstance(itemId == R.id.messagesFragment ? "messages" : "my_rentals")
                        .show(getSupportFragmentManager(), "GuestLoginWall");
                return false;
            }

            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        bottomNav.setOnItemReselectedListener(item -> {
            // No-op
        });
    }
}
