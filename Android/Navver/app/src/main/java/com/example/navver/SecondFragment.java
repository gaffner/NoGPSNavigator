package com.example.navver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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

    public SecondFragment() {
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("logic", "logic created");
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);


        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn = (Button) getView().findViewById(R.id.current_location);
        Button search_button = (Button) getView().findViewById(R.id.search);

        btn.setOnClickListener(v -> {
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

    }

    private void setStreetName(EditText street_editText, EditText xy_editText) {
        String raw_street_name = street_editText.getText().toString();
        Log.i("RSzz", raw_street_name);

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "http://nogpsnavigator.top/geocoding/" + raw_street_name;
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.i("MSRpp", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        street_editText.setText(jsonObject.getString("address"));
                        xy_editText.setText(jsonObject.getString("lating"));
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show();
                        street_editText.setText("Address not found... Try again");
                        throw new RuntimeException(e);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("MSRpp", error.toString());
                Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show();

            }

        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void setLocation() {
        try {
            wifi_coordinates();
        } catch (Exception e) {
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
                        Log.d("wifi API", "Response: " + response.toString());
                        try {
                            JSONObject server_response = response.getJSONObject(0);
                            JSONObject inner_location = server_response.getJSONObject("location");

                            String lating = inner_location.getString("latitude") + ", " + inner_location.getString("longitude");
                            Log.i("coordinates", lating);
                            translate_coordinates(lating);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Log.e("wifi API", "Error: " + error.toString());
            }
        });

        // Add the request to the RequestQueue
        queue.add(jsonArrayRequest);
    }


    public JSONArray scanWifiNetworks() throws JSONException {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 1);
        } else Log.i("Permission", "Got ACCESS WIFI permission");
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CHANGE_WIFI_STATE}, 1);
        } else Log.i("Permission", "Got CHANGE WIFI permission");
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else Log.i("Permission", "Got ACCESS_FINE_LOCATION");

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

        Log.i("wifi", networksArray.toString(2));

        if (networksArray.length() == 0) {
            throw new JSONException("Empty array");
        }


        return networksArray;
    }

    private void cellular_coordinates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else Log.i("Permission", "Got COARSE permission");

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
        Log.i("coordinates", coordinates);
    }

    private void translate_coordinates(String coordinates) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "http://nogpsnavigator.top/reverse-geocoding/" + coordinates;
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.i("MSRpp", response);
                    EditText source = (EditText) getView().findViewById(R.id.starting_point_text);
                    source.setText(response.replaceAll("\"", ""));
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("MSRpp", error.toString());
                Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show();

            }

        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}