package id.solvinap.dev.server.api;

/**
 * Created by Erick Sumargo on 2/6/2017.
 */

public class Response {
    private int status;
    private String message, tag;

    private void setStatus(int status) {
        this.status = status;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getTag() {
        return tag;
    }
}