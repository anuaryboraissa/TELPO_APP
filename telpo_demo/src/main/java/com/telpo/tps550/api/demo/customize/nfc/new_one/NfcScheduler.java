package com.telpo.tps550.api.demo.customize.nfc.new_one;

import android.content.Context;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class NfcScheduler {
    public static void scheduleNfcWork(Context context) {
        PeriodicWorkRequest nfcWorkRequest = new PeriodicWorkRequest.Builder(
                NfcWorker.class, 15, TimeUnit.MINUTES) // Minimum interval is 15 minutes
                .build();

        WorkManager.getInstance(context).enqueue(nfcWorkRequest);
    }
}
