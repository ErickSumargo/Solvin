package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataMentor;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Erick Sumargo on 2/17/2017.
 */

public class ModelMentor extends Response {
    @SerializedName("mentor")
    private DataMentor profile;

    public DataMentor getProfile() {
        return profile;
    }

    private void setProfile(DataMentor profile) {
        this.profile = profile;
    }
}
