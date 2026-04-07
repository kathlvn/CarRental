package com.mobcom.carrental.fragments.provider;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
    private SessionManager sessionManager;

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

        sessionManager = new SessionManager(requireContext());

        bindViews(view);
        populateIdentity();
        populateTrustStatus();
        populatePerformance();
        setupListingsActions();
        setupSettings();
        bindPreferences();
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
        String name  = sessionManager.getName().isEmpty() ? "Provider" : sessionManager.getName();
        String email = sessionManager.getEmail().isEmpty() ? "No email" : sessionManager.getEmail();

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
        layoutEditProfile.setOnClickListener(v -> showEditProfileDialog());

        layoutChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        switchNotifications.setOnCheckedChangeListener((btn, isChecked) -> {
            sessionManager.setNotificationsEnabledForRole(SessionManager.ROLE_PROVIDER, isChecked);
            Toast.makeText(requireContext(),
                    isChecked ? "Notifications enabled" : "Notifications disabled",
                    Toast.LENGTH_SHORT).show();
        });

        layoutLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void bindPreferences() {
        switchNotifications.setChecked(
                sessionManager.isNotificationsEnabledForRole(SessionManager.ROLE_PROVIDER)
        );
    }

    private void showEditProfileDialog() {
        String[] options = {"Edit Name", "Edit Email", "Edit Phone", "Edit Location"};
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Profile")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        promptInput("Update Name", tvName.getText().toString(), false, value -> {
                            tvName.setText(value);
                            tvAvatar.setText(getInitials(value));
                            sessionManager.updateProfile(value, tvEmail.getText().toString());
                        });
                    } else if (which == 1) {
                        promptInput("Update Email", tvEmail.getText().toString(), false, value -> {
                            tvEmail.setText(value);
                            sessionManager.updateProfile(tvName.getText().toString(), value);
                        });
                    } else if (which == 2) {
                        promptInput("Update Phone", tvPhone.getText().toString(), false, tvPhone::setText);
                    } else {
                        promptInput("Update Location", tvLocation.getText().toString(), false, tvLocation::setText);
                    }
                })
                .show();
    }

    private void showChangePasswordDialog() {
        promptInput("Change Password", "", true, value -> {
            if (value.length() < 6) {
                Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show();
        });
    }

    private interface OnInputSaved {
        void onSaved(String value);
    }

    private void promptInput(String title, String initialValue, boolean isPassword, OnInputSaved callback) {
        EditText input = new EditText(requireContext());
        input.setText(initialValue);
        input.setSelection(input.getText().length());
        input.setSingleLine(true);
        input.setPadding(48, 28, 48, 28);
        if (isPassword) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            input.setHint("Enter new password");
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String value = input.getText().toString().trim();
                    if (value.isEmpty()) {
                        Toast.makeText(requireContext(), "Value cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    callback.onSaved(value);
                })
                .setNegativeButton("Cancel", null)
                .show();
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