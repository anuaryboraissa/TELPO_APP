package com.telpo.tps550.api.demo.customize.Ethernet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.widget.TextView;

public class EthernetStateReceiver extends BroadcastReceiver {

    private final TextView ethernetStatusTextView;

    public EthernetStateReceiver(TextView ethernetStatusTextView) {
        this.ethernetStatusTextView = ethernetStatusTextView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            activeNetwork = connectivityManager.getActiveNetwork();
        }

        if (activeNetwork != null) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            if (networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                ethernetStatusTextView.setText("Ethernet Status: Connected");
            } else {
                ethernetStatusTextView.setText("Ethernet Status: Not Connected");
            }
        } else {
            ethernetStatusTextView.setText("Ethernet Status: Not Connected");
        }
    }
}
