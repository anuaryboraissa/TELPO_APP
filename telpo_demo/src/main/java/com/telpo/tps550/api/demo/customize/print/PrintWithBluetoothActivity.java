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
                PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                PDFAdapterHelper helper=new PDFAdapterHelper(PrintWithBluetoothActivity.this,printManager,outputStream);
                //start
                // Get or create the directory path
                String directoryPath = getApplicationContext().getFilesDir().getPath() + "/Demo_SDK_AS";
                File directory = new File(directoryPath);

                // Check if the path exists and is a directory
                if (directory.exists() && !directory.isDirectory()) {
                    // Delete if it exists as a file instead of a directory
                    boolean deleted = directory.delete();
                    if (!deleted) {
                        Log.e("PRINTER", "Failed to delete existing file: " + directoryPath);
                        return;
                    }
                }
                // Create directory if it doesn’t exist
                if (!directory.exists()) {
                    boolean dirCreated = directory.mkdirs();
                    if (!dirCreated) {
                        Log.e("PRINTER", "Failed to create directory: " + directoryPath);
                        return;
                    }
                }
                // Specify the full file path including the file name
                String filePath = directoryPath + "/test_pdf.pdf";
//                    File pdfFile = new File(filePath);
//                    outputStream = new FileOutputStream(pdfFile);
//                    Log.e("PRINTER", "OUTPUT STREAM ===========> " + outputStream);
                // Create PDF helper and generate PDF
//                    PDFAdapterHelper helper = new PDFAdapterHelper(PrintWithWifiActivity.this, printManager, outputStream);
                helper.createPDFFile(filePath);
                //end
//                helper.createPDFFile(Comman.getAppPath(PrintWithBluetoothActivity.this)+"test_pdf.pdf");
                // Close the connection
                closeBluetoothConnection();
            } catch (IOException e) {
                Log.e(TAG, "Error printing data", e);
                runOnUiThread(() -> statusText.setText("Failed to print: " + e.getMessage()));
            } finally {
                closeBluetoothConnection();
            }
        }).start();
    }

    private void closeBluetoothConnection() {
        try {
            if (outputStream != null) outputStream.close();
            if (bluetoothSocket != null) bluetoothSocket.close();
        } catch (IOException e) {
            Log.e("InvoiceActivity", "Error closing Bluetooth connection", e);
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