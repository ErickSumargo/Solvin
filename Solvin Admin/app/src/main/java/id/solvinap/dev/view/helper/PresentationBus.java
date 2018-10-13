package id.solvinap.dev.view.helper;

import id.solvinap.dev.server.data.DataPlainReport;

import java.util.List;

/**
 * Created by Erick Sumargo on 3/2/2017.
 */

public class PresentationBus {
    private List<DataPlainReport> reportList;
    private List<String> xLabelList;

    public PresentationBus(List<DataPlainReport> reportList, List<String> xLabelList) {
        this.reportList = reportList;
        this.xLabelList = xLabelList;
    }

    public List<DataPlainReport> getReportList() {
        return reportList;
    }

    public List<String> getXLabelList() {
        return xLabelList;
    }
}