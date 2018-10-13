package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataAuth;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/6/2017.
 */

public class ModelAuth extends Response {
    private List<DataAuth> authList;

    private void setAuthList(List<DataAuth> authList) {
        this.authList = authList;
    }

    public List<DataAuth> getAuthList() {
        return authList;
    }
}