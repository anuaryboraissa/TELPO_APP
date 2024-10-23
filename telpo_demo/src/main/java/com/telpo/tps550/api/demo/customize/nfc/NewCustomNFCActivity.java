package com.telpo.tps550.api.demo.customize.nfc;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.common.apiutil.nfc.CardTypeEnum;
import com.common.apiutil.nfc.NfcUtil;
import com.common.apiutil.nfc.SelectCardReturn;
import com.common.apiutil.nfc.exception.WrongParamException;
import com.common.demo.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;
import com.telpo.tps550.api.demo.customize.nfc.new_one.NfcScheduler;

import java.util.Arrays;

public class NewCustomNFCActivity extends BaseActivity {
    private TextView statusText;
    private Button controlButton,auth_keyType;
    private NfcUtil nfcUtil;
    private boolean startFindFlag = false;
    private Handler mHandler = new Handler();
    private int findCount = 0;
    int keyType = 0;
    int block = 10;
    boolean authResult = false;
    private TextView authBlock; // Assuming this TextView is for inputting block number
    private TextView authKey;
    private String TAG="NFC";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Schedule NFC background work
        NfcScheduler.scheduleNfcWork(this);
        setContentView(R.layout.activity_new_custom_nfcactivity);
        statusText = findViewById(R.id.statusText);
        controlButton = findViewById(R.id.controlButton);
        authBlock = findViewById(R.id.authBlock); // Initialize authBlock
        authKey = findViewById(R.id.authKey);
//        auth_keyType = findViewById(R.id.auth_keyType);


        nfcUtil = NfcUtil.getInstance(this);
        initNfc();

        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startFindFlag) {
                    stopFindCardDetection();
                } else {
                    startFindCardDetection();
                }
            }
        });
    }

    private void initNfc() {
        nfcUtil.initSerial();
        statusText.setText("NFC Initialized. Tap a card to start.");
    }

    private void startFindCardDetection() {
        startFindFlag = true;
        findCount = 0;
        controlButton.setText("Stop Detection");
        statusText.setText("Searching for NFC cards...");
        mHandler.post(cardDetectionRunnable);
    }

    private void stopFindCardDetection() {
        startFindFlag = false;
        controlButton.setText("Start Detection");
        statusText.setText("Detection stopped.");
        mHandler.removeCallbacks(cardDetectionRunnable);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
////        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
//
//    }

    private Runnable cardDetectionRunnable = new Runnable() {
        @Override
        public void run() {
            if (startFindFlag) {
                findCount++;
                SelectCardReturn selectCardReturn = nfcUtil.selectCard();
                Log.e(TAG,"CARD NFC: "+selectCardReturn+" =============> ");
                if (selectCardReturn != null) {
                    CardTypeEnum cardTypeEnum = selectCardReturn.getCardType();
                    byte[] cardData = selectCardReturn.getCardNum();
                    if (cardTypeEnum == CardTypeEnum.TYPE_A) {
                        cardData = Arrays.copyOfRange(cardData, 0, cardData.length - 3);
                    }
                    statusText.setText(getString(R.string.nfc_card_type) + "[" + getCardTypeString(cardTypeEnum) + "] " +
                            getString(R.string.nfc_card_no) + "[" + NfcUtil.toHexString(cardData) + "]\nCount: " + findCount);
                    processDetectedCard(cardTypeEnum, cardData);

                } else {
                    statusText.setText("No card detected.\nCount: " + findCount);
                }

                mHandler.postDelayed(this, 200); // Check again after 200ms
            }
        }
    };

    private static String getCardTypeString(CardTypeEnum cardTypeEnum) {
        if(cardTypeEnum == CardTypeEnum.M0){
            return "M0";
        }else if(cardTypeEnum == CardTypeEnum.M1){
            return "M1";
        }else if(cardTypeEnum == CardTypeEnum.ISO15693){
            return "ISO15693";
        }else if(cardTypeEnum == CardTypeEnum.FELICA){
            return "FELICA";
        }else if(cardTypeEnum == CardTypeEnum.ID_CARD){
            return "ID_CARD";
        }else if(cardTypeEnum == CardTypeEnum.TYPE_A){
            return "TYPE_A";
        }else if(cardTypeEnum == CardTypeEnum.TYPE_B){
            return "TYPE_B";
        }
        return null;
    }


    private void processDetectedCard(CardTypeEnum cardTypeEnum, byte[] cardData) {
        String cardType = getCardTypeString(cardTypeEnum);
        String cardNumber = NfcUtil.toHexString(cardData);

        // Update status text with card details
        statusText.setText(getString(R.string.nfc_card_type) + "[" + cardType + "] " +
                getString(R.string.nfc_card_no) + "[" + cardNumber + "]\nCount: " + findCount);

        // Perform additional actions here
        performAdditionalActions(cardTypeEnum, cardData);
    }

    private void performAdditionalActions(CardTypeEnum cardTypeEnum, byte[] cardData) {
        // Add the actions you want to perform after detecting the card
        // Example: Log the card data or update a database
        // Log.d("NFC", "Detected Card Type: " + cardTypeEnum + " | Card Data: " + NfcUtil.toHexString(cardData));

        // Example action: Notify user
        // Toast.makeText(this, "Card Detected: " + NfcUtil.toHexString(cardData), Toast.LENGTH_SHORT).show();
        String cardType = getCardTypeString(cardTypeEnum);
        String cardNumber = NfcUtil.toHexString(cardData);

        // Update status text with card details
        statusText.setText(getString(R.string.nfc_card_type) + "[" + cardType + "] " +
                getString(R.string.nfc_card_no) + "[" + cardNumber + "]\nCount: " + findCount);

        // Perform the necessary actions automatically
        checkVersion();
        AUTHENTICATE();
        if (authResult) { // Assume you store authResult in AUTHENTICATE()
            READ_BLOCK();

            // Optionally, you can check if you want to write before reading all blocks

            readAllBlock();
            WRITE_BLOCK();

        } else {
            statusText.setText(getString(R.string.nfc_authentication_fail));
        }


    }
    private void checkVersion() {
        long startTime = System.currentTimeMillis();
        int result = nfcUtil.checkVersion();
        statusText.setText(result + "\ntime[" + (System.currentTimeMillis() - startTime) + "]");
    }

    private void AUTHENTICATE() {
        block = Integer.parseInt(authBlock.getText().toString());
        byte[] key = NfcUtil.toBytes(authKey.getText().toString());
        long startTime = System.currentTimeMillis();

        try {
            authResult = nfcUtil.M1_AUTHENTICATE(block, keyType, key);
        } catch (WrongParamException e) {
            e.printStackTrace();
        }
        if (authResult) {
            statusText.setText(getString(R.string.nfc_authentication_succeed) + "\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        } else {
            statusText.setText(getString(R.string.nfc_authentication_fail) + "\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        }
    }
    public void auth_keyType(View view){
        if(keyType == 1){
            auth_keyType.setText(getString(R.string.nfc_password_TypeA));
            keyType = 0;
        }else if(keyType == 0){
            auth_keyType.setText(getString(R.string.nfc_password_TypeB));
            keyType = 1;
        }
    }
    private void READ_BLOCK() {
        long startTime = System.currentTimeMillis();
        byte[] readResult = null;
        try {
            readResult = nfcUtil.M1_READ_BLOCK(block);
        } catch (WrongParamException e) {
            e.printStackTrace();
        }
        if (readResult != null) {
            statusText.setText(block + getString(R.string.nfc_sector_data) + "[" + NfcUtil.toHexString(readResult) + "]\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        } else {
            statusText.setText(getString(R.string.nfc_read_fail) + "\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        }
    }

    private void WRITE_BLOCK() {
        byte[] data = NfcUtil.toBytes(statusText.getText().toString());
        long startTime = System.currentTimeMillis();
        boolean writeResult = false;
        try {
            writeResult = nfcUtil.M1_WRITE_BLOCK(block, data);
        } catch (WrongParamException e) {
            e.printStackTrace();
        }
        if (writeResult) {
            statusText.setText(getString(R.string.nfc_write_succeed) + "\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        } else {
            statusText.setText(getString(R.string.nfc_write_fail) + "\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        }
    }

    private void readAllBlock() {
        long startTime = System.currentTimeMillis();
        SelectCardReturn selectCardReturn = nfcUtil.selectCard();
        int successCount = 0;
        if (selectCardReturn != null) {
            for (int i = 0; i < 64; i += 4) {
                try {
                    if (nfcUtil.M1_AUTHENTICATE(i, 0, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF})) {
                        for (int j = i; j < i + 4; j++) {
                            long startTime2 = System.currentTimeMillis();
                            byte[] readData = nfcUtil.M1_READ_BLOCK(i);
                            if (readData != null) {
                                successCount++;
                                statusText.append("block[" + j + "] data[" + NfcUtil.toHexString(readData) + "] time[" + (System.currentTimeMillis() - startTime2) + "]\n");
                            } else {
                                statusText.append("block[" + j + "] read fail\n");
                            }
                        }
                    } else {
                        statusText.append("block[" + i + "] auth fail\n");
                    }
                } catch (WrongParamException e) {
                    e.printStackTrace();
                }
            }
            statusText.append("successCount[" + successCount + "] time[" + (System.currentTimeMillis() - startTime) + "]\n");
        }
    }
    //  E-WALLET INTEGRATION

    @Override
    protected void onStop() {
        super.onStop();
        stopFindCardDetection();
        nfcUtil.destroySerial();
    }
}