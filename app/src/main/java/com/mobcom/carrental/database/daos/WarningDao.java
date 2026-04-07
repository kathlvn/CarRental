package com.mobcom.carrental.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mobcom.carrental.database.entities.WarningEntity;

import java.util.List;

@Dao
public interface WarningDao {

    @Insert
    long insert(WarningEntity warning);

    @Update
    void update(WarningEntity warning);

    @Delete
    void delete(WarningEntity warning);

    @Query("SELECT * FROM admin_warnings WHERE warningId = :warningId")
    WarningEntity getById(String warningId);

    @Query("SELECT * FROM admin_warnings WHERE userId = :userId AND isActive = 1 ORDER BY issuedAt DESC")
    List<WarningEntity> getActiveWarningsForUser(String userId);

    @Query("SELECT * FROM admin_warnings WHERE userId = :userId ORDER BY issuedAt DESC")
    List<WarningEntity> getAllWarningsForUser(String userId);

    @Query("SELECT * FROM admin_warnings WHERE severity = :severity AND isActive = 1 ORDER BY issuedAt DESC")
    List<WarningEntity> getByActiveSeverity(String severity);

    @Query("SELECT * FROM admin_warnings WHERE reportId = :reportId")
    List<WarningEntity> getByReportId(String reportId);

    @Query("SELECT COUNT(*) FROM admin_warnings WHERE userId = :userId AND isActive = 1")
    int getActiveWarningCountForUser(String userId);

    @Query("SELECT * FROM admin_warnings WHERE isActive = 1 AND expiresAt > 0 AND expiresAt < :currentTime")
    List<WarningEntity> getExpiredWarnings(long currentTime);

    @Query("UPDATE admin_warnings SET isActive = 0 WHERE warningId = :warningId")
    void expireWarning(String warningId);

    @Query("SELECT * FROM admin_warnings ORDER BY issuedAt DESC LIMIT :limit")
    List<WarningEntity> getRecentWarnings(int limit);
}
