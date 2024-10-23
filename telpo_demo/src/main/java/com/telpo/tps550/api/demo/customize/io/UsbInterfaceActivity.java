package com.telpo.tps550.api.demo.customize.io;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.common.demo.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsbInterfaceActivity extends BaseActivity {
    private static final String ACTION_USB_PERMISSION = "com.common.dem.USB_PERMISSION";

    private UsbManager usbManager;
    private PendingIntent permissionIntent;
    private ListView deviceListView;
    private TextView usbStatus;
    private Button refreshButton;
    private List<String> deviceNames;
    private List<UsbDevice> usbDevices;
    private ArrayAdapter<String> adapter;
    private boolean isReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_usb_interface);

        usbManager = (UsbManager) getSystemService(USB_SERVICE);
        permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);

        usbStatus = findViewById(R.id.usbStatus);
        deviceListView = findViewById(R.id.deviceList);
        refreshButton = findViewById(R.id.refreshButton);

        deviceNames = new ArrayList<>();
        usbDevices = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceNames);
        deviceListView.setAdapter(adapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(usbReceiver, new IntentFilter(ACTION_USB_PERMISSION), Context.RECEIVER_NOT_EXPORTED);
            isReceiverRegistered = true;
        }

        refreshButton.setOnClickListener(view -> listConnectedDevices());

        deviceListView.setOnItemClickListener((parent, view, position, id) -> {
            UsbDevice device = usbDevices.get(position);
            requestPermission(device);
        });

        listConnectedDevices();
    }


    private void listConnectedDevices() {
        deviceNames.clear();
        usbDevices.clear();

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        if (deviceList.isEmpty()) {
            usbStatus.setText("No USB devices found.");
        } else {
            usbStatus.setText("Connected USB Devices:");
            for (UsbDevice device : deviceList.values()) {
                deviceNames.add("Device: " + device.getDeviceName() +
                        " (Vendor: " + device.getVendorId() + ", Product: " + device.getProductId() + ")");
                usbDevices.add(device);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void requestPermission(UsbDevice device) {
        usbManager.requestPermission(device, permissionIntent);
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Toast.makeText(context, "Permission granted for: " + device.getDeviceName(), Toast.LENGTH_SHORT).show();
                            // Further communication can be performed here.
                        }
                    } else {
                        assert device != null;
                        Toast.makeText(context, "Permission denied for: " + device.getDeviceName(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver only if it was registered
        if (isReceiverRegistered) {
            unregisterReceiver(usbReceiver);
            isReceiverRegistered = false;
        }
    }
}