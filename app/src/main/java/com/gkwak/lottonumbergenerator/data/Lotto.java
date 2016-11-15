package com.gkwak.lottonumbergenerator.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Lotto {

    private int bnusNo;
    private String firstWinamnt;
    private int totSellamnt;
    private Boolean returnValue;
    private int drwtNo1;
    private int drwtNo2;
    private int drwtNo3;
    private int drwtNo4;
    private int drwtNo5;
    private int drwtNo6;
    private int drwNo;
    private int firstPrzwnerCo;
    private String drwNoDate;
    private int[] winNumbers;
    private String winNumberStr;


    public Lotto(JSONObject lotto) {
        try {
            this.bnusNo = lotto.isNull("bnusNo") ? 0 : lotto.getInt("bnusNo");
            this.firstWinamnt = lotto.isNull("firstWinamnt") ? "0" : lotto.getLong("firstWinamnt")+"";
            this.totSellamnt = lotto.isNull("totSellamnt") ? 0 : lotto.getInt("totSellamnt");
            this.returnValue = lotto.isNull("returnValue") ? false : (lotto.getString("returnValue").equals("success"));
            this.drwtNo1 = lotto.isNull("drwtNo1") ? 1 : lotto.getInt("drwtNo1");
            this.drwtNo2 = lotto.isNull("drwtNo2") ? 1 : lotto.getInt("drwtNo2");
            this.drwtNo3 = lotto.isNull("drwtNo3") ? 1 : lotto.getInt("drwtNo3");
            this.drwtNo4 = lotto.isNull("drwtNo4") ? 1 : lotto.getInt("drwtNo4");
            this.drwtNo5 = lotto.isNull("drwtNo5") ? 1 : lotto.getInt("drwtNo5");
            this.drwtNo6 = lotto.isNull("drwtNo6") ? 1 : lotto.getInt("drwtNo6");
            this.drwNo = lotto.isNull("drwNo") ? 0 : lotto.getInt("drwNo");
            this.firstPrzwnerCo = lotto.isNull("firstPrzwnerCo") ? 0 : lotto.getInt("firstPrzwnerCo");
            this.drwNoDate = lotto.isNull("drwNoDate") ? "0000-00-00" : lotto.getString("drwNoDate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int[] getWinNumbers() {
        winNumbers = new int[]{this.drwtNo1, this.drwtNo2, this.drwtNo3, this.drwtNo4, this.drwtNo5, this.drwtNo6, this.bnusNo};
        return winNumbers;
    }

    public String getWinNumberStr() {
        winNumbers = new int[]{this.drwtNo1, this.drwtNo2, this.drwtNo3, this.drwtNo4, this.drwtNo5, this.drwtNo6, this.bnusNo};
        for(int i = 0; i < winNumbers.length; i++) {
            if (i == 0) winNumberStr = winNumbers[i] + ",";
            else winNumberStr += winNumbers[i] + ",";
        }
        return winNumberStr;
    }

    public int getBnusNo() {
        return bnusNo;
    }

    public String getFirstWinamnt() {
        return firstWinamnt;
    }

    public int getTotSellamnt() {
        return totSellamnt;
    }

    public Boolean getReturnValue() {
        return returnValue;
    }

    public int getDrwtNo1() {
        return drwtNo1;
    }

    public int getDrwtNo2() {
        return drwtNo2;
    }

    public int getDrwtNo3() {
        return drwtNo3;
    }

    public int getDrwtNo4() {
        return drwtNo4;
    }

    public int getDrwtNo5() {
        return drwtNo5;
    }

    public int getDrwtNo6() {
        return drwtNo6;
    }

    public int getDrwNo() {
        return drwNo;
    }

    public int getFirstPrzwnerCo() {
        return firstPrzwnerCo;
    }

    public String getDrwNoDate() {
        return drwNoDate;
    }
}
