package com.mobcom.carrental.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mobcom.carrental.database.entities.RentalEntity;

import java.util.List;

@Dao
public interface RentalDao {
    @Insert
    void insert(RentalEntity rental);

    @Insert
    void insertAll(List<RentalEntity> rentals);

    @Update
    void update(RentalEntity rental);

    @Delete
    void delete(RentalEntity rental);

    @Query("SELECT * FROM rentals WHERE rentalId = :rentalId")
    RentalEntity getRentalById(String rentalId);

    @Query("SELECT * FROM rentals WHERE customerId = :customerId")
    List<RentalEntity> getCustomerRentals(String customerId);

    @Query("SELECT * FROM rentals WHERE providerId = :providerId")
    List<RentalEntity> getProviderRentals(String providerId);

    @Query("SELECT * FROM rentals WHERE carId = :carId")
    List<RentalEntity> getCarRentals(String carId);

    @Query("SELECT * FROM rentals WHERE status = :status")
    List<RentalEntity> getRentalsByStatus(String status);

    @Query("SELECT * FROM rentals WHERE bookingId = :bookingId")
    RentalEntity getRentalByBookingId(String bookingId);

    @Query("SELECT * FROM rentals")
    List<RentalEntity> getAllRentals();

    @Query("DELETE FROM rentals")
    void deleteAllRentals();
}
