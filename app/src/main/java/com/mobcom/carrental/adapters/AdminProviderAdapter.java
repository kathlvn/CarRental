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
import com.mobcom.carrental.models.AdminProvider;
import java.util.List;

public class AdminProviderAdapter
        extends RecyclerView.Adapter<AdminProviderAdapter.ProviderViewHolder> {

    public interface OnProviderActionListener {
        void onManage(AdminProvider provider);
    }

    private List<AdminProvider> providers;
    private Context context;
    private OnProviderActionListener listener;

    public AdminProviderAdapter(Context context, List<AdminProvider> providers,
                                OnProviderActionListener listener) {
        this.context   = context;
        this.providers = providers;
        this.listener  = listener;
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_provider, parent, false);
        return new ProviderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
        AdminProvider provider = providers.get(position);

        // Avatar
        holder.tvAvatar.setText(provider.getInitials());

        // Info
        holder.tvName.setText(provider.getName());
        holder.tvEmail.setText(provider.getEmail());
        holder.tvLocation.setText("📍 " + provider.getLocation());

        // Stats
        holder.tvListings.setText(provider.getTotalListings()
                + "/" + provider.getApprovedListings() + " approved");
        holder.tvBookings.setText(String.valueOf(provider.getTotalBookings()));
        holder.tvRating.setText(provider.getAverageRating() > 0
                ? "⭐ " + provider.getAverageRating() : "N/A");
        holder.tvReports.setText(String.valueOf(provider.getTotalReports()));

        // Trust bar + badge
        int trustColor = getTrustColor(provider.getTrustLevel());
        holder.viewTrustBar.setBackgroundColor(trustColor);
        holder.tvTrustBadge.setText(getTrustLabel(provider.getTrustLevel()));
        holder.tvTrustBadge.getBackground().setTint(trustColor);

        // Violation warning
        if (provider.hasViolations()) {
            holder.tvViolationWarning.setVisibility(View.VISIBLE);
            holder.tvViolationWarning.setText(
                    buildViolationText(provider));
        } else {
            holder.tvViolationWarning.setVisibility(View.GONE);
        }

        holder.btnManage.setOnClickListener(v -> listener.onManage(provider));
    }

    private String buildViolationText(AdminProvider p) {
        StringBuilder sb = new StringBuilder("⚠ Violations: ");
        if (p.getTotalReports() >= 3)
            sb.append(p.getTotalReports()).append(" reports  ");
        if (p.getAverageRating() > 0 && p.getAverageRating() < 2.5f)
            sb.append("Rating ").append(p.getAverageRating()).append("  ");
        if (p.getCancellationRate() > 0.30f)
            sb.append(Math.round(p.getCancellationRate() * 100)).append("% cancellation rate");
        return sb.toString().trim();
    }

    private String getTrustLabel(AdminProvider.TrustLevel level) {
        switch (level) {
            case TRUSTED:   return "✓ Trusted";
            case FLAGGED:   return "⚠ Flagged";
            case SUSPENDED: return "✕ Suspended";
            default:        return "⏳ Probation";
        }
    }

    private int getTrustColor(AdminProvider.TrustLevel level) {
        switch (level) {
            case TRUSTED:   return Color.parseColor("#2E7D32");
            case FLAGGED:   return Color.parseColor("#E53935");
            case SUSPENDED: return Color.parseColor("#B71C1C");
            default:        return Color.parseColor("#FF9800");
        }
    }

    public void updateList(List<AdminProvider> newList) {
        this.providers = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() { return providers.size(); }

    static class ProviderViewHolder extends RecyclerView.ViewHolder {
        View viewTrustBar;
        TextView tvAvatar, tvName, tvEmail, tvLocation;
        TextView tvListings, tvBookings, tvRating, tvReports;
        TextView tvTrustBadge, tvViolationWarning;
        MaterialButton btnManage;

        ProviderViewHolder(@NonNull View itemView) {
            super(itemView);
            viewTrustBar        = itemView.findViewById(R.id.viewTrustBar);
            tvAvatar            = itemView.findViewById(R.id.tvAvatar);
            tvName              = itemView.findViewById(R.id.tvName);
            tvEmail             = itemView.findViewById(R.id.tvEmail);
            tvLocation          = itemView.findViewById(R.id.tvLocation);
            tvListings          = itemView.findViewById(R.id.tvListings);
            tvBookings          = itemView.findViewById(R.id.tvBookings);
            tvRating            = itemView.findViewById(R.id.tvRating);
            tvReports           = itemView.findViewById(R.id.tvReports);
            tvTrustBadge        = itemView.findViewById(R.id.tvTrustBadge);
            tvViolationWarning  = itemView.findViewById(R.id.tvViolationWarning);
            btnManage           = itemView.findViewById(R.id.btnManage);
        }
    }
}