package com.kradyk.taskalarmer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DeleteReceiver extends BroadcastReceiver {
    AlarmManager alarmManager;
    @Override
    public void onReceive(Context context, Intent intent) {

            Intent i = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, intent.getIntExtra("DelID",0), i, 0);
            if(alarmManager == null){
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            }
            alarmManager.cancel(pendingIntent);
            Toast.makeText(context, "Alarm cancelled Successfully", Toast.LENGTH_SHORT).show();

    }

}
