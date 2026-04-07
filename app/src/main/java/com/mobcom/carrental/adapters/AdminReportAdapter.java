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
import com.google.android.material.button.MaterialButton;
import com.mobcom.carrental.R;
import com.mobcom.carrental.models.AdminReport;
import java.util.List;

public class AdminReportAdapter
        extends RecyclerView.Adapter<AdminReportAdapter.ReportViewHolder> {

    public interface OnReportActionListener {
        void onDismiss(AdminReport report);
        void onEscalate(AdminReport report);
        void onResolve(AdminReport report);
    }

    private List<AdminReport> reports;
    private Context context;
    private OnReportActionListener listener;

    public AdminReportAdapter(Context context, List<AdminReport> reports,
                              OnReportActionListener listener) {
        this.context  = context;
        this.reports  = reports;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_report, parent, false);
        return new ReportViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        AdminReport report = reports.get(position);

        // Severity bar
        int severityColor = getSeverityColor(report.getSeverity());
        holder.viewSeverityBar.setBackgroundColor(severityColor);

        // Badges
        holder.tvCategory.setText(getCategoryLabel(report.getCategory()));
        holder.tvCategory.getBackground().setTint(getCategoryColor(report.getCategory()));

        holder.tvSeverity.setText(getSeverityLabel(report.getSeverity()));
        holder.tvSeverity.getBackground().setTint(severityColor);

        holder.tvStatus.setText(getStatusLabel(report.getStatus()));
        holder.tvStatus.getBackground().setTint(getStatusColor(report.getStatus()));

        // Content
        holder.tvTitle.setText(report.getTitle());
        holder.tvDescription.setText(report.getDescription());
        holder.tvReporter.setText(report.getReporterName());
        holder.tvReported.setText(report.getReportedName());
        holder.tvDate.setText(report.getDate());

        // Actions
        if (report.getStatus() == AdminReport.Status.OPEN
                || report.getStatus() == AdminReport.Status.ESCALATED) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.layoutResolution.setVisibility(View.GONE);
            holder.btnDismiss.setOnClickListener(v -> listener.onDismiss(report));
            holder.btnEscalate.setOnClickListener(v -> listener.onEscalate(report));
            holder.btnResolve.setOnClickListener(v -> listener.onResolve(report));
        } else {
            holder.layoutActions.setVisibility(View.GONE);
            if (report.getResolution() != null && !report.getResolution().isEmpty()) {
                holder.layoutResolution.setVisibility(View.VISIBLE);
                holder.tvResolution.setText(report.getResolution());
            }
        }
    }

    private String getCategoryLabel(AdminReport.Category cat) {
        switch (cat) {
            case SCAM:              return "🚨 Scam";
            case HARASSMENT:        return "⚠ Harassment";
            case VEHICLE_CONDITION: return "🚗 Vehicle";
            case NO_SHOW:           return "❌ No Show";
            case OVERCHARGING:      return "💰 Overcharging";
            default:                return "📋 Other";
        }
    }

    private int getCategoryColor(AdminReport.Category cat) {
        switch (cat) {
            case SCAM:              return Color.parseColor("#B71C1C");
            case HARASSMENT:        return Color.parseColor("#E53935");
            case VEHICLE_CONDITION: return Color.parseColor("#FF9800");
            case NO_SHOW:           return Color.parseColor("#546E7A");
            case OVERCHARGING:      return Color.parseColor("#7B1FA2");
            default:                return Color.parseColor("#1A237E");
        }
    }

    private String getSeverityLabel(AdminReport.Severity sev) {
        switch (sev) {
            case CRITICAL: return "🔴 Critical";
            case HIGH:     return "🟠 High";
            case MEDIUM:   return "🟡 Medium";
            default:       return "🟢 Low";
        }
    }

    private int getSeverityColor(AdminReport.Severity sev) {
        switch (sev) {
            case CRITICAL: return Color.parseColor("#B71C1C");
            case HIGH:     return Color.parseColor("#E53935");
            case MEDIUM:   return Color.parseColor("#FF9800");
            default:       return Color.parseColor("#2E7D32");
        }
    }

    private String getStatusLabel(AdminReport.Status status) {
        switch (status) {
            case OPEN:      return "⏳ Open";
            case ESCALATED: return "🔺 Escalated";
            case RESOLVED:  return "✅ Resolved";
            case DISMISSED: return "✕ Dismissed";
            default:        return "";
        }
    }

    private int getStatusColor(AdminReport.Status status) {
        switch (status) {
            case OPEN:      return Color.parseColor("#FF9800");
            case ESCALATED: return Color.parseColor("#E53935");
            case RESOLVED:  return Color.parseColor("#2E7D32");
            case DISMISSED: return Color.parseColor("#9E9E9E");
            default:        return Color.GRAY;
        }
    }

    public void updateList(List<AdminReport> newList) {
        this.reports = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() { return reports.size(); }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        View viewSeverityBar;
        TextView tvCategory, tvSeverity, tvStatus;
        TextView tvTitle, tvDescription;
        TextView tvReporter, tvReported, tvDate;
        TextView tvResolution;
        LinearLayout layoutActions, layoutResolution;
        MaterialButton btnDismiss, btnEscalate, btnResolve;

        ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            viewSeverityBar  = itemView.findViewById(R.id.viewSeverityBar);
            tvCategory       = itemView.findViewById(R.id.tvCategory);
            tvSeverity       = itemView.findViewById(R.id.tvSeverity);
            tvStatus         = itemView.findViewById(R.id.tvStatus);
            tvTitle          = itemView.findViewById(R.id.tvTitle);
            tvDescription    = itemView.findViewById(R.id.tvDescription);
            tvReporter       = itemView.findViewById(R.id.tvReporter);
            tvReported       = itemView.findViewById(R.id.tvReported);
            tvDate           = itemView.findViewById(R.id.tvDate);
            tvResolution     = itemView.findViewById(R.id.tvResolution);
            layoutActions    = itemView.findViewById(R.id.layoutActions);
            layoutResolution = itemView.findViewById(R.id.layoutResolution);
            btnDismiss       = itemView.findViewById(R.id.btnDismiss);
            btnEscalate      = itemView.findViewById(R.id.btnEscalate);
            btnResolve       = itemView.findViewById(R.id.btnResolve);
        }
    }
}