package id.solvin.dev.model.basic;

import java.io.Serializable;

/**
 * Created by Erick Sumargo on 5/9/2017.
 */

public class MobileNetwork implements Serializable {
    private int id;
    private String name;
    private String number;

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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}