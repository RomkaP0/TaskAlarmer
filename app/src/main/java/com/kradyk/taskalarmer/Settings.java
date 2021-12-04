package com.kradyk.taskalarmer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.Locale;

public class Settings extends AppCompatActivity {
    DBHelper dbHelper;
    SQLiteDatabase database;
    public static final String APP_PREFERENCES = "theme";
    final String KEY_RADIOBUTTON_INDEX = "SAVED_RADIO_BUTTON_INDEX";
    RadioGroup radioGroupTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_items);

        Button wipeFills = findViewById(R.id.wipeFills);

        wipeFills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper= new DBHelper(getBaseContext(), "fills",1);
                database = dbHelper.getWritableDatabase();
                database.delete("fills", null,null);

            }
        });
        radioGroupTheme = findViewById(R.id.themeRG);
        radioGroupTheme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case -1:
                        break;
                    case R.id.systemThemeRB:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        SavePreferences(KEY_RADIOBUTTON_INDEX, 0);
                        break;
                    case R.id.lightThemeRB:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        SavePreferences(KEY_RADIOBUTTON_INDEX, 1);
                        break;
                    case R.id.darkThemeRB:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        SavePreferences(KEY_RADIOBUTTON_INDEX, 2);
                        break;
                    default:
                        break;
                }
            }
        });

        MaterialToolbar materialToolbar = findViewById(R.id.topAppBarSt);
        materialToolbar.setTitle(R.string.setting_page);
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        LoadPreferences();
    }
    private void SavePreferences(String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private void LoadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                APP_PREFERENCES, MODE_PRIVATE);
        int savedRadioIndex = sharedPreferences.getInt(
                KEY_RADIOBUTTON_INDEX, 0);
        RadioButton savedCheckedRadioButton = (RadioButton) radioGroupTheme
                .getChildAt(savedRadioIndex);
        savedCheckedRadioButton.setChecked(true);}



    }

