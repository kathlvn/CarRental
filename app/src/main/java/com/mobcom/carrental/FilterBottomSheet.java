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
import java.util.ArrayList;
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

        public FilterOptions copy() {
            FilterOptions copy = new FilterOptions();
            copy.minPrice = minPrice;
            copy.maxPrice = maxPrice;
            copy.transmission = transmission;
            copy.seats = seats;
            copy.fuelType = fuelType;
            copy.carType = carType;
            copy.minRating = minRating;
            return copy;
        }
    }

    private OnFiltersAppliedListener listener;
    private FilterOptions currentOptions = new FilterOptions();

    public void setOnFiltersAppliedListener(OnFiltersAppliedListener listener) {
        this.listener = listener;
    }

    public void setCurrentOptions(FilterOptions options) {
        currentOptions = options != null ? options.copy() : new FilterOptions();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_filter, container, false);

        // Price Range Slider
        RangeSlider sliderPrice = view.findViewById(R.id.sliderPrice);
        TextView tvMinPrice = view.findViewById(R.id.tvMinPrice);
        TextView tvMaxPrice = view.findViewById(R.id.tvMaxPrice);
        ChipGroup chipGroupTransmission = view.findViewById(R.id.chipGroupTransmission);
        ChipGroup chipGroupSeats = view.findViewById(R.id.chipGroupSeats);
        ChipGroup chipGroupFuel = view.findViewById(R.id.chipGroupFuel);
        ChipGroup chipGroupCarType = view.findViewById(R.id.chipGroupCarType);
        ChipGroup chipGroupRating = view.findViewById(R.id.chipGroupRating);

        sliderPrice.setValues(currentOptions.minPrice, currentOptions.maxPrice);
        tvMinPrice.setText("₱" + (int) currentOptions.minPrice);
        tvMaxPrice.setText("₱" + (int) currentOptions.maxPrice);
        sliderPrice.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            tvMinPrice.setText("₱" + values.get(0).intValue());
            tvMaxPrice.setText("₱" + values.get(1).intValue());
            currentOptions.minPrice = values.get(0);
            currentOptions.maxPrice = values.get(1);
        });

        checkTransmissionChip(view, currentOptions.transmission);
        chipGroupTransmission.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentOptions.transmission = "Any";
                return;
            }
            Chip chip = view.findViewById(checkedIds.get(0));
            if (chip != null) currentOptions.transmission = chip.getText().toString();
        });

        checkSeatsChip(view, currentOptions.seats);
        chipGroupSeats.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentOptions.seats = 0;
                return;
            }
            Chip chip = view.findViewById(checkedIds.get(0));
            if (chip == null) return;
            String text = chip.getText().toString();
            if (text.equals("Any")) currentOptions.seats = 0;
            else if (text.equals("7+")) currentOptions.seats = 7;
            else currentOptions.seats = Integer.parseInt(text);
        });

        checkTextChip(view, R.id.chipFuelAny, R.id.chipGasoline, R.id.chipDiesel, R.id.chipElectric, R.id.chipHybrid,
                currentOptions.fuelType, "Any");
        chipGroupFuel.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentOptions.fuelType = "Any";
                return;
            }
            Chip chip = view.findViewById(checkedIds.get(0));
            if (chip != null) currentOptions.fuelType = chip.getText().toString();
        });

        checkTextChip(view, R.id.chipCarTypeAny, R.id.chipSedan, R.id.chipSuv, R.id.chipVan, R.id.chipPickup,
                currentOptions.carType, "Any");
        chipGroupCarType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentOptions.carType = "Any";
                return;
            }
            Chip chip = view.findViewById(checkedIds.get(0));
            if (chip != null) currentOptions.carType = chip.getText().toString();
        });

        checkRatingChip(view, currentOptions.minRating);
        chipGroupRating.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentOptions.minRating = 0;
                return;
            }
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
            if (listener != null) listener.onFiltersApplied(currentOptions.copy());
            dismiss();
        });

        return view;
    }

    private void checkTransmissionChip(View view, String transmission) {
        if ("Manual".equalsIgnoreCase(transmission)) {
            ((Chip) view.findViewById(R.id.chipManual)).setChecked(true);
        } else if ("Automatic".equalsIgnoreCase(transmission)) {
            ((Chip) view.findViewById(R.id.chipAutomatic)).setChecked(true);
        } else {
            ((Chip) view.findViewById(R.id.chipAny)).setChecked(true);
        }
    }

    private void checkSeatsChip(View view, int seats) {
        if (seats >= 7) {
            ((Chip) view.findViewById(R.id.chipSeats7)).setChecked(true);
        } else if (seats == 5) {
            ((Chip) view.findViewById(R.id.chipSeats5)).setChecked(true);
        } else if (seats == 4) {
            ((Chip) view.findViewById(R.id.chipSeats4)).setChecked(true);
        } else if (seats == 2) {
            ((Chip) view.findViewById(R.id.chipSeats2)).setChecked(true);
        } else {
            ((Chip) view.findViewById(R.id.chipSeatsAny)).setChecked(true);
        }
    }

    private void checkRatingChip(View view, float minRating) {
        if (minRating >= 5f) {
            ((Chip) view.findViewById(R.id.chipRating5)).setChecked(true);
        } else if (minRating >= 4f) {
            ((Chip) view.findViewById(R.id.chipRating4)).setChecked(true);
        } else if (minRating >= 3f) {
            ((Chip) view.findViewById(R.id.chipRating3)).setChecked(true);
        } else {
            ((Chip) view.findViewById(R.id.chipRatingAny)).setChecked(true);
        }
    }

    private void checkTextChip(View view, int defaultChipId, int chip1Id, int chip2Id, int chip3Id, int chip4Id,
                               String value, String defaultValue) {
        List<Integer> chipIds = new ArrayList<>();
        chipIds.add(defaultChipId);
        chipIds.add(chip1Id);
        chipIds.add(chip2Id);
        chipIds.add(chip3Id);
        chipIds.add(chip4Id);

        for (Integer chipId : chipIds) {
            Chip chip = view.findViewById(chipId);
            if (chip != null && chip.getText().toString().equalsIgnoreCase(value)) {
                chip.setChecked(true);
                return;
            }
        }

        Chip fallback = view.findViewById(defaultChipId);
        if (fallback != null && defaultValue != null) {
            fallback.setChecked(true);
        }
    }
}