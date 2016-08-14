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
//            winNumber[0] = result.getInt("drwtNo1");
//            winNumber[1] = result.getInt("drwtNo2");
//            winNumber[2] = result.getInt("drwtNo3");
//            winNumber[3] = result.getInt("drwtNo4");
//            winNumber[4] = result.getInt("drwtNo5");
//            winNumber[5] = result.getInt("drwtNo6");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(final JSONObject success) {
        System.out.println("onPostExecute");
//
//        if (success.success) {
//            Log.i("finish ", "");
//        } else {
//            Log.i("error ", "");
//        }
    }

    @Override
    protected void onCancelled() {

    }
}