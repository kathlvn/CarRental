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
import com.mobcom.carrental.utils.SessionManager;

public class CarDetailFragment extends Fragment {

    private Car car;
    private String startDate;
    private String endDate;
    private int rentalDays;

    public static CarDetailFragment newInstance(Car car, String startDate,
                                                String endDate, int rentalDays) {
        CarDetailFragment fragment = new CarDetailFragment();
        Bundle args = new Bundle();
        args.putString("carName", car.getName());
        args.putString("transmission", car.getTransmission());
        args.putInt("seats", car.getSeats());
        args.putString("fuelType", car.getFuelType());
        args.putDouble("pricePerDay", car.getPricePerDay());
        args.putFloat("distanceKm", car.getDistanceKm());
        args.putInt("imageResId", car.getImageResId());
        args.putString("imageUrl", car.getImageUrl());
        args.putString("location", car.getLocation());
        args.putString("providerName", car.getProviderName());
        args.putString("providerId", car.getProviderId());
        args.putString("plateNumber", car.getPlateNumber());
        args.putString("carType", car.getCarType());
        args.putFloat("rating", car.getRating());
        args.putString("carId", car.getId());
        args.putString("startDate", startDate);
        args.putString("endDate", endDate);
        args.putInt("rentalDays", rentalDays);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_detail, container, false);

        Bundle args = getArguments();
        if (args == null) return view;

        // Rebuild Car object from arguments
        car = new Car(
                args.getString("carId", ""),
                args.getString("carName", ""),
                args.getString("transmission", ""),
                args.getInt("seats", 0),
                args.getString("fuelType", ""),
                args.getDouble("pricePerDay", 0),
                args.getFloat("distanceKm", 0),
                args.getInt("imageResId", 0),
                args.getString("imageUrl", ""),
                args.getString("location", ""),
                args.getString("providerName", ""),
                args.getString("providerId", ""),
                args.getString("plateNumber", ""),
                args.getString("carType", ""),
                args.getFloat("rating", 0f)
        );

        startDate  = args.getString("startDate", "");
        endDate    = args.getString("endDate", "");
        rentalDays = args.getInt("rentalDays", 1);

        // Bind views
        ((ImageView) view.findViewById(R.id.ivCarDetailImage))
                .setImageResource(car.getImageResId());
        ((TextView) view.findViewById(R.id.tvDetailCarName))
                .setText(car.getName());
        ((TextView) view.findViewById(R.id.tvDetailPrice))
                .setText("₱" + (int) car.getPricePerDay() + "/Day");
        ((TextView) view.findViewById(R.id.tvDetailDistance))
                .setText(car.getDistanceKm() + " KM Away");
        ((TextView) view.findViewById(R.id.tvDetailTransmission))
                .setText(car.getTransmission());
        ((TextView) view.findViewById(R.id.tvDetailSeats))
                .setText(car.getSeats() + " Seats");
        ((TextView) view.findViewById(R.id.tvDetailFuel))
                .setText(car.getFuelType());
        ((TextView) view.findViewById(R.id.tvDetailRating))
                .setText("★ " + (car.getRating() > 0 ? car.getRating() : "4.8"));

        // Booking summary
        ((TextView) view.findViewById(R.id.tvRentalPeriod))
                .setText(rentalDays + " day" + (rentalDays > 1 ? "s" : ""));
        ((TextView) view.findViewById(R.id.tvPricePerDay))
                .setText("₱" + String.format("%,.0f", car.getPricePerDay()));
        double total = car.getPricePerDay() * rentalDays;
        ((TextView) view.findViewById(R.id.tvTotalPrice))
                .setText("₱" + String.format("%,.0f", total));

        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                androidx.navigation.fragment.NavHostFragment
                        .findNavController(CarDetailFragment.this)
                        .popBackStack());

        view.findViewById(R.id.btnMessageProvider).setOnClickListener(v -> {
            Bundle chatArgs = new Bundle();
            chatArgs.putString("threadId", "provider-" + car.getProviderId());
            chatArgs.putString("peerName", car.getProviderName());
            androidx.navigation.fragment.NavHostFragment
                    .findNavController(CarDetailFragment.this)
                    .navigate(R.id.messagesFragment, chatArgs);
        });

        view.findViewById(R.id.btnBookNow).setOnClickListener(v -> {
            SessionManager session = new SessionManager(requireContext());

            if (session.isGuest()) {
                                GuestLoginWallBottomSheet
                                                .newInstance("book_now")
                                                .show(getParentFragmentManager(), "GuestLoginWall");
                return;
            }

            Bundle bookingArgs = new Bundle();
            bookingArgs.putSerializable("car", car);
            bookingArgs.putString("startDate", startDate);
            bookingArgs.putString("endDate", endDate);
            bookingArgs.putInt("totalDays", rentalDays);
            androidx.navigation.fragment.NavHostFragment
                    .findNavController(CarDetailFragment.this)
                    .navigate(R.id.action_carDetail_to_bookingForm, bookingArgs);
        });

        return view;
    }
}