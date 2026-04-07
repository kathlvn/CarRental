# Car Rental App - Firestore Database Schema

## Setup Instructions

### 1. Firebase Project Setup
1. Go to https://console.firebase.google.com
2. Create a new Firebase project
3. Enable Firestore Database (Cloud Firestore)
4. Enable Firebase Authentication (Email/Password)
5. Enable Firebase Storage (for car images)
6. Enable Firebase Cloud Messaging (for notifications)

### 2. Replace google-services.json
1. Download the google-services.json from your Firebase console
2. Replace the placeholder file at `app/google-services.json`

### 3. Initialize Database
```java
// Call this when app first launches or from initialization screen
DatabaseInitializer.initializeDatabase();
```

## Database Collections Structure

### 1. **users** Collection
Stores all user data (customers, providers, admins)

```
users/{userId}
├── userId: string
├── fullName: string
├── email: string
├── phone: string
├── role: string (CUSTOMER, PROVIDER, ADMIN)
├── dateOfBirth: string (YYYY-MM-DD)
├── address: string
├── licenseNumber: string (for customers)
├── licenseExpiry: date
├── companyName: string (for providers)
├── businessRegistration: string (for providers)
├── bankAccount: string (masked)
├── createdAt: timestamp
├── rating: number
├── totalBookings/totalListings: number
├── verificationStatus: string (verified, pending, rejected)
└── profileImageUrl: string
```

**Permissions:** Users can read/write their own data, providers can read customer basic info

---

### 2. **providers/{providerId}/cars** Subcollection
Stores cars listed by each provider

```
providers/{providerId}/cars/{carId}
├── carId: string
├── name: string
├── carType: string (Sedan, SUV, Hatchback, MPV, Pickup, etc.)
├── transmission: string (Manual, Automatic)
├── seats: number
├── fuelType: string (Gasoline, Diesel, Hybrid)
├── pricePerDay: number
├── rating: number
├── plateNumber: string
├── location: string
├── imageUrl: string
├── description: string
├── isAvailable: boolean
├── createdAt: timestamp
├── totalRentals: number
├── lastMaintenanceDate: timestamp
├── features: array (Air Conditioning, Power Steering, etc.)
└── documents: object (insurance, registration, inspection)
```

---

### 3. **customers/{customerId}/bookings** Subcollection
Stores booking history for each customer

```
customers/{customerId}/bookings/{bookingId}
├── bookingId: string
├── customerId: string
├── carId: string
├── providerId: string
├── startDate: string (YYYY-MM-DD)
├── endDate: string (YYYY-MM-DD)
├── totalDays: number
├── dailyRate: number
├── serviceFee: number
├── totalAmount: number
├── status: string (PENDING, CONFIRMED, COMPLETED, CANCELLED)
├── paymentMethod: string (CASH_ON_PICKUP, ONLINE)
├── paymentStatus: string (UNPAID, PAID)
├── pickupLocation: string
├── notes: string
├── createdAt: timestamp
├── cancelledAt: timestamp
├── cancellationReason: string
└── rentalId: string (reference to rental after confirmation)
```

---

### 4. **rentals** Collection
Stores active and completed rental records

```
rentals/{rentalId}
├── rentalId: string
├── bookingId: string
├── customerId: string
├── carId: string
├── providerId: string
├── startDate: string
├── endDate: string
├── totalDays: number
├── costBreakdown: object
│   ├── dailyRate: number
│   ├── baseCost: number
│   ├── serviceFee: number
│   └── totalCost: number
├── status: string (ACTIVE, COMPLETED, CANCELLED)
├── mileageStart: number
├── mileageEnd: number
├── damageReport: object
│   ├── hasDamage: boolean
│   ├── description: string
│   ├── estimatedCost: number
│   └── images: array
├── fuelCheckStart: string (EMPTY, QUARTER, HALF, THREE_QUARTER, FULL)
├── fuelCheckEnd: string
├── createdAt: timestamp
└── updatedAt: timestamp
```

---

### 5. **rentals/{rentalId}/reviews** Subcollection
Stores reviews and ratings for completed rentals

```
rentals/{rentalId}/reviews/{reviewId}
├── reviewId: string
├── rentalId: string
├── customerId: string
├── carId: string
├── providerId: string
├── rating: number (1-5)
├── comment: string
├── createdAt: timestamp
└── isAnonymous: boolean
```

---

### 6. **conversations** Collection
Stores message conversations between customers and providers

```
conversations/{conversationId}
├── conversationId: string
├── customerId: string
├── providerId: string
├── carId: string (optional)
├── lastMessage: string
├── lastMessageTime: timestamp
├── participants: array
├── createdAt: timestamp
└── isActive: boolean

conversations/{conversationId}/messages/{messageId}
├── messageId: string
├── senderId: string
├── senderName: string
├── senderRole: string
├── message: string
├── timestamp: timestamp
├── isRead: boolean
└── attachments: array (image URLs)
```

---

### 7. **notifications** Collection (Users)
Stores notifications for users

```
users/{userId}/notifications/{notificationId}
├── notificationId: string
├── type: string (booking, message, review, payment, etc.)
├── title: string
├── message: string
├── relatedId: string (bookingId, rentalId, customerId, etc.)
├── isRead: boolean
├── createdAt: timestamp
└── actionUrl: string
```

---

### 8. **reports** Collection
Stores admin reports and complaints

```
reports/{reportId}
├── reportId: string
├── reporterUserId: string
├── reportedUserId: string
├── type: string (inappropriate_behavior, damaged_car, fraud, etc.)
├── subject: string
├── description: string
├── attachments: array
├── status: string (OPEN, INVESTIGATING, RESOLVED, REJECTED)
├── priority: string (LOW, MEDIUM, HIGH, CRITICAL)
├── createdAt: timestamp
├── resolvedAt: timestamp
└── resolution: string
```

---

### 9. **admin/moderationQueue** Document
Queue of items pending admin review

```
admin/moderationQueue
├── listings: array (carId, providerId that need approval)
├── providers: array (userId pending verification)
├── reports: array (reportId pending review)
├── suspiciousActivity: array (alerts from system)
└── lastUpdated: timestamp
```

---

### 10. **app/analytics** Collection (Optional)
For tracking app metrics

```
app/analytics/{eventId}
├── eventType: string
├── timestamp: timestamp
├── userId: string
├── metadata: object
└── sessionId: string
```

---

## Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Users can read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
      allow read: if request.auth.uid != null; // Read-only public profile
    }

    // Providers can manage their own cars
    match /providers/{providerId}/cars/{carId} {
      allow read: if request.auth.uid != null;
      allow write: if request.auth.uid == providerId;
    }

    // Customers can read/write their own bookings
    match /customers/{customerId}/bookings/{bookingId} {
      allow read, write: if request.auth.uid == customerId;
      allow read: if get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'PROVIDER';
    }

    // Rentals accessible to involved parties
    match /rentals/{rentalId} {
      allow read, write: if request.auth.uid == resource.data.customerId ||
                           request.auth.uid == resource.data.providerId ||
                           get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'ADMIN';
    }

    // Messages in conversations
    match /conversations/{conversationId}/messages/{messageId} {
      allow read: if request.auth.uid == get(/databases/$(database)/documents/conversations/$(conversationId)).data.customerId ||
                     request.auth.uid == get(/databases/$(database)/documents/conversations/$(conversationId)).data.providerId;
      allow write: if request.auth.uid != null;
    }

    // Notifications
    match /users/{userId}/notifications/{notificationId} {
      allow read, write: if request.auth.uid == userId;
    }

    // Admin reports
    match /reports/{reportId} {
      allow write: if request.auth.uid != null;
      allow read: if request.auth.uid == resource.data.reporterUserId ||
                     get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'ADMIN';
    }

    // Admin moderation
    match /admin/{document=**} {
      allow read, write: if get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'ADMIN';
    }
  }
}
```

---

## Indexing Requirements

### Recommended Indexes:

1. **Cars by Provider and Availability**
   - Collection: `providers/{providerId}/cars`
   - Fields: `isAvailable` (Ascending), `createdAt` (Descending)

2. **Bookings by Date Range**
   - Collection: `customers/{customerId}/bookings`
   - Fields: `startDate` (Ascending), `status` (Ascending)

3. **Rentals by Status**
   - Collection: `rentals`
   - Fields: `status` (Ascending), `createdAt` (Descending)

4. **Messages by Time**
   - Collection: `conversations/{conversationId}/messages`
   - Fields: `timestamp` (Descending)

---

## Data Migration Plan

### Phase 1: Basic Setup
- [x] Create Firestore collections
- [x] Define schema
- [x] Set up security rules

### Phase 2: Integration
- [ ] Update LoginActivity to use Firebase Auth
- [ ] Update fragments to read from Firestore
- [ ] Update booking flow to save to Firestore

### Phase 3: Real-time Updates
- [ ] Add real-time listeners for bookings
- [ ] Add real-time listeners for messages
- [ ] Add real-time listeners for notifications

---

## Usage Examples

### Creating a Booking
```java
Map<String, Object> booking = new HashMap<>();
booking.put("bookingId", "BK123");
booking.put("carId", "CAR001");
booking.put("startDate", "2024-04-15");
booking.put("status", "PENDING");
// ... more fields

FirebaseHelper.createBooking("C001", "BK123", booking);
```

### Updating Rental Status
```java
Map<String, Object> updates = new HashMap<>();
updates.put("status", "COMPLETED");
updates.put("mileageEnd", 45567.2);

FirebaseHelper.updateRental("R001", updates);
```

### Sending a Message
```java
Map<String, Object> message = new HashMap<>();
message.put("messageId", UUID.randomUUID().toString());
message.put("senderId", "C001");
message.put("message", "Is the car available?");
message.put("timestamp", System.currentTimeMillis());

FirebaseHelper.sendMessage("CONV001", message.get("messageId").toString(), message);
```

---

## Testing Checklist

- [ ] Database initialization works
- [ ] All collections created with sample data
- [ ] CRUD operations work correctly
- [ ] Security rules prevent unauthorized access
- [ ] Real-time listeners fire correctly
- [ ] Notification system works
- [ ] Message system works end-to-end
