package com.kradyk.taskalarmer;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {

    public Button button1;
    public Button button2;
    public EditText editText2;
    public AutoCompleteTextView autoCompleteTextView;
    public AutoCompleteTextView autoCompleteTextView1;
    public AutoCompleteTextView autoCompleteTextView2;
    public AutoCompleteTextView autoCompleteTextView3;
    int hour=0;
    int minute=0;
    ArrayList cat = new ArrayList<String>();
    ArrayList catid = new ArrayList<Integer>();

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    long timeNotifMillis = 0;
    int c=0;

    ScrollView scrollView;
    FillItem fillItem;
    String data;

    ArrayList<FillItem> fillItems = new ArrayList<>();

    Calendar cal;
    String lastId;
    String switchvalue = "0";
    int curID=-1;

    int timebint;
    int timeeint;


    MaterialTimePicker builder = new MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText("Укажите время")
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .build();
    MaterialTimePicker builderend = new MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText("Укажите время")
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .build();

    MaterialDatePicker.Builder<Long> builderdate;
    MaterialDatePicker<Long> datePicker;



    DBHelper dbHelper;
    SQLiteDatabase database;

    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_activity);

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView1);

        MaterialToolbar materialToolbar = findViewById(R.id.topAppBarD1);
        if (autoCompleteTextView.getHint().toString().equals("Название"))
            materialToolbar.setTitle("Дата : "+getIntent().getExtras().getString("date","-1"));
        else
            materialToolbar.setTitle("Date : "+getIntent().getExtras().getString("date","-1"));

        data = getIntent().getExtras().getString("date","-1");

        setListFills();
        createNotificationChannel();
        Calendar cal = Calendar.getInstance();

        SwitchCompat switcher = findViewById(R.id.paralsw);
        switcher.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                switchvalue = "1";
            }
            else
                switchvalue = "0";
        });

        editText2 = findViewById(R.id.edtxt2);


        String[] notifybef = getResources().getStringArray(R.array.notif);
        autoCompleteTextView1 = findViewById(R.id.notifybefore);
        autoCompleteTextView1.setText(notifybef[0]);
        autoCompleteTextView1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notifybef));

        autoCompleteTextView.setThreshold(2);
        FillAdapter fillAdapter = new FillAdapter(this,R.layout.autolist, fillItems);
        autoCompleteTextView.setAdapter(fillAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fillItem =(FillItem) adapterView.getItemAtPosition(i);

                button1.setText(fillItem.getTimeb());
                button2.setText(fillItem.getTimee());
                autoCompleteTextView1.setText(fillItem.getTimenotif(), false);
            }
        });



        String[] repeating = getResources().getStringArray(R.array.repeating_in);
        autoCompleteTextView2 = findViewById(R.id.repeating);
        autoCompleteTextView2.setText(repeating[0]);
        autoCompleteTextView2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, repeating ));

        dbHelper = new DBHelper(this, "String", 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("categories", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int catIndex = c.getColumnIndex(DBHelper.KEY_CAT);

            do {
                cat.add(c.getString(catIndex));
                Log.d("mainLog", c.getString(catIndex));
            } while (c.moveToNext());
        } else
            Log.d("mainLog", "0 rows");
        c.close();
        cat.add("Добавить");
        dbHelper= new DBHelper(this,"String",1);
        database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        autoCompleteTextView3 = findViewById(R.id.category);
        autoCompleteTextView3.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cat));
        autoCompleteTextView3.setText("Without", false);
        autoCompleteTextView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getItemAtPosition(i).equals("Добавить")){
                    EditText inputEditTextField = new EditText(view.getContext());
                    AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                            .setTitle("Введите название категории")
                            .setView(inputEditTextField)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    int c=0;
                                    for(Object j:cat){
                                        String k = (String) j;
                                        if ( k.toLowerCase().trim().equals(inputEditTextField.getText().toString().toLowerCase().trim()))
                                            c+=1;
                                    }
                                    if (c==0){
                                    cat.add(cat.size()-1,inputEditTextField.getText().toString());
                                    autoCompleteTextView3.setText(cat.get(cat.size()-2).toString(), false);
                                    contentValues.clear();
                                    contentValues.put(DBHelper.KEY_IMP, 1);
                                    contentValues.put(DBHelper.KEY_CAT, cat.get(cat.size()-2).toString());
                                    database.insert(DBHelper.TABLE_CATEGORY, null, contentValues);}
                                    else{
                                        Toast.makeText(getApplicationContext(), "Already exist", Toast.LENGTH_SHORT).show();
                                        autoCompleteTextView3.setText("Without", false);
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    autoCompleteTextView3.setText("Without", false);
                                }
                            })
                            .create();
                    dialog.show();
                }
            }
        });


        scrollView = findViewById(R.id.scrollviewd1);


        builderdate = MaterialDatePicker.Builder.datePicker();
        builderdate.setTitleText("Укажите дату").setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .setTheme(R.style.Theme_App_Calendar);
        datePicker = builderdate.build();
        button1 = findViewById(R.id.btn1);
        button2 = findViewById(R.id.btn2);
        Button button3 = findViewById(R.id.imb3);



        if(getIntent().getExtras().getString("from").equals("rv")){
            autoCompleteTextView.setText(getIntent().getExtras().getString("title"));
            button1.setText(getIntent().getExtras().getString("timeb"));
            button2.setText(getIntent().getExtras().getString("timee"));
            editText2.setText(getIntent().getExtras().getString("desc"));
            autoCompleteTextView1.setText(getIntent().getExtras().getString("notify"),false);
            autoCompleteTextView2.setText(getIntent().getExtras().getString("repeat"), false);
            if (getIntent().getExtras().getString("paral").equals("1"))
                switcher.setChecked(true);
        }

        SwitchCompat switchCompat = findViewById(R.id.switch1);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {

                    button1.setClickable(false);
                    button2.setClickable(false);
                    button1.setText("00:00");
                    timebint = 0;
                    button2.setText("23:59");
                    timeeint = 2359;

                }else {
                    button1.setClickable(true);
                    button2.setClickable(true);
                }
            }
        });


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show(getSupportFragmentManager(), "time");
                builder.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hour = builder.getHour();
                        minute = builder.getMinute();
                        if (hour<10)
                            button1.setText("0"+hour+":");
                        else
                            button1.setText(hour+":");
                        timebint = hour*100;
                        if (minute<10)
                            button1.setText(button1.getText().toString()+"0"+minute);
                        else
                            button1.setText(button1.getText().toString()+minute);
                        timebint+=minute;
                        timeeint = timebint;
                        button2.setText(button1.getText().toString());
                    }
                });
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(button1.getText().toString().equals("Время начала")){
                    Toast.makeText(CreateEventActivity.this,"Укажите начальное время", Toast.LENGTH_SHORT).show();
                }else{
                builderend.show(getSupportFragmentManager(),"time");
                builderend.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (builderend.getHour() > hour) {
                            if (builderend.getHour()<10)
                                button2.setText("0"+builderend.getHour()+":");
                            else
                                button2.setText(builderend.getHour()+":");
                            timeeint=builderend.getHour()*100;
                            if (builderend.getMinute()<10)
                                button2.setText(button2.getText().toString()+"0"+builderend.getMinute());
                            else
                                button2.setText(button2.getText().toString()+builderend.getMinute());
                            timeeint+=builderend.getMinute();
                        } else if (builderend.getHour() == hour) {
                            if (builderend.getMinute() >= minute) {
                                if (builderend.getHour()<10)
                                    button2.setText("0"+builderend.getHour()+":");
                                else
                                    button2.setText(builderend.getHour()+":");
                                timeeint=builderend.getHour()*100;
                                if (builderend.getMinute()<10)
                                    button2.setText(button2.getText().toString()+"0"+builderend.getMinute());
                                else
                                    button2.setText(button2.getText().toString()+builderend.getMinute());
                                timeeint+=builderend.getMinute();
                            }
                        }
                    } });
            }}
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.show(getSupportFragmentManager(), "date");
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        cal.setTimeInMillis(selection);
                        data = "";
                        if ((cal.get(Calendar.DAY_OF_MONTH)) < 10)
                            data += "0" + cal.get(Calendar.DAY_OF_MONTH) + ".";
                        else
                            data += (cal.get(Calendar.DAY_OF_MONTH)) + ".";
                        if ((cal.get(Calendar.MONTH) + 1) < 10)
                            data += "0" + (cal.get(Calendar.MONTH) + 1) + ".";
                        else
                            data += (cal.get(Calendar.MONTH) + 1) + ".";
                        data += cal.get(Calendar.YEAR);
                        materialToolbar.setTitle("Дата : "+ data);
                    }
                });
            }
        });

        materialToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.check) {
                        if ((!autoCompleteTextView.getText().toString().equals("")) && (!button1.getText().toString().equals("Начало")) && (!button2.getText().toString().equals("Окончание"))&& (!button1.getText().toString().equals("Initial time"))&& (!button2.getText().toString().equals("End time"))) {
                            String [] caldate = data.split("\\.");
                            String [] caltime = button1.getText().toString().split(":");
                            cal.set(Integer.parseInt(caldate[2]), Integer.parseInt(caldate[1])-1,Integer.parseInt(caldate[0]),Integer.parseInt(caltime[0]),Integer.parseInt(caltime[1]),0);
                            if (switchvalue.equals("0")) {
                                Cursor cursor = database.query("events", null, "(data = ? OR repeating = ? OR repeating = ? OR ((repeating = ? OR repeating = ?) AND interval = ? ) OR ((repeating = ? OR repeating = ?) AND interval = ? ) OR ((repeating = ? OR repeating = ?) AND interval = ? )) AND paral = ? AND ((timebint <= ? AND timeeint >= ?) OR (timebint <= ? AND timeeint >= ?)OR (timebint >= ? AND timeeint <= ?)) ", new String[]{data, "Daily", "Ежедневно", "Weekly", "Еженедельно", String.valueOf(cal.get(Calendar.DAY_OF_WEEK)),"Monthly","Ежемесячно", caldate[0], "Yearly", "Ежегодно", caldate[0]+"."+caldate[1], "1", String.valueOf(timebint), String.valueOf(timebint), String.valueOf(timeeint), String.valueOf(timeeint), String.valueOf(timebint), String.valueOf(timeeint)}, null, null, "timeb ASC");
                                if (cursor.moveToFirst()) {

                                    if(getIntent().getExtras().getString("from").equals("rv")){
                                        curID = Integer.parseInt(getIntent().getExtras().getString("id"));
                                    }
                                    do {
                                        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                                        System.out.println(Integer.parseInt(cursor.getString(idIndex)));
                                        if (curID== Integer.parseInt(cursor.getString(idIndex)))
                                            continue;
                                        count+=1;
                                    } while (cursor.moveToNext());
                                } else {
                                    Log.d("Flog", "No");
                                }
                                cursor.close();
                            }else {
                                Cursor cursor = database.query("events", null, "(data = ? OR repeating = ? OR repeating = ? OR ((repeating = ? OR repeating = ?) AND interval = ? ) OR ((repeating = ? OR repeating = ?) AND interval = ? ) OR ((repeating = ? OR repeating = ?) AND interval = ? )) AND ((timebint <= ? AND timeeint >= ?) OR (timebint <= ? AND timeeint >= ?)OR (timebint >= ? AND timeeint <= ?)) ", new String[]{data,"Daily", "Ежедневно", "Weekly", "Еженедельно", String.valueOf(cal.get(Calendar.DAY_OF_WEEK)),"Monthly","Ежемесячно", String.valueOf(cal.get(Calendar.DAY_OF_MONTH)), "Yearly", "Ежегодно", cal.get(Calendar.DAY_OF_MONTH)+"."+(cal.get(Calendar.MONTH)+1), String.valueOf(timebint), String.valueOf(timebint), String.valueOf(timeeint), String.valueOf(timeeint), String.valueOf(timebint), String.valueOf(timeeint)}, null, null, "timeb ASC");
                                if (cursor.moveToFirst()) {
                                    if(getIntent().getExtras().getString("from").equals("rv")){
                                        curID = Integer.parseInt(getIntent().getExtras().getString("id"));}
                                    do {
                                        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                                        System.out.println(Integer.parseInt(cursor.getString(idIndex)));
                                        if (curID== Integer.parseInt(cursor.getString(idIndex)))
                                            continue;
                                        count+=1;
                                    } while (cursor.moveToNext());
                                } else {
                                    Log.d("Flog", "No");
                                }
                                cursor.close();
                            }
                            if(getIntent().getExtras().getString("from").equals("main")){

                                if (count==0){
                                try {
                                if(!fillItem.getTitle().equals(autoCompleteTextView.getText().toString())||
                                        !fillItem.getTimeb().equals(button1.getText().toString())||
                                        !fillItem.getTimee().equals(button2.getText().toString())){
                                    contentValues.clear();
                                    contentValues.put(DBHelper.KEY_TITLE, autoCompleteTextView.getText().toString());
                                    contentValues.put(DBHelper.KEY_TIMEB, button1.getText().toString());
                                    contentValues.put(DBHelper.KEY_TIMEE, button2.getText().toString());
                                    contentValues.put(DBHelper.KEY_TIMENOTIF, autoCompleteTextView1.getText().toString());
                                    setTimeNotifMillis();
                                    contentValues.put(DBHelper.KEY_TIMENOTIFMILLIS, timeNotifMillis);

                                    catid.clear();
                                    catid.add(0);
                                    Cursor c = db.query("categories", null, null, null, null, null, null);
                                    if (c.moveToFirst()) {
                                        int cat_ID= c.getColumnIndex(DBHelper.KEY_ID);
                                        int catIndex = c.getColumnIndex(DBHelper.KEY_CAT);

                                        do {
                                            catid.add(cat.indexOf(c.getString(catIndex)), Integer.parseInt(c.getString(cat_ID)));
                                        } while (c.moveToNext());
                                    } else
                                        Log.d("mainLog", "0 rows");
                                    c.close();

                                    database.insert(DBHelper.TABLE_FILLS,null,contentValues);
                                    Log.d("ExcFillInsertLog", "Exception");
                                    contentValues.put(DBHelper.KEY_TIMEBMILLIS, cal.getTimeInMillis());
                                    contentValues.put(DBHelper.KEY_PARAL, switchvalue );
                                    contentValues.put(DBHelper.KEY_DATA, data);
                                    contentValues.put(DBHelper.KEY_DESCRIPTIONS, editText2.getText().toString());
                                    contentValues.put(DBHelper.KEY_TIMEBINT, timebint);
                                    contentValues.put(DBHelper.KEY_TIMEEINT, timeeint);

                                    contentValues.put(DBHelper.KEY_REPEATING, autoCompleteTextView2.getText().toString());
                                    if (autoCompleteTextView2.getText().toString().equals("Еженедельно")||autoCompleteTextView2.getText().toString().equals("Weekly"))
                                        contentValues.put(DBHelper.KEY_INTERVAL, cal.get(Calendar.DAY_OF_WEEK));
                                    else if (autoCompleteTextView2.getText().toString().equals("Ежемесячно")||autoCompleteTextView2.getText().toString().equals("Monthly"))
                                        contentValues.put(DBHelper.KEY_INTERVAL, cal.get(Calendar.DAY_OF_MONTH));
                                    else if (autoCompleteTextView2.getText().toString().equals("Ежегодно")||autoCompleteTextView2.getText().toString().equals("Yearly"))
                                        contentValues.put(DBHelper.KEY_INTERVAL, cal.get(Calendar.DAY_OF_MONTH)+"."+(cal.get(Calendar.MONTH)+1));
                                    contentValues.put(DBHelper.KEY_POSID, (Integer) catid.get(cat.indexOf(autoCompleteTextView3.getText().toString())));



                                    database.insert(DBHelper.TABLE_EVENTS, null, contentValues);
                                    setAlarm();
                                }
                                else{
                                    contentValues.clear();
                                    contentValues.put(DBHelper.KEY_TITLE, autoCompleteTextView.getText().toString());
                                    contentValues.put(DBHelper.KEY_TIMEB, button1.getText().toString());
                                    contentValues.put(DBHelper.KEY_TIMEE, button2.getText().toString());
                                    contentValues.put(DBHelper.KEY_TIMENOTIF, autoCompleteTextView1.getText().toString());
                                    setTimeNotifMillis();
                                    contentValues.put(DBHelper.KEY_TIMENOTIFMILLIS, timeNotifMillis);

                                    catid.clear();
                                    catid.add(0);
                                    Cursor c = db.query("categories", null, null, null, null, null, null);
                                    if (c.moveToFirst()) {
                                        int cat_ID= c.getColumnIndex(DBHelper.KEY_ID);
                                        int catIndex = c.getColumnIndex(DBHelper.KEY_CAT);

                                        do {
                                            catid.add(cat.indexOf(c.getString(catIndex)), Integer.parseInt(c.getString(cat_ID)));
                                        } while (c.moveToNext());
                                    } else
                                        Log.d("mainLog", "0 rows");
                                    c.close();
                                    contentValues.put(DBHelper.KEY_POSID, (Integer) catid.get(cat.indexOf(autoCompleteTextView3.getText().toString())));


                                    contentValues.put(DBHelper.KEY_DATA, data);
                                    contentValues.put(DBHelper.KEY_DESCRIPTIONS, editText2.getText().toString());
                                    contentValues.put(DBHelper.KEY_PARAL, switchvalue );
                                    contentValues.put(DBHelper.KEY_TIMEBMILLIS, cal.getTimeInMillis());
                                    contentValues.put(DBHelper.KEY_TIMEBINT, timebint);
                                    contentValues.put(DBHelper.KEY_TIMEEINT, timeeint);

                                    contentValues.put(DBHelper.KEY_REPEATING, autoCompleteTextView2.getText().toString());
                                    if (autoCompleteTextView2.getText().toString().equals("Еженедельно")||autoCompleteTextView2.getText().toString().equals("Weekly"))
                                        contentValues.put(DBHelper.KEY_INTERVAL, cal.get(Calendar.DAY_OF_WEEK));
                                    else if (autoCompleteTextView2.getText().toString().equals("Ежемесячно")||autoCompleteTextView2.getText().toString().equals("Monthly"))
                                        contentValues.put(DBHelper.KEY_INTERVAL, cal.get(Calendar.DAY_OF_MONTH));
                                    else if (autoCompleteTextView2.getText().toString().equals("Ежегодно")||autoCompleteTextView2.getText().toString().equals("Yearly"))
                                        contentValues.put(DBHelper.KEY_INTERVAL, cal.get(Calendar.DAY_OF_MONTH)+"."+(cal.get(Calendar.MONTH)+1));


                                    database.insert(DBHelper.TABLE_EVENTS, null, contentValues);
                                    setAlarm();
                                }
                                Log.d("FillInsertLog", "fill");
                            }catch (NullPointerException e){
                                contentValues.clear();
                                contentValues.put(DBHelper.KEY_TITLE, autoCompleteTextView.getText().toString());
                                contentValues.put(DBHelper.KEY_TIMEB, button1.getText().toString());
                                contentValues.put(DBHelper.KEY_TIMEE, button2.getText().toString());
                                contentValues.put(DBHelper.KEY_TIMENOTIF, autoCompleteTextView1.getText().toString());
                                setTimeNotifMillis();
                                contentValues.put(DBHelper.KEY_TIMENOTIFMILLIS, timeNotifMillis);
                                    catid.clear();
                                    Cursor c = db.query("categories", null, null, null, null, null, null);
                                    if (c.moveToFirst()) {
                                        int cat_ID= c.getColumnIndex(DBHelper.KEY_ID);
                                        int catIndex = c.getColumnIndex(DBHelper.KEY_CAT);

                                        do {
                                            catid.add(cat.indexOf(c.getString(catIndex)), Integer.parseInt(c.getString(cat_ID)));
                                        } while (c.moveToNext());
                                    } else
                                        Log.d("mainLog", "0 rows");
                                    c.close();


                                    database.insert(DBHelper.TABLE_FILLS,null,contentValues);
                                Log.d("ExcFillInsertLog", "Exception");
                                    int find = (int) catid.get(cat.indexOf(autoCompleteTextView3.getText().toString()));

                                    contentValues.put(DBHelper.KEY_POSID, find);

                                contentValues.put(DBHelper.KEY_DATA, data);
                                contentValues.put(DBHelper.KEY_PARAL, switchvalue );
                                contentValues.put(DBHelper.KEY_DESCRIPTIONS, editText2.getText().toString());
                                contentValues.put(DBHelper.KEY_TIMEBMILLIS, cal.getTimeInMillis());
                                    contentValues.put(DBHelper.KEY_TIMEBINT, timebint);
                                    contentValues.put(DBHelper.KEY_TIMEEINT, timeeint);

                                    contentValues.put(DBHelper.KEY_REPEATING, autoCompleteTextView2.getText().toString());
                                    if (autoCompleteTextView2.getText().toString().equals("Еженедельно")||autoCompleteTextView2.getText().toString().equals("Weekly"))
                                        contentValues.put(DBHelper.KEY_INTERVAL, cal.get(Calendar.DAY_OF_WEEK));
                                    else if (autoCompleteTextView2.getText().toString().equals("Ежемесячно")||autoCompleteTextView2.getText().toString().equals("Monthly"))
                                        contentValues.put(DBHelper.KEY_INTERVAL, cal.get(Calendar.DAY_OF_MONTH));
                                    else if (autoCompleteTextView2.getText().toString().equals("Ежегодно")||autoCompleteTextView2.getText().toString().equals("Yearly"))
                                        contentValues.put(DBHelper.KEY_INTERVAL, cal.get(Calendar.DAY_OF_MONTH)+"."+(cal.get(Calendar.MONTH)+1));


                                    database.insert(DBHelper.TABLE_EVENTS, null, contentValues);
                                setAlarm();
                            }


                                finish();}
                            else {
                                count=0;
                                Toast.makeText(getBaseContext(),"Указанное время занято", Toast.LENGTH_SHORT).show();
                            }
                            }else{
                                if (count == 0) {
                                    contentValues.clear();
                                    contentValues.put(DBHelper.KEY_TITLE, autoCompleteTextView.getText().toString());
                                    contentValues.put(DBHelper.KEY_TIMEB, button1.getText().toString());
                                    contentValues.put(DBHelper.KEY_TIMEE, button2.getText().toString());
                                    contentValues.put(DBHelper.KEY_DATA, data);
                                    contentValues.put(DBHelper.KEY_TIMENOTIF, autoCompleteTextView1.getText().toString());

                                    catid.clear();
                                    catid.add(0);
                                    Cursor c = db.query("categories", null, null, null, null, null, null);
                                    if (c.moveToFirst()) {
                                        int cat_ID= c.getColumnIndex(DBHelper.KEY_ID);
                                        int catIndex = c.getColumnIndex(DBHelper.KEY_CAT);

                                        do {
                                            catid.add(cat.indexOf(c.getString(catIndex)), Integer.parseInt(c.getString(cat_ID)));
                                        } while (c.moveToNext());
                                    } else
                                        Log.d("mainLog", "0 rows");
                                    c.close();
                                        contentValues.put(DBHelper.KEY_POSID, (Integer) catid.get(cat.indexOf(autoCompleteTextView3.getText().toString())));


                                    setTimeNotifMillis();
                                    contentValues.put(DBHelper.KEY_TIMENOTIFMILLIS, timeNotifMillis);
                                    contentValues.put(DBHelper.KEY_DESCRIPTIONS, editText2.getText().toString());
                                    contentValues.put(DBHelper.KEY_PARAL, switchvalue);
                                    contentValues.put(DBHelper.KEY_TIMEBMILLIS, cal.getTimeInMillis());
                                    contentValues.put(DBHelper.KEY_REPEATING, autoCompleteTextView2.getText().toString());
                                    contentValues.put(DBHelper.KEY_TIMEBINT, timebint);
                                    contentValues.put(DBHelper.KEY_TIMEEINT, timeeint);
                                    if (autoCompleteTextView2.getText().toString().equals("Нет")||autoCompleteTextView2.getText().toString().equals("No"))
                                        contentValues.put(DBHelper.KEY_INTERVAL, (String) null);
                                    else if (autoCompleteTextView2.getText().toString().equals("Ежедневно")||autoCompleteTextView2.getText().toString().equals("Daily"))
                                        contentValues.put(DBHelper.KEY_INTERVAL, (String) null);
                                    else if (autoCompleteTextView2.getText().toString().equals("Еженедельно")||autoCompleteTextView2.getText().toString().equals("Weekly"))
                                        contentValues.put(DBHelper.KEY_INTERVAL, cal.get(Calendar.DAY_OF_WEEK));
                                    else if (autoCompleteTextView2.getText().toString().equals("Ежемесячно")||autoCompleteTextView2.getText().toString().equals("Monthly"))
                                        contentValues.put(DBHelper.KEY_INTERVAL, cal.get(Calendar.DAY_OF_MONTH));
                                    else if (autoCompleteTextView2.getText().toString().equals("Ежегодно")||autoCompleteTextView2.getText().toString().equals("Yearly"))
                                        contentValues.put(DBHelper.KEY_INTERVAL, cal.get(Calendar.DAY_OF_MONTH)+"."+(cal.get(Calendar.MONTH)+1));
                                    database.update(DBHelper.TABLE_EVENTS, contentValues, "_id = ?", new String[]{getIntent().getExtras().getString("id")});
                                    setAlarm();
                                    finish();
                                }
                            else{
                                    count=0;
                                    Toast.makeText(getBaseContext(),"Указанное время занято", Toast.LENGTH_SHORT).show();
                            }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Введите недостающие данные", Toast.LENGTH_SHORT).show();
                        }
                    }
                return false;
            }
        });
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
    private void setAlarm() {
        String [] caldate = data.split("\\.");
        String [] caltime = button1.getText().toString().split(":");
        cal = Calendar.getInstance();
        cal.set(Integer.parseInt(caldate[2]), Integer.parseInt(caldate[1])-1,Integer.parseInt(caldate[0]),Integer.parseInt(caltime[0]),Integer.parseInt(caltime[1]),0);

        if(getIntent().getExtras().getString("from").equals("main")) {
            Cursor cursor = database.query(DBHelper.TABLE_EVENTS, new String[]{"_id"}, null, null, null, null, "_id DESC", "1");
            if (cursor != null && cursor.moveToFirst()) {
                lastId = cursor.getString(0);
            }
            cursor.close();
        }else {
            lastId = getIntent().getExtras().getString("id");
        }
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("NOTIFICATION_ID", Integer.parseInt(lastId));
        intent.putExtra("TITLE_TEXT", autoCompleteTextView.getText().toString());
        intent.putExtra("BIG_TEXT", editText2.getText().toString());
        intent.putExtra("TIMENOTIF", (cal.getTimeInMillis()-timeNotifMillis));
        intent.putExtra("REPEATING", autoCompleteTextView2.getText().toString());
        pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(lastId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (cal.getTimeInMillis()-timeNotifMillis), pendingIntent);


    }
    private void createNotificationChannel() {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name ="MyReminderChannel";
            String description = "Channel for Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("OrgNotCll", name, importance);

            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[] { 0, 400, 200, 400 });

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void setListFills() {
        fillItems.clear();
        dbHelper= new DBHelper(this,"String",1);
        database = dbHelper.getWritableDatabase();
        Cursor c = database.query("fills", new String[]{"_id", "title", "timeb", "timee", "timenotif"}, null, null, null, null, "title ASC");
        if (c.moveToFirst()) {


            int idIndex = c.getColumnIndex(DBHelper.KEY_ID);

            int titleIndex = c.getColumnIndex(DBHelper.KEY_TITLE);

            int timebIndex = c.getColumnIndex(DBHelper.KEY_TIMEB);
            int timeeIndex = c.getColumnIndex(DBHelper.KEY_TIMEE);
            int timenotifIndex = c.getColumnIndex(DBHelper.KEY_TIMENOTIF);

            do {

                fillItems.add(new FillItem(c.getString(idIndex), c.getString(titleIndex), c.getString(timebIndex), c.getString(timeeIndex), c.getString(timenotifIndex)));
            } while (c.moveToNext());
        } else
            Log.d("d1Log", "0 rows");
        c.close();

    }

    public void setTimeNotifMillis(){
        if (autoCompleteTextView1.getText().toString().equals("At moment")||autoCompleteTextView1.getText().toString().equals("В момент"))
            timeNotifMillis = 0;
        else if(autoCompleteTextView1.getText().toString().equals("5 minutes")||autoCompleteTextView1.getText().toString().equals("5 минут"))
            timeNotifMillis = 300000;
        else if(autoCompleteTextView1.getText().toString().equals("10 minutes")||autoCompleteTextView1.getText().toString().equals("10 минут"))
            timeNotifMillis = 600000;
        else if(autoCompleteTextView1.getText().toString().equals("30 minutes")||autoCompleteTextView1.getText().toString().equals("30 минут"))
            timeNotifMillis = 1800000;
        else if(autoCompleteTextView1.getText().toString().equals("1 hour")||autoCompleteTextView1.getText().toString().equals("1 час"))
            timeNotifMillis = 3600000;
        else if(autoCompleteTextView1.getText().toString().equals("6 hours")||autoCompleteTextView1.getText().toString().equals("6 часов"))
            timeNotifMillis = 21600000;
        else if(autoCompleteTextView1.getText().toString().equals("12 hours")||autoCompleteTextView1.getText().toString().equals("12 часов"))
            timeNotifMillis = 43200000;
        else if(autoCompleteTextView1.getText().toString().equals("24 hours")||autoCompleteTextView1.getText().toString().equals("24 часа"))
            timeNotifMillis = 86400000;
    }
}
