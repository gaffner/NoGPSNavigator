package com.example.navver;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


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
    }
}