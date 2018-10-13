package id.solvin.dev.model.basic;

import id.solvin.dev.helper.SCrypt;

/**
 * Created by Erick Sumargo on 3/6/2017.
 */

public class MonthlyBalance {
    private String bestVote, balance, balanceBonus;
    private String dateStart, dateEnd;

    public int getBestVote() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(bestVote)));
        } catch (Exception e) {
        }
        return 0;
    }

    public long getBalance() {
        try {
            return Long.parseLong(new String(SCrypt.getInstance().decrypt(balance)));
        } catch (Exception e) {
        }
        return 0;
    }

    public long getBalanceBonus() {
        try {
            return Long.parseLong(new String(SCrypt.getInstance().decrypt(balanceBonus)));
        } catch (Exception e) {
        }
        return 0;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }
}