package com.telpo.tps550.api.demo.customize.battery;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class WakeUpTest {
    private final AlarmManager alarmManager;
    private final PendingIntent pendingIntent;

    public WakeUpTest(Context context) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    // Schedule an alarm to wake the device after 5 seconds
    @SuppressLint("MissingPermission")
    public void scheduleWakeUp() {
        long triggerAtMillis = SystemClock.elapsedRealtime() + 5000;  // 5 seconds
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
    }

    public void cancelAlarm() {
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Logic to execute upon wake-up (e.g., log a message)
            System.out.println("Device woke up from Alarm!");
        }
    }
}

