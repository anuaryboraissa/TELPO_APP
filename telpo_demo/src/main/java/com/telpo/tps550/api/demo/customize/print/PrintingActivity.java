package com.telpo.tps550.api.demo.customize.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.softnet.devicetester.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PrintingActivity extends BaseActivity {
    private Button btnPrintText, btnPrintImage, btnPrintPDF;
    private ImageView printPreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_printing);
        btnPrintText = findViewById(R.id.btnPrintText);
        btnPrintImage = findViewById(R.id.btnPrintImage);
        btnPrintPDF = findViewById(R.id.btnPrintPDF);
        printPreview = findViewById(R.id.printPreview);

        // Set listeners for buttons
        btnPrintText.setOnClickListener(v -> printText());
        btnPrintImage.setOnClickListener(v -> printImage());
        btnPrintPDF.setOnClickListener(v -> printPDF());
    }

    // Method to print plain text
    private void printText() {
        try {
            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

            if (printManager == null) {
                Toast.makeText(this, "Printing is not supported on this device.", Toast.LENGTH_LONG).show();
                return;
            }

            String jobName = getString(R.string.app_name) + " Document";
            PrintTextAdapter adapter = new PrintTextAdapter(this, "HELLO WORLD");

            printManager.print(jobName, adapter, null);
        } catch (Exception e) {
            Log.e("Printer", "Error initializing print job: " + e.getMessage());
            Toast.makeText(this, "Failed to start print job: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Method to print an image
    private void printImage() {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_camera);
            printPreview.setImageBitmap(bitmap);
            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
            if (printManager == null) {
                Toast.makeText(this, "Printing is not supported on this device.", Toast.LENGTH_LONG).show();
                return;
            }
            printManager.print("Print Image", new ImagePrintAdapter(bitmap), null);
        } catch (Exception e) {
            Log.e("Printer", "Error initializing print job: " + e.getMessage());
            Toast.makeText(this, "Failed to start print job: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Method to print a PDF file
    private void printPDF() {
        try {
            Uri pdfUri = Uri.parse(getString(com.softnet.devicetester.R.string.file_sdcard_download_sample_pdf));
            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
            if (printManager == null) {
                Toast.makeText(this, "Printing is not supported on this device.", Toast.LENGTH_LONG).show();
                return;
            }
            PrintDocumentAdapter adapter = new PdfDocumentAdapter(this, pdfUri);
            printManager.print("Print PDF", adapter, new PrintAttributes.Builder().build());
        } catch (Exception e) {
            Log.e("Printer", "Error initializing print job: " + e.getMessage());
            Toast.makeText(this, "Failed to start print job: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Custom PrintDocumentAdapter for PDF printing
    private static class PdfDocumentAdapter extends PrintDocumentAdapter {
        private final Context context;
        private final Uri pdfUri;

        public PdfDocumentAdapter(Context context, Uri uri) {
            this.context = context;
            this.pdfUri = uri;
        }

        @Override
        public void onStart() {
            super.onStart();
            Log.i("Printer", "Starting PDF Print");
        }

        @Override
        public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes1, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {

        }

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
            try (FileInputStream in = new FileInputStream(new File(pdfUri.getPath()));
                 FileOutputStream out = new FileOutputStream(destination.getFileDescriptor())) {

                byte[] buf = new byte[1024];
                int length;
                while ((length = in.read(buf)) > 0) {
                    out.write(buf, 0, length);
                }
                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
            } catch (Exception e) {
                Log.e("Printer", "Error writing PDF: " + e.getMessage());
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            Toast.makeText(context, "PDF Print Finished", Toast.LENGTH_SHORT).show();
        }
    }

    // Custom PrintDocumentAdapter for Image printing
    private static class ImagePrintAdapter extends PrintDocumentAdapter {
        private final Bitmap bitmap;

        public ImagePrintAdapter(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes1, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {

        }

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
            try (FileOutputStream out = new FileOutputStream(destination.getFileDescriptor())) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
            } catch (Exception e) {
                Log.e("Printer", "Error writing Image: " + e.getMessage());
            }
        }
    }
    private static class PrintTextAdapter extends PrintDocumentAdapter {

        private final Context context;
        private final String textContent; // The text content to print
        private PdfDocument pdfDocument;
        private int totalPages = 1;

        public PrintTextAdapter(Context context, String textContent) {
            this.context = context;
            this.textContent = textContent; // Initialize the text content
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal,
                             LayoutResultCallback callback, Bundle extras) {

            pdfDocument = new PdfDocument(); // Create a new PDF document

            // Assume 1 page for simple text printing; adjust if needed.
            totalPages = 1;

            if (totalPages > 0) {
                PrintDocumentInfo info = new PrintDocumentInfo.Builder("text_document.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalPages)
                        .build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }
        }

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                            CancellationSignal cancellationSignal,
                            WriteResultCallback callback) {

            // Loop over each page and generate content
            for (int i = 0; i < totalPages; i++) {
                if (containsPage(pages, i)) {
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, i).create();
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        pdfDocument.close();
                        return;
                    }

                    drawPageContent(page);
                    pdfDocument.finishPage(page);  // Finish the page
                }
            }

            try {
                pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
                callback.onWriteFinished(pages);  // Notify completion
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
            } finally {
                pdfDocument.close();  // Close the document
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            pdfDocument.close(); // Release resources on finish
        }

        // Helper method to draw text content on the page
        private void drawPageContent(PdfDocument.Page page) {
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setTextSize(14);
            int x = 10, y = 25;  // Starting position for the text

            // Draw the text content onto the page's canvas
            for (String line : textContent.split("\n")) {
                canvas.drawText(line, x, y, paint);
                y += paint.descent() - paint.ascent();  // Move to next line
            }
        }

        // Helper method to check if a specific page is in the requested pages
        private boolean containsPage(PageRange[] pageRanges, int page) {
            for (PageRange pageRange : pageRanges) {
                if (page >= pageRange.getStart() && page <= pageRange.getEnd()) {
                    return true;
                }
            }
            return false;
        }
    }
}