package com.mobcom.carrental.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class MessageEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String threadId;        // Conversation identifier
    public String senderRole;      // CUSTOMER, PROVIDER, ADMIN
    public String senderId;        // User ID of sender
    public String senderName;      // Display name of sender
    public String content;         // Message content
    public long timestamp;         // When message was created
    public boolean isRead;         // Whether message has been read by recipient

    public MessageEntity(String threadId, String senderRole, String senderId,
                        String senderName, String content, long timestamp) {
        this.threadId = threadId;
        this.senderRole = senderRole;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = false;
    }
}
