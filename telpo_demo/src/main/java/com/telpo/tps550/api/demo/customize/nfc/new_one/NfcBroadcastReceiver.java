package com.telpo.tps550.api.demo.customize.nfc.new_one;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NfcBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("com.telpo.tps550.NFC_DETECTED".equals(action)) {
            // Handle NFC card detection
            Log.d("NFC", "NFC card detected in background.");
        }
    }
}
