package com.telpo.tps550.api.demo.customize.DeliveryLocker;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.common.demo.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LockerControlActivity extends BaseActivity {
    private static final String TAG = "LOCKER DEVICE";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_SCAN = 2;

    private LockerService lockerService;
    private BluetoothService bluetoothService;
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> discoveredDevices = new ArrayList<>();

    private TextView lockerStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker_control);

        initializeUI();
        initializeServices();
        checkBluetoothSupport();
        startDeviceDiscovery();
        if(!checkBluetoothConnectPermission()){
            requestBluetoothPermissions();
        }
        if(!checkBluetoothScanPermission()){
            requestBluetoothScanPermission();
        }
        registerBluetoothReceiver();
        fetchLockerStatus();
    }
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);
        Log.i(TAG,"bluetoothReceiver GOING TO BE REGISTERED");
        checkBluetoothDeviceAndAdd(getIntent());
    }
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);
    }

    private void initializeUI() {
        lockerStatus = findViewById(R.id.lockerStatus);
        Button unlockButton = findViewById(R.id.unlockButton);
        Button lockButton = findViewById(R.id.lockButton);
        Button connectButton = findViewById(R.id.connectButton);
        Button disconnectButton = findViewById(R.id.disConnectButton);

        unlockButton.setOnClickListener(v -> unlockLocker());
        lockButton.setOnClickListener(v -> lockLocker());
        connectButton.setOnClickListener(v -> connectToFirstDevice());
        disconnectButton.setOnClickListener(v -> disconnectFromDevice());
    }

    private void initializeServices() {
        lockerService = APIClient.getClient().create(LockerService.class);
        bluetoothService = new BluetoothService(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void checkBluetoothSupport() {
        if (bluetoothAdapter == null) {
            showToast("Bluetooth not supported on this device");
            finish(); // Close activity if Bluetooth is not supported
        } else if (!bluetoothAdapter.isEnabled()) {
            Log.i(TAG,"ENABLE BLUETOOTH");
            requestBluetoothEnable();
        }
        else{
            Log.i(TAG,"BLUETOOTH IS OKAY");
        }
    }

    private void requestBluetoothEnable() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    private void registerBluetoothReceiver() {
        Log.i(TAG,"REGISTER BLUETOOTH RECEIVER");

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);
    }

    private void startDeviceDiscovery() {
       final int REQUEST_LOCATION_PERMISSION = 2;
        // Check if Bluetooth is supported
         bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth not supported");
            return;
        }

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            Log.i(TAG," START REQUEST LOCATION BLUETOOTH =========> ");

        } else {
            Log.i(TAG," START BLUETOOTH DISCOVERY =========> ");

            // Permission already granted, start discovery
            bluetoothAdapter.startDiscovery();
        }
    }

    private boolean checkBluetoothScanPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestBluetoothScanPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.i(TAG," BLUETOOTH SCANNING =========> ");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_SCAN);
            startDeviceDiscovery();
        }else{
            Log.i(TAG,"FAILED BLUETOOTH SCANNING =========> ");

        }
    }

    private void connectToFirstDevice() {
        if (discoveredDevices.isEmpty()) {
            Log.i(TAG,"NO DEVICE  = =========> ");
            showToast("No devices discovered");
            return;
        }
        Log.i(TAG,"CONNECT NOW = =========> ");
        String lockerMacAddress = discoveredDevices.get(0).getAddress();
        bluetoothService.connectToLocker(lockerMacAddress);
    }

    private void disconnectFromDevice() {
        try {
            bluetoothService.disconnect();
            showToast("Disconnected from device");
        } catch (IOException e) {
            showErrorToast("Disconnection failed: " + e.getMessage());
        }
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkBluetoothDeviceAndAdd(intent);
        }


    };
    private void checkBluetoothDeviceAndAdd(Intent intent) {
        Log.i(TAG,"INTENT ACTION: "+ intent.getAction());

        if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.i(TAG,"INTENT DEVICE: "+device+" BLUETOOTH CONNECT PERMISSION: "+checkBluetoothConnectPermission());

            if (device != null && checkBluetoothConnectPermission()) {
                discoveredDevices.add(device);
                Log.i(TAG, "Device Found: " + device.getName() + " [" + device.getAddress() + "]");
            }
        }
    }
    private boolean checkBluetoothConnectPermission() {
           return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private static final int REQUEST_CODE_BLUETOOTH_PERMISSIONS = 1;

    private void requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_BLUETOOTH_PERMISSIONS);
    }

    private void fetchLockerStatus() {
        lockerService.getLockerStatus("1").enqueue(new Callback<Locker>() {
            @Override
            public void onResponse(Call<Locker> call, Response<Locker> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Locker locker = response.body();
                    lockerStatus.setText("Locker Status: " + (locker.isLocked() ? "Locked" : "Unlocked"));
                } else {
                    showErrorToast("Failed to retrieve locker status");
                }
            }

            @Override
            public void onFailure(Call<Locker> call, Throwable t) {
                showErrorToast("Failed to get status: " + t.getMessage());
            }
        });
    }

    private void unlockLocker() {
        try {
            bluetoothService.unlockLocker();
            showToast("Locker Unlocked");
        } catch (IOException e) {
            showErrorToast("Unlock failed: " + e.getMessage());
        }
    }

    private void lockLocker() {
        try {
            bluetoothService.lockLocker();
            showToast("Locker Locked");
        } catch (IOException e) {
            showErrorToast("Lock failed: " + e.getMessage());
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_BLUETOOTH_PERMISSIONS) {
            boolean allPermissionsGranted = true;

            for (String permission : REQUIRED_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (!allPermissionsGranted) {
                // Request the permissions
                requestBluetoothPermissions();
            } else {
                // Permissions already granted, proceed with Bluetooth operations
                startDeviceDiscovery();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        bluetoothAdapter.cancelDiscovery();
    }

    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

}
