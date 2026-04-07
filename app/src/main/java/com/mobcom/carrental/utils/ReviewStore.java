package com.mobcom.carrental.utils;

import androidx.annotation.Nullable;
import com.mobcom.carrental.models.RentalReview;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ReviewStore {

    private static final Map<String, RentalReview> REVIEWS = new ConcurrentHashMap<>();

    private ReviewStore() {
    }

    public static void saveReview(String rentalId, RentalReview review) {
        if (rentalId == null || rentalId.trim().isEmpty() || review == null) {
            return;
        }
        REVIEWS.put(rentalId, review);
    }

    @Nullable
    public static RentalReview getReview(String rentalId) {
        if (rentalId == null || rentalId.trim().isEmpty()) {
            return null;
        }
        return REVIEWS.get(rentalId);
    }
}