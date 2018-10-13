package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataComment;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/14/2017.
 */

public class ModelComment extends Response {
    private List<DataComment> commentList;

    private void setCommentList(List<DataComment> commentList) {
        this.commentList = commentList;
    }

    public List<DataComment> getCommentList() {
        return commentList;
    }
}