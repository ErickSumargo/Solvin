package id.solvin.dev.model.response;

import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.User;

/**
 * Created by edinofri on 03/10/2016.
 */

public class ResponseUser extends Response {
    private User user;
    private String token;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "ResponseUser{" +
                "user=" + user +
                '}';
    }
}