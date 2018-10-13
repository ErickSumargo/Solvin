package id.solvin.dev.model.basic;

import java.io.Serializable;

/**
 * Created by edinofri on 07/01/2017.
 */

public class Paket implements Serializable {
    private int id;
    private int active;
    private double nominal;
    private int credit;

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public double getNominal() {
        return nominal;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }
}
