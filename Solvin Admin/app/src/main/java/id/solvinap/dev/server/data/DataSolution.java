package id.solvinap.dev.server.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Erick Sumargo on 2/16/2017.
 */

public class DataSolution implements Serializable {
    private int id, best, active;
    private String content, image;

    @SerializedName("mentor_id")
    private int mentorId;

    @SerializedName("question_id")
    private int questionId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("mentor")
    private DataMentor dataMentor;

    @SerializedName("question")
    private DataQuestion dataQuestion;

    private void setId(int id) {
        this.id = id;
    }

    private void setMentorId(int mentorId) {
        this.mentorId = mentorId;
    }

    private void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    private void setContent(String content) {
        this.content = content;
    }

    private void setImage(String image) {
        this.image = image;
    }

    private void setBest(int best) {
        this.best = best;
    }

    private void setActive(int active) { this.active = active; }

    private void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    private void setDataMentor(DataMentor dataMentor) {
        this.dataMentor = dataMentor;
    }

    private void setDataQuestion(DataQuestion dataQuestion) {
        this.dataQuestion = dataQuestion;
    }

    public int getId() {
        return id;
    }

    public int getMentorId() {
        return mentorId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public int getBest() {
        return best;
    }

    public int getActive() { return active; }

    public String getCreatedAt() {
        return createdAt;
    }

    public DataMentor getDataMentor() {
        return dataMentor;
    }

    public DataQuestion getDataQuestion() {
        return dataQuestion;
    }
}