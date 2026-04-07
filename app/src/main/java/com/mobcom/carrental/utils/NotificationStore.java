package com.mobcom.carrental.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobcom.carrental.models.AppNotification;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.lang.reflect.Type;

public final class NotificationStore {

    private static final String PREF_NAME = "CarRentalNotifications";
    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<AppNotification>>() {}.getType();

    private NotificationStore() {
    }

    public static void pushBookingStatusNotification(@NonNull Context context,
                                                     @NonNull String targetRole,
                                                     @NonNull String bookingId,
                                                     @NonNull String title,
                                                     @NonNull String message) {
        SessionManager sessionManager = new SessionManager(context);
        if (!sessionManager.isNotificationsEnabledForRole(targetRole)) {
            return;
        }
        add(context, targetRole, new AppNotification(
                UUID.randomUUID().toString(),
                targetRole,
                title,
                message,
                bookingId,
                System.currentTimeMillis(),
                false
        ));
    }

    public static void add(@NonNull Context context,
                           @NonNull String role,
                           @NonNull AppNotification notification) {
        List<AppNotification> list = readList(context, role);
        list.add(0, notification);
        writeList(context, role, list);
    }

    @NonNull
    public static List<AppNotification> getByRole(@NonNull Context context,
                                                  @NonNull String role) {
        ArrayList<AppNotification> copy = new ArrayList<>(readList(context, role));
        copy.sort(Comparator.comparingLong(AppNotification::getCreatedAt).reversed());
        return copy;
    }

    public static int getUnreadCount(@NonNull Context context,
                                     @NonNull String role) {
        List<AppNotification> list = readList(context, role);
        int count = 0;
        for (AppNotification notification : list) {
            if (!notification.isRead()) {
                count++;
            }
        }
        return count;
    }

    public static void markAllAsRead(@NonNull Context context,
                                     @NonNull String role) {
        List<AppNotification> list = readList(context, role);
        for (AppNotification notification : list) {
            notification.setRead(true);
        }
        writeList(context, role, list);
    }

    @NonNull
    private static List<AppNotification> readList(@NonNull Context context,
                                                  @NonNull String role) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(getRoleKey(role), "");
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<AppNotification> parsed = GSON.fromJson(json, LIST_TYPE);
        return parsed == null ? new ArrayList<>() : new ArrayList<>(parsed);
    }

    private static void writeList(@NonNull Context context,
                                  @NonNull String role,
                                  @NonNull List<AppNotification> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(getRoleKey(role), GSON.toJson(list)).apply();
    }

    @NonNull
    private static String getRoleKey(@NonNull String role) {
        return String.format(Locale.US, "notifications_%s", role.trim().toUpperCase(Locale.US));
    }
}