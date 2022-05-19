@file:Suppress("LocalVariableName")

package com.kradyk.taskalarmer

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    private var alarmManager: AlarmManager? = null
    private var repeating: String? = null
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, intent: Intent) {
        val NOTIFY_ID = intent.getIntExtra("NOTIFICATION_ID", 0)
        val titleText = intent.getStringExtra("TITLE_TEXT")
        val bigText = intent.getStringExtra("BIG_TEXT")
        val timenotif = intent.getLongExtra("TIMENOTIF", 0)
        repeating = intent.getStringExtra("REPEATING")
        val resultIntent = Intent(context, MainActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(
            context, NOTIFY_ID, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(context, "OrgNotCll")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(titleText)
            .setContentText(bigText)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 400, 200, 400))
            .setContentIntent(resultPendingIntent)
            .setOnlyAlertOnce(false)
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        if (System.currentTimeMillis() - timenotif <= 900000) notificationManagerCompat.notify(
            NOTIFY_ID,
            builder.build()
        )
        if (!(repeating == "No" || repeating == "Нет")) {
            val i = Intent(context, AlarmReceiver::class.java)
            i.putExtra("NOTIFICATION_ID", NOTIFY_ID)
            i.putExtra("TITLE_TEXT", titleText)
            i.putExtra("BIG_TEXT", bigText)
            i.putExtra("REPEATING", repeating)
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            when (repeating) {
                "Daily", "Ежедневно" -> {
                    Log.d("NotifyLog", "Daily")
                    val interval = 86400000L
                    i.putExtra("TIMENOTIF", timenotif + interval)
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        NOTIFY_ID,
                        i,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    alarmManager!!.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timenotif + interval,
                        pendingIntent
                    )
                }
                "Weekly", "Еженедельно" -> {
                    Log.d("NotifyLog", "Weekly")
                    val interval = 604800000L
                    i.putExtra("TIMENOTIF", timenotif + interval)
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        NOTIFY_ID,
                        i,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    alarmManager!!.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timenotif + interval,
                        pendingIntent
                    )
                }
                "Monthly", "Ежемесячно" -> {
                    val calendarNow = Calendar.getInstance()
                    val calendarNext = Calendar.getInstance()
                    calendarNext[Calendar.DAY_OF_MONTH] = 3
                    calendarNext[Calendar.MONTH] = calendarNow[Calendar.MONTH] + 1
                    if (calendarNext.getActualMaximum(Calendar.DAY_OF_MONTH) >= calendarNow[Calendar.DAY_OF_MONTH]) {
                        Log.d("NotifyLog", "Monthly")
                        val interval =
                            86400000L * calendarNow.getActualMaximum(Calendar.DAY_OF_MONTH)
                        i.putExtra("TIMENOTIF", timenotif + interval)
                        val pendingIntent = PendingIntent.getBroadcast(
                            context,
                            NOTIFY_ID,
                            i,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        alarmManager!!.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            timenotif + interval,
                            pendingIntent
                        )
                    } else {
                        Log.d("NotifyLog", "MonthlySkip")
                        val interval =
                            86400000L * calendarNow.getActualMaximum(Calendar.DAY_OF_MONTH) + 86400000L * calendarNext.getActualMaximum(
                                Calendar.DAY_OF_MONTH
                            )
                        i.putExtra("TIMENOTIF", timenotif + interval)
                        val pendingIntent = PendingIntent.getBroadcast(
                            context,
                            NOTIFY_ID,
                            i,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        alarmManager!!.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            timenotif + interval,
                            pendingIntent
                        )
                    }
                }
                "Yearly", "Ежегодно" -> {
                    val calendar = Calendar.getInstance()
                    if (calendar.getActualMaximum(Calendar.DAY_OF_YEAR) == 365) {
                        Log.d("NotifyLog", "Yearly365")
                        val interval = 31536000000L
                        i.putExtra("TIMENOTIF", timenotif + interval)
                        val pendingIntent = PendingIntent.getBroadcast(
                            context,
                            NOTIFY_ID,
                            i,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        alarmManager!!.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            timenotif + interval,
                            pendingIntent
                        )
                    } else {
                        if (calendar[Calendar.DAY_OF_MONTH] == 29 && calendar[Calendar.MONTH] == Calendar.FEBRUARY) {
                            Log.d("NotifyLog", "29Febr")
                            val interval = 31622400000L + 31536000000L * 3
                            i.putExtra("TIMENOTIF", timenotif + interval)
                            val pendingIntent = PendingIntent.getBroadcast(
                                context,
                                NOTIFY_ID,
                                i,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                            alarmManager!!.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                timenotif + interval,
                                pendingIntent
                            )
                        } else {
                            Log.d("NotifyLog", "Yearly366")
                            val interval = 31622400000L
                            i.putExtra("TIMENOTIF", timenotif + interval)
                            val pendingIntent = PendingIntent.getBroadcast(
                                context,
                                NOTIFY_ID,
                                i,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                            alarmManager!!.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                timenotif + interval,
                                pendingIntent
                            )
                        }
                    }
                }
            }
        }
    }
}