package com.kradyk.taskalarmer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    int day;
    int month;
    int yr;
    CalendarView mcalendarView;
    CreateEventActivity dlg1;
    Settings stn;
    Calendar current;
    String s;
    RecyclerView recyclerView;
    ItemAdapter adapter;

    final String LOG_TAG = "myLogs";
    DBHelper dbHelper;
    BottomNavigationView bottomNavigationView;


    ArrayList<Item> items = new ArrayList<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadPreferences();
        setContentView(R.layout.activity_main);

        Calendar today = Calendar.getInstance();
        yr = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH);
        day = today.get(Calendar.DAY_OF_MONTH);

        current = Calendar.getInstance();

        dlg1 = new CreateEventActivity();
        stn = new Settings();

        recyclerView = findViewById(R.id.rv);

        mcalendarView = findViewById(R.id.calendarview);
        mcalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                current.set(year, month, dayOfMonth);
                setInitialData(year, month, dayOfMonth);

            }
        });

        bottomNavigationView = findViewById(R.id.nav);
        bottomNavigationView.clearFocus();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.button1):
                        mcalendarView.setDate(System.currentTimeMillis());
                        current.set(yr, month, day);
                        setInitialData(yr, month, day);
                        break;
                    case (R.id.button2):
                        Intent i = new Intent("com.example.CreateEventActivity");
                        Bundle extras = new Bundle();
                        s = "";
                        if ((current.get(Calendar.DAY_OF_MONTH)) < 10)
                            s += "0" + current.get(Calendar.DAY_OF_MONTH) + ".";
                        else
                            s += (current.get(Calendar.DAY_OF_MONTH)) + ".";
                        if ((current.get(Calendar.MONTH) + 1) < 10)
                            s += "0" + (current.get(Calendar.MONTH) + 1) + ".";
                        else
                            s += (current.get(Calendar.MONTH) + 1) + ".";
                        s += current.get(Calendar.YEAR);
                        extras.putString("date", s);
                        extras.putString("from", "main");
                        i.putExtras(extras);
                        startActivity(i);
                        break;
                    case (R.id.button3):
                        Intent st = new Intent("com.example.Settings");
                        startActivity(st);
                        break;
                    case (R.id.button4):
                        Intent srch = new Intent("com.example.SearchActivity");
                        startActivity(srch);
                        break;

                }
                return false;
            }
        });

    }

    private void setInitialData(int year1, int month1, int day1) {

        items.clear();
        String full;
        if (day1 < 10)
            full = "0" + day1 + ".";
        else
            full = day1 + ".";
        if ((month1 + 1) < 10)
            full += "0" + (month1 + 1) + ".";
        else
            full += (month1 + 1) + ".";
        full += year1;

        dbHelper = new DBHelper(this, "String", 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("events", new String[]{"_id", "title", "timeb", "timee", "timenotif","data", "description", "repeating","paral"}, "data = ? OR repeating = ? OR repeating = ? OR ((repeating = ? OR repeating = ?) AND interval = ? ) OR ((repeating = ? OR repeating = ?) AND interval = ? ) OR ((repeating = ? OR repeating = ?) AND interval = ? )", new String[]{full, "Daily", "Ежедневно", "Weekly", "Еженедельно", String.valueOf(current.get(Calendar.DAY_OF_WEEK)),"Monthly","Ежемесячно", String.valueOf(current.get(Calendar.DAY_OF_MONTH)), "Yearly", "Ежегодно", current.get(Calendar.DAY_OF_MONTH)+"."+(current.get(Calendar.MONTH)+1)}, null, null, "timeb ASC");
        if (c.moveToFirst()) {

            int dataIndex = c.getColumnIndex(DBHelper.KEY_DATA);
            int idIndex = c.getColumnIndex(DBHelper.KEY_ID);

            int titleIndex = c.getColumnIndex(DBHelper.KEY_TITLE);
            int descIndex = c.getColumnIndex(DBHelper.KEY_DESCRIPTIONS);

            int timebIndex = c.getColumnIndex(DBHelper.KEY_TIMEB);
            int timeeIndex = c.getColumnIndex(DBHelper.KEY_TIMEE);
            int timenotifIndex = c.getColumnIndex(DBHelper.KEY_TIMENOTIF);
            int repeatIndex = c.getColumnIndex(DBHelper.KEY_REPEATING);
            int paralIndex = c.getColumnIndex(DBHelper.KEY_PARAL);

            do {

                items.add(new Item(c.getString(idIndex), c.getString(titleIndex), c.getString(dataIndex), c.getString(timebIndex), c.getString(timeeIndex), c.getString(timenotifIndex), c.getString(descIndex), c.getString(repeatIndex), c.getString(paralIndex)));
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
        buildRV();

    }

    public void onStart() {
        super.onStart();
        setInitialData(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH));

    }

    public void buildRV() {
        adapter = new ItemAdapter(this, items);
        recyclerView.setAdapter(adapter);
    }





    private void LoadPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(
                "theme", MODE_PRIVATE);
        int savedRadioIndex = sharedPreferences.getInt(
                "SAVED_RADIO_BUTTON_INDEX", 0);
        switch (savedRadioIndex){
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                break;
        }
    }
}

