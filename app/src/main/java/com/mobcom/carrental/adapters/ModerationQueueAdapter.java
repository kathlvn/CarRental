package com.mobcom.carrental.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.mobcom.carrental.R;
import com.mobcom.carrental.models.ModerationQueueItem;
import java.util.List;

/**
 * Adapter for moderation queue items
 */
public class ModerationQueueAdapter extends RecyclerView.Adapter<ModerationQueueAdapter.QueueViewHolder> {

    public interface OnQueueActionListener {
        void onApprove(ModerationQueueItem item);
        void onReject(ModerationQueueItem item);
        void onReview(ModerationQueueItem item);
    }

    private List<ModerationQueueItem> items;
    private Context context;
    private OnQueueActionListener listener;

    public ModerationQueueAdapter(Context context, List<ModerationQueueItem> items,
                                  OnQueueActionListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_moderation_queue, parent, false);
        return new QueueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueueViewHolder holder, int position) {
        ModerationQueueItem item = items.get(position);

        // Type badge
        holder.tvType.setText(getTypeLabel(item.getType()));
        holder.tvType.setBackgroundColor(getTypeColor(item.getType()));

        // Priority badge
        holder.tvPriority.setText(item.getPriority().toString());
        holder.tvPriority.setBackgroundColor(getPriorityColor(item.getPriority()));

        // Content
        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
        holder.tvSubmittedBy.setText("by " + item.getSubmittedBy() + " • " + item.getSubmittedAt());

        // Action buttons
        holder.btnApprove.setOnClickListener(v -> listener.onApprove(item));
        holder.btnReject.setOnClickListener(v -> listener.onReject(item));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    private String getTypeLabel(ModerationQueueItem.ItemType type) {
        switch (type) {
            case LISTING: return "📝 Listing";
            case REPORT:  return "📋 Report";
            case PROVIDER: return "👤 Provider";
            default: return "Item";
        }
    }

    private int getTypeColor(ModerationQueueItem.ItemType type) {
        switch (type) {
            case LISTING:  return Color.parseColor("#FFF3E0");
            case REPORT:   return Color.parseColor("#FFEBEE");
            case PROVIDER: return Color.parseColor("#F3E5F5");
            default: return Color.parseColor("#EEEEEE");
        }
    }

    private int getPriorityColor(ModerationQueueItem.Priority priority) {
        switch (priority) {
            case LOW:      return Color.parseColor("#C8E6C9");
            case MEDIUM:   return Color.parseColor("#FFE0B2");
            case HIGH:     return Color.parseColor("#FFCCBC");
            case CRITICAL: return Color.parseColor("#FFCDD2");
            default: return Color.parseColor("#EEEEEE");
        }
    }

    public void updateList(List<ModerationQueueItem> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }

    static class QueueViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvPriority, tvTitle, tvDescription, tvSubmittedBy;
        MaterialButton btnApprove, btnReject;

        QueueViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType       = itemView.findViewById(R.id.tvType);
            tvPriority   = itemView.findViewById(R.id.tvPriority);
            tvTitle      = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvSubmittedBy = itemView.findViewById(R.id.tvSubmittedBy);
            btnApprove   = itemView.findViewById(R.id.btnApprove);
            btnReject    = itemView.findViewById(R.id.btnReject);
        }
    }
}
