package com.telpo.tps550.api.demo.nfc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.common.apiutil.util.StringUtil;
import com.common.apiutil.util.SystemUtil;
import com.common.demo.databinding.ActivityNfcBinding;
import com.common.demo.databinding.ActivityNfcNewBinding;
import com.common.demo.databinding.ActivitySensorBinding;
import com.telpo.tps550.api.demo.customize.Constants.ApplicationUtils;
import com.telpo.tps550.api.demo.printer.UsbPrinterActivity;

/**
 * NFC 拉起页面
 */
public class NFCActivityNew extends Activity implements NfcAdapter.CreateNdefMessageCallback,NfcAdapter.OnNdefPushCompleteCallback {

    //支持的标签类型
    private NfcAdapter nfcAdapter;
    private ActivityNfcNewBinding binding;
    int successCount = 0;
    private final String TAG="NFC";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityNfcNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeData();

    }
    private void initializeData(){
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            ApplicationUtils.handleDeviceError(NFCActivityNew.this,"NFC is not supported on this device",this::restartOp);
        }
        // Register callback  *设置一个回调，使用Android Beam（TM）动态生成要发送的NDEF消息。
//        nfcAdapter.setNdefPushMessageCallback(this, this);
        else if (!nfcAdapter.isEnabled()) {
            ApplicationUtils.handleDeviceError(NFCActivityNew.this,"Please enable NFC from setting",this::restartOp);

            // NFC is supported but not enabled
            Toast.makeText(this, "Please enable NFC from settings", Toast.LENGTH_SHORT).show();
            // Open NFC settings to enable it
            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            startActivity(intent);
        }else{
            //how can i process intent from here
            // Process any existing NFC intent when the activity starts
            Intent intent = getIntent();
            if(intent!=null){
                Log.e(TAG, "NFC INTENT EXECUTE READY "+intent.getAction());
            }

            if (intent != null && NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
                processIntent(intent);  // Handle intent if it exists on startup
            }
            Log.e(TAG, "NFC INTENT EXECUTED SUCCESS");

        }
//        nfcAdapter.setNdefPushMessageCallback(this, this);

        if(!SystemUtil.getInternalModel().equals("C9")){
            binding.boundingBox.setVisibility(View.GONE);
        }
    }

    private void restartOp() {
        initializeData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "EXECUTE ON-RESUME METHOD "+getIntent().getAction()+" DISCOVER: "+NfcAdapter.ACTION_TECH_DISCOVERED);
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            Log.e(TAG, "EXECUTE ON-RESUME SUCCESS");
            processIntent(getIntent());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableReaderMode(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.e(TAG,"ON NEW INTENT CALLED =========> : "+intent.getAction());
        processIntent(intent);
    }

    /**
     * 处理Intent携带的数据
     */
    private void processIntent(Intent intent) {
        Log.e(TAG, "STARTING ON-START METHOD");
        long startTime = System.currentTimeMillis();
//        Bundle tag = intent.getExtras();
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) return;

//        String content = StringUtil.toHexString(((Tag) tag.get("android.nfc.extra.TAG")).getId());

        String[] techList = tag.getTechList();
        String data = tag.toString();
        byte[] ID =  tag.getId();
        if(ID != null && !data.isEmpty()){
            data += "\n\nUID:\n" +StringUtil.toHexString(ID);
            data += "\nData format:";
            for (String tech : techList) {
                data += "\n" + tech;
            }
            successCount++;
        }
        long endTime = System.currentTimeMillis();
        binding.successCount.setText("成功次数:"+successCount + "\n" + "读卡时间：" + (endTime - startTime) + " ms");
        binding.tvShowNfc.setText(data);
//        Toast.makeText(this, "获取NFC数据:" + data, Toast.LENGTH_LONG).show();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = "Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis();
        Log.e(TAG, "STARTING createNdefMessage over");

        return new NdefMessage(
                new NdefRecord[]{
                        NdefRecord.createMime("application/vnd.com.example.android.beam", text.getBytes())
                }
        );
    }

    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        Log.e(TAG, "STARTING onNdefPushComplete over: "+nfcEvent.nfcAdapter);

    }
}
