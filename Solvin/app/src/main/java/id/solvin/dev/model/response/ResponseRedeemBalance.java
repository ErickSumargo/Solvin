package id.solvin.dev.model.response;

import id.solvin.dev.helper.SCrypt;
import id.solvin.dev.model.basic.Base;
import id.solvin.dev.model.basic.RedeemBalance;
import id.solvin.dev.model.basic.Response;

import java.util.List;

/**
 * Created by Erick Sumargo on 3/6/2017.
 */

public class ResponseRedeemBalance extends Response {
    private ResultData data;

    public ResultData getData() {
        return data;
    }

    public class ResultData extends Base {
        private String totalBalance, balanceBonus, balanceRedeemed;
        private List<RedeemBalance> redeemBalance;

        public int getTotalBalance() {
            try {
                return Integer.parseInt(new String(SCrypt.getInstance().decrypt(totalBalance)));
            } catch (Exception e) {
            }
            return 0;
        }

        public int getBalanceBonus() {
            try {
                return Integer.parseInt(new String(SCrypt.getInstance().decrypt(balanceBonus)));
            } catch (Exception e) {
            }
            return 0;
        }

        public int getBalanceRedeemed() {
            try {
                return Integer.parseInt(new String(SCrypt.getInstance().decrypt(balanceRedeemed)));
            } catch (Exception e) {
            }
            return 0;
        }

        public List<RedeemBalance> getRedeemBalance() {
            return redeemBalance;
        }
    }
}