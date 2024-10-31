package com.telpo.tps550.api.demo.customize;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.softnet.devicetester.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;
import com.telpo.tps550.api.demo.customize.Ethernet.EthernetStateReceiver;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class EthernetTestActivity extends BaseActivity {
    private static final String TAG = "EthernetDetector";
    private TextView ethernetStatusTextView, pingResultTextView;
    private EthernetStateReceiver ethernetStateReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ethernet_test);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(ethernetStateReceiver);  // Clean up BroadcastReceiver
    }

    // Check Ethernet connectivity status
    @SuppressLint("SetTextI18n")
    private void checkEthernetStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            activeNetwork = connectivityManager.getActiveNetwork();
        }

        if (activeNetwork != null) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            if (networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                ethernetStatusTextView.setText(R.string.ethernet_status_connected);
            } else {
                ethernetStatusTextView.setText("Ethernet Status: Not Connected");
            }
        } else {
            ethernetStatusTextView.setText("Ethernet Status: Not Connected");
        }
    }

    // Run a ping test to verify network connectivity
    private void runPingTest() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);  // Allow network operations on the main thread (for demo purposes only)

        try {
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 4 8.8.8.8");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder pingResult = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                pingResult.append(line).append("\n");
            }
            reader.close();

            int exitValue = process.waitFor();
            if (exitValue == 0) {
                pingResultTextView.setText(getString(R.string.ping_result) + pingResult);
            } else {
                pingResultTextView.setText(R.string.ping_failed_please_check_the_ethernet_connection);
            }

        } catch (Exception e) {
            Log.e(TAG, "Ping test failed", e);
            Toast.makeText(this, "Ping test failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}