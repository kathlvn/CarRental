package com.mobcom.carrental.fragments.provider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.mobcom.carrental.R;
import com.mobcom.carrental.adapters.ProviderBookingAdapter;
import com.mobcom.carrental.models.ProviderBooking;
import com.mobcom.carrental.utils.NotificationStore;
import com.mobcom.carrental.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProviderBookingsFragment extends Fragment
        implements ProviderBookingAdapter.OnBookingActionListener {

    private TabLayout tabBookings;
    private RecyclerView rvBookings;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyTitle, tvEmptySubtitle;
    private ImageView btnOpenMessages;

    private ProviderBookingAdapter adapter;
    private List<ProviderBooking> allBookings = new ArrayList<>();
    private int currentTab = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provider_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabBookings    = view.findViewById(R.id.tabBookings);
        rvBookings     = view.findViewById(R.id.rvBookings);
        layoutEmpty    = view.findViewById(R.id.layoutEmpty);
        tvEmptyTitle   = view.findViewById(R.id.tvEmptyTitle);
        tvEmptySubtitle= view.findViewById(R.id.tvEmptySubtitle);
        btnOpenMessages = view.findViewById(R.id.btnOpenMessages);

        setupTabs();
        setupRecyclerView();
        // loadDummyData(); // Load from database instead
        filterAndShow(0);
        btnOpenMessages.setOnClickListener(v ->
            androidx.navigation.Navigation.findNavController(v)
                .navigate(R.id.providerMessagesFragment));
    }

    private void setupTabs() {
        tabBookings.addTab(tabBookings.newTab().setText("Pending"));
        tabBookings.addTab(tabBookings.newTab().setText("Active"));
        tabBookings.addTab(tabBookings.newTab().setText("Completed"));
        tabBookings.addTab(tabBookings.newTab().setText("All"));

        tabBookings.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                filterAndShow(currentTab);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new ProviderBookingAdapter(requireContext(), new ArrayList<>(), this);
        rvBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBookings.setNestedScrollingEnabled(false);
        rvBookings.setAdapter(adapter);
    }

    private void filterAndShow(int tab) {
        List<ProviderBooking> filtered;

        switch (tab) {
            case 0: // Pending
                filtered = allBookings.stream()
                        .filter(b -> b.getStatus() == ProviderBooking.Status.PENDING)
                        .collect(Collectors.toList());
                break;
            case 1: // Active
                filtered = allBookings.stream()
                        .filter(b -> b.getStatus() == ProviderBooking.Status.CONFIRMED
                                || b.getStatus() == ProviderBooking.Status.ACTIVE)
                        .collect(Collectors.toList());
                break;
            case 2: // Completed
                filtered = allBookings.stream()
                        .filter(b -> b.getStatus() == ProviderBooking.Status.COMPLETED
                                || b.getStatus() == ProviderBooking.Status.REJECTED
                                || b.getStatus() == ProviderBooking.Status.CANCELLED)
                        .collect(Collectors.toList());
                break;
            default: // All
                filtered = new ArrayList<>(allBookings);
                break;
        }

        adapter.updateList(filtered);

        if (filtered.isEmpty()) {
            rvBookings.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            setEmptyMessage(tab);
        } else {
            rvBookings.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void setEmptyMessage(int tab) {
        switch (tab) {
            case 0:
                tvEmptyTitle.setText("No pending requests");
                tvEmptySubtitle.setText("New booking requests will appear here");
                break;
            case 1:
                tvEmptyTitle.setText("No active bookings");
                tvEmptySubtitle.setText("Confirmed and ongoing rentals will appear here");
                break;
            case 2:
                tvEmptyTitle.setText("No completed bookings");
                tvEmptySubtitle.setText("Finished and rejected bookings will appear here");
                break;
            default:
                tvEmptyTitle.setText("No bookings yet");
                tvEmptySubtitle.setText("Booking requests from customers will appear here");
                break;
        }
    }

    // ── Dummy data ────────────────────────────────────────────────────────────

    private void loadDummyData() {
        allBookings.add(new ProviderBooking(
                "BK001", "Toyota Vios 2023", "ABC 1234",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/2023_Toyota_Vios_1.5_G_CVT_%28facelift%2C_white%29%2C_front_8.24.22.jpg/1280px-2023_Toyota_Vios_1.5_G_CVT_%28facelift%2C_white%29%2C_front_8.24.22.jpg",
                "Juan dela Cruz", "+63 912 345 6789",
                "SM City Bacolod", "Jun 10, 2025", "Jun 13, 2025",
                3, 4500, ProviderBooking.Status.PENDING, "2 hours ago"));

        allBookings.add(new ProviderBooking(
                "BK002", "Honda City 2022", "XYZ 5678",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ef/2021_Honda_City_1.0_V_Turbo_CVT_%28Philippines%29%2C_front_8.19.21.jpg/1280px-2021_Honda_City_1.0_V_Turbo_CVT_%28Philippines%29%2C_front_8.19.21.jpg",
                "Maria Santos", "+63 917 654 3210",
                "Robinsons Bacolod", "Jun 15, 2025", "Jun 17, 2025",
                2, 2400, ProviderBooking.Status.PENDING, "5 hours ago"));

        allBookings.add(new ProviderBooking(
                "BK003", "Toyota Vios 2023", "ABC 1234",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/2023_Toyota_Vios_1.5_G_CVT_%28facelift%2C_white%29%2C_front_8.24.22.jpg/1280px-2023_Toyota_Vios_1.5_G_CVT_%28facelift%2C_white%29%2C_front_8.24.22.jpg",
                "Pedro Reyes", "+63 998 111 2222",
                "Bacolod-Silay Airport", "May 1, 2025", "May 5, 2025",
                4, 6000, ProviderBooking.Status.ACTIVE, "2 days ago"));

        allBookings.add(new ProviderBooking(
                "BK004", "Honda City 2022", "XYZ 5678",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ef/2021_Honda_City_1.0_V_Turbo_CVT_%28Philippines%29%2C_front_8.19.21.jpg/1280px-2021_Honda_City_1.0_V_Turbo_CVT_%28Philippines%29%2C_front_8.19.21.jpg",
                "Ana Reyes", "+63 921 999 8888",
                "SM City Bacolod", "Apr 10, 2025", "Apr 12, 2025",
                2, 2400, ProviderBooking.Status.COMPLETED, "3 weeks ago"));
    }

    // ── OnBookingActionListener ───────────────────────────────────────────────

    @Override
    public void onAccept(ProviderBooking booking) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Accept Booking?")
                .setMessage("Confirm booking from " + booking.getCustomerName()
                        + " for " + booking.getCarName()
                        + " (" + booking.getStartDate() + " → " + booking.getEndDate() + ")")
                .setPositiveButton("Accept", (dialog, which) -> {
                    NotificationStore.pushBookingStatusNotification(
                        requireContext(),
                        SessionManager.ROLE_CUSTOMER,
                        booking.getBookingId(),
                        "Booking confirmed",
                        booking.getCarName() + " was accepted by the provider"
                    );

                    booking.setStatus(ProviderBooking.Status.CONFIRMED);
                    filterAndShow(currentTab);
                    Toast.makeText(requireContext(), "Booking accepted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onReject(ProviderBooking booking) {
        // Ask for rejection reason
        String[] reasons = {
                "Car unavailable on those dates",
                "Customer requirements not met",
                "Scheduling conflict",
                "Other"
        };

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Reject Booking")
                .setMessage("Select a reason for rejection:")
                .setItems(reasons, (dialog, which) -> {
                    NotificationStore.pushBookingStatusNotification(
                            requireContext(),
                            SessionManager.ROLE_CUSTOMER,
                            booking.getBookingId(),
                            "Booking rejected",
                            booking.getCarName() + " was rejected: " + reasons[which]
                    );

                    booking.setStatus(ProviderBooking.Status.REJECTED);
                    filterAndShow(currentTab);
                    Toast.makeText(requireContext(),
                            "Booking rejected: " + reasons[which],
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onContact(ProviderBooking booking) {
        Bundle args = new Bundle();
        args.putString("threadId", booking.getBookingId());
        args.putString("peerName", booking.getCustomerName());
        androidx.navigation.Navigation.findNavController(requireView())
                .navigate(R.id.providerMessagesFragment, args);
    }

    @Override
    public void onViewDetail(ProviderBooking booking) {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Booking #" + booking.getBookingId())
            .setMessage("Car: " + booking.getCarName()
                + "\nCustomer: " + booking.getCustomerName()
                + "\nDates: " + booking.getStartDate() + " to " + booking.getEndDate()
                + "\nPickup: " + booking.getPickupLocation()
                + "\nStatus: " + booking.getStatus().name())
            .setPositiveButton("OK", null)
            .show();
    }
}