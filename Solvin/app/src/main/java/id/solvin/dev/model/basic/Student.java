package id.solvin.dev.model.basic;

import id.solvin.dev.helper.SCrypt;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by edinofri on 10/02/2017.
 */

public class Student extends Auth implements Serializable {
    public String getSchool() {
        if (this.auth_type.equals(AUTH_TYPE_STUDENT)) {
            return getSafeString(school);
        }
        return null;
    }

    public int getQuestionCount() {
        if (this.auth_type.equals(AUTH_TYPE_STUDENT)) {
            return question_count;
        }
        return -1;
    }

    public int getCredit() {
        if (this.auth_type.equals(AUTH_TYPE_STUDENT)) {
            try {
                return Integer.parseInt(new String(SCrypt.getInstance().decrypt(credit)));
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public String getCredit_timelife() {
        if (this.auth_type.equals(AUTH_TYPE_STUDENT)) {
            try {
                return new String(SCrypt.getInstance().decrypt(getSafeStringDefault(credit_timelife, "0000-00-00 00:00:00")));
            } catch (Exception e) {
            }
        }
        return "";
    }

    public boolean isCreditExpired() {
        if (this.auth_type.equals(AUTH_TYPE_STUDENT)) {
            if (getSafeString(credit_expired).equals("true")) {
                return true;
            } else if (getSafeString(credit_expired).equals("false")) {
                return false;
            }
        }
        return true;
    }

    public static Student parseToStudent(Auth auth) {
        return new Gson().fromJson(new Gson().toJson(auth), Student.class);
    }
}