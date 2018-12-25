package com.gkwak.lottonumbergenerator.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.gkwak.lottonumbergenerator.R;
import com.gkwak.lottonumbergenerator.data.Lotto;
import com.gkwak.lottonumbergenerator.libs.GetLottoNumTask;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import static com.igaworks.core.RequestParameter.df;

public class SplashActivity extends Activity {

    private static String TAG = "SPLASH_ACTIVITY";
    private GetLottoNumTask mAuthTask = null;
    private Lotto lotto;
    private SharedPreferences sharedPref;

    public static final long HOUR = 3600*1000;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.i(TAG, "isOnline : " + isOnline());
        if (!isOnline()) {
            Toast.makeText(this, "Please Connect Internet", Toast.LENGTH_LONG).show();
        } else {
            String standard = "2018-09-22";
            int standardDrwNo = 825;

            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date standardDate;
            Date currentDate;
            try {
                Log.i(TAG, "start date compare");
                currentDate = d;
                standardDate = sdf.parse(standard);

                currentDate = new Date(currentDate.getTime() + 9 * HOUR);

                Log.i(TAG, "currentDate : " + currentDate);
                Log.i(TAG, "standardDate : " + standardDate);

                long diff = currentDate.getTime() - standardDate.getTime();
                long diffDays = diff / (24 * 60 * 60 * 1000);

                Log.i(TAG,"standardDate : " + diffDays);
                if (diffDays/7 > 0 && diffDays%7 != 0) {
                    standardDrwNo += diffDays/7;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


            Log.i(TAG,"standardDrwNo : " + standardDrwNo);
            String url = "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=" + standardDrwNo;
//            String url = "https://www.nlotto.co.kr/common.do?method=getLottoNumber&drwNo=" + standardDrwNo;
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
            String winNumberStr = lotto.getWinNumberStr();
            Log.i(TAG, winNumberStr);

//        SharedPreferences mPref = getSharedPreferences("lotto", Activity.MODE_PRIVATE);
//        int drwNo = mPref.getInt("drwNo", 1);

            sharedPref = getSharedPreferences("lotto", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();



            String checkDate = sharedPref.getString("checkDate", "");

            if (checkDate.equals("") || !checkDate.equals(sdf.format(d).toString())) {
                Log.i(TAG, "first in");
                editor.putString("checkDate", sdf.format(d).toString());
                editor.putInt("checkLottoNumberCount", 5);
            }

            Log.i(TAG, "firstWinamnt : " + lotto.getFirstWinamnt());
            editor.putBoolean("returnValue", lotto.getReturnValue());
            editor.putString("winNumber", winNumberStr);
            editor.putInt("drwNo", lotto.getDrwNo());
            editor.putInt("firstPrzwnerCo", lotto.getFirstPrzwnerCo());
            editor.putString("firstWinamnt", lotto.getFirstWinamnt()+"");
            editor.commit();
            Log.i(TAG, "Start Splash Activity");
        }



        Handler hd = new Handler();
        hd.postDelayed(new splashhandler() , 3000); // 3초 후에 hd Handler 실행
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private class splashhandler implements Runnable{
        public void run() {
            if (!isOnline()) {
                SplashActivity.this.finish();
                System.exit(0);
            } else {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent); // 로딩이 끝난후 이동할 Activity
                SplashActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
            }
        }
    }
}
