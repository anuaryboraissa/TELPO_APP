package com.telpo.tps550.api.demo.customize.nfc.new_one;


import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.common.apiutil.nfc.CardTypeEnum;
import com.common.apiutil.nfc.NfcUtil;
import com.common.apiutil.nfc.SelectCardReturn;
import java.util.Arrays;

public class NfcWorker extends Worker {
    private static final String TAG = "NfcWorker";
    private NfcUtil nfcUtil;

    public NfcWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        nfcUtil = NfcUtil.getInstance(context);
        nfcUtil.initSerial();
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Performing NFC detection in background.");
        SelectCardReturn card = nfcUtil.selectCard();

        if (card != null) {
            CardTypeEnum cardType = card.getCardType();
            byte[] cardData = card.getCardNum();
            if (cardType == CardTypeEnum.TYPE_A) {
                cardData = Arrays.copyOfRange(cardData, 0, cardData.length - 3);
            }
            Log.d(TAG, "Card Detected: " + getCardTypeString(cardType) +
                    " | Card Number: " + NfcUtil.toHexString(cardData));
        } else {
            Log.d(TAG, "No NFC card detected.");
        }

        // Reschedule background task after it completes
        return Result.success();
    }

    private String getCardTypeString(CardTypeEnum cardTypeEnum) {
        switch (cardTypeEnum) {
            case M0: return "M0";
            case M1: return "M1";
            case ISO15693: return "ISO15693";
            case FELICA: return "FELICA";
            case ID_CARD: return "ID_CARD";
            case TYPE_A: return "TYPE_A";
            case TYPE_B: return "TYPE_B";
            default: return "Unknown";
        }
    }
}
