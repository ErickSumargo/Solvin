package id.solvinap.dev.view.helper;

/**
 * Created by Erick Sumargo on 3/2/2017.
 */

public class DataPresentationYear {
    private String year;

    public DataPresentationYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    @Override
    public String toString() {
        return year;
    }
}