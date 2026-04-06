package com.mobcom.carrental;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.mobcom.carrental.R;
import com.mobcom.carrental.models.Booking;

public class BookingConfirmationFragment extends Fragment {

    private Booking booking;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            booking = (Booking) getArguments().getSerializable("booking");
        }

        if (booking != null) populateData(view);

        MaterialButton btnBackHome = view.findViewById(R.id.btnBackHome);
        MaterialButton btnViewRentals = view.findViewById(R.id.btnViewRentals);

        btnBackHome.setOnClickListener(v -> {
            // Navigate back to Explore (root)
            androidx.navigation.Navigation.findNavController(requireView())
                    .navigate(R.id.exploreFragment);
        });

        btnViewRentals.setOnClickListener(v -> {
            // Navigate to My Rentals tab
            com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                    requireActivity().findViewById(R.id.bottom_nav);
            bottomNav.setSelectedItemId(R.id.myRentalsFragment);
        });
    }

    private void populateData(View view) {
        // Booking ID
        TextView tvBookingId = view.findViewById(R.id.tvBookingId);
        tvBookingId.setText("Booking ID: #" + booking.getBookingId());

        // Car
        TextView tvCarName     = view.findViewById(R.id.tvCarName);
        TextView tvProviderName= view.findViewById(R.id.tvProviderName);
        android.widget.ImageView imgCar = view.findViewById(R.id.imgCar);

        tvCarName.setText(booking.getCarName());
        tvProviderName.setText("by " + booking.getProviderName());

        Glide.with(this)
                .load(booking.getCarImageUrl())
                .centerCrop()
                .placeholder(R.drawable.placeholder_car)
                .into(imgCar);

        // Booking details
        ((TextView) view.findViewById(R.id.tvStartDate)).setText(booking.getStartDate());
        ((TextView) view.findViewById(R.id.tvEndDate)).setText(booking.getEndDate());
        ((TextView) view.findViewById(R.id.tvTotalDays))
                .setText(booking.getTotalDays() + " day"
                        + (booking.getTotalDays() != 1 ? "s" : ""));
        ((TextView) view.findViewById(R.id.tvPickupLocation))
                .setText(booking.getPickupLocation());
        ((TextView) view.findViewById(R.id.tvPaymentMethod))
                .setText(booking.getPaymentMethod() == Booking.PaymentMethod.CASH_ON_PICKUP
                        ? "💵 Cash on Pickup"
                        : "💳 Online");

        // Payment summary
        double base       = booking.getDailyRate() * booking.getTotalDays();
        TextView tvSummaryRate = view.findViewById(R.id.tvSummaryRate);
        tvSummaryRate.setText("₱" + String.format("%,.0f", booking.getDailyRate())
                + " × " + booking.getTotalDays() + " days");
        ((TextView) view.findViewById(R.id.tvSummaryRateValue))
                .setText("₱" + String.format("%,.0f", base));
        ((TextView) view.findViewById(R.id.tvSummaryFee))
                .setText("₱" + String.format("%,.0f", booking.getServiceFee()));
        ((TextView) view.findViewById(R.id.tvSummaryTotal))
                .setText("₱" + String.format("%,.0f", booking.getTotalAmount()));

        // Note
        if (booking.getNoteToProvider() != null
                && !booking.getNoteToProvider().isEmpty()) {
            view.findViewById(R.id.cardNote).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.tvNote))
                    .setText(booking.getNoteToProvider());
        }
    }
}

