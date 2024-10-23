package com.telpo.tps550.api.demo.customize.Adapters;// MyListAdapter.java

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.common.demo.R;
import com.telpo.tps550.api.demo.HomeActivity;
import com.telpo.tps550.api.demo.customize.Models.MyItem;

import java.util.List;

public class HomeListAdapter extends ArrayAdapter<MyItem> {
    private final HomeActivity context;
    private final List<MyItem> items;

    public HomeListAdapter(HomeActivity context, List<MyItem> items) {
        super(context, R.layout.list_item, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView title = convertView.findViewById(R.id.textViewTitle);
        TextView description = convertView.findViewById(R.id.textViewDescription);
        ImageView imageView =convertView.findViewById(R.id.imageViewLeading);
        MyItem item = items.get(position);
        title.setText(item.getTitle());
        description.setText(item.getDescription());
        imageView.setImageResource(item.getResId());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Clicked: " + item.getTitle()+" ID: "+item.getId(), Toast.LENGTH_SHORT).show();
                    Intent intent;
                    switch (item.getId()){
                        case 1:
                            context.devicePrint();
                            break;
                        case 2:
                            context.deviceNfc();
                            break;
                        case 3:
                            context.deviceQrCodeTest();
                            break;
                        case 5:
                            context.deviceCustomEWallet();
                            break;
                        case 6:
                            context.deviceCamera();
                            break;
                        case 7:
                            context.deviceLEDIndicator();
                            break;
                        case 8:
                            context.deviceOtherTest();
                            break;
                        case 9:
                            context.deviceAudioTest();
                            break;
                        case 10:
                            context.deviceFingerprint();
                            break;
                        case 12:
                            context.deviceLcd();
                            break;
                        case 13:
                            context.deviceTp();
                            break;
                        case 14:
                            context.deviceMagneticCard();
                            break;
                        case 15:
                            context.deviceMoneyBox();
                            break;
                        case 16:
                            context.deviceIdentifyIDCard();
                            break;
                        case 17:
                            context.devicePcsc();
                            break;
                        case 18:
                            context.psamCard();
                            break;
                        case 19:
                            context.deviceTemper();
                            break;
                        case 21:
                            context.deviceDigitalTube();
                            break;
                        case 22:
                            context.deviceSystemTest();
                            break;
                        case 23:
                            context.deviceCanTest();
                            break;
                        case 24:
                            context.deviceSerial();
                            break;
                        case 25:
                            context.deviceSimpleSubLCD();
                            break;
                        case 26:
                            context.devicePowerControl();
                            break;
                        case 27:
                            context.deviceDeliveryLocker();
                            break;
                        case 28:
                            context.devicePowerManage();
                            break;
                        case 29:
                            context.deviceWifiCellular();
                            break;
                        case 30:
                            context.deviceUsbTest();
                            break;
                        default:
                            context.deviceComingSoon();
                            break;
                    }
                    // Pass data to SecondActivity
                }
                // Handle the click event


        });
        return convertView;
    }


}
