package com.telpo.tps550.api.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.common.apiutil.util.ShellUtils;
import com.common.apiutil.util.SystemUtil;
import com.common.demo.R;
import com.google.zxing.other.BeepManager;
import com.telpo.tps550.api.demo.bean.BaseActivity;
import com.telpo.tps550.api.demo.can.Can2DeptActivity;
import com.telpo.tps550.api.demo.can.CanActivity;
import com.telpo.tps550.api.demo.decode.DecodeReaderActivity;
import com.telpo.tps550.api.demo.deliverylocker.DeliverylockerActivity;
import com.telpo.tps550.api.demo.encrypt.Encrypt_537Activity;

import com.telpo.tps550.api.demo.iccard.IccActivityNew;

import com.telpo.tps550.api.demo.iccard.PsamCardActivity;
import com.telpo.tps550.api.demo.ir.IrActivity;

import com.telpo.tps550.api.demo.lcd.LcdActivity;
import com.telpo.tps550.api.demo.lcd.SimpleSubLcdActivity;
import com.telpo.tps550.api.demo.ledscreen.LedScreenActivity;
import com.telpo.tps550.api.demo.megnetic.MegneticActivity;
import com.telpo.tps550.api.demo.moneybox.MoneyBoxActivity;
import com.telpo.tps550.api.demo.multireader.MultiReaderActivity;
import com.telpo.tps550.api.demo.nfc.NFCActivityNew;
import com.telpo.tps550.api.demo.nfc.NfcActivity_tps537;

import com.telpo.tps550.api.demo.nfc.NfcPN512ActivityMain;
import com.telpo.tps550.api.demo.pos.InterfaceActivityMain;
import com.telpo.tps550.api.demo.power.PowerControlActivity;
import com.telpo.tps550.api.demo.power.PowerManageActivity;
import com.telpo.tps550.api.demo.printer.PrinterActivity;
import com.telpo.tps550.api.demo.printer.PrinterActivitySY581;
import com.telpo.tps550.api.demo.printer.UsbPrinterActivity;
import com.telpo.tps550.api.demo.serial.SerialTestActivity;
import com.telpo.tps550.api.demo.serial.TTLTestActivity;
import com.telpo.tps550.api.demo.system.SystemActivity;
import com.telpo.tps550.api.demo.touch.OnTouchTainActivity;

public class MainActivity extends BaseActivity {
	private static final String TAG = "TELPO_SDK";
	private int Oriental = -1;
	private Button BnPrint, BnQRCode, psambtn, magneticCardBtn, rfidBtn, pcscBtn, identifyBtn, 
	               moneybox, irbtn, ledbtn, decodebtn, nfcbtn, hardreaderbtn, tamperBtn, digitaltubeBtn,
			       otherApiBtn,systemApiBtn, canTestBtn,serialBtn,simpleSubledBtn,powerControlBtn,deliverylockerBtn,lcdBtn,tpBtn,powerManagerBtn;
	private TextView versionTv;
//	private Switch otgSwitchBtn;
	private BeepManager mBeepManager;
	private static int printerCheck = -1;
	private static final String FILE_NAME = "/sdcard/tpsdk/printerVersion.txt";
	ProgressDialog checkDialog;
	
	boolean isreading;

	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		isreading = false;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_home_main);
		requestPermission();

		BnPrint = (Button) findViewById(R.id.print_test);
		BnQRCode = (Button) findViewById(R.id.qrcode_verify);
		magneticCardBtn = (Button) findViewById(R.id.magnetic_card_btn);
		rfidBtn = (Button) findViewById(R.id.rfid_btn);
		pcscBtn = (Button) findViewById(R.id.pcsc_btn);
		identifyBtn = (Button) findViewById(R.id.identity_btn);
		moneybox = (Button) findViewById(R.id.moneybox_btn);
		irbtn = (Button) findViewById(R.id.ir_btn);
		ledbtn = (Button) findViewById(R.id.led_btn);
		psambtn = (Button) findViewById(R.id.psam);
		decodebtn = (Button) findViewById(R.id.decode_btn);
		nfcbtn = (Button) findViewById(R.id.nfc_btn);
		mBeepManager = new BeepManager(this, R.raw.beep);
		hardreaderbtn = (Button) findViewById(R.id.hardreader_btn);
		tamperBtn = (Button) findViewById(R.id.tamper_btn);
		digitaltubeBtn = (Button) findViewById(R.id.digitaltube_btn);
		otherApiBtn = (Button) findViewById(R.id.other_api_btn);
		systemApiBtn = (Button) findViewById(R.id.system_api_btn);
		canTestBtn = (Button) findViewById(R.id.can_test_btn);
		serialBtn = (Button) findViewById(R.id.serial_btn);
		simpleSubledBtn = findViewById(R.id.simple_subled_btn);
		powerControlBtn = findViewById(R.id.power_control_btn);
		deliverylockerBtn = findViewById(R.id.deliverylocker_btn);
		lcdBtn = findViewById(R.id.lcd_btn);
		tpBtn = findViewById(R.id.tp_btn);
		powerManagerBtn = findViewById(R.id.powerManager_btn);

		versionTv = findViewById(R.id.tv_version);
		versionTv.setVisibility(View.VISIBLE);
		versionTv.setText("telpoLib_v2.14.20231218");

		checkDialog = new ProgressDialog(MainActivity.this);
		checkDialog.setTitle(getString(R.string.checkPrinterType));
		checkDialog.setMessage(getText(R.string.watting));
		checkDialog.setCancelable(false);

		setFunctions(MyApplication.getConfig());

		//C1Pro专业OTG
//		otgSwitchBtn = findViewById(R.id.otgSwitchBtn);
//		ShellUtils.execCommand("echo host > /sys/class/extcon/extcon3/role", false);
//		otgSwitchBtn.setChecked(true);
//
//		otgSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//										 boolean isChecked) {
//				// TODO Auto-generated method stub
//				String error = null;
//				if(isChecked){
//					error = ShellUtils.execCommand("echo host > /sys/class/extcon/extcon3/role", false).errorMsg;
//				}else{
//					error = ShellUtils.execCommand("echo devices > /sys/class/extcon/extcon3/role", false).errorMsg;
//				}
//				Log.d("tagg","otg switch :" + error);
//			}
//		});

		//液晶屏测试
		lcdBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				startActivity(new Intent(MainActivity.this, LcdActivity.class));
			}
		});
		//触摸屏测试
		tpBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, OnTouchTainActivity.class));
			}
		});

		//电源管理
		powerManagerBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, PowerManageActivity.class));
			}
		});

		//自提柜控制
		deliverylockerBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, DeliverylockerActivity.class));
			}
		});

		// 上电控制
		powerControlBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, PowerControlActivity.class));
			}
		});

		//简单小副屏测试
		simpleSubledBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, SimpleSubLcdActivity.class));
			}
		});

		//串口测试
		serialBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle(getString(R.string.serial_dialog_title));
				dialog.setMessage(getString(R.string.serial_dialog_msg));
				dialog.setNegativeButton(getString(R.string.serial_dialog_serial), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						// 软解码
						startActivity(new Intent(MainActivity.this, SerialTestActivity.class));
					}
				});
				dialog.setPositiveButton(getString(R.string.serial_dialog_ttlreader), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						// 硬解码
						startActivity(new Intent(MainActivity.this, TTLTestActivity.class));
					}
				});
				dialog.show();
			}
		});

		//can总线测试
		canTestBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (SystemUtil.isInstallServiceApk()) {
					startActivity(new Intent(MainActivity.this, CanActivity.class));
					return;
				}

				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle(getString(R.string.can_dialog_title));
				dialog.setMessage(getString(R.string.can_dialog_msg));
				dialog.setNegativeButton(getString(R.string.can_dialog_can_util), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						// 易用版
						startActivity(new Intent(MainActivity.this, CanActivity.class));
					}
				});
				dialog.setPositiveButton(getString(R.string.can_dialog_can_util2), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						// 专业版
						startActivity(new Intent(MainActivity.this, Can2DeptActivity.class));
					}
				});
				dialog.show();
			}
		});

		//其他接口测试
		otherApiBtn.setOnClickListener(new OnClickListener() {
		   @Override
		   public void onClick(View view) {
				   startActivity(new Intent(MainActivity.this, InterfaceActivityMain.class));
		   }
	   	});

		systemApiBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, SystemActivity.class));
			}
		});

		digitaltubeBtn.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this, LedScreenActivity.class));
			}
		});
		
		//MoneyBox
		moneybox.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this, MoneyBoxActivity.class));
			}
		});

		//Barcode And Qrcode
		BnQRCode.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {

				if (!checkPackage("com.telpo.tps550.api")) {
					// 软解码APP不存在，直接跳转硬解码界面
					startActivity(new Intent(MainActivity.this, DecodeReaderActivity.class));
				} else {
					// 软解码APP存在，让用户选择解码类型
					AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
					dialog.setTitle(getString(R.string.qrcode_dialog_title));
					dialog.setMessage(getString(R.string.qrcode_dialog_msg));
					dialog.setNegativeButton(getString(R.string.qrcode_dialog_soft_decoding), new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialogInterface, int i) {
							// 软解码
							Intent intent = new Intent();
							intent.setClassName("com.telpo.tps550.api", "com.telpo.tps550.api.barcode.Capture");
							try {
								startActivityForResult(intent, 0x124);
							} catch (ActivityNotFoundException e) {
								Toast.makeText(MainActivity.this, getResources().getString(R.string.identify_fail), Toast.LENGTH_LONG).show();
							}
						}
					});
					dialog.setPositiveButton(getString(R.string.qrcode_dialog_hard_decoding), new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialogInterface, int i) {
							// 硬解码
							startActivity(new Intent(MainActivity.this, DecodeReaderActivity.class));
						}
					});
					dialog.show();
				}
			}
		});

		//Print
		BnPrint.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				checkDialog.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						if(!SystemUtil.isInstallServiceApk()){
							startActivity(new Intent(MainActivity.this, UsbPrinterActivity.class));
							runOnUiThread(new Runnable() {
								public void run() {
									checkDialog.dismiss();
								}
							});
						}else{
							int type = SystemUtil.checkPrinter581(MainActivity.this);
							Log.d("printer","printer type = " + type);
							runOnUiThread(new Runnable() {
								public void run() {
									checkDialog.dismiss();
								}
							});

							if(type == SystemUtil.PRINTER_80MM_USB_COMMON || type == SystemUtil.PRINTER_SY581){
								if(type == SystemUtil.PRINTER_80MM_USB_COMMON){
									SystemUtil.setProperty("persist.printer.interface", "usb");
								}else{
									SystemUtil.setProperty("persist.printer.interface", "serial");
								}
								startActivity(new Intent(MainActivity.this, PrinterActivitySY581.class));
							} else if(type == SystemUtil.PRINTER_PRT_COMMON){
								startActivity(new Intent(MainActivity.this, UsbPrinterActivity.class));
							} else {
								startActivity(new Intent(MainActivity.this, PrinterActivity.class));
							}
						}
					}
				}).start();

//				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
//				dialog.setTitle(getString(R.string.printer_type_select));
//				dialog.setNegativeButton(getString(R.string.printer_type_80mm), new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialogInterface, int i) {
//						if(printerCheck == 9){
//							showSelectPrinterInterfaceDialog();
//						}else{
//							if (deviceType == StringUtil.DeviceModelEnum.TPS650.ordinal()
//									&& !(Build.MODEL.equals("MTDP-618A") || Build.MODEL.equals("TPS650M"))) {
//								if(getFileContent(FILE_NAME) != null && getFileContent(FILE_NAME).contains("SY581")) {
//									startActivity(new Intent(MainActivity.this, PrinterActivitySY581.class));
//								}else {
//									startActivity(new Intent(MainActivity.this, PrinterActivity.class));
//								}
//							}else if((deviceType == StringUtil.DeviceModelEnum.TPS650T.ordinal() && SystemUtil.tps650t_is_sy581) ||
//									deviceType == StringUtil.DeviceModelEnum.TPS680.ordinal() ||
//									deviceType == StringUtil.DeviceModelEnum.C1B.ordinal() ||
//									deviceType == StringUtil.DeviceModelEnum.TPS650P.ordinal() ||
//									"C1".equals(SystemUtil.getInternalModel()) || "C1P".equals(SystemUtil.getInternalModel())){
//								if(deviceType == StringUtil.DeviceModelEnum.C1B.ordinal()){
//									checkDialog.show();
//									new Thread(new Runnable() {
//										@Override
//										public void run() {
//											// TODO Auto-generated method stub
//											SystemUtil.checkPrinter581(MainActivity.this);
//											runOnUiThread(new Runnable() {
//												@Override
//												public void run() {
//													// TODO Auto-generated method stub
//													checkDialog.dismiss();
//													startActivity(new Intent(MainActivity.this, PrinterActivitySY581.class));
//												}
//											});
//										}
//									}).start();
//								}else{
//									startActivity(new Intent(MainActivity.this, PrinterActivitySY581.class));
//								}
//							}else{
//								startActivity(new Intent(MainActivity.this, PrinterActivity.class));
//							}
//						}
//					}
//				});
//				dialog.setPositiveButton(getString(R.string.printer_type_58mm), new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialogInterface, int i) {
//						startActivity(new Intent(MainActivity.this, UsbPrinterActivity.class));
////						startActivity(new Intent(MainActivity.this, UsbPrintTest.class));
//
//					}
//				});
//				dialog.show();
			}
		});
		
		//Magnetic Card
		magneticCardBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				startActivity(new Intent(MainActivity.this, MegneticActivity.class));
			}
		});
		
//		//RFID
//		rfidBtn.setOnClickListener(new View.OnClickListener() {
//
//			public void onClick(View view) {
//				startActivity(new Intent(MainActivity.this, RfidActivity.class));
//			}
//		});

		//IC Card
		pcscBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, IccActivityNew.class));
			}
		});

		//IR
		irbtn.setOnClickListener(new OnClickListener() {

			
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this, IrActivity.class));
			}
		});
		
		//Led
//		ledbtn.setOnClickListener(new OnClickListener() {
//
//
//			public void onClick(View arg0) {
//				startActivity(new Intent(MainActivity.this, LedActivity.class));
//			}
//		});

		//ID Card
		identifyBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
//				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
//				dialog.setTitle(getString(R.string.idcard_xzgn));
//				dialog.setMessage(getString(R.string.idcard_xzsfsbfs));
//
//				dialog.setNegativeButton(getString(R.string.idcard_sxtsb), new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialogInterface, int i) {
//						//use camera
////						startActivity(new Intent(MainActivity.this, OcrIdCardActivity.class));
//					}
//				});
//				dialog.setPositiveButton(getString(R.string.idcard_dkqsb), new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialogInterface, int i) {
//						//use ID Card reader
//						//startActivity(new Intent(MainActivity.this, IdCardActivity.class));
//						AlertDialog.Builder idcard_dialog = new AlertDialog.Builder(MainActivity.this);
//						idcard_dialog.setTitle(getString(R.string.idcard_xzgn));
//						idcard_dialog.setMessage(getString(R.string.idcard_xzsfsbfs));
//						idcard_dialog.setNegativeButton(getString(R.string.idcard_hqsfzxx), new DialogInterface.OnClickListener() {
//
//
//							public void onClick(DialogInterface dialog, int which) {
//								// TODO Auto-generated method stub
//								startActivity(new Intent(MainActivity.this, /*IdCardActivity*/TwoInOneReaderActivity.class));
//							}
//						});
//						idcard_dialog.setPositiveButton(getString(R.string.idcard_bt_hqsfzxx), new DialogInterface.OnClickListener() {
//
//
//							public void onClick(DialogInterface dialog, int which) {
//								// TODO Auto-generated method stub
////								startActivity(new Intent(MainActivity.this, BluetoothIdCardActivity.class));
//							}
//						});
//						idcard_dialog.show();
//					}
//				});
//				dialog.show();

				startActivity(new Intent(MainActivity.this, MultiReaderActivity.class));

			}

		});
		
		//PSAM Card
		psambtn.setOnClickListener(new OnClickListener() {

			
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this, PsamCardActivity.class));
			}
		});		

		//laser qrcode
		decodebtn.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
//				startActivity(new Intent(MainActivity.this, DecodeActivity.class));
			}
		});

		//NFC
		nfcbtn.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle(getString(R.string.nfc_xzgn));
				dialog.setMessage(getString(R.string.nfc_xznfcsbfs));
				dialog.setNegativeButton(R.string.nfc_ys, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						//use camera
						startActivity(new Intent(MainActivity.this, NFCActivityNew.class));
					}
				});
				dialog.setNeutralButton(R.string.nfc_telpo, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(MainActivity.this, NfcActivity_tps537.class));
					}
				});
				dialog.setPositiveButton(R.string.nfc_telpo_pn512, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						startActivity(new Intent(MainActivity.this, NfcPN512ActivityMain.class));
					}
				});
				dialog.show();

				//File device = new File("/dev/pn544");
				//File device2 = new File("/dev/nq-nci");
				//File device3 = new File("dev/nxpnfc");
				//if (device.exists() || device2.exists() || device3.exists()) {
				//	startActivity(new Intent(MainActivity.this, NfcActivity.class));
				//} else if(!SystemUtil.isInstallServiceApk() || SystemUtil.isPN512NFC()){
				//	startActivity(new Intent(MainActivity.this, NfcPN512ActivityMain.class));
				//} else {
				//	startActivity(new Intent(MainActivity.this, NfcActivity_tps537.class));
				//}

//				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
//				dialog.setTitle(getString(R.string.nfc_xzgn));
//				dialog.setMessage(getString(R.string.nfc_xznfcsbfs));

//				dialog.setNegativeButton(getString(R.string.nfc_ys), new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialogInterface, int i) {
//						//use camera
//						startActivity(new Intent(MainActivity.this, NfcActivity.class));
//					}
//				});
//				dialog.setPositiveButton(getString(R.string.nfc_telpo), new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialogInterface, int i) {
//						if(!SystemUtil.isInstallServiceApk() || SystemUtil.isPN512NFC()){
//							startActivity(new Intent(MainActivity.this, NfcActivity_tps900.class));
//						}else {
//							startActivity(new Intent(MainActivity.this, NfcActivity_tps537.class));
//						}
//					}
//				});
//				dialog.show();


//			    if(deviceType == StringUtil.DeviceModelEnum.TPS900.ordinal() &&
//			    		!"TPS360S".equals(SystemUtil.getInternalModel())  &&
//			    		!"S10A".equals(SystemUtil.getInternalModel())){
//			        startActivity(new Intent(MainActivity.this, NfcActivity_tps900.class));
//			    }else if("D2".equals(SystemUtil.getInternalModel()) || "C20".equals(SystemUtil.getInternalModel()) || "T20".equals(SystemUtil.getInternalModel())){
//			        startActivity(new Intent(MainActivity.this, NfcActivity_tps537.class));
//			    }else{
//			        startActivity(new Intent(MainActivity.this, NfcActivity.class));
//			    }
			}
		});
		
		//硬读头 hardreade Scan
		hardreaderbtn.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, DecodeReaderActivity.class));
			}
		});
		
		//防拆传感器
		tamperBtn.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, Encrypt_537Activity.class));
			}
		});

//		int deviceType = SystemUtil.getDeviceType();
//		if(deviceType == StringUtil.DeviceModelEnum.TPS650.ordinal() ||
//				deviceType == StringUtil.DeviceModelEnum.TPS650T.ordinal() ||
//				"C1".equals(SystemUtil.getInternalModel()) || "C1P".equals(SystemUtil.getInternalModel()) ||
//				"C2".equals(SystemUtil.getInternalModel()) ||
//				deviceType == StringUtil.DeviceModelEnum.C1B.ordinal() ||
//				deviceType == StringUtil.DeviceModelEnum.TPS680.ordinal()) {
//			checkDialog.show();
//
//			new Thread(new Runnable() {
//
//				public void run() {
//					// TODO Auto-generated method stub
//					printerCheck = SystemUtil.checkPrinter581(MainActivity.this);
//					Log.d(TAG, "check read:"+getFileContent(FILE_NAME));
//					runOnUiThread(new Runnable() {
//						public void run() {
//							checkDialog.dismiss();
//						}
//					});
//				}
//			}).start();
//		}

		ShellUtils.CommandResult result = ShellUtils.execCommand("echo on >/sys/class/gpio-ctrl/prt_pwr/ctrl", false);
		Log.d("tagg", "/sys/class/gpio-ctrl/prt_pwr/ctrl result = " + result.result + " [" + result.successMsg + "] [" + result.errorMsg + "]");
		try {
			UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
			HashMap<String, UsbDevice> deviceHashMap = mUsbManager.getDeviceList();
			Iterator<UsbDevice> iterator = deviceHashMap.values().iterator();
			while (iterator.hasNext()) {
				UsbDevice usbDevice = iterator.next();
				int pid = usbDevice.getProductId();
				int vid = usbDevice.getVendorId();

				if (pid == 0x028d && vid == 0x28e9){
					Log.d("tagg", "pid="+pid+" vid="+vid);
					if (mUsbManager.hasPermission(usbDevice)) {
						// Do something with the device
					} else {
						PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent("com.android.usb.USB_PERMISSION"), PendingIntent.FLAG_IMMUTABLE);
						mUsbManager.requestPermission(usbDevice, mPermissionIntent);
					}
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

    private void showSelectPrinterInterfaceDialog(){
    	AlertDialog.Builder selectInterfaceDialog = new AlertDialog.Builder(MainActivity.this);
    	selectInterfaceDialog.setTitle("请选择使用USB方式或串口方式");
    	selectInterfaceDialog.setNegativeButton("USB", new DialogInterface.OnClickListener() {

    		public void onClick(DialogInterface dialogInterface, int i) {
    			SystemUtil.setProperty("persist.printer.interface", "usb");
    			startActivity(new Intent(MainActivity.this, PrinterActivitySY581.class));
    		}
    	});
    	selectInterfaceDialog.setPositiveButton("串口", new DialogInterface.OnClickListener() {

    		public void onClick(DialogInterface dialogInterface, int i) {
    			SystemUtil.setProperty("persist.printer.interface", "serial");
            	startActivity(new Intent(MainActivity.this, PrinterActivitySY581.class));
            }
       });
       selectInterfaceDialog.show();
    }

//	private boolean checkPackage(String packageName) {
//		PackageManager manager = this.getPackageManager();
//		Intent intent = new Intent().setPackage(packageName);
//		List<ResolveInfo> infos = manager.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
//		if (infos == null || infos.size() < 1) {
//			return false;
//		}
//		return true;
//	}

	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0x124) {
			if (resultCode == 0) {
				if (data != null) {
					try {
						mBeepManager.playBeepSoundAndVibrate();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					String qrcode = data.getStringExtra("qrCode");
					//change(qrcode);
					Toast.makeText(MainActivity.this, "Scan result:" + qrcode, Toast.LENGTH_LONG).show();
					return;
				}
			} else {
				Toast.makeText(MainActivity.this, "Scan Failed", Toast.LENGTH_LONG).show();
			}
			
		}
	}

	
	@SuppressLint("WrongConstant")
    protected void onResume() {
		super.onResume();
		setRequestedOrientation(Oriental);
		
		
	}
	
	private static void openAirplaneModeOn(Context context,boolean enabling) {  
	    
		Settings.Global.putInt(context.getContentResolver(),  
		                     Settings.Global.AIRPLANE_MODE_ON,enabling ? 1 : 0);  
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);  
		intent.putExtra("state", enabling);  
		context.sendBroadcast(intent);

	}

	
	protected void onDestroy() {
		super.onDestroy();
		mBeepManager.close();
		mBeepManager = null;
		System.exit(0);
	}
	
	private static String getFileContent(String file_name) {    
        String filePath = file_name;
        String fileContent = null;
        try {    
            File file = new File(filePath);    
            if (file.isFile() && file.exists()) {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file));    
                BufferedReader br = new BufferedReader(isr);    
                String lineTxt = null;    
                while ((lineTxt = br.readLine()) != null) {  
                	fileContent = lineTxt; 
                }
                isr.close();    
                br.close();    
            }else {
                Log.e(TAG, "can not find file");
                file.createNewFile();
            }    
        } catch (Exception e) {    
            e.printStackTrace();    
        }    
        return fileContent;    
    }
	
	public static Bitmap getLoacalBitmap(String url) {
        if (url != null) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(url);
                return BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } finally {
                if(fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fis = null;
                }
            }
        } else {
            return null;
        }
    }
	
	String[] portNum = new String[20];
	String[] productNum = new String[20];
	String[] readerNum = new String[4];

	public void setFunctions(int[] configure){
		BnPrint.setVisibility(View.GONE);
		BnQRCode.setVisibility(View.GONE);
		magneticCardBtn.setVisibility(View.GONE);
		rfidBtn.setVisibility(View.GONE);
		pcscBtn.setVisibility(View.GONE);
		identifyBtn.setVisibility(View.GONE);
		moneybox.setVisibility(View.GONE);
		irbtn.setVisibility(View.GONE);
		ledbtn.setVisibility(View.GONE);
		psambtn.setVisibility(View.GONE);
		decodebtn.setVisibility(View.GONE);
		nfcbtn.setVisibility(View.GONE);
		hardreaderbtn.setVisibility(View.GONE);
		tamperBtn.setVisibility(View.GONE);
		digitaltubeBtn.setVisibility(View.GONE);
		otherApiBtn.setVisibility(View.GONE);
		systemApiBtn.setVisibility(View.GONE);
		canTestBtn.setVisibility(View.GONE);
		serialBtn.setVisibility(View.GONE);
		simpleSubledBtn.setVisibility(View.GONE);
		deliverylockerBtn.setVisibility(View.GONE);
		powerControlBtn.setVisibility(View.GONE);

		for(int i=0; i<configure.length;i++){
			switch (configure[i]){
				case 1:
					BnPrint.setVisibility(View.VISIBLE);
					break;
				case 2:
					BnQRCode.setVisibility(View.VISIBLE);
					break;
				case 3:
					magneticCardBtn.setVisibility(View.VISIBLE);
					break;
				case 4:
					moneybox.setVisibility(View.VISIBLE);
					break;
				case 5:
					identifyBtn.setVisibility(View.VISIBLE);
				case 6:
					pcscBtn.setVisibility(View.VISIBLE);
					break;
				case 7:
					nfcbtn.setVisibility(View.VISIBLE);
					break;
				case 8:
					psambtn.setVisibility(View.VISIBLE);
					break;
				case 9:
					tamperBtn.setVisibility(View.VISIBLE);
					break;
				case 10:
					otherApiBtn.setVisibility(View.VISIBLE);
					break;
				case 11:
					otherApiBtn.setVisibility(View.VISIBLE);
					break;
				case 12:
					otherApiBtn.setVisibility(View.VISIBLE);
					break;
				case 13:
					otherApiBtn.setVisibility(View.VISIBLE);
					break;
				case 14:
					digitaltubeBtn.setVisibility(View.VISIBLE);
					break;
				case 15:
					systemApiBtn.setVisibility(View.VISIBLE);
					break;
				case 16:
					canTestBtn.setVisibility(View.VISIBLE);
					break;
				case 17:
					otherApiBtn.setVisibility(View.VISIBLE);
					break;
				case 18:
					serialBtn.setVisibility(View.VISIBLE);
					break;
				case 19:
					simpleSubledBtn.setVisibility(View.VISIBLE);
					break;
				case 20:
					powerControlBtn.setVisibility(View.VISIBLE);
					break;
				case 21:
					deliverylockerBtn.setVisibility(View.VISIBLE);
					break;
				case 22:
					otherApiBtn.setVisibility(View.VISIBLE);
			}

		}
	}

	private boolean checkPackage(String pkgName) {
		if (pkgName == null || pkgName.isEmpty()) {
			return false;
		}
		PackageInfo packageInfo;
		try {
			packageInfo = getPackageManager().getPackageInfo(pkgName, 0);
		} catch (PackageManager.NameNotFoundException e) {
			packageInfo = null;
//            e.printStackTrace();
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;//true为安装了，false为未安装
		}
	}

	/**
	 * 动态申请权限
	 */
	private void requestPermission() {
		//Android 11需要申请所有文件读写权限
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			//是否有所有问读写权限
			if (Environment.isExternalStorageManager()) {
				//有所有文件读写权限  TODO something
			}else {
				//跳转到打开权限页面
				Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
				intent.setData(Uri.parse("package:" + getPackageName()));
				startActivityForResult(intent, 1011);
			}
		}
	}
}
