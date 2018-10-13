package id.solvin.dev.model.basic;

import java.io.Serializable;

/**
 * Created by edinofri on 10/02/2017.
 */

public abstract class Model extends Base implements Serializable {
    protected int id;

    public int getId() {
        return id;
    }
}
