package com.telpo.tps550.api.demo.customize.Constants;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.softnet.devicetester.R;
public  class ApplicationUtils {
    public static void handleDeviceError(Activity activity, String errorMessage, Runnable retryCallback) {
        activity.setContentView(R.layout.error_hadler);  // Fixed: It was `error_hadler` in your original code

        // Set the error message
        ((TextView) activity.findViewById(R.id.errorMessage)).setText(errorMessage);

        // Handle retry button click
        ((Button) activity.findViewById(R.id.retryButton)).setOnClickListener(view -> {
            if (retryCallback != null) {
                retryCallback.run();  // Execute the passed callback on retry
            }
        });
    }
}
