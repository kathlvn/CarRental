package com.mobcom.carrental.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mobcom.carrental.database.entities.RentalReviewEntity;

import java.util.List;

@Dao
public interface ReviewDao {

    @Insert
    long insert(RentalReviewEntity review);

    @Update
    void update(RentalReviewEntity review);

    @Delete
    void delete(RentalReviewEntity review);

    @Query("SELECT * FROM rental_reviews WHERE reviewId = :reviewId")
    RentalReviewEntity getById(String reviewId);

    @Query("SELECT * FROM rental_reviews WHERE rentalId = :rentalId")
    RentalReviewEntity getByRentalId(String rentalId);

    @Query("SELECT * FROM rental_reviews WHERE customerId = :customerId ORDER BY submittedAt DESC")
    List<RentalReviewEntity> getByCustomerId(String customerId);

    @Query("SELECT * FROM rental_reviews WHERE providerId = :providerId ORDER BY submittedAt DESC")
    List<RentalReviewEntity> getByProviderId(String providerId);

    @Query("SELECT AVG(providerRating) FROM rental_reviews WHERE providerId = :providerId")
    Double getAverageProviderRating(String providerId);

    @Query("SELECT AVG(carRating) FROM rental_reviews WHERE providerId = :providerId")
    Double getAverageCarRating(String providerId);

    @Query("SELECT COUNT(*) FROM rental_reviews WHERE providerId = :providerId")
    int getReviewCountForProvider(String providerId);

    @Query("SELECT * FROM rental_reviews ORDER BY submittedAt DESC LIMIT :limit")
    List<RentalReviewEntity> getRecentReviews(int limit);
}
