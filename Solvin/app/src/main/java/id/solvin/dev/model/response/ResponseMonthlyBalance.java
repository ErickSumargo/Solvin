package id.solvin.dev.model.response;

import id.solvin.dev.model.basic.MonthlyBalance;
import id.solvin.dev.model.basic.Response;

import java.util.List;

/**
 * Created by Erick Sumargo on 3/6/2017.
 */

public class ResponseMonthlyBalance extends Response {
    private List<MonthlyBalance> data;

    public List<MonthlyBalance> getMonthlyBalanceList() {
        return data;
    }

    private void setMonthlyBalanceList(List<MonthlyBalance> data) {
        this.data = data;
    }
}