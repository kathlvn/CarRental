package com.mobcom.carrental.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rental_reviews")
public class RentalReviewEntity {

    @NonNull
    @PrimaryKey
    public String reviewId;

    public String rentalId;           // FK to RentalEntity
    public String customerId;         // Who wrote the review
    public String providerId;         // Who is being reviewed
    public int providerRating;        // 1-5 stars for provider
    public int carRating;             // 1-5 stars for vehicle
    public String comment;            // Review text
    public long submittedAt;          // Timestamp

    public RentalReviewEntity(String reviewId, String rentalId, String customerId,
                             String providerId, int providerRating, int carRating,
                             String comment, long submittedAt) {
        this.reviewId = reviewId;
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.providerId = providerId;
        this.providerRating = providerRating;
        this.carRating = carRating;
        this.comment = comment;
        this.submittedAt = submittedAt;
    }
}
