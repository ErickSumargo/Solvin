package id.solvin.dev.realm.table;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Erick Sumargo on 4/6/2017.
 */

public class TableDraftSolution extends RealmObject {
    @PrimaryKey
    private int id;
    private RealmList<TableImage> images;
    private String title, content, updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public RealmList<TableImage> getImages() {
        return images;
    }

    public void setImages(RealmList<TableImage> images) {
        this.images = images;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}