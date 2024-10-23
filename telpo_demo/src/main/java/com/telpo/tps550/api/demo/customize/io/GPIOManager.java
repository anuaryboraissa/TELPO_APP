//package com.telpo.tps550.api.demo.customize.io;
//
//import com.google.android.things.pio.Gpio;
//import com.google.android.things.pio.PeripheralManagerService;
//import java.io.IOException;
//
//public class GPIOManager {
//    private Gpio mGpio;
//
//    public GPIOManager(String pinName) throws IOException {
//        PeripheralManagerService manager = PeripheralManagerService.();
//        mGpio = manager.openGpio(pinName);
//        initializeGpio();
//    }
//
//    private void initializeGpio() throws IOException {
//        mGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); // Default to LOW
//        mGpio.setActiveType(Gpio.ACTIVE_LOW); // Define active state
//        mGpio.setEdgeTriggerType(Gpio.EDGE_BOTH); // For input pins
//    }
//
//    public void testGpioOutput() throws IOException, InterruptedException {
//        // Turn GPIO HIGH
//        mGpio.setValue(true);
//        Thread.sleep(500); // Wait for half a second
//        // Turn GPIO LOW
//        mGpio.setValue(false);
//    }
//
//    public boolean testGpioInput() throws IOException {
//        // Read the GPIO value
//        return mGpio.getValue();
//    }
//
//    public void cleanUp() throws IOException {
//        if (mGpio != null) {
//            mGpio.close();
//        }
//    }
//}