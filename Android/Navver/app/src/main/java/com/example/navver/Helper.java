package com.example.navver;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.content.SharedPreferences;


import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.VolleyError;

public class Helper {
    public static String volley_error_description(VolleyError error)
    {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            return new String(error.networkResponse.data);
        }

        return "unknown error";
    }

    public static void add_log_line(Context context, String log_line) {
        if(context == null)
        {
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
