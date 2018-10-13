package id.solvin.dev.model.basic;


import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by edinofri on 30/09/2016.
 */

public class User extends Auth implements Serializable {
    private int credit;
    private String credit_timelife;
    private String sekolah;

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getCredit_timelife() {
        return credit_timelife;
    }

    public void setCredit_timelife(String credit_timelife) {
        this.credit_timelife = credit_timelife;
    }


    public String getSekolah() {
        if(sekolah==null||TextUtils.isEmpty(sekolah)){
            return "-";
        }
        return sekolah;
    }

    public void setSekolah(String sekolah) {
        this.sekolah = sekolah;
    }

    @Override
    public String toString() {
        return "";
    }
}
