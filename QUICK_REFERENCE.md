# Quick Reference - SQLite Database

## Initialize (Do This First!)

```java
// In MainActivity.java onCreate():
import com.mobcom.carrental.database.DatabaseInitializer;

DatabaseInitializer.initializeDatabase(this);
```

This creates the database with:
- 12 cars
- 7 users (3 customers, 3 providers, 1 admin)
- 2 bookings
- 2 rentals

---

## Access Database Anywhere

```java
AppDatabase db = AppDatabase.getInstance(context);
```

---

## Car Operations

### Get All Available Cars
```java
List<CarEntity> cars = db.carDao().getAvailableCars();
```

### Get Specific Car
```java
CarEntity car = db.carDao().getCarById("CAR001");
```

### Get Cars by Provider
```java
List<CarEntity> cars = db.carDao().getCarsByProvider("P001");
```

### Get Top Rated Cars
```java
List<CarEntity> cars = db.carDao().getTopRatedCars(4.5);
```

### Add New Car
```java
CarEntity car = new CarEntity("CAR999", "P001", "Toyota Vios",
    "Sedan", "Automatic", 5, "Gasoline", 2000, 4.8, "ABC-9999",
    "Metro Manila", "", "Sedan - Automatic", true);
db.carDao().insert(car);
```

### Update Car
```java
car.pricePerDay = 2500;
car.isAvailable = false;
db.carDao().update(car);
```

### Delete Car
```java
db.carDao().delete(car);
```

---

## User Operations

### Get User by ID
```java
UserEntity user = db.userDao().getUserById("C001");
```

### Get User by Email
```java
UserEntity user = db.userDao().getUserByEmail("customer@test.com");
```

### Get All Providers
```java
List<UserEntity> providers = db.userDao().getAllProviders();
```

### Get All Customers
```java
List<UserEntity> customers = db.userDao().getAllCustomers();
```

### Add New User
```java
UserEntity user = new UserEntity("C999", "John Doe",
    "john@test.com", "+63123456789", "CUSTOMER", "verified");
db.userDao().insert(user);
```

---

## Booking Operations

### Get Customer Bookings
```java
List<BookingEntity> bookings = db.bookingDao().getCustomerBookings("C001");
```

### Get Pending Bookings
```java
List<BookingEntity> pending =
    db.bookingDao().getBookingsByStatus("PENDING");
```

### Create Booking
```java
BookingEntity booking = new BookingEntity("BK999", "C001", "CAR001",
    "P001", "2024-05-01", "2024-05-05", 4, 2000, "PENDING", "ONLINE");
db.bookingDao().insert(booking);
```

### Update Booking Status
```java
booking.status = "CONFIRMED";
db.bookingDao().update(booking);
```

---

## Rental Operations

### Get Active Rentals
```java
List<RentalEntity> rentals =
    db.rentalDao().getRentalsByStatus("ACTIVE");
```

### Get Customer Rentals
```java
List<RentalEntity> rentals =
    db.rentalDao().getCustomerRentals("C001");
```

### Create Rental
```java
RentalEntity rental = new RentalEntity("R999", "BK001", "C001",
    "CAR001", "P001", "2024-05-01", "2024-05-05", 4, 8000, "ACTIVE");
db.rentalDao().insert(rental);
```

### Mark Rental as Completed
```java
rental.status = "COMPLETED";
rental.mileageEnd = 45500.5;
rental.fuelCheckEnd = "HALF";
rental.updatedAt = System.currentTimeMillis();
db.rentalDao().update(rental);
```

---

## Advanced Filtering

### Cars under ₱2500/day
```java
List<CarEntity> cars = db.carDao().getCarsByMaxPrice(2500);
```

### Automatic transmission cars
```java
List<CarEntity> cars =
    db.carDao().getCarsByTransmission("Automatic");
```

### 7-seater vehicles
```java
List<CarEntity> cars = db.carDao().getCarsBySeats(7);
```

### Vehicles by type
```java
List<CarEntity> suv = db.carDao().getCarsByType("SUV");
```

---

## Data Management

### Clear Everything & Reinitialize
```java
AppDatabase.wipeAllData(context);
DatabaseInitializer.initializeDatabase(context);
```

### Delete All Cars
```java
db.carDao().deleteAllCars();
```

### Delete All Users
```java
db.userDao().deleteAllUsers();
```

### Delete All Bookings
```java
db.bookingDao().deleteAllBookings();
```

---

## Use Cases

### Load Cars in ExploreFragment
```java
AppDatabase db = AppDatabase.getInstance(requireContext());
List<CarEntity> cars = db.carDao().getAvailableCars();
// Convert to Car objects and update adapter
```

### Show User Profile
```java
UserEntity user = db.userDao().getUserById(userId);
// Display: user.fullName, user.email, user.phone
```

### Show Booking History
```java
List<BookingEntity> bookings =
    db.bookingDao().getCustomerBookings(customerId);
// Show in list: booking.carId, status, totalAmount, dates
```

### Check Car Availability
```java
List<CarEntity> cars = db.carDao().getAvailableCars();
if (cars.isEmpty()) {
    // Show "No cars available"
} else {
    // Display available cars
}
```

---

## Database File Location

View/backup your database:
```
Device File Explorer → data → data → com.mobcom.carrental → databases → car_rental.db
```

Download in Android Studio:
1. Device File Explorer
2. Right-click car_rental.db
3. Download

---

## Common Issues & Solutions

**"No cars showing?"**
→ Make sure `DatabaseInitializer.initializeDatabase(context)` was called

**"NullPointerException when accessing DB?"**
→ Use `requireContext()` in fragments, not `this`

**"Database locked?"**
→ Ensure you're not doing heavy operations on UI thread

**"Want to test with fresh data?"**
→ `AppDatabase.wipeAllData(context)` then reinitialize

---

## Module Structure
```
database/
├── AppDatabase.java              ← Use this to get DB instance
├── DatabaseInitializer.java      ← Initialize once in MainActivity
├── daos/
│   ├── CarDao.java
│   ├── UserDao.java
│   ├── BookingDao.java
│   └── RentalDao.java
└── entities/
    ├── CarEntity.java
    ├── UserEntity.java
    ├── BookingEntity.java
    └── RentalEntity.java
```

---

**That's it! You have a fully functional local SQLite database with 12 cars and sample users ready to go.**
