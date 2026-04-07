package com.mobcom.carrental.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mobcom.carrental.R;
import com.mobcom.carrental.models.AppNotification;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final Context context;
    private List<AppNotification> items;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());

    public NotificationAdapter(Context context, List<AppNotification> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        AppNotification item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvMessage.setText(item.getMessage());
        holder.tvTime.setText(dateFormat.format(new Date(item.getCreatedAt())));
        holder.tvBookingId.setText("Booking #" + item.getBookingId());

        if (item.isRead()) {
            holder.viewUnreadDot.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.WHITE);
        } else {
            holder.viewUnreadDot.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.parseColor("#F8FBFF"));
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void update(List<AppNotification> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        final View viewUnreadDot;
        final TextView tvTitle;
        final TextView tvMessage;
        final TextView tvBookingId;
        final TextView tvTime;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            viewUnreadDot = itemView.findViewById(R.id.viewUnreadDot);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}