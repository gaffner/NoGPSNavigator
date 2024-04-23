package com.example.navver;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        SharedPreferences logger = getSafeContext().getSharedPreferences("logs", MODE_PRIVATE);
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

        RequestQueue queue = Volley.newRequestQueue(getSafeContext());
        String url = "http://nogpsnavigator.top/send-logs";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if(validateContext()) return;
                    Helper.add_log_line(getSafeContext(), "Logs sent successfully to server");
                    Toast.makeText(getSafeContext(), "Logs sent successfully to server", Toast.LENGTH_SHORT).show();
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(validateContext()) return;
                String description = Helper.volley_error_description(error);
                Helper.add_log_line(getSafeContext(), "Failed sending logs to server: " + description);
                Toast.makeText(getSafeContext(), "Failed sending logs to server", Toast.LENGTH_SHORT).show();
            }

        }) {
            @Override
            public byte[] getBody() {
                if(validateContext()) return null;
                SharedPreferences logger = getSafeContext().getSharedPreferences("logs", MODE_PRIVATE);
                String logs = logger.getString("content", "");

                return logs.getBytes();
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private Context getSafeContext() {
        return getContext();
    }
    private boolean validateContext() {
        if(getContext() == null) {
            Log.i("saved", "baby just saves");
        }
        return getContext() == null;
    }

}