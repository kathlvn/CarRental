package com.mobcom.carrental.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mobcom.carrental.database.entities.UserEntity;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(UserEntity user);

    @Insert
    void insertAll(List<UserEntity> users);

    @Update
    void update(UserEntity user);

    @Delete
    void delete(UserEntity user);

    @Query("SELECT * FROM users WHERE userId = :userId")
    UserEntity getUserById(String userId);

    @Query("SELECT * FROM users WHERE email = :email")
    UserEntity getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE role = :role")
    List<UserEntity> getUsersByRole(String role);

    @Query("SELECT * FROM users WHERE role = 'PROVIDER'")
    List<UserEntity> getAllProviders();

    @Query("SELECT * FROM users WHERE role = 'CUSTOMER'")
    List<UserEntity> getAllCustomers();

    @Query("SELECT * FROM users")
    List<UserEntity> getAllUsers();

    @Query("DELETE FROM users")
    void deleteAllUsers();
}
