package com.mobcom.carrental.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mobcom.carrental.R;
import com.mobcom.carrental.models.AdminProvider;

public class AdminProviderDetailFragment extends Fragment {

    private AdminProvider provider;

    private TextView tvAvatar, tvName, tvEmail, tvTrustBadge;
    private TextView tvPhone, tvLocation, tvMemberSince;
    private TextView tvTotalListings, tvApprovedListings;
    private TextView tvTotalBookings, tvAvgRating;
    private TextView tvCancellationRate, tvTotalReports;
    private TextView tvViolationWarning;
    private MaterialButton btnSetTrusted, btnSetProbation, btnFlag, btnSuspend;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_provider_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupToolbar(view);

        if (getArguments() != null) {
            provider = (AdminProvider) getArguments().getSerializable("provider");
        }

        if (provider != null) {
            populateData();
            setupTrustButtons();
        }
    }

    private void bindViews(View view) {
        tvAvatar            = view.findViewById(R.id.tvAvatar);
        tvName              = view.findViewById(R.id.tvName);
        tvEmail             = view.findViewById(R.id.tvEmail);
        tvTrustBadge        = view.findViewById(R.id.tvTrustBadge);
        tvPhone             = view.findViewById(R.id.tvPhone);
        tvLocation          = view.findViewById(R.id.tvLocation);
        tvMemberSince       = view.findViewById(R.id.tvMemberSince);
        tvTotalListings     = view.findViewById(R.id.tvTotalListings);
        tvApprovedListings  = view.findViewById(R.id.tvApprovedListings);
        tvTotalBookings     = view.findViewById(R.id.tvTotalBookings);
        tvAvgRating         = view.findViewById(R.id.tvAvgRating);
        tvCancellationRate  = view.findViewById(R.id.tvCancellationRate);
        tvTotalReports      = view.findViewById(R.id.tvTotalReports);
        tvViolationWarning  = view.findViewById(R.id.tvViolationWarning);
        btnSetTrusted       = view.findViewById(R.id.btnSetTrusted);
        btnSetProbation     = view.findViewById(R.id.btnSetProbation);
        btnFlag             = view.findViewById(R.id.btnFlag);
        btnSuspend          = view.findViewById(R.id.btnSuspend);
    }

    private void setupToolbar(View view) {
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());
    }

    private void populateData() {
        tvAvatar.setText(provider.getInitials());
        tvName.setText(provider.getName());
        tvEmail.setText(provider.getEmail());
        tvPhone.setText(provider.getPhone());
        tvLocation.setText(provider.getLocation());
        tvMemberSince.setText(provider.getMemberSince());

        tvTotalListings.setText(String.valueOf(provider.getTotalListings()));
        tvApprovedListings.setText(String.valueOf(provider.getApprovedListings()));
        tvTotalBookings.setText(String.valueOf(provider.getTotalBookings()));
        tvAvgRating.setText(provider.getAverageRating() > 0
                ? "⭐ " + provider.getAverageRating() : "No ratings yet");
        tvCancellationRate.setText(
                Math.round(provider.getCancellationRate() * 100) + "%");
        tvTotalReports.setText(String.valueOf(provider.getTotalReports()));

        updateTrustBadge();

        if (provider.hasViolations()) {
            tvViolationWarning.setVisibility(View.VISIBLE);
            tvViolationWarning.setText(buildViolationText());
        }
    }

    private void updateTrustBadge() {
        switch (provider.getTrustLevel()) {
            case TRUSTED:
                tvTrustBadge.setText("✓ Trusted");
                tvTrustBadge.getBackground()
                        .setTint(android.graphics.Color.parseColor("#2E7D32"));
                break;
            case FLAGGED:
                tvTrustBadge.setText("⚠ Flagged");
                tvTrustBadge.getBackground()
                        .setTint(android.graphics.Color.parseColor("#E53935"));
                break;
            case SUSPENDED:
                tvTrustBadge.setText("✕ Suspended");
                tvTrustBadge.getBackground()
                        .setTint(android.graphics.Color.parseColor("#B71C1C"));
                break;
            default:
                tvTrustBadge.setText("⏳ Probation");
                tvTrustBadge.getBackground()
                        .setTint(android.graphics.Color.parseColor("#FF9800"));
                break;
        }
    }

    private void setupTrustButtons() {
        btnSetTrusted.setOnClickListener(v ->
                confirmTrustChange("Set as Trusted?",
                        "This provider's future listings will be auto-published.",
                        AdminProvider.TrustLevel.TRUSTED));

        btnSetProbation.setOnClickListener(v ->
                confirmTrustChange("Set to Probation?",
                        "This provider's listings will require manual review again.",
                        AdminProvider.TrustLevel.PROBATION));

        btnFlag.setOnClickListener(v ->
                confirmTrustChange("Flag Provider?",
                        "All listings will be put under manual review and "
                                + "the provider will be notified.",
                        AdminProvider.TrustLevel.FLAGGED));

        btnSuspend.setOnClickListener(v ->
                confirmTrustChange("Suspend Provider?",
                        "All listings will be hidden and the provider "
                                + "will not be able to accept bookings.",
                        AdminProvider.TrustLevel.SUSPENDED));
    }

    private void confirmTrustChange(String title, String message,
                                    AdminProvider.TrustLevel newLevel) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    provider.setTrustLevel(newLevel);
                    updateTrustBadge();
                    // TODO: API call to update trust level
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String buildViolationText() {
        StringBuilder sb = new StringBuilder("⚠ Active violations: ");
        if (provider.getTotalReports() >= 3)
            sb.append(provider.getTotalReports()).append(" reports  ");
        if (provider.getAverageRating() > 0 && provider.getAverageRating() < 2.5f)
            sb.append("Rating ").append(provider.getAverageRating()).append("  ");
        if (provider.getCancellationRate() > 0.30f)
            sb.append(Math.round(provider.getCancellationRate() * 100))
                    .append("% cancellation rate");
        return sb.toString().trim();
    }
}