package com.mobcom.carrental.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mobcom.carrental.R;
import com.mobcom.carrental.adapters.ModerationQueueAdapter;
import com.mobcom.carrental.adapters.RecentActivityAdapter;
import com.mobcom.carrental.adapters.RiskSignalAdapter;
import com.mobcom.carrental.models.ActivityEvent;
import com.mobcom.carrental.models.AdminListing;
import com.mobcom.carrental.models.AdminProvider;
import com.mobcom.carrental.models.AdminReport;
import com.mobcom.carrental.models.Booking;
import com.mobcom.carrental.models.ModerationQueueItem;
import com.mobcom.carrental.models.Rental;
import com.mobcom.carrental.models.RiskSignal;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardFragment extends Fragment {

    private TextView tvUnresolvedReportsCount, tvPendingListingsCount,
            tvFlaggedProvidersCount, tvActiveDisputesCount;
    private RecyclerView rvModerationQueue, rvRiskSignals, rvRecentActivity;
    private LinearLayout layoutNoModeration, layoutNoRisks, layoutNoActivity;
    private ModerationQueueAdapter moderationAdapter;
    private RiskSignalAdapter riskSignalAdapter;
    private RecentActivityAdapter activityAdapter;

    // Data collections
    private List<AdminProvider> allProviders = new ArrayList<>();
    private List<AdminListing> allListings = new ArrayList<>();
    private List<AdminReport> allReports = new ArrayList<>();
    private List<Booking> allBookings = new ArrayList<>();
        private List<ModerationQueueItem> moderationQueueItems = new ArrayList<>();
        private List<RiskSignal> riskSignals = new ArrayList<>();
    private List<ActivityEvent> recentActivities = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViewReferences(view);
                setupRecyclerViews();
        loadAllData();
        updateDashboard();
    }

    private void initializeViewReferences(@NonNull View view) {
        // Needs Attention TextViews
        tvUnresolvedReportsCount = view.findViewById(R.id.tvUnresolvedReportsCount);
        tvPendingListingsCount   = view.findViewById(R.id.tvPendingListingsCount);
        tvFlaggedProvidersCount  = view.findViewById(R.id.tvFlaggedProvidersCount);
        tvActiveDisputesCount    = view.findViewById(R.id.tvActiveDisputesCount);

        rvModerationQueue = view.findViewById(R.id.rvModerationQueue);
        rvRiskSignals     = view.findViewById(R.id.rvRiskSignals);
        rvRecentActivity = view.findViewById(R.id.rvRecentActivity);

        layoutNoModeration = view.findViewById(R.id.layoutNoModeration);
        layoutNoRisks = view.findViewById(R.id.layoutNoRisks);
        layoutNoActivity = view.findViewById(R.id.layoutNoActivity);

        // Set click listeners for cards
        CardView cardUnresolvedReports = view.findViewById(R.id.cardUnresolvedReports);
        CardView cardPendingListings   = view.findViewById(R.id.cardPendingListings);
        CardView cardFlaggedProviders  = view.findViewById(R.id.cardFlaggedProviders);

        cardUnresolvedReports.setOnClickListener(v -> navigateToReports());
        cardPendingListings.setOnClickListener(v -> navigateToListings());
        cardFlaggedProviders.setOnClickListener(v -> navigateToProviders());
    }

        private void setupRecyclerViews() {
                moderationAdapter = new ModerationQueueAdapter(
                                requireContext(),
                                new ArrayList<>(),
                                new ModerationQueueAdapter.OnQueueActionListener() {
                                        @Override
                                        public void onApprove(ModerationQueueItem item) {
                                                moderationQueueItems.remove(item);
                                                updateModerationQueue();
                                        }

                                        @Override
                                        public void onReject(ModerationQueueItem item) {
                                                moderationQueueItems.remove(item);
                                                updateModerationQueue();
                                        }

                                        @Override
                                        public void onReview(ModerationQueueItem item) {
                                                // Reserved for future detailed review flow.
                                        }
                                });
                rvModerationQueue.setLayoutManager(new LinearLayoutManager(requireContext()));
                rvModerationQueue.setNestedScrollingEnabled(false);
                rvModerationQueue.setAdapter(moderationAdapter);

                riskSignalAdapter = new RiskSignalAdapter(requireContext(), new ArrayList<>());
                rvRiskSignals.setLayoutManager(new LinearLayoutManager(requireContext()));
                rvRiskSignals.setNestedScrollingEnabled(false);
                rvRiskSignals.setAdapter(riskSignalAdapter);

        activityAdapter = new RecentActivityAdapter(requireContext(), new ArrayList<>());
        rvRecentActivity.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecentActivity.setNestedScrollingEnabled(false);
        rvRecentActivity.setAdapter(activityAdapter);
    }

    private void loadAllData() {
        // Load from database instead of dummy data
        loadDummyModerationQueue();
        loadDummyRiskSignals();
        generateRecentActivities();
    }

    private void updateDashboard() {
        updateNeedsAttention();
                updateModerationQueue();
                updateRiskSignals();
        updateActivityFeed();
    }

    // ==================== Needs Attention ====================

    private void updateNeedsAttention() {
        // Unresolved reports (OPEN + ESCALATED)
        long unresolvedReports = allReports.stream()
                .filter(r -> r.getStatus() == AdminReport.Status.OPEN ||
                           r.getStatus() == AdminReport.Status.ESCALATED)
                .count();
        tvUnresolvedReportsCount.setText(String.valueOf(unresolvedReports));

        // Pending listings
        long pendingListings = allListings.stream()
                .filter(l -> l.getStatus() == AdminListing.Status.PENDING_REVIEW)
                .count();
        tvPendingListingsCount.setText(String.valueOf(pendingListings));

        // Flagged providers
        long flaggedProviders = allProviders.stream()
                .filter(p -> p.getTrustLevel() == AdminProvider.TrustLevel.FLAGGED ||
                           p.getTrustLevel() == AdminProvider.TrustLevel.SUSPENDED)
                .count();
        tvFlaggedProvidersCount.setText(String.valueOf(flaggedProviders));

        // Active disputes are currently tracked as escalated reports.
        long activeDisputes = allReports.stream()
                .filter(r -> r.getStatus() == AdminReport.Status.ESCALATED)
                .count();
        tvActiveDisputesCount.setText(String.valueOf(activeDisputes));
    }

        private void updateModerationQueue() {
                if (moderationQueueItems.isEmpty()) {
                        rvModerationQueue.setVisibility(View.GONE);
                        layoutNoModeration.setVisibility(View.VISIBLE);
                } else {
                        rvModerationQueue.setVisibility(View.VISIBLE);
                        layoutNoModeration.setVisibility(View.GONE);
                        moderationAdapter.updateList(moderationQueueItems);
                }
        }

        private void updateRiskSignals() {
                if (riskSignals.isEmpty()) {
                        rvRiskSignals.setVisibility(View.GONE);
                        layoutNoRisks.setVisibility(View.VISIBLE);
                } else {
                        rvRiskSignals.setVisibility(View.VISIBLE);
                        layoutNoRisks.setVisibility(View.GONE);
                        riskSignalAdapter.updateList(riskSignals);
                }
        }

    // ==================== Recent Activity ====================

    private void updateActivityFeed() {
        if (recentActivities.isEmpty()) {
            rvRecentActivity.setVisibility(View.GONE);
            layoutNoActivity.setVisibility(View.VISIBLE);
        } else {
            rvRecentActivity.setVisibility(View.VISIBLE);
            layoutNoActivity.setVisibility(View.GONE);
            activityAdapter.updateList(recentActivities);
        }
    }

    // ==================== Navigation ====================

    private void navigateToReports() {
        // Navigate to Admin Reports Fragment
        if (getParentFragment() != null) {
            getParentFragment().getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.admin_nav_host_fragment,
                            new AdminReportsFragment())
                    .commit();
        }
    }

    private void navigateToListings() {
        // Navigate to Admin Listings Fragment
        if (getParentFragment() != null) {
            getParentFragment().getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.admin_nav_host_fragment,
                            new AdminListingsFragment())
                    .commit();
        }
    }

    private void navigateToProviders() {
        // Navigate to Admin Providers Fragment
        if (getParentFragment() != null) {
            getParentFragment().getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.admin_nav_host_fragment,
                            new AdminProvidersFragment())
                    .commit();
        }
    }

    // ==================== Dummy Data Loading ====================

    private void loadDummyProviders() {
        allProviders.add(new AdminProvider(
                "P001", "Juan dela Cruz", "juan@email.com",
                "+63 912 345 6789", "Bacolod City", "January 2025",
                AdminProvider.TrustLevel.PROBATION,
                2, 1, 3, 2, 0, 4.5f, 3000, 0.10f, ""));

        allProviders.add(new AdminProvider(
                "P002", "Maria Santos", "maria@email.com",
                "+63 917 654 3210", "Bacolod City", "February 2025",
                AdminProvider.TrustLevel.TRUSTED,
                5, 4, 12, 11, 0, 4.8f, 18000, 0.05f, ""));

        allProviders.add(new AdminProvider(
                "P003", "Pedro Reyes", "pedro@email.com",
                "+63 998 111 2222", "Silay City", "March 2025",
                AdminProvider.TrustLevel.FLAGGED,
                3, 2, 8, 5, 4, 2.1f, 6000, 0.35f, ""));

        allProviders.add(new AdminProvider(
                "P004", "Ana Garcia", "ana@email.com",
                "+63 921 999 8888", "Talisay City", "March 2025",
                AdminProvider.TrustLevel.SUSPENDED,
                1, 0, 2, 0, 6, 1.5f, 0, 0.80f, ""));
    }

    private void loadDummyListings() {
        allListings.add(new AdminListing(
                "L001", "Honda Civic", "ABC-2024", "",
                "Sedan", "Automatic", "Gasoline", 5, 1500,
                "Bacolod City", "OR123", "CR123",
                "P001", "Juan dela Cruz", "juan@email.com",
                1, 0, 4.5f, "PROBATION",
                AdminListing.Status.PENDING_REVIEW, "Today 10:30 AM"));

        allListings.add(new AdminListing(
                "L002", "Toyota Innova", "ABC-2025", "",
                "Van", "Automatic", "Diesel", 7, 2000,
                "Bacolod City", "OR124", "CR124",
                "P002", "Maria Santos", "maria@email.com",
                4, 0, 4.8f, "TRUSTED",
                AdminListing.Status.APPROVED, "Yesterday 2:15 PM"));

        allListings.add(new AdminListing(
                "L003", "Nissan Altis", "ABC-2026", "",
                "Sedan", "Manual", "Gasoline", 5, 1200,
                "Silay City", "OR125", "CR125",
                "P003", "Pedro Reyes", "pedro@email.com",
                2, 4, 2.1f, "FLAGGED",
                AdminListing.Status.PENDING_REVIEW, "2 hours ago"));
    }

    private void loadDummyReports() {
        allReports.add(new AdminReport(
                "R001", AdminReport.Category.SCAM, AdminReport.Severity.HIGH,
                AdminReport.Status.OPEN,
                "Vehicle condition not as advertised",
                "Rented a car that had significant damage not mentioned in listing",
                "John Customer", "Juan dela Cruz", "P001", "Today 09:00 AM"));

        allReports.add(new AdminReport(
                "R002", AdminReport.Category.NO_SHOW, AdminReport.Severity.MEDIUM,
                AdminReport.Status.ESCALATED,
                "Customer failed to show up",
                "Booking was confirmed but customer never appeared",
                "Maria Santos", "Jane Renter", "C001", "Yesterday 3:00 PM"));

        allReports.add(new AdminReport(
                "R003", AdminReport.Category.OVERCHARGING, AdminReport.Severity.LOW,
                AdminReport.Status.RESOLVED,
                "Unexpected additional charges",
                "Was charged for fuel and late return fee",
                "Pedro Reyes", "Bob Customer", "C002", "3 days ago"));
    }

    private void loadDummyBookings() {
        Booking booking1 = new Booking(
                "B001", "L001", "Honda Civic", "",
                "ABC-2024", "Juan dela Cruz", "P001",
                "Bacolod City", "2025-04-08", "2025-04-10",
                2, 1500, "", Booking.PaymentMethod.ONLINE);
        booking1.setStatus(Rental.Status.CONFIRMED);
        allBookings.add(booking1);

        Booking booking2 = new Booking(
                "B002", "L002", "Toyota Innova", "",
                "ABC-2025", "Maria Santos", "P002",
                "Bacolod City", "2025-04-10", "2025-04-15",
                5, 2000, "", Booking.PaymentMethod.ONLINE);
        booking2.setStatus(Rental.Status.ACTIVE);
        allBookings.add(booking2);

        Booking booking3 = new Booking(
                "B003", "L003", "Nissan Altis", "",
                "ABC-2026", "Pedro Reyes", "P003",
                "Silay City", "2025-04-05", "2025-04-07",
                2, 1200, "", Booking.PaymentMethod.ONLINE);
        booking3.setStatus(Rental.Status.COMPLETED);
        allBookings.add(booking3);

        Booking booking4 = new Booking(
                "B004", "L001", "Honda Civic", "",
                "ABC-2024", "Juan dela Cruz", "P001",
                "Bacolod City", "2025-04-15", "2025-04-18",
                3, 1500, "", Booking.PaymentMethod.ONLINE);
        booking4.setStatus(Rental.Status.PENDING);
        allBookings.add(booking4);
    }

    private void generateRecentActivities() {
        recentActivities.clear();

        // Keep activity concise and low priority in the dashboard.
        recentActivities.add(new ActivityEvent(
                "A_L123",
                ActivityEvent.Type.LISTING_SUBMITTED,
                "New listing submitted",
                "John D. submitted Listing #123",
                "John D.",
                "L123",
                "10m",
                "📝"
        ));

        recentActivities.add(new ActivityEvent(
                "A_B102",
                ActivityEvent.Type.BOOKING_CREATED,
                "Booking cancelled",
                "User #102 cancelled a booking",
                "User #102",
                "B102",
                "35m",
                "❌"
        ));

        recentActivities.add(new ActivityEvent(
                "A_R55",
                ActivityEvent.Type.REPORT_FILED,
                "Report filed",
                "New report submitted on Listing #55",
                "System",
                "R55",
                "1h",
                "📋"
        ));
    }

    private void loadDummyModerationQueue() {
        moderationQueueItems.clear();

        moderationQueueItems.add(new ModerationQueueItem(
                "MQ001",
                ModerationQueueItem.ItemType.LISTING,
                ModerationQueueItem.Priority.MEDIUM,
                "Listing #123",
                "Verify documents and approve/reject listing",
                "John D.",
                "12m ago",
                "L123"
        ));

        moderationQueueItems.add(new ModerationQueueItem(
                "MQ002",
                ModerationQueueItem.ItemType.REPORT,
                ModerationQueueItem.Priority.HIGH,
                "Report #456",
                "Review report details and resolve case",
                "User #455",
                "28m ago",
                "R456"
        ));

        moderationQueueItems.add(new ModerationQueueItem(
                "MQ003",
                ModerationQueueItem.ItemType.PROVIDER,
                ModerationQueueItem.Priority.MEDIUM,
                "Provider #789",
                "Provider profile requires manual review",
                "System",
                "1h ago",
                "P789"
        ));
    }

    private void loadDummyRiskSignals() {
        riskSignals.clear();

        riskSignals.add(new RiskSignal(
                "RS001",
                RiskSignal.Severity.HIGH,
                "High cancellation rate",
                "Provider X reached a 40% cancellation rate",
                "Provider X",
                "PX",
                "40% ↑",
                "20m ago",
                false
        ));

        riskSignals.add(new RiskSignal(
                "RS002",
                RiskSignal.Severity.MEDIUM,
                "Low listing rating",
                "Listing Y dropped to a 1.8 star rating",
                "Listing Y",
                "LY",
                "1.8★",
                "42m ago",
                false
        ));

        riskSignals.add(new RiskSignal(
                "RS003",
                RiskSignal.Severity.CRITICAL,
                "Frequent user reports",
                "User Z was reported 3 times today",
                "User Z",
                "UZ",
                "3 reports",
                "1h ago",
                false
        ));
    }
}