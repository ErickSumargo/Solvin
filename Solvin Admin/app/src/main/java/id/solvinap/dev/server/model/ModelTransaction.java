package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataTransaction;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/16/2017.
 */

public class ModelTransaction extends Response {
    private List<DataTransaction> transactionList;

    private void setTransactionList(List<DataTransaction> transactionList) {
        this.transactionList = transactionList;
    }

    public List<DataTransaction> getTransactionList() {
        return transactionList;
    }
}