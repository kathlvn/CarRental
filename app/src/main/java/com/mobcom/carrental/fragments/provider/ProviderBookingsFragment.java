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
import com.mobcom.carrental.utils.BookingService;
import com.mobcom.carrental.utils.NotificationStore;
import com.mobcom.carrental.utils.SessionManager;
import com.mobcom.carrental.database.entities.BookingEntity;
import com.mobcom.carrental.database.entities.CarEntity;
import com.mobcom.carrental.database.AppDatabase;
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
        loadBookingsFromDatabase();
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

    private void loadBookingsFromDatabase() {
        SessionManager sessionManager = new SessionManager(requireContext());
        String providerId = sessionManager.getEmail();

        AppDatabase db = AppDatabase.getInstance(requireContext());
        java.util.List<BookingEntity> bookingEntities = db.bookingDao().getProviderBookings(providerId);

        allBookings.clear();
        for (BookingEntity booking : bookingEntities) {
            // Get car details
            CarEntity car = db.carDao().getCarById(booking.carId);
            String carName = car != null ? car.name : "Unknown Car";
            String carImage = car != null ? car.imageUrl : "";

            // Map booking status
            ProviderBooking.Status status = mapBookingStatus(booking.status);

            ProviderBooking pb = new ProviderBooking(
                    booking.bookingId,
                    carName,
                    booking.carPlateNumber != null ? booking.carPlateNumber : (car != null ? car.plateNumber : ""),
                    carImage,
                    booking.customerName != null ? booking.customerName : "Customer",
                    booking.customerPhone != null ? booking.customerPhone : "",
                    booking.pickupLocation != null ? booking.pickupLocation : "",
                    booking.startDate,
                    booking.endDate,
                    booking.totalDays,
                    booking.totalAmount,
                    status,
                    getTimeAgo(booking.createdAt)
            );
            allBookings.add(pb);
        }
    }

    private ProviderBooking.Status mapBookingStatus(String status) {
        if (status == null) return ProviderBooking.Status.PENDING;
        switch (status) {
            case "CONFIRMED": return ProviderBooking.Status.CONFIRMED;
            case "ACTIVE": return ProviderBooking.Status.ACTIVE;
            case "COMPLETED": return ProviderBooking.Status.COMPLETED;
            case "REJECTED": return ProviderBooking.Status.REJECTED;
            case "CANCELLED": return ProviderBooking.Status.CANCELLED;
            default: return ProviderBooking.Status.PENDING;
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

    // ── OnBookingActionListener ───────────────────────────────────────────────

    @Override
    public void onAccept(ProviderBooking booking) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Accept Booking?")
                .setMessage("Confirm booking from " + booking.getCustomerName()
                        + " for " + booking.getCarName()
                        + " (" + booking.getStartDate() + " → " + booking.getEndDate() + ")")
                .setPositiveButton("Accept", (dialog, which) -> {
                    // Save to database
                    BookingService.acceptBooking(booking.getBookingId());

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
                    // Save to database
                    BookingService.rejectBooking(booking.getBookingId(), reasons[which]);

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