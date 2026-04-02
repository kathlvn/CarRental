package com.mobcom.carrental;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExploreFragment extends Fragment {

    private TextView tvLocation, tvStartDate, tvEndDate;
    private LinearLayout dateRangeRow;
    private boolean isExpanded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        tvLocation = view.findViewById(R.id.tvLocation);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        dateRangeRow = view.findViewById(R.id.dateRangeRow);

        // Set default dates
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        tvStartDate.setText(sdf.format(new Date()));
        tvEndDate.setText(sdf.format(new Date(System.currentTimeMillis() + 4L * 24 * 60 * 60 * 1000)));

        // Expand/collapse top bar
        view.findViewById(R.id.ivExpandCollapse).setOnClickListener(v -> {
            isExpanded = !isExpanded;
            dateRangeRow.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        });

        // Tap location to open bottom sheet
        tvLocation.setOnClickListener(v -> openLocationSheet());
        view.findViewById(R.id.topBar).setOnClickListener(v -> openLocationSheet());

        // Setup car sections
        setupCarSections(view);

        return view;
    }

    private void openLocationSheet() {
        LocationBottomSheet sheet = new LocationBottomSheet();
        sheet.setOnLocationSelectedListener(location -> tvLocation.setText(location));
        sheet.show(getParentFragmentManager(), "LocationBottomSheet");
    }

    private void setupCarSections(View view) {
        List<Car> popularCars = getSampleCars();
        List<Car> shortTripCars = getSampleCars();
        List<Car> budgetCars = getSampleCars();

        setupRecyclerView(view, R.id.rvPopularCars, popularCars);
        setupRecyclerView(view, R.id.rvShortTrips, shortTripCars);
        setupRecyclerView(view, R.id.rvBudgetFriendly, budgetCars);
    }

    private void setupRecyclerView(View view, int rvId, List<Car> cars) {
        RecyclerView rv = view.findViewById(rvId);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(new CarAdapter(getContext(), cars));
    }

    private List<Car> getSampleCars() {
        List<Car> cars = new ArrayList<>();
        cars.add(new Car("Tesla Model S 2024", "Automatic", 4, "Electricity", 3985, 1.2f, R.drawable.ic_rentals));
        cars.add(new Car("Toyota Vios 2023", "Automatic", 5, "Gasoline", 1500, 2.1f, R.drawable.ic_rentals));
        cars.add(new Car("Honda City 2024", "Automatic", 5, "Gasoline", 1800, 0.8f, R.drawable.ic_rentals));
        cars.add(new Car("Mitsubishi Mirage", "Manual", 5, "Gasoline", 1200, 3.0f, R.drawable.ic_rentals));
        return cars;
    }
}