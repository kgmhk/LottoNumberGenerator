package com.gkwak.lottonumbergenerator.libs;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebParser extends AsyncTask<String, Void, String> {

    private static String TAG = "WEB_PARSER";
    private String url = "";
    public WebParser(String url) throws IOException {
        this.url = url;
    }

    @Override
    protected String doInBackground(String... strings) {
        StringBuffer buffer = new StringBuffer();
        try {
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
//            Log.d("JSwa", "Connected to ["+strings[0]+"]");
// Get document (HTML page) title
            String title = doc.title();
            Log.d("JSwA", "Title ["+title+"]");
            buffer.append("Title: " + title + "rn");

// Get meta info
            Elements metaElems = doc.select("meta");
            buffer.append("META DATArn");
            for (Element metaElem : metaElems) {
                String name = metaElem.attr("name");
                String content = metaElem.attr("content");
                buffer.append("name ["+name+"] - content ["+content+"] rn");
            }

            Elements topicList = doc.select("h2.topic");
            buffer.append("Topic listrn");
            for (Element topic : topicList) {
                String data = topic.text();

                buffer.append("Data ["+data+"] rn");
            }

        }
        catch(Throwable t) {
            t.printStackTrace();
        }

        return buffer.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        respText.setText(s);
    }
}
