package com.mobcom.carrental.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.mobcom.carrental.R;
import com.mobcom.carrental.adapters.AdminReportAdapter;
import com.mobcom.carrental.models.AdminReport;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminReportsFragment extends Fragment
        implements AdminReportAdapter.OnReportActionListener {

    private TabLayout tabCategory;
    private RecyclerView rvReports;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyTitle, tvEmptySubtitle, tvOpenBadge;

    private AdminReportAdapter adapter;
    private List<AdminReport> allReports = new ArrayList<>();
    private int currentTab = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabCategory  = view.findViewById(R.id.tabCategory);
        rvReports    = view.findViewById(R.id.rvReports);
        layoutEmpty  = view.findViewById(R.id.layoutEmpty);
        tvEmptyTitle = view.findViewById(R.id.tvEmptyTitle);
        tvEmptySubtitle = view.findViewById(R.id.tvEmptySubtitle);
        tvOpenBadge  = view.findViewById(R.id.tvOpenBadge);

        setupTabs();
        setupRecyclerView();
        loadDummyData();
        filterAndShow();
        updateOpenBadge();
    }

    private void setupTabs() {
        tabCategory.addTab(tabCategory.newTab().setText("All"));
        tabCategory.addTab(tabCategory.newTab().setText("Open"));
        tabCategory.addTab(tabCategory.newTab().setText("Escalated"));
        tabCategory.addTab(tabCategory.newTab().setText("Resolved"));
        tabCategory.addTab(tabCategory.newTab().setText("Dismissed"));

        tabCategory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                filterAndShow();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new AdminReportAdapter(requireContext(), new ArrayList<>(), this);
        rvReports.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvReports.setNestedScrollingEnabled(false);
        rvReports.setAdapter(adapter);
    }

    private void filterAndShow() {
        List<AdminReport> filtered;

        switch (currentTab) {
            case 1:
                filtered = allReports.stream()
                        .filter(r -> r.getStatus() == AdminReport.Status.OPEN)
                        .collect(Collectors.toList());
                break;
            case 2:
                filtered = allReports.stream()
                        .filter(r -> r.getStatus() == AdminReport.Status.ESCALATED)
                        .collect(Collectors.toList());
                break;
            case 3:
                filtered = allReports.stream()
                        .filter(r -> r.getStatus() == AdminReport.Status.RESOLVED)
                        .collect(Collectors.toList());
                break;
            case 4:
                filtered = allReports.stream()
                        .filter(r -> r.getStatus() == AdminReport.Status.DISMISSED)
                        .collect(Collectors.toList());
                break;
            default:
                filtered = new ArrayList<>(allReports);
                break;
        }

        adapter.updateList(filtered);

        if (filtered.isEmpty()) {
            rvReports.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            setEmptyMessage();
        } else {
            rvReports.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void setEmptyMessage() {
        switch (currentTab) {
            case 1:
                tvEmptyTitle.setText("No open reports");
                tvEmptySubtitle.setText("All clear! No pending reports to review");
                break;
            case 2:
                tvEmptyTitle.setText("No escalated reports");
                tvEmptySubtitle.setText("No reports have been escalated");
                break;
            case 3:
                tvEmptyTitle.setText("No resolved reports");
                tvEmptySubtitle.setText("Resolved reports will appear here");
                break;
            case 4:
                tvEmptyTitle.setText("No dismissed reports");
                tvEmptySubtitle.setText("Dismissed reports will appear here");
                break;
            default:
                tvEmptyTitle.setText("No reports yet");
                tvEmptySubtitle.setText("Customer and provider reports will appear here");
                break;
        }
    }

    private void updateOpenBadge() {
        long openCount = allReports.stream()
                .filter(r -> r.getStatus() == AdminReport.Status.OPEN
                        || r.getStatus() == AdminReport.Status.ESCALATED)
                .count();

        if (openCount > 0) {
            tvOpenBadge.setVisibility(View.VISIBLE);
            tvOpenBadge.setText(openCount + " open");
            tvOpenBadge.getBackground()
                    .setTint(android.graphics.Color.parseColor("#E53935"));
        } else {
            tvOpenBadge.setVisibility(View.GONE);
        }
    }

    private void loadDummyData() {
        allReports.add(new AdminReport(
                "RPT001",
                AdminReport.Category.SCAM,
                AdminReport.Severity.CRITICAL,
                AdminReport.Status.OPEN,
                "Provider demanded extra cash",
                "Provider asked for additional ₱2,000 on top of the agreed booking price, threatening to cancel if not paid.",
                "Juan dela Cruz",
                "Pedro Reyes (Provider)",
                "P003",
                "Apr 5, 2025"));

        allReports.add(new AdminReport(
                "RPT002",
                AdminReport.Category.VEHICLE_CONDITION,
                AdminReport.Severity.HIGH,
                AdminReport.Status.OPEN,
                "Car condition didn't match listing photos",
                "The car had visible dents and scratches not shown in the listing. Interior was also dirty.",
                "Maria Santos",
                "Juan dela Cruz (Provider)",
                "P001",
                "Apr 4, 2025"));

        allReports.add(new AdminReport(
                "RPT003",
                AdminReport.Category.NO_SHOW,
                AdminReport.Severity.MEDIUM,
                AdminReport.Status.ESCALATED,
                "Provider didn't show up at pickup location",
                "Provider confirmed booking but didn't appear at the agreed pickup time and location.",
                "Ana Garcia",
                "Pedro Reyes (Provider)",
                "P003",
                "Apr 3, 2025"));

        AdminReport resolved = new AdminReport(
                "RPT004",
                AdminReport.Category.OVERCHARGING,
                AdminReport.Severity.LOW,
                AdminReport.Status.RESOLVED,
                "Charged more than listed price",
                "Provider charged ₱500 more than the listed daily rate without prior notice.",
                "Carlo Reyes",
                "Maria Santos (Provider)",
                "P002",
                "Mar 28, 2025");
        resolved.setResolution(
                "Provider warned. Refund of ₱500 issued to customer. "
                        + "Provider reminded of pricing policy.");
        allReports.add(resolved);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    @Override
    public void onDismiss(AdminReport report) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Dismiss Report?")
                .setMessage("Are you sure this report does not require action?")
                .setPositiveButton("Dismiss", (dialog, which) -> {
                    report.setStatus(AdminReport.Status.DISMISSED);
                    report.setResolution("Report dismissed — no violation found.");
                    filterAndShow();
                    updateOpenBadge();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onEscalate(AdminReport report) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Escalate Report?")
                .setMessage("This will flag the report as high priority. "
                        + "The provider may be temporarily restricted.")
                .setPositiveButton("Escalate", (dialog, which) -> {
                    report.setStatus(AdminReport.Status.ESCALATED);
                    filterAndShow();
                    updateOpenBadge();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResolve(AdminReport report) {
        String[] actions = {
                "Warning issued to provider",
                "Provider temporarily suspended",
                "Provider permanently banned",
                "Refund issued to customer",
                "No action — resolved amicably"
        };

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Resolve Report")
                .setMessage("Select the action taken:")
                .setItems(actions, (dialog, which) -> {
                    report.setStatus(AdminReport.Status.RESOLVED);
                    report.setResolution(actions[which]);
                    filterAndShow();
                    updateOpenBadge();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}