package id.solvinap.dev.server.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Erick Sumargo on 2/21/2017.
 */

public class DataTransactionConfirm {
    private int id;
    private String image;

    @SerializedName("bank_account_owner")
    private String bankAccountOwner;

    @SerializedName("bank_name")
    private String bankName;

    @SerializedName("bank_name_other")
    private String bankNameOther;

    @SerializedName("created_at")
    private String createdAt;

    private void setId(int id) {
        this.id = id;
    }

    private void setBankAccountOwner(String bankAccountOwner) {
        this.bankAccountOwner = bankAccountOwner;
    }

    private void setBankName(String bankName) {
        this.bankName = bankName;
    }

    private void setBankNameOther(String bankNameOther) {
        this.bankNameOther = bankNameOther;
    }

    private void setImage(String image) {
        this.image = image;
    }

    private void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getBankAccountOwner() {
        return bankAccountOwner;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankNameOther() {
        return bankNameOther;
    }

    public String getImage() {
        return image;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
