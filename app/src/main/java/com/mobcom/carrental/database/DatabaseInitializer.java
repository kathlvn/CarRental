package com.mobcom.carrental.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.mobcom.carrental.database.entities.BookingEntity;
import com.mobcom.carrental.database.entities.CarEntity;
import com.mobcom.carrental.database.entities.RentalEntity;
import com.mobcom.carrental.database.entities.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class DatabaseInitializer {
    private static final String TAG = "DatabaseInitializer";
    private static final String PREFS_NAME = "CarRental_Prefs";
    private static final String KEY_DB_INITIALIZED = "database_initialized";

    public static void initializeDatabase(Context context) {
        // Check if database is already initialized
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isInitialized = prefs.getBoolean(KEY_DB_INITIALIZED, false);

        if (isInitialized) {
            Log.d(TAG, "Database already initialized, skipping initialization");
            return;
        }

        Log.d(TAG, "Starting database initialization");

        // Wipe all existing data FIRST (only on first run)
        AppDatabase.wipeAllData(context);

        // Get a FRESH instance after wiping
        AppDatabase db = AppDatabase.getInstance(context);

        // Populate users
        populateUsers(db);

        // Populate cars
        populateCars(db);

        // Populate bookings
        populateBookings(db);

        // Populate rentals
        populateRentals(db);

        // Mark database as initialized
        prefs.edit().putBoolean(KEY_DB_INITIALIZED, true).apply();

        Log.d(TAG, "Database initialization completed");
    }

    private static void populateUsers(AppDatabase db) {
        Log.d(TAG, "Populating users");
        List<UserEntity> users = new ArrayList<>();

        // Test Accounts (for login testing)
        users.add(new UserEntity("TEST_C001", "Test Customer", "customer@test.com", "+639001234567",
                "CUSTOMER", "verified"));
        users.add(new UserEntity("TEST_P001", "Test Provider", "provider@test.com", "+639001234568",
                "PROVIDER", "verified"));
        users.add(new UserEntity("TEST_A001", "Test Admin", "admin@test.com", "+639001234569",
                "ADMIN", "verified"));

        // Customers
        users.add(new UserEntity("C001", "Sophia Reyes", "sophia.reyes@email.com", "+639155234567",
                "CUSTOMER", "verified"));
        users.add(new UserEntity("C002", "James Tan", "james.tan@email.com", "+639165234568",
                "CUSTOMER", "verified"));
        users.add(new UserEntity("C003", "Nicole Cruz", "nicole.cruz@email.com", "+639175234569",
                "CUSTOMER", "verified"));
        users.add(new UserEntity("C004", "Daniel Santos", "daniel.santos@email.com", "+639185234570",
                "CUSTOMER", "verified"));
        users.add(new UserEntity("C005", "Lisa Wong", "lisa.wong@email.com", "+639195234571",
                "CUSTOMER", "verified"));
        users.add(new UserEntity("C006", "Patrick Gomez", "patrick.gomez@email.com", "+639205234572",
                "CUSTOMER", "verified"));

        // Providers
        users.add(new UserEntity("P001", "Velocity Motors PH", "contact@velocitymotors.com", "+639065234500",
                "PROVIDER", "verified"));
        users.add(new UserEntity("P002", "Premier Auto Leasing", "support@premierauto.com", "+639075234501",
                "PROVIDER", "verified"));
        users.add(new UserEntity("P003", "Speedway Rentals Inc", "info@speedwayrentals.com", "+639085234502",
                "PROVIDER", "verified"));
        users.add(new UserEntity("P004", "Metro Wheels Fleet", "hello@metrowheels.com", "+639095234503",
                "PROVIDER", "verified"));

        // Admin
        users.add(new UserEntity("A001", "Admin Manager", "admin@carrental.com", "+63996234505",
                "ADMIN", "verified"));

        db.userDao().insertAll(users);
    }

    private static void populateCars(AppDatabase db) {
        Log.d(TAG, "Populating cars");
        List<CarEntity> cars = new ArrayList<>();

        // Provider P001 - Elite Car Rental (Economy & Mid-range)
        cars.add(createCar("CAR001", "P001", "Toyota Wigo 2024", "Hatchback", "Manual", 4, "Gasoline", 1200, 4.7, "ECR-2401"));
        cars.add(createCar("CAR002", "P001", "Hyundai i10 2023", "Hatchback", "Automatic", 4, "Gasoline", 1400, 4.6, "ECR-2402"));
        cars.add(createCar("CAR003", "P001", "Honda Jazz 2024", "Hatchback", "Manual", 5, "Gasoline", 1600, 4.8, "ECR-2403"));
        cars.add(createCar("CAR004", "P001", "Mitsubishi Mirage G4 2023", "Sedan", "Automatic", 5, "Gasoline", 1800, 4.7, "ECR-2404"));
        cars.add(createCar("CAR005", "P001", "Toyota Vios 2024", "Sedan", "Automatic", 5, "Gasoline", 2000, 4.8, "ECR-2405"));
        cars.add(createCar("CAR006", "P001", "Nissan Almera 2023", "Sedan", "Manual", 5, "Gasoline", 1700, 4.6, "ECR-2406"));

        // Provider P002 - Fast Track Motors (SUVs & Crossovers)
        cars.add(createCar("CAR007", "P002", "Toyota Raize 2024", "Crossover", "Automatic", 5, "Gasoline", 2400, 4.9, "FTM-2401"));
        cars.add(createCar("CAR008", "P002", "Hyundai Kona 2023", "SUV", "Automatic", 5, "Gasoline", 2800, 4.8, "FTM-2402"));
        cars.add(createCar("CAR009", "P002", "Honda CR-V 2024", "SUV", "Automatic", 5, "Gasoline", 3200, 4.9, "FTM-2403"));
        cars.add(createCar("CAR010", "P002", "Kia Seltos 2023", "SUV", "Automatic", 5, "Diesel", 2600, 4.7, "FTM-2404"));
        cars.add(createCar("CAR011", "P002", "Mazda CX-5 2024", "SUV", "Automatic", 5, "Gasoline", 3100, 4.8, "FTM-2405"));

        // Provider P003 - Premium Vehicles Ltd (Premium & Family)
        cars.add(createCar("CAR012", "P003", "Toyota Innova 2024", "MPV", "Automatic", 8, "Diesel", 3800, 4.9, "PVL-2401"));
        cars.add(createCar("CAR013", "P003", "Honda Odyssey 2023", "MPV", "Automatic", 7, "Gasoline", 3500, 4.8, "PVL-2402"));
        cars.add(createCar("CAR014", "P003", "Toyota Avanza 2024", "MPV", "Manual", 7, "Gasoline", 2100, 4.7, "PVL-2403"));
        cars.add(createCar("CAR015", "P003", "Kia Carnival 2023", "MPV", "Automatic", 9, "Diesel", 4200, 4.9, "PVL-2404"));

        // Provider P004 - City Drive Rentals (Trucks & Commercial)
        cars.add(createCar("CAR016", "P004", "Isuzu D-Max 2024", "Pickup", "Automatic", 5, "Diesel", 3300, 4.8, "CDR-2401"));
        cars.add(createCar("CAR017", "P004", "Ford Ranger 2023", "Pickup", "Automatic", 5, "Diesel", 3400, 4.7, "CDR-2402"));
        cars.add(createCar("CAR018", "P004", "Toyota Fortuner 2024", "SUV", "Automatic", 7, "Diesel", 4500, 4.9, "CDR-2403"));
        cars.add(createCar("CAR019", "P004", "Mitsubishi Pajero 2023", "SUV", "Automatic", 7, "Diesel", 4200, 4.8, "CDR-2404"));
        cars.add(createCar("CAR020", "P004", "Mahindra Bolero 2024", "SUV", "Manual", 7, "Diesel", 2200, 4.6, "CDR-2405"));

        Log.d(TAG, "Inserting " + cars.size() + " cars into database");
        db.carDao().insertAll(cars);

        // Verify insertion
        List<CarEntity> inserted = db.carDao().getAllCars();
        Log.d(TAG, "✓ Successfully inserted " + inserted.size() + " cars");
    }

    private static CarEntity createCar(String carId, String providerId, String name, String carType,
                                       String transmission, int seats, String fuelType,
                                       double pricePerDay, double rating, String plateNumber) {
        CarEntity car = new CarEntity(carId, providerId, name, carType, transmission, seats,
                fuelType, pricePerDay, rating, plateNumber, "Metro Manila", "",
                carType + " - " + transmission + " - " + seats + " seats", true);
        car.approvalStatus = "APPROVED";  // Sample cars are pre-approved
        car.totalRentals = (int)(Math.random() * 100);
        return car;
    }

    private static void populateBookings(AppDatabase db) {
        Log.d(TAG, "Populating bookings");
        List<BookingEntity> bookings = new ArrayList<>();

        // Booking 1: Maria - Toyota Wigo (Budget friendly)
        BookingEntity booking1 = new BookingEntity("BK001", "C001", "CAR001", "P001",
                "2026-04-10", "2026-04-12", 2, 2400, "CONFIRMED", "ONLINE");
        booking1.pickupLocation = "NAIA Terminal 1";
        bookings.add(booking1);

        // Booking 2: Juan - Honda CR-V (Family trip)
        BookingEntity booking2 = new BookingEntity("BK002", "C002", "CAR009", "P002",
                "2026-04-15", "2026-04-22", 7, 22400, "PENDING", "ONLINE");
        booking2.pickupLocation = "Makati CBD";
        bookings.add(booking2);

        // Booking 3: Anna - Toyota Innova (Group travel)
        BookingEntity booking3 = new BookingEntity("BK003", "C003", "CAR012", "P003",
                "2026-04-18", "2026-04-20", 2, 7600, "CONFIRMED", "ONLINE");
        booking3.pickupLocation = "BGC";
        bookings.add(booking3);

        // Booking 4: Carlos - Honda Jazz (City exploration)
        BookingEntity booking4 = new BookingEntity("BK004", "C004", "CAR003", "P001",
                "2026-04-12", "2026-04-14", 2, 3200, "CONFIRMED", "CASH_ON_PICKUP");
        booking4.pickupLocation = "Quezon City";
        bookings.add(booking4);

        // Booking 5: Rosa - Isuzu D-Max (Offroad adventure)
        BookingEntity booking5 = new BookingEntity("BK005", "C005", "CAR016", "P004",
                "2026-04-20", "2026-04-25", 5, 16500, "PENDING", "ONLINE");
        booking5.pickupLocation = "Pasay City";
        bookings.add(booking5);

        // Booking 6: Miguel - Hyundai i10 (Short term)
        BookingEntity booking6 = new BookingEntity("BK006", "C006", "CAR002", "P001",
                "2026-04-11", "2026-04-13", 2, 2800, "CANCELLED", "ONLINE");
        booking6.pickupLocation = "Taguig";
        bookings.add(booking6);

        // Booking 7: Maria - Honda Odyssey (Another booking)
        BookingEntity booking7 = new BookingEntity("BK007", "C001", "CAR013", "P003",
                "2026-05-01", "2026-05-07", 6, 21000, "PENDING", "ONLINE");
        booking7.pickupLocation = "Las Piñas";
        bookings.add(booking7);

        // Booking 8: Juan - Toyota Raize (Weekend getaway)
        BookingEntity booking8 = new BookingEntity("BK008", "C002", "CAR007", "P002",
                "2026-04-25", "2026-04-27", 2, 4800, "CONFIRMED", "ONLINE");
        booking8.pickupLocation = "Cavite";
        bookings.add(booking8);

        db.bookingDao().insertAll(bookings);
    }

    private static void populateRentals(AppDatabase db) {
        Log.d(TAG, "Populating rentals");
        List<RentalEntity> rentals = new ArrayList<>();

        // Rental 1: Maria with Toyota Wigo - Completed
        RentalEntity rental1 = new RentalEntity("R001", "BK001", "C001", "CAR001", "P001",
                "2026-04-05", "2026-04-07", 2, 2400, "COMPLETED");
        rental1.mileageStart = 45230.5;
        rental1.mileageEnd = 45480.2;
        rental1.fuelCheckStart = "FULL";
        rental1.fuelCheckEnd = "FULL";
        rentals.add(rental1);

        // Rental 2: Juan with Honda CR-V - Active
        RentalEntity rental2 = new RentalEntity("R002", "BK002", "C002", "CAR009", "P002",
                "2026-04-01", "2026-04-08", 7, 22400, "ACTIVE");
        rental2.mileageStart = 32100.0;
        rental2.mileageEnd = 32456.8;
        rental2.fuelCheckStart = "FULL";
        rental2.fuelCheckEnd = "THREE_QUARTER";
        rentals.add(rental2);

        // Rental 3: Anna with Toyota Innova - Completed
        RentalEntity rental3 = new RentalEntity("R003", "BK003", "C003", "CAR012", "P003",
                "2026-03-28", "2026-03-30", 2, 7600, "COMPLETED");
        rental3.mileageStart = 28500.0;
        rental3.mileageEnd = 28620.5;
        rental3.fuelCheckStart = "FULL";
        rental3.fuelCheckEnd = "HALF";
        rentals.add(rental3);

        // Rental 4: Carlos with Honda Jazz - Completed
        RentalEntity rental4 = new RentalEntity("R004", "BK004", "C004", "CAR003", "P001",
                "2026-04-02", "2026-04-04", 2, 3200, "COMPLETED");
        rental4.mileageStart = 85340.0;
        rental4.mileageEnd = 85520.3;
        rental4.fuelCheckStart = "FULL";
        rental4.fuelCheckEnd = "FULL";
        rentals.add(rental4);

        // Rental 5: Rosa with Isuzu D-Max - Pending
        RentalEntity rental5 = new RentalEntity("R005", "BK005", "C005", "CAR016", "P004",
                "2026-04-08", "2026-04-08", 0, 0, "PENDING");
        rental5.mileageStart = 125400.0;
        rental5.mileageEnd = 0;
        rental5.fuelCheckStart = "FULL";
        rental5.fuelCheckEnd = "FULL";
        rentals.add(rental5);

        // Rental 6: Miguel with Hyundai i10 - Completed (cancelled booking)
        RentalEntity rental6 = new RentalEntity("R006", "BK006", "C006", "CAR002", "P001",
                "2026-03-25", "2026-03-27", 2, 2800, "COMPLETED");
        rental6.mileageStart = 15230.0;
        rental6.mileageEnd = 15398.2;
        rental6.fuelCheckStart = "FULL";
        rental6.fuelCheckEnd = "THREE_QUARTER";
        rentals.add(rental6);

        // Rental 7: Maria with Honda Odyssey - Pending
        RentalEntity rental7 = new RentalEntity("R007", "BK007", "C001", "CAR013", "P003",
                "2026-04-08", "2026-04-08", 0, 0, "PENDING");
        rental7.mileageStart = 0;
        rental7.mileageEnd = 0;
        rental7.fuelCheckStart = "FULL";
        rental7.fuelCheckEnd = "FULL";
        rentals.add(rental7);

        // Rental 8: Juan with Toyota Raize - Confirmed
        RentalEntity rental8 = new RentalEntity("R008", "BK008", "C002", "CAR007", "P002",
                "2026-04-08", "2026-04-08", 0, 0, "CONFIRMED");
        rental8.mileageStart = 0;
        rental8.mileageEnd = 0;
        rental8.fuelCheckStart = "FULL";
        rental8.fuelCheckEnd = "FULL";
        rentals.add(rental8);

        db.rentalDao().insertAll(rentals);
    }
}
