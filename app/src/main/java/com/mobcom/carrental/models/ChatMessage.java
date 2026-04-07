package com.mobcom.carrental.models;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    private String id;
    private String threadId;
    private String senderRole;
    private String senderName;
    private String content;
    private long timestamp;

    public ChatMessage(String id, String threadId, String senderRole,
                       String senderName, String content, long timestamp) {
        this.id = id;
        this.threadId = threadId;
        this.senderRole = senderRole;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getThreadId() { return threadId; }
    public String getSenderRole() { return senderRole; }
    public String getSenderName() { return senderName; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
}
