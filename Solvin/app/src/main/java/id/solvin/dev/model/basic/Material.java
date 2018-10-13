package id.solvin.dev.model.basic;

import java.io.Serializable;

/**
 * Created by edinofri on 01/12/2016.
 */

public class Material implements Serializable {
    private int id;
    private String name;
    private Subject subject;

    public Material(int id, String name) {
        this.id = id;
        this.name = name;
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

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return name;
    }
}
