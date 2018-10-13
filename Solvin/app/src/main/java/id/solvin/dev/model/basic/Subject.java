package id.solvin.dev.model.basic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by edinofri on 01/12/2016.
 */

public class Subject implements Serializable {
    private int id;
    private String name;
    private List<Material> material;

    public Subject(int id, String name) {
        this.id = id;
        this.name = name;
        this.material = new ArrayList<>();
    }

    public List<Material> getMaterial() {
        return material;
    }

    public void setMaterial(List<Material> material) {
        this.material = material;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}