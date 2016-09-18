package com.gkwak.lottonumbergenerator.libs;

import android.os.AsyncTask;
import android.util.Log;

import com.gkwak.lottonumbergenerator.data.Lotto;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by gihyun on 2016-08-14.
 */
public class GetLottoNumTask extends AsyncTask<Void, Void, JSONObject> {

    private static String TAG = "GET_LOTTO_NUM_TASK";
    HttpRequest httpRequest = new HttpRequest();

    private String getUrl = "";
    public GetLottoNumTask(String url) {
        getUrl = url;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.


        JSONObject result = null;
        try {
            result = httpRequest.downloadUrl(getUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(final JSONObject success) {
        System.out.println("onPostExecute");
    }

    @Override
    protected void onCancelled() {

    }
}