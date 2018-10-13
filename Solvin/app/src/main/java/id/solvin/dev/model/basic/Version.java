package id.solvin.dev.model.basic;

import java.io.Serializable;

/**
 * Created by edinofri on 10/02/2017.
 */

public class Version extends Base implements Serializable {
    private String materials;
    private String packages;
    private String banks;
    private String mobileNetworks;

    public String getMaterials() {
        return getSafeString(materials);
    }

    public String getPackages() {
        return getSafeString(packages);
    }

    public String getBanks() {
        return getSafeString(banks);
    }

    public String getMobileNetworks() {
        return getSafeString(mobileNetworks);
    }
}
