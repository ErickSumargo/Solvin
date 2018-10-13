package id.solvin.dev.presenter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.ConnectionAPI;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.SCrypt;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Error;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.interfaces.OnErrors;
import id.solvin.dev.model.response.ResponseAuth;
import id.solvin.dev.model.response.ResponseSearch;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by edinofri on 23/11/2016.
 */

public class AuthPresenter {
    private IBaseResponse iBaseResponse;
    private File temp;
    private SCrypt sCrypt;

    public AuthPresenter(IBaseResponse iBaseResponse) {
        this.iBaseResponse = iBaseResponse;
        sCrypt = SCrypt.getInstance();
    }

    public void doLogin(final String email, final String password, final String deviceID, final Context context, final OnErrors onErrors) {
        final List<Error> errorList = new ArrayList();
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(Global.get().key().REQUEST_EMAIL, email);
        metadata.put(Global.get().key().REQUEST_PASSWORD, password);
        metadata.put("device_id", deviceID);
        metadata.put(Global.get().key().REQUEST_FIREBASE, FirebaseInstanceId.getInstance().getToken());
        ConnectionAPI.getInstance(context).getApi().login(metadata).enqueue(new Callback<ResponseAuth>() {
            @Override
            public void onResponse(Call<ResponseAuth> call, retrofit2.Response<ResponseAuth> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        if (response.body().getError() != null) {
                            errorList.addAll(response.body().getError());
                            onErrors.onError(errorList);
                        } else {
                            iBaseResponse.onFailure(response.message());
                        }
                    }
                } else {
                    iBaseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseAuth> call, Throwable t) {
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void doRegisterStep1(final String phone, final String tag, final Context context, final OnErrors onErrors) {
        final List<Error> errorList = new ArrayList();
        Map<String, String> metadata = new HashMap<>();
        metadata.put(Global.get().key().REQUEST_PHONE, phone);
        ConnectionAPI.getInstance(context).getApi().registerStepOne(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        if(tag.equals("resend_code")){
                            response.body().setTag(tag);
                        }
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        if (response.body().getError() != null) {
                            errorList.addAll(response.body().getError());
                            onErrors.onError(errorList);
                        } else {
                            iBaseResponse.onFailure("Error " + response.code() + ", " + response.message());
                        }
                    }
                } else {
                    iBaseResponse.onFailure("Error " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void doRegisterStep2(final String phone, final String key, final Context context, final OnErrors onErrors) {
        final List<Error> errorList = new ArrayList();
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(Global.get().key().REQUEST_PHONE, phone);
        metadata.put(Global.get().key().REQUEST_CODE_VERIFY, key);

        ConnectionAPI.getInstance(context).getApi().registerStepTwo(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        if (response.body().getError() != null) {
                            errorList.addAll(response.body().getError());
                            onErrors.onError(errorList);
                        } else {
                            iBaseResponse.onFailure("Error " + response.code() + ", " + response.message());
                        }
                    }
                } else {
                    iBaseResponse.onFailure("Error " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void doRegisterStep3(final String phone, final String name, final String email, final String password,
                                final String membercode, final String deviceID, final Context context, final OnErrors onErrors) {
        final List<Error> errorList = new ArrayList();
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(Global.get().key().REQUEST_PHONE, phone);
        metadata.put(Global.get().key().REQUEST_NAME, name);
        metadata.put(Global.get().key().REQUEST_EMAIL, email);
        metadata.put(Global.get().key().REQUEST_PASSWORD, password);
        metadata.put(Global.get().key().REQUEST_MEMBER_CODE, membercode);
        metadata.put("device_id", deviceID);
        metadata.put(Global.get().key().REQUEST_FIREBASE, FirebaseInstanceId.getInstance().getToken());
        ConnectionAPI.getInstance(context).getApi().registerStepThird(metadata).enqueue(new Callback<ResponseAuth>() {
            @Override
            public void onResponse(Call<ResponseAuth> call, retrofit2.Response<ResponseAuth> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        if (response.body().getError() != null) {
                            errorList.addAll(response.body().getError());
                            onErrors.onError(errorList);
                        } else {
                            iBaseResponse.onFailure(response.message());
                        }
                    }
                } else {
                    iBaseResponse.onFailure("Error " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseAuth> call, Throwable t) {
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void doGetProfile(final String type, final String id, final Context context) {
        ConnectionAPI.getInstance(context).getApi().getProfile(type, id).enqueue(new Callback<ResponseAuth>() {
            @Override
            public void onResponse(Call<ResponseAuth> call, retrofit2.Response<ResponseAuth> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        response.body().setTag(Response.TAG_PROFILE_LOAD);
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.body().getMessage());
                    }
                } else {
                    iBaseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseAuth> call, Throwable t) {
                iBaseResponse.onFailure(t.getMessage());
                if (t instanceof SocketTimeoutException) {
                    doGetProfile(type, id, context);
                }
            }
        });
    }

    public void updateProfile(final Context context, final ClassApplicationTool applicationTool, final boolean isDeleted,
                              final Uri image, final ByteArrayOutputStream byteArrayOutputStream, final OnErrors onErrors, final String... args) {
        final List<Error> errorList = new ArrayList();
        Map<String, RequestBody> metadata = new HashMap<>();
        metadata.put("email", Global.get().convertToRequestBody(args[0]));
        metadata.put("name", Global.get().convertToRequestBody(args[1]));
        metadata.put("phone", Global.get().convertToRequestBody(args[2]));
        metadata.put("birth", Global.get().convertToRequestBody(args[3]));
        metadata.put("address", Global.get().convertToRequestBody(args[4]));
        metadata.put("other", Global.get().convertToRequestBody(args[5]));

        if (image != null) {
            if (!isDeleted) {
                temp = applicationTool.getTempFileImage(byteArrayOutputStream);
                RequestBody requestFileImage = RequestBody.create(MediaType.parse("image/jpeg"), temp);
                metadata.put("image\"; filename=\"image.jpg\" ", requestFileImage);
            }
        }
        if (isDeleted) {
            metadata.put("change_image", Global.get().convertToRequestBody("true"));
        } else if (!isDeleted && image != null) {
            metadata.put("change_image", Global.get().convertToRequestBody("true"));
        }

        ConnectionAPI.getInstance(context).getApi().updateProfile(metadata).enqueue(new Callback<ResponseAuth>() {
            @Override
            public void onResponse(Call<ResponseAuth> call, retrofit2.Response<ResponseAuth> response) {
                if (image != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        response.body().setTag(Response.TAG_PROFILE_UPDATE);
                        Session.with(context).createSessionToken(response.body().getData().getToken());
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        if (response.body().getError() != null) {
                            errorList.addAll(response.body().getError());
                            onErrors.onError(errorList);
                        } else {
                            iBaseResponse.onFailure(response.message());
                        }
                    }
                } else {
                    iBaseResponse.onFailure("Error " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseAuth> call, Throwable t) {
                if (image != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void changePassword(final Context context, final String oldPassword, final String newPassword,
                               final String renewPassword, final OnErrors onErrors) {
        final List<Error> errorList = new ArrayList();
        if (!newPassword.equals(renewPassword)) {
            onErrors.onError(errorList);
            return;
        }
        String encryptedOldPassword = oldPassword, encryptedNewPassword = newPassword;
        try {
            encryptedOldPassword = sCrypt.bytesToHex(sCrypt.encrypt(oldPassword));
            encryptedNewPassword = sCrypt.bytesToHex(sCrypt.encrypt(newPassword));
        } catch (Exception e) {
        }
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("old_password", encryptedOldPassword);
        metadata.put("new_password", encryptedNewPassword);
        ConnectionAPI.getInstance(context).getApi().updatePassword(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        response.body().setTag(Response.TAG_UPDATE_PASSWORD);
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        if (response.body().getError() != null) {
                            errorList.addAll(response.body().getError());
                            onErrors.onError(errorList);
                        } else {
                            iBaseResponse.onFailure(response.message());
                        }
                    }
                } else {
                    iBaseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void doSearch(String query, Context context) {
        if (TextUtils.isEmpty(query)) {
            return;
        }
        ConnectionAPI.getInstance(context).getApi().search(query).enqueue(new Callback<ResponseSearch>() {
            @Override
            public void onResponse(Call<ResponseSearch> call, retrofit2.Response<ResponseSearch> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        response.body().setTag(Response.TAG_SEARCH_DATA);
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.body().getMessage());
                    }
                } else {
                    iBaseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseSearch> call, Throwable t) {
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void resetPasswordStep1(final String phone, final Context context) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put(Global.get().key().REQUEST_PHONE, phone);
        ConnectionAPI.getInstance(context).getApi().resetStepOne(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.code() == 200) {
                    response.body().setTag(Response.TAG_RESET_PASSWORD_STEP_1);
                    iBaseResponse.onSuccess(response.body());
                } else {
                    iBaseResponse.onFailure("Error " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void resetPasswordStep2(final String phone, final String code, final Context context) {
        Map<String, String> metadata = new HashMap<>();

        metadata.put(Global.get().key().REQUEST_PHONE, phone);
        metadata.put(Global.get().key().REQUEST_CODE_VERIFY, code);
        ConnectionAPI.getInstance(context).getApi().resetStepTwo(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.code() == 200) {
                    response.body().setTag(Response.TAG_RESET_PASSWORD_STEP_2);
                    iBaseResponse.onSuccess(response.body());
                } else {
                    iBaseResponse.onFailure("Error " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void resetPasswordStep3(final String phone, final String password, final String token, final Context context) {
        Map<String, String> metadata = new HashMap<>();

        metadata.put(Global.get().key().REQUEST_PHONE, phone);
        metadata.put("token", token);
        metadata.put("password", password);

        ConnectionAPI.getInstance(context).getApi().resetStepThree(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        response.body().setTag(Response.TAG_RESET_PASSWORD_STEP_3);
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.body().getMessage());
                    }
                } else {
                    iBaseResponse.onFailure("Error " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void sendFeedback(final Context context, final String... args) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("title", args[0]);
        metadata.put("content", args[1]);
        ConnectionAPI.getInstance(context).getApi().sendFeedback(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.code() == 200) {
                    iBaseResponse.onSuccess(response.body());
                } else {
                    iBaseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }
}