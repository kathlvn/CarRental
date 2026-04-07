package com.mobcom.carrental;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobcom.carrental.R;
import com.mobcom.carrental.models.Booking;
import com.mobcom.carrental.Car;
import java.util.UUID;

public class BookingFormFragment extends Fragment {

    private Car car;

    // Views
    private android.widget.ImageView imgCar;
    private TextView tvCarName, tvProviderName, tvDailyRate;
    private TextView tvStartDate, tvEndDate, tvTotalDays;
    private TextView tvBreakdownRate, tvBreakdownRateValue;
    private TextView tvBreakdownFee, tvBreakdownTotal;
    private TextInputLayout tilPickupLocation;
    private TextInputEditText etPickupLocation, etNote;
    private LinearLayout layoutCash, layoutOnline;
    private TextView tvCashSelected;
    private MaterialButton btnConfirmBooking;

    private Booking.PaymentMethod selectedPayment = Booking.PaymentMethod.CASH_ON_PICKUP;

    // Dates passed from CarDetailFragment
    private String startDate;
    private String endDate;
    private int totalDays;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupToolbar(view);

        // Get data passed from CarDetailFragment
        if (getArguments() != null) {
            car       = (Car) getArguments().getSerializable("car");
            startDate = getArguments().getString("startDate", "");
            endDate   = getArguments().getString("endDate", "");
            totalDays = getArguments().getInt("totalDays", 1);
        }

        if (car != null) {
            populateCarInfo();
            populateDates();
            populateBreakdown();
        }

        setupPaymentSelector();

        tilPickupLocation.setEndIconOnClickListener(v -> openPickupLocationSheet());

        btnConfirmBooking.setOnClickListener(v -> {
            if (validateForm()) submitBooking();
        });
    }

    private void bindViews(View view) {
        imgCar              = view.findViewById(R.id.imgCar);
        tvCarName           = view.findViewById(R.id.tvCarName);
        tvProviderName      = view.findViewById(R.id.tvProviderName);
        tvDailyRate         = view.findViewById(R.id.tvDailyRate);
        tvStartDate         = view.findViewById(R.id.tvStartDate);
        tvEndDate           = view.findViewById(R.id.tvEndDate);
        tvTotalDays         = view.findViewById(R.id.tvTotalDays);
        tvBreakdownRate     = view.findViewById(R.id.tvBreakdownRate);
        tvBreakdownRateValue= view.findViewById(R.id.tvBreakdownRateValue);
        tvBreakdownFee      = view.findViewById(R.id.tvBreakdownFee);
        tvBreakdownTotal    = view.findViewById(R.id.tvBreakdownTotal);
        tilPickupLocation   = view.findViewById(R.id.tilPickupLocation);
        etPickupLocation    = view.findViewById(R.id.etPickupLocation);
        etNote              = view.findViewById(R.id.etNote);
        layoutCash          = view.findViewById(R.id.layoutCash);
        layoutOnline        = view.findViewById(R.id.layoutOnline);
        tvCashSelected      = view.findViewById(R.id.tvCashSelected);
        btnConfirmBooking   = view.findViewById(R.id.btnConfirmBooking);
    }

    private void setupToolbar(View view) {
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());
    }

    private void openPickupLocationSheet() {
        LocationBottomSheet sheet = new LocationBottomSheet();
        sheet.setOnLocationSelectedListener(location -> etPickupLocation.setText(location));
        sheet.show(getParentFragmentManager(), "BookingPickupLocationSheet");
    }

    private void populateCarInfo() {
        tvCarName.setText(car.getName());
        tvProviderName.setText("by " + car.getProviderName());
        tvDailyRate.setText("₱" + String.format("%,.0f", car.getPricePerDay()) + " / day");
        etPickupLocation.setText(car.getLocation());

        Glide.with(this)
                .load(car.getImageUrl())
                .centerCrop()
                .placeholder(R.drawable.placeholder_car)
                .into(imgCar);
    }

    private void populateDates() {
        tvStartDate.setText(startDate.isEmpty() ? "Select date" : startDate);
        tvEndDate.setText(endDate.isEmpty() ? "Select date" : endDate);
        tvTotalDays.setText(totalDays + " day" + (totalDays != 1 ? "s" : ""));
    }

    private void populateBreakdown() {
        double base       = car.getPricePerDay() * totalDays;
        double serviceFee = base * 0.05;
        double total      = base + serviceFee;

        tvBreakdownRate.setText("₱" + String.format("%,.0f", car.getPricePerDay())
                + " × " + totalDays + " days");
        tvBreakdownRateValue.setText("₱" + String.format("%,.0f", base));
        tvBreakdownFee.setText("₱" + String.format("%,.0f", serviceFee));
        tvBreakdownTotal.setText("₱" + String.format("%,.0f", total));
    }

    private void setupPaymentSelector() {
        layoutCash.setOnClickListener(v -> {
            selectedPayment = Booking.PaymentMethod.CASH_ON_PICKUP;
            layoutCash.setBackgroundResource(R.drawable.bg_role_selected);
            layoutOnline.setBackgroundResource(R.drawable.bg_role_unselected);
            tvCashSelected.setVisibility(View.VISIBLE);
        });

        // Online payment disabled for now
        layoutOnline.setOnClickListener(v -> {
            Toast.makeText(requireContext(),
                    "Online payment is coming soon. Use cash on pickup for now.",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validateForm() {
        if (startDate.isEmpty() || endDate.isEmpty()) {
            tvStartDate.setText("Required");
            tvStartDate.setTextColor(
                    requireContext().getColor(android.R.color.holo_red_light));
            return false;
        }

        if (TextUtils.isEmpty(etPickupLocation.getText())) {
            ((com.google.android.material.textfield.TextInputLayout)
                    etPickupLocation.getParent().getParent())
                    .setError("Pickup location is required");
            return false;
        }

        return true;
    }

    private void submitBooking() {
        String bookingId = "BK" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String note      = etNote.getText() != null ? etNote.getText().toString().trim() : "";

        Booking booking = new Booking(
                bookingId,
                car.getId(),
                car.getName(),
                car.getImageUrl(),
                car.getPlateNumber(),
                car.getProviderName(),
                car.getProviderId(),
                etPickupLocation.getText().toString(),
                startDate,
                endDate,
                totalDays,
                car.getPricePerDay(),
                note,
                selectedPayment
        );

        // Navigate to confirmation screen
        Bundle args = new Bundle();
        args.putSerializable("booking", booking);
        androidx.navigation.Navigation.findNavController(requireView())
                .navigate(R.id.action_bookingForm_to_bookingConfirmation, args);
    }
}
