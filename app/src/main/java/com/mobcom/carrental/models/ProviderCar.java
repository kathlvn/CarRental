package com.mobcom.carrental.models;

import java.io.Serializable;
import java.util.List;

public class ProviderCar implements Serializable {

    public enum Status { ACTIVE, INACTIVE, PENDING_REVIEW }

    private String carId;
    private String brand;
    private String model;
    private int year;
    private String plateNumber;
    private String transmission; // Manual / Automatic
    private String fuelType;     // Gasoline / Diesel / Electric
    private int seats;
    private String carType;      // Sedan / SUV / Van / Hatchback
    private double pricePerDay;
    private String location;
    private String imageUrl;
    private Status status;
    private float rating;
    private int totalBookings;
    private List<String> unavailableDates; // blocked dates

    public ProviderCar(String carId, String brand, String model, int year,
                       String plateNumber, String transmission, String fuelType,
                       int seats, String carType, double pricePerDay,
                       String location, String imageUrl, Status status,
                       float rating, int totalBookings) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plateNumber = plateNumber;
        this.transmission = transmission;
        this.fuelType = fuelType;
        this.seats = seats;
        this.carType = carType;
        this.pricePerDay = pricePerDay;
        this.location = location;
        this.imageUrl = imageUrl;
        this.status = status;
        this.rating = rating;
        this.totalBookings = totalBookings;
    }

    // Getters
    public String getCarId()         { return carId; }
    public String getBrand()         { return brand; }
    public String getModel()         { return model; }
    public int getYear()             { return year; }
    public String getPlateNumber()   { return plateNumber; }
    public String getTransmission()  { return transmission; }
    public String getFuelType()      { return fuelType; }
    public int getSeats()            { return seats; }
    public String getCarType()       { return carType; }
    public double getPricePerDay()   { return pricePerDay; }
    public String getLocation()      { return location; }
    public String getImageUrl()      { return imageUrl; }
    public Status getStatus()        { return status; }
    public float getRating()         { return rating; }
    public int getTotalBookings()    { return totalBookings; }
    public String getFullName()      { return brand + " " + model + " " + year; }

    // Setters
    public void setStatus(Status status)   { this.status = status; }
    public void setPricePerDay(double p)   { this.pricePerDay = p; }
}