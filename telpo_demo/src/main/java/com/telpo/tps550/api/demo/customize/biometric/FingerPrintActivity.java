package com.telpo.tps550.api.demo.customize.biometric;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.biometric.BiometricPrompt.AuthenticationCallback;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.softnet.devicetester.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;
import com.telpo.tps550.api.demo.customize.BaseActivity2;

import java.util.concurrent.Executor;

public class FingerPrintActivity extends BaseActivity2 {

    private TextView tvStatus;
    private Button btnRetry;
    private String TAG="FINGERPRINT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print);
        tvStatus = findViewById(R.id.tv_status);
        btnRetry = findViewById(R.id.btn_retry);

        // Initialize biometric authentication
        checkBiometricSupport();
        showBiometricPrompt();
        // Retry button logic
        btnRetry.setOnClickListener(v -> showBiometricPrompt());
    }


    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(FingerPrintActivity.this);
        Log.e(TAG,"SHOW FINGERPRINT PROMPT ===========> ");
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(
                            @NonNull BiometricPrompt.AuthenticationResult result) {
                        tvStatus.setText("Authentication Successful!");
                        Toast.makeText(FingerPrintActivity.this, "Access Granted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode,
                                                      @NonNull CharSequence errString) {
                        tvStatus.setText("Authentication Error: " + errString);
                        btnRetry.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        tvStatus.setText("Authentication Failed");
                        btnRetry.setVisibility(View.VISIBLE);
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Use your fingerprint to authenticate")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void checkBiometricSupport() {
        Log.e(TAG,"CHECK FINGERPRINT AUTH ===========> ");
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                tvStatus.setText("Place your finger on the scanner");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                tvStatus.setText("Device does not support biometric authentication");
                btnRetry.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                tvStatus.setText("Biometric hardware unavailable");
                btnRetry.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                tvStatus.setText("No fingerprint enrolled. Please set up a fingerprint in Settings.");
                btnRetry.setVisibility(View.GONE);
                break;
        }
    }
}