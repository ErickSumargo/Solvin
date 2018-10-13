package id.solvin.dev.model.basic;

import java.util.ArrayList;

/**
 * Created by Erick Sumargo on 9/6/2016.
 */
public class FAQGroup {
    private String name;
    private ArrayList<FAQChild> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<FAQChild> getItems() {
        return items;
    }

    public void setItems(ArrayList<FAQChild> items) {
        this.items = items;
    }
}
