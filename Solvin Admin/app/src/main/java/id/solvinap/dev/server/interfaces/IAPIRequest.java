package id.solvinap.dev.server.interfaces;

import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.model.ModelAdmin;
import id.solvinap.dev.server.model.ModelAuth;
import id.solvinap.dev.server.model.ModelBalanceBonus;
import id.solvinap.dev.server.model.ModelComment;
import id.solvinap.dev.server.model.ModelCumulativeRecord;
import id.solvinap.dev.server.model.ModelFeedback;
import id.solvinap.dev.server.model.ModelMentor;
import id.solvinap.dev.server.model.ModelMentorList;
import id.solvinap.dev.server.model.ModelNotification;
import id.solvinap.dev.server.model.ModelPrimary;
import id.solvinap.dev.server.model.ModelQuestion;
import id.solvinap.dev.server.model.ModelRedeemBalance;
import id.solvinap.dev.server.model.ModelRegisterMentor;
import id.solvinap.dev.server.model.ModelReport;
import id.solvinap.dev.server.model.ModelSolution;
import id.solvinap.dev.server.model.ModelStudent;
import id.solvinap.dev.server.model.ModelStudentList;
import id.solvinap.dev.server.model.ModelTransaction;

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
 * Created by Erick Sumargo on 2/6/2017.
 */

public interface IAPIRequest {
    @GET("primary")
    Call<ModelPrimary> loadPrimaryData();

    @POST("login")
    Call<ModelAdmin> login(@Body Map<String, String> metadata);

    @GET("main")
    Call<ModelCumulativeRecord> getFinanceRecord();

    @GET("main/home/search")
    Call<ModelAuth> search(@Query("query") String query);

    @GET("main/home/student/{num}")
    Call<ModelStudentList> loadStudentList(@Path("num") String lastId);

    @GET("main/home/mentor/{num}")
    Call<ModelMentorList> loadMentorList(@Path("num") String lastId);

    @Multipart
    @POST("main/home/mentor/register")
    Call<ModelRegisterMentor> registerMentor(@PartMap Map<String, RequestBody> metadata);

    @GET("main/home/notification")
    Call<ModelNotification> loadNotificationList(@Query("last_id") int lastId);

    @GET("main/home/notification/count")
    Call<ModelNotification> getNotificationCount();

    @POST("main/home/notification/read")
    Call<Response> setNotificationReadStatus(@Query("id") int id);

    @GET("main/priority/question/discuss/{num}")
    Call<ModelQuestion> loadPriorityQuestionList(@Path("num") String lastId);

    @GET("main/priority/question/detail")
    Call<ModelSolution> loadPriorityQuestionDetail(@Query("question_id") String questionId);

    @POST("main/priority/question/detail/vote")
    Call<Response> voteBestSolution(@Query("question_id") String questionId,
                                    @Query("solution_id") String solutionId, @Query("mentor_id") String mentorId);

    @GET("main/priority/transaction/{num}")
    Call<ModelTransaction> loadPriorityTransactionList(@Path("num") String lastId);

    @POST("main/priority/transaction/action")
    Call<Response> setTransactionAction(@Query("transaction_id") String id, @Query("action") String action);

    @GET("main/priority/redeem_balance/{num}")
    Call<ModelRedeemBalance> loadPriorityRedeemBalanceList(@Path("num") String lastId);

    @POST("main/priority/redeem_balance/action")
    Call<Response> setRedeemBalanceAction(@Query("id") String id, @Query("mentor_id") String mentorId,
                                          @Query("action") String action);

    @GET("main/feedback/{num}")
    Call<ModelFeedback> loadFeedbackList(@Path("num") String lastId);

    @POST("main/feedback/read")
    Call<Response> setFeedbackReadStatus(@Body Map<String, Integer> metadata);

    @GET("detail/student/profile")
    Call<ModelStudent> loadStudentProfile(@Query("id") String id);

    @GET("detail/student/question/{num}")
    Call<ModelQuestion> loadQuestionList(@Path("num") String lastId, @Query("student_id") String studentId);

    @Multipart
    @POST("detail/student/question/edit")
    Call<Response> editQuestion(@PartMap Map<String, RequestBody> metadata);

    @POST("detail/student/question/block")
    Call<Response> blockQuestion(@Query("id") String id);

    @GET("detail/student/transaction/{num}")
    Call<ModelTransaction> loadTransactionList(@Path("num") String lastId,
                                               @Query("student_id") String studentId);

    @GET("detail/mentor/profile")
    Call<ModelMentor> loadMentorProfile(@Query("id") String id);

    @Multipart
    @POST("detail/mentor/change_photo")
    Call<Response> changePhoto(@PartMap Map<String, RequestBody> metadata);

    @GET("detail/mentor/solution/{num}")
    Call<ModelSolution> loadSolutionList(@Path("num") String lastId, @Query("mentor_id") String mentorId);

    @Multipart
    @POST("detail/mentor/solution/edit")
    Call<Response> editSolution(@PartMap Map<String, RequestBody> metadata);

    @POST("detail/mentor/solution/block")
    Call<Response> blockSolution(@Query("id") String id);

    @GET("detail/mentor/redeem_balance/{num}")
    Call<ModelRedeemBalance> loadRedeemBalanceList(@Path("num") String lastId,
                                                   @Query("mentor_id") String mentorId);

    @GET("detail/mentor/balance_bonus/{num}")
    Call<ModelBalanceBonus> loadBalanceBonusList(@Path("num") String lastId,
                                                 @Query("mentor_id") String mentorId);

    @GET("detail/auth/comment/{num}")
    Call<ModelComment> loadCommentList(@Path("num") String lastId,
                                       @Query("auth_id") String authId,
                                       @Query("auth_type") String authType);

    @Multipart
    @POST("detail/auth/comment/edit")
    Call<Response> editComment(@PartMap Map<String, RequestBody> metadata);

    @POST("detail/auth/comment/block")
    Call<Response> blockComment(@Query("id") String id);

    @POST("detail/auth/block")
    Call<Response> blockAuth(@Query("id") String id, @Query("auth_type") String authType);

    @GET("report")
    Call<ModelReport> loadReportList(@Query("month") int month, @Query("year") int year);
}