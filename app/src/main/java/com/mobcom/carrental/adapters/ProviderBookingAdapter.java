package com.mobcom.carrental.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.mobcom.carrental.R;
import com.mobcom.carrental.models.ProviderBooking;
import java.util.List;

public class ProviderBookingAdapter
        extends RecyclerView.Adapter<ProviderBookingAdapter.BookingViewHolder> {

    public interface OnBookingActionListener {
        void onAccept(ProviderBooking booking);
        void onReject(ProviderBooking booking);
        void onContact(ProviderBooking booking);
        void onViewDetail(ProviderBooking booking);
    }

    private List<ProviderBooking> bookings;
    private Context context;
    private OnBookingActionListener listener;

    public ProviderBookingAdapter(Context context,
                                  List<ProviderBooking> bookings,
                                  OnBookingActionListener listener) {
        this.context  = context;
        this.bookings = bookings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_provider_booking, parent, false);
        return new BookingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        ProviderBooking booking = bookings.get(position);

        // Car info
        holder.tvCarName.setText(booking.getCarName());
        holder.tvCarPlate.setText(booking.getCarPlate());
        holder.tvCreatedAt.setText("Requested " + booking.getCreatedAt());

        // Customer info
        holder.tvCustomerName.setText(booking.getCustomerName());
        holder.tvCustomerPhone.setText(booking.getCustomerPhone());

        // Dates
        holder.tvStartDate.setText(booking.getStartDate());
        holder.tvEndDate.setText(booking.getEndDate());
        holder.tvTotalDays.setText(booking.getTotalDays() + " days");
        holder.tvTotalAmount.setText("₱" + String.format("%,.0f", booking.getTotalAmount()));

        // Status
        holder.tvStatus.setText(getStatusLabel(booking.getStatus()));
        holder.tvStatus.getBackground().setTint(getStatusColor(booking.getStatus()));
        holder.viewStatusBar.setBackgroundColor(getStatusColor(booking.getStatus()));

        // Show accept/reject only for PENDING
        if (booking.getStatus() == ProviderBooking.Status.PENDING) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnAccept.setOnClickListener(v -> listener.onAccept(booking));
            holder.btnReject.setOnClickListener(v -> listener.onReject(booking));
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }

        // Car image
        Glide.with(context)
                .load(booking.getCarImageUrl())
                .centerCrop()
                .placeholder(R.drawable.placeholder_car)
                .into(holder.imgCar);

        holder.btnContact.setOnClickListener(v -> listener.onContact(booking));
        holder.itemView.setOnClickListener(v -> listener.onViewDetail(booking));
    }

    private String getStatusLabel(ProviderBooking.Status status) {
        switch (status) {
            case PENDING:   return "⏳ Pending";
            case CONFIRMED: return "✓ Confirmed";
            case REJECTED:  return "✕ Rejected";
            case ACTIVE:    return "🔑 Active";
            case COMPLETED: return "✅ Completed";
            case CANCELLED: return "✕ Cancelled";
            default:        return "";
        }
    }

    private int getStatusColor(ProviderBooking.Status status) {
        switch (status) {
            case PENDING:   return Color.parseColor("#FF9800");
            case CONFIRMED: return Color.parseColor("#1A237E");
            case REJECTED:  return Color.parseColor("#C62828");
            case ACTIVE:    return Color.parseColor("#2E7D32");
            case COMPLETED: return Color.parseColor("#546E7A");
            case CANCELLED: return Color.parseColor("#9E9E9E");
            default:        return Color.GRAY;
        }
    }

    public void updateList(List<ProviderBooking> newList) {
        this.bookings = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() { return bookings.size(); }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        View viewStatusBar;
        ImageView imgCar;
        TextView tvCarName, tvCarPlate, tvCreatedAt, tvStatus;
        TextView tvCustomerName, tvCustomerPhone;
        TextView tvStartDate, tvEndDate, tvTotalDays, tvTotalAmount;
        LinearLayout layoutActions;
        MaterialButton btnAccept, btnReject, btnContact;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            viewStatusBar   = itemView.findViewById(R.id.viewStatusBar);
            imgCar          = itemView.findViewById(R.id.imgCar);
            tvCarName       = itemView.findViewById(R.id.tvCarName);
            tvCarPlate      = itemView.findViewById(R.id.tvCarPlate);
            tvCreatedAt     = itemView.findViewById(R.id.tvCreatedAt);
            tvStatus        = itemView.findViewById(R.id.tvStatus);
            tvCustomerName  = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerPhone = itemView.findViewById(R.id.tvCustomerPhone);
            tvStartDate     = itemView.findViewById(R.id.tvStartDate);
            tvEndDate       = itemView.findViewById(R.id.tvEndDate);
            tvTotalDays     = itemView.findViewById(R.id.tvTotalDays);
            tvTotalAmount   = itemView.findViewById(R.id.tvTotalAmount);
            layoutActions   = itemView.findViewById(R.id.layoutActions);
            btnAccept       = itemView.findViewById(R.id.btnAccept);
            btnReject       = itemView.findViewById(R.id.btnReject);
            btnContact      = itemView.findViewById(R.id.btnContact);
        }
    }
}