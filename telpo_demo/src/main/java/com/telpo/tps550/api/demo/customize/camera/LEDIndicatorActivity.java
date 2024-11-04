package com.telpo.tps550.api.demo.customize.camera;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.softnet.devicetester.R;
import com.telpo.tps550.api.demo.HomeActivity;
import com.telpo.tps550.api.demo.bean.BaseActivity;

import com.common.apiutil.util.SystemUtil;
import com.telpo.tps550.api.demo.customize.print.PrintWithBluetoothActivity;
import com.telpo.tps550.api.demo.pos.LedActivity;

public class LEDIndicatorActivity extends BaseActivity {
    private static final String CHANNEL_ID = "led_channel";
    private NotificationManager notificationManager;
    private CameraManager cameraManager;
    private String cameraId;
    private String TAG="LED_INDICATOR";



    private boolean isFlashOn = false;
    private Handler handler = new Handler();
    private Runnable blinkRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ledindicator);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        requestPermission();
        // Create Notification Channel for Android 8.0+
        createNotificationChannel();

        // Test LED indicator or Flashlight as fallback
        // Find Buttons from XML
        Button ledTestButton = findViewById(R.id.buttonLEDTest);
        Button btnStartBlink = findViewById(R.id.btnStartBlink);
        Button btnStopBlink = findViewById(R.id.btnStopBlink);
//        Button btnTorch = findViewById(R.id.ledTorch);
//
//        btnTorch.setOnClickListener(view -> {
//            startActivity(new Intent(LEDIndicatorActivity.this, LedActivity.class));
//        });

        // Set Click Listener for LED Test Button
        ledTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLEDSupported()) {
                    testLEDIndicator();
                    Toast.makeText(LEDIndicatorActivity.this, "LED Notification triggered", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LEDIndicatorActivity.this, "LED not supported on this device", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            String[] cameraIds = cameraManager.getCameraIdList();
            for (String id : cameraIds) {
                Log.d("CameraID", "Camera found: " + id);
//
                Boolean hasFlash = cameraManager.getCameraCharacteristics(id)
                        .get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
//                boolean hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                if (hasFlash!=null) {
                    if(!hasFlash){
                        Toast.makeText(this, "No flash available on this device", Toast.LENGTH_SHORT).show();
                    }else{
                        cameraId = id;
                        break;
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        btnStartBlink.setOnClickListener(view -> {
           if( cameraId==null){
               Toast.makeText(LEDIndicatorActivity.this, "LED Notification triggered", Toast.LENGTH_SHORT).show();
           }else{
               startBlinkingFlash();
           }

        });
        btnStopBlink.setOnClickListener(v -> {
            if( cameraId==null){
                Toast.makeText(LEDIndicatorActivity.this, "LED Notification triggered", Toast.LENGTH_SHORT).show();
            }else{
                stopBlinkingFlash();
            }
        });

    }
    // Check if the device supports LED notifications (simple assumption)
    private boolean isLEDSupported() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q;  // Many newer devices lack LED support
    }
    private void requestPermission(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 101);
//        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "LED Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            // Enable LED light
            channel.enableLights(true);

            // Set the LED color to RED
            channel.setLightColor(android.graphics.Color.RED);

            // Configure blinking pattern (1000ms ON, 1000ms OFF)
            channel.setLightColor(android.graphics.Color.RED);
            channel.setVibrationPattern(new long[]{1000, 1000});
            channel.enableVibration(true);

            // Register the channel with the system
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Method to test LED Indicator using Notification
    private void testLEDIndicator() {
        Log.e(TAG,"test LED Indicator");
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("LED Test")
                .setContentText("Testing LED indicator...")
                .setSmallIcon(R.drawable.ic_led_indicator) // Replace with a valid icon
                .setLights(android.graphics.Color.GREEN, 3000, 3000) // 3s ON, 3s OFF
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        // Display the notification (ID = 1)
        notificationManager.notify(1, notification);
    }

    // Blink the Flashlight as an Alternative to LED
    private void blinkFlashlight() {
        try {
            Log.e(TAG,"Blink the Flashlight");
            // Get the first available camera ID
            String[] cameras= cameraManager.getCameraIdList();
            if(cameras.length>0){
                Log.e(TAG,"CAMERA DETECTED "+cameras.length);
                cameraId = cameraManager.getCameraIdList()[0];
                // Turn on the flashlight for 1 second, then turn it off
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cameraManager.setTorchMode(cameraId, true);
                    Thread.sleep(1000);  // Wait for 1 second
                    cameraManager.setTorchMode(cameraId, false);
                    Log.e(TAG,"Blink the Flashlight SUCCESSFULLY ");
                    // Turn OFF// Turn ON
                }
            }else{
                Toast.makeText(LEDIndicatorActivity.this, "NO CAMERA DETECTED", Toast.LENGTH_SHORT).show();
            }

        } catch (CameraAccessException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    // Method to blink LED on Telpo terminal
    // Start blinking flashlight with a specific interval
    private void startBlinkingFlash() {
        blinkRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    // Toggle flash state
                    isFlashOn = !isFlashOn;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraId, isFlashOn);
                    }

                    // Schedule the next blink after 500ms
                    handler.postDelayed(this, 500);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        };

        // Start the blinking
        handler.post(blinkRunnable);
    }

    // Stop blinking flashlight
    private void stopBlinkingFlash() {
        handler.removeCallbacks(blinkRunnable);
        try {
            // Ensure the flashlight is off
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, false);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}