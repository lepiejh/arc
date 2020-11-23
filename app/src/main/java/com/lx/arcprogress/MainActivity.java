package com.lx.arcprogress;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.czp.library.ArcProgress;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArcProgress arcProgress = findViewById(R.id.myProgress);
        arcProgress.setRoate(1/438f);
        arcProgress.invalidate();
    }
}