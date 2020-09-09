package com.examseatingarrangement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.examseatingarrangement.R;

public class SettingActivity extends AppCompatActivity {

    SwitchCompat dark_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getSupportActionBar().setTitle("Setting");
        SharedPreferences preferences = getSharedPreferences("ESADARK", MODE_PRIVATE);
        dark_mode = findViewById(R.id.dark_mode);
        if (preferences.getBoolean("darkMode", true)) {
            dark_mode.setChecked(true);
        }else{
            dark_mode.setChecked(false);
        }

        dark_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    getSharedPreferences("ESADARK", MODE_PRIVATE).edit().putBoolean("darkMode", true).commit();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    getSharedPreferences("ESADARK", MODE_PRIVATE).edit().putBoolean("darkMode", false).commit();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
