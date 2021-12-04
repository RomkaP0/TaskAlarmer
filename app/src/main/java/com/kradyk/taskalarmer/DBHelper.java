package com.kradyk.taskalarmer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final  int DATABASE_VERSION =1;
    public static final String DATABASE_NAME = "eventDb";
    public static final String TABLE_EVENTS = "events";
    public static final String TABLE_FILLS = "fills";
    public static final String KEY_ID = "_id";
    public static final String KEY_DATA = "data";
    public static final String KEY_TIMEB = "timeb";
    public static final String KEY_TIMEBMILLIS = "timebmillis";
    public static final String KEY_TIMEE = "timee";
    public static final String KEY_TIMENOTIF = "timenotif";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTIONS = "description";
    public static final String KEY_PARAL="paral";
    public static final String KEY_TIMENOTIFMILLIS = "timenotifmillis";
    public static final String KEY_REPEATING = "repeating";
    public static final String KEY_INTERVAL = "interval";
    public static final String KEY_TIMEBINT = "timebint";
    public static final String KEY_TIMEEINT = "timeeint";

    public DBHelper(@Nullable Context context, @Nullable String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ TABLE_EVENTS+"("+ KEY_ID
        +" integer primary key,"+KEY_DATA+" text,"+KEY_TIMENOTIF+" text,"+KEY_TIMEB+" text,"+KEY_TIMEE+" text,"+KEY_TITLE+" text,"+ KEY_DESCRIPTIONS +" text,"+KEY_PARAL+" text,"+KEY_TIMEBMILLIS+" INTEGER,"+KEY_TIMENOTIFMILLIS +" INTEGER,"+KEY_REPEATING + " text," + KEY_INTERVAL+ " text,"+ KEY_TIMEBINT+ " INTEGER,"+ KEY_TIMEEINT+" INTEGER"+")");

        db.execSQL("create table "+ TABLE_FILLS+"("+ KEY_ID
                +" integer primary key,"+KEY_TIMENOTIF+" text,"+KEY_TIMEB+" text,"+KEY_TIMEE+" text,"+KEY_TITLE+" text,"+KEY_TIMENOTIFMILLIS+" INTEGER"+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

}
