package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataReport;

import java.util.List;

/**
 * Created by Erick Sumargo on 3/1/2017.
 */

public class ModelReport extends Response {
    private int currentMonth, currentYear;
    private List<DataReport> reportList;

    private void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    private void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    private void setReportList(List<DataReport> reportList) {
        this.reportList = reportList;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public List<DataReport> getReportList() {
        return reportList;
    }
}