package id.solvin.dev.model.basic;

import java.io.Serializable;

/**
 * Created by edinofri on 03/12/2016.
 */

public class TransferCategory implements Serializable {
    private int subject_id;
    private int material_id;
    private String other;
    private String status;

    public TransferCategory(  int subject_id, int material_id, String other) {
        this.subject_id = subject_id;
        this.material_id = material_id;
        this.other = other;
    }

    public String getStatus() {
        return status;
    }

    public TransferCategory setStatus(String status) {
        this.status = status;
        return this;
    }

    public int getSubject() {
        return subject_id;
    }

    public void setSubject(int subject_id) {
        this.subject_id = subject_id;
    }

    public int getMaterial() {
        return material_id;
    }

    public void setMaterial(int material_id) {
        this.material_id = material_id;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}