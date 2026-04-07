package com.mobcom.carrental.models;

public class Conversation {
    public String threadId;
    public String peerName;
    public String lastMessage;
    public long timestamp;

    public Conversation(String threadId, String peerName, String lastMessage, long timestamp) {
        this.threadId = threadId;
        this.peerName = peerName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }
}
