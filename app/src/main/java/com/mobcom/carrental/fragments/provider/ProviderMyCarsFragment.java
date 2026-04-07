package com.mobcom.carrental.fragments.provider;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.mobcom.carrental.R;
import com.mobcom.carrental.adapters.ProviderCarAdapter;
import com.mobcom.carrental.models.ProviderCar;
import com.mobcom.carrental.utils.CarService;
import com.mobcom.carrental.utils.SessionManager;
import com.mobcom.carrental.database.entities.CarEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProviderMyCarsFragment extends Fragment implements ProviderCarAdapter.OnCarActionListener {

    private TabLayout tabFilter;
    private RecyclerView rvMyCars;
    private LinearLayout layoutEmpty;
    private TextView tvCarCount;
    private FloatingActionButton fabAddCar;

    private ProviderCarAdapter adapter;
    private List<ProviderCar> allCars = new ArrayList<>();
    private int currentTab = 0; // 0=All, 1=Active, 2=Inactive

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provider_my_cars, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabFilter   = view.findViewById(R.id.tabFilter);
        rvMyCars    = view.findViewById(R.id.rvMyCars);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvCarCount  = view.findViewById(R.id.tvCarCount);
        fabAddCar   = view.findViewById(R.id.fabAddCar);

        setupTabs();
        setupRecyclerView();
        loadCarsFromDatabase();
        filterAndShow(0);

        fabAddCar.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(R.id.action_myCars_to_addEditCar)
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh car listings when returning to this fragment
        loadCarsFromDatabase();
        filterAndShow(currentTab);
    }

    private void setupTabs() {
        tabFilter.addTab(tabFilter.newTab().setText("All"));
        tabFilter.addTab(tabFilter.newTab().setText("Active"));
        tabFilter.addTab(tabFilter.newTab().setText("Inactive"));

        tabFilter.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        adapter = new ProviderCarAdapter(requireContext(), new ArrayList<>(), this);
        rvMyCars.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMyCars.setNestedScrollingEnabled(false);
        rvMyCars.setAdapter(adapter);
    }

    private void filterAndShow(int tab) {
        List<ProviderCar> filtered;

        switch (tab) {
            case 1:
                filtered = allCars.stream()
                        .filter(c -> c.getStatus() == ProviderCar.Status.ACTIVE)
                        .collect(Collectors.toList());
                break;
            case 2:
                filtered = allCars.stream()
                        .filter(c -> c.getStatus() == ProviderCar.Status.INACTIVE
                                || c.getStatus() == ProviderCar.Status.PENDING_REVIEW)
                        .collect(Collectors.toList());
                break;
            default:
                filtered = new ArrayList<>(allCars);
                break;
        }

        adapter.updateList(filtered);
        tvCarCount.setText(allCars.size() + " listing" + (allCars.size() != 1 ? "s" : ""));

        if (filtered.isEmpty()) {
            rvMyCars.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvMyCars.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void loadCarsFromDatabase() {
        SessionManager sessionManager = new SessionManager(requireContext());
        String providerId = sessionManager.getEmail();

        allCars.clear();
        java.util.List<CarEntity> carEntities = CarService.getInstance().getProviderCars(providerId);

        for (CarEntity entity : carEntities) {
            ProviderCar.Status status;
            if ("REJECTED".equals(entity.approvalStatus)) {
                status = ProviderCar.Status.INACTIVE;
            } else if ("PENDING".equals(entity.approvalStatus) || entity.approvalStatus == null) {
                status = ProviderCar.Status.PENDING_REVIEW;
            } else if ("APPROVED".equals(entity.approvalStatus)) {
                status = entity.isAvailable ? ProviderCar.Status.ACTIVE : ProviderCar.Status.INACTIVE;
            } else {
                status = entity.isAvailable ? ProviderCar.Status.ACTIVE : ProviderCar.Status.INACTIVE;
            }

            ProviderCar car = new ProviderCar(
                    entity.carId,
                    entity.name.split(" ")[0],  // Brand
                    entity.name.split(" ").length > 1 ? entity.name.split(" ")[1] : "",  // Model
                    2025,  // Year (would need to store this)
                    entity.plateNumber,
                    entity.transmission,
                    entity.fuelType,
                    entity.seats,
                    entity.carType,
                    entity.pricePerDay,
                    entity.location,
                    entity.imageUrl,
                    status,
                    (float) entity.rating,
                    entity.totalRentals
            );
            allCars.add(car);
        }
    }

    // ── ProviderCarAdapter.OnCarActionListener ────────────────────────────────

    @Override
    public void onEdit(ProviderCar car) {
        Bundle args = new Bundle();
        args.putSerializable("car", car);

        androidx.navigation.Navigation.findNavController(requireView())
                .navigate(R.id.action_myCars_to_addEditCar, args);
    }

    @Override
    public void onToggleStatus(ProviderCar car) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(car.getStatus() == ProviderCar.Status.ACTIVE
                        ? "Deactivate Listing?"
                        : "Activate Listing?")
                .setMessage(car.getStatus() == ProviderCar.Status.ACTIVE
                        ? "This car will no longer appear in search results."
                        : "This car will be visible to customers again.")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    // Update in database
                    CarService.getInstance().toggleCarAvailability(car.getCarId());

                    car.setStatus(car.getStatus() == ProviderCar.Status.ACTIVE
                            ? ProviderCar.Status.INACTIVE
                            : ProviderCar.Status.ACTIVE);
                    filterAndShow(currentTab);
                    Toast.makeText(requireContext(),
                        "Listing status updated",
                        Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}