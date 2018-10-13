package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataFeedback;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/10/2017.
 */

public class ModelFeedback extends Response {
    private List<DataFeedback> feedbackList;

    private void setFeedbackList(List<DataFeedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    public List<DataFeedback> getFeedbackList() {
        return feedbackList;
    }
}