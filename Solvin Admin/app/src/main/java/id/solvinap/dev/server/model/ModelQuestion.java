package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataQuestion;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/13/2017.
 */

public class ModelQuestion extends Response {
    private List<DataQuestion> questionList;

    private void setQuestion(List<DataQuestion> questionList) {
        this.questionList = questionList;
    }

    public List<DataQuestion> getQuestionList() {
        return questionList;
    }
}