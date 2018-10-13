package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataCumulativeRecord;

/**
 * Created by Erick Sumargo on 2/22/2017.
 */

public class ModelCumulativeRecord extends Response {
    private DataCumulativeRecord cumulativeRecord;

    private void setDataCumulativeRecord(DataCumulativeRecord cumulativeRecord) {
        this.cumulativeRecord = cumulativeRecord;
    }

    public DataCumulativeRecord getDataCumulativeRecord() {
        return cumulativeRecord;
    }
}