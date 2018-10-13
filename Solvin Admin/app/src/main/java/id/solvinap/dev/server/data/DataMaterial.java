package id.solvinap.dev.server.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Erick Sumargo on 2/13/2017.
 */

public class DataMaterial implements Serializable {
    private int id;
    private String name;

    @SerializedName("subject")
    private DataSubject dataSubject;

    public DataMaterial(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private void setId(int id) {
        this.id = id;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setDataSubject(DataSubject dataSubject) {
        this.dataSubject = dataSubject;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public DataSubject getDataSubject() {
        return dataSubject;
    }

    @Override
    public String toString() {
        return name;
    }
}