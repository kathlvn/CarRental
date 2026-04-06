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
import com.mobcom.carrental.models.AdminListing;
import java.util.List;

public class AdminListingAdapter
        extends RecyclerView.Adapter<AdminListingAdapter.ListingViewHolder> {

    public interface OnListingActionListener {
        void onApprove(AdminListing listing);
        void onReject(AdminListing listing);
        void onViewProvider(AdminListing listing);
    }

    private List<AdminListing> listings;
    private Context context;
    private OnListingActionListener listener;

    public AdminListingAdapter(Context context, List<AdminListing> listings,
                               OnListingActionListener listener) {
        this.context  = context;
        this.listings = listings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_listing, parent, false);
        return new ListingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        AdminListing listing = listings.get(position);

        // Car info
        holder.tvCarName.setText(listing.getCarName());
        holder.tvCarPlate.setText(listing.getCarPlate()
                + " • " + listing.getCarType()
                + " • " + listing.getSeats() + " seats");
        holder.tvSubmittedAt.setText("Submitted " + listing.getSubmittedAt());
        holder.tvPrice.setText("₱" + String.format("%,.0f", listing.getPricePerDay()) + "/day");

        // Car image
        Glide.with(context)
                .load(listing.getCarImageUrl())
                .centerCrop()
                .placeholder(R.drawable.placeholder_car)
                .into(holder.imgCar);

        // Risk bar + badge
        int riskColor = getRiskColor(listing.getRiskLevel());
        holder.viewRiskBar.setBackgroundColor(riskColor);
        holder.tvRiskBadge.setText(getRiskLabel(listing.getRiskLevel()));
        holder.tvRiskBadge.getBackground().setTint(riskColor);

        // Status badge
        holder.tvStatusBadge.setText(getStatusLabel(listing.getStatus()));
        holder.tvStatusBadge.getBackground().setTint(getStatusColor(listing.getStatus()));

        // Provider info
        holder.tvProviderName.setText(listing.getProviderName());
        holder.tvProviderMeta.setText(
                listing.getProviderApprovedListings() + " approved • "
                        + "⭐ " + (listing.getProviderRating() > 0
                        ? listing.getProviderRating() : "No ratings")
                        + " • " + listing.getProviderReports() + " reports");

        // Trust badge
        holder.tvTrustBadge.setText(getTrustLabel(listing.getProviderTrustLevel()));
        holder.tvTrustBadge.getBackground()
                .setTint(getTrustColor(listing.getProviderTrustLevel()));

        // Risk indicators
        // OR/CR
        holder.tvIndicatorOrCr.setText(
                listing.isHasOrCr() ? "✓ OR/CR" : "✕ No OR/CR");
        holder.tvIndicatorOrCr.setTextColor(
                listing.isHasOrCr()
                        ? Color.parseColor("#2E7D32")
                        : Color.parseColor("#C62828"));

        // Reports
        if (listing.getProviderReports() > 0) {
            holder.tvIndicatorReports.setVisibility(View.VISIBLE);
            holder.tvIndicatorReports.setText(
                    "⚠ " + listing.getProviderReports() + " reports");
            holder.tvIndicatorReports.setTextColor(Color.parseColor("#E53935"));
        } else {
            holder.tvIndicatorReports.setVisibility(View.GONE);
        }

        // New provider
        if (listing.getProviderApprovedListings() == 0) {
            holder.tvIndicatorNew.setVisibility(View.VISIBLE);
            holder.tvIndicatorNew.setText("🆕 New Provider");
            holder.tvIndicatorNew.setTextColor(Color.parseColor("#1A237E"));
        } else {
            holder.tvIndicatorNew.setVisibility(View.GONE);
        }

        // Show/hide action buttons
        if (listing.getStatus() == AdminListing.Status.PENDING_REVIEW) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.layoutRejectionReason.setVisibility(View.GONE);
            holder.btnApprove.setOnClickListener(v -> listener.onApprove(listing));
            holder.btnReject.setOnClickListener(v -> listener.onReject(listing));
        } else if (listing.getStatus() == AdminListing.Status.REJECTED) {
            holder.layoutActions.setVisibility(View.GONE);
            holder.layoutRejectionReason.setVisibility(View.VISIBLE);
            holder.tvRejectionReason.setText(listing.getRejectionReason());
        } else {
            holder.layoutActions.setVisibility(View.GONE);
            holder.layoutRejectionReason.setVisibility(View.GONE);
        }

        holder.tvProviderName.setOnClickListener(v -> listener.onViewProvider(listing));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private int getRiskColor(AdminListing.RiskLevel level) {
        switch (level) {
            case HIGH:   return Color.parseColor("#C62828");
            case MEDIUM: return Color.parseColor("#FF9800");
            default:     return Color.parseColor("#2E7D32");
        }
    }

    private String getRiskLabel(AdminListing.RiskLevel level) {
        switch (level) {
            case HIGH:   return "⚠ High Risk";
            case MEDIUM: return "! Medium Risk";
            default:     return "✓ Low Risk";
        }
    }

    private String getStatusLabel(AdminListing.Status status) {
        switch (status) {
            case PENDING_REVIEW: return "⏳ Pending";
            case APPROVED:       return "✓ Approved";
            case REJECTED:       return "✕ Rejected";
            default:             return "";
        }
    }

    private int getStatusColor(AdminListing.Status status) {
        switch (status) {
            case PENDING_REVIEW: return Color.parseColor("#FF9800");
            case APPROVED:       return Color.parseColor("#2E7D32");
            case REJECTED:       return Color.parseColor("#C62828");
            default:             return Color.GRAY;
        }
    }

    private String getTrustLabel(String trust) {
        switch (trust) {
            case "TRUSTED":   return "✓ Trusted";
            case "FLAGGED":   return "⚠ Flagged";
            case "SUSPENDED": return "✕ Suspended";
            default:          return "⏳ Probation";
        }
    }

    private int getTrustColor(String trust) {
        switch (trust) {
            case "TRUSTED":   return Color.parseColor("#2E7D32");
            case "FLAGGED":   return Color.parseColor("#E53935");
            case "SUSPENDED": return Color.parseColor("#B71C1C");
            default:          return Color.parseColor("#FF9800");
        }
    }

    public void updateList(List<AdminListing> newList) {
        this.listings = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() { return listings.size(); }

    static class ListingViewHolder extends RecyclerView.ViewHolder {
        View viewRiskBar;
        ImageView imgCar;
        TextView tvCarName, tvCarPlate, tvSubmittedAt, tvPrice;
        TextView tvRiskBadge, tvStatusBadge;
        TextView tvProviderName, tvProviderMeta, tvTrustBadge;
        TextView tvIndicatorOrCr, tvIndicatorReports, tvIndicatorNew;
        TextView tvRejectionReason;
        LinearLayout layoutActions, layoutRejectionReason;
        MaterialButton btnApprove, btnReject;

        ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            viewRiskBar           = itemView.findViewById(R.id.viewRiskBar);
            imgCar                = itemView.findViewById(R.id.imgCar);
            tvCarName             = itemView.findViewById(R.id.tvCarName);
            tvCarPlate            = itemView.findViewById(R.id.tvCarPlate);
            tvSubmittedAt         = itemView.findViewById(R.id.tvSubmittedAt);
            tvPrice               = itemView.findViewById(R.id.tvPrice);
            tvRiskBadge           = itemView.findViewById(R.id.tvRiskBadge);
            tvStatusBadge         = itemView.findViewById(R.id.tvStatusBadge);
            tvProviderName        = itemView.findViewById(R.id.tvProviderName);
            tvProviderMeta        = itemView.findViewById(R.id.tvProviderMeta);
            tvTrustBadge          = itemView.findViewById(R.id.tvTrustBadge);
            tvIndicatorOrCr       = itemView.findViewById(R.id.tvIndicatorOrCr);
            tvIndicatorReports    = itemView.findViewById(R.id.tvIndicatorReports);
            tvIndicatorNew        = itemView.findViewById(R.id.tvIndicatorNew);
            tvRejectionReason     = itemView.findViewById(R.id.tvRejectionReason);
            layoutActions         = itemView.findViewById(R.id.layoutActions);
            layoutRejectionReason = itemView.findViewById(R.id.layoutRejectionReason);
            btnApprove            = itemView.findViewById(R.id.btnApprove);
            btnReject             = itemView.findViewById(R.id.btnReject);
        }
    }
}