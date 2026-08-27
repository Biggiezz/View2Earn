package com.nguyenmanhphuc.view2earn.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.nguyenmanhphuc.view2earn.app.models.User;

public class SessionManager {
    private static final String PREF_NAME = "view2earn_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private static SessionManager instance;

    private SessionManager(Context context) {
        pref = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void saveUser(User user) {
        if (user == null) return;
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        if (user.getId() != null) editor.putString(KEY_USER_ID, user.getId());
        if (user.getUsername() != null) editor.putString(KEY_USERNAME, user.getUsername());
        if (user.getEmail() != null) editor.putString(KEY_EMAIL, user.getEmail());
        if (user.getToken() != null) editor.putString(KEY_TOKEN, user.getToken());
        editor.putFloat(KEY_BALANCE, (float) user.getBalance());
        editor.apply();
    }

    public void updateBalance(double balance) {
        editor.putFloat(KEY_BALANCE, (float) balance);
        editor.apply();
    }

    public double getBalance() {
        return pref.getFloat(KEY_BALANCE, 0.0f);
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, "");
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, "Guest");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
