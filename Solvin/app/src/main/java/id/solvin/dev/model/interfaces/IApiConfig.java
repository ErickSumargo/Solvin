package id.solvin.dev.model.interfaces;

import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.response.ResponseAuth;
import id.solvin.dev.model.response.ResponseFirst;
import id.solvin.dev.model.response.ResponseMonthlyBalance;
import id.solvin.dev.model.response.ResponseNotification;
import id.solvin.dev.model.response.ResponseQuestion;
import id.solvin.dev.model.response.ResponseQuestions;
import id.solvin.dev.model.response.ResponseRedeemBalance;
import id.solvin.dev.model.response.ResponseSearch;
import id.solvin.dev.model.response.ResponseTransaction;
import id.solvin.dev.model.response.ResponseTransactions;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by edinofri on 03/10/2016.
 */

public interface IApiConfig {
    @POST("app/basic")
    Call<ResponseFirst> loadFirstData(@Body Map<String, Object> metadata);

    @POST("auth/register/step-1")
    Call<Response> registerStepOne(@Body Map<String, String> metadata);

    @POST("auth/register/step-2")
    Call<Response> registerStepTwo(@Body Map<String, String> metadata);

    @POST("auth/register/step-3")
    Call<ResponseAuth> registerStepThird(@Body Map<String, String> metadata);

    @POST("auth/login")
    Call<ResponseAuth> login(@Body Map<String, String> metadata);

    @GET("auth/profile/{auth}/{id}")
    Call<ResponseAuth> getProfile(@Path("auth") String auth_type, @Path("id") String auth_id);

    @Multipart
    @POST("auth/profile/update")
    Call<ResponseAuth> updateProfile(@PartMap Map<String, RequestBody> metadata);

    @POST("auth/profile/changepassword")
    Call<Response> updatePassword(@Body Map<String, Object> metadata);

    @GET("auth/search")
    Call<ResponseSearch> search(@Query("q") String query);

    @POST("auth/resetpasswordStepOne")
    Call<Response> resetStepOne(@Body Map<String, String> metadata);

    @POST("auth/resetpasswordStepTwo")
    Call<Response> resetStepTwo(@Body Map<String, String> metadata);

    @POST("auth/resetpasswordStepThree")
    Call<Response> resetStepThree(@Body Map<String, String> metadata);

    @POST("auth/feedback")
    Call<Response> sendFeedback(@Body Map<String, Object> metadata);

    /**
     * Question
     */

    @Multipart
    @POST("question/create")
    Call<Response> createQuestion(@PartMap Map<String, RequestBody> metadata);

    @Multipart
    @POST("question/edit")
    Call<Response> editQuestion(@PartMap Map<String, RequestBody> metadata);

    @GET("question/page/{num}")
    Call<ResponseQuestions> getQuestions(@Path("num") String lastId,
                                         @Query("status") String status,
                                         @Query("material_id") String materi_id,
                                         @Query("auth_id") String auth_id,
                                         @Query("auth_type") String auth_type);

    @GET("auth/notification/count")
    Call<ResponseNotification> getNotificationCount();

    @GET("auth/notification")
    Call<ResponseNotification> getNotification(@Query("last_id") int lastId);

    @GET("auth/notification/read")
    Call<Response> readNotification(@Query("notification_id") int id);

    @GET("question/detail")
    Call<ResponseQuestion> getQuestion(@Query("question_id") int question_id);

    @Multipart
    @POST("question/solution/create")
    Call<Response> addSolution(@PartMap Map<String, RequestBody> metadata);

    @Multipart
    @POST("question/solution/edit")
    Call<Response> editSolution(@PartMap Map<String, RequestBody> metadata);

    @POST("question/solution/best")
    Call<Response> chooseBestSolution(@Body Map<String, Object> metadata);

    @Multipart
    @POST("question/comment/create")
    Call<Response> addComment(@PartMap Map<String, RequestBody> metadata);

    @Multipart
    @POST("question/comment/edit")
    Call<Response> editComment(@PartMap Map<String, RequestBody> metadata);

    /**
     * Transaction
     */

    @POST("transaction/purchase")
    Call<ResponseTransaction> doBuyPackage(@Body Map<String, Object> metadata);

    @GET("transaction/purchase/detail")
    Call<ResponseTransaction> purchaseDetail(@Query("id") int transaction_id);

    @Multipart
    @POST("transaction/purchase/confirm")
    Call<Response> doConfirm(@PartMap Map<String, RequestBody> metadata);

    @POST("transaction/purchase/check")
    Call<ResponseTransaction> doCheckPayment();

    @POST("transaction/purchase/action")
    Call<Response> doConfirmAction(@Body Map<String, Object> metadata);

    @GET("transaction/purchase/history/{num}")
    Call<ResponseTransactions> getTransactionHistory(@Path("num") int lastId);

    @POST("transaction/redeem")
    Call<Response> sendRedeemBalanceRequest(@Query("security_code") String securityCode, @Query("balance") String balance);

    @GET("transaction/redeem/summary")
    Call<ResponseRedeemBalance> getSummaryRedeem();

    @GET("transaction/redeem/history/{num}")
    Call<ResponseRedeemBalance> getRedeemBalanceHistory(@Path("num") String lastId);

    @GET("transaction/redeem/monthly/balance/{num}")
    Call<ResponseMonthlyBalance> getMonthlyBalance(@Path("num") String page);
}