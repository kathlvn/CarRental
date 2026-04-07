package com.mobcom.carrental.models;

import java.io.Serializable;

public class AdminReport implements Serializable {

    public enum Category {
        SCAM, HARASSMENT, VEHICLE_CONDITION,
        NO_SHOW, OVERCHARGING, OTHER
    }

    public enum Severity { LOW, MEDIUM, HIGH, CRITICAL }

    public enum Status { OPEN, ESCALATED, RESOLVED, DISMISSED }

    private String reportId;
    private Category category;
    private Severity severity;
    private Status status;
    private String title;
    private String description;
    private String reporterName;
    private String reportedName;  // provider or customer
    private String reportedId;
    private String date;
    private String resolution;

    public AdminReport(String reportId, Category category, Severity severity,
                       Status status, String title, String description,
                       String reporterName, String reportedName,
                       String reportedId, String date) {
        this.reportId     = reportId;
        this.category     = category;
        this.severity     = severity;
        this.status       = status;
        this.title        = title;
        this.description  = description;
        this.reporterName = reporterName;
        this.reportedName = reportedName;
        this.reportedId   = reportedId;
        this.date         = date;
    }

    // Getters
    public String getReportId()     { return reportId; }
    public Category getCategory()   { return category; }
    public Severity getSeverity()   { return severity; }
    public Status getStatus()       { return status; }
    public String getTitle()        { return title; }
    public String getDescription()  { return description; }
    public String getReporterName() { return reporterName; }
    public String getReportedName() { return reportedName; }
    public String getReportedId()   { return reportedId; }
    public String getDate()         { return date; }
    public String getResolution()   { return resolution; }

    // Setters
    public void setStatus(Status s)       { this.status = s; }
    public void setResolution(String r)   { this.resolution = r; }
}