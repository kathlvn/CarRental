package com.mobcom.carrental.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mobcom.carrental.R;
import com.mobcom.carrental.models.ActivityEvent;
import java.util.List;

/**
 * Adapter for displaying recent activity feed in admin dashboard
 */
public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ActivityViewHolder> {

    private List<ActivityEvent> activities;
    private Context context;

    public RecentActivityAdapter(Context context, List<ActivityEvent> activities) {
        this.context = context;
        this.activities = activities;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_recent_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityEvent event = activities.get(position);

        holder.tvIcon.setText(event.getIcon());
        holder.tvTitle.setText(event.getTitle());
        holder.tvDescription.setText(event.getDescription());
        holder.tvTimestamp.setText(event.getTimestamp());
        holder.tvActor.setText(event.getActor());

        // Set background color based on event type
        int bgColor = getEventTypeColor(event.getType());
        holder.itemView.setBackgroundColor(bgColor);
    }

    @Override
    public int getItemCount() {
        return activities != null ? activities.size() : 0;
    }

    private int getEventTypeColor(ActivityEvent.Type type) {
        switch (type) {
            case BOOKING_CREATED:
            case BOOKING_COMPLETED:
                return 0xFFF1F8E9;    // Light green
            case LISTING_SUBMITTED:
            case LISTING_APPROVED:
                return 0xFFFFF3E0;    // Light orange
            case LISTING_REJECTED:
                return 0xFFFFEBEE;    // Light red
            case REPORT_FILED:
            case REPORT_RESOLVED:
                return 0xFFFFF9C4;    // Light yellow
            case PROVIDER_FLAGGED:
            case PROVIDER_SUSPENDED:
                return 0xFFF3E5F5;    // Light purple
            default:
                return 0xFFFFFFFF;    // White
        }
    }

    public void updateList(List<ActivityEvent> newList) {
        this.activities = newList;
        notifyDataSetChanged();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvTitle, tvDescription, tvTimestamp, tvActor;

        ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon        = itemView.findViewById(R.id.tvIcon);
            tvTitle       = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTimestamp   = itemView.findViewById(R.id.tvTimestamp);
            tvActor       = itemView.findViewById(R.id.tvActor);
        }
    }
}
