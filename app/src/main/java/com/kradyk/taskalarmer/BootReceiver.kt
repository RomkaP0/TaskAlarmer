package com.kradyk.taskalarmer

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.os.Build
import android.util.Log
import com.kradyk.taskalarmerimport.DBHelper

class BootReceiver : BroadcastReceiver() {
    var dbHelper: DBHelper? = null
    var alarmManager: AlarmManager? = null
    var pendingIntent: PendingIntent? = null
    override fun onReceive(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            val notificationChannel = notificationManager.getNotificationChannel("OrgNotCll")
            if (notificationChannel == null) {
                val name: CharSequence = "MyReminderChannel"
                val description = "Channel for Alarm Manager"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel("OrgNotCll", name, importance)
                channel.description = description
                channel.enableVibration(true)
                channel.vibrationPattern = longArrayOf(0, 400, 200, 400)
                notificationManager.createNotificationChannel(channel)
            }
        }
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        dbHelper = DBHelper(context, "String", 1)
        val db = dbHelper!!.writableDatabase
        val c = db.query("events", null, null, null, null, null, "title ASC")
        if (c.moveToFirst()) {
            val idIndex = c.getColumnIndex(DBHelper.KEY_ID)
            val titleIndex = c.getColumnIndex(DBHelper.KEY_TITLE)
            val descIndex = c.getColumnIndex(DBHelper.KEY_DESCRIPTIONS)
            val timebmillisIndex = c.getColumnIndex(DBHelper.KEY_TIMEBMILLIS)
            val timenotifmillisIndex = c.getColumnIndex(DBHelper.KEY_TIMENOTIFMILLIS)
            val repeatingIndex = c.getColumnIndex(DBHelper.KEY_REPEATING)
            do {
                val alarmIntent = Intent(context, AlarmReceiver::class.java)
                alarmIntent.putExtra("NOTIFICATION_ID", c.getString(idIndex).toInt())
                alarmIntent.putExtra("TITLE_TEXT", c.getString(titleIndex))
                alarmIntent.putExtra("BIG_TEXT", c.getString(descIndex))
                alarmIntent.putExtra(
                    "TIMENOTIF",
                    c.getLong(timebmillisIndex) - c.getLong(timenotifmillisIndex)
                )
                alarmIntent.putExtra("REPEATING", c.getString(repeatingIndex))
                pendingIntent = PendingIntent.getBroadcast(
                    context,
                    c.getString(idIndex).toInt(),
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                alarmManager!!.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    c.getLong(timebmillisIndex) - c.getLong(timenotifmillisIndex),
                    pendingIntent
                )
            } while (c.moveToNext())
        } else Log.d("mainLog", "0 rows")
        c.close()
    }
}