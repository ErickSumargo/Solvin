package id.solvinap.dev.server.model;

import java.util.List;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataBalanceBonus;

/**
 * Created by Erick Sumargo on 4/11/2017.
 */

public class ModelBalanceBonus extends Response {
    private List<DataBalanceBonus> balanceBonusList;

    private void setBalanceBonusList(List<DataBalanceBonus> balanceBonusList) {
        this.balanceBonusList = balanceBonusList;
    }

    public List<DataBalanceBonus> getBalanceBonusList() {
        return balanceBonusList;
    }
}