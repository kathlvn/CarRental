package com.mobcom.carrental.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "rentals")
public class RentalEntity {
    @PrimaryKey
    @NonNull
    public String rentalId;
    public String bookingId;
    public String customerId;
    public String carId;
    public String providerId;
    public String carName;            // For display (denormalized)
    public String carImageUrl;        // For display
    public String carPlateNumber;     // For display
    public String providerName;       // For display
    public String pickupLocation;
    public String startDate;
    public String endDate;
    public int totalDays;
    public double baseCost;
    public double serviceFee;
    public double totalCost;
    public String status;             // ACTIVE, COMPLETED, CANCELLED
    public double mileageStart;
    public double mileageEnd;
    public String fuelCheckStart;
    public String fuelCheckEnd;
    public long createdAt;
    public long updatedAt;

    public RentalEntity() {
        this.rentalId = "";
    }

    @Ignore
    public RentalEntity(@NonNull String rentalId, String bookingId, String customerId,
                        String carId, String providerId, String startDate, String endDate,
                        int totalDays, double baseCost, String status) {
        this.rentalId = rentalId;
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.carId = carId;
        this.providerId = providerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = totalDays;
        this.baseCost = baseCost;
        this.serviceFee = baseCost * 0.05;
        this.totalCost = baseCost + this.serviceFee;
        this.status = status;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
