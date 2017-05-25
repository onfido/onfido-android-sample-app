package com;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class L {

    private static String TAG = "ONFIDO";

    public static void d(Context context, String title, String msg) {
        ui(context, title, msg);
        catd(title, msg);
    }

    public static void d(Context context, String msg) {
        ui(context, msg);
        catd(msg);
    }

    public static void d(String title, String msg) {
        catd(title, msg);
    }

    public static void d(String msg) {
        catd(msg);
    }

    public static void e(String msg) {
        cate(TAG, msg);
    }

    private static void ui(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private static void ui(Context context, String title, String msg) {
        Toast.makeText(context, title + ": " + msg, Toast.LENGTH_SHORT).show();
    }

    private static void catd(String msg) {
        Log.d(TAG, msg);
    }

    private static void catd(String title, String msg) {
        Log.d(title, msg);
    }

    private static void cate(String title, String msg) {
        Log.e(title, msg);
    }
}
