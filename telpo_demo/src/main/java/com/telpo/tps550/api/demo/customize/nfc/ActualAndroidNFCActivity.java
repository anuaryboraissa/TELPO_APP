package com.telpo.tps550.api.demo.customize.nfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.common.apiutil.util.StringUtil;
import com.common.apiutil.util.SystemUtil;
import com.common.demo.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;
import com.telpo.tps550.api.demo.customize.Constants.ApplicationUtils;
import com.telpo.tps550.api.demo.nfc.NFCActivityNew;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ActualAndroidNFCActivity extends BaseActivity  implements NfcAdapter.CreateNdefMessageCallback{
    private static final String TAG = "NFC_APP";
    private NfcAdapter nfcAdapter;
    private TextView textStatus, textTagContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_android_nfcactivity);
        textStatus = findViewById(R.id.textStatus);
        textTagContent = findViewById(R.id.textTagContent);
        initialize();
    }

    private void restartOp() {
        initialize();
    }
    private void initialize(){
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        } else if (!nfcAdapter.isEnabled()) {
            ApplicationUtils.handleDeviceError(ActualAndroidNFCActivity.this,"Please enable NFC from setting",this::restartOp);

            // NFC is supported but not enabled
            Toast.makeText(this, "Please enable NFC from settings", Toast.LENGTH_SHORT).show();
            // Open NFC settings to enable it
            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            startActivity(intent);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "EXECUTE ON-RESUME METHOD "+ getIntent().getAction());
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            Log.e(TAG, "EXECUTE ON-RESUME SUCCESS");
            handleIntent(getIntent());
        }

    }

    @Override
    protected void onPause() {
        Log.e(TAG,"ON PAUSE CALLED =======> ");
        super.onPause();
        if (nfcAdapter != null) {
            Log.e(TAG,"ON PAUSE CALLED STOP BACKGROUND=======> ");
            nfcAdapter.disableReaderMode(this);
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG,"ON NEW INTENT ACTION: "+intent.getAction());
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        Log.e(TAG,"action: "+action);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Log.e(TAG,"TAG CAPTURED AS RESULT: "+tag);

            if (tag != null) {
                Log.e(TAG,"NFC-TAG DETECTED: "+tag);
                textStatus.setText("NFC Tag Detected");
                readFromTag(tag);
            }
        }
    }

    private void readFromTag(Tag tag) {

        long startTime = System.currentTimeMillis();
        if (tag == null) return;
        Log.e(TAG, "TAG IS "+tag);

        String[] techList = tag.getTechList();
        String data = tag.toString();
        byte[] ID =  tag.getId();
        if(ID != null && !data.isEmpty()){
            data += "\n\nUID:\n" + StringUtil.toHexString(ID);
            data += "\nData format:";
            for (String tech : techList) {
                data += "\n" + tech;
            }
            Log.e(TAG, "DATA IS "+data);
        }
        textTagContent.setText("Tag Content: " + data);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        String text = "Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis();
        Log.e(TAG, "Ndef Message: "+ text);
        return new NdefMessage(
                new NdefRecord[]{
                        NdefRecord.createMime("application/vnd.com.example.android.beam", text.getBytes())
                }
        );
    }
}