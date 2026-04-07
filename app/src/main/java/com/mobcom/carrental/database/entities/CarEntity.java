package com.mobcom.carrental.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "cars")
public class CarEntity {
    @PrimaryKey
    @NonNull
    public String carId;
    public String providerId;
    public String name;
    public String carType;
    public String transmission;
    public int seats;
    public String fuelType;
    public double pricePerDay;
    public double rating;
    public String plateNumber;
    public String location;
    public String imageUrl;
    public String description;
    public boolean isAvailable;
    public long createdAt;
    public int totalRentals;
    public long lastMaintenanceDate;
    public double distanceKm;
    public long updatedAt;
    public String approvalStatus;    // PENDING_REVIEW, APPROVED, REJECTED
    public String rejectionReason;   // For rejected listings

    public CarEntity() {}

    @Ignore
    public CarEntity(@NonNull String carId, String providerId, String name, String carType,
                     String transmission, int seats, String fuelType, double pricePerDay,
                     double rating, String plateNumber, String location, String imageUrl,
                     String description, boolean isAvailable) {
        this.carId = carId;
        this.providerId = providerId;
        this.name = name;
        this.carType = carType;
        this.transmission = transmission;
        this.seats = seats;
        this.fuelType = fuelType;
        this.pricePerDay = pricePerDay;
        this.rating = rating;
        this.plateNumber = plateNumber;
        this.location = location;
        this.imageUrl = imageUrl;
        this.description = description;
        this.isAvailable = isAvailable;
        this.createdAt = System.currentTimeMillis();
        this.totalRentals = 0;
        this.lastMaintenanceDate = System.currentTimeMillis();
    }
}
