package id.solvin.dev.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.onegravity.rteditor.api.format.RTFormat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.ConfigApp;
import id.solvin.dev.helper.ConnectionAPI;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.SCrypt;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.Comment;
import id.solvin.dev.model.basic.Error;
import id.solvin.dev.model.basic.Question;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.Solution;
import id.solvin.dev.model.basic.TransferCategory;
import id.solvin.dev.model.basic.Version;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.interfaces.OnErrors;
import id.solvin.dev.model.response.ResponseFirst;
import id.solvin.dev.model.response.ResponseQuestion;
import id.solvin.dev.model.response.ResponseQuestions;
import id.solvin.dev.view.widget.ClassRichEditText;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by edinofri on 24/11/2016.
 */

public class QuestionPresenter {
    private IBaseResponse iBaseResponse;
    private File temp;
    private SCrypt sCrypt;

    public QuestionPresenter(IBaseResponse iBaseResponse) {
        sCrypt = SCrypt.getInstance();
        this.iBaseResponse = iBaseResponse;
    }

    public void doGetData(final Context context) {
        Map<String, Object> metadata = new HashMap<>();
        Version version = Session.with(context).getVersion();
        metadata.put("version_material", version.getMaterials());
        metadata.put("version_package", version.getPackages());
        metadata.put("version_bank", version.getBanks());
        metadata.put("version_mobile_network", version.getMobileNetworks());
        ConnectionAPI.getInstance(context).getApi().loadFirstData(metadata).enqueue(new Callback<ResponseFirst>() {
            @Override
            public void onResponse(Call<ResponseFirst> call, retrofit2.Response<ResponseFirst> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        Session.with(context).createSessionVersion(response.body().getData().getVersion());
                        Session.with(context).createSessionMaterial(response.body().getData().getMaterials());
                        Session.with(context).createSessionBanks(response.body().getData().getBanks());
                        Session.with(context).createSessionMobileNetworks(response.body().getData().getMobileNetworks());
                        Session.with(context).createSessionPackages(response.body().getData().getPackages());

                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                } else {
                    iBaseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseFirst> call, Throwable t) {
                doGetData(context);
            }
        });
    }

    public void doLoadFeedData(final String auth_type, final String id, final int lastId, final String status, final String materi_id,
                               final Context context) {
        ConnectionAPI.getInstance(context).getApi().getQuestions(String.valueOf(lastId), status, materi_id,
                id,
                auth_type
        ).enqueue(new Callback<ResponseQuestions>() {
            @Override
            public void onResponse(Call<ResponseQuestions> call, retrofit2.Response<ResponseQuestions> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.body().getMessage());
                    }
                } else {
                    iBaseResponse.onFailure(response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseQuestions> call, Throwable t) {
                iBaseResponse.onFailure(t.getMessage());
                if (t instanceof SocketTimeoutException) {
                    doLoadFeedData(auth_type, id, lastId, status, materi_id, context);
                }
            }
        });
    }

    public void doRefreshQuestion(final int id, final ProgressBar progressBar, final Context context) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        ConnectionAPI.getInstance(context).getApi().getQuestion(id
        ).enqueue(new Callback<ResponseQuestion>() {
            @Override
            public void onResponse(Call<ResponseQuestion> call, retrofit2.Response<ResponseQuestion> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.body().getMessage());
                    }
                } else {
                    iBaseResponse.onFailure(response.code() + " " + response.message() + " " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseQuestion> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
                iBaseResponse.onFailure(t.getMessage());
                if (t instanceof SocketTimeoutException) {
                    doRefreshQuestion(id, progressBar, context);
                }
            }
        });
    }

    public void doCreateQuestion(final TransferCategory category, final Uri uri, final Bitmap bitmap, final ClassRichEditText sheet, final ClassApplicationTool applicationTool,
                                 final ByteArrayOutputStream byteArrayOutputStream, final Context context, final OnErrors onErrors) {
        Map<String, RequestBody> metadata = new HashMap<>();
        metadata.put("student_id", Global.get().convertToRequestBody(String.valueOf(Session.with(context).getAuth().getId())));
        metadata.put("material_id", Global.get().convertToRequestBody(String.valueOf(category.getMaterial())));
        metadata.put("other", Global.get().convertToRequestBody(category.getOther()));
        metadata.put("content", Global.get().convertToRequestBody(
                TextUtils.isEmpty(sheet.toString()) ? "" : sheet.getText(RTFormat.HTML)));
        if (uri != null && bitmap != null) {
            temp = applicationTool.getTempFileImage(byteArrayOutputStream);
            RequestBody requestFileImage = RequestBody.create(MediaType.parse("image/jpeg"), temp);
            metadata.put("image\"; filename=\"image.jpg\" ", requestFileImage);
        }

        final List<Error> errorList = new ArrayList();
        ConnectionAPI.getInstance(context).getApi().createQuestion(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (uri != null && bitmap != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
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
                    iBaseResponse.onFailure(response.code() + " " + response.message() + " " + new Gson().toJson(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                if (uri != null && bitmap != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void doEditQuestion(final Question question, final TransferCategory category, final Uri uri, final ImageView image,
                               final Bitmap bitmap, final ClassRichEditText sheet, final ClassApplicationTool applicationTool,
                               final ByteArrayOutputStream byteArrayOutputStream, final Context context, final OnErrors onErrors) {
        Map<String, RequestBody> metadata = new HashMap<>();
        metadata.put("question_id", Global.get().convertToRequestBody(String.valueOf(question.getId())));
        metadata.put("student_id", Global.get().convertToRequestBody(String.valueOf(Session.with(context).getAuth().getId())));
        metadata.put("material_id", Global.get().convertToRequestBody(String.valueOf(category.getMaterial())));
        metadata.put("other", Global.get().convertToRequestBody(category.getOther()));
        metadata.put("content", Global.get().convertToRequestBody(
                TextUtils.isEmpty(sheet.toString()) ? "" : sheet.getText(RTFormat.HTML)));

        if (uri != null && bitmap != null) {
            temp = applicationTool.getTempFileImage(byteArrayOutputStream);
            RequestBody requestFileImage = RequestBody.create(MediaType.parse("image/jpeg"), temp);

            metadata.put("image\"; filename=\"image.jpg\" ", requestFileImage);
            metadata.put("change_image", Global.get().convertToRequestBody("true"));
        } else {
            if (!question.getImage().isEmpty() && image.getDrawable() == null) {
                metadata.put("change_image", Global.get().convertToRequestBody("true"));
            } else {
                metadata.put("change_image", Global.get().convertToRequestBody("false"));
            }
        }

        final List<Error> errorList = new ArrayList();
        ConnectionAPI.getInstance(context).getApi().editQuestion(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (uri != null && bitmap != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
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
                    iBaseResponse.onFailure(response.code() + " " + response.message() + " " + new Gson().toJson(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                if (uri != null && bitmap != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void doAddSolution(Question question, final Uri uri, final Bitmap bitmap, ClassRichEditText sheet, final ClassApplicationTool applicationTool,
                              ByteArrayOutputStream byteArrayOutputStream, Context context, final OnErrors onErrors) {
        Map<String, RequestBody> metadata = new HashMap<>();
        metadata.put("mentor_id", Global.get().convertToRequestBody(String.valueOf(Session.with(context).getAuth().getId())));
        metadata.put("question_id", Global.get().convertToRequestBody(String.valueOf(question.getId())));
        metadata.put("content", Global.get().convertToRequestBody(
                TextUtils.isEmpty(sheet.toString()) ? "" : sheet.getText(RTFormat.HTML)));
        if (uri != null && bitmap != null) {
            temp = applicationTool.getTempFileImage(byteArrayOutputStream);
            RequestBody requestFileImage = RequestBody.create(MediaType.parse("image/jpeg"), temp);
            metadata.put("image\"; filename=\"image.jpg\" ", requestFileImage);
        }

        final List<Error> errorList = new ArrayList();
        ConnectionAPI.getInstance(context).getApi().addSolution(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (uri != null && bitmap != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
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
                    iBaseResponse.onFailure(response.code() + " " + response.message() + " " + new Gson().toJson(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                if (uri != null && bitmap != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void doEditSolution(Solution solution, final Uri uri, ImageView image, final Bitmap bitmap, ClassRichEditText sheet, final ClassApplicationTool applicationTool,
                               ByteArrayOutputStream byteArrayOutputStream, Context context, final OnErrors onErrors) {
        Map<String, RequestBody> metadata = new HashMap<>();
        metadata.put("question_id", Global.get().convertToRequestBody(String.valueOf(solution.getQuestion_id())));
        metadata.put("solution_id", Global.get().convertToRequestBody(String.valueOf(solution.getId())));
        metadata.put("mentor_id", Global.get().convertToRequestBody(String.valueOf(Session.with(context).getAuth().getId())));
        metadata.put("content", Global.get().convertToRequestBody(
                TextUtils.isEmpty(sheet.toString()) ? "" : sheet.getText(RTFormat.HTML)));
        if (uri != null && bitmap != null) {
            temp = applicationTool.getTempFileImage(byteArrayOutputStream);
            RequestBody requestFileImage = RequestBody.create(MediaType.parse("image/jpeg"), temp);

            metadata.put("image\"; filename=\"image.jpg\" ", requestFileImage);
            metadata.put("change_image", Global.get().convertToRequestBody("true"));
        } else {
            if (!solution.getImage().isEmpty() && image.getDrawable() == null) {
                metadata.put("change_image", Global.get().convertToRequestBody("true"));
            } else {
                metadata.put("change_image", Global.get().convertToRequestBody("false"));
            }
        }

        final List<Error> errorList = new ArrayList();
        ConnectionAPI.getInstance(context).getApi().editSolution(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (uri != null && bitmap != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
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
                    iBaseResponse.onFailure(response.code() + " " + response.message() + " " + new Gson().toJson(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                if (uri != null && bitmap != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void doAddComment(Question question, List<Auth> checkedList, final Uri uri, final Bitmap bitmap, ClassRichEditText sheet,
                             final ClassApplicationTool applicationTool, ByteArrayOutputStream byteArrayOutputStream,
                             Context context) {
        Map<String, RequestBody> metadata = new HashMap<>();
        metadata.put("auth_id", Global.get().convertToRequestBody(String.valueOf(Session.with(context).getAuth().getId())));
        metadata.put("auth_type", Global.get().convertToRequestBody(String.valueOf(Session.with(context).getLoginType() == ConfigApp.get().STUDENT ? Auth.AUTH_TYPE_STUDENT : Auth.AUTH_TYPE_MENTOR)));
        metadata.put("question_id", Global.get().convertToRequestBody(String.valueOf(question.getId())));
        metadata.put("content", Global.get().convertToRequestBody(
                TextUtils.isEmpty(sheet.toString()) ? "" : sheet.getText(RTFormat.HTML)));
        metadata.put("auth_notif", Global.get().convertToRequestBody(convertToArrStr(checkedList)));
        if (uri != null && bitmap != null) {
            temp = applicationTool.getTempFileImage(byteArrayOutputStream);
            RequestBody requestFileImage = RequestBody.create(MediaType.parse("image/jpeg"), temp);
            metadata.put("image\"; filename=\"image.jpg\" ", requestFileImage);
        }

        ConnectionAPI.getInstance(context).getApi().addComment(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (uri != null && bitmap != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                } else {
                    iBaseResponse.onFailure(response.code() + " " + response.message() + " " + new Gson().toJson(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                if (uri != null && bitmap != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void doEditComment(Comment comment, final Uri uri, ImageView image, final Bitmap bitmap, ClassRichEditText sheet,
                              final ClassApplicationTool applicationTool, ByteArrayOutputStream byteArrayOutputStream,
                              Context context) {
        Map<String, RequestBody> metadata = new HashMap<>();
        metadata.put("auth_id", Global.get().convertToRequestBody(String.valueOf(Session.with(context).getAuth().getId())));
        metadata.put("auth_type", Global.get().convertToRequestBody(String.valueOf(Session.with(context).getLoginType() == ConfigApp.get().STUDENT ? Auth.AUTH_TYPE_STUDENT : Auth.AUTH_TYPE_MENTOR)));
        metadata.put("comment_id", Global.get().convertToRequestBody(String.valueOf(comment.getId())));
        metadata.put("question_id", Global.get().convertToRequestBody(String.valueOf(comment.getQuestion_id())));
        metadata.put("content", Global.get().convertToRequestBody(
                TextUtils.isEmpty(sheet.toString()) ? "" : sheet.getText(RTFormat.HTML)));

        if (uri != null && bitmap != null) {
            temp = applicationTool.getTempFileImage(byteArrayOutputStream);
            RequestBody requestFileImage = RequestBody.create(MediaType.parse("image/jpeg"), temp);

            metadata.put("image\"; filename=\"image.jpg\" ", requestFileImage);
            metadata.put("change_image", Global.get().convertToRequestBody("true"));
        } else {
            if (!comment.getImage().isEmpty() && image.getDrawable() == null) {
                metadata.put("change_image", Global.get().convertToRequestBody("true"));
            } else {
                metadata.put("change_image", Global.get().convertToRequestBody("false"));
            }
        }

        ConnectionAPI.getInstance(context).getApi().editComment(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (uri != null && bitmap != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                } else {
                    iBaseResponse.onFailure(response.code() + " " + response.message() + " " + new Gson().toJson(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                if (uri != null && bitmap != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void doSelectBest(Solution solution, Context context) {
        Map<String, Object> metadata = new HashMap<>();
        try {
            metadata.put("question_id", sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(solution.getQuestion_id()))));
            metadata.put("solution_id", sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(solution.getId()))));
        } catch (Exception e) {
        }

        ConnectionAPI.getInstance(context).getApi().chooseBestSolution(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        response.body().setTag("vote");
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                } else {
                    iBaseResponse.onFailure(response.code() + " " + response.message() + " " + new Gson().toJson(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    private String convertToArrStr(List<Auth> authList) {
        String strArrayAuthId = "";
        for (Auth a : authList) {
            if (a.getAuth_type().equals(Auth.AUTH_TYPE_STUDENT)) {
                strArrayAuthId += Auth.AUTH_TYPE_STUDENT + "." + a.getId();
            } else {
                strArrayAuthId += Auth.AUTH_TYPE_MENTOR + "." + a.getId();
            }
            strArrayAuthId += ",";
        }
        if (strArrayAuthId.length() < 2) {
            return "";
        }
        return strArrayAuthId.substring(0, strArrayAuthId.length() - 1);
    }
}