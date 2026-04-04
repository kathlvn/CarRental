// com/mobcom/carrental/models/Rental.java
package com.mobcom.carrental.models;

import java.io.Serializable;

public class Rental implements Serializable {
    public enum Status { PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED }

    private String rentalId;
    private String carName;
    private String carImageUrl;
    private String carPlate;
    private String pickupLocation;
    private String startDate;     // e.g. "Jun 10, 2025"
    private String endDate;       // e.g. "Jun 13, 2025"
    private int totalDays;
    private double totalPrice;
    private Status status;
    private String providerName;

    public Rental(String rentalId, String carName, String carImageUrl,
                  String carPlate, String pickupLocation,
                  String startDate, String endDate,
                  int totalDays, double totalPrice,
                  Status status, String providerName) {
        this.rentalId = rentalId;
        this.carName = carName;
        this.carImageUrl = carImageUrl;
        this.carPlate = carPlate;
        this.pickupLocation = pickupLocation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = totalDays;
        this.totalPrice = totalPrice;
        this.status = status;
        this.providerName = providerName;
    }

    // Getters
    public String getRentalId()       { return rentalId; }
    public String getCarName()        { return carName; }
    public String getCarImageUrl()    { return carImageUrl; }
    public String getCarPlate()       { return carPlate; }
    public String getPickupLocation() { return pickupLocation; }
    public String getStartDate()      { return startDate; }
    public String getEndDate()        { return endDate; }
    public int getTotalDays()         { return totalDays; }
    public double getTotalPrice()     { return totalPrice; }
    public Status getStatus()         { return status; }
    public String getProviderName()   { return providerName; }
}