package id.solvinap.dev.server.interfaces;

import id.solvinap.dev.server.api.Response;

/**
 * Created by Erick Sumargo on 2/6/2017.
 */

public interface IBaseResponse {
    void onSuccess(Response response);

    void onFailure(String message);
}
