package com.gkwak.lottonumbergenerator.libs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebParser extends AsyncTask<Elements, Void, Elements> {

    private static String TAG = "WEB_PARSER";
    private String url = "";
    private LinearLayout topLayout;
//    private Activity mainActivity = null;
    public WebParser(String url, LinearLayout topLayout) throws IOException {
        this.url = url;
        this.topLayout = topLayout;
//        this.mainActivity = mainActivity;
    }

    @Override
    protected Elements doInBackground(Elements... strings) {
        Elements elements = new Elements();
        try {

            //  지도 주소
            // http://www.nlotto.co.kr/lotto645Confirm.do?method=topStoreLocation&gbn=lotto&rtlrId=11190018

//            Document doc = Jsoup.connect(url)
//                    .header("content-type", "multipart/form-data; boundary=---011000010111000001101001")
//                    .header("authorization", "Basic ZmJfMTY2MTUzMDc0NzQ2MTk5NDox").header("cache-control", "no-cache")
//                    .get();
//
//            Elements title = doc.select("div.title_bar");
//            for (Element e : title) {
//                System.out.println(e.text());
//            }

            Log.d("JSwa", "Connecting to ["+url+"]");
            Document doc = Jsoup.connect(url).get();
// Get document (HTML page) title
            Elements tbody = doc.getElementsByTag("tbody");
//            for (Element body: tbody) {
//                Log.i(TAG, "------table------------");
//                Elements trs = body.getElementsByTag("tr");
//                for (Element tr: trs) {
//                    Log.i(TAG, "-----------tr------------");
//                    Log.i(TAG, tr.toString());
//                    Elements tds = tr.getElementsByTag("td");
//                    for (Element td: tds) {
//                        Log.i(TAG, "-----------td------------");
////                        Log.i(TAG, td.toString());
//                        Log.i(TAG + "val", td.ownText());
//                    }
//
//                }
//            }
            return tbody;

        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        return elements;
    }

    @Override
    protected void onPostExecute(Elements s) {
        super.onPostExecute(s);
//        respText.setText(s);
    }
}
