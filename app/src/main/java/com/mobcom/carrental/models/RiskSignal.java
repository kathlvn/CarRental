package com.mobcom.carrental.models;

import java.io.Serializable;

/**
 * Model for risk signals/alerts
 */
public class RiskSignal implements Serializable {

    public enum Severity { LOW, MEDIUM, HIGH, CRITICAL }

    private String signalId;
    private Severity severity;
    private String title;
    private String description;
    private String affectedEntity;  // User/Provider/Listing name
    private String affectedId;      // User/Provider/Listing ID
    private String metric;          // e.g., "40%" cancellation rate
    private String timestamp;
    private boolean resolved;

    public RiskSignal(String signalId, Severity severity, String title,
                     String description, String affectedEntity,
                     String affectedId, String metric, String timestamp, boolean resolved) {
        this.signalId = signalId;
        this.severity = severity;
        this.title = title;
        this.description = description;
        this.affectedEntity = affectedEntity;
        this.affectedId = affectedId;
        this.metric = metric;
        this.timestamp = timestamp;
        this.resolved = resolved;
    }

    // Getters
    public String getSignalId()        { return signalId; }
    public Severity getSeverity()      { return severity; }
    public String getTitle()           { return title; }
    public String getDescription()     { return description; }
    public String getAffectedEntity()  { return affectedEntity; }
    public String getAffectedId()      { return affectedId; }
    public String getMetric()          { return metric; }
    public String getTimestamp()       { return timestamp; }
    public boolean isResolved()        { return resolved; }

    // Setters
    public void setResolved(boolean resolved) { this.resolved = resolved; }
}
