package com.mobcom.carrental.utils;

import com.mobcom.carrental.models.ChatMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Temporary in-memory chat store.
 * Replace with Room/API integration later.
 */
public final class ChatMemoryStore {

    private static final Map<String, List<ChatMessage>> THREADS = new HashMap<>();

    private ChatMemoryStore() {}

    public static synchronized List<ChatMessage> getThreadMessages(String threadId) {
        List<ChatMessage> messages = THREADS.get(threadId);
        if (messages == null) {
            messages = new ArrayList<>();
            THREADS.put(threadId, messages);
        }
        return new ArrayList<>(messages);
    }

    public static synchronized void sendMessage(String threadId, String senderRole,
                                                String senderName, String content) {
        List<ChatMessage> messages = THREADS.get(threadId);
        if (messages == null) {
            messages = new ArrayList<>();
            THREADS.put(threadId, messages);
        }

        messages.add(new ChatMessage(
                UUID.randomUUID().toString(),
                threadId,
                senderRole,
                senderName,
                content,
                System.currentTimeMillis()
        ));
    }

    public static synchronized String getAnyThreadId() {
        for (String key : THREADS.keySet()) {
            return key;
        }
        return null;
    }

    public static synchronized List<String> getAllThreadIds() {
        return new ArrayList<>(THREADS.keySet());
    }

    public static synchronized ChatMessage getLastMessage(String threadId) {
        List<ChatMessage> messages = THREADS.get(threadId);
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }
}