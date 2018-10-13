package id.solvinap.dev.server.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Erick Sumargo on 2/10/2017.
 */

public class DataFeedback {
    private int id, read;
    private String title, content;

    @SerializedName("auth_id")
    private int authId;

    @SerializedName("auth_type")
    private String authType;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("auth")
    private DataAuth dataAuth;

    private void setId(int id) {
        this.id = id;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private void setContent(String content) {
        this.content = content;
    }

    private void setAuthId(int authId) {
        this.authId = authId;
    }

    private void setAuthType(String authType) {
        this.authType = authType;
    }

    private void setRead(int read) {
        this.read = read;
    }

    private void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    private void setDataAuth(DataAuth dataAuth) {
        this.dataAuth = dataAuth;
    }

    public int getId() {
        return id;
    }

    public int getAuthId() {
        return authId;
    }

    public String getAuthType() {
        return authType;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getRead() {
        return read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public DataAuth getDataAuth() {
        return dataAuth;
    }
}