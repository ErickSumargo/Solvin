package id.solvin.dev.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.ConnectionAPI;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.SCrypt;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.Transaction;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.response.ResponseMonthlyBalance;
import id.solvin.dev.model.response.ResponseRedeemBalance;
import id.solvin.dev.model.response.ResponseTransaction;
import id.solvin.dev.model.response.ResponseTransactions;
import id.solvin.dev.view.widget.CustomProgressDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static id.solvin.dev.helper.ClassMediaChooser.uri;

/**
 * Created by edinofri on 07/01/2017.
 */

public class TransactionPresenter {
    private IBaseResponse baseResponse;
    private File temp;
    private SCrypt sCrypt;

    public TransactionPresenter(IBaseResponse baseResponse) {
        this.baseResponse = baseResponse;
        sCrypt = new SCrypt();
    }

    public void buyPackage(final String paket_id, final String bank_id, final String mobile_network_id, final String unique_code, final Context context) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(Global.get().key().REQUEST_PACKAGE_ID, paket_id);
        metadata.put(Global.get().key().REQUEST_BANK_ID, bank_id);
        metadata.put(Global.get().key().REQUEST_MOBILE_NETWORK_ID, mobile_network_id);
        metadata.put(Global.get().key().REQUEST_UNIQUE_CODE, unique_code);
        ConnectionAPI.getInstance(context).getApi().doBuyPackage(metadata).enqueue(new Callback<ResponseTransaction>() {
            @Override
            public void onResponse(Call<ResponseTransaction> call, retrofit2.Response<ResponseTransaction> response) {
                if (response.code() == 200) {
                    if (response.isSuccessful()) {
                        response.body().setTag(Response.TAG_PURCHASE_PACKAGE_BUY);
                        baseResponse.onSuccess(response.body());
                    } else {
                        baseResponse.onFailure(response.message());
                    }
                } else {
                    baseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseTransaction> call, Throwable t) {
                baseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void confirmPackage(final Transaction transaction, final String bank_name, final String bank_name_other,
                               final String bank_account_owner, final String mobile_number_owner,
                               final Uri uri, final Bitmap bitmap, final ClassApplicationTool applicationTool,
                               final ByteArrayOutputStream byteArrayOutputStream, final Context context) {
        Map<String, RequestBody> metadata = new HashMap<>();
        metadata.put("transaction_id", Global.get().convertToRequestBody(String.valueOf(transaction.getId())));
        metadata.put("bank_name", Global.get().convertToRequestBody(bank_name));
        metadata.put("bank_name_other", Global.get().convertToRequestBody(bank_name_other));
        metadata.put("bank_account_owner", Global.get().convertToRequestBody(bank_account_owner));
        metadata.put("mobile_number_owner", Global.get().convertToRequestBody(mobile_number_owner));
        if (uri != null && bitmap != null) {
            temp = applicationTool.getTempFileImage(byteArrayOutputStream);
            RequestBody requestFileImage = RequestBody.create(MediaType.parse("image/jpeg"), temp);
            metadata.put("image\"; filename=\"image.jpg\" ", requestFileImage);
        }

        ConnectionAPI.getInstance(context).getApi().doConfirm(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (uri != null && bitmap != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        response.body().setTag(Response.TAG_CONFIRM_PACKAGE_SUCCESS);
                        baseResponse.onSuccess(response.body());
                    } else {
                        baseResponse.onFailure(response.body().getMessage());
                    }
                } else {
                    baseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                if (uri != null && bitmap != null) {
                    applicationTool.deleteTempFileImage(temp);
                }
                baseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void checkTransaction(final Context context) {
        ConnectionAPI.getInstance(context).getApi().doCheckPayment().enqueue(new Callback<ResponseTransaction>() {
            @Override
            public void onResponse(Call<ResponseTransaction> call, retrofit2.Response<ResponseTransaction> response) {
                if (response.code() == 200) {
                    response.body().setTag(Response.TAG_PURCHASE_PACKAGE_CHECK);
                    baseResponse.onSuccess(response.body());
                } else {
                    baseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseTransaction> call, Throwable t) {
                baseResponse.onFailure(t.getMessage());
                if (t instanceof SocketTimeoutException) {
                    checkTransaction(context);
                }
            }
        });
    }

    public void detailTransaction(final int transaction_id, final Context context) {
        ConnectionAPI.getInstance(context).getApi().purchaseDetail(transaction_id).enqueue(new Callback<ResponseTransaction>() {
            @Override
            public void onResponse(Call<ResponseTransaction> call, retrofit2.Response<ResponseTransaction> response) {
                if (response.code() == 200) {
                    response.body().setTag(Response.TAG_PURCHASE_PACKAGE_DETAIL);
                    baseResponse.onSuccess(response.body());
                } else {
                    baseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseTransaction> call, Throwable t) {
                baseResponse.onFailure(t.getMessage());
                if (t instanceof SocketTimeoutException) {
                    detailTransaction(transaction_id, context);
                }
            }
        });
    }

    public void action(final int transaction_id, final String action, final Context context) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("transaction_id", transaction_id);
        metadata.put("action", action);
        ConnectionAPI.getInstance(context).getApi().doConfirmAction(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        response.body().setTag(Response.TAG_PURCHASE_PACKAGE_ACTION);
                        baseResponse.onSuccess(response.body());
                    } else {
                        baseResponse.onFailure(response.body().getMessage());
                    }
                } else {
                    baseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                baseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void getTransactionHistory(final int lastId, final Context context) {
        ConnectionAPI.getInstance(context).getApi().getTransactionHistory(lastId).enqueue(new Callback<ResponseTransactions>() {
            @Override
            public void onResponse(Call<ResponseTransactions> call, retrofit2.Response<ResponseTransactions> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        response.body().setTag(Response.TAG_PURCHASE_HISTORY);
                        baseResponse.onSuccess(response.body());
                    } else {
                        baseResponse.onFailure(response.body().getMessage());
                    }
                } else {
                    baseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseTransactions> call, Throwable t) {
                baseResponse.onFailure(t.getMessage());
                if (t instanceof SocketTimeoutException) {
                    getTransactionHistory(lastId, context);
                }
            }
        });
    }

    public void getSummaryRedeem(final Context context) {
        ConnectionAPI.getInstance(context).getApi().getSummaryRedeem().enqueue(new Callback<ResponseRedeemBalance>() {
            @Override
            public void onResponse(Call<ResponseRedeemBalance> call, retrofit2.Response<ResponseRedeemBalance> response) {
                if (response.code() == 200) {
                    baseResponse.onSuccess(response.body());
                } else {
                    baseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseRedeemBalance> call, Throwable t) {
                baseResponse.onFailure(t.getMessage());
                if (t instanceof SocketTimeoutException) {
                    getSummaryRedeem(context);
                }
            }
        });
    }

    public void redeemBalance(final String securityCode, final String balance, final CustomProgressDialog customProgressDialog, final Context context) {
        ConnectionAPI.getInstance(context).getApi().sendRedeemBalanceRequest(securityCode, balance).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                customProgressDialog.dismiss();
                if (response.code() == 200) {
                    baseResponse.onSuccess(response.body());
                } else {
                    baseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                customProgressDialog.dismiss();
                baseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void getRedeemBalanceHistory(final int lastId, final Context context) {
        ConnectionAPI.getInstance(context).getApi().getRedeemBalanceHistory(String.valueOf(lastId)).enqueue(new Callback<ResponseRedeemBalance>() {
            @Override
            public void onResponse(Call<ResponseRedeemBalance> call, retrofit2.Response<ResponseRedeemBalance> response) {
                if (response.code() == 200) {
                    baseResponse.onSuccess(response.body());
                } else {
                    baseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseRedeemBalance> call, Throwable t) {
                baseResponse.onFailure(t.getMessage());
                if (t instanceof SocketTimeoutException) {
                    getRedeemBalanceHistory(lastId, context);
                }
            }
        });
    }

    public void getMonthlyBalance(final int page, final Context context) {
        ConnectionAPI.getInstance(context).getApi().getMonthlyBalance(String.valueOf(page)).enqueue(new Callback<ResponseMonthlyBalance>() {
            @Override
            public void onResponse(Call<ResponseMonthlyBalance> call, retrofit2.Response<ResponseMonthlyBalance> response) {
                if (response.code() == 200) {
                    baseResponse.onSuccess(response.body());
                } else {
                    baseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseMonthlyBalance> call, Throwable t) {
                baseResponse.onFailure(t.getMessage());
                if (t instanceof SocketTimeoutException) {
                    getMonthlyBalance(page, context);
                }
            }
        });
    }
}