package com.gkwak.lottonumbergenerator.libs;

import android.util.Log;

import com.gkwak.lottonumbergenerator.data.Lotto;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by gkwak on 9/17/16.
 */
public class TodayLottoGenerator {

    private static String TAG = "TODAY_LOTTO_GENERATOR";
    private int drwNo;
    private GetLottoNumTask mAuthTask = null;
    private Lotto lotto;

    public TodayLottoGenerator(int drwNo) {
        this.drwNo = drwNo;
    }

    public int[][] todayLottoNumbers() {
        int[][] resultNumbers = new int[5][5];
        int[][] todayNumbers= new int[10][6];

        int[] randomDrwNo = this.randomDrwNo();


        for (int i = 0; i < randomDrwNo.length; i++) {
            int[] a = this.getDrwNoLotto(randomDrwNo[i]);
            Log.i(TAG, "Number : " + a);
            todayNumbers[i] = a;
        }

        Log.i(TAG, Arrays.deepToString(todayNumbers));

        Random rand = new Random();
        for (int i=0; i<5; i++) {
            for (int j=0; j<5; j++) {
                int n = rand.nextInt(10);
                int r = rand.nextInt(5);
                boolean validateNumber = this.validateNumber(resultNumbers[i], todayNumbers[n][r]);
                Log.i(TAG, "valiedeate : "+ validateNumber);
                if (validateNumber) {
                    resultNumbers[i][j] = todayNumbers[n][r];
                }
                else {
                    resultNumbers[i][j] = rand.nextInt(46);
                }

            }
        }

        for (int i=0; i<5; i++) {
            Arrays.sort(resultNumbers[i]);
        }
        Log.i(TAG, Arrays.deepToString(resultNumbers));

        return resultNumbers;
    }

    private boolean validateNumber(int[] savedNumber, int randomNumber) {
        if (savedNumber.length == 0) return true;
        for (int i=0; i<savedNumber.length; i++) {
            if (savedNumber[i] == randomNumber) return false;
        }
        return true;
    }

    private int[] randomDrwNo() {
        int[] randomDrwNo = new int[10];
        Random rand = new Random();

        for(int i = 0; i < 10; i++) {
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
