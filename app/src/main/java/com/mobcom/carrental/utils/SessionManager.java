// com/mobcom/carrental/utils/SessionManager.java
package com.mobcom.carrental.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME    = "CarRentalSession";
    private static final String KEY_IS_GUEST = "isGuest";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_ROLE     = "role";
    private static final String KEY_USER_ID  = "userId";
    private static final String KEY_NAME     = "userName";
    private static final String KEY_EMAIL    = "userEmail";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notificationsEnabled";

    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_PROVIDER = "PROVIDER";
    public static final String ROLE_ADMIN    = "ADMIN";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ── Guest ────────────────────────────────────────────────────────────────

    public void setGuest() {
        editor.putBoolean(KEY_IS_GUEST, true);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }

    public boolean isGuest() {
        return prefs.getBoolean(KEY_IS_GUEST, false);
    }

    // ── Login ────────────────────────────────────────────────────────────────

    public void login(String userId, String name, String email, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putBoolean(KEY_IS_GUEST, false);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getRole()    { return prefs.getString(KEY_ROLE, ""); }
    public String getUserId()  { return prefs.getString(KEY_USER_ID, ""); }
    public String getName()    { return prefs.getString(KEY_NAME, ""); }
    public String getEmail()   { return prefs.getString(KEY_EMAIL, ""); }

    private String getNotificationsKeyForRole(String role) {
        String safeRole = role == null ? "" : role.trim();
        if (safeRole.isEmpty()) {
            safeRole = "GLOBAL";
        }
        return KEY_NOTIFICATIONS_ENABLED + "_" + safeRole;
    }

    public boolean isNotificationsEnabledForRole(String role) {
        return prefs.getBoolean(getNotificationsKeyForRole(role), true);
    }

    public void setNotificationsEnabledForRole(String role, boolean enabled) {
        editor.putBoolean(getNotificationsKeyForRole(role), enabled);
        editor.apply();
    }

    public boolean isNotificationsEnabled() {
        return isNotificationsEnabledForRole(getRole());
    }

    public void setNotificationsEnabled(boolean enabled) {
        setNotificationsEnabledForRole(getRole(), enabled);
    }

    public void updateProfile(String name, String email) {
        editor.putString(KEY_NAME, name == null ? "" : name.trim());
        editor.putString(KEY_EMAIL, email == null ? "" : email.trim());
        editor.apply();
    }

    // ── Logout ───────────────────────────────────────────────────────────────

    public void logout() {
        editor.clear();
        editor.apply();
    }
}