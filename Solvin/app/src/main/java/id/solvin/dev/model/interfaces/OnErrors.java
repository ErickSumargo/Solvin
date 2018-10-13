package id.solvin.dev.model.interfaces;

import id.solvin.dev.model.basic.Error;

import java.util.List;

/**
 * Created by edinofri on 05/03/2017.
 */

public interface OnErrors {
     void onError(List<Error> errorList);
}
