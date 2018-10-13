package id.solvin.dev.realm.table;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Erick Sumargo on 4/13/2017.
 */

public class TableImage extends RealmObject {
    @PrimaryKey
    private int draft_id;
    private String image;

    public void setDraft_id(int draft_id) {
        this.draft_id = draft_id;
    }

    public int getDraft_id() {
        return draft_id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}