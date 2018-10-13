package id.solvin.dev.model.basic;

import id.solvin.dev.helper.SCrypt;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by edinofri on 02/11/2016.
 */

public class Mentor extends Auth implements Serializable {
    public String getWorkplace() {
        if (this.auth_type.equals(AUTH_TYPE_MENTOR)) {
            return getSafeString(workplace);
        }
        return null;
    }

    public int getBestCount() {
        if (this.auth_type.equals(AUTH_TYPE_MENTOR)) {
            try {
                return Integer.parseInt(new String(SCrypt.getInstance().decrypt(best_count)));
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public int getSolutionCount() {
        if (this.auth_type.equals(AUTH_TYPE_MENTOR)) {
            try {
                return Integer.parseInt(new String(SCrypt.getInstance().decrypt(solution_count)));
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public int getBalanceAmount() {
        if (this.auth_type.equals(AUTH_TYPE_MENTOR)) {
            try {
                return Integer.parseInt(new String(SCrypt.getInstance().decrypt(balance_amount)));
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public static Mentor parseToMentor(Auth auth) {
        return new Gson().fromJson(new Gson().toJson(auth), Mentor.class);
    }
}