package com.telpo.tps550.api.demo.customize.DeliveryLocker;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private final UUID LOCKER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Activity activity;
    private String TAG="DELIVERY LOCKER";

    public BluetoothService(Activity activity) {
        this.activity = activity;
    }
    public boolean connectToLocker(String macAddress) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);

        // Check Bluetooth permissions
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission at runtime
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
            Log.e(TAG, "Bluetooth permission not granted.");
            return false;
        }

        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(LOCKER_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            Log.i(TAG, "Bluetooth connected successfully.");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error connecting to locker", e);
            return false;
        }
    }

    public void unlockLocker() throws IOException {
        if (outputStream == null) {
            throw new IOException("OutputStream is not initialized. Please connect to a device first.");
        }
        // Your unlock command logic
        outputStream.write(new byte[]{/* command bytes */});
    }

    public void lockLocker() throws IOException {
        if (outputStream == null) {
            throw new IOException("OutputStream is not initialized. Please connect to a device first.");
        }
        // Your lock command logic
        outputStream.write(new byte[]{/* command bytes */});
    }

    public void disconnect() throws IOException {
        if (bluetoothSocket != null) bluetoothSocket.close();
    }
}
