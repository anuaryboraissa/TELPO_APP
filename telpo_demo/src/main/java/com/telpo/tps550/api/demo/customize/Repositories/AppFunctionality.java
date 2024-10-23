package com.telpo.tps550.api.demo.customize.Repositories;




import com.telpo.tps550.api.demo.customize.Constants.AppConstants;
import com.telpo.tps550.api.demo.customize.Models.MyItem;

import java.util.Arrays;
import java.util.List;

public class AppFunctionality {
     public static List<MyItem> functionalities= Arrays.asList(
            new MyItem("Thermal Printer Test","The device includes a built-in thermal printer that allows for quick printing of receipts, invoices, and transaction summaries", AppConstants.thermalPrinter,1),
            new MyItem("NFC (Near Field Communication) Test","The NFC interface allows for contactless communication, enabling the terminal to interact with NFC-enabled devices and cards", AppConstants.nfc,2),
            new MyItem("Barcode and QR Code Scanner Test","The device is equipped with a barcode/QR code scanner that allows for quick scanning of product codes", AppConstants.barcodeQrCode,3),
            new MyItem("E-wallet Integration Test","allowing users to manage digital wallets.", AppConstants.eWalletIntegration,5),
            new MyItem("Camera Test","The terminal's built-in camera can be used for capturing images", AppConstants.camera,6),
            new MyItem("LED Indicator Test","The LED lights can be controlled to provide visual notification", AppConstants.ledIndicator,7),
            new MyItem("GPIO (General Purpose Input/Output) Test","GPIO ports allow the terminal to control external devices or peripherals.", AppConstants.gpid,8),
            new MyItem("Audio Output","The terminal includes an audio jack that can be used for audio feedback.", AppConstants.audioOutput,9),
            new MyItem("General Security and User Authentication Test","Fingerprint scanning is crucial for applications requiring user authentication, especially in sensitive sectors like banking or healthcare", AppConstants.auth,10),
            new MyItem("LCD Test","Validate the functionality and display quality of the device's screen by running various display patterns and color tests.", AppConstants.lcd_icon,12),
            new MyItem("Touch Panel (TP) Test","Check the responsiveness and accuracy of the touch screen input through interaction tests like touch, swipe, and multi-touch", AppConstants.tp,13),
            new MyItem("Magnetic Card Test","Test the magnetic card reader by swiping magnetic cards to verify the correct reading and processing of card information.", AppConstants.magnetic_card,14),
            new MyItem("Money Box Test","Connect and test the money box functionality, ensuring the terminal can trigger and interact with external cash drawers", AppConstants.money_box,15),
            new MyItem("ID Card Test"," Validate the terminal’s ability to read data from ID cards, including testing card recognition and data extraction.", AppConstants.id_card,16),
            new MyItem("IC Card Test"," Insert and test IC cards (chip cards) to ensure proper reading, writing, and communication between the terminal and the card", AppConstants.ic_card,17),
            new MyItem("PSAM Test","Verify the functionality of the PSAM (Security Access Module) slots by testing card insertion and secure communication.", AppConstants.psam,18),
            new MyItem("Tamper Test"," Ensure device security by testing anti-tamper mechanisms, verifying that the terminal reacts correctly to potential tampering", AppConstants.temper,19),
            new MyItem("Digital Tube Test","Test any digital tube displays on the device, checking for correct numerical or symbolic output", AppConstants.digital_tube,21),
            new MyItem("System Test"," Run comprehensive system diagnostics, including CPU, memory, storage, and battery status checks.\n", AppConstants.system_ic,22),
            new MyItem("CAN Test","Validate the terminal’s CAN (Controller Area Network) bus communication functionality for automotive or industrial applications.", AppConstants.can_ic,23),
            new MyItem("Serial Test","Test the terminal’s serial communication ports to ensure data transmission and reception works as expected", AppConstants.ic_serial,24),
            new MyItem("Simple SubLCD Test","Run tests on any secondary display or sub-LCD present on the terminal for visual output verification.", AppConstants.sub_lcd,25),
            new MyItem("Power Control Test"," Test the terminal’s power management features, including battery health, charging status, and power saving modes.", AppConstants.power_control,26),
            new MyItem("Delivery Locker Test","Simulate interactions with connected delivery lockers, ensuring the terminal can control and manage locker operations.", AppConstants.deliveryLocker,27),
            new MyItem("Power Management Test","Assess the terminal's power management features by checking standby modes, wake-up functions, and energy consumption settings.", AppConstants.power_manage,28),
            new MyItem("Wi-Fi and Cellular Connectivity Test","Supports both Wi-Fi and cellular connections (through SIM card slots for 4G LTE)", AppConstants.wifiCellular,29),
             new MyItem("USB Interfaces Test","Multiple USB ports allow for the connection of various peripherals and devices.", AppConstants.usbInterface,30)
    );
}
