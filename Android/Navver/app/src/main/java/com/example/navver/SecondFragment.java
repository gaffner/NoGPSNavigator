package com.example.navver;

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


import java.util.List;

public class SecondFragment extends Fragment {
    private LocationManager locationManager;
    private WifiManager wifiManager;
    private WebView map;

    private boolean isFirstRun = true;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (isFirstRun) {
            SharedPreferences logger = getContext().getSharedPreferences("logs", MODE_PRIVATE);
            logger.edit().remove("content").commit();

            isFirstRun = false;
        }
    }

    public SecondFragment() {
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button current_location = (Button) getView().findViewById(R.id.current_location);
        Button search_button = (Button) getView().findViewById(R.id.search);

        current_location.setOnClickListener(v -> {
            setLocation();
        });

        search_button.setOnClickListener(v -> {
            EditText source = (EditText) getView().findViewById(R.id.starting_point_xy);
            EditText destination = (EditText) getView().findViewById(R.id.ending_point_xy);

            String source_coordinate = source.getText().toString();
            String destination_coordinate = destination.getText().toString();

            map = getView().findViewById(R.id.map);
            map.getSettings().setJavaScriptEnabled(true);
            map.setWebViewClient(new WebViewClient());
            map.loadUrl("https://www.openstreetmap.org/directions?engine=fossgis_osrm_foot&route=" +
                    source_coordinate + ";" + destination_coordinate);
        });

        EditText starting_text_editText = (EditText) getView().findViewById(R.id.starting_point_text);
        EditText starting_xy_editText = (EditText) getView().findViewById(R.id.starting_point_xy);
        Handler starting_handler = new Handler();

        starting_text_editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // If EditText lost focus, start a delay to execute code after user finished typing
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            setStreetName(starting_text_editText, starting_xy_editText);
                        }
                    };
                    starting_handler.postDelayed(runnable, 0); // Adjust the delay as needed
                }
            }
        });

        EditText ending_text_editText = (EditText) getView().findViewById(R.id.ending_point_text);
        EditText ending_xy_editText = (EditText) getView().findViewById(R.id.ending_point_xy);
        Handler ending_handler = new Handler();

        ending_text_editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // If EditText lost focus, start a delay to execute code after user finished typing
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            setStreetName(ending_text_editText, ending_xy_editText);
                        }
                    };
                    ending_handler.postDelayed(runnable, 0); // Adjust the delay as needed
                }
            }
        });


        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
            add_log_line("Got ACCESS WIFI permission");
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
            add_log_line("Got CHANGE WIFI permission");
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            add_log_line("Got ACCESS_FINE_LOCATION");
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            add_log_line("Got COARSE permission");
        }
    }

    private void setStreetName(EditText street_editText, EditText xy_editText) {
        String raw_street_name = street_editText.getText().toString();
        add_log_line("Trying to translate " + raw_street_name + "...");


        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "http://nogpsnavigator.top/geocoding/" + raw_street_name;
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        street_editText.setText(jsonObject.getString("address"));
                        xy_editText.setText(jsonObject.getString("lating"));
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show();
                        street_editText.setText("Address not found... Try again");
                        add_log_line("Failed setting street name: " + e.toString());
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(requireContext(), "Failed setting street name... please try again!", Toast.LENGTH_SHORT).show();
                add_log_line("Failed setting street name: " + error.toString());
            }

        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void setLocation() {
        try {
            add_log_line("Trying to get wifi coordinates");
            wifi_coordinates();
        } catch (Exception e) {
            add_log_line("Trying to get cellular coordinates [Normal flow]");
            cellular_coordinates();
        }
    }

    private void wifi_coordinates() throws JSONException {
        JSONArray wifi = scanWifiNetworks();
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "http://nogpsnavigator.top/wifi";
        // Request a string response from the provided URL.
        // Request a JSONObject response from the provided URL
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, wifi,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Handle response
                        try {
                            JSONObject server_response = response.getJSONObject(0);
                            JSONObject inner_location = server_response.getJSONObject("location");

                            String lating = inner_location.getString("latitude") + ", " + inner_location.getString("longitude");
                            add_log_line("location from wifi: " + lating);
                            translate_coordinates(lating);

                        } catch (JSONException e) {
                            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            add_log_line("Error in wifi API, fallback to cellular location (" + e.toString() + ")");
                            cellular_coordinates();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                add_log_line("Error in wifi API, fallback to cellular location (" + error.toString() + ")");
                cellular_coordinates();
            }
        });

        // Add the request to the RequestQueue
        queue.add(jsonArrayRequest);
    }


    public JSONArray scanWifiNetworks() throws JSONException {


        wifiManager = (WifiManager) requireContext().getSystemService(Context.WIFI_SERVICE);

        // Ensure Wi-Fi is enabled
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        // Start Wi-Fi scan
        wifiManager.startScan();

        List<ScanResult> scanResults = wifiManager.getScanResults();
        JSONArray networksArray = new JSONArray();

        for (ScanResult result : scanResults) {
            JSONObject network = new JSONObject();
            network.put("macAddress", result.BSSID);
            networksArray.put(network);
        }

        if (networksArray.length() == 0) {
            throw new JSONException("Empty array");
        }


        return networksArray;
    }

    private void cellular_coordinates() {
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        String coordinates = "";

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            coordinates = latitude + ", " + longitude;
            translate_coordinates(coordinates);

        } else {
            coordinates = "Error fetching location";
        }

        EditText source = (EditText) getView().findViewById(R.id.starting_point_xy);
        source.setText(coordinates);
    }

    private void translate_coordinates(String coordinates) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "http://nogpsnavigator.top/reverse-geocoding/" + coordinates;

        // set coordinates at xy box
        EditText source_xy = (EditText) getView().findViewById(R.id.starting_point_xy);
        source_xy.setText(coordinates);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    EditText source_text = (EditText) getView().findViewById(R.id.starting_point_text);
                    source_text.setText(response.replaceAll("\"", ""));
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(requireContext(), "Failed translate coordinates, Try again!", Toast.LENGTH_SHORT).show();
                add_log_line("Failed translate coordinates: " + error.toString());

            }

        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void add_log_line(String log_line) {
        SharedPreferences logger = getContext().getSharedPreferences("logs", MODE_PRIVATE);

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