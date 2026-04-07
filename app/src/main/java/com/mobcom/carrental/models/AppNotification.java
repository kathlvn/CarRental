package com.mobcom.carrental.models;

import java.io.Serializable;

public class AppNotification implements Serializable {

    private final String id;
    private final String role;
    private final String title;
    private final String message;
    private final String bookingId;
    private final long createdAt;
    private boolean read;

    public AppNotification(String id,
                           String role,
                           String title,
                           String message,
                           String bookingId,
                           long createdAt,
                           boolean read) {
        this.id = id;
        this.role = role;
        this.title = title;
        this.message = message;
        this.bookingId = bookingId;
        this.createdAt = createdAt;
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getBookingId() {
        return bookingId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}