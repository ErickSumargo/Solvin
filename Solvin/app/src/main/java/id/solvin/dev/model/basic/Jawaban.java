package id.solvin.dev.model.basic;

import java.io.Serializable;

/**
 * Created by edinofri on 12/11/2016.
 */

public class Jawaban implements Serializable {
    private int id;
    private int mentor_id;
    private int question_id;
    private String content;
    private String image;
    private String created_at;
    private String updated_at;
    private int best;
    private Mentor mentor;

    public boolean isBest() {
        return best==1?true:false;
    }

    public void setBest(int best) {
        this.best = best;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMentor_id() {
        return mentor_id;
    }

    public void setMentor_id(int mentor_id) {
        this.mentor_id = mentor_id;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
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
