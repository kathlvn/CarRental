package com.mobcom.carrental.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "conversation_threads")
public class ConversationThreadEntity {

    @NonNull
    @PrimaryKey
    public String threadId;

    public String participant1Id;   // User ID of first participant
    public String participant1Role; // Role of first participant
    public String participant2Id;   // User ID of second participant
    public String participant2Role; // Role of second participant
    public long createdAt;
    public long lastMessageAt;

    public ConversationThreadEntity(String threadId, String participant1Id, String participant1Role,
                                   String participant2Id, String participant2Role, long createdAt) {
        this.threadId = threadId;
        this.participant1Id = participant1Id;
        this.participant1Role = participant1Role;
        this.participant2Id = participant2Id;
        this.participant2Role = participant2Role;
        this.createdAt = createdAt;
        this.lastMessageAt = createdAt;
    }
}
