package com.mobcom.carrental.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mobcom.carrental.database.entities.CarEntity;

import java.util.List;

@Dao
public interface CarDao {
    @Insert
    void insert(CarEntity car);

    @Insert
    void insertAll(List<CarEntity> cars);

    @Update
    void update(CarEntity car);

    @Delete
    void delete(CarEntity car);

    @Query("SELECT * FROM cars WHERE carId = :carId")
    CarEntity getCarById(String carId);

    @Query("SELECT * FROM cars WHERE providerId = :providerId")
    List<CarEntity> getCarsByProvider(String providerId);

    @Query("SELECT * FROM cars WHERE isAvailable = 1")
    List<CarEntity> getAvailableCars();

    @Query("SELECT * FROM cars")
    List<CarEntity> getAllCars();

    @Query("SELECT * FROM cars WHERE carType = :carType AND isAvailable = 1")
    List<CarEntity> getCarsByType(String carType);

    @Query("SELECT * FROM cars WHERE pricePerDay <= :maxPrice AND isAvailable = 1")
    List<CarEntity> getCarsByMaxPrice(double maxPrice);

    @Query("SELECT * FROM cars WHERE transmission = :transmission AND isAvailable = 1")
    List<CarEntity> getCarsByTransmission(String transmission);

    @Query("SELECT * FROM cars WHERE seats >= :minSeats AND isAvailable = 1")
    List<CarEntity> getCarsBySeats(int minSeats);

    @Query("SELECT * FROM cars WHERE rating >= :minRating AND isAvailable = 1 ORDER BY rating DESC")
    List<CarEntity> getTopRatedCars(double minRating);

    @Query("DELETE FROM cars")
    void deleteAllCars();
}
