package com.ricky9090.smallworld;

import android.util.Log;

public class Logger {

    public static final String LOG_TAG = "Robotalk";

    public static void LOG_E(String msg, Throwable e) {
        Log.e(LOG_TAG, msg);
        e.printStackTrace();
    }

    public static void LOG_E(String msg) {
        Log.e(LOG_TAG, msg);
    }

    public static void LOG_D(String msg) {
        Log.d(LOG_TAG, msg);
    }
}
