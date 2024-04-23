package com.example.navver;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.navver.Helper;


public class ThirdFragment extends Fragment {

    public ThirdFragment() {
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_third, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        TextView log_box = (TextView) getView().findViewById(R.id.log_box);
        SharedPreferences logger = getContext().getSharedPreferences("logs", MODE_PRIVATE);
        String logs = logger.getString("content", "");
        log_box.setText(logs);
        log_box.setMovementMethod(new ScrollingMovementMethod());
        log_box.setTextIsSelectable(true);

        Button send_logs = (Button) getView().findViewById(R.id.send_logs);
        send_logs.setOnClickListener(v -> {
            send_logs_to_server();
        });

    }

    private void send_logs_to_server() {

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "http://192.168.1.193:8080/send-logs";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Helper.add_log_line(getContext(), "Logs sent successfully to server");
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String description = Helper.volley_error_description(error);
                Helper.add_log_line(getContext(), "Failed sending logs to server: " + description);
            }

        }) {
            @Override
            public byte[] getBody() {
                SharedPreferences logger = getContext().getSharedPreferences("logs", MODE_PRIVATE);
                String logs = logger.getString("content", "");

                return logs.getBytes();
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }



}