package id.solvin.dev.model.response;

import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.Transaction;

import java.io.Serializable;

/**
 * Created by edinofri on 08/01/2017.
 */

public class ResponseTransaction extends Response implements Serializable {
    private ResultData data;

    public ResultData getData() {
        return data;
    }

    public class ResultData {
        private Transaction transaction;

        public Transaction getTransaction() {
            return transaction;
        }

        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }
    }
}
