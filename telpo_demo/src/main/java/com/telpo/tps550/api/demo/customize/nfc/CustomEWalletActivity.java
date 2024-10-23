package com.telpo.tps550.api.demo.customize.nfc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.common.apiutil.nfc.CardTypeEnum;
import com.common.apiutil.nfc.NfcUtil;
import com.common.apiutil.nfc.exception.NotInitWalletException;
import com.common.apiutil.nfc.exception.ReadWalletMoneyWrongException;
import com.common.apiutil.nfc.exception.WrongParamException;
import com.common.demo.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;

public class CustomEWalletActivity extends BaseActivity {
    private EditText init_money, deal_money;
    private Button wallet_type;
    private TextView show_result;
    private NfcUtil util; // Assuming you have an NfcUtil class to handle NFC operations
    private int block=10 ;
    int dealType = 0;
     String TAG="E-WALLET";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_ewallet);
      initView();
    }


    private void initView() {
        Log.e(TAG,"WALLET INT =========> ");
        util = NfcUtil.getInstance(this);
        wallet_type = (Button) findViewById(R.id.wallet_type1);
        init_money = (EditText) findViewById(R.id.init_money1);
        deal_money = (EditText) findViewById(R.id.deal_money1);
        show_result = (TextView) findViewById(R.id.show_result1);
        // Set onClickListeners
    }
    public void CUSTOM_INIT_WALLET(View view) {
        Log.e(TAG,"WALLET INIT TRIGGERED=========> ");
        int money = Integer.parseInt(init_money.getText().toString());
        long startTime = System.currentTimeMillis();
        boolean initResult = false;
        Log.e(TAG,"WALLET INT DATA: =========> "+money+" time: "+startTime);
        try {
            initResult = util.M1_INIT_WALLET(block, money);
            Log.e(TAG,"WALLET INT RES: =========> "+initResult);
        } catch (WrongParamException e) {
            e.printStackTrace();
            Log.e(TAG,"WALLET INT ERROR: =========> "+e.getMessage());
        }
        if (initResult) {
            show_result.setText("success init " + "\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        } else {
            show_result.setText("init failed"+ "\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        }
    }
    public void dealType(View view){
        if(dealType == 1){
            wallet_type.setText(getResources().getString(R.string.nfc_plus));
            dealType = 0;
        }else if(dealType == 0){
            wallet_type.setText(getResources().getString(R.string.nfc_reduce));
            dealType = 1;
        }
    }

    public void CUSTOM_READ_WALLET(View view) {
        long startTime = System.currentTimeMillis();
        Long money = -1L;
        try {
            money = util.M1_READ_WALLET(block);
            show_result.setText(getString(R.string.nfc_wallet_amount) + "[" + money + "]\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        } catch (WrongParamException | ReadWalletMoneyWrongException | NotInitWalletException e) {
            e.printStackTrace();
            show_result.setText(getString(R.string.nfc_read_wallet_fail) + "\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        }
    }

    public void CUSTOM_WRITE_WALLET(View view) {
        int money = Integer.valueOf(deal_money.getText().toString()); // Example amount to write
        long startTime = System.currentTimeMillis();
        boolean writeResult = false;
        try {
            writeResult = util.M1_WRITE_WALLET(block, dealType, money);
        } catch (WrongParamException e) {
            e.printStackTrace();
        }
        if (writeResult) {
            show_result.setText(getString(R.string.nfc_write_wallet_succeed) + "\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        } else {
            show_result.setText(getString(R.string.nfc_write_wallet_fail) + "\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        }
    }

    public void CUSTOM_TRANSFER(View view) {
        long startTime = System.currentTimeMillis();
        boolean transferResult = false;
        try {
            transferResult = util.M1_TRANSFER(block);
        } catch (WrongParamException e) {
            e.printStackTrace();
        }
        if (transferResult) {
            show_result.setText(getString(R.string.nfc_block_to_cache_succeed) + "\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        } else {
            show_result.setText(getString(R.string.nfc_block_to_cache_fail) + "\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        }
    }

    public void CUSTOM_RESTORE(View view) {
        long startTime = System.currentTimeMillis();
        boolean restoreResult = false;
        try {
            restoreResult = util.M1_RESTORE(block);
        } catch (WrongParamException e) {
            e.printStackTrace();
        }
        if (restoreResult) {
            show_result.setText(getString(R.string.nfc_cache_to_block_succeed) + "\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        } else {
            show_result.setText(getString(R.string.nfc_cache_to_block_fail) + "\ntime[" + (System.currentTimeMillis() - startTime) + "]");
        }
    }

    // Add remaining methods as necessary
    private String getCardTypeString(CardTypeEnum cardTypeEnum) {
        if (cardTypeEnum == CardTypeEnum.M0) {
            return "M0";
        } else if (cardTypeEnum == CardTypeEnum.M1) {
            return "M1";
        } else if (cardTypeEnum == CardTypeEnum.ISO15693) {
            return "ISO15693";
        } else if (cardTypeEnum == CardTypeEnum.FELICA) {
            return "FELICA";
        } else if (cardTypeEnum == CardTypeEnum.ID_CARD) {
            return "ID_CARD";
        } else if (cardTypeEnum == CardTypeEnum.TYPE_A) {
            return "TYPE_A";
        } else if (cardTypeEnum == CardTypeEnum.TYPE_B) {
            return "TYPE_B";
        }
        return null;
    }



}