package com.mobcom.carrental.provider;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobcom.carrental.R;
import com.mobcom.carrental.WelcomeActivity;
import com.mobcom.carrental.utils.SessionManager;

public class ProviderProfileFragment extends Fragment {

    // Trust level constants
    private static final int TRUSTED_THRESHOLD = 3;

    // Views — Identity
    private TextView tvAvatar, tvName, tvEmail, tvMemberSince;
    private TextView tvPhone, tvLocation;

    // Views — Trust
    private TextView tvTrustBadge, tvApprovedListings;
    private TextView tvTrustProgress, tvTrustGuidance;
    private LinearProgressIndicator progressTrust;

    // Views — Performance
    private TextView tvBookingsCompleted, tvAvgRating, tvTotalEarnings;

    // Views — Listings
    private LinearLayout layoutManageListings, layoutAddCar;

    // Views — Settings
    private LinearLayout layoutEditProfile, layoutChangePassword, layoutLogout;
    private SwitchMaterial switchNotifications;

    // Dummy data — replace with real API later
    private int approvedListings  = 2;
    private int bookingsCompleted = 8;
    private float avgRating       = 4.7f;
    private double totalEarnings  = 12400;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provider_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        populateIdentity();
        populateTrustStatus();
        populatePerformance();
        setupListingsActions();
        setupSettings();
    }

    private void bindViews(View view) {
        tvAvatar             = view.findViewById(R.id.tvAvatar);
        tvName               = view.findViewById(R.id.tvName);
        tvEmail              = view.findViewById(R.id.tvEmail);
        tvMemberSince        = view.findViewById(R.id.tvMemberSince);
        tvPhone              = view.findViewById(R.id.tvPhone);
        tvLocation           = view.findViewById(R.id.tvLocation);
        tvTrustBadge         = view.findViewById(R.id.tvTrustBadge);
        tvApprovedListings   = view.findViewById(R.id.tvApprovedListings);
        progressTrust        = view.findViewById(R.id.progressTrust);
        tvTrustProgress      = view.findViewById(R.id.tvTrustProgress);
        tvTrustGuidance      = view.findViewById(R.id.tvTrustGuidance);
        tvBookingsCompleted  = view.findViewById(R.id.tvBookingsCompleted);
        tvAvgRating          = view.findViewById(R.id.tvAvgRating);
        tvTotalEarnings      = view.findViewById(R.id.tvTotalEarnings);
        layoutManageListings = view.findViewById(R.id.layoutManageListings);
        layoutAddCar         = view.findViewById(R.id.layoutAddCar);
        layoutEditProfile    = view.findViewById(R.id.layoutEditProfile);
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);
        layoutLogout         = view.findViewById(R.id.layoutLogout);
        switchNotifications  = view.findViewById(R.id.switchNotifications);
    }

    // ── Identity ─────────────────────────────────────────────────────────────

    private void populateIdentity() {
        SessionManager session = new SessionManager(requireContext());

        String name  = session.getName().isEmpty() ? "Provider" : session.getName();
        String email = session.getEmail().isEmpty() ? "No email" : session.getEmail();

        // Avatar initials
        String initials = getInitials(name);
        tvAvatar.setText(initials);

        tvName.setText(name);
        tvEmail.setText(email);
        tvMemberSince.setText("Member since April 2025");

        // Dummy — replace with real profile data
        tvPhone.setText("+63 912 345 6789");
        tvLocation.setText("Bacolod City, Negros Occidental");
    }

    private String getInitials(String name) {
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return String.valueOf(parts[0].charAt(0)).toUpperCase()
                    + String.valueOf(parts[1].charAt(0)).toUpperCase();
        } else if (parts.length == 1 && !parts[0].isEmpty()) {
            return String.valueOf(parts[0].charAt(0)).toUpperCase();
        }
        return "P";
    }

    // ── Trust Status ─────────────────────────────────────────────────────────

    private void populateTrustStatus() {
        // Determine trust level
        TrustLevel level = getTrustLevel();

        // Badge
        tvTrustBadge.setText(level.label);
        tvTrustBadge.getBackground().setTint(level.color);

        // Approved count
        tvApprovedListings.setText(approvedListings + " listings approved");

        // Progress bar (capped at TRUSTED_THRESHOLD)
        int progress = Math.min(approvedListings, TRUSTED_THRESHOLD);
        progressTrust.setMax(TRUSTED_THRESHOLD);
        progressTrust.setProgress(progress);

        // Progress text
        if (level == TrustLevel.TRUSTED || level == TrustLevel.FLAGGED
                || level == TrustLevel.SUSPENDED) {
            tvTrustProgress.setText("Trusted status achieved");
        } else {
            int remaining = TRUSTED_THRESHOLD - approvedListings;
            tvTrustProgress.setText(approvedListings + " / " + TRUSTED_THRESHOLD
                    + " listings approved — " + remaining + " more to become Trusted");
        }

        // Guidance
        tvTrustGuidance.setText(level.guidance);
    }

    private TrustLevel getTrustLevel() {
        // TODO: replace with real trust level from backend
        // For now derive from approvedListings count
        if (approvedListings >= TRUSTED_THRESHOLD) {
            return TrustLevel.TRUSTED;
        } else {
            return TrustLevel.PROBATION;
        }
    }

    private enum TrustLevel {
        PROBATION(
                "⏳ Probation",
                android.graphics.Color.parseColor("#FF9800"),
                "Your first 3 listings are manually reviewed by our admin team. "
                        + "Once approved, your future listings will be published automatically."
        ),
        TRUSTED(
                "✓ Trusted Provider",
                android.graphics.Color.parseColor("#2E7D32"),
                "Your listings are automatically published. "
                        + "Keep up the great service to maintain your Trusted status!"
        ),
        FLAGGED(
                "⚠ Flagged",
                android.graphics.Color.parseColor("#E53935"),
                "Your account has been flagged due to reports or low ratings. "
                        + "All listings are under manual review. Contact support to resolve."
        ),
        SUSPENDED(
                "✕ Suspended",
                android.graphics.Color.parseColor("#B71C1C"),
                "Your account has been suspended. "
                        + "All listings are hidden. Please contact support."
        );

        final String label;
        final int color;
        final String guidance;

        TrustLevel(String label, int color, String guidance) {
            this.label    = label;
            this.color    = color;
            this.guidance = guidance;
        }
    }

    // ── Performance ───────────────────────────────────────────────────────────

    private void populatePerformance() {
        tvBookingsCompleted.setText(String.valueOf(bookingsCompleted));
        tvAvgRating.setText("⭐ " + avgRating);
        tvTotalEarnings.setText("₱" + String.format("%,.0f", totalEarnings));
    }

    // ── Listings Actions ──────────────────────────────────────────────────────

    private void setupListingsActions() {
        layoutManageListings.setOnClickListener(v -> {
            BottomNavigationView nav =
                    requireActivity().findViewById(R.id.provider_bottom_nav);
            nav.setSelectedItemId(R.id.providerMyCarsFragment);
        });

        layoutAddCar.setOnClickListener(v -> {
            BottomNavigationView nav =
                    requireActivity().findViewById(R.id.provider_bottom_nav);
            nav.setSelectedItemId(R.id.providerMyCarsFragment);
            // TODO: trigger FAB click to open AddEditCarFragment directly
        });
    }

    // ── Settings ──────────────────────────────────────────────────────────────

    private void setupSettings() {
        layoutEditProfile.setOnClickListener(v -> {
            // TODO: navigate to EditProfileFragment
        });

        layoutChangePassword.setOnClickListener(v -> {
            // TODO: navigate to ChangePasswordFragment
        });

        switchNotifications.setOnCheckedChangeListener((btn, isChecked) -> {
            // TODO: save notification preference
        });

        layoutLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    new SessionManager(requireContext()).logout();
                    Intent intent = new Intent(requireContext(), WelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}