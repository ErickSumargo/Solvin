package id.solvin.dev.model.basic;

import java.io.Serializable;
import java.util.List;

/**
 * Created by edinofri on 07/01/2017.
 */

public class Payment implements Serializable {
    private List<Bank> bank;
    private List<Paket> paket;

    public List<Bank> getBank() {
        return bank;
    }

    public void setBank(List<Bank> bank) {
        this.bank = bank;
    }

    public List<Paket> getPaket() {
        return paket;
    }

    public void setPaket(List<Paket> paket) {
        this.paket = paket;
    }
}
