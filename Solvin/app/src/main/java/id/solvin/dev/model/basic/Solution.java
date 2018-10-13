package id.solvin.dev.model.basic;

import java.io.Serializable;

/**
 * Created by edinofri on 11/02/2017.
 */

public class Solution extends Model implements Serializable{
    private int mentor_id;
    private String content;
    private String image;
    private int question_id;
    private int best;
    private String created_at;
    private String updated_at;
    private Mentor mentor;

    public int getMentor_id() {
        return mentor_id;
    }

    public void setMentor_id(int mentor_id) {
        this.mentor_id = mentor_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public boolean isBest() {
        return best==1;
    }

    public void setBest(int best) {
        this.best = best;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }
}
