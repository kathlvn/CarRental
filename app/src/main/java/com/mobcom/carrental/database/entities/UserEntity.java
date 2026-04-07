package com.mobcom.carrental.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey
    @NonNull
    public String userId;
    public String fullName;
    public String email;
    public String phone;
    public String role; // CUSTOMER, PROVIDER, ADMIN
    public String dateOfBirth;
    public String address;
    public String licenseNumber;
    public String licenseExpiry;
    public String companyName; // for providers
    public String businessRegistration; // for providers
    public double rating;
    public int totalBookings;
    public String verificationStatus; // verified, pending, rejected
    public long createdAt;

    public UserEntity() {}

    @Ignore
    public UserEntity(@NonNull String userId, String fullName, String email, String phone,
                      String role, String verificationStatus) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.verificationStatus = verificationStatus;
        this.rating = 0.0;
        this.totalBookings = 0;
        this.createdAt = System.currentTimeMillis();
    }
}
