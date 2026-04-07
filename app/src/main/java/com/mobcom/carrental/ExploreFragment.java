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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExploreFragment extends Fragment {

    private TextView tvLocation, tvStartDate, tvEndDate;
    private LinearLayout dateRangeRow;
    private boolean isExpanded = false;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private List<Car> allPopularCars = new ArrayList<>();
    private List<Car> allShortTripCars = new ArrayList<>();
    private List<Car> allBudgetCars = new ArrayList<>();
    private CarAdapter popularAdapter;
    private CarAdapter shortTripAdapter;
    private CarAdapter budgetAdapter;
    private FilterBottomSheet.FilterOptions activeFilters = new FilterBottomSheet.FilterOptions();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        tvLocation  = view.findViewById(R.id.tvLocation);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate   = view.findViewById(R.id.tvEndDate);
        dateRangeRow= view.findViewById(R.id.dateRangeRow);

        Bundle args = getArguments();
        if (args != null) {
            String prefillLocation = args.getString("prefillLocation", "");
            if (!prefillLocation.isEmpty()) {
                tvLocation.setText(prefillLocation);
            }
        }

        // Set default dates (today and 4 days later)
        endCalendar.add(Calendar.DAY_OF_MONTH, 4);
        updateDateLabels();

        // Expand/collapse top bar
        view.findViewById(R.id.ivExpandCollapse).setOnClickListener(v -> {
            isExpanded = !isExpanded;
            dateRangeRow.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        });

        // Location
        tvLocation.setOnClickListener(v -> openLocationSheet());
        view.findViewById(R.id.topBar).setOnClickListener(v -> openLocationSheet());

        // Dates
        view.findViewById(R.id.btnStartDate).setOnClickListener(v -> showDatePicker(true));
        view.findViewById(R.id.btnEndDate).setOnClickListener(v -> showDatePicker(false));

        // Filter
        view.findViewById(R.id.ivFilter).setOnClickListener(v -> openFilterSheet());

        // Car sections
        setupCarSections(view);

        return view;
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startCalendar : endCalendar;

        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (v, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);

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

        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
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

    private void openFilterSheet() {
        FilterBottomSheet sheet = new FilterBottomSheet();
        sheet.setCurrentOptions(activeFilters);
        sheet.setOnFiltersAppliedListener(this::applyFilters);
        sheet.show(getParentFragmentManager(), "FilterBottomSheet");
    }

    private void applyFilters(FilterBottomSheet.FilterOptions options) {
        activeFilters = options != null ? options.copy() : new FilterBottomSheet.FilterOptions();
        popularAdapter.updateList(filterCars(allPopularCars, activeFilters));
        shortTripAdapter.updateList(filterCars(allShortTripCars, activeFilters));
        budgetAdapter.updateList(filterCars(allBudgetCars, activeFilters));
    }

    private List<Car> filterCars(List<Car> source, FilterBottomSheet.FilterOptions options) {
        List<Car> filtered = new ArrayList<>();
        for (Car car : source) {
            if (car.getPricePerDay() < options.minPrice || car.getPricePerDay() > options.maxPrice) {
                continue;
            }
            if (!"Any".equalsIgnoreCase(options.transmission)
                    && !car.getTransmission().equalsIgnoreCase(options.transmission)) {
                continue;
            }
            if (options.seats > 0) {
                if (options.seats >= 7) {
                    if (car.getSeats() < 7) continue;
                } else if (car.getSeats() != options.seats) {
                    continue;
                }
            }
            if (!"Any".equalsIgnoreCase(options.fuelType)
                    && !car.getFuelType().equalsIgnoreCase(options.fuelType)) {
                continue;
            }
            if (!"Any".equalsIgnoreCase(options.carType)
                    && !car.getCarType().equalsIgnoreCase(options.carType)) {
                continue;
            }
            if (car.getRating() < options.minRating) {
                continue;
            }
            filtered.add(car);
        }
        return filtered;
    }

    // ── Car Sections ──────────────────────────────────────────────────────────

    private void setupCarSections(View view) {
        allPopularCars = getSampleCars();
        allShortTripCars = getSampleCars();
        allBudgetCars = getSampleCars();

        popularAdapter = setupRecyclerView(view, R.id.rvPopularCars, allPopularCars);
        shortTripAdapter = setupRecyclerView(view, R.id.rvShortTrips, allShortTripCars);
        budgetAdapter = setupRecyclerView(view, R.id.rvBudgetFriendly, allBudgetCars);

        applyFilters(activeFilters);
    }

    private CarAdapter setupRecyclerView(View view, int rvId, List<Car> cars) {
        RecyclerView rv = view.findViewById(rvId);
        rv.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));

        CarAdapter adapter = new CarAdapter(getContext(), cars);
        adapter.setOnCarClickListener(car -> navigateToCarDetail(car));
        rv.setAdapter(adapter);
        return adapter;
    }

    private void navigateToCarDetail(Car car) {
        // Calculate rental days
        long diffMs = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        int days = (int) (diffMs / (1000 * 60 * 60 * 24));
        if (days < 1) days = 1;

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        Bundle args = new Bundle();
        args.putString("carId",        car.getId());
        args.putString("carName",      car.getName());
        args.putString("transmission", car.getTransmission());
        args.putInt("seats",           car.getSeats());
        args.putString("fuelType",     car.getFuelType());
        args.putDouble("pricePerDay",  car.getPricePerDay());
        args.putFloat("distanceKm",    car.getDistanceKm());
        args.putInt("imageResId",      car.getImageResId());
        args.putString("imageUrl",     car.getImageUrl());
        args.putString("location",     car.getLocation());
        args.putString("providerName", car.getProviderName());
        args.putString("providerId",   car.getProviderId());
        args.putString("plateNumber",  car.getPlateNumber());
        args.putString("carType",      car.getCarType());
        args.putFloat("rating",        car.getRating());
        args.putString("startDate",    sdf.format(startCalendar.getTime()));
        args.putString("endDate",      sdf.format(endCalendar.getTime()));
        args.putInt("rentalDays",      days);

        NavHostFragment.findNavController(ExploreFragment.this)
                .navigate(R.id.action_explore_to_carDetail, args);
    }

    // ── Sample Data ───────────────────────────────────────────────────────────

    private List<Car> getSampleCars() {
        List<Car> cars = new ArrayList<>();
        cars.add(new Car(
                "C001", "Toyota Vios 2023", "Automatic", 5,
                "Gasoline", 1500, 2.1f, R.drawable.ic_rentals,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/2023_Toyota_Vios_1.5_G_CVT_%28facelift%2C_white%29%2C_front_8.24.22.jpg/1280px-2023_Toyota_Vios_1.5_G_CVT_%28facelift%2C_white%29%2C_front_8.24.22.jpg",
                "Bacolod City", "Juan dela Cruz", "P001",
                "ABC 1234", "Sedan", 4.8f));

        cars.add(new Car(
                "C002", "Honda City 2024", "Automatic", 5,
                "Gasoline", 1800, 0.8f, R.drawable.ic_rentals,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ef/2021_Honda_City_1.0_V_Turbo_CVT_%28Philippines%29%2C_front_8.19.21.jpg/1280px-2021_Honda_City_1.0_V_Turbo_CVT_%28Philippines%29%2C_front_8.19.21.jpg",
                "Bacolod City", "Maria Santos", "P002",
                "XYZ 5678", "Sedan", 4.5f));

        cars.add(new Car(
                "C003", "Mitsubishi Xpander 2022", "Automatic", 7,
                "Gasoline", 2000, 3.0f, R.drawable.ic_rentals,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ae/2022_Mitsubishi_Xpander_GLS_Sport_AT_%28Philippines%29%2C_front_11.6.22.jpg/1280px-2022_Mitsubishi_Xpander_GLS_Sport_AT_%28Philippines%29%2C_front_11.6.22.jpg",
                "Bacolod City", "Pedro Reyes", "P003",
                "DEF 9012", "Van", 4.2f));

        cars.add(new Car(
                "C004", "Mitsubishi Mirage 2022", "Manual", 5,
                "Gasoline", 1200, 3.0f, R.drawable.ic_rentals,
                "",
                "Bacolod City", "Ana Reyes", "P004",
                "GHI 3456", "Hatchback", 4.0f));

        return cars;
    }
}