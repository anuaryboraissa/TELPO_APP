package com.telpo.tps550.api.demo;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.common.apiutil.util.ShellUtils;
import com.common.apiutil.util.SystemUtil;
import com.common.demo.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;
import com.telpo.tps550.api.demo.can.Can2DeptActivity;
import com.telpo.tps550.api.demo.can.CanActivity;
import com.telpo.tps550.api.demo.customize.Adapters.HomeListAdapter;
import com.telpo.tps550.api.demo.customize.DefaultActivity;
import com.telpo.tps550.api.demo.customize.DeliveryLocker.LockerControlActivity;
import com.telpo.tps550.api.demo.customize.Models.MyItem;
import com.telpo.tps550.api.demo.customize.PowerManagementActivity;
import com.telpo.tps550.api.demo.customize.Repositories.AppFunctionality;
import com.telpo.tps550.api.demo.customize.WifiTestActivity;
import com.telpo.tps550.api.demo.customize.audio.AudioTestActivity;
import com.telpo.tps550.api.demo.customize.biometric.FingerPrintActivity;
import com.telpo.tps550.api.demo.customize.camera.CameraActivity;
import com.telpo.tps550.api.demo.customize.camera.CustomQRBARCodeScannerActivity;
import com.telpo.tps550.api.demo.customize.camera.LEDIndicatorActivity;
import com.telpo.tps550.api.demo.customize.cards.IDCardActivity;
import com.telpo.tps550.api.demo.customize.io.UsbInterfaceActivity;
import com.telpo.tps550.api.demo.customize.nfc.ActualAndroidNFCActivity;
import com.telpo.tps550.api.demo.customize.nfc.CustomEWalletActivity;
import com.telpo.tps550.api.demo.customize.nfc.CustomNFCActivity;
import com.telpo.tps550.api.demo.customize.nfc.NewCustomNFCActivity;
import com.telpo.tps550.api.demo.customize.print.PrintingActivity;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HomeActivity extends BaseActivity {
    ProgressDialog checkDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        requestPermission();

        //SET LISTVIEW

        ListView listView=findViewById((R.id.listView));

        List<MyItem> items= AppFunctionality.functionalities;

        HomeListAdapter adapter = new HomeListAdapter(this, items);
        listView.setAdapter(adapter);
        checkDialog = new ProgressDialog(HomeActivity.this);
        checkDialog.setTitle(getString(R.string.checkPrinterType));
        checkDialog.setMessage(getText(R.string.watting));
        checkDialog.setCancelable(false);
        initFunction();
    }
    //print
    public void devicePrint(){
//        PrintingActivity
        startActivity(new Intent(HomeActivity.this, PrintingActivity.class));
//        if(!SystemUtil.isInstallServiceApk()){
//            startActivity(new Intent(HomeActivity.this, UsbPrinterActivity.class));
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    checkDialog.dismiss();
//                }
//            });
//        }else{
//            int type = SystemUtil.checkPrinter581(HomeActivity.this);
//            Log.d("printer","printer type = " + type);
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    checkDialog.dismiss();
//                }
//            });
//
//            if(type == SystemUtil.PRINTER_80MM_USB_COMMON || type == SystemUtil.PRINTER_SY581){
//                if(type == SystemUtil.PRINTER_80MM_USB_COMMON){
//                    SystemUtil.setProperty("persist.printer.interface", "usb");
//                }else{
//                    SystemUtil.setProperty("persist.printer.interface", "serial");
//                }
//                startActivity(new Intent(HomeActivity.this, PrinterActivitySY581.class));
//            } else if(type == SystemUtil.PRINTER_PRT_COMMON){
//                startActivity(new Intent(HomeActivity.this, UsbPrinterActivity.class));
//            } else {
//                startActivity(new Intent(HomeActivity.this, PrinterActivity.class));
//            }
//        }
    }

    public void deviceLcd(){
        startActivity(new Intent(HomeActivity.this, LcdActivity.class));
    }

    public void deviceTp(){
        startActivity(new Intent(HomeActivity.this, OnTouchTainActivity.class));
    }


    public void devicePowerManage(){
//        startActivity(new Intent(HomeActivity.this, PowerManageActivity.class));
        startActivity(new Intent(HomeActivity.this, PowerManagementActivity.class));
//        PowerManagementActivity
    }
//    WifiTestActivity
public void deviceWifiCellular(){
    startActivity(new Intent(HomeActivity.this, WifiTestActivity.class));
}
    public void deviceDeliveryLocker(){
//        LockerControlActivity
//        startActivity(new Intent(HomeActivity.this, DeliverylockerActivity.class));
        startActivity(new Intent(HomeActivity.this, LockerControlActivity.class));
    }

    public void devicePowerControl(){
        startActivity(new Intent(HomeActivity.this, PowerControlActivity.class));
    }
    public void deviceSimpleSubLCD(){
        startActivity(new Intent(HomeActivity.this, SimpleSubLcdActivity.class));
    }
    public void deviceSerial(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
        dialog.setTitle(getString(R.string.serial_dialog_title));
        dialog.setMessage(getString(R.string.serial_dialog_msg));
        dialog.setNegativeButton(getString(R.string.serial_dialog_serial), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                // 软解码
                startActivity(new Intent(HomeActivity.this, SerialTestActivity.class));
            }
        });
        dialog.setPositiveButton(getString(R.string.serial_dialog_ttlreader), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                // 硬解码
                startActivity(new Intent(HomeActivity.this, TTLTestActivity.class));
            }
        });
        dialog.show();
    }

    public void deviceCanTest(){
        if (SystemUtil.isInstallServiceApk()) {
            startActivity(new Intent(HomeActivity.this, CanActivity.class));
            return;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
        dialog.setTitle(getString(R.string.can_dialog_title));
        dialog.setMessage(getString(R.string.can_dialog_msg));
        dialog.setNegativeButton(getString(R.string.can_dialog_can_util), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                // 易用版
                startActivity(new Intent(HomeActivity.this, CanActivity.class));
            }
        });
        dialog.setPositiveButton(getString(R.string.can_dialog_can_util2), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                // 专业版
                startActivity(new Intent(HomeActivity.this, Can2DeptActivity.class));
            }
        });
        dialog.show();
    }

   public void deviceOtherTest(){
       startActivity(new Intent(HomeActivity.this, InterfaceActivityMain.class));
   }
    public void deviceComingSoon(){
        startActivity(new Intent(HomeActivity.this, DefaultActivity.class));
    }


    public void deviceSystemTest(){
       startActivity(new Intent(HomeActivity.this, SystemActivity.class));
   }

   public void deviceDigitalTube(){
       startActivity(new Intent(HomeActivity.this, LedScreenActivity.class));
   }
    public void deviceCustomEWallet(){
        startActivity(new Intent(HomeActivity.this, CustomEWalletActivity.class));
    }

   public void deviceMoneyBox(){
       startActivity(new Intent(HomeActivity.this, MoneyBoxActivity.class));
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
   public void deviceQrCodeTest(){
       startActivity(new Intent(HomeActivity.this, CustomQRBARCodeScannerActivity.class));
//       if (!checkPackage("com.telpo.tps550.api")) {
//           // 软解码APP不存在，直接跳转硬解码界面
//           startActivity(new Intent(HomeActivity.this, DecodeReaderActivity.class));
//       } else {
//           // 软解码APP存在，让用户选择解码类型
//           AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
//           dialog.setTitle(getString(R.string.qrcode_dialog_title));
//           dialog.setMessage(getString(R.string.qrcode_dialog_msg));
//           dialog.setNegativeButton(getString(R.string.qrcode_dialog_soft_decoding), new DialogInterface.OnClickListener() {
//
//               public void onClick(DialogInterface dialogInterface, int i) {
//                   // 软解码
////                   CustomQRBARCodeScannerActivity
//
//
////                   Intent intent = new Intent();
////                   intent.setClassName("com.telpo.tps550.api", "com.telpo.tps550.api.barcode.Capture");
////                   try {
////                       startActivityForResult(intent, 0x124);
////                   } catch (ActivityNotFoundException e) {
////                       Toast.makeText(HomeActivity.this, getResources().getString(R.string.identify_fail), Toast.LENGTH_LONG).show();
////                   }
//               }
//           });
//           dialog.setPositiveButton(getString(R.string.qrcode_dialog_hard_decoding), new DialogInterface.OnClickListener() {
//
//               public void onClick(DialogInterface dialogInterface, int i) {
//                   // 硬解码
//                   startActivity(new Intent(HomeActivity.this, DecodeReaderActivity.class));
//               }
//           });
//           dialog.show();
//       }
   }

   public void deviceMagneticCard(){
       startActivity(new Intent(HomeActivity.this, MegneticActivity.class));
   }
   public void devicePcsc(){
       startActivity(new Intent(HomeActivity.this, IccActivityNew.class));
   }

   public void deviceIr(){
       startActivity(new Intent(HomeActivity.this, IrActivity.class));
   }

   public void deviceIdentifyIDCard(){
//               startActivity(new Intent(HomeActivity.this, MultiReaderActivity.class));
               startActivity(new Intent(HomeActivity.this, IDCardActivity.class));
   }

   public void psamCard(){
       startActivity(new Intent(HomeActivity.this, PsamCardActivity.class));
   }
   public void deviceNfc(){
//       AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
//       dialog.setTitle(getString(R.string.nfc_xzgn));
//       dialog.setMessage(getString(R.string.nfc_xznfcsbfs));
//       dialog.setNegativeButton(R.string.nfc_ys, new DialogInterface.OnClickListener() {
//           public void onClick(DialogInterface dialogInterface, int i) {
//               //use camera
//               startActivity(new Intent(HomeActivity.this, ActualAndroidNFCActivity.class));
//           }
//       });
//       dialog.setNeutralButton(R.string.nfc_telpo, new DialogInterface.OnClickListener() {
//           @Override
//           public void onClick(DialogInterface dialog, int which) {
//               startActivity(new Intent(HomeActivity.this, NewCustomNFCActivity.class));
//           }
//       });
//       dialog.setPositiveButton(R.string.nfc_telpo_pn512, new DialogInterface.OnClickListener() {
//           public void onClick(DialogInterface dialogInterface, int i) {
//               startActivity(new Intent(HomeActivity.this, NfcPN512ActivityMain.class));
//           }
//       });
//       dialog.show();
//       startActivity(new Intent(HomeActivity.this, ActualAndroidNFCActivity.class));
       startActivity(new Intent(HomeActivity.this, ActualAndroidNFCActivity.class));

   }

   public void deviceHardReadScan(){
       startActivity(new Intent(HomeActivity.this, DecodeReaderActivity.class));
   }
   public void deviceTemper(){
       startActivity(new Intent(HomeActivity.this, Encrypt_537Activity.class));

   }
    public void requestPermission() {
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

private void initFunction(){
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


    public void deviceCamera() {
        startActivity(new Intent(HomeActivity.this, CameraActivity.class));
    }
    public void deviceUsbTest() {
        startActivity(new Intent(HomeActivity.this, UsbInterfaceActivity.class));
    }
//    UsbInterfaceActivity

    public void deviceAudioTest() {
        startActivity(new Intent(HomeActivity.this, AudioTestActivity.class));
    }

    public void deviceFingerprint() {
        startActivity(new Intent(HomeActivity.this, FingerPrintActivity.class));
    }

    public void deviceLEDIndicator() {
        startActivity(new Intent(HomeActivity.this, LEDIndicatorActivity.class));
    }
}