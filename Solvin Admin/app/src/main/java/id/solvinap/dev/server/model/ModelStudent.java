package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataStudent;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Erick Sumargo on 2/17/2017.
 */

public class ModelStudent extends Response {
    @SerializedName("student")
    private DataStudent profile;

    public DataStudent getProfile() {
        return profile;
    }

    private void setProfile(DataStudent profile) {
        this.profile = profile;
    }
}
