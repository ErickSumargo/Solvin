package id.solvin.dev.model.response;

import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.Response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by edinofri on 14/01/2017.
 */

public class ResponseSearch extends Response implements Serializable {
    private List<Auth> data;

    public List<Auth> getData() {
        return data;
    }

    public void setData(List<Auth> data) {
        this.data = data;
    }
}
