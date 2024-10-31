package com.telpo.tps550.api.demo.customize.cards;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.softnet.devicetester.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class SIMCardDetectionActivity extends BaseActivity {
    private static final int REQUEST_CODE_PHONE_STATE = 1;

    private TextView tvSimStatus;
    private ListView lvSimDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_simcard_detection);
        tvSimStatus = findViewById(R.id.tvSimStatus);
        lvSimDetails = findViewById(R.id.lvSimDetails);

        requestPhoneStatePermission();

    }

    // Request phone state permission at runtime
    private void requestPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_CODE_PHONE_STATE);
        } else {
            detectSIMCard();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                detectSIMCard();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Detect SIM cards and update the UI
    private void detectSIMCard() {
        SubscriptionManager subscriptionManager =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            subscriptionManager = (SubscriptionManager) getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<SubscriptionInfo> simInfoList = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            simInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        }
        ArrayList<String> simDetails = new ArrayList<>();

        if (simInfoList != null && !simInfoList.isEmpty()) {
            tvSimStatus.setText("SIM Card(s) Detected:");
            for (SubscriptionInfo info : simInfoList) {
                String details = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    details = "Slot: " + info.getSimSlotIndex() +
                            "\nCarrier: " + info.getCarrierName() +
                            "\nCountry: " + info.getCountryIso() +
                            "\nPhone Number: " + info.getNumber();
                }
                simDetails.add(details);
            }
        } else {
            tvSimStatus.setText("No SIM card detected.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, simDetails);
        lvSimDetails.setAdapter(adapter);
    }
}