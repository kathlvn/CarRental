package com.mobcom.carrental.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mobcom.carrental.database.entities.ReportEntity;

import java.util.List;

@Dao
public interface ReportDao {

    @Insert
    long insert(ReportEntity report);

    @Update
    void update(ReportEntity report);

    @Delete
    void delete(ReportEntity report);

    @Query("SELECT * FROM admin_reports WHERE reportId = :reportId")
    ReportEntity getById(String reportId);

    @Query("SELECT * FROM admin_reports WHERE status = :status ORDER BY createdAt DESC")
    List<ReportEntity> getByStatus(String status);

    @Query("SELECT * FROM admin_reports WHERE reportedId = :userId ORDER BY createdAt DESC")
    List<ReportEntity> getReportsForUser(String userId);

    @Query("SELECT * FROM admin_reports WHERE severity = :severity ORDER BY createdAt DESC")
    List<ReportEntity> getBySeverity(String severity);

    @Query("SELECT * FROM admin_reports WHERE status IN ('OPEN', 'ESCALATED') ORDER BY severity DESC, createdAt ASC")
    List<ReportEntity> getOpenReports();

    @Query("SELECT * FROM admin_reports ORDER BY createdAt DESC")
    List<ReportEntity> getAllReports();

    @Query("SELECT COUNT(*) FROM admin_reports WHERE status IN ('OPEN', 'ESCALATED')")
    int getUnresolvedReportCount();

    @Query("SELECT COUNT(*) FROM admin_reports WHERE reportedId = :userId")
    int getReportCountForUser(String userId);

    @Query("UPDATE admin_reports SET status = :status, resolution = :resolution, adminNotes = :notes, updatedAt = :updatedAt WHERE reportId = :reportId")
    void updateReportStatus(String reportId, String status, String resolution, String notes, long updatedAt);
}
