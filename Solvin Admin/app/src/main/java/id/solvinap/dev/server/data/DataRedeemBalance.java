package id.solvinap.dev.server.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Erick Sumargo on 2/16/2017.
 */

public class DataRedeemBalance {
    private int id, status;
    private long balance;

    @SerializedName("redeem_code")
    private String redeemCode;

    @SerializedName("date_agreement")
    private String dateAgreement;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("mentor")
    private DataMentor dataMentor;

    private void setId(int id) {
        this.id = id;
    }

    private void setStatus(int status) {
        this.status = status;
    }

    private void setBalance(long balance) {
        this.balance = balance;
    }

    private void setRedeemCode(String redeemCode) {
        this.redeemCode = redeemCode;
    }

    private void setDateAgreement(String dateAgreement) {
        this.dateAgreement = dateAgreement;
    }

    private void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    private void setDataMentor(DataMentor dataMentor) {
        this.dataMentor = dataMentor;
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public long getBalance() {
        return balance;
    }

    public String getRedeemCode() {
        return redeemCode;
    }

    public String getDateAgreement() {
        return dateAgreement;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public DataMentor getDataMentor() {
        return dataMentor;
    }
}