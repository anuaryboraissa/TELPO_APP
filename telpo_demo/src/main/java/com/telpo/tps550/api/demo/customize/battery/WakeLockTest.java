package com.telpo.tps550.api.demo.customize.battery;

import android.content.Context;
import android.os.PowerManager;

public class WakeLockTest {
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    public WakeLockTest(Context context) {
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    // Acquire WakeLock to prevent device from sleeping
    public void acquireWakeLock() {
        wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "PowerManagementTest::WakeLock"
        );
        wakeLock.acquire(10 * 60 * 1000L);  // Acquire for 10 minutes
    }

    // Release WakeLock when not needed
    public void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    public boolean isWakeLockHeld() {
        return wakeLock != null && wakeLock.isHeld();
    }
}
