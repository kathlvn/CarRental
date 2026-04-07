package com.mobcom.carrental.utils;

import android.content.Context;

import com.mobcom.carrental.database.AppDatabase;
import com.mobcom.carrental.database.entities.RentalReviewEntity;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing review persistence
 */
public final class ReviewService {

    private static Context appContext;

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    private static AppDatabase getDatabase() {
        if (appContext == null) {
            throw new IllegalStateException("ReviewService not initialized");
        }
        return AppDatabase.getInstance(appContext);
    }

    /**
     * Save review to database
     */
    public static long saveReview(String rentalId, String customerId, String providerId,
                                 int providerRating, int carRating, String comment) {
        AppDatabase db = getDatabase();

        String reviewId = "REV" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        RentalReviewEntity review = new RentalReviewEntity(
                reviewId,
                rentalId,
                customerId,
                providerId,
                providerRating,
                carRating,
                comment,
                System.currentTimeMillis()
        );

        return db.reviewDao().insert(review);
    }

    /**
     * Get review for rental
     */
    public static RentalReviewEntity getReviewForRental(String rentalId) {
        AppDatabase db = getDatabase();
        return db.reviewDao().getByRentalId(rentalId);
    }

    /**
     * Get average rating for provider
     */
    public static double getAverageProviderRating(String providerId) {
        AppDatabase db = getDatabase();
        Double avg = db.reviewDao().getAverageProviderRating(providerId);
        return avg == null ? 0 : avg;
    }

    /**
     * Get provider's reviews
     */
    public static List<RentalReviewEntity> getProviderReviews(String providerId) {
        AppDatabase db = getDatabase();
        return db.reviewDao().getByProviderId(providerId);
    }

    /**
     * Get customer's reviews
     */
    public static List<RentalReviewEntity> getCustomerReviews(String customerId) {
        AppDatabase db = getDatabase();
        return db.reviewDao().getByCustomerId(customerId);
    }

    /**
     * Check if rental has been reviewed
     */
    public static boolean hasReview(String rentalId) {
        return getReviewForRental(rentalId) != null;
    }

    /**
     * Get review count for provider
     */
    public static int getReviewCount(String providerId) {
        AppDatabase db = getDatabase();
        return db.reviewDao().getReviewCountForProvider(providerId);
    }
}
