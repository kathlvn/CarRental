package com.mobcom.carrental;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Calendar;

public class CarDetailFragment extends Fragment {

    private Car car;
    private String startDate;
    private String endDate;
    private int rentalDays;

    public static CarDetailFragment newInstance(Car car, String startDate, String endDate, int rentalDays) {
        CarDetailFragment fragment = new CarDetailFragment();
        Bundle args = new Bundle();
        args.putString("carName", car.getName());
        args.putString("transmission", car.getTransmission());
        args.putInt("seats", car.getSeats());
        args.putString("fuelType", car.getFuelType());
        args.putDouble("pricePerDay", car.getPricePerDay());
        args.putFloat("distanceKm", car.getDistanceKm());
        args.putInt("imageResId", car.getImageResId());
        args.putString("startDate", startDate);
        args.putString("endDate", endDate);
        args.putInt("rentalDays", rentalDays);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_detail, container, false);

        // Get arguments
        Bundle args = getArguments();
        if (args == null) return view;

        String carName = args.getString("carName");
        String transmission = args.getString("transmission");
        int seats = args.getInt("seats");
        String fuelType = args.getString("fuelType");
        double pricePerDay = args.getDouble("pricePerDay");
        float distanceKm = args.getFloat("distanceKm");
        int imageResId = args.getInt("imageResId");
        String startDate = args.getString("startDate");
        String endDate = args.getString("endDate");
        int days = args.getInt("rentalDays", 1);

        // Bind views
        ((ImageView) view.findViewById(R.id.ivCarDetailImage)).setImageResource(imageResId);
        ((TextView) view.findViewById(R.id.tvDetailCarName)).setText(carName);
        ((TextView) view.findViewById(R.id.tvDetailPrice)).setText("₱" + (int) pricePerDay + "/Day");
        ((TextView) view.findViewById(R.id.tvDetailDistance)).setText(distanceKm + " KM Away");
        ((TextView) view.findViewById(R.id.tvDetailTransmission)).setText(transmission);
        ((TextView) view.findViewById(R.id.tvDetailSeats)).setText(seats + " Seats");
        ((TextView) view.findViewById(R.id.tvDetailFuel)).setText(fuelType);
        ((TextView) view.findViewById(R.id.tvDetailRating)).setText("★ 4.8");

        // Booking summary
        ((TextView) view.findViewById(R.id.tvRentalPeriod)).setText(days + " day" + (days > 1 ? "s" : ""));
        ((TextView) view.findViewById(R.id.tvPricePerDay)).setText("₱" + String.format("%,.0f", pricePerDay));
        double total = pricePerDay * days;
        ((TextView) view.findViewById(R.id.tvTotalPrice)).setText("₱" + String.format("%,.0f", total));

        // Back button
        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        // Book Now (placeholder for now)
        view.findViewById(R.id.btnBookNow).setOnClickListener(v -> {
            // Will implement booking flow later
        });

        return view;
    }
}