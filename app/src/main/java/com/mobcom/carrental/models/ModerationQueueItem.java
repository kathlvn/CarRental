package com.mobcom.carrental.models;

import java.io.Serializable;

/**
 * Model for moderation queue items (listings, reports, providers)
 */
public class ModerationQueueItem implements Serializable {

    public enum ItemType { LISTING, REPORT, PROVIDER }
    public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }

    private String itemId;
    private ItemType type;
    private Priority priority;
    private String title;
    private String description;
    private String submittedBy;
    private String submittedAt;
    private String relatedId;  // Listing/Report/Provider ID

    public ModerationQueueItem(String itemId, ItemType type, Priority priority,
                              String title, String description,
                              String submittedBy, String submittedAt, String relatedId) {
        this.itemId = itemId;
        this.type = type;
        this.priority = priority;
        this.title = title;
        this.description = description;
        this.submittedBy = submittedBy;
        this.submittedAt = submittedAt;
        this.relatedId = relatedId;
    }

    // Getters
    public String getItemId()        { return itemId; }
    public ItemType getType()        { return type; }
    public Priority getPriority()    { return priority; }
    public String getTitle()         { return title; }
    public String getDescription()   { return description; }
    public String getSubmittedBy()   { return submittedBy; }
    public String getSubmittedAt()   { return submittedAt; }
    public String getRelatedId()     { return relatedId; }

    // Setters
    public void setItemId(String itemId)          { this.itemId = itemId; }
    public void setType(ItemType type)            { this.type = type; }
    public void setPriority(Priority priority)    { this.priority = priority; }
    public void setTitle(String title)            { this.title = title; }
    public void setDescription(String description){ this.description = description; }
    public void setSubmittedBy(String submittedBy){ this.submittedBy = submittedBy; }
    public void setSubmittedAt(String submittedAt){ this.submittedAt = submittedAt; }
    public void setRelatedId(String relatedId)    { this.relatedId = relatedId; }
}
