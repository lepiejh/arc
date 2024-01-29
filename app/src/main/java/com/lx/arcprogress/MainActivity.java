package com.lx.arcprogress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ved.framework.utils.Utils;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.init(this.getApplication());
    }
}
