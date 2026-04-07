package com.mobcom.carrental.utils;

import android.content.Context;

import com.mobcom.carrental.database.AppDatabase;
import com.mobcom.carrental.database.entities.ConversationThreadEntity;
import com.mobcom.carrental.database.entities.MessageEntity;
import com.mobcom.carrental.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Database-backed chat store using Room ORM.
 * Provides persistent message storage across customer, provider, and admin.
 */
public final class DatabaseChatStore {

    private static Context appContext;

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    private static AppDatabase getDatabase() {
        if (appContext == null) {
            throw new IllegalStateException("DatabaseChatStore not initialized");
        }
        return AppDatabase.getInstance(appContext);
    }

    /**
     * Send a message to a thread
     */
    public static synchronized void sendMessage(String threadId, String senderRole,
                                               String senderId, String senderName, String content) {
        AppDatabase db = getDatabase();

        // Ensure thread exists
        ConversationThreadEntity thread = db.messageDao().getThread(threadId);
        if (thread == null) {
            thread = new ConversationThreadEntity(
                    threadId,
                    senderId,
                    senderRole,
                    "",  // participant2Id will be extracted from threadId or set later
                    "", // participant2Role will be set later
                    System.currentTimeMillis()
            );
            db.messageDao().insertThread(thread);
        }

        // Insert message
        MessageEntity message = new MessageEntity(
                threadId,
                senderRole,
                senderId,
                senderName,
                content,
                System.currentTimeMillis()
        );
        db.messageDao().insert(message);

        // Update thread last message time
        db.messageDao().updateThreadLastMessage(threadId, System.currentTimeMillis());
    }

    /**
     * Get all messages in a thread
     */
    public static synchronized List<ChatMessage> getThreadMessages(String threadId) {
        AppDatabase db = getDatabase();
        List<MessageEntity> entities = db.messageDao().getByThreadId(threadId);
        List<ChatMessage> messages = new ArrayList<>();

        for (MessageEntity entity : entities) {
            messages.add(new ChatMessage(
                    String.valueOf(entity.id),
                    entity.threadId,
                    entity.senderRole,
                    entity.senderName,
                    entity.content,
                    entity.timestamp
            ));
        }

        return messages;
    }

    /**
     * Get last message in a thread
     */
    public static synchronized ChatMessage getLastMessage(String threadId) {
        AppDatabase db = getDatabase();
        MessageEntity entity = db.messageDao().getLastMessage(threadId);

        if (entity == null) return null;

        return new ChatMessage(
                String.valueOf(entity.id),
                entity.threadId,
                entity.senderRole,
                entity.senderName,
                entity.content,
                entity.timestamp
        );
    }

    /**
     * Get all thread IDs for a user
     */
    public static synchronized List<String> getAllThreadIds() {
        AppDatabase db = getDatabase();
        return db.messageDao().getAllThreadIds();
    }

    /**
     * Get all thread IDs for a specific user
     */
    public static synchronized List<String> getUserThreadIds(String userId) {
        AppDatabase db = getDatabase();
        List<ConversationThreadEntity> threads = db.messageDao().getUserConversations(userId);
        List<String> threadIds = new ArrayList<>();

        for (ConversationThreadEntity thread : threads) {
            threadIds.add(thread.threadId);
        }

        return threadIds;
    }

    /**
     * Create or get a thread between two users
     */
    public static synchronized String getOrCreateThread(String userId1, String role1,
                                                        String userId2, String role2) {
        AppDatabase db = getDatabase();

        // Generate thread ID (consistent ordering for bidirectional lookup)
        String id1 = userId1.compareTo(userId2) < 0 ? userId1 : userId2;
        String id2 = userId1.compareTo(userId2) < 0 ? userId2 : userId1;
        String threadId = id1 + "_" + id2;

        ConversationThreadEntity thread = db.messageDao().getThread(threadId);
        if (thread == null) {
            thread = new ConversationThreadEntity(
                    threadId,
                    userId1,
                    role1,
                    userId2,
                    role2,
                    System.currentTimeMillis()
            );
            db.messageDao().insertThread(thread);
        }

        return threadId;
    }

    /**
     * Mark messages in thread as read
     */
    public static synchronized void markThreadAsRead(String threadId) {
        AppDatabase db = getDatabase();
        db.messageDao().markThreadAsRead(threadId);
    }

    /**
     * Get unread message count
     */
    public static synchronized int getUnreadCount() {
        AppDatabase db = getDatabase();
        return db.messageDao().getUnreadCount();
    }

    /**
     * Delete a conversation thread
     */
    public static synchronized void deleteThread(String threadId) {
        AppDatabase db = getDatabase();
        db.messageDao().deleteThread(threadId);
    }
}
