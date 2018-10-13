package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataMentor;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/17/2017.
 */

public class ModelMentorList extends Response {
    private List<DataMentor> mentorList;

    private void setMentorList(List<DataMentor> mentorList) {
        this.mentorList = mentorList;
    }

    public List<DataMentor> getMentorList() {
        return mentorList;
    }
}
