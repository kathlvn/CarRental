# SQLite Database Setup Guide (Car Rental App)

## ✅ Database Implementation Complete

Your car rental app now uses **SQLite (Room)** as the local database. No external services required!

---

## Quick Start

### Step 1: Initialize Database with Sample Data

Add this code to your `MainActivity.java` or `WelcomeActivity.java` in the `onCreate()` method (only first time):

```java
import com.mobcom.carrental.database.DatabaseInitializer;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Initialize database with sample data (only once, on first launch)
    DatabaseInitializer.initializeDatabase(this);
}
```

This will create the local SQLite database and populate it with:
- **12 Cars** (from 3 providers)
- **3 Customers**
- **3 Providers**
- **2 Bookings**
- **2 Rentals**

### Step 2: Build & Run

```bash
./gradlew clean build
./gradlew installDebug
```

The app will automatically create `car_rental.db` in your device's app data directory.

---

## Database Schema

### Cars Table
```sql
CREATE TABLE cars (
    carId TEXT PRIMARY KEY,
    providerId TEXT,
    name TEXT,
    carType TEXT,
    transmission TEXT,
    seats INTEGER,
    fuelType TEXT,
    pricePerDay REAL,
    rating REAL,
    plateNumber TEXT,
    location TEXT,
    imageUrl TEXT,
    description TEXT,
    isAvailable INTEGER,
    distanceKm REAL,
    createdAt INTEGER,
    totalRentals INTEGER,
    lastMaintenanceDate INTEGER
)
```

### Users Table
```sql
CREATE TABLE users (
    userId TEXT PRIMARY KEY,
    fullName TEXT,
    email TEXT,
    phone TEXT,
    role TEXT,
    dateOfBirth TEXT,
    address TEXT,
    licenseNumber TEXT,
    licenseExpiry TEXT,
    companyName TEXT,
    businessRegistration TEXT,
    rating REAL,
    totalBookings INTEGER,
    verificationStatus TEXT,
    createdAt INTEGER
)
```

### Bookings Table
```sql
CREATE TABLE bookings (
    bookingId TEXT PRIMARY KEY,
    customerId TEXT,
    carId TEXT,
    providerId TEXT,
    startDate TEXT,
    endDate TEXT,
    totalDays INTEGER,
    dailyRate REAL,
    serviceFee REAL,
    totalAmount REAL,
    status TEXT,
    paymentMethod TEXT,
    paymentStatus TEXT,
    pickupLocation TEXT,
    notes TEXT,
    createdAt INTEGER,
    cancelledAt INTEGER,
    cancellationReason TEXT,
    rentalId TEXT
)
```

### Rentals Table
```sql
CREATE TABLE rentals (
    rentalId TEXT PRIMARY KEY,
    bookingId TEXT,
    customerId TEXT,
    carId TEXT,
    providerId TEXT,
    startDate TEXT,
    endDate TEXT,
    totalDays INTEGER,
    baseCost REAL,
    serviceFee REAL,
    totalCost REAL,
    status TEXT,
    mileageStart REAL,
    mileageEnd REAL,
    fuelCheckStart TEXT,
    fuelCheckEnd TEXT,
    createdAt INTEGER,
    updatedAt INTEGER
)
```

---

## Database Access

### Access Database from Any Fragment

```java
// Get database instance
AppDatabase db = AppDatabase.getInstance(requireContext());

// Query cars
List<CarEntity> cars = db.carDao().getAvailableCars();

// Query users
UserEntity user = db.userDao().getUserById("C001");

// Insert booking
BookingEntity booking = new BookingEntity(...);
db.bookingDao().insert(booking);

// Update car
car.isAvailable = false;
db.carDao().update(car);
```

### Common Queries

**Get all available cars:**
```java
List<CarEntity> cars = db.carDao().getAvailableCars();
```

**Get cars by provider:**
```java
List<CarEntity> providerCars = db.carDao().getCarsByProvider("P001");
```

**Get customer bookings:**
```java
List<BookingEntity> bookings = db.bookingDao().getCustomerBookings("C001");
```

**Get active rentals:**
```java
List<RentalEntity> rentals = db.rentalDao().getRentalsByStatus("ACTIVE");
```

**Get top-rated cars:**
```java
List<CarEntity> topCars = db.carDao().getTopRatedCars(4.5);
```

---

## Test Data Included

### Providers
- **P001:** Maria Santos - Santos Car Rental (5 cars)
- **P002:** Antonio Garcia - Garcia Motors (3 cars)
- **P003:** Elena Cruz - Cruz Automotive (4 cars)

### Customers
- **C001:** Juan dela Cruz
- **C002:** Maria Santos
- **C003:** Carlos Reyes

### Cars (12 Total)
- Toyota Vios 2023 - ₱2,000/day
- Honda CR-V 2022 - ₱3,500/day
- Toyota Innova 2023 - ₱2,800/day
- Mitsubishi Mirage 2021 - ₱1,500/day
- Honda Civic 2022 - ₱2,500/day
- Hyundai Accent 2022 - ₱1,800/day
- Kia Seltos 2023 - ₱3,200/day
- Nissan Almera 2021 - ₱1,600/day
- Mazda2 2022 - ₱2,200/day
- Toyota Fortuner 2023 - ₱4,500/day
- Ford Ranger 2022 - ₱3,000/day
- Isuzu D-Max 2022 - ₱3,200/day

### Test Login Accounts
```
Customer: customer@test.com / 1234
Provider: provider@test.com / 1234
Admin: admin@test.com / 1234
```

---

## Database Files

### Entity Classes (Models)
- `CarEntity.java` - Car model
- `UserEntity.java` - User model (Customer, Provider, Admin)
- `BookingEntity.java` - Booking model
- `RentalEntity.java` - Rental model

### DAO Classes (Data Access)
- `CarDao.java` - Car database operations
- `UserDao.java` - User database operations
- `BookingDao.java` - Booking database operations
- `RentalDao.java` - Rental database operations

### Database
- `AppDatabase.java` - Main database class (singleton)
- `DatabaseInitializer.java` - Sample data population

### Fragment Updates
- `ExploreFragment.java` - Updated to load cars from SQLite

---

## Wipe Database

To clear all data and start fresh:

```java
AppDatabase.wipeAllData(context);
```

Then reinitialize:

```java
DatabaseInitializer.initializeDatabase(context);
```

---

## Integration Checklist

- [x] SQLite/Room setup complete
- [x] Database schema created
- [x] Sample data initializer created
- [x] ExploreFragment updated to use database
- [ ] Update LoginActivity to use database authentication
- [ ] Update BookingsFragment to load from database
- [ ] Update ProfileFragment to save/load user data
- [ ] Update ChatFragment to use database messages
- [ ] Update ProviderCarFragment to load provider's cars
- [ ] Add database migrations for schema updates

---

## Advanced Features

### Filtering Cars in Code

```java
// By price
List<CarEntity> cheapCars = db.carDao().getCarsByMaxPrice(2000);

// By transmission
List<CarEntity> automatics = db.carDao().getCarsByTransmission("Automatic");

// By seats
List<CarEntity> sevenSeater = db.carDao().getCarsBySeats(7);

// By type
List<CarEntity> suvs = db.carDao().getCarsByType("SUV");
```

### Complex Queries

For more complex queries, modify the SQL in the DAO:

```java
@Query("SELECT * FROM cars WHERE pricePerDay <= :maxPrice AND seats >= :minSeats AND isAvailable = 1")
List<CarEntity> searchCars(double maxPrice, int minSeats);
```

---

## Performance Tips

1. **Use indexes** - CarEntity already has indexes on `carId`, `providerId`, `isAvailable`
2. **Limit queries** - Use `.limit()` for large result sets
3. **Background threads** - For heavy operations, use:
   ```java
   MainActivity.this.runOnUiThread(() -> {
       // UI updates here
   });
   ```

---

## Database Location

Your SQLite database file is stored at:
```
/data/data/com.mobcom.carrental/databases/car_rental.db
```

View with Android Studio:
1. Device File Explorer
2. Navigate to app → databases
3. Download and inspect with SQLite viewer

---

## Troubleshooting

**"Database initialization not working?"**
- ✓ Call `DatabaseInitializer.initializeDatabase(context)` in MainActivityonCreate()
- ✓ Check logcat for error messages
- ✓ Ensure Room annotation processor is configured

**"No cars showing in Explore?"**
- ✓ Run `DatabaseInitializer.initializeDatabase()` first
- ✓ Check if cars are actually in database
- ✓ Verify `getAvailableCars()` query

**"Database locked error?"**
- ✓ Only one thread should write at a time
- ✓ Use `ThreadPoolExecutor` for concurrent operations

---

## Next Steps

1. **Call initialization** in Main/Welcome Activity
2. **Update other fragments** to use database
3. **Migrate authentication** from SharedPreferences to database
4. **Add more DAOs** as needed (Messages, Reviews, Reports)
5. **Test with real data** from all user roles

---

## Summary

✅ **No external services needed** - Everything is local
✅ **No internet required** - App works offline
✅ **12 cars ready** - With sample customers and providers
✅ **Easy to use** - Simple CRUD operations with DAOs
✅ **Scalable** - Room handles schema migrations automatically

**Ready to integrate with your UI!**
