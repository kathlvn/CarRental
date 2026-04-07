package com.mobcom.carrental.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "admin_warnings")
public class WarningEntity {

    @NonNull
    @PrimaryKey
    public String warningId;

    public String userId;             // User being warned
    public String userRole;           // CUSTOMER or PROVIDER
    public String severity;           // WARNING, SUSPENSION, BAN
    public String reason;             // Why they're being warned
    public String reportId;           // FK to ReportEntity if applicable
    public long issuedAt;
    public long expiresAt;            // When warning expires (0 if permanent)
    public boolean isActive;
    public String issuedBy;           // Admin email who issued it

    public WarningEntity(String warningId, String userId, String userRole, String severity,
                        String reason, String reportId, long issuedAt, long expiresAt,
                        String issuedBy) {
        this.warningId = warningId;
        this.userId = userId;
        this.userRole = userRole;
        this.severity = severity;
        this.reason = reason;
        this.reportId = reportId;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.isActive = true;
        this.issuedBy = issuedBy;
    }
}
