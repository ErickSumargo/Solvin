package id.solvin.dev.model.basic;

import java.io.Serializable;

import id.solvin.dev.helper.SCrypt;

/**
 * Created by edinofri on 08/01/2017.
 */

public class Transaction extends Model implements Serializable {
    private int student_id;
    private String package_id, bank_id, mobile_network_id, status, unique_code;
    private String payment_time;
    private String updated_at;
    private String created_at;
    private Paket package_;

    public int getPackageId() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(package_id)));
        } catch (Exception e) {
        }
        return 0;
    }

    public int getBank_id() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(bank_id)));
        } catch (Exception e) {
        }
        return 0;
    }

    public int getMobileNetwork_id() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(mobile_network_id)));
        } catch (Exception e) {
        }
        return 0;
    }

    public int getStatus() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(status)));
        } catch (Exception e) {
        }
        return 0;
    }

    public int getUnique_code() {
        try {
            return Integer.parseInt(new String(SCrypt.getInstance().decrypt(unique_code)));
        } catch (Exception e) {
        }
        return 0;
    }

    public String getPayment_time() {
        return payment_time;
    }

    public void setPayment_time(String payment_time) {
        this.payment_time = payment_time;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Paket getPackage() {
        return package_;
    }

    public void setPackage(Paket package_) {
        this.package_ = package_;
    }
}