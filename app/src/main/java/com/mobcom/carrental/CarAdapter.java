package com.mobcom.carrental;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private Context context;
    private List<Car> carList;

    public CarAdapter(Context context, List<Car> carList) {
        this.context = context;
        this.carList = carList;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_car_card, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);
        holder.tvCarName.setText(car.getName());
        holder.tvDistance.setText(car.getDistanceKm() + " KM Away");
        holder.tvTransmission.setText(car.getTransmission());
        holder.tvSeats.setText(car.getSeats() + " Seats");
        holder.tvFuel.setText(car.getFuelType());
        holder.tvPrice.setText("₱" + (int) car.getPricePerDay() + "/Day");
        if (car.getImageUrl() != null && !car.getImageUrl().isEmpty()) {
            com.bumptech.glide.Glide.with(context)
                    .load(car.getImageUrl())
                    .centerCrop()
                    .placeholder(car.getImageResId() != 0
                            ? car.getImageResId()
                            : R.drawable.placeholder_car)
                    .into(holder.ivCarImage);
        } else {
            holder.ivCarImage.setImageResource(car.getImageResId() != 0
                    ? car.getImageResId()
                    : R.drawable.placeholder_car);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onCarClick(car);
        });
        holder.itemView.findViewById(R.id.btnBook).setOnClickListener(v -> {
            if (clickListener != null) clickListener.onCarClick(car);
        });
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCarImage;
        TextView tvCarName, tvDistance, tvTransmission, tvSeats, tvFuel, tvPrice;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCarImage = itemView.findViewById(R.id.ivCarImage);
            tvCarName = itemView.findViewById(R.id.tvCarName);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvTransmission = itemView.findViewById(R.id.tvTransmission);
            tvSeats = itemView.findViewById(R.id.tvSeats);
            tvFuel = itemView.findViewById(R.id.tvFuel);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }

    public interface OnCarClickListener {
        void onCarClick(Car car);
    }

    private OnCarClickListener clickListener;

    public void setOnCarClickListener(OnCarClickListener listener) {
        this.clickListener = listener;
    }

}