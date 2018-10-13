package id.solvinap.dev.server.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erick Sumargo on 2/13/2017.
 */

public class DataSubject implements Serializable {
    private int id;
    private String name;

    @SerializedName("material")
    private List<DataMaterial> materialList;

    public DataSubject(int id, String name) {
        this.id = id;
        this.name = name;

        materialList = new ArrayList<>();
    }

    private void setId(int id) {
        this.id = id;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setMaterialList(List<DataMaterial> materialList) {
        this.materialList = materialList;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<DataMaterial> getMaterialList() {
        return materialList;
    }

    @Override
    public String toString() {
        return name;
    }
}