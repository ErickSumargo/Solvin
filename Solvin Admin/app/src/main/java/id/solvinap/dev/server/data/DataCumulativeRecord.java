package id.solvinap.dev.server.data;

import id.solvinap.dev.view.helper.SCrypt;

/**
 * Created by Erick Sumargo on 2/22/2017.
 */

public class DataCumulativeRecord {
    private String totalStudent, totalMentor;
    private String totalIncome, totalBalanceRedeemed;

    public int getTotalStudent() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(totalStudent)));
        } catch (Exception e) {
        }
        return 0;
    }

    public int getTotalMentor() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(totalMentor)));
        } catch (Exception e) {
        }
        return 0;
    }

    public long getTotalIncome() {
        try {
            return Long.parseLong(new String(SCrypt.getInstance().decrypt(totalIncome)));
        } catch (Exception e) {
        }
        return 0;
    }

    public long getTotalBalanceRedeemed() {
        try {
            return Long.parseLong(new String(SCrypt.getInstance().decrypt(totalBalanceRedeemed)));
        } catch (Exception e) {
        }
        return 0;
    }
}