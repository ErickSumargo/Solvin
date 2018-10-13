package id.solvinap.dev.server.data;

/**
 * Created by Erick Sumargo on 2/16/2017.
 */

public class DataPackage {
    private int id, credit, active;
    private long nominal;

    public void setId(int id) {
        this.id = id;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public void setNominal(long nominal) {
        this.nominal = nominal;
    }

    public int getId() {
        return id;
    }

    public int getCredit() {
        return credit;
    }

    public int getActive() {
        return active;
    }

    public long getNominal() {
        return nominal;
    }
}