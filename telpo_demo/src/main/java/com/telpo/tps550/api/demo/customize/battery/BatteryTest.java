package com.telpo.tps550.api.demo.customize.battery;

import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;

import java.util.concurrent.atomic.AtomicInteger;

public class BatteryTest {
    private final BatteryManager batteryManager;

    public BatteryTest(Context context) {
        batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
    }

    // Get the current battery level
    public int getBatteryLevel() {
        AtomicInteger level = new AtomicInteger(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
        return level.get();  // Percentage from 0 to 100
    }

    // Get battery charging status
    public boolean isCharging() {
        int status = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            status = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
        }
        return status == BatteryManager.BATTERY_STATUS_CHARGING;
    }

    // Get estimated battery time remaining (in milliseconds)
    public long getBatteryRemainingTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return batteryManager.computeChargeTimeRemaining();
        }
        return 0;
    }
}
