package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataPrimary;

/**
 * Created by Erick Sumargo on 2/13/2017.
 */

public class ModelPrimary extends Response {
    private DataPrimary data;

    public DataPrimary getData() {
        return data;
    }

    private void setData(DataPrimary data) {
        this.data = data;
    }
}