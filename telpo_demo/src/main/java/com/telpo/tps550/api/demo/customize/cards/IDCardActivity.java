package com.telpo.tps550.api.demo.customize.cards;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
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

import java.util.HashMap;

public class IDCardActivity extends BaseActivity {
    private UsbManager usbManager;
    private TextView resultTextView;
    private Button openReaderButton, readCardButton;
    private boolean isReaderOpen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_idcard);
        usbManager = (UsbManager) getSystemService(USB_SERVICE);
        resultTextView = findViewById(R.id.resultTextView);
        openReaderButton = findViewById(R.id.openReaderButton);
        readCardButton = findViewById(R.id.readCardButton);

        openReaderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReader();
            }
        });

        readCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readCard();
            }
        });
    }
    private void openReader() {
        // Check for connected USB devices
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            // Replace with your reader's vendor and product IDs
//            if (device.getVendorId() == YOUR_VENDOR_ID && device.getProductId() == YOUR_PRODUCT_ID) {
                // Grant permission and open the device (omitting permission handling for simplicity)
                isReaderOpen = true;
                resultTextView.setText("Reader opened successfully.");
                Log.d("CardReader", "Reader opened: " + device.getDeviceName());
                return;

        }
        resultTextView.setText("No compatible reader found.");
    }

    private void readCard() {
        if (!isReaderOpen) {
            resultTextView.setText("Reader is not open.");
            return;
        }

        // Read card logic goes here (e.g., using APDU commands)
        String cardData = simulateCardRead(); // Simulate reading card data
        resultTextView.setText("Card Data: " + cardData);
    }

    private String simulateCardRead() {
        // This is where you'd implement the actual card reading using APDU commands.
        // Return simulated card data for demonstration purposes.
        return "Simulated Card Data: [Name: John Doe, ID: 123456]";
    }
}