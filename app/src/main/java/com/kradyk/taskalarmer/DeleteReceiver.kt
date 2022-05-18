package com.kradyk.taskalarmer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class DeleteReceiver : BroadcastReceiver() {
    var alarmManager: AlarmManager? = null
    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, intent.getIntExtra("DelID", 0), i, 0)
        if (alarmManager == null) {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
        alarmManager!!.cancel(pendingIntent)
        Toast.makeText(context, "Alarm cancelled Successfully", Toast.LENGTH_SHORT).show()
    }
}