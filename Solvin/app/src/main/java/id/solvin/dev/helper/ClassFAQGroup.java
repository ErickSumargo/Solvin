package id.solvin.dev.helper;

import android.text.Spanned;

import java.util.ArrayList;

/**
 * Created by Erick Sumargo on 9/6/2016.
 */
public class ClassFAQGroup {
    private Spanned name;
    private ArrayList<ClassFAQChild> items;

    public Spanned getName() {
        return name;
    }

    public void setName(Spanned name) {
        this.name = name;
    }

    public ArrayList<ClassFAQChild> getItems() {
        return items;
    }

    public void setItems(ArrayList<ClassFAQChild> items) {
        this.items = items;
    }
}
