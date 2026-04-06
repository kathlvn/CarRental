package com.mobcom.carrental;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.admin_nav_host_fragment);

        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.admin_bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}