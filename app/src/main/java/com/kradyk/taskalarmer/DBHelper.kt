package com.kradyk.taskalarmerimport

import android.content.*
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?, name: String?, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "create table " + TABLE_EVENTS + "(" + KEY_ID
                    + " integer primary key," + KEY_DATA + " text," + KEY_TIMENOTIF + " text," + KEY_TIMEB + " text," + KEY_TIMEE + " text," + KEY_TITLE + " text," + KEY_DESCRIPTIONS + " text," + KEY_PARAL + " text," + KEY_TIMEBMILLIS + " INTEGER," + KEY_TIMENOTIFMILLIS + " INTEGER," + KEY_REPEATING + " text," + KEY_INTERVAL + " text," + KEY_TIMEBINT + " INTEGER," + KEY_TIMEEINT + " INTEGER," + KEY_POSID + " INTEGER" + ")"
        )
        db.execSQL(
            "create table " + TABLE_FILLS + "(" + KEY_ID
                    + " integer primary key," + KEY_TIMENOTIF + " text," + KEY_TIMEB + " text," + KEY_TIMEE + " text," + KEY_TITLE + " text," + KEY_TIMENOTIFMILLIS + " INTEGER," + KEY_POSID + " INTEGER" + ")"
        )
        db.execSQL(
            "create table " + TABLE_CATEGORY + "(" + KEY_ID
                    + " integer primary key," + KEY_CAT + " text," + KEY_IMP + " INTEGER" + ")"
        )
        db.execSQL(
            "INSERT INTO " + TABLE_CATEGORY + " (" + KEY_CAT + ", " + KEY_IMP
                    + ") VALUES ('Without', 1);"
        )
        db.execSQL(
            "INSERT INTO " + TABLE_CATEGORY + " (" + KEY_CAT + ", " + KEY_IMP
                    + ") VALUES ('Studying', 2);"
        )
        db.execSQL(
            "INSERT INTO " + TABLE_CATEGORY + " (" + KEY_CAT + ", " + KEY_IMP
                    + ") VALUES ('Working', 2);"
        )
        db.execSQL(
            "INSERT INTO " + TABLE_CATEGORY + " (" + KEY_CAT + ", " + KEY_IMP
                    + ") VALUES ('Relaxing', 0);"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {}

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "eventDb"
        const val TABLE_EVENTS = "events"
        const val TABLE_FILLS = "fills"
        const val TABLE_CATEGORY = "categories"
        const val KEY_ID = "_id"
        const val KEY_DATA = "data"
        const val KEY_TIMEB = "timeb"
        const val KEY_TIMEBMILLIS = "timebmillis"
        const val KEY_TIMEE = "timee"
        const val KEY_TIMENOTIF = "timenotif"
        const val KEY_TITLE = "title"
        const val KEY_DESCRIPTIONS = "description"
        const val KEY_PARAL = "paral"
        const val KEY_TIMENOTIFMILLIS = "timenotifmillis"
        const val KEY_REPEATING = "repeating"
        const val KEY_INTERVAL = "interval"
        const val KEY_TIMEBINT = "timebint"
        const val KEY_TIMEEINT = "timeeint"
        const val KEY_CAT = "cat"
        const val KEY_POSID = "posid"
        const val KEY_IMP = "important"
    }
}