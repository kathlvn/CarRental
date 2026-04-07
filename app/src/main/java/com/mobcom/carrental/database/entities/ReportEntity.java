package com.mobcom.carrental.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "admin_reports")
public class ReportEntity {

    @NonNull
    @PrimaryKey
    public String reportId;

    public String category;           // SCAM, HARASSMENT, VEHICLE_CONDITION, NO_SHOW, OVERCHARGING, OTHER
    public String severity;           // LOW, MEDIUM, HIGH, CRITICAL
    public String status;             // OPEN, ESCALATED, RESOLVED, DISMISSED
    public String title;
    public String description;
    public String reporterName;       // Who filed the report
    public String reporterRole;       // CUSTOMER or PROVIDER
    public String reportedName;       // Who is being reported
    public String reportedId;         // User ID of reported person
    public String reportedRole;       // CUSTOMER or PROVIDER
    public long createdAt;
    public long updatedAt;
    public String resolution;         // Admin decision/outcome
    public String adminNotes;         // Admin's notes on the case

    public ReportEntity(String reportId, String category, String severity, String status,
                       String title, String description, String reporterName, String reporterRole,
                       String reportedName, String reportedId, String reportedRole, long createdAt) {
        this.reportId = reportId;
        this.category = category;
        this.severity = severity;
        this.status = status;
        this.title = title;
        this.description = description;
        this.reporterName = reporterName;
        this.reporterRole = reporterRole;
        this.reportedName = reportedName;
        this.reportedId = reportedId;
        this.reportedRole = reportedRole;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.resolution = "";
        this.adminNotes = "";
    }
}
