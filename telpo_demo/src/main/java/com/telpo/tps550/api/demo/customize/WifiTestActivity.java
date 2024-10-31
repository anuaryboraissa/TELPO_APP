package com.telpo.tps550.api.demo.customize;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.common.demo.R;
import com.telpo.tps550.api.demo.HomeActivity;
import com.telpo.tps550.api.demo.bean.BaseActivity;
import com.telpo.tps550.api.demo.iccard.PsamCardActivity;

public class WifiTestActivity extends BaseActivity {
    private TextView wifiStatus,cellularStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wifi_test);
// Add this in onCreate or before using Wi-Fi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        wifiStatus = findViewById(R.id.wifiStatus);
        cellularStatus = findViewById(R.id.cellularStatus);

        Button testWifiButton = findViewById(R.id.testWifiButton);
        Button testCellularButton = findViewById(R.id.testCellularButton);
        Button testEthernet = findViewById(R.id.testEthernet);
        testEthernet.setOnClickListener(view -> {
            startActivity(new Intent(WifiTestActivity.this, EthernetTestActivity.class));
        });

        testWifiButton.setOnClickListener(v -> testWifiConnectivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            testCellularButton.setOnClickListener(v -> testCellularConnectivity());
        }

    }
    private void testWifiConnectivity() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            String ssid = wifiManager.getConnectionInfo().getSSID();
            wifiStatus.setText("Connected to Wi-Fi: " + ssid);
        } else {
            wifiStatus.setText("Wi-Fi is not enabled.");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void testCellularConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                cellularStatus.setText("Cellular is connected.");
            } else {
                cellularStatus.setText("Cellular is not connected.");
            }
        } else {
            Toast.makeText(this, "Connectivity Manager not available.", Toast.LENGTH_SHORT).show();
        }
    }
}