package com.mobcom.carrental.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mobcom.carrental.database.entities.BookingEntity;

import java.util.List;

@Dao
public interface BookingDao {
    @Insert
    void insert(BookingEntity booking);

    @Insert
    void insertAll(List<BookingEntity> bookings);

    @Update
    void update(BookingEntity booking);

    @Delete
    void delete(BookingEntity booking);

    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId")
    BookingEntity getBookingById(String bookingId);

    @Query("SELECT * FROM bookings WHERE customerId = :customerId")
    List<BookingEntity> getCustomerBookings(String customerId);

    @Query("SELECT * FROM bookings WHERE providerId = :providerId")
    List<BookingEntity> getProviderBookings(String providerId);

    @Query("SELECT * FROM bookings WHERE status = :status")
    List<BookingEntity> getBookingsByStatus(String status);

    @Query("SELECT * FROM bookings WHERE customerId = :customerId AND status = :status")
    List<BookingEntity> getCustomerBookingsByStatus(String customerId, String status);

    @Query("SELECT * FROM bookings WHERE carId = :carId")
    List<BookingEntity> getCarBookings(String carId);

    @Query("SELECT * FROM bookings")
    List<BookingEntity> getAllBookings();

    @Query("DELETE FROM bookings")
    void deleteAllBookings();
}
