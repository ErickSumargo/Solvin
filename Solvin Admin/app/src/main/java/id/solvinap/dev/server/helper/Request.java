package id.solvinap.dev.server.helper;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Erick Sumargo on 2/7/2017.
 */

public class Request {
    private RequestBody requestBody;

    public static Request getInstance() {
        return new Request();
    }

    public RequestBody getText(String text) {
        requestBody = RequestBody.create(MediaType.parse("text/plain"), text);
        return requestBody;
    }

    public RequestBody getImage(File image) {
        requestBody = RequestBody.create(MediaType.parse("image/jpeg"), image);
        return requestBody;
    }
}