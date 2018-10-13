package id.solvinap.dev.server.model;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.view.helper.SCrypt;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Erick Sumargo on 3/30/2017.
 */

public class ModelRegisterMentor extends Response {
    @SerializedName("security_code")
    private String securityCode;

    public String getSecurityCode() {
        try {
            return new String(SCrypt.getInstance().decrypt(securityCode));
        } catch (Exception e) {
        }
        return "";
    }
}