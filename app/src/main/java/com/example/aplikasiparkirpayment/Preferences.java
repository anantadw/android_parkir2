package com.example.aplikasiparkirpayment;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    static final String KEY_PARKER_ID = "parker_id";
    static final String KEY_PARKER_NAME = "parker_name";
    static final String KEY_TOKEN = "token";

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setParkerId(Context context, int id) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(KEY_PARKER_ID, id);
        editor.apply();
    }

    public static int getParkerId(Context context) {
        return getSharedPreferences(context).getInt(KEY_PARKER_ID, 0);
    }

    public static void setParkerName(Context context, String name) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_PARKER_NAME, name);
        editor.apply();
    }

    public static String getParkerName(Context context) {
        return getSharedPreferences(context).getString(KEY_PARKER_NAME, null);
    }

    public static void setToken(Context context, String token) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context context) {
        return getSharedPreferences(context).getString(KEY_TOKEN, null);
    }

    public static void clearLoggedInUser(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(KEY_PARKER_ID);
        editor.remove(KEY_PARKER_NAME);
        editor.remove(KEY_TOKEN);
        editor.apply();
    }
}
