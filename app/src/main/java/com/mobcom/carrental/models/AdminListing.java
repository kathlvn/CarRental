package com.mobcom.carrental.models;

import java.io.Serializable;

public class AdminListing implements Serializable {

    public enum Status { PENDING_REVIEW, APPROVED, REJECTED }
    public enum RiskLevel { LOW, MEDIUM, HIGH }

    private String listingId;
    private String carName;
    private String carPlate;
    private String carImageUrl;
    private String carType;
    private String transmission;
    private String fuelType;
    private int seats;
    private double pricePerDay;
    private String location;
    private String orNumber;
    private String crNumber;

    // Provider info
    private String providerId;
    private String providerName;
    private String providerEmail;
    private int providerApprovedListings;
    private int providerReports;
    private float providerRating;
    private String providerTrustLevel; // PROBATION, TRUSTED, FLAGGED, SUSPENDED

    // Listing meta
    private Status status;
    private RiskLevel riskLevel;
    private String submittedAt;
    private String rejectionReason;
    private boolean hasOrCr;

    public AdminListing(String listingId, String carName, String carPlate,
                        String carImageUrl, String carType, String transmission,
                        String fuelType, int seats, double pricePerDay,
                        String location, String orNumber, String crNumber,
                        String providerId, String providerName, String providerEmail,
                        int providerApprovedListings, int providerReports,
                        float providerRating, String providerTrustLevel,
                        Status status, String submittedAt) {
        this.listingId               = listingId;
        this.carName                 = carName;
        this.carPlate                = carPlate;
        this.carImageUrl             = carImageUrl;
        this.carType                 = carType;
        this.transmission            = transmission;
        this.fuelType                = fuelType;
        this.seats                   = seats;
        this.pricePerDay             = pricePerDay;
        this.location                = location;
        this.orNumber                = orNumber;
        this.crNumber                = crNumber;
        this.providerId              = providerId;
        this.providerName            = providerName;
        this.providerEmail           = providerEmail;
        this.providerApprovedListings= providerApprovedListings;
        this.providerReports         = providerReports;
        this.providerRating          = providerRating;
        this.providerTrustLevel      = providerTrustLevel;
        this.status                  = status;
        this.submittedAt             = submittedAt;
        this.hasOrCr                 = !orNumber.isEmpty() && !crNumber.isEmpty();
        this.riskLevel               = calculateRisk();
    }

    // Risk calculation
    private RiskLevel calculateRisk() {
        int score = 0;
        if (providerReports >= 3)          score += 3;
        else if (providerReports >= 1)     score += 1;
        if (!hasOrCr)                      score += 2;
        if (providerApprovedListings == 0) score += 1;
        if (providerRating > 0
                && providerRating < 3.0f)  score += 2;
        if ("FLAGGED".equals(providerTrustLevel)
                || "SUSPENDED".equals(providerTrustLevel)) score += 3;

        if (score >= 4) return RiskLevel.HIGH;
        if (score >= 2) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    // Getters
    public String getListingId()                { return listingId; }
    public String getCarName()                  { return carName; }
    public String getCarPlate()                 { return carPlate; }
    public String getCarImageUrl()              { return carImageUrl; }
    public String getCarType()                  { return carType; }
    public String getTransmission()             { return transmission; }
    public String getFuelType()                 { return fuelType; }
    public int getSeats()                       { return seats; }
    public double getPricePerDay()              { return pricePerDay; }
    public String getLocation()                 { return location; }
    public String getOrNumber()                 { return orNumber; }
    public String getCrNumber()                 { return crNumber; }
    public String getProviderId()               { return providerId; }
    public String getProviderName()             { return providerName; }
    public String getProviderEmail()            { return providerEmail; }
    public int getProviderApprovedListings()    { return providerApprovedListings; }
    public int getProviderReports()             { return providerReports; }
    public float getProviderRating()            { return providerRating; }
    public String getProviderTrustLevel()       { return providerTrustLevel; }
    public Status getStatus()                   { return status; }
    public RiskLevel getRiskLevel()             { return riskLevel; }
    public String getSubmittedAt()              { return submittedAt; }
    public String getRejectionReason()          { return rejectionReason; }
    public boolean isHasOrCr()                  { return hasOrCr; }

    // Setters
    public void setStatus(Status status)                { this.status = status; }
    public void setRejectionReason(String reason)       { this.rejectionReason = reason; }
    public void setProviderApprovedListings(int count)  { this.providerApprovedListings = count; }
}