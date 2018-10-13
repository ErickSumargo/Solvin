package id.solvinap.dev.server.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Erick Sumargo on 2/14/2017.
 */

public class DataComment implements Serializable {
    private int id, active;
    private String content, image;

    @SerializedName("question_id")
    private int questionId;

    @SerializedName("auth_id")
    private int authId;

    @SerializedName("auth_type")
    private String authType;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("question")
    private DataQuestion dataQuestion;

    private void setId(int id) {
        this.id = id;
    }

    private void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    private void setAuthId(int authId) {
        this.authId = authId;
    }

    private void setAuthType(String authType) {
        this.authType = authType;
    }

    private void setContent(String content) {
        this.content = content;
    }

    private void setImage(String image) {
        this.image = image;
    }

    private void setActive(int active) { this.active = active; }

    private void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    private void setDataQuestion(DataQuestion dataQuestion) {
        this.dataQuestion = dataQuestion;
    }

    public int getId() {
        return id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public int getAuthId() {
        return authId;
    }

    public String getAuthType() {
        return authType;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public int getActive() { return active; }

    public String getCreatedAt() {
        return createdAt;
    }

    public DataQuestion getDataQuestion() {
        return dataQuestion;
    }
}