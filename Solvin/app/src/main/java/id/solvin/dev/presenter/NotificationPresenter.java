package id.solvin.dev.presenter;

import android.content.Context;

import java.net.SocketTimeoutException;

import id.solvin.dev.helper.ConnectionAPI;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.response.ResponseNotification;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by edinofri on 26/02/2017.
 */

public class NotificationPresenter {
    private IBaseResponse baseResponse;

    public NotificationPresenter(IBaseResponse baseResponse) {
        this.baseResponse = baseResponse;
    }

    public void getNotification(final int lastId, final Context context) {
        ConnectionAPI.getInstance(context).getApi().getNotification(lastId).enqueue(new Callback<ResponseNotification>() {
            @Override
            public void onResponse(Call<ResponseNotification> call, Response<ResponseNotification> response) {
                if (response.code() == 200) {
                    baseResponse.onSuccess(response.body());
                } else {
                    baseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseNotification> call, Throwable t) {
                baseResponse.onFailure(t.getMessage());
                if (t instanceof SocketTimeoutException) {
                    getNotification(lastId, context);
                }
            }
        });
    }

    public void readNotification(final int notification_id, final Context context) {
        ConnectionAPI.getInstance(context).getApi().readNotification(notification_id).enqueue(new Callback<id.solvin.dev.model.basic.Response>() {
            @Override
            public void onResponse(Call<id.solvin.dev.model.basic.Response> call, Response<id.solvin.dev.model.basic.Response> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        response.body().setTag(id.solvin.dev.model.basic.Response.TAG_READ_NOTIFICATION);
                        baseResponse.onSuccess(response.body());
                    } else {
                        baseResponse.onFailure(response.body().getMessage());
                    }
                } else {
                    baseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<id.solvin.dev.model.basic.Response> call, Throwable t) {
                baseResponse.onFailure(t.getMessage());
                if (t instanceof SocketTimeoutException) {
                    readNotification(notification_id, context);
                }
            }
        });
    }

    public void getCount(final Context context) {
        ConnectionAPI.getInstance(context).getApi().getNotificationCount().enqueue(new Callback<ResponseNotification>() {
            @Override
            public void onResponse(Call<ResponseNotification> call, Response<ResponseNotification> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        response.body().setTag(id.solvin.dev.model.basic.Response.TAG_COUNT_NOTIFICATION);
                        baseResponse.onSuccess(response.body());
                    } else {
                        baseResponse.onFailure(response.body().getMessage());
                    }
                } else {
                    baseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseNotification> call, Throwable t) {
                baseResponse.onFailure(t.getMessage());
                if (t instanceof SocketTimeoutException) {
                    getCount(context);
                }
            }
        });
    }
}