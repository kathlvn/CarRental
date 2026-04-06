package com.mobcom.carrental.models;

import java.io.Serializable;

public class AdminProvider implements Serializable {

    public enum TrustLevel {
        PROBATION, TRUSTED, FLAGGED, SUSPENDED
    }

    private String providerId;
    private String name;
    private String email;
    private String phone;
    private String location;
    private String memberSince;
    private TrustLevel trustLevel;
    private int totalListings;
    private int approvedListings;
    private int totalBookings;
    private int completedBookings;
    private int totalReports;
    private float averageRating;
    private double totalEarnings;
    private float cancellationRate;
    private String imageUrl;

    public AdminProvider(String providerId, String name, String email,
                         String phone, String location, String memberSince,
                         TrustLevel trustLevel, int totalListings,
                         int approvedListings, int totalBookings,
                         int completedBookings, int totalReports,
                         float averageRating, double totalEarnings,
                         float cancellationRate, String imageUrl) {
        this.providerId        = providerId;
        this.name              = name;
        this.email             = email;
        this.phone             = phone;
        this.location          = location;
        this.memberSince       = memberSince;
        this.trustLevel        = trustLevel;
        this.totalListings     = totalListings;
        this.approvedListings  = approvedListings;
        this.totalBookings     = totalBookings;
        this.completedBookings = completedBookings;
        this.totalReports      = totalReports;
        this.averageRating     = averageRating;
        this.totalEarnings     = totalEarnings;
        this.cancellationRate  = cancellationRate;
        this.imageUrl          = imageUrl;
    }

    // Getters
    public String getProviderId()       { return providerId; }
    public String getName()             { return name; }
    public String getEmail()            { return email; }
    public String getPhone()            { return phone; }
    public String getLocation()         { return location; }
    public String getMemberSince()      { return memberSince; }
    public TrustLevel getTrustLevel()   { return trustLevel; }
    public int getTotalListings()       { return totalListings; }
    public int getApprovedListings()    { return approvedListings; }
    public int getTotalBookings()       { return totalBookings; }
    public int getCompletedBookings()   { return completedBookings; }
    public int getTotalReports()        { return totalReports; }
    public float getAverageRating()     { return averageRating; }
    public double getTotalEarnings()    { return totalEarnings; }
    public float getCancellationRate()  { return cancellationRate; }
    public String getImageUrl()         { return imageUrl; }

    // Setters
    public void setTrustLevel(TrustLevel t) { this.trustLevel = t; }

    // Helpers
    public String getInitials() {
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2)
            return String.valueOf(parts[0].charAt(0)).toUpperCase()
                    + String.valueOf(parts[1].charAt(0)).toUpperCase();
        return String.valueOf(name.charAt(0)).toUpperCase();
    }

    public boolean hasViolations() {
        return totalReports >= 3
                || averageRating < 2.5f
                || cancellationRate > 0.30f;
    }
}