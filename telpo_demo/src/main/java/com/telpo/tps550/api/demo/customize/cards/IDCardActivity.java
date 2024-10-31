package com.telpo.tps550.api.demo.customize.cards;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.softnet.devicetester.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class IDCardActivity extends BaseActivity {
    private static final String ACTION_USB_PERMISSION = "com.softnet.devicetester.USB_PERMISSION";
    private UsbManager usbManager;
    private UsbDeviceConnection connection;
    private UsbEndpoint endpointIn, endpointOut;
    private TextView resultTextView;
    private Button openReaderButton, readCardButton;
    private boolean isReaderOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard);

        usbManager = (UsbManager) getSystemService(USB_SERVICE);
        resultTextView = findViewById(R.id.resultTextView);
        openReaderButton = findViewById(R.id.openReaderButton);
        readCardButton = findViewById(R.id.readCardButton);

        openReaderButton.setOnClickListener(v -> openReader());
        readCardButton.setOnClickListener(v -> readCard());

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        // Register a BroadcastReceiver for USB permissions
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
//        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        registerReceiver(usbReceiver, filter);
    }

    private void openReader() {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        for (UsbDevice device : deviceList.values()) {
            Log.d("CardReader", "Found device: " + device.getDeviceName());

            // Replace with your actual Vendor ID and Product ID
//            if (device.getVendorId() == YOUR_VENDOR_ID && device.getProductId() == YOUR_PRODUCT_ID) {
                PendingIntent permissionIntent = PendingIntent.getBroadcast(
                        this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
                usbManager.requestPermission(device, permissionIntent);
                return;
//            }
        }

        resultTextView.setText("No compatible reader found.");
    }

    private void readCard() {
        if (!isReaderOpen) {
            resultTextView.setText("Reader is not open.");
            return;
        }

        byte[] selectApdu = new byte[]{
                (byte) 0x00, // CLA: Command class
                (byte) 0xA4, // INS: Select instruction
                (byte) 0x04, // P1: Select by name
                (byte) 0x00, // P2: First occurrence
                (byte) 0x07, // Lc: Length of the AID (Application Identifier)
                (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x47, (byte) 0x10, (byte) 0x01, // AID
                (byte) 0x00  // Le: Expected length of the response
        };
        byte[] response = sendApdu(selectApdu);

        if (response != null) {
            String cardData = new String(response, StandardCharsets.UTF_8);
            resultTextView.setText("Card Data: " + cardData);
        } else {
            resultTextView.setText("Failed to read card.");
        }
    }

    private byte[] sendApdu(byte[] command) {
        if (endpointOut == null || endpointIn == null || connection == null) {
            Log.e("CardReader", "USB connection is not established.");
            return null;
        }

        // Send the command to the reader
        int result = connection.bulkTransfer(endpointOut, command, command.length, 5000);
        if (result < 0) {
            Log.e("CardReader", "Failed to send APDU command.");
            return null;
        }

        // Receive the response from the reader
        byte[] buffer = new byte[256];
        int received = connection.bulkTransfer(endpointIn, buffer, buffer.length, 5000);
        if (received < 0) {
            Log.e("CardReader", "Failed to receive response.");
            return null;
        }

        byte[] response = new byte[received];
        System.arraycopy(buffer, 0, response, 0, received);
        return response;
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
                            setupDevice(device);
                        }
                    } else {
                        resultTextView.setText("Permission denied for device.");
                    }
                }
            }
            // Since we are not using a specific action, we will just check for USB connection events
//            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
//                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                if (device != null) {
//                    Log.d("USB Receiver", "Device attached: " + device.getDeviceName());
//                }
//            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
//                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                if (device != null) {
//                    Log.d("USB Receiver", "Device detached: " + device.getDeviceName());
//                }
//            }
        }
    };

    private void setupDevice(UsbDevice device) {
        UsbInterface usbInterface = device.getInterface(0);
        connection = usbManager.openDevice(device);
        if (connection == null) {
            resultTextView.setText("Failed to open device.");
            return;
        }

        connection.claimInterface(usbInterface, true);
        endpointOut = usbInterface.getEndpoint(0);
        endpointIn = usbInterface.getEndpoint(1);

        isReaderOpen = true;
        resultTextView.setText("Reader opened successfully.");
        Log.d("CardReader", "Reader opened: " + device.getDeviceName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(usbReceiver);
    }
}
