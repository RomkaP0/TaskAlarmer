package com.kradyk.taskalarmer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;


public class AlarmReceiver extends BroadcastReceiver {
    AlarmManager alarmManager;
    String repeating;
    @Override
    public void onReceive(Context context, Intent intent) {


        int NOTIFY_ID = intent.getIntExtra("NOTIFICATION_ID", 0);
        String titleText = intent.getStringExtra("TITLE_TEXT");
        String bigText = intent.getStringExtra("BIG_TEXT");
        Long timenotif = intent.getLongExtra("TIMENOTIF", 0);
        repeating = intent.getStringExtra("REPEATING");

        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, NOTIFY_ID, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder= new NotificationCompat.Builder(context, "OrgNotCll")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(titleText)
                .setContentText(bigText)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{ 0, 400, 200, 400})
                .setContentIntent(resultPendingIntent)
                .setOnlyAlertOnce(false);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (System.currentTimeMillis()-timenotif<=900000)
            notificationManagerCompat.notify(NOTIFY_ID, builder.build());
        if(!(repeating.equals("No")||repeating.equals("Нет"))){
            Intent i = new Intent(context, AlarmReceiver.class);
            i.putExtra("NOTIFICATION_ID", NOTIFY_ID);
            i.putExtra("TITLE_TEXT", titleText);
            i.putExtra("BIG_TEXT", bigText);
            i.putExtra("REPEATING", repeating);
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE) ;
            switch (repeating) {
                case "Daily":
                case "Ежедневно": {
                    Log.d("NotifyLog", "Daily");
                    Long interval = 86400000L;
                    i.putExtra("TIMENOTIF", (timenotif + interval));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFY_ID, i, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (timenotif + interval), pendingIntent);
                    break;
                }
                case "Weekly":
                case "Еженедельно": {
                    Log.d("NotifyLog", "Weekly");
                    Long interval = 604800000L;
                    i.putExtra("TIMENOTIF", (timenotif + interval));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFY_ID, i, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (timenotif + interval), pendingIntent);
                    break;
                }
                case "Monthly":
                case "Ежемесячно": {
                    Calendar calendarNow = Calendar.getInstance();
                    Calendar calendarNext = Calendar.getInstance();
                    calendarNext.set(Calendar.DAY_OF_MONTH, 3);
                    calendarNext.set(Calendar.MONTH, (calendarNow.get(Calendar.MONTH)+1));
                    if (calendarNext.getActualMaximum(Calendar.DAY_OF_MONTH)>=calendarNow.get(Calendar.DAY_OF_MONTH)){
                        Log.d("NotifyLog", "Monthly");
                        Long interval = 86400000L*calendarNow.getActualMaximum(Calendar.DAY_OF_MONTH);
                        i.putExtra("TIMENOTIF", (timenotif + interval));
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFY_ID, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (timenotif + interval), pendingIntent);
                    }else{
                        Log.d("NotifyLog", "MonthlySkip");
                        Long interval = ((86400000L*calendarNow.getActualMaximum(Calendar.DAY_OF_MONTH))+(86400000L*calendarNext.getActualMaximum(Calendar.DAY_OF_MONTH)));
                        i.putExtra("TIMENOTIF", (timenotif + interval));
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFY_ID, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (timenotif + interval), pendingIntent);
                    }
                    break;
                }
                case "Yearly":
                case "Ежегодно":
                    Calendar calendar = Calendar.getInstance();
                    if (calendar.getActualMaximum(Calendar.DAY_OF_YEAR) == 365) {
                        Log.d("NotifyLog", "Yearly365");
                        Long interval = 31536000000L;
                        i.putExtra("TIMENOTIF", (timenotif + interval));
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFY_ID, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (timenotif + interval), pendingIntent);
                    } else {
                        if (calendar.get(Calendar.DAY_OF_MONTH) == 29 && calendar.get(Calendar.MONTH) == Calendar.FEBRUARY) {
                            Log.d("NotifyLog", "29Febr");
                            Long interval = (31622400000L + (31536000000L * 3));
                            i.putExtra("TIMENOTIF", (timenotif + interval));
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFY_ID, i, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (timenotif + interval), pendingIntent);
                        } else {
                            Log.d("NotifyLog", "Yearly366");
                            Long interval = 31622400000L;
                            i.putExtra("TIMENOTIF", (timenotif + interval));
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFY_ID, i, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (timenotif + interval), pendingIntent);

                        }
                    }

                    break;
            }
        }



    }

    }


