package com.mobcom.carrental;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import android.widget.TextView;
import java.util.List;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    public interface OnFiltersAppliedListener {
        void onFiltersApplied(FilterOptions options);
    }

    public static class FilterOptions {
        public float minPrice = 0;
        public float maxPrice = 10000;
        public String transmission = "Any";
        public int seats = 0;
        public String fuelType = "Any";
        public String carType = "Any";
        public float minRating = 0;
    }

    private OnFiltersAppliedListener listener;
    private FilterOptions currentOptions = new FilterOptions();

    public void setOnFiltersAppliedListener(OnFiltersAppliedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_filter, container, false);

        // Price Range Slider
        RangeSlider sliderPrice = view.findViewById(R.id.sliderPrice);
        TextView tvMinPrice = view.findViewById(R.id.tvMinPrice);
        TextView tvMaxPrice = view.findViewById(R.id.tvMaxPrice);

        sliderPrice.setValues(currentOptions.minPrice, currentOptions.maxPrice);
        sliderPrice.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            tvMinPrice.setText("₱" + values.get(0).intValue());
            tvMaxPrice.setText("₱" + values.get(1).intValue());
            currentOptions.minPrice = values.get(0);
            currentOptions.maxPrice = values.get(1);
        });

        // Transmission chips
        setupSingleChipGroup(view, R.id.chipAny, "Any", v -> currentOptions.transmission = "Any");
        setupSingleChipGroup(view, R.id.chipAutomatic, "Automatic", v -> currentOptions.transmission = "Automatic");
        setupSingleChipGroup(view, R.id.chipManual, "Manual", v -> currentOptions.transmission = "Manual");

        // Seats
        ChipGroup chipGroupSeats = view.findViewById(R.id.chipGroupSeats);
        chipGroupSeats.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            Chip chip = view.findViewById(checkedIds.get(0));
            if (chip == null) return;
            String text = chip.getText().toString();
            if (text.equals("Any")) currentOptions.seats = 0;
            else if (text.equals("7+")) currentOptions.seats = 7;
            else currentOptions.seats = Integer.parseInt(text);
        });

        // Fuel
        ChipGroup chipGroupFuel = view.findViewById(R.id.chipGroupFuel);
        chipGroupFuel.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            Chip chip = view.findViewById(checkedIds.get(0));
            if (chip != null) currentOptions.fuelType = chip.getText().toString();
        });

        // Car Type
        ChipGroup chipGroupCarType = view.findViewById(R.id.chipGroupCarType);
        chipGroupCarType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            Chip chip = view.findViewById(checkedIds.get(0));
            if (chip != null) currentOptions.carType = chip.getText().toString();
        });

        // Rating
        ChipGroup chipGroupRating = view.findViewById(R.id.chipGroupRating);
        chipGroupRating.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            Chip chip = view.findViewById(checkedIds.get(0));
            if (chip == null) return;
            String text = chip.getText().toString();
            if (text.equals("Any")) currentOptions.minRating = 0;
            else if (text.equals("3★ & up")) currentOptions.minRating = 3;
            else if (text.equals("4★ & up")) currentOptions.minRating = 4;
            else currentOptions.minRating = 5;
        });

        // Reset
        view.findViewById(R.id.btnResetFilters).setOnClickListener(v -> {
            currentOptions = new FilterOptions();
            sliderPrice.setValues(0f, 10000f);
            tvMinPrice.setText("₱0");
            tvMaxPrice.setText("₱10,000");
            ((Chip) view.findViewById(R.id.chipAny)).setChecked(true);
            ((Chip) view.findViewById(R.id.chipSeatsAny)).setChecked(true);
            ((Chip) view.findViewById(R.id.chipFuelAny)).setChecked(true);
            ((Chip) view.findViewById(R.id.chipCarTypeAny)).setChecked(true);
            ((Chip) view.findViewById(R.id.chipRatingAny)).setChecked(true);
        });

        // Apply
        view.findViewById(R.id.btnApplyFilters).setOnClickListener(v -> {
            if (listener != null) listener.onFiltersApplied(currentOptions);
            dismiss();
        });

        return view;
    }

    private void setupSingleChipGroup(View view, int chipId, String value, View.OnClickListener extra) {
        Chip chip = view.findViewById(chipId);
        chip.setOnClickListener(v -> {
            currentOptions.transmission = value;
            extra.onClick(v);
        });
    }
}