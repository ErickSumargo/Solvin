package id.solvin.dev.model.basic;

import java.io.Serializable;

/**
 * Created by edinofri on 17/12/2016.
 */

public class TransferTraditional implements Serializable {
    private String tag;
    private Object obj;

    public TransferTraditional(String tag, Object obj) {
        this.tag = tag;
        this.obj = obj;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
