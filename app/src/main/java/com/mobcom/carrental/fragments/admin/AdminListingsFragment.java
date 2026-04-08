package com.mobcom.carrental.fragments.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.mobcom.carrental.R;
import com.mobcom.carrental.adapters.AdminListingAdapter;
import com.mobcom.carrental.models.AdminListing;
import com.mobcom.carrental.models.AdminProvider;
import com.mobcom.carrental.database.AppDatabase;
import com.mobcom.carrental.database.entities.CarEntity;
import com.mobcom.carrental.database.entities.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminListingsFragment extends Fragment
        implements AdminListingAdapter.OnListingActionListener {

    private TabLayout tabStatus;
    private MaterialButton btnRiskFilter;
    private RecyclerView rvListings;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyTitle, tvEmptySubtitle, tvPendingBadge;

    private AdminListingAdapter adapter;
    private List<AdminListing> allListings = new ArrayList<>();
    private int currentTab = 0;
    private String currentRiskFilter = "All"; // All, High, Medium, Low

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_listings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabStatus      = view.findViewById(R.id.tabStatus);
        btnRiskFilter  = view.findViewById(R.id.btnRiskFilter);
        rvListings     = view.findViewById(R.id.rvListings);
        layoutEmpty    = view.findViewById(R.id.layoutEmpty);
        tvEmptyTitle   = view.findViewById(R.id.tvEmptyTitle);
        tvEmptySubtitle= view.findViewById(R.id.tvEmptySubtitle);
        tvPendingBadge = view.findViewById(R.id.tvPendingBadge);

        setupTabs();
        setupRecyclerView();
        loadListingsFromDatabase();
        filterAndShow();
        updatePendingBadge();
        setupRiskFilter();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh listings when returning to this fragment
        loadListingsFromDatabase();
        filterAndShow();
        updatePendingBadge();
    }

    private void setupTabs() {
        tabStatus.addTab(tabStatus.newTab().setText("Pending"));
        tabStatus.addTab(tabStatus.newTab().setText("Approved"));
        tabStatus.addTab(tabStatus.newTab().setText("Rejected"));

        tabStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                filterAndShow();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new AdminListingAdapter(requireContext(), new ArrayList<>(), this);
        rvListings.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvListings.setNestedScrollingEnabled(false);
        rvListings.setAdapter(adapter);
    }

    private void setupRiskFilter() {
        btnRiskFilter.setOnClickListener(v -> {
            String[] options = {"All", "High Risk", "Medium Risk", "Low Risk"};
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Filter by Risk")
                    .setItems(options, (dialog, which) -> {
                        currentRiskFilter = options[which];
                        btnRiskFilter.setText("Risk: " + currentRiskFilter);
                        filterAndShow();
                    })
                    .show();
        });
    }

    private void filterAndShow() {
        List<AdminListing> filtered = allListings.stream()
                .filter(l -> {
                    // Status filter
                    switch (currentTab) {
                        case 0: return l.getStatus() == AdminListing.Status.PENDING_REVIEW;
                        case 1: return l.getStatus() == AdminListing.Status.APPROVED;
                        case 2: return l.getStatus() == AdminListing.Status.REJECTED;
                        default: return true;
                    }
                })
                .filter(l -> {
                    // Risk filter
                    switch (currentRiskFilter) {
                        case "High Risk":   return l.getRiskLevel() == AdminListing.RiskLevel.HIGH;
                        case "Medium Risk": return l.getRiskLevel() == AdminListing.RiskLevel.MEDIUM;
                        case "Low Risk":    return l.getRiskLevel() == AdminListing.RiskLevel.LOW;
                        default:            return true;
                    }
                })
                .collect(Collectors.toList());

        adapter.updateList(filtered);

        if (filtered.isEmpty()) {
            rvListings.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            setEmptyMessage();
        } else {
            rvListings.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void setEmptyMessage() {
        switch (currentTab) {
            case 0:
                tvEmptyTitle.setText("No pending listings");
                tvEmptySubtitle.setText("New listings from providers will appear here");
                break;
            case 1:
                tvEmptyTitle.setText("No approved listings");
                tvEmptySubtitle.setText("Listings you've approved will appear here");
                break;
            case 2:
                tvEmptyTitle.setText("No rejected listings");
                tvEmptySubtitle.setText("Listings you've rejected will appear here");
                break;
        }
    }

    private void updatePendingBadge() {
        long pendingCount = allListings.stream()
                .filter(l -> l.getStatus() == AdminListing.Status.PENDING_REVIEW)
                .count();

        if (pendingCount > 0) {
            tvPendingBadge.setVisibility(View.VISIBLE);
            tvPendingBadge.setText(pendingCount + " pending");
            tvPendingBadge.getBackground().setTint(Color.parseColor("#FF9800"));
        } else {
            tvPendingBadge.setVisibility(View.GONE);
        }
    }

    // ── Dummy Data ────────────────────────────────────────────────────────────

    private void loadListingsFromDatabase() {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        java.util.List<CarEntity> carEntities = db.carDao().getAllCars();

        android.util.Log.d("AdminListingsFragment", "Loaded " + carEntities.size() + " cars from database");

        allListings.clear();
        for (CarEntity car : carEntities) {
            android.util.Log.d("AdminListingsFragment", "Car: " + car.name + " | Status: " + car.approvalStatus + " | Provider: " + car.providerId);

            // Get provider info - providerId is actually the email
            UserEntity provider = db.userDao().getUserByEmail(car.providerId);
            if (provider == null) {
                android.util.Log.d("AdminListingsFragment", "  → Provider not found for email: " + car.providerId + ", skipping");
                continue;
            }

            // Map approval status to AdminListing.Status
            AdminListing.Status status = mapApprovalStatus(car.approvalStatus);

            AdminListing listing = new AdminListing(
                    car.carId,
                    car.name,
                    car.plateNumber,
                    car.imageUrl,
                    car.carType,
                    car.transmission,
                    car.fuelType,
                    car.seats,
                    car.pricePerDay,
                    car.location,
                    "", // OR Number (not stored in CarEntity)
                    "", // CR Number (not stored in CarEntity)
                    car.providerId,
                    provider.fullName,
                    provider.email,
                    0,  // reports (would need separate query)
                    0,  // totalListings (would need count query)
                    (float) provider.rating,
                    provider.verificationStatus != null ? provider.verificationStatus : "",
                    status,
                    getTimeAgo(car.createdAt)
            );

            // Set rejection reason if rejected
            if (status == AdminListing.Status.REJECTED && car.rejectionReason != null) {
                listing.setRejectionReason(car.rejectionReason);
            }

            allListings.add(listing);
        }

        android.util.Log.d("AdminListingsFragment", "Total listings after processing: " + allListings.size());

        // Fallback to empty state if no listings
        if (allListings.isEmpty()) {
            // Don't load dummy data - let empty state show
        }
    }

    private AdminListing.Status mapApprovalStatus(String status) {
        if (status == null) return AdminListing.Status.PENDING_REVIEW;
        switch (status) {
            case "APPROVED": return AdminListing.Status.APPROVED;
            case "REJECTED": return AdminListing.Status.REJECTED;
            default: return AdminListing.Status.PENDING_REVIEW;
        }
    }

    private String getTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diffMs = now - timestamp;
        long diffSecs = diffMs / 1000;
        long diffMins = diffSecs / 60;
        long diffHours = diffMins / 60;
        long diffDays = diffHours / 24;

        if (diffMins < 1) return "Just now";
        if (diffMins < 60) return diffMins + " min" + (diffMins > 1 ? "s" : "") + " ago";
        if (diffHours < 24) return diffHours + " hour" + (diffHours > 1 ? "s" : "") + " ago";
        if (diffDays < 7) return diffDays + " day" + (diffDays > 1 ? "s" : "") + " ago";
        return "Long ago";
    }

    private void loadDummyData() {
        // New provider, no OR/CR — HIGH risk
        allListings.add(new AdminListing(
                "LST001", "Toyota Vios 2023", "ABC 1234",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/2023_Toyota_Vios_1.5_G_CVT_%28facelift%2C_white%29%2C_front_8.24.22.jpg/1280px-2023_Toyota_Vios_1.5_G_CVT_%28facelift%2C_white%29%2C_front_8.24.22.jpg",
                "Sedan", "Automatic", "Gasoline", 5, 1500,
                "Bacolod City", "", "",
                "P001", "Juan dela Cruz", "juan@email.com",
                0, 0, 0f, "PROBATION",
                AdminListing.Status.PENDING_REVIEW, "2 hours ago"));

        // Has OR/CR, 1 report — MEDIUM risk
        allListings.add(new AdminListing(
                "LST002", "Honda City 2022", "XYZ 5678",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ef/2021_Honda_City_1.0_V_Turbo_CVT_%28Philippines%29%2C_front_8.19.21.jpg/1280px-2021_Honda_City_1.0_V_Turbo_CVT_%28Philippines%29%2C_front_8.19.21.jpg",
                "Sedan", "Manual", "Gasoline", 5, 1200,
                "Bacolod City", "OR123456", "CR654321",
                "P002", "Maria Santos", "maria@email.com",
                1, 1, 4.2f, "PROBATION",
                AdminListing.Status.PENDING_REVIEW, "5 hours ago"));

        // Trusted provider, has OR/CR — LOW risk
        allListings.add(new AdminListing(
                "LST003", "Mitsubishi Xpander 2022", "DEF 9012",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ae/2022_Mitsubishi_Xpander_GLS_Sport_AT_%28Philippines%29%2C_front_11.6.22.jpg/1280px-2022_Mitsubishi_Xpander_GLS_Sport_AT_%28Philippines%29%2C_front_11.6.22.jpg",
                "Van", "Automatic", "Gasoline", 7, 2000,
                "Bacolod City", "OR789012", "CR210987",
                "P003", "Pedro Reyes", "pedro@email.com",
                3, 0, 4.8f, "TRUSTED",
                AdminListing.Status.APPROVED, "1 day ago"));

        // Rejected listing
        AdminListing rejected = new AdminListing(
                "LST004", "Toyota Wigo 2021", "GHI 3456",
                "",
                "Hatchback", "Manual", "Gasoline", 4, 900,
                "Bacolod City", "", "",
                "P001", "Juan dela Cruz", "juan@email.com",
                0, 0, 0f, "PROBATION",
                AdminListing.Status.REJECTED, "3 days ago");
        rejected.setRejectionReason(
                "Missing OR/CR documents. Please resubmit with complete documentation.");
        allListings.add(rejected);
    }

    // ── OnListingActionListener ───────────────────────────────────────────────

    @Override
    public void onApprove(AdminListing listing) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Approve Listing?")
                .setMessage("Approve " + listing.getCarName()
                        + " from " + listing.getProviderName() + "?\n\n"
                        + (listing.getProviderApprovedListings() + 1 >= 3
                        ? "⭐ This will make the provider TRUSTED — future listings auto-publish."
                        : "Provider will have "
                        + (listing.getProviderApprovedListings() + 1)
                        + "/3 approved listings."))
                .setPositiveButton("Approve", (dialog, which) -> {
                    // Update listing in database
                    AppDatabase db = AppDatabase.getInstance(requireContext());
                    CarEntity car = db.carDao().getCarById(listing.getListingId());
                    if (car != null) {
                        car.approvalStatus = "APPROVED";
                        db.carDao().update(car);
                    }

                    listing.setStatus(AdminListing.Status.APPROVED);
                    listing.setProviderApprovedListings(listing.getProviderApprovedListings() + 1);
                    filterAndShow();
                    updatePendingBadge();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onReject(AdminListing listing) {
        String[] reasons = {
                "Missing OR/CR documents",
                "Invalid plate number",
                "Car does not match photos",
                "Provider account flagged",
                "Duplicate listing",
                "Other"
        };

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Reject Listing")
                .setMessage("Select a reason for rejection:")
                .setItems(reasons, (dialog, which) -> {
                    // Update listing in database
                    AppDatabase db = AppDatabase.getInstance(requireContext());
                    CarEntity car = db.carDao().getCarById(listing.getListingId());
                    if (car != null) {
                        car.approvalStatus = "REJECTED";
                        car.rejectionReason = reasons[which];
                        db.carDao().update(car);
                    }

                    listing.setStatus(AdminListing.Status.REJECTED);
                    listing.setRejectionReason(reasons[which]);
                    filterAndShow();
                    updatePendingBadge();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onViewProvider(AdminListing listing) {
        AdminProvider provider = new AdminProvider(
                listing.getProviderId(),
                listing.getProviderName(),
                listing.getProviderEmail(),
                "N/A",
                listing.getLocation(),
                "Unknown",
                mapTrustLevel(listing.getProviderTrustLevel()),
                listing.getProviderApprovedListings(),
                listing.getProviderApprovedListings(),
            0,
            0,
            listing.getProviderReports(),
            listing.getProviderRating(),
                0,
                0,
                ""
        );

        Bundle args = new Bundle();
        args.putSerializable("provider", provider);
        androidx.navigation.fragment.NavHostFragment
                .findNavController(AdminListingsFragment.this)
                .navigate(R.id.action_listings_to_providerDetail, args);
    }

    private AdminProvider.TrustLevel mapTrustLevel(String trustLevel) {
        if ("TRUSTED".equalsIgnoreCase(trustLevel)) {
            return AdminProvider.TrustLevel.TRUSTED;
        }
        if ("FLAGGED".equalsIgnoreCase(trustLevel)) {
            return AdminProvider.TrustLevel.FLAGGED;
        }
        if ("SUSPENDED".equalsIgnoreCase(trustLevel)) {
            return AdminProvider.TrustLevel.SUSPENDED;
        }
        return AdminProvider.TrustLevel.PROBATION;
    }
}