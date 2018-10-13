package id.solvin.dev.model.basic;

import java.io.Serializable;
import java.util.List;

/**
 * Created by edinofri on 11/02/2017.
 */

public class Question extends Model implements Serializable {
    private int student_id;
    private Student student;
    private int status;
    private int material_id;
    private Material material;
    private List<Solution> solutions;
    private List<Comment> comments;
    private String image;
    private String content;
    private String other;
    private String created_at;
    private String updated_at;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMaterial_id(int material_id) {
        this.material_id = material_id;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getStudent_id() {
        return student_id;
    }

    public Student getStudent() {
        return student;
    }

    public int getStatus() {
        return status;
    }

    public int getMaterial_id() {
        return material_id;
    }

    public Material getMaterial() {
        return material;
    }

    public String getImage() {
        return image;
    }

    public String getContent() {
        return content;
    }

    public String getOther() {
        return other;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solution> solutions) {
        this.solutions = solutions;
    }
}
