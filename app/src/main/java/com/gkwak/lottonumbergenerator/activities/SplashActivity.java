package com.gkwak.lottonumbergenerator.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.gkwak.lottonumbergenerator.R;
import com.gkwak.lottonumbergenerator.data.Lotto;
import com.gkwak.lottonumbergenerator.libs.GetLottoNumTask;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class SplashActivity extends Activity {

    private static String TAG = "SPLASH_ACTIVITY";
    private GetLottoNumTask mAuthTask = null;
    private Lotto lotto;
    private SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String url = "http://www.nlotto.co.kr/common.do?method=getLottoNumber";
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
        int[] a = lotto.getWinNumber();
        Log.i(TAG, a[0] + "");
        String joiWinNumber = new String();

        for(int i = 0; i < a.length; i++) {
            joiWinNumber += a[i] + ",";
        }

        sharedPref = getSharedPreferences("lotto", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("winNumber", joiWinNumber);
        editor.commit();
        Log.i(TAG, "Start Splash Activity");

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler() , 3000); // 3초 후에 hd Handler 실행
    }

    private class splashhandler implements Runnable{
        public void run() {
            Intent intent = new Intent(getApplication(), MainActivity.class);
//            intent.putExtra("Lotto", (Serializable) lotto);
            startActivity(intent); // 로딩이 끝난후 이동할 Activity
            SplashActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
        }
    }
}
