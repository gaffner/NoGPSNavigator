package com.example.navver;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.VolleyError;

public class Helper {
    public static String volley_error_description(VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            return new String(error.networkResponse.data);
        }

        return "unknown error";
    }

    public static void add_log_line(Context context, String log_line) {
        if (context == null) {
            Log.i("Console logger", log_line);
            return;
        }
        SharedPreferences logger = context.getSharedPreferences("logs", MODE_PRIVATE);
        String logs = logger.getString("content", "");

        if (logs.length() == 0) {
            logs = "[+] " + log_line;
        } else {
            logs = logs + "\n[+] " + log_line;
        }

        SharedPreferences.Editor edit = logger.edit();
        edit.putString("content", logs);
        edit.commit();
    }
}
