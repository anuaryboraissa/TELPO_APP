package com.telpo.tps550.api.demo.customize.print;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.softnet.devicetester.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PrintWithBluetoothActivity extends BaseActivity {
    private static final String TAG = "BluetoothPrinter";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private Spinner spinnerPrinters;
    private TextView statusText;

    private ArrayList<BluetoothDevice> printerDevices;
    private ArrayAdapter<String> deviceListAdapter;

    private BluetoothDevice selectedPrinter;

    private static final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_print_with_bluetooth);
        // Initialize UI components
        spinnerPrinters = findViewById(R.id.spinnerPrinters);
        statusText = findViewById(R.id.statusText);
        Button btnFindPrinters = findViewById(R.id.btnFindPrinters);
        Button btnPrint = findViewById(R.id.btnPrint);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        printerDevices = new ArrayList<>();
        deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

        // Set spinner adapter
        spinnerPrinters.setAdapter(deviceListAdapter);
        btnFindPrinters.setOnClickListener(v -> showBluetoothDevices());

        spinnerPrinters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPrinter = printerDevices.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPrinter = null;
            }
        });
        if (checkBluetoothPermissions()) {
            requestBluetoothPermissions();
        } else {
            // Your Bluetooth functionality code
            showBluetoothDevices();
        }
        btnPrint.setOnClickListener(v -> {
            if(checkBluetoothPermissions()){
                Log.e(TAG, "No perm 1: ");
                requestBluetoothPermissions();
            }else{
                Log.e(TAG, "PRINT NOW : "+(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED));
                printData();
            }
        });
        // Set up the ListView with dummy data

    }

    // Method to connect and print data
    private void printData() {
        if (selectedPrinter == null) {
            Toast.makeText(this, "Please select a printer", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            try {
                Log.e(TAG, "NO PERMISSION STILL 1: "+(Build.VERSION.SDK_INT < Build.VERSION_CODES.S));
                Log.e(TAG, "NO PERMISSION STILL 2: "+(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED));
                Log.e(TAG, "NO PERMISSION STILL 3: "+(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED));

                if (!(Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                        (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED))) {
                    Log.e(TAG, "NO PERMISSION STILL");
                    return;
                }
                bluetoothSocket = selectedPrinter.createRfcommSocketToServiceRecord(PRINTER_UUID);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                String selectedDeviceName = (String) spinnerPrinters.getSelectedItem();
                runOnUiThread(() ->statusText.setText("Connected to " + selectedDeviceName) );
                //format content to print
                printReceipt();
                closeBluetoothConnection();
            } catch (IOException e) {
                Log.e(TAG, "Error printing data", e);
                runOnUiThread(() -> statusText.setText("Failed to print: " + e.getMessage()));
            } finally {
                closeBluetoothConnection();
            }
        }).start();
    }

    private void printReceipt() {
        try {
            // Set up the printer for receipt formatting
            outputStream.write(new byte[]{0x1B, 0x61, 0x01}); // Center alignment for header

            // Print header
            outputStream.write("Your Store Name\n".getBytes());
            outputStream.write("123 Main St.\n".getBytes());
            outputStream.write("City, State, ZIP\n".getBytes());
            outputStream.write("Tel: (123) 456-7890\n".getBytes());
            outputStream.write("===============================\n".getBytes());

            // Reset alignment to left for item list
            outputStream.write(new byte[]{0x1B, 0x61, 0x00}); // Left alignment
            outputStream.write("Item            Qty     Price\n".getBytes());
            outputStream.write("-------------------------------\n".getBytes());

            // Print items
            outputStream.write("Item A          1       $4.99\n".getBytes());
            outputStream.write("Item B          2       $9.99\n".getBytes());
            outputStream.write("Item C          1       $2.49\n".getBytes());

            outputStream.write("-------------------------------\n".getBytes());

            // Subtotal, tax, and total
            outputStream.write(String.format("%-20s %7s\n", "Subtotal", "$17.47").getBytes());
            outputStream.write(String.format("%-20s %7s\n", "Tax (5%)", "$0.87").getBytes());
            outputStream.write(String.format("%-20s %7s\n", "Total", "$18.34").getBytes());

            // Footer message
            outputStream.write("===============================\n".getBytes());
            outputStream.write("Thank you for your purchase!\n".getBytes());
            outputStream.write("Visit us again!\n\n\n".getBytes());

            // Flush the output stream
            outputStream.flush();

        } catch (IOException e) {
            Log.e(TAG, "Error printing receipt", e);
        }
    }


    private void closeBluetoothConnection() {
        try {
            if (outputStream != null) outputStream.close();
            if (bluetoothSocket != null) bluetoothSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing Bluetooth connection", e);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeBluetoothConnection();
    }

    //configure permissions
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private boolean checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED;
        }
        // For lower versions, Bluetooth permission is automatically granted
        return false;
    }
    private void requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                    REQUEST_BLUETOOTH_PERMISSIONS
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Bluetooth permissions granted.");
                showBluetoothDevices();
            } else {
                Log.e(TAG, "Bluetooth permissions denied.");
            }
        }
    }

    // Function to show list of Bluetooth devices (requires granted permissions)
    private void showBluetoothDevices() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED)) {

            // Add your Bluetooth discovery or listing logic here
            Log.i(TAG, "Listing available Bluetooth devices...");
            // This is where you would add the code to list paired Bluetooth devices or scan nearby ones
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_PERMISSIONS);
            }

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            Log.e(TAG, "PRINTERS: "+ pairedDevices.size());

            printerDevices.clear();
            deviceListAdapter.clear();

            if (!pairedDevices.isEmpty()) {
                for (BluetoothDevice device : pairedDevices) {
                    Log.e(TAG, "BLUETOOTH DEVICE: "+ device.getName());

                    printerDevices.add(device);
                    deviceListAdapter.add(device.getName());
                }
                deviceListAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "No paired printers found", Toast.LENGTH_SHORT).show();
            }

        } else {
            Log.e(TAG, "No Bluetooth permissions.");
            requestBluetoothPermissions();  // Request permissions again if necessary
        }
    }
}