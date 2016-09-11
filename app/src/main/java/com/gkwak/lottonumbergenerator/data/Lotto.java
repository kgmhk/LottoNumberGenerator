package com.gkwak.lottonumbergenerator.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Lotto {

    private int bnusNo;
    private int firstWinamnt;
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


    public Lotto(JSONObject lotto) {
        try {
            this.bnusNo = lotto.getInt("bnusNo");
            this.firstWinamnt = lotto.getInt("firstWinamnt");
            this.totSellamnt = lotto.getInt("totSellamnt");
            this.returnValue = Boolean.parseBoolean(lotto.getString("returnValue"));
            this.drwtNo1 = lotto.getInt("drwtNo1");
            this.drwtNo2 = lotto.getInt("drwtNo2");
            this.drwtNo3 = lotto.getInt("drwtNo3");
            this.drwtNo4 = lotto.getInt("drwtNo4");
            this.drwtNo5 = lotto.getInt("drwtNo5");
            this.drwtNo6 = lotto.getInt("drwtNo6");
            this.drwNo = lotto.getInt("drwNo");
            this.firstPrzwnerCo = lotto.getInt("firstPrzwnerCo");
            this.drwNoDate = lotto.getString("drwNoDate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int[] getWinNumber() {
        int[] winNumbers = new int[]{this.drwtNo1, this.drwtNo2, this.drwtNo3, this.drwtNo4, this.drwtNo5, this.drwtNo6};
        return winNumbers;
    }

    public int getBnusNo() {
        return bnusNo;
    }

    public int getFirstWinamnt() {
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
