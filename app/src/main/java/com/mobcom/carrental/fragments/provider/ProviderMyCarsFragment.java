package com.mobcom.carrental.fragments.provider;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.mobcom.carrental.R;
import com.mobcom.carrental.adapters.ProviderCarAdapter;
import com.mobcom.carrental.models.ProviderCar;
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
        loadDummyData();
        filterAndShow(0);

        fabAddCar.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(R.id.action_myCars_to_addEditCar)
        );
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

    private void loadDummyData() {
        allCars.add(new ProviderCar("CAR001", "Toyota", "Vios", 2023,
                "ABC 1234", "Automatic", "Gasoline", 5, "Sedan",
                1500, "Bacolod City",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/2023_Toyota_Vios_1.5_G_CVT_%28facelift%2C_white%29%2C_front_8.24.22.jpg/1280px-2023_Toyota_Vios_1.5_G_CVT_%28facelift%2C_white%29%2C_front_8.24.22.jpg",
                ProviderCar.Status.ACTIVE, 4.8f, 12));

        allCars.add(new ProviderCar("CAR002", "Honda", "City", 2022,
                "XYZ 5678", "Manual", "Gasoline", 5, "Sedan",
                1200, "Bacolod City",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ef/2021_Honda_City_1.0_V_Turbo_CVT_%28Philippines%29%2C_front_8.19.21.jpg/1280px-2021_Honda_City_1.0_V_Turbo_CVT_%28Philippines%29%2C_front_8.19.21.jpg",
                ProviderCar.Status.ACTIVE, 4.5f, 8));

        allCars.add(new ProviderCar("CAR003", "Mitsubishi", "Xpander", 2022,
                "DEF 9012", "Automatic", "Gasoline", 7, "Van",
                2000, "Bacolod City",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ae/2022_Mitsubishi_Xpander_GLS_Sport_AT_%28Philippines%29%2C_front_11.6.22.jpg/1280px-2022_Mitsubishi_Xpander_GLS_Sport_AT_%28Philippines%29%2C_front_11.6.22.jpg",
                ProviderCar.Status.INACTIVE, 0f, 0));
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
                    // TODO: API call
                    car.setStatus(car.getStatus() == ProviderCar.Status.ACTIVE
                            ? ProviderCar.Status.INACTIVE
                            : ProviderCar.Status.ACTIVE);
                    filterAndShow(currentTab);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}