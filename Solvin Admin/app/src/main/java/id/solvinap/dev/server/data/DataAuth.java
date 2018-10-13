package id.solvinap.dev.server.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Erick Sumargo on 2/12/2017.
 */

public class DataAuth implements Serializable {
    private int id, age, active;
    private String name, email, phone, photo, address, birth;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("created_at")
    private String joinDate;

    @SerializedName("auth_type")
    private String authType;

    @SerializedName("auth_total_question")
    private int authTotalQuestion;

    @SerializedName("auth_total_best_solution")
    private int authTotalBestSolution;

    @SerializedName("auth_total_solution")
    private int authTotalSolution;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public void setAuthTotalQuestion(int authTotalQuestion) {
        this.authTotalQuestion = authTotalQuestion;
    }

    public void setAuthTotalBestSolution(int authTotalBestSolution) {
        this.authTotalBestSolution = authTotalBestSolution;
    }

    public void setAuthTotalSolution(int authTotalSolution) {
        this.authTotalSolution = authTotalSolution;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoto() {
        return photo;
    }

    public String getAddress() {
        return address;
    }

    public String getBirth() {
        return birth;
    }

    public int getAge() {
        return age;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public String getAuthType() {
        return authType;
    }

    public int getAuthTotalQuestion() {
        return authTotalQuestion;
    }

    public int getAuthTotalBestSolution() {
        return authTotalBestSolution;
    }

    public int getAuthTotalSolution() {
        return authTotalSolution;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}