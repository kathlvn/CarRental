package com.mobcom.carrental.fragments.provider;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobcom.carrental.R;
import com.mobcom.carrental.adapters.ProviderBookingAdapter;
import com.mobcom.carrental.models.ProviderBooking;
import com.mobcom.carrental.utils.SessionManager;
import com.mobcom.carrental.database.AppDatabase;
import com.mobcom.carrental.database.entities.BookingEntity;
import com.mobcom.carrental.database.entities.CarEntity;
import com.mobcom.carrental.database.entities.RentalEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ProviderDashboardFragment extends Fragment
        implements ProviderBookingAdapter.OnBookingActionListener {

    private TextView tvGreeting, tvProviderName, tvNotifBadge;
    private TextView tvTotalEarnings, tvActiveRentals, tvPendingCount, tvTotalCars;
    private TextView tvCurrentMonth;
    private ImageButton btnPrevMonth, btnNextMonth;
    private TextView tvSeeAllBookings;
    private RecyclerView rvPendingBookings;
    private LinearLayout layoutNoPending;
    private GridLayout calendarGrid;

    private ProviderBookingAdapter adapter;
    private List<ProviderBooking> pendingBookings = new ArrayList<>();
    private Set<String> occupiedDates = new HashSet<>();

    private Calendar displayedMonth = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provider_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupGreeting();
        setupRecyclerView();
        // loadDummyData(); // Load from database instead
        updateStats();
        updatePendingList();
        buildCalendar();

        // Quick actions
        view.findViewById(R.id.btnAddCar).setOnClickListener(v -> {
            BottomNavigationView nav = requireActivity().findViewById(R.id.provider_bottom_nav);
            nav.setSelectedItemId(R.id.providerMyCarsFragment);
        });

        view.findViewById(R.id.btnViewBookings).setOnClickListener(v -> {
            BottomNavigationView nav = requireActivity().findViewById(R.id.provider_bottom_nav);
            nav.setSelectedItemId(R.id.providerBookingsFragment);
        });

        tvSeeAllBookings.setOnClickListener(v -> {
            BottomNavigationView nav = requireActivity().findViewById(R.id.provider_bottom_nav);
            nav.setSelectedItemId(R.id.providerBookingsFragment);
        });

        // Month navigation
        btnPrevMonth.setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, -1);
            buildCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, 1);
            buildCalendar();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh stats and pending bookings when returning to dashboard
        updateStats();
        updatePendingList();
    }

    private void bindViews(View view) {
        tvGreeting        = view.findViewById(R.id.tvGreeting);
        tvProviderName    = view.findViewById(R.id.tvProviderName);
        tvNotifBadge      = view.findViewById(R.id.tvNotifBadge);
        tvTotalEarnings   = view.findViewById(R.id.tvTotalEarnings);
        tvActiveRentals   = view.findViewById(R.id.tvActiveRentals);
        tvPendingCount    = view.findViewById(R.id.tvPendingCount);
        tvTotalCars       = view.findViewById(R.id.tvTotalCars);
        tvCurrentMonth    = view.findViewById(R.id.tvCurrentMonth);
        btnPrevMonth      = view.findViewById(R.id.btnPrevMonth);
        btnNextMonth      = view.findViewById(R.id.btnNextMonth);
        tvSeeAllBookings  = view.findViewById(R.id.tvSeeAllBookings);
        rvPendingBookings = view.findViewById(R.id.rvPendingBookings);
        layoutNoPending   = view.findViewById(R.id.layoutNoPending);
        calendarGrid      = view.findViewById(R.id.calendarGrid);
    }

    // ── Greeting ─────────────────────────────────────────────────────────────

    private void setupGreeting() {
        // Time-based greeting
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12)       greeting = "Good morning,";
        else if (hour < 17)  greeting = "Good afternoon,";
        else                 greeting = "Good evening,";
        tvGreeting.setText(greeting);

        // Provider name from session
        SessionManager session = new SessionManager(requireContext());
        tvProviderName.setText(session.getName().isEmpty()
                ? "Provider" : session.getName());
    }

    // ── RecyclerView ─────────────────────────────────────────────────────────

    private void setupRecyclerView() {
        adapter = new ProviderBookingAdapter(requireContext(), new ArrayList<>(), this);
        rvPendingBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPendingBookings.setNestedScrollingEnabled(false);
        rvPendingBookings.setAdapter(adapter);
    }

    // ── Dummy Data ────────────────────────────────────────────────────────────

    private void loadDummyData() {
        // Pending bookings
        pendingBookings.add(new ProviderBooking(
                "BK001", "Toyota Vios 2023", "ABC 1234",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/2023_Toyota_Vios_1.5_G_CVT_%28facelift%2C_white%29%2C_front_8.24.22.jpg/1280px-2023_Toyota_Vios_1.5_G_CVT_%28facelift%2C_white%29%2C_front_8.24.22.jpg",
                "Juan dela Cruz", "+63 912 345 6789",
                "SM City Bacolod", "Jun 10, 2025", "Jun 13, 2025",
                3, 4500, ProviderBooking.Status.PENDING, "2 hours ago"));

        pendingBookings.add(new ProviderBooking(
                "BK002", "Honda City 2022", "XYZ 5678",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ef/2021_Honda_City_1.0_V_Turbo_CVT_%28Philippines%29%2C_front_8.19.21.jpg/1280px-2021_Honda_City_1.0_V_Turbo_CVT_%28Philippines%29%2C_front_8.19.21.jpg",
                "Maria Santos", "+63 917 654 3210",
                "Robinsons Bacolod", "Jun 15, 2025", "Jun 17, 2025",
                2, 2400, ProviderBooking.Status.PENDING, "5 hours ago"));

        // Occupied dates for calendar (simulate booked date ranges)
        addOccupiedRange("2025-06-10", "2025-06-13");
        addOccupiedRange("2025-06-15", "2025-06-17");
        addOccupiedRange("2025-06-20", "2025-06-22");
    }

    private void addOccupiedRange(String startStr, String endStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar start = Calendar.getInstance();
            Calendar end   = Calendar.getInstance();
            start.setTime(sdf.parse(startStr));
            end.setTime(sdf.parse(endStr));

            while (!start.after(end)) {
                occupiedDates.add(sdf.format(start.getTime()));
                start.add(Calendar.DAY_OF_MONTH, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Stats ─────────────────────────────────────────────────────────────────

    private void updateStats() {
        SessionManager session = new SessionManager(requireContext());
        String providerId = session.getEmail();
        AppDatabase db = AppDatabase.getInstance(requireContext());

        // Get total cars
        List<CarEntity> cars = db.carDao().getCarsByProvider(providerId);
        tvTotalCars.setText(cars.size() + "");

        // Get bookings for this provider
        List<BookingEntity> bookings = db.bookingDao().getProviderBookings(providerId);

        // Calculate earnings from completed rentals
        double totalEarnings = 0;
        List<RentalEntity> rentals = db.rentalDao().getProviderRentals(providerId);
        for (RentalEntity rental : rentals) {
            if ("COMPLETED".equalsIgnoreCase(rental.status)) {
                totalEarnings += rental.totalCost;
            }
        }
        tvTotalEarnings.setText("₱" + String.format("%,.0f", totalEarnings));

        // Count active rentals (ACTIVE status)
        int activeCount = 0;
        for (RentalEntity rental : rentals) {
            if ("ACTIVE".equalsIgnoreCase(rental.status)) {
                activeCount++;
            }
        }
        tvActiveRentals.setText(activeCount + "");

        // Count pending bookings
        int pendingCount = 0;
        for (BookingEntity booking : bookings) {
            if ("PENDING".equalsIgnoreCase(booking.status)) {
                pendingCount++;
            }
        }
        tvPendingCount.setText(pendingCount + "");

        // Notification badge
        if (pendingCount > 0) {
            tvNotifBadge.setVisibility(View.VISIBLE);
            tvNotifBadge.setText(pendingCount + " new");
            tvNotifBadge.getBackground().setTint(Color.parseColor("#FF9800"));
        } else {
            tvNotifBadge.setVisibility(View.GONE);
        }
    }

    // ── Pending List ──────────────────────────────────────────────────────────

    private void updatePendingList() {
        SessionManager session = new SessionManager(requireContext());
        String providerId = session.getEmail();
        AppDatabase db = AppDatabase.getInstance(requireContext());

        pendingBookings.clear();
        List<BookingEntity> bookingEntities = db.bookingDao().getProviderBookings(providerId);

        for (BookingEntity booking : bookingEntities) {
            if ("PENDING".equalsIgnoreCase(booking.status)) {
                CarEntity car = db.carDao().getCarById(booking.carId);
                String carName = car != null ? car.name : "Unknown Car";
                String carImage = car != null ? car.imageUrl : "";

                ProviderBooking pb = new ProviderBooking(
                        booking.bookingId,
                        carName,
                        booking.carPlateNumber != null ? booking.carPlateNumber : "",
                        carImage,
                        booking.customerName != null ? booking.customerName : "Customer",
                        booking.customerPhone != null ? booking.customerPhone : "",
                        booking.pickupLocation != null ? booking.pickupLocation : "",
                        booking.startDate,
                        booking.endDate,
                        booking.totalDays,
                        booking.totalAmount,
                        ProviderBooking.Status.PENDING,
                        getTimeAgo(booking.createdAt)
                );
                pendingBookings.add(pb);
            }
        }

        if (pendingBookings.isEmpty()) {
            rvPendingBookings.setVisibility(View.GONE);
            layoutNoPending.setVisibility(View.VISIBLE);
        } else {
            rvPendingBookings.setVisibility(View.VISIBLE);
            layoutNoPending.setVisibility(View.GONE);
            adapter.updateList(pendingBookings);
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

    // ── Calendar ──────────────────────────────────────────────────────────────

    private void buildCalendar() {
        calendarGrid.removeAllViews();

        SimpleDateFormat monthFmt = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvCurrentMonth.setText(monthFmt.format(displayedMonth.getTime()));

        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Get first day of month
        Calendar cal = (Calendar) displayedMonth.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0=Sun
        int daysInMonth    = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        Calendar today = Calendar.getInstance();
        String todayStr = dateFmt.format(today.getTime());

        // Add empty cells for days before the 1st
        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarGrid.addView(makeEmptyCell());
        }

        // Add day cells
        for (int day = 1; day <= daysInMonth; day++) {
            cal.set(Calendar.DAY_OF_MONTH, day);
            String dateStr = dateFmt.format(cal.getTime());
            boolean isOccupied = occupiedDates.contains(dateStr);
            boolean isToday    = dateStr.equals(todayStr);
            calendarGrid.addView(makeDayCell(day, isOccupied, isToday));
        }
    }

    private View makeEmptyCell() {
        TextView tv = new TextView(requireContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width  = 0;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        tv.setLayoutParams(params);
        return tv;
    }

    private TextView makeDayCell(int day, boolean isOccupied, boolean isToday) {
        TextView tv = new TextView(requireContext());

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width      = 0;
        params.height     = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(2, 2, 2, 2);
        tv.setLayoutParams(params);

        tv.setText(String.valueOf(day));
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(0, 8, 0, 8);
        tv.setTextSize(12f);

        if (isOccupied) {
            tv.setBackgroundResource(R.drawable.bg_step_active);
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(11f);
        } else if (isToday) {
            tv.setTextColor(Color.parseColor("#1A237E"));
            tv.setTypeface(null, android.graphics.Typeface.BOLD);  // ← fixed
            tv.setBackgroundResource(R.drawable.bg_days_badge);
        } else {
            tv.setTextColor(Color.parseColor("#212121"));
        }

        return tv;
    }

    // ── OnBookingActionListener ───────────────────────────────────────────────

    @Override
    public void onAccept(ProviderBooking booking) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Accept Booking?")
                .setMessage("Confirm booking from " + booking.getCustomerName()
                        + " for " + booking.getCarName() + "?")
                .setPositiveButton("Accept", (dialog, which) -> {
                    booking.setStatus(ProviderBooking.Status.CONFIRMED);
                    pendingBookings.remove(booking);
                    updateStats();
                    updatePendingList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onReject(ProviderBooking booking) {
        String[] reasons = {
                "Car unavailable on those dates",
                "Customer requirements not met",
                "Scheduling conflict",
                "Other"
        };

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Reject Booking")
                .setItems(reasons, (dialog, which) -> {
                    booking.setStatus(ProviderBooking.Status.REJECTED);
                    pendingBookings.remove(booking);
                    updateStats();
                    updatePendingList();
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
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Booking #" + booking.getBookingId())
            .setMessage("Car: " + booking.getCarName()
                + "\nCustomer: " + booking.getCustomerName()
                + "\nDates: " + booking.getStartDate() + " to " + booking.getEndDate()
                + "\nPickup: " + booking.getPickupLocation()
                + "\nAmount: ₱" + String.format("%,.0f", booking.getTotalAmount()))
            .setPositiveButton("OK", null)
            .show();
    }
}