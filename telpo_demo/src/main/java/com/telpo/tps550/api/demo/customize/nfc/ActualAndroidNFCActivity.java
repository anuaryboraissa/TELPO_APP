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
//        nfcAdapter.setNdefPushMessageCallback(this, this);

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
//        onResume1();
//        initialize();
//        onResume2();
    }

    private void onResume2(){
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        IntentFilter[] filters = new IntentFilter[] {
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        };

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
        Log.e(TAG,"ON RESUME CALLED 1 =======> "+intent.getAction());
    }

    private void onResume1() {
        Log.e(TAG,"ON RESUME CALLED 1 =======> "+nfcAdapter);

        if (nfcAdapter != null) {
            nfcAdapter.enableReaderMode(this, tag -> {
                        // Handle the discovered tag
                        Log.e(TAG, "Tag discovered: " + tag.toString());
                    },
                    NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_NFC_V,
                    null);
        }
        Log.e(TAG,"ON RESUME CALLED =======> "+getIntent());
        // Create Pending Intent for NFC Tag Detection
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        Log.e(TAG,"ON PENDING INTENT =======> "+pendingIntent.getIntentSender());

        // Setup intent filters for NDEF, TECH, and TAG discovery
        IntentFilter[] intentFilters = new IntentFilter[]{
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        };

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        Log.e(TAG,"ON AFTER FINISH BACKGROUND =======> "+getIntent().getAction());
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            Log.e(TAG, "EXECUTE ON-RESUME SUCCESS NOW");
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

//        Ndef ndef = Ndef.get(tag);
//        Log.e(TAG,"NDEF DETECTED: "+ndef);
//
//        if (ndef != null) {
//            try {
//                ndef.connect();
//                NdefMessage ndefMessage = ndef.getNdefMessage();
//                NdefRecord[] records = ndefMessage.getRecords();
//
//                for (NdefRecord record : records) {
//                    if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN &&
//                            Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
//                        String payload = new String(record.getPayload(), StandardCharsets.UTF_8);
//                        Log.e(TAG,"TAG CONTENT: "+payload);
//
//                        textTagContent.setText("Tag Content: " + payload);
//                    }
//                }
//                ndef.close();
//            } catch (Exception e) {
//                Log.e(TAG, "Error reading NFC tag", e);
//                Toast.makeText(this, "Error reading NFC tag: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }



        long startTime = System.currentTimeMillis();
//        Bundle tag = intent.getExtras();
//        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) return;
        Log.e(TAG, "TAG IS "+tag);

//        String content = StringUtil.toHexString(((Tag) tag.get("android.nfc.extra.TAG")).getId());

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
//        binding.tvShowNfc.setText(data);
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