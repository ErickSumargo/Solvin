package id.solvin.dev.model.response;

import id.solvin.dev.model.basic.Question;
import id.solvin.dev.model.basic.Response;

/**
 * Created by edinofri on 29/10/2016.
 */

public class ResponseQuestion extends Response {
    public ResultData data;

    public ResultData getData() {
        return data;
    }

    public class ResultData {
        private Question question;

        public Question getQuestion() {
            return question;
        }

        public void setQuestion(Question question) {
            this.question = question;
        }

    }
}
