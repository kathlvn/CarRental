package com.mobcom.carrental.utils;

import android.content.Context;

import com.mobcom.carrental.database.AppDatabase;
import com.mobcom.carrental.database.entities.BookingEntity;
import com.mobcom.carrental.database.entities.RentalEntity;
import com.mobcom.carrental.models.Booking;
import com.mobcom.carrental.models.Rental;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing booking persistence and workflow
 */
public final class BookingService {

    private static Context appContext;

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    private static AppDatabase getDatabase() {
        if (appContext == null) {
            throw new IllegalStateException("BookingService not initialized");
        }
        return AppDatabase.getInstance(appContext);
    }

    /**
     * Check if customer is suspended before allowing booking
     */
    public static boolean isCustomerSuspended(String customerId) {
        return ReportService.isSuspended(customerId);
    }

    /**
     * Save booking to database
     */
    public static void saveBooking(String bookingId, String customerId, String carId,
                                  String providerId, String startDate, String endDate,
                                  int totalDays, double dailyRate, String pickupLocation,
                                  String notes, String paymentMethod) {
        AppDatabase db = getDatabase();

        BookingEntity entity = new BookingEntity(
                bookingId,
                customerId,
                carId,
                providerId,
                startDate,
                endDate,
                totalDays,
                dailyRate,
                "PENDING",
                paymentMethod
        );
        entity.pickupLocation = pickupLocation;
        entity.notes = notes;

        db.bookingDao().insert(entity);
    }

    /**
     * Get booking by ID
     */
    public static BookingEntity getBooking(String bookingId) {
        AppDatabase db = getDatabase();
        return db.bookingDao().getBookingById(bookingId);
    }

    /**
     * Get all pending bookings for provider
     */
    public static List<BookingEntity> getPendingBookingsForProvider(String providerId) {
        AppDatabase db = getDatabase();
        return db.bookingDao().getCustomerBookingsByStatus(providerId, "PENDING");
    }

    /**
     * Get all bookings for customer
     */
    public static List<BookingEntity> getBookingsForCustomer(String customerId) {
        AppDatabase db = getDatabase();
        return db.bookingDao().getCustomerBookings(customerId);
    }

    /**
     * Accept booking and create rental
     */
    public static void acceptBooking(String bookingId) {
        AppDatabase db = getDatabase();

        // Create rental from booking
        BookingEntity booking = db.bookingDao().getBookingById(bookingId);
        if (booking != null) {
            // Update booking status
            booking.status = "CONFIRMED";
            db.bookingDao().update(booking);

            String rentalId = "RNT" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

            // Get car info
            com.mobcom.carrental.database.entities.CarEntity car = db.carDao().getCarById(booking.carId);

            RentalEntity rental = new RentalEntity(
                    rentalId,
                    bookingId,
                    booking.customerId,
                    booking.carId,
                    booking.providerId,
                    booking.startDate,
                    booking.endDate,
                    booking.totalDays,
                    booking.totalAmount,
                    "ACTIVE"
            );

            // Populate car display info
            if (car != null) {
                rental.carName = car.name;
                rental.carImageUrl = car.imageUrl;
                rental.carPlateNumber = car.plateNumber;
            }

            // Populate provider name
            com.mobcom.carrental.database.entities.UserEntity provider = db.userDao().getUserById(booking.providerId);
            if (provider != null) {
                rental.providerName = provider.fullName;
            }

            rental.pickupLocation = booking.pickupLocation;

            db.rentalDao().insert(rental);

            // Link rental to booking
            booking.rentalId = rentalId;
            db.bookingDao().update(booking);
        }
    }

    /**
     * Reject booking
     */
    public static void rejectBooking(String bookingId, String reason) {
        AppDatabase db = getDatabase();
        BookingEntity booking = db.bookingDao().getBookingById(bookingId);
        if (booking != null) {
            booking.status = "REJECTED";
            booking.notes = reason;
            db.bookingDao().update(booking);
        }
    }

    /**
     * Cancel booking
     */
    public static void cancelBooking(String bookingId, String reason) {
        AppDatabase db = getDatabase();
        BookingEntity booking = db.bookingDao().getBookingById(bookingId);
        if (booking != null) {
            booking.status = "CANCELLED";
            booking.notes = reason;
            db.bookingDao().update(booking);
        }
    }

    /**
     * Complete booking (move to rental completion)
     */
    public static void completeBooking(String bookingId) {
        AppDatabase db = getDatabase();
        BookingEntity booking = db.bookingDao().getBookingById(bookingId);
        if (booking != null) {
            booking.status = "COMPLETED";
            db.bookingDao().update(booking);
        }
    }
}
