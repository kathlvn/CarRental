package com.mobcom.carrental.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CarFormData implements Serializable {

    private String brand;
    private String customBrand;
    private String model;
    private String customModel;
    private int year;
    private String carType;
    private String transmission;
    private String fuelType;
    private int seats;
    private double pricePerDay;
    private String location;
    private String plateNumber;
    private String orNumber;
    private String crNumber;
    private List<String> imageUris = new ArrayList<>();
    private boolean alwaysAvailable = true;

    // Getters & Setters
    public String getBrand()              { return brand; }
    public void setBrand(String b)        { this.brand = b; }

    public String getCustomBrand()        { return customBrand; }
    public void setCustomBrand(String b)  { this.customBrand = b; }

    public String getModel()              { return model; }
    public void setModel(String m)        { this.model = m; }

    public String getCustomModel()        { return customModel; }
    public void setCustomModel(String m)  { this.customModel = m; }

    public int getYear()                  { return year; }
    public void setYear(int y)            { this.year = y; }

    public String getCarType()            { return carType; }
    public void setCarType(String t)      { this.carType = t; }

    public String getTransmission()       { return transmission; }
    public void setTransmission(String t) { this.transmission = t; }

    public String getFuelType()           { return fuelType; }
    public void setFuelType(String f)     { this.fuelType = f; }

    public int getSeats()                 { return seats; }
    public void setSeats(int s)           { this.seats = s; }

    public double getPricePerDay()        { return pricePerDay; }
    public void setPricePerDay(double p)  { this.pricePerDay = p; }

    public String getLocation()           { return location; }
    public void setLocation(String l)     { this.location = l; }

    public String getPlateNumber()        { return plateNumber; }
    public void setPlateNumber(String p)  { this.plateNumber = p; }

    public String getOrNumber()           { return orNumber; }
    public void setOrNumber(String o)     { this.orNumber = o; }

    public String getCrNumber()           { return crNumber; }
    public void setCrNumber(String c)     { this.crNumber = c; }

    public List<String> getImageUris()    { return imageUris; }
    public void setImageUris(List<String> u) { this.imageUris = u; }

    public boolean isAlwaysAvailable()       { return alwaysAvailable; }
    public void setAlwaysAvailable(boolean a) { this.alwaysAvailable = a; }

    // Returns the final brand string (custom or preset)
    public String getEffectiveBrand() {
        return "Others".equals(brand) ? customBrand : brand;
    }

    // Returns the final model string (custom or preset)
    public String getEffectiveModel() {
        return "Others".equals(model) ? customModel : model;
    }
}