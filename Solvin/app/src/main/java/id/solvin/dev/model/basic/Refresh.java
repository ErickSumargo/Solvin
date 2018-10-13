package id.solvin.dev.model.basic;

import java.io.Serializable;

/**
 * Created by edinofri on 08/12/2016.
 */

public class Refresh implements Serializable {
    public boolean refresh;

    public Refresh(boolean refresh) {
        this.refresh = refresh;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }
}
