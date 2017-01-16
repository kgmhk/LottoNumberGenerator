package com.gkwak.lottonumbergenerator.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gkwak.lottonumbergenerator.R;
import com.gkwak.lottonumbergenerator.libs.WebParser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class StoreActivity extends AppCompatActivity {

    private static String TAG = "STORE_ACTIVITY";
    private WebParser webParser = null;
    private LinearLayout topLayout;
    private Elements resultHtml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        // Add AdMob
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        assert mAdView != null;
        mAdView.loadAd(adRequest);

        topLayout = (LinearLayout) findViewById(R.id.store_top_container);

        String url = "http://www.nlotto.co.kr/store.do?method=topStore&pageGubun=L645";

        try {
            webParser = new WebParser(url, topLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.nlotto.co.kr/lotto645Confirm.do?method=topStoreLocation&gbn=lotto&rtlrId=11190018"));
//        startActivity(intent);
        try {
            resultHtml = webParser.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        int countNum = 0;
        for (Element table: resultHtml) {
            if (countNum == 0) {
                countNum++;
                continue;
            }
            // title layout
            int leftMargin=0;
            int topMargin=0;
            int rightMargin=0;
            int bottomMargin=50;
            TableLayout.LayoutParams titleParams=
                    new TableLayout.LayoutParams
                            (TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT, 1);
            titleParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

            TextView title = new TextView(this);
            title.setText(countNum++ + getResources().getString(R.string.winner_sell_place));
            title.setTextSize(20);
            title.setLayoutParams(titleParams);

            topLayout.addView(title);
            TableLayout.LayoutParams tableRowParams=
                    new TableLayout.LayoutParams
                            (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.MATCH_PARENT);
            TableLayout tl = new TableLayout(this);
            tl.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            Elements trs = table.getElementsByTag("tr");

            // table title

            Elements theads = table.getElementsByTag("thead");
            Log.i(TAG, theads.toString());

            for (Element thead: theads) {
                TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(tableRowParams);
//                tr.setBackgroundResource(R.drawable.table_row_border);
                Elements ths = thead.getElementsByTag("th");
                for (Element th: ths) {
                    TextView a = new TextView(this);
                    a.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT,1));
                    a.setText(th.ownText());
                    a.setTextSize(12);
                    a.setGravity(Gravity.CENTER);
                    a.setTypeface(null, Typeface.BOLD);
                    tableRow.addView(a);
                }
                tl.addView(tableRow);
            }

            for (Element tr: trs) {
                TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(tableRowParams);
//                tr.setBackgroundResource(R.drawable.table_row_border);
                Elements tds = tr.getElementsByTag("td");
                for (Element td: tds) {
                    Elements aTag = td.getElementsByTag("a");
                    if (!aTag.toString().isEmpty()) {
                        ImageButton btn = new ImageButton(this);
                        String addr = aTag.attr("onclick").replaceAll("[^0-9]", "");
                        btn.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        btn.setImageResource(android.R.drawable.ic_dialog_map);
                        MyLovelyOnClickListener myClass = new MyLovelyOnClickListener(addr);
                        btn.setOnClickListener(myClass);
                        tableRow.addView(btn);
                    } else {
                        String tempString = td.ownText();
                        String[] splitedString = tempString.split(" ");
                        TextView a = new TextView(this);
                        a.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT,1));
                        if (splitedString.length > 2) {
                            a.setText(splitedString[0] + " " + splitedString[1] + " " + splitedString[2]);
                        } else {
                            a.setText(tempString);
                        }
                        a.setTextSize(10);
                        a.setGravity(Gravity.CENTER_VERTICAL);
                        a.setTypeface(null, Typeface.BOLD);
//                    a.setGravity(Gravity.CENTER);
                        tableRow.addView(a);
                    }
                }
                tl.addView(tableRow);
            }
            topLayout.addView(tl);
        }
//        Log.i(TAG, resultHtml.toString());

//        TextView tv = new TextView(this);
//        tv.setText(Html.fromHtml(resultHtml.html()));
//        topLayout.addView(tv);
    }

    public class MyLovelyOnClickListener implements View.OnClickListener
    {

        String addr;
        public MyLovelyOnClickListener(String addr) {
            this.addr = addr;
        }

        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.nlotto.co.kr/lotto645Confirm.do?method=topStoreLocation&gbn=lotto&rtlrId=" + addr));
            startActivity(intent);
        }

    };
}
