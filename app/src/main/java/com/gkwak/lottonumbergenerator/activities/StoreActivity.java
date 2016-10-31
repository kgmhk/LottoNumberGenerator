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
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gkwak.lottonumbergenerator.R;
import com.gkwak.lottonumbergenerator.libs.WebParser;

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

        topLayout = (LinearLayout) findViewById(R.id.store_top_container);

        String url = "http://www.nlotto.co.kr/lotto645Confirm.do?method=topStore&pageGubun=L645";

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



        for (Element table: resultHtml) {
            TableLayout.LayoutParams tableRowParams=
                    new TableLayout.LayoutParams
                            (TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT, 1);
            TableLayout tl = new TableLayout(this);
            tl.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            Elements trs = table.getElementsByTag("tr");
            for (Element tr: trs) {
                TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(tableRowParams);
//                tr.setBackgroundResource(R.drawable.table_row_border);
                Elements tds = tr.getElementsByTag("td");
                for (Element td: tds) {
                    Elements aTag = td.getElementsByTag("a");
                    if (!aTag.toString().isEmpty()) {
                        Button btn = new Button(this);
                        String addr = aTag.attr("onclick").replaceAll("[^0-9]", "");
                        btn.setText(addr);
                        MyLovelyOnClickListener myClass = new MyLovelyOnClickListener(addr);
                        btn.setOnClickListener(myClass);

                        tableRow.addView(btn);
                        Log.i(TAG, "여기는 주소");
                    } else {
                        TextView a = new TextView(this);
                        a.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
                        a.setText(td.ownText());
                        a.setTextSize(9);
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
