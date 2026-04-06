package com.mobcom.carrental.models;

import java.io.Serializable;

public class Booking implements Serializable {

    public enum PaymentMethod { CASH_ON_PICKUP, ONLINE }

    private String bookingId;
    private String carId;
    private String carName;
    private String carImageUrl;
    private String carPlate;
    private String providerName;
    private String providerId;
    private String pickupLocation;
    private String startDate;
    private String endDate;
    private int totalDays;
    private double dailyRate;
    private double serviceFee;
    private double totalAmount;
    private String noteToProvider;
    private PaymentMethod paymentMethod;
    private Rental.Status status;
    private String createdAt;

    public Booking(String bookingId, String carId, String carName,
                   String carImageUrl, String carPlate,
                   String providerName, String providerId,
                   String pickupLocation, String startDate,
                   String endDate, int totalDays, double dailyRate,
                   String noteToProvider, PaymentMethod paymentMethod) {
        this.bookingId       = bookingId;
        this.carId           = carId;
        this.carName         = carName;
        this.carImageUrl     = carImageUrl;
        this.carPlate        = carPlate;
        this.providerName    = providerName;
        this.providerId      = providerId;
        this.pickupLocation  = pickupLocation;
        this.startDate       = startDate;
        this.endDate         = endDate;
        this.totalDays       = totalDays;
        this.dailyRate       = dailyRate;
        this.serviceFee      = (dailyRate * totalDays) * 0.05;
        this.totalAmount     = (dailyRate * totalDays) + serviceFee;
        this.noteToProvider  = noteToProvider;
        this.paymentMethod   = paymentMethod;
        this.status          = Rental.Status.PENDING;
        this.createdAt       = "Just now";
    }

    // Getters
    public String getBookingId()          { return bookingId; }
    public String getCarId()              { return carId; }
    public String getCarName()            { return carName; }
    public String getCarImageUrl()        { return carImageUrl; }
    public String getCarPlate()           { return carPlate; }
    public String getProviderName()       { return providerName; }
    public String getProviderId()         { return providerId; }
    public String getPickupLocation()     { return pickupLocation; }
    public String getStartDate()          { return startDate; }
    public String getEndDate()            { return endDate; }
    public int getTotalDays()             { return totalDays; }
    public double getDailyRate()          { return dailyRate; }
    public double getServiceFee()         { return serviceFee; }
    public double getTotalAmount()        { return totalAmount; }
    public String getNoteToProvider()     { return noteToProvider; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public Rental.Status getStatus()      { return status; }
    public String getCreatedAt()          { return createdAt; }
}