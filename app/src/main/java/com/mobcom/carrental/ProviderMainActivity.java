// com/mobcom/carrental/ProviderMainActivity.java
package com.mobcom.carrental;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobcom.carrental.database.DatabaseInitializer;
import com.mobcom.carrental.utils.BookingService;
import com.mobcom.carrental.utils.DatabaseChatStore;
import com.mobcom.carrental.utils.ReviewService;
import com.mobcom.carrental.utils.ReportService;
import com.mobcom.carrental.utils.CarService;

public class ProviderMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_main);

        // Initialize all services
        DatabaseChatStore.initialize(this);
        BookingService.initialize(this);
        ReviewService.initialize(this);
        ReportService.initialize(this);
        CarService.initialize(this);

        DatabaseInitializer.initializeDatabase(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.provider_nav_host_fragment);

        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.provider_bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}