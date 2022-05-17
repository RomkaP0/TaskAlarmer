package com.kradyk.taskalarmer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

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
    String[] cat;
    String[] repeate;

    MotionLayout motionLayout;

    final String LOG_TAG = "myLogs";
    DBHelper dbHelper;
    BottomNavigationView bottomNavigationView;


    ArrayList<Item> items = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadPreferences();
        LangLoadPreferences();

        setTitle(R.string.page_title);
        setContentView(R.layout.activity_main);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null){
            Intent st = new Intent("com.example.AuthActivity");
        st.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(st,0);
        }
        motionLayout = findViewById(R.id.motionlt);
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
        cat = getResources().getStringArray(R.array.notif);
        repeate = getResources().getStringArray(R.array.repeating_in);
        motionLayout.getTransition(R.id.rvmotion).setEnabled(true);

    Cursor c = db.query("events as EV inner join categories as CT on EV.posid = CT._id", new String[]{"EV._id", "EV.title", "EV.timeb", "EV.timee", "EV.timenotif","EV.data", "EV.description", "EV.repeating","EV.paral","CT.important"}, "data = ? OR repeating = ? OR repeating = ? OR ((repeating = ? OR repeating = ?) AND interval = ? ) OR ((repeating = ? OR repeating = ?) AND interval = ? ) OR ((repeating = ? OR repeating = ?) AND interval = ? )", new String[]{full, "Daily", "Ежедневно", "Weekly", "Еженедельно", String.valueOf(current.get(Calendar.DAY_OF_WEEK)),"Monthly","Ежемесячно", String.valueOf(current.get(Calendar.DAY_OF_MONTH)), "Yearly", "Ежегодно", current.get(Calendar.DAY_OF_MONTH)+"."+(current.get(Calendar.MONTH)+1)}, null, null, "timeb ASC");
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
            int impIndex = c.getColumnIndex(DBHelper.KEY_IMP);

            do {

                items.add(new Item(c.getString(idIndex), c.getString(titleIndex), c.getString(dataIndex), c.getString(timebIndex), c.getString(timeeIndex), c.getString(timenotifIndex), c.getString(descIndex), c.getString(repeatIndex), c.getString(paralIndex), c.getInt(impIndex)));
            } while (c.moveToNext());
        } else{
            Log.d(LOG_TAG, "0 rows");
        motionLayout.getTransition(R.id.rvmotion).setEnabled(false);}
        c.close();
        buildRV();

    }

    public void onStart() {
        super.onStart();
        setInitialData(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH));

    }

    public void buildRV() {
        adapter = new ItemAdapter(this, items, cat, repeate);
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
    private void setLocale(String language) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(language);
        resources.updateConfiguration(configuration, metrics);
        onConfigurationChanged(configuration);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    private void LangLoadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                "lang", MODE_PRIVATE);
        int langsavedRadioIndex = sharedPreferences.getInt(
                "LAND_SAVED_RADIO_BUTTON_INDEX", 0);
        Log.e("LANG", String.valueOf(langsavedRadioIndex));
        switch (langsavedRadioIndex){
            case 0:
                    setLocale("en");
                break;
            case 1:
                setLocale("ru");
                break;
            default:
                break;
        }
        }

    @Override
    protected void onRestart() {
        recreate();
        super.onRestart();
    }
}

