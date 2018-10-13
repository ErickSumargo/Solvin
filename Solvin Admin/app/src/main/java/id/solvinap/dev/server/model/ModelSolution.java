package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataSolution;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/16/2017.
 */

public class ModelSolution extends Response {
    private List<DataSolution> solutionList;

    private void setSolutionList(List<DataSolution> solutionList) {
        this.solutionList = solutionList;
    }

    public List<DataSolution> getSolutionList() {
        return solutionList;
    }
}