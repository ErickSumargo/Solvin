package id.solvin.dev.model.interfaces;

import id.solvin.dev.model.basic.Response;

/**
 * Created by edinofri on 02/11/2016.
 */

public interface IBaseResponse {
    void onSuccess(Response response);
    void onFailure(String message);
}
