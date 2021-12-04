package com.kradyk.taskalarmer;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    DBHelper dbHelper;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel("OrgNotCll");
            if(notificationChannel == null){
            CharSequence name ="MyReminderChannel";
            String description = "Channel for Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("OrgNotCll", name, importance);

            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[] { 0, 400, 200, 400 });

            notificationManager.createNotificationChannel(channel);
        }}
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        dbHelper = new DBHelper(context, "String", 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("events", null, null, null, null, null, "title ASC");
        if (c.moveToFirst()) {

            int idIndex = c.getColumnIndex(DBHelper.KEY_ID);

            int titleIndex = c.getColumnIndex(DBHelper.KEY_TITLE);
            int descIndex = c.getColumnIndex(DBHelper.KEY_DESCRIPTIONS);

            int timebmillisIndex = c.getColumnIndex(DBHelper.KEY_TIMEBMILLIS);
            int timenotifmillisIndex = c.getColumnIndex(DBHelper.KEY_TIMENOTIFMILLIS);
            int repeatingIndex = c.getColumnIndex(DBHelper.KEY_REPEATING);

            do {
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                alarmIntent.putExtra("NOTIFICATION_ID", Integer.parseInt(c.getString(idIndex)));
                alarmIntent.putExtra("TITLE_TEXT", c.getString(titleIndex));
                alarmIntent.putExtra("BIG_TEXT", c.getString(descIndex));
                alarmIntent.putExtra("TIMENOTIF", (c.getLong(timebmillisIndex)-c.getLong(timenotifmillisIndex)));
                alarmIntent.putExtra("REPEATING", c.getString(repeatingIndex));
                pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(c.getString(idIndex)), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (c.getLong(timebmillisIndex)-c.getLong(timenotifmillisIndex)), pendingIntent);

            } while (c.moveToNext());
        } else
            Log.d("mainLog", "0 rows");
        c.close();

}
    }
