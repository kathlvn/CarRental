package com.mobcom.carrental;

import android.app.DatePickerDialog;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExploreFragment extends Fragment {

    private TextView tvLocation, tvStartDate, tvEndDate;
    private LinearLayout dateRangeRow;
    private boolean isExpanded = false;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        tvLocation = view.findViewById(R.id.tvLocation);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        dateRangeRow = view.findViewById(R.id.dateRangeRow);

        // Set default dates (today and 4 days later)
        endCalendar.add(Calendar.DAY_OF_MONTH, 4);
        updateDateLabels();

        // Expand/collapse top bar
        view.findViewById(R.id.ivExpandCollapse).setOnClickListener(v -> {
            isExpanded = !isExpanded;
            dateRangeRow.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        });

        // Tap location to open bottom sheet
        tvLocation.setOnClickListener(v -> openLocationSheet());
        view.findViewById(R.id.topBar).setOnClickListener(v -> openLocationSheet());

        // Tap dates to open date picker
        view.findViewById(R.id.btnStartDate).setOnClickListener(v -> showDatePicker(true));
        view.findViewById(R.id.btnEndDate).setOnClickListener(v -> showDatePicker(false));

        view.findViewById(R.id.ivFilter).setOnClickListener(v -> openFilterSheet());

        // Setup car sections
        setupCarSections(view);

        return view;
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startCalendar : endCalendar;

        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);

                    // If start date is after end date, push end date forward
                    if (isStartDate && startCalendar.after(endCalendar)) {
                        endCalendar = (Calendar) startCalendar.clone();
                        endCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    }

                    updateDateLabels();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Prevent selecting past dates
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        // If picking end date, can't be before start date
        if (!isStartDate) {
            dialog.getDatePicker().setMinDate(startCalendar.getTimeInMillis());
        }

        dialog.show();
    }

    private void updateDateLabels() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        tvStartDate.setText(sdf.format(startCalendar.getTime()));
        tvEndDate.setText(sdf.format(endCalendar.getTime()));
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
        CarAdapter adapter = new CarAdapter(getContext(), cars);
        adapter.setOnCarClickListener(car -> {
            // Calculate rental days
            long diffMs = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
            int days = (int) (diffMs / (1000 * 60 * 60 * 24));
            if (days < 1) days = 1;

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            CarDetailFragment detail = CarDetailFragment.newInstance(
                    car,
                    sdf.format(startCalendar.getTime()),
                    sdf.format(endCalendar.getTime()),
                    days
            );

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, detail)
                    .addToBackStack(null)
                    .commit();
        });
        rv.setAdapter(adapter);
    }

    private List<Car> getSampleCars() {
        List<Car> cars = new ArrayList<>();
        cars.add(new Car("Tesla Model S 2024", "Automatic", 4, "Electricity", 3985, 1.2f, R.drawable.ic_rentals));
        cars.add(new Car("Toyota Vios 2023", "Automatic", 5, "Gasoline", 1500, 2.1f, R.drawable.ic_rentals));
        cars.add(new Car("Honda City 2024", "Automatic", 5, "Gasoline", 1800, 0.8f, R.drawable.ic_rentals));
        cars.add(new Car("Mitsubishi Mirage", "Manual", 5, "Gasoline", 1200, 3.0f, R.drawable.ic_rentals));
        return cars;
    }

    private void openFilterSheet() {
        FilterBottomSheet sheet = new FilterBottomSheet();
        sheet.setOnFiltersAppliedListener(options -> {
            // We'll use these filter options to filter car lists later
        });
        sheet.show(getParentFragmentManager(), "FilterBottomSheet");
    }
}