package com.telpo.tps550.api.demo.customize.battery;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.provider.Settings;

public class PowerSaveTest {
    private final PowerManager powerManager;

    public PowerSaveTest(Context context) {
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    // Check if power-saving mode is enabled
    public boolean isPowerSaveModeEnabled() {
        return powerManager.isPowerSaveMode();
    }

    // Force device to disable battery optimizations (requires permission)
    public void disableBatteryOptimization(Context context) {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        }
        context.startActivity(intent);
    }
}
