package com.mobcom.carrental;

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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.mobcom.carrental.adapters.NotificationAdapter;
import com.mobcom.carrental.models.AppNotification;
import com.mobcom.carrental.utils.NotificationStore;
import com.mobcom.carrental.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView rvNotifications;
    private LinearLayout layoutEmpty;
    private TextView tvEmptySubtitle;
    private MaterialButton btnMarkAllRead;

    private NotificationAdapter adapter;
    private SessionManager sessionManager;
    private String currentRole;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        currentRole = sessionManager.getRole();

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        rvNotifications = view.findViewById(R.id.rvNotifications);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvEmptySubtitle = view.findViewById(R.id.tvEmptySubtitle);
        btnMarkAllRead = view.findViewById(R.id.btnMarkAllRead);

        toolbar.setNavigationOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        adapter = new NotificationAdapter(requireContext(), new ArrayList<>());
        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvNotifications.setAdapter(adapter);

        btnMarkAllRead.setOnClickListener(v -> {
            NotificationStore.markAllAsRead(requireContext(), currentRole);
            loadNotifications();
        });

        loadNotifications();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotifications();
    }

    private void loadNotifications() {
        List<AppNotification> notifications = NotificationStore.getByRole(requireContext(), currentRole);
        adapter.update(notifications);

        if (notifications.isEmpty()) {
            rvNotifications.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            tvEmptySubtitle.setText("Status updates for your bookings will appear here.");
            btnMarkAllRead.setVisibility(View.GONE);
        } else {
            rvNotifications.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            btnMarkAllRead.setVisibility(View.VISIBLE);
        }
    }
}