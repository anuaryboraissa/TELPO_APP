package com.telpo.tps550.api.demo.customize;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.common.demo.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;
import com.telpo.tps550.api.demo.customize.battery.BatteryTest;
import com.telpo.tps550.api.demo.customize.battery.WakeLockTest;
import com.telpo.tps550.api.demo.customize.battery.WakeUpTest;

public class PowerManagementActivity extends BaseActivity {
    private WakeLockTest wakeLockTest;
    private BatteryTest batteryTest;
    private WakeUpTest wakeUpTest;

    private TextView batteryStatus;
    private TextView wakeLockStatus;
    private Button acquireWakeLockButton;
    private Button releaseWakeLockButton;
    private Button checkBatteryStatusButton;
    private Button scheduleWakeUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_power_management);

        batteryStatus = findViewById(R.id.battery_status);
        wakeLockStatus = findViewById(R.id.wake_lock_status);
        acquireWakeLockButton = findViewById(R.id.acquire_wake_lock);
        releaseWakeLockButton = findViewById(R.id.release_wake_lock);
        checkBatteryStatusButton = findViewById(R.id.check_battery_status);
        scheduleWakeUpButton = findViewById(R.id.schedule_wake_up);

        wakeLockTest = new WakeLockTest(this);
        batteryTest = new BatteryTest(this);
        wakeUpTest = new WakeUpTest(this);

        acquireWakeLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wakeLockTest.acquireWakeLock();
                updateWakeLockStatus();
            }
        });

        releaseWakeLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wakeLockTest.releaseWakeLock();
                updateWakeLockStatus();
            }
        });

        checkBatteryStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBatteryStatus();
            }
        });

        scheduleWakeUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wakeUpTest.scheduleWakeUp();
            }
        });

    }
    private void updateBatteryStatus() {
        int batteryLevel = batteryTest.getBatteryLevel();
        batteryStatus.setText("Battery Level: " + batteryLevel + "%");
        boolean isCharging = batteryTest.isCharging();
        batteryStatus.append("\nCharging: " + (isCharging ? "Yes" : "No"));
    }

    private void updateWakeLockStatus() {
        boolean isHeld = wakeLockTest.isWakeLockHeld();
        wakeLockStatus.setText("Wake Lock Status: " + (isHeld ? "Held" : "Released"));
    }
}