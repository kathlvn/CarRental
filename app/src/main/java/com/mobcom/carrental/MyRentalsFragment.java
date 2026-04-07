// com/mobcom/carrental/fragments/MyRentalsFragment.java
package com.mobcom.carrental;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.mobcom.carrental.R;
import com.mobcom.carrental.adapters.RentalAdapter;
import com.mobcom.carrental.models.Rental;
import com.mobcom.carrental.models.RentalReview;
import com.mobcom.carrental.utils.NotificationStore;
import com.mobcom.carrental.utils.ReviewStore;
import com.mobcom.carrental.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyRentalsFragment extends Fragment implements RentalAdapter.OnRentalActionListener {

    private TabLayout tabLayout;
    private RecyclerView rvRentals;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyTitle, tvEmptySubtitle;
    private MaterialButton btnExplore;

    private RentalAdapter adapter;
    private List<Rental> allRentals = new ArrayList<>();
    private int currentTab = 0; // 0 = Active, 1 = Completed, 2 = Cancelled

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_rentals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout      = view.findViewById(R.id.tabLayout);
        rvRentals      = view.findViewById(R.id.rvRentals);
        layoutEmpty    = view.findViewById(R.id.layoutEmpty);
        tvEmptyTitle   = view.findViewById(R.id.tvEmptyTitle);
        tvEmptySubtitle= view.findViewById(R.id.tvEmptySubtitle);
        btnExplore     = view.findViewById(R.id.btnExplore);

        setupTabs();
        setupRecyclerView();
        loadDummyData();

        int initialTab = 0;
        Bundle args = getArguments();
        if (args != null) {
            int requestedTab = args.getInt("initialTab", 0);
            if (requestedTab >= 0 && requestedTab <= 2) {
                initialTab = requestedTab;
            }
        }

        filterAndShow(initialTab);
        TabLayout.Tab tab = tabLayout.getTabAt(initialTab);
        if (tab != null) {
            tab.select();
        }

        btnExplore.setOnClickListener(v -> {
            // Navigate to Explore tab (index 0 in bottom nav)
            com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                    requireActivity().findViewById(R.id.bottom_nav);
            bottomNav.setSelectedItemId(R.id.exploreFragment);
        });
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Active"));
        tabLayout.addTab(tabLayout.newTab().setText("Completed"));
        tabLayout.addTab(tabLayout.newTab().setText("Cancelled"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        adapter = new RentalAdapter(requireContext(), new ArrayList<>(), this);
        rvRentals.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRentals.setNestedScrollingEnabled(false);
        rvRentals.setAdapter(adapter);
    }

    private void filterAndShow(int tab) {
        List<Rental> filtered;

        if (tab == 0) {
            // Active = PENDING + CONFIRMED + ACTIVE
            filtered = allRentals.stream()
                    .filter(r -> r.getStatus() == Rental.Status.PENDING
                            || r.getStatus() == Rental.Status.CONFIRMED
                            || r.getStatus() == Rental.Status.ACTIVE)
                    .collect(Collectors.toList());
        } else if (tab == 1) {
            filtered = allRentals.stream()
                    .filter(r -> r.getStatus() == Rental.Status.COMPLETED)
                    .collect(Collectors.toList());
        } else {
            filtered = allRentals.stream()
                    .filter(r -> r.getStatus() == Rental.Status.CANCELLED)
                    .collect(Collectors.toList());
        }

        adapter.updateList(filtered);

        if (filtered.isEmpty()) {
            rvRentals.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            setEmptyMessage(tab);
        } else {
            rvRentals.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void setEmptyMessage(int tab) {
        switch (tab) {
            case 0:
                tvEmptyTitle.setText("No active rentals");
                tvEmptySubtitle.setText("Your upcoming and ongoing bookings will appear here");
                btnExplore.setVisibility(View.VISIBLE);
                break;
            case 1:
                tvEmptyTitle.setText("No completed rentals");
                tvEmptySubtitle.setText("Rentals you've finished will show up here");
                btnExplore.setVisibility(View.GONE);
                break;
            case 2:
                tvEmptyTitle.setText("No cancelled rentals");
                tvEmptySubtitle.setText("Cancelled bookings will appear here");
                btnExplore.setVisibility(View.GONE);
                break;
        }
    }

    // ── Dummy data (replace with real API/DB later) ──────────────────────────
    private void loadDummyData() {
        allRentals.add(new Rental("BK001", "Toyota Vios 2023",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/2023_Toyota_Vios_1.5_G_CVT_%28facelift%2C_white%29%2C_front_8.24.22.jpg/1280px-2023_Toyota_Vios_1.5_G_CVT_%28facelift%2C_white%29%2C_front_8.24.22.jpg",
                "ABC 1234", "SM City Bacolod, Reclamation Area",
                "Jun 10, 2025", "Jun 13, 2025", 3, 4500,
                Rental.Status.CONFIRMED, "Juan dela Cruz"));

        allRentals.add(new Rental("BK002", "Honda City 2022",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ef/2021_Honda_City_1.0_V_Turbo_CVT_%28Philippines%29%2C_front_8.19.21.jpg/1280px-2021_Honda_City_1.0_V_Turbo_CVT_%28Philippines%29%2C_front_8.19.21.jpg",
                "XYZ 5678", "Robinsons Place Bacolod",
                "May 1, 2025", "May 3, 2025", 2, 3000,
                Rental.Status.COMPLETED, "Maria Santos"));

        allRentals.add(new Rental("BK003", "Mitsubishi Xpander",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ae/2022_Mitsubishi_Xpander_GLS_Sport_AT_%28Philippines%29%2C_front_11.6.22.jpg/1280px-2022_Mitsubishi_Xpander_GLS_Sport_AT_%28Philippines%29%2C_front_11.6.22.jpg",
                "DEF 9012", "Bacolod-Silay Airport",
                "Apr 20, 2025", "Apr 22, 2025", 2, 5000,
                Rental.Status.CANCELLED, "Pedro Reyes"));
    }

    // ── RentalAdapter.OnRentalActionListener ─────────────────────────────────
    @Override
    public void onViewDetails(Rental rental) {
        Bundle args = new Bundle();
        args.putSerializable("rental", rental);
        androidx.navigation.Navigation.findNavController(requireView())
                .navigate(R.id.action_myRentals_to_rentalDetail, args);
    }

    @Override
    public void onCancelRental(Rental rental) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel booking #" + rental.getRentalId() + "?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    NotificationStore.pushBookingStatusNotification(
                        requireContext(),
                        SessionManager.ROLE_PROVIDER,
                        rental.getRentalId(),
                        "Booking cancelled by customer",
                        rental.getCarName() + " was cancelled for "
                            + rental.getStartDate() + " to " + rental.getEndDate()
                    );

                    allRentals.remove(rental);
                    allRentals.add(new Rental(
                            rental.getRentalId(),
                            rental.getCarName(),
                            rental.getCarImageUrl(),
                            rental.getCarPlate(),
                            rental.getPickupLocation(),
                            rental.getStartDate(),
                            rental.getEndDate(),
                            rental.getTotalDays(),
                            rental.getTotalPrice(),
                            Rental.Status.CANCELLED,
                            rental.getProviderName()
                    ));
                    filterAndShow(currentTab);
                    Toast.makeText(requireContext(), "Booking cancelled", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Keep Booking", null)
                .show();
    }

    @Override
    public void onRebook(Rental rental) {
        Bundle args = new Bundle();
        args.putString("prefillLocation", rental.getPickupLocation());
        androidx.navigation.Navigation.findNavController(requireView())
                .navigate(R.id.exploreFragment, args);
    }

    @Override
    public void onRateReview(Rental rental) {
        RentalReview existing = ReviewStore.getReview(rental.getRentalId());
        if (existing != null) {
            Toast.makeText(requireContext(), "You already reviewed this rental", Toast.LENGTH_SHORT).show();
            filterAndShow(currentTab);
            return;
        }

        ReviewDialogHelper.show(requireContext(), rental.getCarName(), review -> {
            ReviewStore.saveReview(rental.getRentalId(), review);
            Toast.makeText(requireContext(), "Thanks for your review!", Toast.LENGTH_SHORT).show();
            filterAndShow(currentTab);
        });
    }
}