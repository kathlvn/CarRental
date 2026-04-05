package com.mobcom.carrental.models;

import java.io.Serializable;

public class ProviderBooking implements Serializable {

    public enum Status {
        PENDING,    // waiting for provider action
        CONFIRMED,  // provider accepted
        REJECTED,   // provider rejected
        ACTIVE,     // rental is ongoing
        COMPLETED,  // rental finished
        CANCELLED   // customer cancelled
    }

    private String bookingId;
    private String carName;
    private String carPlate;
    private String carImageUrl;
    private String customerName;
    private String customerPhone;
    private String pickupLocation;
    private String startDate;
    private String endDate;
    private int totalDays;
    private double totalAmount;
    private Status status;
    private String createdAt; // when booking was made

    public ProviderBooking(String bookingId, String carName, String carPlate,
                           String carImageUrl, String customerName,
                           String customerPhone, String pickupLocation,
                           String startDate, String endDate,
                           int totalDays, double totalAmount,
                           Status status, String createdAt) {
        this.bookingId       = bookingId;
        this.carName         = carName;
        this.carPlate        = carPlate;
        this.carImageUrl     = carImageUrl;
        this.customerName    = customerName;
        this.customerPhone   = customerPhone;
        this.pickupLocation  = pickupLocation;
        this.startDate       = startDate;
        this.endDate         = endDate;
        this.totalDays       = totalDays;
        this.totalAmount     = totalAmount;
        this.status          = status;
        this.createdAt       = createdAt;
    }

    // Getters
    public String getBookingId()      { return bookingId; }
    public String getCarName()        { return carName; }
    public String getCarPlate()       { return carPlate; }
    public String getCarImageUrl()    { return carImageUrl; }
    public String getCustomerName()   { return customerName; }
    public String getCustomerPhone()  { return customerPhone; }
    public String getPickupLocation() { return pickupLocation; }
    public String getStartDate()      { return startDate; }
    public String getEndDate()        { return endDate; }
    public int getTotalDays()         { return totalDays; }
    public double getTotalAmount()    { return totalAmount; }
    public Status getStatus()         { return status; }
    public String getCreatedAt()      { return createdAt; }

    // Setter
    public void setStatus(Status status) { this.status = status; }
}