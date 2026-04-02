package com.mobcom.carrental;

public class Car {
    private String name;
    private String transmission;
    private int seats;
    private String fuelType;
    private double pricePerDay;
    private float distanceKm;
    private int imageResId;

    public Car(String name, String transmission, int seats, String fuelType, double pricePerDay, float distanceKm, int imageResId) {
        this.name = name;
        this.transmission = transmission;
        this.seats = seats;
        this.fuelType = fuelType;
        this.pricePerDay = pricePerDay;
        this.distanceKm = distanceKm;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getTransmission() { return transmission; }
    public int getSeats() { return seats; }
    public String getFuelType() { return fuelType; }
    public double getPricePerDay() { return pricePerDay; }
    public float getDistanceKm() { return distanceKm; }
    public int getImageResId() { return imageResId; }
}