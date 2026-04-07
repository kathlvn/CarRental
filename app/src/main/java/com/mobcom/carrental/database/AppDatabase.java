package com.mobcom.carrental.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mobcom.carrental.database.daos.BookingDao;
import com.mobcom.carrental.database.daos.CarDao;
import com.mobcom.carrental.database.daos.MessageDao;
import com.mobcom.carrental.database.daos.RentalDao;
import com.mobcom.carrental.database.daos.ReviewDao;
import com.mobcom.carrental.database.daos.ReportDao;
import com.mobcom.carrental.database.daos.UserDao;
import com.mobcom.carrental.database.daos.WarningDao;
import com.mobcom.carrental.database.entities.BookingEntity;
import com.mobcom.carrental.database.entities.CarEntity;
import com.mobcom.carrental.database.entities.ConversationThreadEntity;
import com.mobcom.carrental.database.entities.MessageEntity;
import com.mobcom.carrental.database.entities.RentalReviewEntity;
import com.mobcom.carrental.database.entities.RentalEntity;
import com.mobcom.carrental.database.entities.ReportEntity;
import com.mobcom.carrental.database.entities.UserEntity;
import com.mobcom.carrental.database.entities.WarningEntity;

@Database(
    entities = {
        CarEntity.class,
        UserEntity.class,
        BookingEntity.class,
        RentalEntity.class,
        MessageEntity.class,
        ConversationThreadEntity.class,
        RentalReviewEntity.class,
        ReportEntity.class,
        WarningEntity.class
    },
    version = 3,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract CarDao carDao();
    public abstract UserDao userDao();
    public abstract BookingDao bookingDao();
    public abstract RentalDao rentalDao();
    public abstract MessageDao messageDao();
    public abstract ReviewDao reviewDao();
    public abstract ReportDao reportDao();
    public abstract WarningDao warningDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "car_rental.db"
                    )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
                }
            }
        }
        return instance;
    }

    public static void wipeAllData(Context context) {
        // Close the current instance first so the file isn't locked
        if (instance != null) {
            instance.close();
            instance = null;
        }
        // Now delete the database file
        context.deleteDatabase("car_rental.db");
    }
}
