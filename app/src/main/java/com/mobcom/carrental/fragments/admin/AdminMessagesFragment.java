package com.mobcom.carrental.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobcom.carrental.R;
import com.mobcom.carrental.adapters.ChatMessageAdapter;
import com.mobcom.carrental.adapters.ConversationListAdapter;
import com.mobcom.carrental.models.ChatMessage;
import com.mobcom.carrental.models.Conversation;
import com.mobcom.carrental.utils.DatabaseChatStore;
import com.mobcom.carrental.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class AdminMessagesFragment extends Fragment {

    private RecyclerView rvMessages;
    private RecyclerView rvConversations;
    private TextInputEditText etMessage;
    private TextView tvThreadInfo;
    private TextView tvNoConversations;
    private MaterialButton btnSend;

    private ChatMessageAdapter adapter;
    private ConversationListAdapter conversationAdapter;
    private String threadId;
    private String peerName;
    private SessionManager sessionManager;
    private boolean isShowingChat = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        threadId = getArgumentOrDefault("threadId", null);

        // If threadId is provided, show chat view; otherwise show conversation list
        if (threadId != null && !threadId.isEmpty()) {
            return inflater.inflate(R.layout.fragment_message, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_conversations_list, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        threadId = getArgumentOrDefault("threadId", null);
        peerName = getArgumentOrDefault("peerName", "User");

        if (threadId != null && !threadId.isEmpty()) {
            // Show chat view
            isShowingChat = true;
            setupChatView(view);
        } else {
            // Show conversations list
            isShowingChat = false;
            setupConversationsList(view);
        }
    }

    private void setupChatView(View view) {
        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etMessage);
        tvThreadInfo = view.findViewById(R.id.tvThreadInfo);
        btnSend = view.findViewById(R.id.btnSend);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbarMessages);

        adapter = new ChatMessageAdapter(SessionManager.ROLE_ADMIN);
        rvMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMessages.setAdapter(adapter);

        tvThreadInfo.setText("Chat with " + peerName + " (Admin)");

        // Add back button
        toolbar.setNavigationOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText() == null ? "" : etMessage.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a message", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = sessionManager.getName().isEmpty() ? "Admin" : sessionManager.getName();
            String userId = sessionManager.getEmail();
            DatabaseChatStore.sendMessage(threadId, SessionManager.ROLE_ADMIN, userId, name, text);
            etMessage.setText("");
            refreshMessages();
        });

        refreshMessages();
    }

    private void setupConversationsList(View view) {
        rvConversations = view.findViewById(R.id.rvConversations);
        tvNoConversations = view.findViewById(R.id.tvNoConversations);

        conversationAdapter = new ConversationListAdapter(new ArrayList<>(), conversation -> {
            // Navigate to chat view
            Bundle args = new Bundle();
            args.putString("threadId", conversation.threadId);
            args.putString("peerName", conversation.peerName);
            // TODO: Add chat detail fragment to admin_nav_graph.xml
            // NavHostFragment.findNavController(this).navigate(R.id.adminMessagesFragment, args);
        });

        rvConversations.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvConversations.setAdapter(conversationAdapter);

        refreshConversationsList();
    }

    private void refreshConversationsList() {
        String userId = sessionManager.getEmail();
        List<String> threadIds = DatabaseChatStore.getUserThreadIds(userId);
        List<Conversation> conversations = new ArrayList<>();

        for (String id : threadIds) {
            ChatMessage lastMsg = DatabaseChatStore.getLastMessage(id);
            if (lastMsg != null) {
                String peerName = extractPeerNameFromThread(id, lastMsg);
                conversations.add(new Conversation(
                        id,
                        peerName,
                        lastMsg.getContent(),
                        lastMsg.getTimestamp()
                ));
            }
        }

        if (conversations.isEmpty()) {
            rvConversations.setVisibility(View.GONE);
            tvNoConversations.setVisibility(View.VISIBLE);
        } else {
            rvConversations.setVisibility(View.VISIBLE);
            tvNoConversations.setVisibility(View.GONE);
            conversationAdapter.updateList(conversations);
        }
    }

    private String extractPeerNameFromThread(String threadId, ChatMessage lastMsg) {
        // Extract peer name from last message or thread ID
        return lastMsg.getSenderName() != null ? lastMsg.getSenderName() : "Unknown";
    }

    private void refreshMessages() {
        List<ChatMessage> items = DatabaseChatStore.getThreadMessages(threadId);
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
