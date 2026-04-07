package com.mobcom.carrental.models;

import java.io.Serializable;

public class RentalReview implements Serializable {

    private final int providerRating;
    private final int carRating;
    private final String comment;
    private final long submittedAt;

    public RentalReview(int providerRating, int carRating, String comment, long submittedAt) {
        this.providerRating = providerRating;
        this.carRating = carRating;
        this.comment = comment;
        this.submittedAt = submittedAt;
    }

    public int getProviderRating() {
        return providerRating;
    }

    public int getCarRating() {
        return carRating;
    }

    public String getComment() {
        return comment;
    }

    public long getSubmittedAt() {
        return submittedAt;
    }
}