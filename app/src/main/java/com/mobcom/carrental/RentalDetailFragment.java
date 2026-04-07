package com.mobcom.carrental;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.mobcom.carrental.R;
import com.mobcom.carrental.models.Rental;
import com.mobcom.carrental.models.RentalReview;
import com.mobcom.carrental.utils.BookingApiClient;
import com.mobcom.carrental.utils.NotificationStore;
import com.mobcom.carrental.utils.ReviewStore;
import com.mobcom.carrental.utils.SessionManager;

public class RentalDetailFragment extends Fragment {

    private Rental rental;

    // Views
    private ImageView imgCar;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView tvStatusBanner, tvCarName, tvCarPlate;
    private TextView tvRentalId, tvProviderName;
    private TextView tvStartDate, tvEndDate, tvTotalDays, tvPickupLocation;
    private TextView tvDailyRateLabel, tvDailyRateValue, tvServiceFee, tvTotalPrice;
    private TextView dotPending, dotConfirmed, dotActive, dotCompleted;
    private View linePendingConfirmed, lineConfirmedActive, lineActiveCompleted;
    private MaterialButton btnContact, btnPrimary;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rental_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupToolbar(view);

        // Get rental passed from MyRentalsFragment
        if (getArguments() != null) {
            rental = (Rental) getArguments().getSerializable("rental");
        }

        if (rental != null) {
            populateData();
            setupStatusTracker();
            setupActionButtons();
        }
    }

    private void bindViews(View view) {
        imgCar               = view.findViewById(R.id.imgCar);
        collapsingToolbar    = view.findViewById(R.id.collapsingToolbar);
        tvStatusBanner       = view.findViewById(R.id.tvStatusBanner);
        tvCarName            = view.findViewById(R.id.tvCarName);
        tvCarPlate           = view.findViewById(R.id.tvCarPlate);
        tvRentalId           = view.findViewById(R.id.tvRentalId);
        tvProviderName       = view.findViewById(R.id.tvProviderName);
        tvStartDate          = view.findViewById(R.id.tvStartDate);
        tvEndDate            = view.findViewById(R.id.tvEndDate);
        tvTotalDays          = view.findViewById(R.id.tvTotalDays);
        tvPickupLocation     = view.findViewById(R.id.tvPickupLocation);
        tvDailyRateLabel     = view.findViewById(R.id.tvDailyRateLabel);
        tvDailyRateValue     = view.findViewById(R.id.tvDailyRateValue);
        tvServiceFee         = view.findViewById(R.id.tvServiceFee);
        tvTotalPrice         = view.findViewById(R.id.tvTotalPrice);
        dotPending           = view.findViewById(R.id.dotPending);
        dotConfirmed         = view.findViewById(R.id.dotConfirmed);
        dotActive            = view.findViewById(R.id.dotActive);
        dotCompleted         = view.findViewById(R.id.dotCompleted);
        linePendingConfirmed = view.findViewById(R.id.linePendingConfirmed);
        lineConfirmedActive  = view.findViewById(R.id.lineConfirmedActive);
        lineActiveCompleted  = view.findViewById(R.id.lineActiveCompleted);
        btnContact           = view.findViewById(R.id.btnContact);
        btnPrimary           = view.findViewById(R.id.btnPrimary);
    }

    private void setupToolbar(View view) {
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());
    }

    private void populateData() {
        // Car image + title
        collapsingToolbar.setTitle(rental.getCarName());
        Glide.with(this)
                .load(rental.getCarImageUrl())
                .centerCrop()
                .placeholder(R.drawable.placeholder_car)
                .into(imgCar);

        // Status banner
        tvStatusBanner.setText(getStatusLabel(rental.getStatus()));
        tvStatusBanner.setBackgroundColor(getStatusColor(rental.getStatus()));

        // Booking info
        tvCarName.setText(rental.getCarName());
        tvCarPlate.setText("Plate: " + rental.getCarPlate());
        tvRentalId.setText("#" + rental.getRentalId());
        tvProviderName.setText(rental.getProviderName());

        // Dates
        tvStartDate.setText(rental.getStartDate());
        tvEndDate.setText(rental.getEndDate());
        tvTotalDays.setText(rental.getTotalDays() + " days");
        tvPickupLocation.setText(rental.getPickupLocation());

        // Payment — assume service fee is 5% of total
        double serviceFee = rental.getTotalPrice() * 0.05;
        double basePrice  = rental.getTotalPrice() - serviceFee;
        double dailyRate  = basePrice / rental.getTotalDays();

        tvDailyRateLabel.setText("₱" + String.format("%,.0f", dailyRate)
                + " × " + rental.getTotalDays() + " days");
        tvDailyRateValue.setText("₱" + String.format("%,.0f", basePrice));
        tvServiceFee.setText("₱" + String.format("%,.0f", serviceFee));
        tvTotalPrice.setText("₱" + String.format("%,.0f", rental.getTotalPrice()));
    }

    private void setupStatusTracker() {
        // Reset all to inactive
        setStepInactive(dotPending, linePendingConfirmed);
        setStepInactive(dotConfirmed, lineConfirmedActive);
        setStepInactive(dotActive, lineActiveCompleted);
        dotCompleted.getBackground().setTint(Color.parseColor("#E0E0E0"));
        dotCompleted.setTextColor(Color.parseColor("#9E9E9E"));

        // Activate steps up to current status
        switch (rental.getStatus()) {
            case COMPLETED:
                setStepDone(dotCompleted, lineActiveCompleted);
                // fall through
            case ACTIVE:
                setStepDone(dotActive, lineConfirmedActive);
                // fall through
            case CONFIRMED:
                setStepDone(dotConfirmed, linePendingConfirmed);
                // fall through
            case PENDING:
                setStepDone(dotPending, null);
                break;
            case CANCELLED:
                // Show only pending as reached, rest grey
                setStepDone(dotPending, null);
                break;
        }
    }

    private void setStepDone(TextView dot, @Nullable View line) {
        dot.getBackground().setTint(Color.parseColor("#1A237E"));
        dot.setTextColor(Color.WHITE);
        if (line != null) line.setBackgroundColor(Color.parseColor("#1A237E"));
    }

    private void setStepInactive(TextView dot, @Nullable View line) {
        dot.getBackground().setTint(Color.parseColor("#E0E0E0"));
        dot.setTextColor(Color.parseColor("#9E9E9E"));
        if (line != null) line.setBackgroundColor(Color.parseColor("#E0E0E0"));
    }

    private void setupActionButtons() {
        // Contact provider always available
        btnContact.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("threadId", rental.getRentalId());
            args.putString("peerName", rental.getProviderName());
            androidx.navigation.Navigation.findNavController(v)
                    .navigate(R.id.messagesFragment, args);
        });

        // Primary button changes based on status
        switch (rental.getStatus()) {
            case PENDING:
            case CONFIRMED:
                btnPrimary.setText("Cancel Booking");
                btnPrimary.setOnClickListener(v -> showCancelDialog());
                break;
            case ACTIVE:
                btnPrimary.setText("View on Map");
                btnPrimary.setOnClickListener(v -> {
                    String query = Uri.encode(rental.getPickupLocation());
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q=" + query));
                    startActivity(intent);
                });
                break;
            case COMPLETED:
                RentalReview existingReview = ReviewStore.getReview(rental.getRentalId());
                if (existingReview == null) {
                    btnPrimary.setText("Rate & Review");
                    btnPrimary.setOnClickListener(v -> ReviewDialogHelper.show(
                            requireContext(),
                            rental.getCarName(),
                            review -> {
                                ReviewStore.saveReview(rental.getRentalId(), review);
                                Toast.makeText(requireContext(), "Thanks for your review!", Toast.LENGTH_SHORT).show();
                                setupActionButtons();
                            }
                    ));
                } else {
                    btnPrimary.setText("Rebook");
                    btnPrimary.setOnClickListener(v -> {
                        Bundle args = new Bundle();
                        args.putString("prefillLocation", rental.getPickupLocation());
                        androidx.navigation.Navigation.findNavController(v)
                                .navigate(R.id.exploreFragment, args);
                    });
                }
                break;
            case CANCELLED:
                btnPrimary.setVisibility(View.GONE);
                break;
        }
    }

    private void showCancelDialog() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel booking #"
                        + rental.getRentalId() + "? This action cannot be undone.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                String originalText = btnPrimary.getText().toString();
                btnPrimary.setEnabled(false);
                btnPrimary.setText("Cancelling...");

                BookingApiClient.cancelBooking(rental.getRentalId(), new BookingApiClient.CancelBookingCallback() {
                    @Override
                    public void onSuccess() {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            NotificationStore.pushBookingStatusNotification(
                                requireContext(),
                                SessionManager.ROLE_PROVIDER,
                                rental.getRentalId(),
                                "Booking cancelled by customer",
                                rental.getCarName() + " was cancelled for "
                                    + rental.getStartDate() + " to " + rental.getEndDate()
                            );

                            rental = new Rental(
                                rental.getRentalId(),
                                rental.getCarName(),
                                rental.getCarImageUrl(),
                                rental.getCarPlate(),
                                rental.getPickupLocation(),
                                rental.getStartDate(),
                                rental.getEndDate(),
                                rental.getTotalDays(),
                                rental.getTotalPrice(),
                                Rental.Status.CANCELLED,
                                rental.getProviderName()
                            );
                            populateData();
                            setupStatusTracker();
                            setupActionButtons();
                            Toast.makeText(requireContext(), "Booking cancelled", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onError(@NonNull String message) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            btnPrimary.setEnabled(true);
                            btnPrimary.setText(originalText);
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
                })
                .setNegativeButton("Keep Booking", null)
                .show();
    }

    private String getStatusLabel(Rental.Status status) {
        switch (status) {
            case PENDING:   return "⏳  Awaiting confirmation from provider";
            case CONFIRMED: return "✓  Booking confirmed — get ready!";
            case ACTIVE:    return "🔑  Rental is currently active";
            case COMPLETED: return "✅  Rental completed";
            case CANCELLED: return "✕  This booking was cancelled";
            default:        return "";
        }
    }

    private int getStatusColor(Rental.Status status) {
        switch (status) {
            case PENDING:   return Color.parseColor("#FF9800");
            case CONFIRMED: return Color.parseColor("#1A237E");
            case ACTIVE:    return Color.parseColor("#2E7D32");
            case COMPLETED: return Color.parseColor("#546E7A");
            case CANCELLED: return Color.parseColor("#C62828");
            default:        return Color.GRAY;
        }
    }
}