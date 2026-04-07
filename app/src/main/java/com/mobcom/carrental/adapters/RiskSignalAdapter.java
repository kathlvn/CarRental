package com.mobcom.carrental.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mobcom.carrental.R;
import com.mobcom.carrental.models.RiskSignal;
import java.util.List;

/**
 * Adapter for risk signals/alerts
 */
public class RiskSignalAdapter extends RecyclerView.Adapter<RiskSignalAdapter.RiskViewHolder> {

    private List<RiskSignal> signals;
    private Context context;

    public RiskSignalAdapter(Context context, List<RiskSignal> signals) {
        this.context = context;
        this.signals = signals;
    }

    @NonNull
    @Override
    public RiskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_risk_signal, parent, false);
        return new RiskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RiskViewHolder holder, int position) {
        RiskSignal signal = signals.get(position);

        // Severity indicator
        int bgColor = getSeverityBgColor(signal.getSeverity());
        holder.containerBg.setBackgroundColor(bgColor);

        holder.tvSeverity.setText(signal.getSeverity().toString());
        holder.tvSeverity.setBackgroundColor(getSeverityColor(signal.getSeverity()));

        // Content
        holder.tvTitle.setText(signal.getTitle());
        holder.tvDescription.setText(signal.getDescription());
        holder.tvEntity.setText(signal.getAffectedEntity());
        holder.tvMetric.setText(signal.getMetric());
        holder.tvTimestamp.setText(signal.getTimestamp());

        // Resolved state
        if (signal.isResolved()) {
            holder.tvResolved.setVisibility(View.VISIBLE);
        } else {
            holder.tvResolved.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return signals != null ? signals.size() : 0;
    }

    private int getSeverityColor(RiskSignal.Severity severity) {
        switch (severity) {
            case LOW:      return Color.parseColor("#66BB6A");
            case MEDIUM:   return Color.parseColor("#FFA726");
            case HIGH:     return Color.parseColor("#EF5350");
            case CRITICAL: return Color.parseColor("#D32F2F");
            default: return Color.parseColor("#757575");
        }
    }

    private int getSeverityBgColor(RiskSignal.Severity severity) {
        switch (severity) {
            case LOW:      return Color.parseColor("#F1F8E9");
            case MEDIUM:   return Color.parseColor("#FFF3E0");
            case HIGH:     return Color.parseColor("#FFEBEE");
            case CRITICAL: return Color.parseColor("#FCE4EC");
            default: return Color.parseColor("#FAFAFA");
        }
    }

    public void updateList(List<RiskSignal> newList) {
        this.signals = newList;
        notifyDataSetChanged();
    }

    static class RiskViewHolder extends RecyclerView.ViewHolder {
        LinearLayout containerBg;
        TextView tvSeverity, tvTitle, tvDescription, tvEntity, tvMetric, tvTimestamp, tvResolved;

        RiskViewHolder(@NonNull View itemView) {
            super(itemView);
            containerBg  = itemView.findViewById(R.id.containerBg);
            tvSeverity   = itemView.findViewById(R.id.tvSeverity);
            tvTitle      = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvEntity     = itemView.findViewById(R.id.tvEntity);
            tvMetric     = itemView.findViewById(R.id.tvMetric);
            tvTimestamp  = itemView.findViewById(R.id.tvTimestamp);
            tvResolved   = itemView.findViewById(R.id.tvResolved);
        }
    }
}
