package com.telpo.tps550.api.demo.customize.nfc;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.common.demo.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;

public class CustomNFCActivity extends BaseActivity {
    private TextView statusText;
    private ProgressBar progressBar;
    private Button controlButton;
    private NfcTask nfcTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_nfcactivity);
        statusText = findViewById(R.id.statusText);
        progressBar = findViewById(R.id.progressBar);
        controlButton = findViewById(R.id.controlButton);

        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nfcTask != null && nfcTask.getStatus() == AsyncTask.Status.RUNNING) {
                    nfcTask.cancel(true);
                    statusText.setText("Cancelled!");
                } else {
                    startNfcProcess();
                }
            }
        });

        // Start NFC process automatically on activity launch
        startNfcProcess();
    }

    private void startNfcProcess() {
        nfcTask = new NfcTask();
        nfcTask.execute();
    }


    //async task in background
    private class NfcTask extends AsyncTask<Void, String, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            statusText.setText("Initializing NFC...");
            controlButton.setText("Cancel");

            // Optional: Show a progress dialog
            progressDialog = new ProgressDialog(CustomNFCActivity.this);
            progressDialog.setMessage("Processing...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Simulate different NFC operations with sleep
                publishProgress("Authenticating...");
                Thread.sleep(1000);  // Simulate authentication

                if (isCancelled()) return false;  // Handle cancellation

                publishProgress("Reading Block...");
                Thread.sleep(1000);  // Simulate block reading

                if (isCancelled()) return false;

                publishProgress("Writing to Wallet...");
                Thread.sleep(1000);  // Simulate writing to wallet

                if (isCancelled()) return false;

                publishProgress("Transfer Complete.");
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            statusText.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();
            controlButton.setText("Start / Restart");

            if (result) {
                statusText.setText("Process completed successfully!");
            } else {
                statusText.setText("Process failed!");
            }
        }

        @Override
        protected void onCancelled() {
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();
            controlButton.setText("Start / Restart");
            statusText.setText("Process was cancelled.");
        }
    }
}