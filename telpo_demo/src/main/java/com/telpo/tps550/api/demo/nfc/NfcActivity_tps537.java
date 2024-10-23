package com.telpo.tps550.api.demo.nfc;

import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.common.apiutil.nfc.CardTypeEnum;
import com.common.apiutil.nfc.NfcUtil;
import com.common.apiutil.nfc.SelectCardReturn;
import com.common.apiutil.nfc.exception.NotInitWalletException;
import com.common.apiutil.nfc.exception.ReadWalletMoneyWrongException;
import com.common.apiutil.nfc.exception.WrongParamException;
import com.common.demo.R;
import com.common.apiutil.fingerprint.FingerPrint;
import com.telpo.tps550.api.demo.bean.BaseActivity;


public class NfcActivity_tps537 extends BaseActivity {
	
	TextView showResult;
	EditText auth_block, auth_key, write_content, init_money, deal_money;
	Button selectCard, auth_keyType, wallet_type;
	CheckBox loopFindChb;
	int keyType = 0;
	int dealType = 0;
	boolean startFindFlag = false;
	int findCount = 0;
	
	int block = 10;
	
	NfcUtil util;
	
	private Handler mHandler = new Handler();
//	private FindCardThread mFindCardThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.tps537_demo);
		initView();
		util = NfcUtil.getInstance(this);
		
	}
	
	private void initView(){
		showResult = (TextView) findViewById(R.id.showResult);
		selectCard = (Button) findViewById(R.id.selectCard);
		auth_block = (EditText) findViewById(R.id.auth_block);
		auth_keyType = (Button) findViewById(R.id.auth_keyType);
		auth_key = (EditText) findViewById(R.id.auth_key);
		write_content = (EditText) findViewById(R.id.write_content);
		init_money = (EditText) findViewById(R.id.init_money);
		deal_money = (EditText) findViewById(R.id.deal_money);
		wallet_type = (Button) findViewById(R.id.wallet_type);
		loopFindChb = (CheckBox) findViewById(R.id.loopFindChb);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		util.initSerial();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
//		loopStartFlag = false;
		util.destroySerial();
//		System.exit(0);
	}
	
	public void reset(View view){
		FingerPrint.TPS537nfcReset(0);
		FingerPrint.TPS537nfcReset(1);
	}
	
	public void openSerial(View view){
		util.initSerial();
	}
	
	public void closeSerial(View view){
		util.destroySerial();
	}
	
	public void checkVersion(View view){
		long startTime = System.currentTimeMillis();
		int result = util.checkVersion();
		showResult.setText(result+"\ntime["+(System.currentTimeMillis() - startTime)+"]");
	}
	
	byte[] allkey = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, };
	public void readAllBlock(View view){
		long startTime = System.currentTimeMillis();
		SelectCardReturn selectCardReturn = util.selectCard();
		int successCount = 0;
		if(selectCardReturn != null){
			for(int i=0;i<64;i+=4){
				try {
					if(util.M1_AUTHENTICATE(i, 0, allkey)){
						for(int j=i;j<i+4;j++){
							long startTime2 = System.currentTimeMillis();
							byte[] readData = util.M1_READ_BLOCK(i);
							if(readData != null){
								successCount++;
								Log.d("taggg", "block["+j+"] data["+NfcUtil.toHexString(readData)+"] time["+(System.currentTimeMillis() - startTime2)+"]");
							}else{
								Log.d("taggg", "block["+j+"] read fail");
							}
						}
					}else{
						Log.d("taggg", "block["+i+"] auth fail");
					}
				} catch (WrongParamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Log.d("taggg", "successCount["+successCount+"] time["+(System.currentTimeMillis() - startTime)+"]");
		}
	}
	
	public void select(View view){
		if(!startFindFlag){
			startFindView();
//				if(mFindCardThread == null){
//					mFindCardThread = new FindCardThread();
//					mFindCardThread.start();
//				}
		}else{
			stopFindView();
//				if(mFindCardThread != null){
//					mFindCardThread.interrupt();
//					mFindCardThread.start();
//				}
		}

		new Thread(new Runnable() {

			long startTime = 0;
			SelectCardReturn selectCardReturn = null;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(startFindFlag){
					findCount++;
					startTime = System.currentTimeMillis();
					selectCardReturn = util.selectCard();
					runOnUiThread(new Runnable() {
						public void run() {
							// TODO Auto-generated method stub
							if(selectCardReturn != null){
								CardTypeEnum cardTypeEnum = selectCardReturn.getCardType();
								byte[] cardData = selectCardReturn.getCardNum();
								if(cardTypeEnum == CardTypeEnum.TYPE_A){
									cardData = Arrays.copyOfRange(cardData, 0, cardData.length-3);
								}
								showResult.setText(getString(R.string.nfc_card_type) + "["+getCardTypeString(cardTypeEnum)+"] "+ getString(R.string.nfc_card_no) +"["+NfcUtil.toHexString(cardData)+"]\ntime["+(System.currentTimeMillis() - startTime)+"]\n" + "count[" + findCount + "]");
							}else{
								showResult.setText("null\ntime["+(System.currentTimeMillis() - startTime)+"]\n" + "count[" + findCount + "]");
							}

							if(!loopFindChb.isChecked()){
								stopFindView();
							}

						}
					});

					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void startFindView(){
		selectCard.setText(R.string.nfc_loop_stop_find);
		loopFindChb.setEnabled(false);
		findCount = 0;
		startFindFlag = true;
		showResult.setText("");
	}

	private void stopFindView(){
		selectCard.setText(R.string.nfc_loop_start_find);
		loopFindChb.setEnabled(true);
		startFindFlag = false;
	}

//	private class FindCardThread extends Thread{
//		long startTime = System.currentTimeMillis();
//		SelectCardReturn selectCardReturn = util.selectCard();
//		@Override
//		public void run() {
//			super.run();
//			// TODO Auto-generated method stub
//			while(loopStartFlag){
//				startTime = System.currentTimeMillis();
//				selectCardReturn = util.selectCard();
//				runOnUiThread(new Runnable() {
//					public void run() {
//						// TODO Auto-generated method stub
//						if(selectCardReturn != null){
//							CardTypeEnum cardTypeEnum = selectCardReturn.getCardType();
//							byte[] cardData = selectCardReturn.getCardNum();
//							if(cardTypeEnum == CardTypeEnum.TYPE_A){
//								cardData = Arrays.copyOfRange(cardData, 0, cardData.length-3);
//							}
//							showResult.setText(getString(R.string.nfc_card_type) + "["+getCardTypeString(cardTypeEnum)+"] "+ getString(R.string.nfc_card_no) +"["+NfcUtil.toHexString(cardData)+"]\ntime["+(System.currentTimeMillis() - startTime)+"]");
//						}else{
//							showResult.setText("null\ntime["+(System.currentTimeMillis() - startTime)+"]");
//						}
//					}
//				});
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
////					e.printStackTrace();
//					currentThread().interrupt();
//				}
//			}
//		}
//	}

	private static String getCardTypeString(CardTypeEnum cardTypeEnum){
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
	
	public void AUTHENTICATE(View view){
		block = Integer.valueOf(auth_block.getText().toString());
		byte[] key = NfcUtil.toBytes(auth_key.getText().toString());
		long startTime = System.currentTimeMillis();
		boolean authResult = false;
		try {
			authResult = util.M1_AUTHENTICATE(block, keyType, key);
		} catch (WrongParamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(authResult){
			showResult.setText(getString(R.string.nfc_authentication_succeed) + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
			Log.d("tagg", "auth app ---------");
		}else{
			showResult.setText(getString(R.string.nfc_authentication_fail) + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
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
	
	public void READ_BLOCK(View view){
		long startTime = System.currentTimeMillis();
		byte[] readResult = null;
		try {
			readResult = util.M1_READ_BLOCK(block);
		} catch (WrongParamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(readResult != null){
			showResult.setText(block+ getString(R.string.nfc_sector_data) + "["+NfcUtil.toHexString(readResult)+"]\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}else{
			showResult.setText(getString(R.string.nfc_read_fail) + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}
	}
	
	public void WRITE_BLOCK(View view){
		byte[] data = NfcUtil.toBytes(write_content.getText().toString());
		long startTime = System.currentTimeMillis();
		boolean writeResult = false;
		try {
			writeResult = util.M1_WRITE_BLOCK(block, data);
		} catch (WrongParamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(writeResult){
			showResult.setText(getString(R.string.nfc_write_succeed) + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}else{
			showResult.setText(getString(R.string.nfc_write_fail) + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}
	}
	
	public void INIT_WALLET(View view){
		int money = Integer.valueOf(init_money.getText().toString());
		long startTime = System.currentTimeMillis();
		boolean initResult = false;
		try {
			initResult = util.M1_INIT_WALLET(block, money);
		} catch (WrongParamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(initResult){
			showResult.setText(getString(R.string.nfc_init_wallet_succeed) + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}else{
			showResult.setText(getString(R.string.nfc_init_wallet_fail)  + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}
	}
	
	public void READ_WALLET(View view){
		long startTime = System.currentTimeMillis();
		Long money = -1l;
		try {
			money = util.M1_READ_WALLET(block);
			showResult.setText(getString(R.string.nfc_wallet_amount) +"["+money+"]\ntime["+(System.currentTimeMillis() - startTime)+"]");
		} catch (WrongParamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadWalletMoneyWrongException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showResult.setText(getString(R.string.nfc_read_wallet_fail) + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
		} catch (NotInitWalletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showResult.setText(getString(R.string.nfc_init_wallet_fail) + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}
	}
	
	public void WRITE_WALLET(View view){
		int money = Integer.valueOf(deal_money.getText().toString());
		long startTime = System.currentTimeMillis();
		boolean initResult = false;
		try {
			initResult = util.M1_WRITE_WALLET(block, dealType, money);
		} catch (WrongParamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(initResult){
			showResult.setText(getString(R.string.nfc_write_wallet_succeed) + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}else{
			showResult.setText(getString(R.string.nfc_write_wallet_fail) + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
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
	
	public void TRANSFER(View view){
		long startTime = System.currentTimeMillis();
		boolean transferResult = false;
		try {
			transferResult = util.M1_TRANSFER(block);
		} catch (WrongParamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(transferResult){
			showResult.setText(getString(R.string.nfc_block_to_cache_succeed) + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}else{
			showResult.setText(getString(R.string.nfc_block_to_cache_fail) + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}
	}
	
	public void RESTORE(View view){
		long startTime = System.currentTimeMillis();
		boolean restoreResult = false;
		try {
			restoreResult = util.M1_RESTORE(block);
		} catch (WrongParamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(restoreResult){
			showResult.setText(getString(R.string.nfc_cache_to_block_succeed) + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}else{
			showResult.setText(getString(R.string.nfc_cache_to_block_fail) + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}
	}
	
	public void sendapdu(View view){
		long startTime = System.currentTimeMillis();
		byte[] receive = util.sendAPDU(NfcUtil.toBytes("00A4040000"), 1000);
		if(receive != null){
			showResult.setText("receive["+NfcUtil.toHexString(receive)+"]\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}else{
			showResult.setText(getString(R.string.nfc_send_apdu_fail) + "\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}
	}
	
	public void CLOSE_CARD(View view){
		long startTime = System.currentTimeMillis();
		boolean closeResult = util.M1_CLOSE_CARD();
		if(closeResult){
			showResult.setText(getString(R.string.nfc_close_succeed) + "关闭成功\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}else{
			showResult.setText(getString(R.string.nfc_close_fail) + "关闭失败\ntime["+(System.currentTimeMillis() - startTime)+"]");
		}
	}

    private void sleepThread(int delay){
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
