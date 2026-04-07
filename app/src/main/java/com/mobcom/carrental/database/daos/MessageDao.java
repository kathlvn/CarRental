package com.mobcom.carrental.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mobcom.carrental.database.entities.MessageEntity;
import com.mobcom.carrental.database.entities.ConversationThreadEntity;

import java.util.List;

@Dao
public interface MessageDao {

    // Message operations
    @Insert
    long insert(MessageEntity message);

    @Update
    void update(MessageEntity message);

    @Delete
    void delete(MessageEntity message);

    @Query("SELECT * FROM messages WHERE threadId = :threadId ORDER BY timestamp ASC")
    List<MessageEntity> getByThreadId(String threadId);

    @Query("SELECT * FROM messages WHERE threadId = :threadId ORDER BY timestamp DESC LIMIT 1")
    MessageEntity getLastMessage(String threadId);

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    List<MessageEntity> getAllMessages();

    @Query("SELECT DISTINCT threadId FROM messages ORDER BY threadId DESC")
    List<String> getAllThreadIds();

    @Query("SELECT * FROM messages WHERE senderRole = :senderRole ORDER BY timestamp DESC")
    List<MessageEntity> getMessagesBySenderRole(String senderRole);

    @Query("SELECT * FROM messages WHERE isRead = 0 AND threadId = :threadId")
    List<MessageEntity> getUnreadMessages(String threadId);

    @Query("UPDATE messages SET isRead = 1 WHERE threadId = :threadId")
    void markThreadAsRead(String threadId);

    @Query("DELETE FROM messages WHERE threadId = :threadId")
    void deleteThread(String threadId);

    @Query("SELECT COUNT(*) FROM messages WHERE isRead = 0")
    int getUnreadCount();

    // Conversation thread operations
    @Insert
    long insertThread(ConversationThreadEntity thread);

    @Update
    void updateThread(ConversationThreadEntity thread);

    @Query("SELECT * FROM conversation_threads WHERE threadId = :threadId")
    ConversationThreadEntity getThread(String threadId);

    @Query("SELECT * FROM conversation_threads WHERE participant1Id = :userId OR participant2Id = :userId ORDER BY lastMessageAt DESC")
    List<ConversationThreadEntity> getUserConversations(String userId);

    @Query("SELECT * FROM conversation_threads ORDER BY lastMessageAt DESC")
    List<ConversationThreadEntity> getAllThreads();

    @Query("UPDATE conversation_threads SET lastMessageAt = :timestamp WHERE threadId = :threadId")
    void updateThreadLastMessage(String threadId, long timestamp);
}
