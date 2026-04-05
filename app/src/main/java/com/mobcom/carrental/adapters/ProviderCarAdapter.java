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
import com.mobcom.carrental.models.ProviderCar;
import java.util.List;

public class ProviderCarAdapter extends RecyclerView.Adapter<ProviderCarAdapter.CarViewHolder> {

    public interface OnCarActionListener {
        void onEdit(ProviderCar car);
        void onToggleStatus(ProviderCar car);
    }

    private List<ProviderCar> cars;
    private Context context;
    private OnCarActionListener listener;

    public ProviderCarAdapter(Context context, List<ProviderCar> cars, OnCarActionListener listener) {
        this.context = context;
        this.cars = cars;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_provider_car, parent, false);
        return new CarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        ProviderCar car = cars.get(position);

        holder.tvCarName.setText(car.getFullName());
        holder.tvPlate.setText(car.getPlateNumber());
        holder.tvPrice.setText("₱" + String.format("%,.0f", car.getPricePerDay()) + "/day");
        holder.tvBookings.setText(String.valueOf(car.getTotalBookings()));
        holder.tvRating.setText(car.getRating() > 0
                ? "⭐ " + car.getRating()
                : "No ratings");

        // Status badge
        switch (car.getStatus()) {
            case ACTIVE:
                holder.tvStatus.setText("● Active");
                holder.tvStatus.getBackground().setTint(Color.parseColor("#2E7D32"));
                holder.btnToggleStatus.setText("Deactivate");
                break;
            case INACTIVE:
                holder.tvStatus.setText("● Inactive");
                holder.tvStatus.getBackground().setTint(Color.parseColor("#616161"));
                holder.btnToggleStatus.setText("Activate");
                break;
            case PENDING_REVIEW:
                holder.tvStatus.setText("⏳ Pending Review");
                holder.tvStatus.getBackground().setTint(Color.parseColor("#FF9800"));
                holder.btnToggleStatus.setText("Activate");
                break;
        }

        // Car image
        Glide.with(context)
                .load(car.getImageUrl())
                .centerCrop()
                .placeholder(R.drawable.placeholder_car)
                .into(holder.imgCar);

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(car));
        holder.btnToggleStatus.setOnClickListener(v -> listener.onToggleStatus(car));
    }

    public void updateList(List<ProviderCar> newList) {
        this.cars = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() { return cars.size(); }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCar;
        TextView tvCarName, tvStatus, tvPrice, tvPlate, tvBookings, tvRating;
        MaterialButton btnEdit, btnToggleStatus;

        CarViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCar          = itemView.findViewById(R.id.imgCar);
            tvCarName       = itemView.findViewById(R.id.tvCarName);
            tvStatus        = itemView.findViewById(R.id.tvStatus);
            tvPrice         = itemView.findViewById(R.id.tvPrice);
            tvPlate         = itemView.findViewById(R.id.tvPlate);
            tvBookings      = itemView.findViewById(R.id.tvBookings);
            tvRating        = itemView.findViewById(R.id.tvRating);
            btnEdit         = itemView.findViewById(R.id.btnEdit);
            btnToggleStatus = itemView.findViewById(R.id.btnToggleStatus);
        }
    }
}