package id.solvinap.dev.server.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Erick Sumargo on 2/13/2017.
 */

public class DataQuestion implements Serializable {
    private int id, status, active;
    private String content, image, other;

    @SerializedName("student_id")
    private int studentId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("student")
    private DataStudent dataStudent;

    @SerializedName("material")
    private DataMaterial dataMaterial;

    @SerializedName("solution")
    private DataSolution dataSolution;

    private void setId(int id) {
        this.id = id;
    }

    private void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    private void setStatus(int status) {
        this.status = status;
    }

    private void setContent(String content) {
        this.content = content;
    }

    private void setImage(String image) {
        this.image = image;
    }

    private void setOther(String other) {
        this.other = other;
    }

    private void setActive(int active) { this.active = active; }

    private void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    private void setDataStudent(DataStudent dataStudent) {
        this.dataStudent = dataStudent;
    }

    private void setDataMaterial(DataMaterial dataMaterial) {
        this.dataMaterial = dataMaterial;
    }

    private void setDataSolution(DataSolution dataSolution) {
        this.dataSolution = dataSolution;
    }

    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public String getOther() {
        return other;
    }

    public int getActive() { return active; }

    public String getCreatedAt() {
        return createdAt;
    }

    public DataStudent getDataStudent() {
        return dataStudent;
    }

    public DataMaterial getDataMaterial() {
        return dataMaterial;
    }

    public DataSolution getDataSolution() {
        return dataSolution;
    }
}