package id.solvinap.dev.server.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Erick Sumargo on 2/16/2017.
 */

public class DataTransaction implements Serializable {
    private int id, status, clear;

    @SerializedName("unique_code")
    private int uniqueCode;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("package_")
    private DataPackage dataPackage;

    @SerializedName("transaction_confirm")
    private DataTransactionConfirm dataTransactionConfirm;

    @SerializedName("student")
    private DataStudent dataStudent;

    private void setId(int id) {
        this.id = id;
    }

    private void setStatus(int status) {
        this.status = status;
    }

    private void setClear(int clear) {
        this.clear = clear;
    }

    private void setUniqueCode(int uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    private void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    private void setDataPackage(DataPackage dataPackage) {
        this.dataPackage = dataPackage;
    }

    private void setDataTransactionConfirm(DataTransactionConfirm dataTransactionConfirm) {
        this.dataTransactionConfirm = dataTransactionConfirm;
    }

    private void setDataStudent(DataStudent dataStudent) {
        this.dataStudent = dataStudent;
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public int getClear() {
        return clear;
    }

    public int getUniqueCode() {
        return uniqueCode;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public DataPackage getDataPackage() {
        return dataPackage;
    }

    public DataTransactionConfirm getDataTransactionConfirm() {
        return dataTransactionConfirm;
    }

    public DataStudent getDataStudent() {
        return dataStudent;
    }
}