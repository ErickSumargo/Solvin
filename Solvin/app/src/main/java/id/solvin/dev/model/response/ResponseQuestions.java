package id.solvin.dev.model.response;


import id.solvin.dev.helper.SCrypt;
import id.solvin.dev.model.basic.Base;
import id.solvin.dev.model.basic.Question;
import id.solvin.dev.model.basic.Response;

import java.util.List;

/**
 * Created by edinofri on 29/10/2016.
 */

public class ResponseQuestions extends Response {
    private ResultData data;

    public ResultData getData() {
        return data;
    }

    public class ResultData extends Base {
        private List<Question> questions;
        private String max_id;
        private boolean related;

        public List<Question> getQuestions() {
            return questions;
        }

        public int getMax_id() {
            try {
                return Integer.parseInt(new String(SCrypt.getInstance().decrypt(max_id)));
            } catch (Exception e) {
            }
            return -1;
        }

        public boolean isRelated() {
            return related;
        }
    }
}