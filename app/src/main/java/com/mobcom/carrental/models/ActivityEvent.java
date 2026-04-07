package com.mobcom.carrental.models;

import java.io.Serializable;

/**
 * Model for dashboard activity feed
 */
public class ActivityEvent implements Serializable {

    public enum Type {
        BOOKING_CREATED, BOOKING_COMPLETED, LISTING_SUBMITTED,
        LISTING_APPROVED, LISTING_REJECTED, REPORT_FILED,
        REPORT_RESOLVED, PROVIDER_FLAGGED, PROVIDER_SUSPENDED
    }

    private String eventId;
    private Type type;
    private String title;
    private String description;
    private String actor;
    private String relatedId;        // Related booking/listing/provider ID
    private String timestamp;
    private String icon;

    public ActivityEvent(String eventId, Type type, String title,
                        String description, String actor,
                        String relatedId, String timestamp, String icon) {
        this.eventId      = eventId;
        this.type         = type;
        this.title        = title;
        this.description  = description;
        this.actor        = actor;
        this.relatedId    = relatedId;
        this.timestamp    = timestamp;
        this.icon         = icon;
    }

    // Getters
    public String getEventId()     { return eventId; }
    public Type getType()          { return type; }
    public String getTitle()       { return title; }
    public String getDescription() { return description; }
    public String getActor()       { return actor; }
    public String getRelatedId()   { return relatedId; }
    public String getTimestamp()   { return timestamp; }
    public String getIcon()        { return icon; }

    // Setters
    public void setEventId(String eventId)           { this.eventId = eventId; }
    public void setType(Type type)                   { this.type = type; }
    public void setTitle(String title)               { this.title = title; }
    public void setDescription(String description)   { this.description = description; }
    public void setActor(String actor)               { this.actor = actor; }
    public void setRelatedId(String relatedId)       { this.relatedId = relatedId; }
    public void setTimestamp(String timestamp)       { this.timestamp = timestamp; }
    public void setIcon(String icon)                 { this.icon = icon; }
}
