package com.gkwak.lottonumbergenerator.libs;

import android.util.Log;

import com.gkwak.lottonumbergenerator.data.Lotto;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class TodayLottoGenerator {

    private static String TAG = "TODAY_LOTTO_GENERATOR";
    private int drwNo;
    private GetLottoNumTask mAuthTask = null;
    private Lotto lotto;
    private int resultLength;
    private int drwLength;

    public TodayLottoGenerator(int drwNo, int drwLength, int resultLength) {
        this.drwNo = drwNo;
        this.drwLength = drwLength;
        this.resultLength = resultLength;
    }

    public int[][] todayLottoNumbers() {
        int[][] resultNumbers = new int[this.resultLength][6];
        int[][] todayNumbers= new int[this.drwLength][6];

        int[] randomDrwNo = this.randomDrwNo();


        for (int i = 0; i < randomDrwNo.length; i++) {
            int[] a = this.getDrwNoLotto(randomDrwNo[i]);
            todayNumbers[i] = a;
        }

        Log.i(TAG, Arrays.deepToString(todayNumbers));

        Random rand = new Random();
        for (int i=0; i<this.resultLength; i++) {
            for (int j=0; j<6; j++) {
                int n = rand.nextInt(this.drwLength);
                int r = rand.nextInt(6);
                if (rand.nextBoolean()) this.validateNumber(resultNumbers[i], rand.nextInt(46));
                else this.validateNumber(resultNumbers[i], todayNumbers[n][r]);
                int validateNumber = this.validateNumber(resultNumbers[i], todayNumbers[n][r]);
                resultNumbers[i][j] = validateNumber;
            }
        }

        for (int i=0; i<this.resultLength; i++) {
            Arrays.sort(resultNumbers[i]);
        }
        Log.i(TAG, Arrays.deepToString(resultNumbers));

        return resultNumbers;
    }

    private int validateNumber(int[] savedNumber, int randomNumber) {
        Random rand = new Random();
        if (savedNumber.length == 0) return randomNumber;
        for (int i=0; i<savedNumber.length; i++) {
            if (savedNumber[i] == randomNumber) return this.validateNumber(savedNumber, rand.nextInt(46));
        }
        return randomNumber;
    }

    private int[] randomDrwNo() {
        int[] randomDrwNo = new int[this.drwLength];
        Random rand = new Random();

        for(int i = 0; i < randomDrwNo.length; i++) {
            int n = rand.nextInt(this.drwNo); // Gives n such that 0 <= n < 20
            randomDrwNo[i] = n;
        }

        return randomDrwNo;
    }

    private int[] getDrwNoLotto(int drwNo) {


        String url = "http://www.nlotto.co.kr/common.do?method=getLottoNumber&drwNo=" + drwNo;
        mAuthTask = new GetLottoNumTask(url);
        JSONObject result = null;
        try {
            result = mAuthTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        lotto = new Lotto(result);

        int[] lottoNumbers = lotto.getWinNumbers();

        return lottoNumbers;
    }
}
