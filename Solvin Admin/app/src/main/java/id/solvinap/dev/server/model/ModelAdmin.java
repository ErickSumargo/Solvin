package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataAuth;

/**
 * Created by Erick Sumargo on 3/3/2017.
 */

public class ModelAdmin extends Response {
    private ResultData resultData;

    public class ResultData{
        private DataAuth dataAdmin;
        private String token;
        private boolean emailValid, passwordValid;

        public DataAuth getDataAdmin() {
            return dataAdmin;
        }

        public void setDataAdmin(DataAuth dataAdmin) {
            this.dataAdmin = dataAdmin;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public boolean isEmailValid() {
            return emailValid;
        }

        public void setEmailValid(boolean emailValid) {
            this.emailValid = emailValid;
        }

        public boolean isPasswordValid() {
            return passwordValid;
        }

        public void setPasswordValid(boolean passwordValid) {
            this.passwordValid = passwordValid;
        }
    }

    public ResultData getResultData() {
        return resultData;
    }

    public void setResultData(ResultData resultData) {
        this.resultData = resultData;
    }
}
