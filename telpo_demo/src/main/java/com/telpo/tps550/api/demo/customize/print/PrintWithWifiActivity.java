package com.telpo.tps550.api.demo.customize.print;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.softnet.devicetester.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PrintWithWifiActivity extends AppCompatActivity {
    Button btn_create_pdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_print_with_wifi);
        btn_create_pdf = (Button)findViewById(R.id.btn_create_pdf);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        btn_create_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.e("PRINTER","BUTTON CLICKED ===========> ");
                                PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                                OutputStream outputStream= null;
                                try {
                                    String path=(Comman.getAppPath(PrintWithWifiActivity.this));

                                    outputStream = new FileOutputStream(path);
                                    Log.e("PRINTER","OUTPUT STREAM ===========> "+outputStream);
                                    PDFAdapterHelper helper=new PDFAdapterHelper(PrintWithWifiActivity.this,printManager,outputStream);

                                    helper.createPDFFile(path+"test_pdf.pdf");
                                } catch (FileNotFoundException e) {
                                    Log.e("PRINTER","OUTPUT STREAM error===========> "+e.getMessage());
                                    throw new RuntimeException(e);
                                }

                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                     Log.e("PRINTER","PERMISSION DENIED ===========> ");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        Log.e("PRINTER","PERMISSION SHOULD BE SHOWN ===========> ");
                    }
                })
                .check();
    }

}