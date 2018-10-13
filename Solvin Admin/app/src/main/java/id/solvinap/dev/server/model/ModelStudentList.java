package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataStudent;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/17/2017.
 */

public class ModelStudentList extends Response {
    private List<DataStudent> studentList;

    private void setStudentList(List<DataStudent> studentList) {
        this.studentList = studentList;
    }

    public List<DataStudent> getStudentList() {
        return studentList;
    }
}
