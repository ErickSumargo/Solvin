package id.solvin.dev.model.response;

import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.Base;
import id.solvin.dev.model.basic.Response;

import java.io.Serializable;

/**
 * Created by edinofri on 02/11/2016.
 */

public class ResponseAuth extends Response {
    private ResultData data;

    public ResultData getData() {
        return data;
    }

    public class ResultData extends Base implements Serializable{
        private Auth auth;
        private String token;
        private boolean emailValid, passwordValid;

        public Auth getAuth() {
            return auth;
        }

        public String getToken() {
            return token;
        }

        public boolean isEmailValid() {
            return emailValid;
        }

        public boolean isPasswordValid() {
            return passwordValid;
        }
    }
}
