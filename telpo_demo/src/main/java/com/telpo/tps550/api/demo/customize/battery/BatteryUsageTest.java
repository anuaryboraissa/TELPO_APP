package com.telpo.tps550.api.demo.customize.battery;

import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import androidx.annotation.RequiresApi;

public class BatteryUsageTest {
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void getAppEnergyConsumption(Context context) {
//        BatteryStats.Uid uidStats = BatteryStats.getUidStats(context.getPackageName());
//        long powerUsageMah = uidStats.getPowerUseMah();  // Power in mAh
//        System.out.println("App power consumption: " + powerUsageMah + "mAh");
    }
}
