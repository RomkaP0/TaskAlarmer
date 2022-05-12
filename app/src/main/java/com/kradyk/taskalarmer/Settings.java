package com.kradyk.taskalarmer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class Settings extends AppCompatActivity {
    DBHelper dbHelper;
    SQLiteDatabase database;
    public static final String APP_PREFERENCES = "theme";
    final String KEY_RADIOBUTTON_INDEX = "SAVED_RADIO_BUTTON_INDEX";
    RadioGroup radioGroupTheme;
    static int c=0; //DON`T TOUCH THIS

    public static final String LANG_APP_PREFERENCES = "lang";
    final String LANG_KEY_RADIOBUTTON_INDEX = "LAND_SAVED_RADIO_BUTTON_INDEX";
    RadioGroup radioGroupLang;

    Button signout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_items);

        Button wipeFills = findViewById(R.id.wipeFills);
        signout = findViewById(R.id.signout);

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
        radioGroupLang = findViewById(R.id.langRG);
        radioGroupLang.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case -1:
                        break;
                    case R.id.EN:
                        LangSavePreferences(LANG_KEY_RADIOBUTTON_INDEX, 0);
                        c+=1;
                        if(c>1)
                            finish();
                        break;
                    case R.id.RU:
                        LangSavePreferences(LANG_KEY_RADIOBUTTON_INDEX, 1);
                        c+=1;
                        if(c>1)
                            finish();
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
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null) {
                    FirebaseAuth.getInstance().signOut();
            }}
        });
        LangLoadPreferences();
        LoadPreferences();
    }


    private void SavePreferences(String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    private void LangSavePreferences(String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                LANG_APP_PREFERENCES, MODE_PRIVATE);
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

    private void LangLoadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                LANG_APP_PREFERENCES, MODE_PRIVATE);
        int savedRadioIndex = sharedPreferences.getInt(
                LANG_KEY_RADIOBUTTON_INDEX, 0);
        RadioButton savedCheckedRadioButton = (RadioButton) radioGroupLang
                .getChildAt(savedRadioIndex);
        savedCheckedRadioButton.setChecked(true);}

    @Override
    protected void onStop() {
        c=0;
        super.onStop();
    }
}

