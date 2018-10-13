package id.solvin.dev.model.basic;

import java.io.Serializable;

/**
 * Created by edinofri on 05/03/2017.
 */

public class Error implements Serializable {
    private String message;
    private int type;
    public static final int ERROR_EMAIL = 0;
    public static final int ERROR_PHONE = 1;
    public static final int ERROR_MEMBER_CODE = 2;
    public static final int ERROR_OLD_PASSWORD = 3;
    public static final int ERROR_VALIDATE_CODE = 4;
    public static final int ERROR_ACTIVE_PASSWORD = 5;

    public static final int ERROR_CREATE_QUESTION = 6;
    public static final int ERROR_EDIT_QUESTION = 7;
    public static final int ERROR_CREATE_SOLUTION = 8;
    public static final int ERROR_EDIT_SOLUTION = 9;
    public static final int ERROR_CREATE_COMMENT = 10;
    public static final int ERROR_EDIT_COMMENT = 11;

    public static final int ERROR_MOBILE = 12;
    public static final int ERROR_CODE = 13;

    public Error(String message, int type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }
}