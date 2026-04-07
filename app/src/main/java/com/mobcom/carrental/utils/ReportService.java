package com.mobcom.carrental.utils;

import android.content.Context;

import com.mobcom.carrental.database.AppDatabase;
import com.mobcom.carrental.database.entities.ReportEntity;
import com.mobcom.carrental.database.entities.WarningEntity;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing admin reports and warnings
 */
public final class ReportService {

    private static Context appContext;

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    private static AppDatabase getDatabase() {
        if (appContext == null) {
            throw new IllegalStateException("ReportService not initialized");
        }
        return AppDatabase.getInstance(appContext);
    }

    // REPORTS

    /**
     * File a report against user
     */
    public static long fileReport(String category, String severity, String title,
                                 String description, String reporterName, String reporterRole,
                                 String reportedName, String reportedId, String reportedRole) {
        AppDatabase db = getDatabase();

        String reportId = "RPT" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        ReportEntity report = new ReportEntity(
                reportId,
                category,
                severity,
                "OPEN",
                title,
                description,
                reporterName,
                reporterRole,
                reportedName,
                reportedId,
                reportedRole,
                System.currentTimeMillis()
        );

        return db.reportDao().insert(report);
    }

    /**
     * Get open/unresolved reports
     */
    public static List<ReportEntity> getOpenReports() {
        AppDatabase db = getDatabase();
        return db.reportDao().getOpenReports();
    }

    /**
     * Get reports for user
     */
    public static List<ReportEntity> getReportsForUser(String userId) {
        AppDatabase db = getDatabase();
        return db.reportDao().getReportsForUser(userId);
    }

    /**
     * Get count of unresolved reports
     */
    public static int getUnresolvedReportCount() {
        AppDatabase db = getDatabase();
        return db.reportDao().getUnresolvedReportCount();
    }

    /**
     * Update report with resolution
     */
    public static void updateReportResolution(String reportId, String status, String resolution, String notes) {
        AppDatabase db = getDatabase();
        db.reportDao().updateReportStatus(reportId, status, resolution, notes, System.currentTimeMillis());
    }

    /**
     * Escalate report
     */
    public static void escalateReport(String reportId, String notes) {
        AppDatabase db = getDatabase();
        updateReportResolution(reportId, "ESCALATED", "", notes);
    }

    /**
     * Dismiss report
     */
    public static void dismissReport(String reportId) {
        AppDatabase db = getDatabase();
        updateReportResolution(reportId, "DISMISSED", "No violation found", "");
    }

    // WARNINGS

    /**
     * Issue warning to user
     */
    public static long issueWarning(String userId, String userRole, String severity,
                                   String reason, String reportId, long expiresAt,
                                   String issuedBy) {
        AppDatabase db = getDatabase();

        String warningId = "WARN" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        WarningEntity warning = new WarningEntity(
                warningId,
                userId,
                userRole,
                severity,
                reason,
                reportId,
                System.currentTimeMillis(),
                expiresAt,
                issuedBy
        );

        return db.warningDao().insert(warning);
    }

    /**
     * Get active warnings for user
     */
    public static List<WarningEntity> getActiveWarnings(String userId) {
        AppDatabase db = getDatabase();
        return db.warningDao().getActiveWarningsForUser(userId);
    }

    /**
     * Get count of active warnings for user
     */
    public static int getActiveWarningCount(String userId) {
        AppDatabase db = getDatabase();
        return db.warningDao().getActiveWarningCountForUser(userId);
    }

    /**
     * Check if user is suspended/banned
     */
    public static boolean isSuspended(String userId) {
        AppDatabase db = getDatabase();
        List<WarningEntity> warnings = getActiveWarnings(userId);
        for (WarningEntity w : warnings) {
            if ("SUSPENSION".equals(w.severity) || "BAN".equals(w.severity)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Expire a warning
     */
    public static void expireWarning(String warningId) {
        AppDatabase db = getDatabase();
        db.warningDao().expireWarning(warningId);
    }

    /**
     * Clean up expired warnings
     */
    public static void cleanExpiredWarnings() {
        AppDatabase db = getDatabase();
        List<WarningEntity> expired = db.warningDao().getExpiredWarnings(System.currentTimeMillis());
        for (WarningEntity w : expired) {
            expireWarning(w.warningId);
        }
    }
}
