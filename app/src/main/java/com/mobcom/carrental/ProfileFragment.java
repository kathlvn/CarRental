package com.mobcom.carrental;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.mobcom.carrental.models.Rental;
import com.mobcom.carrental.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvPhone;
    private TextView tvStatsTotal, tvStatsCompleted, tvStatsCancelled;
    private CardView cardStatsTotal, cardStatsCompleted, cardStatsCancelled;
    private LinearLayout layoutEditProfile, layoutChangePassword, layoutLogout;
    private SwitchMaterial switchNotifications;

    private SessionManager sessionManager;
    private final List<Rental> rentalHistory = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        bindViews(view);
        loadProfileData();
        loadRentalHistory();
        updateBookingStats();
        setupActions();
    }

    private void bindViews(@NonNull View view) {
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);

        tvStatsTotal = view.findViewById(R.id.tvStatsTotal);
        tvStatsCompleted = view.findViewById(R.id.tvStatsCompleted);
        tvStatsCancelled = view.findViewById(R.id.tvStatsCancelled);

        cardStatsTotal = view.findViewById(R.id.cardStatsTotal);
        cardStatsCompleted = view.findViewById(R.id.cardStatsCompleted);
        cardStatsCancelled = view.findViewById(R.id.cardStatsCancelled);

        layoutEditProfile = view.findViewById(R.id.layoutEditProfile);
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);
        layoutLogout = view.findViewById(R.id.layoutLogout);
        switchNotifications = view.findViewById(R.id.switchNotifications);
    }

    private void loadProfileData() {
        String name = sessionManager.getName();
        String email = sessionManager.getEmail();

        tvName.setText(name == null || name.isEmpty() ? "Customer User" : name);
        tvEmail.setText(email == null || email.isEmpty() ? "customer@carrental.com" : email);
        tvPhone.setText("+63 9XX XXX XXXX");
    }

    private void loadRentalHistory() {
        rentalHistory.clear();

        rentalHistory.add(new Rental(
                "R001", "Toyota Vios", "", "ABC-1234", "Bacolod City",
                "Mar 20, 2026", "Mar 22, 2026", 2, 2600,
                Rental.Status.COMPLETED, "Juan Dela Cruz"
        ));

        rentalHistory.add(new Rental(
                "R002", "Honda City", "", "XYZ-8765", "Silay City",
                "Apr 01, 2026", "Apr 03, 2026", 2, 3000,
                Rental.Status.CANCELLED, "Maria Santos"
        ));

        rentalHistory.add(new Rental(
                "R003", "Mitsubishi Mirage", "", "DEF-3321", "Talisay City",
                "Apr 10, 2026", "Apr 12, 2026", 2, 2400,
                Rental.Status.ACTIVE, "Pedro Reyes"
        ));

        rentalHistory.add(new Rental(
                "R004", "Toyota Innova", "", "HIJ-9900", "Bago City",
                "Feb 28, 2026", "Mar 03, 2026", 4, 7600,
                Rental.Status.COMPLETED, "Ana Garcia"
        ));
    }

    private void updateBookingStats() {
        int total = rentalHistory.size();
        int completed = 0;
        int cancelled = 0;

        for (Rental rental : rentalHistory) {
            if (rental.getStatus() == Rental.Status.COMPLETED) {
                completed++;
            } else if (rental.getStatus() == Rental.Status.CANCELLED) {
                cancelled++;
            }
        }

        tvStatsTotal.setText(String.valueOf(total));
        tvStatsCompleted.setText(String.valueOf(completed));
        tvStatsCancelled.setText(String.valueOf(cancelled));
    }

    private void setupActions() {
        cardStatsTotal.setOnClickListener(v -> openMyRentalsTab(0));
        cardStatsCompleted.setOnClickListener(v -> openMyRentalsTab(1));
        cardStatsCancelled.setOnClickListener(v -> openMyRentalsTab(2));

        layoutEditProfile.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Edit Profile coming soon", Toast.LENGTH_SHORT).show());

        layoutChangePassword.setOnClickListener(v ->
            Toast.makeText(requireContext(), "Change Password coming soon", Toast.LENGTH_SHORT).show());

        switchNotifications.setOnCheckedChangeListener((button, isChecked) ->
            Toast.makeText(requireContext(),
                isChecked ? "Notifications enabled" : "Notifications disabled",
                Toast.LENGTH_SHORT).show());

        layoutLogout.setOnClickListener(v -> showLogoutConfirmation());
        }

        private void openMyRentalsTab(int tab) {
        Bundle args = new Bundle();
        args.putInt("initialTab", tab);
        androidx.navigation.Navigation.findNavController(requireView())
            .navigate(R.id.myRentalsFragment, args);
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void performLogout() {
        sessionManager.logout();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}