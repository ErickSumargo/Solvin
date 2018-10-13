package id.solvin.dev.model.response;

import id.solvin.dev.model.basic.Base;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.Transaction;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by edinofri on 03/03/2017.
 */

public class ResponseTransactions extends Response implements Serializable {
    @SerializedName("data")
    private ResultData dataTransaction;

    public ResultData getDataTransaction() {
        return dataTransaction;
    }

    public class ResultData extends Base {
        private List<Transaction> transactions;

        public List<Transaction> getTransactions() {
            return transactions;
        }
    }
}