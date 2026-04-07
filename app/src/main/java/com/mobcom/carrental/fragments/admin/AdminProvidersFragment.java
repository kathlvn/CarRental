package com.mobcom.carrental.fragments.admin;

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
import com.google.android.material.tabs.TabLayout;
import com.mobcom.carrental.R;
import com.mobcom.carrental.adapters.AdminProviderAdapter;
import com.mobcom.carrental.models.AdminProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminProvidersFragment extends Fragment
        implements AdminProviderAdapter.OnProviderActionListener {

    private TabLayout tabTrust;
    private RecyclerView rvProviders;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyTitle;

    private AdminProviderAdapter adapter;
    private List<AdminProvider> allProviders = new ArrayList<>();
    private int currentTab = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_providers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabTrust    = view.findViewById(R.id.tabTrust);
        rvProviders = view.findViewById(R.id.rvProviders);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvEmptyTitle= view.findViewById(R.id.tvEmptyTitle);

        setupTabs();
        setupRecyclerView();
        loadDummyData();
        filterAndShow();
    }

    private void setupTabs() {
        tabTrust.addTab(tabTrust.newTab().setText("All"));
        tabTrust.addTab(tabTrust.newTab().setText("Probation"));
        tabTrust.addTab(tabTrust.newTab().setText("Trusted"));
        tabTrust.addTab(tabTrust.newTab().setText("Flagged"));
        tabTrust.addTab(tabTrust.newTab().setText("Suspended"));

        tabTrust.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        adapter = new AdminProviderAdapter(requireContext(), new ArrayList<>(), this);
        rvProviders.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvProviders.setNestedScrollingEnabled(false);
        rvProviders.setAdapter(adapter);
    }

    private void filterAndShow() {
        List<AdminProvider> filtered;

        switch (currentTab) {
            case 1:
                filtered = allProviders.stream()
                        .filter(p -> p.getTrustLevel() == AdminProvider.TrustLevel.PROBATION)
                        .collect(Collectors.toList());
                break;
            case 2:
                filtered = allProviders.stream()
                        .filter(p -> p.getTrustLevel() == AdminProvider.TrustLevel.TRUSTED)
                        .collect(Collectors.toList());
                break;
            case 3:
                filtered = allProviders.stream()
                        .filter(p -> p.getTrustLevel() == AdminProvider.TrustLevel.FLAGGED)
                        .collect(Collectors.toList());
                break;
            case 4:
                filtered = allProviders.stream()
                        .filter(p -> p.getTrustLevel() == AdminProvider.TrustLevel.SUSPENDED)
                        .collect(Collectors.toList());
                break;
            default:
                filtered = new ArrayList<>(allProviders);
                break;
        }

        adapter.updateList(filtered);

        if (filtered.isEmpty()) {
            rvProviders.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            tvEmptyTitle.setText("No providers in this category");
        } else {
            rvProviders.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void loadDummyData() {
        allProviders.add(new AdminProvider(
                "P001", "Juan dela Cruz", "juan@email.com",
                "+63 912 345 6789", "Bacolod City", "January 2025",
                AdminProvider.TrustLevel.PROBATION,
                2, 1, 3, 2, 0, 4.5f, 3000, 0.10f, ""));

        allProviders.add(new AdminProvider(
                "P002", "Maria Santos", "maria@email.com",
                "+63 917 654 3210", "Bacolod City", "February 2025",
                AdminProvider.TrustLevel.TRUSTED,
                5, 4, 12, 11, 0, 4.8f, 18000, 0.05f, ""));

        allProviders.add(new AdminProvider(
                "P003", "Pedro Reyes", "pedro@email.com",
                "+63 998 111 2222", "Silay City", "March 2025",
                AdminProvider.TrustLevel.FLAGGED,
                3, 2, 8, 5, 4, 2.1f, 6000, 0.35f, ""));

        allProviders.add(new AdminProvider(
                "P004", "Ana Garcia", "ana@email.com",
                "+63 921 999 8888", "Talisay City", "March 2025",
                AdminProvider.TrustLevel.SUSPENDED,
                1, 0, 2, 0, 6, 1.5f, 0, 0.80f, ""));
    }

    @Override
    public void onManage(AdminProvider provider) {
        Bundle args = new Bundle();
        args.putSerializable("provider", provider);
        androidx.navigation.fragment.NavHostFragment
                .findNavController(AdminProvidersFragment.this)
                .navigate(R.id.action_providers_to_providerDetail, args);
    }
}