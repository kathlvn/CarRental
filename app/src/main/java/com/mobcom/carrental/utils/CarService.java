package com.mobcom.carrental.utils;

import android.content.Context;
import com.mobcom.carrental.database.AppDatabase;
import com.mobcom.carrental.database.entities.CarEntity;
import java.util.List;
import java.util.UUID;

public class CarService {
    private static CarService instance;
    private AppDatabase db;

    private CarService(AppDatabase database) {
        this.db = database;
    }

    public static void initialize(Context context) {
        if (instance == null) {
            instance = new CarService(AppDatabase.getInstance(context));
        }
    }

    public static CarService getInstance() {
        if (instance == null) {
            throw new RuntimeException("CarService not initialized. Call initialize(context) first.");
        }
        return instance;
    }

    /**
     * Add a new car listing
     */
    public String addCar(String providerId, String name, String carType, String transmission,
                        int seats, String fuelType, double pricePerDay, String plateNumber,
                        String location, String imageUrl, String description) {
        String carId = UUID.randomUUID().toString();
        CarEntity car = new CarEntity();
        car.carId = carId;
        car.providerId = providerId;
        car.name = name;
        car.carType = carType;
        car.transmission = transmission;
        car.seats = seats;
        car.fuelType = fuelType;
        car.pricePerDay = pricePerDay;
        car.plateNumber = plateNumber;
        car.location = location;
        car.imageUrl = imageUrl;
        car.description = description;
        car.isAvailable = true;
        car.rating = 0.0;
        car.createdAt = System.currentTimeMillis();
        car.totalRentals = 0;
        car.lastMaintenanceDate = System.currentTimeMillis();
        car.approvalStatus = "PENDING";

        db.carDao().insert(car);
        return carId;
    }

    /**
     * Update an existing car listing
     */
    public void updateCar(CarEntity car) {
        car.updatedAt = System.currentTimeMillis();
        db.carDao().update(car);
    }

    /**
     * Delete a car listing
     */
    public void deleteCar(String carId) {
        CarEntity car = db.carDao().getCarById(carId);
        if (car != null) {
            db.carDao().delete(car);
        }
    }

    /**
     * Get all cars for a provider
     */
    public List<CarEntity> getProviderCars(String providerId) {
        return db.carDao().getCarsByProvider(providerId);
    }

    /**
     * Get a specific car
     */
    public CarEntity getCarById(String carId) {
        return db.carDao().getCarById(carId);
    }

    /**
     * Get all available cars for a provider
     */
    public List<CarEntity> getProviderAvailableCars(String providerId) {
        List<CarEntity> cars = db.carDao().getCarsByProvider(providerId);
        cars.removeIf(car -> !car.isAvailable);
        return cars;
    }

    /**
     * Toggle car availability
     */
    public void toggleCarAvailability(String carId) {
        CarEntity car = db.carDao().getCarById(carId);
        if (car != null) {
            car.isAvailable = !car.isAvailable;
            updateCar(car);
        }
    }
}
