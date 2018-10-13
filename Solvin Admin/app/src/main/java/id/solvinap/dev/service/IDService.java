package id.solvinap.dev.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Erick Sumargo on 2/6/2017.
 */

public class IDService extends FirebaseInstanceIdService{
    private String token;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        token = FirebaseInstanceId.getInstance().getToken();

        Log.d("FIREBASE TOKEN", token);
    }
}
