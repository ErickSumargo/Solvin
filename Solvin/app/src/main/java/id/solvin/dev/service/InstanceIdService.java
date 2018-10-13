package id.solvin.dev.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by edinofri on 16/12/2016.
 */

public class InstanceIdService  extends FirebaseInstanceIdService {
    private static final String REG_TOKEN = "FIREBASE_TOKEN";

    @Override
    public void onTokenRefresh() {
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Log.d(REG_TOKEN, recent_token);
    }
}
