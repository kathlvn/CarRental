package com.mobcom.carrental.fragments.provider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobcom.carrental.R;
import com.mobcom.carrental.adapters.ChatMessageAdapter;
import com.mobcom.carrental.models.ChatMessage;
import com.mobcom.carrental.utils.ChatMemoryStore;
import com.mobcom.carrental.utils.SessionManager;
import java.util.List;

public class ProviderMessagesFragment extends Fragment {

    private RecyclerView rvMessages;
    private TextInputEditText etMessage;
    private TextView tvThreadInfo;
    private MaterialButton btnSend;

    private ChatMessageAdapter adapter;
    private String threadId;
    private String peerName;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        threadId = getArgumentOrDefault("threadId", null);
        peerName = getArgumentOrDefault("peerName", "Customer");

        if (threadId == null || threadId.isEmpty()) {
            String anyThread = ChatMemoryStore.getAnyThreadId();
            threadId = anyThread == null ? "provider-general-thread" : anyThread;
            if (anyThread == null) {
                ChatMemoryStore.sendMessage(threadId, SessionManager.ROLE_CUSTOMER,
                        "Customer", "Hi! I need help with my booking.");
            }
        }

        bindViews(view);
        setupRecycler();
        bindHeader();
        bindActions();
        refreshMessages();
    }

    private void bindViews(@NonNull View view) {
        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etMessage);
        tvThreadInfo = view.findViewById(R.id.tvThreadInfo);
        btnSend = view.findViewById(R.id.btnSend);
    }

    private void setupRecycler() {
        adapter = new ChatMessageAdapter(SessionManager.ROLE_PROVIDER);
        rvMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMessages.setAdapter(adapter);
    }

    private void bindHeader() {
        tvThreadInfo.setText("Chat with " + peerName);
    }

    private void bindActions() {
        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText() == null ? "" : etMessage.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a message", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = sessionManager.getName().isEmpty() ? "Provider" : sessionManager.getName();
            ChatMemoryStore.sendMessage(threadId, SessionManager.ROLE_PROVIDER, name, text);
            etMessage.setText("");
            refreshMessages();
        });
    }

    private void refreshMessages() {
        List<ChatMessage> items = ChatMemoryStore.getThreadMessages(threadId);
        adapter.submitList(items);
        if (!items.isEmpty()) {
            rvMessages.scrollToPosition(items.size() - 1);
        }
    }

    private String getArgumentOrDefault(String key, String fallback) {
        Bundle args = getArguments();
        if (args == null) return fallback;
        String value = args.getString(key);
        return value == null ? fallback : value;
    }
}
