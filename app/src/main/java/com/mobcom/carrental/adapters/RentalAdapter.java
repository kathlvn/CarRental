// com/mobcom/carrental/adapters/RentalAdapter.java
package com.mobcom.carrental.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.mobcom.carrental.R;
import com.mobcom.carrental.models.RentalReview;
import com.mobcom.carrental.models.Rental;
import com.mobcom.carrental.utils.ReviewStore;
import java.util.List;

public class RentalAdapter extends RecyclerView.Adapter<RentalAdapter.RentalViewHolder> {

    public interface OnRentalActionListener {
        void onViewDetails(Rental rental);
        void onCancelRental(Rental rental);
        void onRebook(Rental rental);
        void onRateReview(Rental rental);
    }

    private List<Rental> rentals;
    private Context context;
    private OnRentalActionListener listener;

    public RentalAdapter(Context context, List<Rental> rentals, OnRentalActionListener listener) {
        this.context = context;
        this.rentals = rentals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RentalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_rental_card, parent, false);
        return new RentalViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RentalViewHolder holder, int position) {
        Rental rental = rentals.get(position);

        holder.tvCarName.setText(rental.getCarName());
        holder.tvRentalId.setText("Booking #" + rental.getRentalId());
        holder.tvProviderName.setText(rental.getProviderName());
        holder.tvStartDate.setText(rental.getStartDate());
        holder.tvEndDate.setText(rental.getEndDate());
        holder.tvTotalDays.setText(rental.getTotalDays() + "d");
        holder.tvPickupLocation.setText(rental.getPickupLocation());
        holder.tvTotalPrice.setText("₱" + String.format("%,.0f", rental.getTotalPrice()));

        // Status badge
        String statusLabel = getStatusLabel(rental.getStatus());
        holder.tvStatus.setText(statusLabel);
        holder.tvStatus.getBackground().setTint(getStatusColor(rental.getStatus()));

        // Action button changes based on status
        switch (rental.getStatus()) {
            case PENDING:
            case CONFIRMED:
                holder.btnAction.setText("Cancel");
                holder.btnAction.setOnClickListener(v -> listener.onCancelRental(rental));
                break;
            case COMPLETED:
                RentalReview review = ReviewStore.getReview(rental.getRentalId());
                if (review == null) {
                    holder.btnAction.setText("Rate & Review");
                    holder.btnAction.setOnClickListener(v -> listener.onRateReview(rental));
                } else {
                    holder.btnAction.setText("Rebook");
                    holder.btnAction.setOnClickListener(v -> listener.onRebook(rental));
                }
                break;
            case CANCELLED:
                holder.btnAction.setText("Rebook");
                holder.btnAction.setOnClickListener(v -> listener.onRebook(rental));
                break;
            default:
                holder.btnAction.setText("View Details");
                holder.btnAction.setOnClickListener(v -> listener.onViewDetails(rental));
        }

        // Car image
        if (rental.getCarImageUrl() != null && !rental.getCarImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(rental.getCarImageUrl())
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.imgCar);
        }

        holder.itemView.setOnClickListener(v -> listener.onViewDetails(rental));
    }

    private String getStatusLabel(Rental.Status status) {
        switch (status) {
            case PENDING:    return "⏳ Pending";
            case CONFIRMED:  return "✓ Confirmed";
            case ACTIVE:     return "🔑 Active";
            case COMPLETED:  return "✅ Completed";
            case CANCELLED:  return "✕ Cancelled";
            default:         return "Unknown";
        }
    }

    private int getStatusColor(Rental.Status status) {
        switch (status) {
            case PENDING:    return Color.parseColor("#FF9800");
            case CONFIRMED:  return Color.parseColor("#1A237E");
            case ACTIVE:     return Color.parseColor("#2E7D32");
            case COMPLETED:  return Color.parseColor("#546E7A");
            case CANCELLED:  return Color.parseColor("#C62828");
            default:         return Color.GRAY;
        }
    }

    public void updateList(List<Rental> newList) {
        this.rentals = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() { return rentals.size(); }

    static class RentalViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCar;
        TextView tvCarName, tvStatus, tvRentalId, tvProviderName;
        TextView tvStartDate, tvEndDate, tvTotalDays, tvPickupLocation, tvTotalPrice;
        MaterialButton btnAction;

        RentalViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCar           = itemView.findViewById(R.id.imgCar);
            tvCarName        = itemView.findViewById(R.id.tvCarName);
            tvStatus         = itemView.findViewById(R.id.tvStatus);
            tvRentalId       = itemView.findViewById(R.id.tvRentalId);
            tvProviderName   = itemView.findViewById(R.id.tvProviderName);
            tvStartDate      = itemView.findViewById(R.id.tvStartDate);
            tvEndDate        = itemView.findViewById(R.id.tvEndDate);
            tvTotalDays      = itemView.findViewById(R.id.tvTotalDays);
            tvPickupLocation = itemView.findViewById(R.id.tvPickupLocation);
            tvTotalPrice     = itemView.findViewById(R.id.tvTotalPrice);
            btnAction        = itemView.findViewById(R.id.btnAction);
        }
    }
}