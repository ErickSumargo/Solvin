package id.solvin.dev.model.basic;

import java.io.Serializable;
import java.lang.*;
import java.util.List;

/**
 * Created by edinofri on 03/10/2016.
 */

public class Response implements Serializable {
    public static final String TAG_SEARCH_DATA = "search.data";
    public static final String TAG_PURCHASE_PACKAGE_CHECK = "purchase.package.check";
    public static final String TAG_PURCHASE_PACKAGE_ACTION = "purchase.package.action";
    public static final String TAG_PURCHASE_PACKAGE_BUY = "purchase.package.buy";
    public static final String TAG_PURCHASE_PACKAGE_DETAIL = "purchase.package.detail";
    public static final String TAG_PURCHASE_HISTORY = "purchase.package.history";
    public static final String TAG_CONFIRM_PACKAGE_SUCCESS = "confirm.package.success";
    public static final String TAG_PROFILE_UPDATE = "auth.profile.update";
    public static final String TAG_PROFILE_LOAD = "auth.profile.load";
    public static final String TAG_UPDATE_PASSWORD = "auth.password.update";
    public static final String TAG_UPDATE_PHOTO = "auth.photo.update";
    public static final String TAG_DELETE_PHOTO = "auth.photo.delete";
    public static final String TAG_READ_NOTIFICATION = "auth.notification.read";
    public static final String TAG_COUNT_NOTIFICATION = "auth.notification.count";
    public static final String TAG_RESET_PASSWORD_STEP_1 = "auth.reset.password.1";
    public static final String TAG_RESET_PASSWORD_STEP_2 = "auth.reset.password.2";
    public static final String TAG_RESET_PASSWORD_STEP_3 = "auth.reset.password.3";

    private boolean success;
    private String message;
    private String tag;
    private List<Error> error;

    public List<Error> getError() {
        return error;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
