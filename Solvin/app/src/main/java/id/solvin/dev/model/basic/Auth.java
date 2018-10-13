package id.solvin.dev.model.basic;

import id.solvin.dev.helper.SCrypt;

import java.io.Serializable;

/**
 * Created by edinofri on 02/11/2016.
 */


public class Auth extends Model implements Serializable {
    private String name;
    private String email;

    private String phone;
    private String photo;
    private String created_at;
    private String updated_at;
    private String address;
    private String birth;
    private int age;
    private String member_code;
    private String join_time;

    protected String auth_type;
    /**
     * student
     */
    protected String school;
    protected int question_count;
    protected String credit;
    protected String credit_timelife;
    protected String credit_expired;
    /**
     * mentor
     */
    protected String workplace;
    protected String best_count;
    protected String solution_count;
    protected String balance_amount;

    public static final String AUTH_TYPE_USER = "user";
    public static final String AUTH_TYPE_STUDENT = "student";
    public static final String AUTH_TYPE_MENTOR = "mentor";

    public String getName() {
        return getSafeString(name);
    }

    public String getEmail() {
        return getSafeString(email);
    }

    public String getPhone() {
        return getSafeString(phone);
    }

    public String getPhoto() {
        return getSafeString(photo);
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getAddress() {
        return getSafeString(address);
    }

    public String getBirth() {
        return getSafeString(birth, "0000-00-00");
    }

    public int getAge() {
        return age;
    }

    public String getMember_code() {
        if (member_code != null) {
            return member_code.toUpperCase();
        } else {
            return "";
        }
    }

    public String getAuth_type() {
        return auth_type;
    }

    public void setAuth_type(String auth_type) {
        this.auth_type = auth_type;
    }

    public int getQuestion_count() {
        return question_count;
    }

    public void setQuestion_count(int question_count) {
        this.question_count = question_count;
    }

    public int getBest_count() {
        if (this.auth_type.equals(Auth.AUTH_TYPE_MENTOR)) {
            try {
                return Integer.parseInt(new String(SCrypt.getInstance().decrypt(best_count)));
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public int getSolution_count() {
        if (this.auth_type.equals(Auth.AUTH_TYPE_MENTOR)) {
            try {
                return Integer.parseInt(new String(SCrypt.getInstance().decrypt(solution_count)));
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public int getBalanceAmount() {
        if (this.auth_type.equals(Auth.AUTH_TYPE_MENTOR)) {
            try {
                return Integer.parseInt(new String(SCrypt.getInstance().decrypt(balance_amount)));
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public String getJoin_time() {
        return join_time;
    }

    private void setJoin_time(String join_time) {
        this.join_time = join_time;
    }
}