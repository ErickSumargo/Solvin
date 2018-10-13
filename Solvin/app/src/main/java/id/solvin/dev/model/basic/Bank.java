package id.solvin.dev.model.basic;

import java.io.Serializable;

/**
 * Created by edinofri on 05/01/2017.
 */

public class Bank implements Serializable{
    private int id;
    private String name;
    private String account_owner;
    private String account_number;
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getAccountOwner() {
        return account_owner;
    }

    public String getAccountNumber() {
        return account_number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
