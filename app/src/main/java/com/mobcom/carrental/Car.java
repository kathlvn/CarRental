package com.mobcom.carrental;

import java.io.Serializable;

public class Car implements Serializable {

    private String id;
    private String name;
    private String transmission;
    private int seats;
    private String fuelType;
    private double pricePerDay;
    private float distanceKm;
    private int imageResId;
    private String imageUrl;
    private String location;
    private String providerName;
    private String providerId;
    private String plateNumber;
    private String carType;
    private float rating;

    // Original constructor (keeps existing code working)
    public Car(String name, String transmission, int seats, String fuelType,
               double pricePerDay, float distanceKm, int imageResId) {
        this.id           = "";
        this.name         = name;
        this.transmission = transmission;
        this.seats        = seats;
        this.fuelType     = fuelType;
        this.pricePerDay  = pricePerDay;
        this.distanceKm   = distanceKm;
        this.imageResId   = imageResId;
        this.imageUrl     = "";
        this.location     = "";
        this.providerName = "";
        this.providerId   = "";
        this.plateNumber  = "";
        this.carType      = "";
        this.rating       = 0f;
    }

    // Full constructor for booking flow
    public Car(String id, String name, String transmission, int seats,
               String fuelType, double pricePerDay, float distanceKm,
               int imageResId, String imageUrl, String location,
               String providerName, String providerId,
               String plateNumber, String carType, float rating) {
        this.id           = id;
        this.name         = name;
        this.transmission = transmission;
        this.seats        = seats;
        this.fuelType     = fuelType;
        this.pricePerDay  = pricePerDay;
        this.distanceKm   = distanceKm;
        this.imageResId   = imageResId;
        this.imageUrl     = imageUrl;
        this.location     = location;
        this.providerName = providerName;
        this.providerId   = providerId;
        this.plateNumber  = plateNumber;
        this.carType      = carType;
        this.rating       = rating;
    }

    // Getters
    public String getId()           { return id; }
    public String getName()         { return name; }
    public String getTransmission() { return transmission; }
    public int getSeats()           { return seats; }
    public String getFuelType()     { return fuelType; }
    public double getPricePerDay()  { return pricePerDay; }
    public float getDistanceKm()    { return distanceKm; }
    public int getImageResId()      { return imageResId; }
    public String getImageUrl()     { return imageUrl; }
    public String getLocation()     { return location; }
    public String getProviderName() { return providerName; }
    public String getProviderId()   { return providerId; }
    public String getPlateNumber()  { return plateNumber; }
    public String getCarType()      { return carType; }
    public float getRating()        { return rating; }
}