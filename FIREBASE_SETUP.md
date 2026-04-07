# Firebase Database Setup Guide

## Quick Start (3 Steps)

### Step 1: Create Firebase Project
1. Go to https://console.firebase.google.com
2. Click "Create a project"
3. Name it "car-rental" (or your preferred name)
4. Accept the terms and click "Create project"
5. Wait for project creation to complete

### Step 2: Set Up Firebase in Android
1. In Firebase Console, click "Develop" → "Authentication"
2. Click "Sign-in method" tab
3. Enable "Email/Password" provider
4. Go back and click your project settings (gear icon)
5. Go to "Projects settings" → "Your apps"
6. Click "Android" icon to add app
7. Register with package name: `com.mobcom.carrental`
8. Download `google-services.json`
9. Place the file at: `app/google-services.json` (it's already there - just replace the placeholder)

### Step 3: Enable Firestore Database
1. In Firebase Console, click "Develop" → "Firestore Database"
2. Click "Create Database"
3. Start in **Test Mode** (for development)
4. Choose region closest to you
5. Click "Create"

### Step 4: Enable Cloud Storage (Optional - for car images)
1. In Firebase Console, click "Develop" → "Storage"
2. Click "Get Started"
3. Accept default rules for test mode
4. Click "Done"

---

## Initialize Database with Sample Data

### Option A: In Code (Recommended for Testing)

Add this to your `MainActivity.java` or `WelcomeActivity.java`:

```java
import com.mobcom.carrental.database.DatabaseInitializer;

// In onCreate() method, add:
DatabaseInitializer.initializeDatabase();
```

This will populate your Firestore with 12 sample cars, 3 customers, 3 providers, and 2 bookings.

### Option B: Through Firebase Console

Go to Firestore Database and manually create collections:

1. Create collection: `providers`
   - Document ID: `P001`
   - Add these fields:
     ```
     userId: "P001"
     fullName: "Maria Santos"
     email: "provider@test.com"
     phone: "+63912345678"
     role: "PROVIDER"
     companyName: "Santos Car Rental"
     businessRegistration: "BR-2020-001"
     rating: 4.9
     verificationStatus: "verified"
     ```

---

## Security Rules Configuration

**⚠️ IMPORTANT: Test Mode Expires in 30 Days**

Go to Firestore → Rules and replace with:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Users can read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
      allow read: if request.auth.uid != null;
    }

    // Providers can manage their own cars
    match /providers/{providerId}/cars/{carId} {
      allow read: if request.auth.uid != null;
      allow write: if request.auth.uid == providerId;
    }

    // Customers can read/write their own bookings
    match /customers/{customerId}/bookings/{bookingId} {
      allow read, write: if request.auth.uid == customerId;
    }

    // Rentals
    match /rentals/{rentalId} {
      allow read, write: if request.auth.uid == resource.data.customerId ||
                           request.auth.uid == resource.data.providerId;
    }

    // Everyone can read conversations they're in
    match /conversations/{conversationId} {
      allow read: if request.auth.uid != null;
      allow create, update: if request.auth.uid != null;
    }

    match /conversations/{conversationId}/messages/{messageId} {
      allow read, write: if request.auth.uid != null;
    }

    // Notifications
    match /users/{userId}/notifications/{notificationId} {
      allow read, write: if request.auth.uid == userId;
    }

    // Admin reports
    match /reports/{reportId} {
      allow write: if request.auth.uid != null;
      allow read: if request.auth.uid == resource.data.reporterUserId;
    }
  }
}
```

Click "Publish"

---

## Test Accounts

After setup, these test accounts will work:

| Role     | Email               | Password | ID    |
|----------|---------------------|----------|-------|
| Customer | customer@test.com   | 1234     | C001  |
| Provider | provider@test.com   | 1234     | P001  |
| Admin    | admin@test.com      | 1234     | A001  |

---

## What's Included

✅ **Database Helper Class** - `FirebaseHelper.java` for all CRUD operations
✅ **Data Initializer** - `DatabaseInitializer.java` with sample data
✅ **Schema Documentation** - `FIREBASE_SCHEMA.md` with full structure
✅ **Updated ExploreFragment** - Now loads cars from Firestore instead of hard-coded data
✅ **Firebase Dependencies** - Already added to `build.gradle.kts`

---

## Common Issues

### Issue: "Google Services not found"
**Solution:** Make sure `google-services.json` is in the `app/` directory (not root)

### Issue: "Firestore not enabled"
**Solution:** Go to Firebase Console → Firestore Database → Create Database

### Issue: "Permission denied" errors
**Solution:** Check Firestore Security Rules - make sure you're using the rules above

### Issue: "No cars showing in Explore"
**Solution:** Make sure you ran `DatabaseInitializer.initializeDatabase()` or manually added sample data

---

## Next Steps

1. ✅ **Setup:** Follow the 4 steps above
2. ✅ **Initialize:** Call `DatabaseInitializer.initializeDatabase()`
3. 🔄 **Integration:** Update other fragments to load from Firestore:
   - `ProviderCarFragment` - load provider's cars
   - `BookingsFragment` - load user's bookings
   - `ChatFragment` - load messages
4. 🔄 **Authentication:** Update `LoginActivity` to use Firebase Auth instead of SharedPreferences
5. 🔄 **Real-time:** Add listeners for bookings and messages updates

---

## Database Collections Created

| Collection | Purpose | Contains |
|-----------|---------|----------|
| `users` | User profiles | Customers, Providers, Admins |
| `providers/{id}/cars` | Car listings | Car details for each provider |
| `customers/{id}/bookings` | Booking history | User's bookings |
| `rentals` | Active rentals | On-going and completed rentals |
| `conversations` | Chats | Messages between users |
| `reports` | Complaints | Admin reports system |

---

## Questions?

Refer to:
- Full schema: See `FIREBASE_SCHEMA.md`
- Firebase docs: https://firebase.google.com/docs/firestore
- Android SDK docs: https://firebase.google.com/docs/android/setup
