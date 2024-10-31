package com.telpo.tps550.api.demo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.ListView;

import com.common.apiutil.util.SystemUtil;
import com.common.demo.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;
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
import com.telpo.tps550.api.demo.customize.cards.SIMCardDetectionActivity;
import com.telpo.tps550.api.demo.customize.io.UsbInterfaceActivity;
import com.telpo.tps550.api.demo.customize.nfc.ActualAndroidNFCActivity;
import com.telpo.tps550.api.demo.customize.print.PrintingActivity;
import com.telpo.tps550.api.demo.encrypt.Encrypt_537Activity;
import com.telpo.tps550.api.demo.iccard.IccActivityNew;
import com.telpo.tps550.api.demo.iccard.PsamCardActivity;
import com.telpo.tps550.api.demo.lcd.LcdActivity;
import com.telpo.tps550.api.demo.lcd.SimpleSubLcdActivity;
import com.telpo.tps550.api.demo.ledscreen.LedScreenActivity;
//import com.telpo.tps550.api.demo.megnetic.MegneticActivity;
//import com.telpo.tps550.api.demo.moneybox.MoneyBoxActivity;
//import com.telpo.tps550.api.demo.pos.InterfaceActivityMain;
import com.telpo.tps550.api.demo.power.PowerControlActivity;

import com.telpo.tps550.api.demo.serial.SerialTestActivity;
import com.telpo.tps550.api.demo.serial.TTLTestActivity;
import com.telpo.tps550.api.demo.system.SystemActivity;
import com.telpo.tps550.api.demo.touch.OnTouchTainActivity;

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
    }
    //print
    public void devicePrint(){
        startActivity(new Intent(HomeActivity.this, PrintingActivity.class));
    }

    public void deviceLcd(){
        startActivity(new Intent(HomeActivity.this, LcdActivity.class));
    }

    public void deviceTp(){
        startActivity(new Intent(HomeActivity.this, OnTouchTainActivity.class));
    }


    public void devicePowerManage(){
        startActivity(new Intent(HomeActivity.this, PowerManagementActivity.class));
    }
public void deviceWifiCellular(){
    startActivity(new Intent(HomeActivity.this, WifiTestActivity.class));
}
    public void deviceDeliveryLocker(){
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
//        if (SystemUtil.isInstallServiceApk()) {
//            startActivity(new Intent(HomeActivity.this, CanActivity.class));
//            return;
//        }
//
//        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
//        dialog.setTitle(getString(R.string.can_dialog_title));
//        dialog.setMessage(getString(R.string.can_dialog_msg));
//        dialog.setNegativeButton(getString(R.string.can_dialog_can_util), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialogInterface, int i) {
//                // 易用版
//                startActivity(new Intent(HomeActivity.this, CanActivity.class));
//            }
//        });
//        dialog.setPositiveButton(getString(R.string.can_dialog_can_util2), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialogInterface, int i) {
//                // 专业版
//                startActivity(new Intent(HomeActivity.this, Can2DeptActivity.class));
//            }
//        });
//        dialog.show();
    }

   public void deviceOtherTest(){
//       startActivity(new Intent(HomeActivity.this, InterfaceActivityMain.class));
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

   public void deviceMoneyBox(){
//       startActivity(new Intent(HomeActivity.this, MoneyBoxActivity.class));
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
        //true为安装了，false为未安装
        return packageInfo != null;
    }
   public void deviceQrCodeTest(){
       startActivity(new Intent(HomeActivity.this, CustomQRBARCodeScannerActivity.class));
   }

   public void deviceMagneticCard(){
//       startActivity(new Intent(HomeActivity.this, MegneticActivity.class));
   }
   public void devicePcsc(){
       startActivity(new Intent(HomeActivity.this, IccActivityNew.class));
   }


   public void deviceIdentifyIDCard(){
       AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
       dialog.setTitle(getString(R.string.serial_dialog_title));
       dialog.setMessage(getString(R.string.serial_dialog_msg));
       dialog.setNegativeButton("ID CARD Reader", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialogInterface, int i) {
               // 软解码
               startActivity(new Intent(HomeActivity.this, IDCardActivity.class));
           }
       });
       dialog.setPositiveButton("SIM Cards", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialogInterface, int i) {
               // 硬解码
               startActivity(new Intent(HomeActivity.this, SIMCardDetectionActivity.class));
           }
       });
       dialog.show();
   }

   public void psamCard(){
       startActivity(new Intent(HomeActivity.this, PsamCardActivity.class));
   }
   public void deviceNfc(){
       startActivity(new Intent(HomeActivity.this, ActualAndroidNFCActivity.class));
   }

   public void deviceTemper(){
       startActivity(new Intent(HomeActivity.this, Encrypt_537Activity.class));

   }
    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
            }else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1011);
            }
        }
    }



    public void deviceCamera() {
        startActivity(new Intent(HomeActivity.this, CameraActivity.class));
    }
    public void deviceUsbTest() {
        startActivity(new Intent(HomeActivity.this, UsbInterfaceActivity.class));
    }

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