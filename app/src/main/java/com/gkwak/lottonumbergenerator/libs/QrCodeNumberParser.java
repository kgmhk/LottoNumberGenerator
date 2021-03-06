package com.gkwak.lottonumbergenerator.libs;

import android.util.Log;

import com.gkwak.lottonumbergenerator.data.QrLotto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QrCodeNumberParser {
    private static String TAG = "QR_CODE_NUMBER_PARSER";
    private String qrCode;
    private int drwNo;

    public QrCodeNumberParser(String qrCode) {
        this.qrCode = qrCode;
    }

    public List<QrLotto> getQrCodeNumber() throws Exception {
        Log.i(TAG, qrCode);

        List<QrLotto> parseredQrCodeNumbers = new ArrayList<QrLotto>();
        try {
            String[] splitedQrCode = qrCode.split("v=");
            String[] splitedCodeNumber = splitedQrCode[1].split("q");

            this.drwNo = Integer.parseInt(splitedCodeNumber[0]);
//            if (splitedCodeNumber.length == 1) throw new Error("it is wrong qrCode");
            for(int i=1; i<splitedCodeNumber.length; i++) {
                List<String> tempArr = new ArrayList<String>();
                Log.i(TAG, "splitedCodeNumber = " + splitedCodeNumber[i]);

                tempArr.add(splitedCodeNumber[i].substring(0, 2));
                tempArr.add(splitedCodeNumber[i].substring(2, 4));
                tempArr.add(splitedCodeNumber[i].substring(4, 6));
                tempArr.add(splitedCodeNumber[i].substring(6, 8));
                tempArr.add(splitedCodeNumber[i].substring(8, 10));
                tempArr.add(splitedCodeNumber[i].substring(10, 12));

                QrLotto tempQrLotto = new QrLotto(tempArr);
                Log.i(TAG, "tempArr = " + tempArr);

                parseredQrCodeNumbers.add(tempQrLotto);

                Log.i(TAG, "return parseredQrCodeNumber = " + parseredQrCodeNumbers.iterator().hasNext());
            }
        } catch (Exception e) {
            Log.i(TAG, "Exception Error : " + e.toString());
            throw new Exception("it was wrong qrCode");
        }
        Log.i(TAG, "test error");
        return parseredQrCodeNumbers;
    }

    public int getDrwNo() {
        return this.drwNo;
    }
}
