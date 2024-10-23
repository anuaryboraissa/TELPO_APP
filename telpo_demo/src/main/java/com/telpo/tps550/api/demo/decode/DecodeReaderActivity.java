package com.telpo.tps550.api.demo.decode;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.common.apiutil.ResultCode;
import com.common.apiutil.decode.DecodeReader;
import com.common.callback.IDecodeReaderListener;
import com.common.demo.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class DecodeReaderActivity extends BaseActivity implements KeyEventResolver.OnScanSuccessListener {

    private DecodeReader mDecodeReader;
    private TextView tvDataShow, circleCountShow;
    private Button openBtn;
    private Button closeBtn;
    private int successCount = 0;

    private ArrayAdapter<String> mCharsetAdapter;
    private ArrayAdapter<Integer> mBaudAdapter;
    private int mCharsetIndex = -1;
    private int mBaudIndex = -1;

    private KeyEventResolver mKeyEventResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_decodereader);

        initView();
    }

    private void initView() {

        tvDataShow = (TextView) findViewById(R.id.data_show);
        tvDataShow.setInputType(0);
        tvDataShow.setSingleLine(false);
        tvDataShow.setHorizontallyScrolling(false);
        circleCountShow = (TextView) findViewById(R.id.circleCountShow);

        openBtn = findViewById(R.id.open_btn);
        closeBtn = findViewById(R.id.close_btn);

        closeBtn.setEnabled(false);

        Spinner spnCharset = findViewById(R.id.spn_charset);
        mCharsetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        mCharsetAdapter.add("GB2312");
        mCharsetAdapter.add("GBK");
        mCharsetAdapter.add(StandardCharsets.US_ASCII.name());
        mCharsetAdapter.add(StandardCharsets.UTF_8.name());
        mCharsetAdapter.add(StandardCharsets.UTF_16.name());
        mCharsetAdapter.add(StandardCharsets.ISO_8859_1.name());
        spnCharset.setAdapter(mCharsetAdapter);
        spnCharset.setSelection(0);
        mCharsetIndex = 0;
        spnCharset.setOnItemSelectedListener(spnCharsetListener);

        Spinner spnBaud = findViewById(R.id.spn_baud);
        Integer[] baud_array={9600,19200,38400,57600,115200,230400,460800,500000,576000,921600,1000000};
        mBaudAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item,baud_array);
        mBaudAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBaud.setAdapter(mBaudAdapter);
        spnBaud.setSelection(4);
        mBaudIndex = 4;
        spnBaud.setOnItemSelectedListener(spnBaudListener);
    }

    public void openHardreader(View view) {
        int baud = mBaudAdapter.getItem(mBaudIndex);
        Log.d("tagg", "openHardreader baud: " + baud);
        int ret = mDecodeReader.open(baud);
        openBtn.setEnabled(ret == ResultCode.SUCCESS ? false : true);
        closeBtn.setEnabled(ret == ResultCode.SUCCESS ? true : false);
        if (ret == ResultCode.SUCCESS) {
            mDecodeReader.setDecodeReaderListener(listener);
        }
    }

    public void closeHardreader(View view) {
        int ret = mDecodeReader.close();
        openBtn.setEnabled(ret == ResultCode.SUCCESS ? true : false);
        closeBtn.setEnabled(ret == ResultCode.SUCCESS ? false : true);
        successCount = 0;
        tvDataShow.setText("");
        circleCountShow.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDecodeReader == null) {
            mDecodeReader = new DecodeReader(this);//初始化
        }
        mKeyEventResolver = new KeyEventResolver(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDecodeReader != null) {
            mDecodeReader.close();
        }
    }


    /**
     * 截获按键事件.发给ScanGunKeyEventHelper
     */
    @Override
    public boolean dispatchKeyEvent(android.view.KeyEvent event) {
        //要是重虚拟键盘输入怎不拦截
        if ("Virtual".equals(event.getDevice().getName())) {
            return super.dispatchKeyEvent(event);
        }
        mKeyEventResolver.analysisKeyEvent(event);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mKeyEventResolver.onDestroy();
    }

    @Override
    public void onScanSuccess(String barcode) {
        tvDataShow.setText(barcode);
        successCount++;
        circleCountShow.setText(getString(R.string.qrcode_scan_success) + "[" + successCount + "]");
    }

    private AdapterView.OnItemSelectedListener spnCharsetListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mCharsetIndex = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener spnBaudListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mBaudIndex = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final IDecodeReaderListener listener = new IDecodeReaderListener() {
        @Override
        public void onRecvData(byte[] data) {

            try {
                final String str = new String(data, mCharsetAdapter.getItem(mCharsetIndex));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        successCount++;
                        circleCountShow.setText(getString(R.string.qrcode_scan_success) + "[" + successCount + "]");

                        tvDataShow.setText(str);
                    }
                });
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    };
}
