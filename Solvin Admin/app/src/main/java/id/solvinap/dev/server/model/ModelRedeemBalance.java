package id.solvinap.dev.server.model;


import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataRedeemBalance;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/16/2017.
 */

public class ModelRedeemBalance extends Response {
    private List<DataRedeemBalance> redeemBalanceList;

    private void setRedeemBalanceList(List<DataRedeemBalance> redeemBalanceList) {
        this.redeemBalanceList = redeemBalanceList;
    }

    public List<DataRedeemBalance> getRedeemBalanceList() {
        return redeemBalanceList;
    }
}