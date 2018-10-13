package id.solvin.dev.model.basic;

import id.solvin.dev.helper.SCrypt;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Erick Sumargo on 3/6/2017.
 */

public class RedeemBalance {
    private int id;
    private String status, balance;

    @SerializedName("redeem_code")
    private String redeemCode;

    @SerializedName("date_agreement")
    private String dateAgreement;

    @SerializedName("created_at")
    private String createdAt;

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(status)));
        } catch (Exception e) {
        }
        return 0;
    }

    public int getBalance() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(balance)));
        } catch (Exception e) {
        }
        return 0;
    }

    public String getRedeemCode() {
        return redeemCode;
    }

    private void setRedeemCode(String redeemCode) {
        this.redeemCode = redeemCode;
    }

    public String getDateAgreement() {
        try {
            return new String(SCrypt.getInstance().decrypt(dateAgreement));
        } catch (Exception e) {
        }
        return "";
    }

    private void setDateAgreement(String dateAgreement) {
        this.dateAgreement = dateAgreement;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}