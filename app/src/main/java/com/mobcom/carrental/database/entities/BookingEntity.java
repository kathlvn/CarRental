package com.mobcom.carrental.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookings")
public class BookingEntity {
    @PrimaryKey
    @NonNull
    public String bookingId;
    public String customerId;
    public String carId;
    public String providerId;
    public String startDate;
    public String endDate;
    public int totalDays;
    public double dailyRate;
    public double serviceFee;
    public double totalAmount;
    public String status; // PENDING, CONFIRMED, COMPLETED, CANCELLED
    public String paymentMethod; // CASH_ON_PICKUP, ONLINE
    public String paymentStatus; // UNPAID, PAID
    public String pickupLocation;
    public String notes;
    public long createdAt;
    public long cancelledAt;
    public String cancellationReason;
    public String rentalId;
    public String customerName;      // Denormalized for display
    public String customerPhone;     // Denormalized for display
    public String carPlateNumber;    // Denormalized for display

    public BookingEntity() {
        this.bookingId = "";
    }

    @Ignore
    public BookingEntity(@NonNull String bookingId, String customerId, String carId, String providerId,
                         String startDate, String endDate, int totalDays, double dailyRate,
                         String status, String paymentMethod) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.carId = carId;
        this.providerId = providerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = totalDays;
        this.dailyRate = dailyRate;
        this.serviceFee = dailyRate * totalDays * 0.05;
        this.totalAmount = (dailyRate * totalDays) + this.serviceFee;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = "UNPAID";
        this.createdAt = System.currentTimeMillis();
    }
}
