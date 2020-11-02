package com.android.app.weather.logger;

import android.util.Log;

public class Logger {

    public static final String TAG = "WeatherApp";

    public static void info(String message) {
        Log.i(TAG, message);
    }

    public static void debug(String message) {
        Log.d(TAG, message);
    }

    public static void error(String message) {
        Log.e(TAG, message);
    }

}
