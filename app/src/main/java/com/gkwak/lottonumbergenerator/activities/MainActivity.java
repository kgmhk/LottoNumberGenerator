package com.gkwak.lottonumbergenerator.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gkwak.lottonumbergenerator.R;
import com.gkwak.lottonumbergenerator.data.Lotto;
import com.gkwak.lottonumbergenerator.data.QrLotto;
import com.gkwak.lottonumbergenerator.libs.ConvertNumberToResource;
import com.gkwak.lottonumbergenerator.libs.GetLottoNumTask;
import com.gkwak.lottonumbergenerator.libs.HttpRequest;
import com.gkwak.lottonumbergenerator.libs.QrCodeNumberParser;
import com.gkwak.lottonumbergenerator.libs.TodayLottoGenerator;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.igaworks.IgawCommon;
import com.igaworks.adpopcorn.IgawAdpopcorn;
import com.igaworks.interfaces.IgawRewardItem;
import com.igaworks.interfaces.IgawRewardItemEventListener;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.log.DeviceLog;
import com.unity3d.ads.metadata.PlayerMetaData;
import com.unity3d.ads.misc.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.gravity;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private static String TAG = "MAIN_ACTIVITY";
    private static String UNITY_ADS_GAME_ID = "1144759";
    String[] winNumbers;
    TextView num1, num2, num3, num4, num5, num6, bonus_num;
    TextView todayNum1_0, todayNum1_1, todayNum1_2, todayNum1_3, todayNum1_4, todayNum1_5, todayNum2_0,
            todayNum2_1, todayNum2_2, todayNum2_3, todayNum2_4, todayNum2_5, todayNum3_0, todayNum3_1,
            todayNum3_2, todayNum3_3, todayNum3_4, todayNum3_5, todayNum4_0, todayNum4_1, todayNum4_2,
            todayNum4_3, todayNum4_4, todayNum4_5, todayNum5_0, todayNum5_1, todayNum5_2, todayNum5_3,
            todayNum5_4, todayNum5_5;
    TextView checkNum1_0, checkNum1_1, checkNum1_2, checkNum1_3, checkNum1_4, checkNum1_5, checkNum2_0,
            checkNum2_1, checkNum2_2, checkNum2_3, checkNum2_4, checkNum2_5, checkNum3_0, checkNum3_1,
            checkNum3_2, checkNum3_3, checkNum3_4, checkNum3_5, checkNum4_0, checkNum4_1, checkNum4_2,
            checkNum4_3, checkNum4_4, checkNum4_5, checkNum5_0, checkNum5_1, checkNum5_2, checkNum5_3,
            checkNum5_4, checkNum5_5;
    TextView check_lotto_num_title;
    Button qr_btn, today_num_btn, today_num_popup_close_btn, check_num_popup_close_btn, charge_offerwall_btn,
    charge_video_btn;

    private PopupWindow pwindo;
    private int mWidthPixels, mHeightPixels;
    private String interstitialPlacementId;
    private String incentivizedPlacementId;

    private TodayLottoGenerator todayLottoGenerator;
    private QrCodeNumberParser qrCodeNumberParser;
    private Handler mHandler;
    private ProgressDialog mProgressDialog;
    private GetLottoNumTask mAuthTask = null;
    private Lotto lotto;
    private ConvertNumberToResource convertNumberToResource;
    final UnityAdsListener unityAdsListener = new UnityAdsListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 유니티 애드 연동
        UnityAds.initialize(MainActivity.this, UNITY_ADS_GAME_ID, unityAdsListener);
        UnityAds.setListener(unityAdsListener);
        UnityAds.setDebugMode(true);

        // 애드팝콘 오퍼월 연동
        IgawCommon.startApplication(MainActivity.this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        IgawCommon.setUserId("user" + android_id);

        // Add AdMob
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        assert mAdView != null;
        mAdView.loadAd(adRequest);

        SharedPreferences mPref = getSharedPreferences("lotto", Activity.MODE_PRIVATE);
        String joinedWinNumbers = mPref.getString("winNumber", "no exist");
        int checkLottoNumberCount = mPref.getInt("checkLottoNumberCount", 0);

        LinearLayout winNumberLinear = (LinearLayout) findViewById(R.id.win_number_linear);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);

        convertNumberToResource = new ConvertNumberToResource();
        winNumbers = joinedWinNumbers.split(",");

        for (int i=0; i<8; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(layoutParams);

            TextView tv = new TextView(this);
            tv.setLayoutParams(layoutParams);
            tv.setGravity(Gravity.CENTER);
            if (i == 6) {
                tv.setText("+");
                winNumberLinear.addView(tv);
            } else if (i == 7) {
                iv = convertNumberToResource.convertNumberToResource(Integer.parseInt(winNumbers[6]), iv);
                winNumberLinear.addView(iv);
            } else {
                iv = convertNumberToResource.convertNumberToResource(Integer.parseInt(winNumbers[i]), iv);
                winNumberLinear.addView(iv);
            }
        }

        qr_btn = (Button) findViewById(R.id.qr_btn);
        today_num_btn = (Button) findViewById(R.id.today_num_btn);
        charge_offerwall_btn = (Button) findViewById(R.id.charge_offerwall_btn);
        charge_video_btn = (Button) findViewById(R.id.charge_video_btn);

        today_num_btn.setText("오늘의 번호 \n 추첨 가능 횟수 : " + checkLottoNumberCount);
        // 버튼 비활성화
        if (checkLottoNumberCount == 0) {
            today_num_btn.setEnabled(false);
        }

        // POPUP SIZE SETTING
        WindowManager w = getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        mWidthPixels = metrics.widthPixels;
        mHeightPixels = metrics.heightPixels;

        // 상태바와 메뉴바의 크기를 포함해서 재계산
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
        }
        // 상태바와 메뉴바의 크기를 포함
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                mWidthPixels = realSize.x;
                mHeightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        }

        disableButton(charge_video_btn);
        charge_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButton(charge_video_btn);
                UnityAds.show(MainActivity.this, incentivizedPlacementId);
            }
        });

        charge_offerwall_btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v){
                Log.i(TAG, "onClick charge Button");
                IgawAdpopcorn.openOfferWall(MainActivity.this);
            }
        });

        IgawCommon.setClientRewardEventListener(new IgawRewardItemEventListener() {
            private int reward = 0;
            @Override
            public void onGetRewardInfo(boolean isSuccess, String resultMsg, IgawRewardItem[] rewardItems) {
                for (IgawRewardItem rewardItem : rewardItems) {
                    //아래 정보를 이용하여 유저에게 리워드를 지급합니다.
                    rewardItem.getCampaignKey();
                    rewardItem.getCampaignTitle();
                    rewardItem.getRTID();
                    rewardItem.getRewardQuantity();

                    reward = rewardItem.getRewardQuantity();
                    Log.i(TAG, "onGetRewardInfo rewardIte" + rewardItem.getRewardQuantity());

                    //didGiveRewardItem 을 호출하여 리워드 지급 처리 완료를 IGAW 리워드 서버에 통지합니다.
                    rewardItem.didGiveRewardItem();
                }
            }

            @Override
            public void onDidGiveRewardItemResult(boolean isSuccess, String resultMsg, int resultCode, String completedRewardKey) {
                // TODO Auto-generated method stub
                // 동일한 completedRewardKey에 대해서 중복지급방지처리를 합니다.
                // 정상적인 리턴을 수신한 다음에 유저 리워드 지급 처리를 진행해야 합니다.
                Log.i(TAG, "onDidGiveRewardItemResult isSu"+isSuccess +" resultMsg" + resultMsg +" resultCode"+resultCode +" completedRewardKey "+ completedRewardKey);

                if (isSuccess) {
                    SharedPreferences mPref = getSharedPreferences("lotto", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mPref.edit();
                    int checkLottoNumberCount = mPref.getInt("checkLottoNumberCount", 0);
                    editor.putInt("checkLottoNumberCount", checkLottoNumberCount+reward);
                    editor.commit();

                    today_num_btn.setText("오늘의 번호 \n 추첨 가능 횟수 : " + (checkLottoNumberCount+reward));
                }

            }
        });

        qr_btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v){
                Log.i(TAG, "onClick Button");
                IntentIntegrator.initiateScan(MainActivity.this);
            }
        });

        today_num_btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, TAG+"Today Number Button Click", Toast.LENGTH_LONG).show();
                disableButton(today_num_btn);
                mHandler = new Handler();

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mProgressDialog = ProgressDialog.show(MainActivity.this, "",
                                "로또 번호를 취합 중 입니다.", true);
                        mHandler.postDelayed( new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    SharedPreferences mPref = getSharedPreferences("lotto", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = mPref.edit();
                                    int drwNo = mPref.getInt("drwNo", 1);
                                    int checkLottoNumberCount = mPref.getInt("checkLottoNumberCount", 0);
                                    editor.putInt("checkLottoNumberCount", checkLottoNumberCount - 1);
                                    editor.commit();

                                    today_num_btn.setText("오늘의 번호 \n 추첨 가능 횟수 : " + (checkLottoNumberCount-1));

                                    if (checkLottoNumberCount-1 == 0) {
                                        today_num_btn.setEnabled(false);
                                    }

                                    todayLottoGenerator = new TodayLottoGenerator(drwNo);

                                    int[][] todayNumbers = todayLottoGenerator.todayLottoNumbers();

                                    mProgressDialog.dismiss();
                                    todayLottoNumberPopupWindow(todayNumbers);
                                }
                                catch ( Exception e )
                                {
                                    e.printStackTrace();
                                }
                            }
                        }, 10);
                    }
                } );
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Log.i(TAG, "result : " + result.getContents().toString());
                qrCodeNumberParser = new QrCodeNumberParser(result.getContents().toString());
                checkLottoNumberPopupWindow(qrCodeNumberParser.getQrCodeNumber(), qrCodeNumberParser.getDrwNo());
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("MainActivity", "Weird");
// This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IgawCommon.startSession(MainActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        IgawCommon.endSession();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkLottoNumberPopupWindow(List<QrLotto> qrNumbers, int drwNo) {
        try {
            //  LayoutInflater 객체와 시킴
            LayoutInflater inflater = (LayoutInflater) MainActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.check_num_popup,
                    (ViewGroup) findViewById(R.id.check_lotto_num_element));

            pwindo = new PopupWindow(layout, mWidthPixels-100, mHeightPixels-500, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            check_num_popup_close_btn = (Button) layout.findViewById(R.id.check_num_popup_close_btn);
            check_lotto_num_title = (TextView) layout.findViewById(R.id.check_lotto_num_title);
            checkNum1_0 = (TextView) layout.findViewById(R.id.checkNum1_0);
            checkNum1_1 = (TextView) layout.findViewById(R.id.checkNum1_1);
            checkNum1_2 = (TextView) layout.findViewById(R.id.checkNum1_2);
            checkNum1_3 = (TextView) layout.findViewById(R.id.checkNum1_3);
            checkNum1_4 = (TextView) layout.findViewById(R.id.checkNum1_4);
            checkNum1_5 = (TextView) layout.findViewById(R.id.checkNum1_5);

            checkNum2_0 = (TextView) layout.findViewById(R.id.checkNum2_0);
            checkNum2_1 = (TextView) layout.findViewById(R.id.checkNum2_1);
            checkNum2_2 = (TextView) layout.findViewById(R.id.checkNum2_2);
            checkNum2_3 = (TextView) layout.findViewById(R.id.checkNum2_3);
            checkNum2_4 = (TextView) layout.findViewById(R.id.checkNum2_4);
            checkNum2_5 = (TextView) layout.findViewById(R.id.checkNum2_5);

            checkNum3_0 = (TextView) layout.findViewById(R.id.checkNum3_0);
            checkNum3_1 = (TextView) layout.findViewById(R.id.checkNum3_1);
            checkNum3_2 = (TextView) layout.findViewById(R.id.checkNum3_2);
            checkNum3_3 = (TextView) layout.findViewById(R.id.checkNum3_3);
            checkNum3_4 = (TextView) layout.findViewById(R.id.checkNum3_4);
            checkNum3_5 = (TextView) layout.findViewById(R.id.checkNum3_5);

            checkNum4_0 = (TextView) layout.findViewById(R.id.checkNum4_0);
            checkNum4_1 = (TextView) layout.findViewById(R.id.checkNum4_1);
            checkNum4_2 = (TextView) layout.findViewById(R.id.checkNum4_2);
            checkNum4_3 = (TextView) layout.findViewById(R.id.checkNum4_3);
            checkNum4_4 = (TextView) layout.findViewById(R.id.checkNum4_4);
            checkNum4_5 = (TextView) layout.findViewById(R.id.checkNum4_5);

            checkNum5_0 = (TextView) layout.findViewById(R.id.checkNum5_0);
            checkNum5_1 = (TextView) layout.findViewById(R.id.checkNum5_1);
            checkNum5_2 = (TextView) layout.findViewById(R.id.checkNum5_2);
            checkNum5_3 = (TextView) layout.findViewById(R.id.checkNum5_3);
            checkNum5_4 = (TextView) layout.findViewById(R.id.checkNum5_4);
            checkNum5_5 = (TextView) layout.findViewById(R.id.checkNum5_5);


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

            String reponseResult = result.getString("returnValue");

            if (reponseResult.equals("fail")) {
                Log.i(TAG, "it doesn't exist number");
                check_lotto_num_title.setText(R.string.not_yet_draw);
                return;
            }

            lotto = new Lotto(result);
            int[] lottoNumbers = lotto.getWinNumbers();

            boolean find = false;
            for(int i=0; i<qrNumbers.size(); i++) {
                if (i == 0) {
                    List<String> temp = new ArrayList<String>();
                    temp = qrNumbers.get(i).getQrNumber();

                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(0)));
                    if (find) checkNum1_0.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum1_0.setText(temp.get(0));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(1)));
                    if (find) checkNum1_1.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum1_1.setText(temp.get(1));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(2)));
                    if (find) checkNum1_2.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum1_2.setText(temp.get(2));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(3)));
                    if (find) checkNum1_3.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum1_3.setText(temp.get(3));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(4)));
                    if (find) checkNum1_4.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum1_4.setText(temp.get(4));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(5)));
                    if (find) checkNum1_5.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum1_5.setText(temp.get(5));
                } else if (i == 1) {
                    List<String> temp = new ArrayList<String>();
                    temp = qrNumbers.get(i).getQrNumber();

                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(0)));
                    if (find) checkNum2_0.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum2_0.setText(temp.get(0));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(1)));
                    if (find) checkNum2_1.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum2_1.setText(temp.get(1));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(2)));
                    if (find) checkNum2_2.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum2_2.setText(temp.get(2));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(3)));
                    if (find) checkNum2_3.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum2_3.setText(temp.get(3));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(4)));
                    if (find) checkNum2_4.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum2_4.setText(temp.get(4));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(5)));
                    if (find) checkNum2_5.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum2_5.setText(temp.get(5));
                } else if (i == 2) {
                    List<String> temp = new ArrayList<String>();
                    temp = qrNumbers.get(i).getQrNumber();

                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(0)));
                    if (find) checkNum3_0.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum3_0.setText(temp.get(0));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(1)));
                    if (find) checkNum3_1.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum3_1.setText(temp.get(1));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(2)));
                    if (find) checkNum3_2.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum3_2.setText(temp.get(2));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(3)));
                    if (find) checkNum3_3.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum3_3.setText(temp.get(3));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(4)));
                    if (find) checkNum3_4.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum3_4.setText(temp.get(4));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(5)));
                    if (find) checkNum3_5.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum3_5.setText(temp.get(5));
                } else  if (i == 3) {
                    List<String> temp = new ArrayList<String>();
                    temp = qrNumbers.get(i).getQrNumber();

                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(0)));
                    if (find) checkNum4_0.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum4_0.setText(temp.get(0));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(1)));
                    if (find) checkNum4_1.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum4_1.setText(temp.get(1));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(2)));
                    if (find) checkNum4_2.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum4_2.setText(temp.get(2));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(3)));
                    if (find) checkNum4_3.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum4_3.setText(temp.get(3));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(4)));
                    if (find) checkNum4_4.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum4_4.setText(temp.get(4));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(5)));
                    if (find) checkNum4_5.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum4_5.setText(temp.get(5));
                } else if (i == 4) {
                    List<String> temp = new ArrayList<String>();
                    temp = qrNumbers.get(i).getQrNumber();

                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(0)));
                    if (find) checkNum5_0.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum5_0.setText(temp.get(0));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(1)));
                    if (find) checkNum5_1.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum5_1.setText(temp.get(1));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(2)));
                    if (find) checkNum5_2.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum5_2.setText(temp.get(2));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(3)));
                    if (find) checkNum5_3.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum5_3.setText(temp.get(3));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(4)));
                    if (find) checkNum5_4.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum5_4.setText(temp.get(4));
                    find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(5)));
                    if (find) checkNum5_5.setTextColor(getResources().getColor(R.color.right_color));
                    checkNum5_5.setText(temp.get(5));
                }
            }

            // popup btn click
            check_num_popup_close_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    pwindo.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isEqual(int[] origin, int target) {
        for(Integer s: origin){
            if(s.equals(target))
                return true;
        }
        return false;
    }


    private void todayLottoNumberPopupWindow(int[][] todayNumbers) {
        try {
            //  LayoutInflater 객체와 시킴
            LayoutInflater inflater = (LayoutInflater) MainActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.today_num_popup,
                    (ViewGroup) findViewById(R.id.today_lotto_num_element));

            LinearLayout top = (LinearLayout) layout.findViewById(R.id.today_num_lotto_top);

            pwindo = new PopupWindow(layout, mWidthPixels-100, mHeightPixels-500, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            today_num_popup_close_btn = (Button) layout.findViewById(R.id.today_num_popup_close_btn);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

            for (int j=0; j<5; j++) {
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setGravity(Gravity.CENTER);
                for (int i=0; i<6; i++) {
                    TextView tv = new TextView(this);
                    tv.setText(todayNumbers[j][i]+ "");
                    tv.setLayoutParams(textParams);
                    tv.setGravity(Gravity.CENTER);
                    linearLayout.addView(tv);
                }
                top.addView(linearLayout);
            }

            today_num_popup_close_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                enableButton(today_num_btn);
                pwindo.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableButton (Button btn) {
        btn.setEnabled(true);
    }
    private void disableButton (Button btn) {
        btn.setEnabled(false);
    }


    /* LISTENER */

    private class UnityAdsListener implements IUnityAdsListener {

        @Override
        public void onUnityAdsReady(final String zoneId) {

            // TODO: 검수 완료 후 문자열 넣기
            charge_video_btn.setText("TEST");

            DeviceLog.debug("onUnityAdsReady: " + zoneId);
            Utilities.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // look for various default placement ids over time
                    switch (zoneId) {
                        case "video":
                        case "defaultZone":
                        case "defaultVideoAndPictureZone":
                            interstitialPlacementId = zoneId;
                            enableButton(charge_video_btn);
                            break;

                        case "rewardedVideo":
                        case "rewardedVideoZone":
                        case "incentivizedZone":
                            incentivizedPlacementId = zoneId;
                            enableButton(charge_video_btn);
                            break;
                    }
                }
            });

            toast("Ready", zoneId);
        }

        @Override
        public void onUnityAdsStart(String zoneId) {
            DeviceLog.debug("onUnityAdsStart: " + zoneId);
            toast("Start", zoneId);
        }

        @Override
        public void onUnityAdsFinish(String zoneId, UnityAds.FinishState result) {
            DeviceLog.debug("onUnityAdsFinish: " + zoneId + " - " + result);
            if (UnityAds.FinishState.COMPLETED == result) {
                SharedPreferences mPref = getSharedPreferences("lotto", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = mPref.edit();
                int checkLottoNumberCount = mPref.getInt("checkLottoNumberCount", 0);
                editor.putInt("checkLottoNumberCount", checkLottoNumberCount+1);
                editor.commit();

                today_num_btn.setText("오늘의 번호 \n 추첨 가능 횟수 : " + (checkLottoNumberCount+1));
            }
            //TODO: 오퍼월 검수 후 텍스트 넣기
            charge_video_btn.setText("WAIT");
            disableButton(charge_video_btn);
            toast("Finish", zoneId + " " + result);
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError error, String message) {
            DeviceLog.debug("onUnityAdsError: " + error + " - " + message);
            toast("Error", error + " " + message);

            charge_video_btn.setText(error + " - " + message);
        }

        private void toast(String callback, String msg) {
            Toast.makeText(getApplicationContext(), callback + ": " + msg, Toast.LENGTH_SHORT).show();
        }
    }

}
