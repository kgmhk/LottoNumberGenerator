package com.gkwak.lottonumbergenerator.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.IntegerRes;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.igaworks.adpopcorn.IgawAdpopcorn;
import com.igaworks.interfaces.IgawRewardItem;
import com.igaworks.interfaces.IgawRewardItemEventListener;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.log.DeviceLog;
import com.unity3d.ads.metadata.PlayerMetaData;
import com.unity3d.ads.misc.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.fingerprintAuthDrawable;
import static android.R.attr.gravity;

public class MainActivity extends AppCompatActivity {
    private static int SPECIAL_RESULT_LENGTH = 1;
    private static int RESULT_LENGTH = 5;
    private static int DRW_LENGTH = 5;
    private static String TAG = "MAIN_ACTIVITY";
    private static String UNITY_ADS_GAME_ID = "1144759";
    String[] winNumbers;
    TextView check_lotto_num_title, today_lotto_number_title;
    Button qr_btn, today_num_btn, today_num_popup_close_btn, check_num_popup_close_btn, charge_offerwall_btn,
    charge_video_btn, today_num_popup_share_btn, check_num_popup_share_btn, settiong_info_close_btn;

    private PopupWindow pwindo, cehckNumPopupWindo ,settingInfoPopupWindo;
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

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // [START image_view_event]
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "main");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MainActivty");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        // [END image_view_event]

        // font 적용
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/SangSangTitle.ttf"));

        // 유니티 애드 연동
        UnityAds.initialize(MainActivity.this, UNITY_ADS_GAME_ID, unityAdsListener);
        UnityAds.setListener(unityAdsListener);

        // Add AdMob
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        assert mAdView != null;
        mAdView.loadAd(adRequest);

        SharedPreferences mPref = getSharedPreferences("lotto", Activity.MODE_PRIVATE);
        String joinedWinNumbers = mPref.getString("winNumber", "no exist");
        int drwNo = mPref.getInt("drwNo", 1);
        int checkLottoNumberCount = mPref.getInt("checkLottoNumberCount", 0);
        int firstPrzwnerCo = mPref.getInt("firstPrzwnerCo", 0);
        String firstWinamnt = mPref.getString("firstWinamnt", "");
        String convertCurrency = String.format(Currency.getInstance(Locale.KOREA).getSymbol() + "%,d", Long.parseLong(firstWinamnt));

        Log.i(TAG, "firstWinamnt : " + firstWinamnt);

        LinearLayout winNumberLinear = (LinearLayout) findViewById(R.id.win_number_linear);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        TextView winNumberDrw = (TextView) findViewById(R.id.win_number_drw);
        convertNumberToResource = new ConvertNumberToResource();
        winNumbers = joinedWinNumbers.split(",");

        TextView coWinner = (TextView) findViewById(R.id.coWinner);
        TextView winAmnt = (TextView) findViewById(R.id.winAmnt);
        TextView coWinnerTitle = (TextView) findViewById(R.id.coWinnerTitle);;
        TextView winAmntTitle = (TextView) findViewById(R.id.winAmntTitle);

        coWinner.setTypeface(Typekit.createFromAsset(this, "fonts/SangSangTitle.ttf"));
        winAmnt.setTypeface(Typekit.createFromAsset(this, "fonts/SangSangTitle.ttf"));
        coWinnerTitle.setTypeface(Typekit.createFromAsset(this, "fonts/SangSangTitle.ttf"));
        winAmntTitle.setTypeface(Typekit.createFromAsset(this, "fonts/SangSangTitle.ttf"));

        coWinner.setText("" + firstPrzwnerCo + "명");
        winAmnt.setText("" + convertCurrency);

        winNumberDrw.setText(drwNo + " 회 당첨번호");
        winNumberDrw.setTypeface(Typekit.createFromAsset(this, "fonts/SangSangTitle.ttf"));
        for (int i=0; i<8; i++) {
            layoutParams.setMargins(6, 6, 6, 6);
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(layoutParams);
//            iv.setScaleType(ImageView.ScaleType.CENTER);

            TextView tv = new TextView(this);
            tv.setLayoutParams(layoutParams);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(0,20,0,0);
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

        // button font face
        qr_btn.setTypeface(Typekit.createFromAsset(this, "fonts/SangSangTitle.ttf"));
        charge_offerwall_btn.setTypeface(Typekit.createFromAsset(this, "fonts/SangSangTitle.ttf"));
        charge_video_btn.setTypeface(Typekit.createFromAsset(this, "fonts/SangSangTitle.ttf"));
        today_num_btn.setText(R.string.today_num_btn);
        today_num_btn.setTypeface(Typekit.createFromAsset(this, "fonts/SangSangTitle.ttf"));
        // 버튼 비활성화
        if (checkLottoNumberCount <= 0) {
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

        charge_video_btn.setText(R.string.unityAdsWait);
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

        qr_btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v){
                Log.i(TAG, "onClick Button");
                IntentIntegrator.initiateScan(MainActivity.this);
            }
        });

        today_num_btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
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
                                    int drwNo = mPref.getInt("drwNo", 1);

                                    todayLottoGenerator = new TodayLottoGenerator(drwNo, DRW_LENGTH, RESULT_LENGTH);

                                    int[][] todayNumbers = todayLottoGenerator.todayLottoNumbers();

                                    mProgressDialog.dismiss();
                                    todayLottoNumberPopupWindow(todayNumbers, R.string.today_lotto_number, 100, 500);
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
            } else {
                qrCodeNumberParser = new QrCodeNumberParser(result.getContents().toString());
                checkLottoNumberPopupWindow(qrCodeNumberParser.getQrCodeNumber(), qrCodeNumberParser.getDrwNo());
            }
        } else {
            Log.d("MainActivity", "Weird");
// This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
            settingInfoPopupWindow(100, 600);
            Log.i(TAG, "action_settings");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkLottoNumberPopupWindow(List<QrLotto> qrNumbers, int drwNo) {
        final View layout;
        try {
            //  LayoutInflater 객체와 시킴
            LayoutInflater inflater = (LayoutInflater) MainActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            layout = inflater.inflate(R.layout.check_num_popup,
                    (ViewGroup) findViewById(R.id.check_lotto_num_element));

            LinearLayout checkNumberLinear = (LinearLayout) layout.findViewById(R.id.check_lotto_num_linear);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);

            int windowHeight = 500;
            if (mHeightPixels <= 800) windowHeight = 200;
            cehckNumPopupWindo = new PopupWindow(layout, mWidthPixels-100, mHeightPixels-windowHeight, true);
            cehckNumPopupWindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            check_num_popup_close_btn = (Button) layout.findViewById(R.id.check_num_popup_close_btn);
            check_lotto_num_title = (TextView) layout.findViewById(R.id.check_lotto_num_title);
            check_num_popup_share_btn = (Button) layout.findViewById(R.id.check_num_popup_share_btn);

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
                for(int i=0; i<6; i++) {
                    LinearLayout linearLayout = new LinearLayout(this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setGravity(Gravity.CENTER);
                    for(int j=0; j<6; j++) {
                        TextView tv = new TextView(this);
                        tv.setLayoutParams(layoutParams);
                        tv.setGravity(Gravity.CENTER);
                        tv.setText("-");
                        linearLayout.addView(tv);
                    }
                    checkNumberLinear.addView(linearLayout);
                }
            } else {
                lotto = new Lotto(result);
                int[] lottoNumbers = lotto.getWinNumbers();

                boolean find = false;
                for(int i=0; i<qrNumbers.size(); i++) {
                    List<String> temp = new ArrayList<String>();
                    temp = qrNumbers.get(i).getQrNumber();

                    LinearLayout linearLayout = new LinearLayout(this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setGravity(Gravity.CENTER);

                    for(int j=0; j<6; j++) {
                        find = this.isEqual(lottoNumbers, Integer.parseInt(temp.get(j)));
                        ImageView iv = new ImageView(this);
                        layoutParams.setMargins(4, 4, 4, 4);
                        iv.setLayoutParams(layoutParams);
                        if (find) {
                            iv = convertNumberToResource.convertNumberToResource(Integer.parseInt(temp.get(j)), iv);
                            linearLayout.addView(iv);
                        } else {
                            iv = convertNumberToResource.convertNumberToResourceNoWin(Integer.parseInt(temp.get(j)), iv);
                            linearLayout.addView(iv);
                        }
                    }
                    checkNumberLinear.addView(linearLayout);
                }
            }
            // popup btn click
            check_num_popup_close_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.i(TAG, "Click Check_num_popup_close_btn");
                    cehckNumPopupWindo.dismiss();
                }
            });

            check_num_popup_share_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    takeScreenshot(layout);
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


    private void todayLottoNumberPopupWindow(int[][] todayNumbers, int todayNumberTitle, int windowWidth, int windowHeight) {
        final View layout;
        try {
            //  LayoutInflater 객체와 시킴
            final LayoutInflater inflater = (LayoutInflater) MainActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            layout = inflater.inflate(R.layout.today_num_popup,
                    (ViewGroup) findViewById(R.id.today_lotto_num_element));

            final LinearLayout top = (LinearLayout) layout.findViewById(R.id.today_num_lotto_top);
            if (mHeightPixels <= 800) windowHeight = 200;
            pwindo = new PopupWindow(layout, mWidthPixels-windowWidth, mHeightPixels-windowHeight, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            today_num_popup_close_btn = (Button) layout.findViewById(R.id.today_num_popup_close_btn);
            today_lotto_number_title = (TextView) layout.findViewById(R.id.today_lotto_number_title);
            today_num_popup_share_btn = (Button) layout.findViewById(R.id.today_num_popup_share_btn);

            today_lotto_number_title.setText(todayNumberTitle);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

            for (int j=0; j<todayNumbers.length; j++) {
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setGravity(Gravity.CENTER);
                for (int i=0; i<6; i++) {

                    ImageView iv = new ImageView(this);
                    layoutParams.setMargins(4, 4, 4, 4);
                    iv.setLayoutParams(layoutParams);

                    iv = convertNumberToResource.convertNumberToResource(todayNumbers[j][i], iv);
                    linearLayout.addView(iv);
                }
                top.addView(linearLayout);
            }

            today_num_popup_close_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                enableButton(today_num_btn);
                pwindo.dismiss();
                }
            });

            today_num_popup_share_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    takeScreenshot(layout);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void settingInfoPopupWindow(int windowWidth, int windowHeight) {
        try {
            //  LayoutInflater 객체와 시킴
            LayoutInflater inflater = (LayoutInflater) MainActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.setting_info_popup,
                    (ViewGroup) findViewById(R.id.settiong_info_element));


            settingInfoPopupWindo = new PopupWindow(layout, mWidthPixels-windowWidth, mHeightPixels-windowHeight, true);
            settingInfoPopupWindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            settiong_info_close_btn = (Button) layout.findViewById(R.id.setting_info_close_btn);

            settiong_info_close_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    settingInfoPopupWindo.dismiss();
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
            charge_video_btn.setText(R.string.unityAdsReady);

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
        }

        @Override
        public void onUnityAdsStart(String zoneId) {
            DeviceLog.debug("onUnityAdsStart: " + zoneId);
        }

        @Override
        public void onUnityAdsFinish(String zoneId, UnityAds.FinishState result) {
            DeviceLog.debug("onUnityAdsFinish: " + zoneId + " - " + result);
            Toast.makeText(MainActivity.this, R.string.getting_lotto_number, Toast.LENGTH_LONG).show();
            if (UnityAds.FinishState.COMPLETED == result) {
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
                                    int drwNo = mPref.getInt("drwNo", 1);

                                    todayLottoGenerator = new TodayLottoGenerator(drwNo, DRW_LENGTH, SPECIAL_RESULT_LENGTH);

                                    int[][] todayNumbers = todayLottoGenerator.todayLottoNumbers();

                                    mProgressDialog.dismiss();
                                    todayLottoNumberPopupWindow(todayNumbers, R.string.today_lotto_spcial_number, 100, 800);
                                }
                                catch ( Exception e )
                                {
                                    e.printStackTrace();
                                }
                            }
                        }, 10);
                    }
                } );

                //TODO: 오퍼월 검수 후 텍스트 넣기
                charge_video_btn.setText(R.string.unityAdsWait);
                disableButton(charge_video_btn);
            }
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

    public void takeScreenshot(View v) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            v.setDrawingCacheEnabled(true);
            Bitmap bitmapDialog = Bitmap.createBitmap(v.getDrawingCache());
            v.setDrawingCacheEnabled(false);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float centerX = (width  - bitmapDialog.getWidth()) * 0.5f;
            float centerY = (height- bitmapDialog.getHeight()) * 0.5f;

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
            canvas.drawBitmap(bitmapDialog, centerX, centerY, paint);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "인생 역전 로또 번호 생성기");
        intent.putExtra(Intent.EXTRA_TEXT, "인생 한방으로 인생 역전 하세요!! \n 다운로드 : https://play.google.com/store/apps/details?id=com.gkwak.lottonumbergenerator");
        intent.putExtra(Intent.EXTRA_STREAM,  Uri.parse("file:///"+imageFile));
        startActivity(intent);
    }
}
