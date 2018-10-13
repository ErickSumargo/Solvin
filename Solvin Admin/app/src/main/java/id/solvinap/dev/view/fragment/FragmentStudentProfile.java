package id.solvinap.dev.view.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import id.solvinap.dev.R;
import id.solvinap.dev.server.api.Connection;
import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataAuth;
import id.solvinap.dev.server.data.DataStudent;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelStudent;
import id.solvinap.dev.view.activity.ActivityStudentDetail;
import id.solvinap.dev.view.helper.Tool;
import com.squareup.picasso.Picasso;

import java.net.SocketTimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Erick Sumargo on 2/2/2017.
 */

public class FragmentStudentProfile extends Fragment implements IBaseResponse {
    //    VIEW
    private View view;
    private View loadingContainer;
    private LinearLayout profileContainer;
    private RelativeLayout avatarLayout;
    private TextView avatarInitial, name, joinDate;
    private CircleImageView photo;
    private AutoCompleteTextView email, phone, deviceId, membershipCode, age, address, school;
    private TextView credit, creditTimelife, totalComment, totalFreeCredit,
            totalQuestionPending, totalQuestionDiscuss, totalQuestionComplete,
            totalTransactionPending, totalTransactionSuccess, totalTransactionCanceled;

    //    HELPER
    private GradientDrawable gradientDrawable;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    OBJECT
    private ModelStudent student;
    private DataAuth dataAuth;
    private DataStudent dataStudent;

    //    VARIABLE
    private String creditTimelifeText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_student_profile, container, false);

        init();
        fetchProfile();

        return view;
    }

    private void init() {
        //    VIEW
        loadingContainer = view.findViewById(R.id.loading_view_container);
        profileContainer = (LinearLayout) view.findViewById(R.id.student_profile_container);
        avatarLayout = (RelativeLayout) view.findViewById(R.id.student_profile_avatar_layout);
        avatarInitial = (TextView) view.findViewById(R.id.student_profile_avatar_initial);
        photo = (CircleImageView) view.findViewById(R.id.student_profile_photo);
        name = (TextView) view.findViewById(R.id.student_profile_name);
        joinDate = (TextView) view.findViewById(R.id.student_profile_join_date);

        email = (AutoCompleteTextView) view.findViewById(R.id.student_profile_email);
        phone = (AutoCompleteTextView) view.findViewById(R.id.student_profile_phone);
        deviceId = (AutoCompleteTextView) view.findViewById(R.id.student_profile_device_id);
        membershipCode = (AutoCompleteTextView) view.findViewById(R.id.student_profile_membership_code);
        age = (AutoCompleteTextView) view.findViewById(R.id.student_profile_age);
        address = (AutoCompleteTextView) view.findViewById(R.id.student_profile_address);
        school = (AutoCompleteTextView) view.findViewById(R.id.student_profile_school);

        credit = (TextView) view.findViewById(R.id.student_profile_credit);
        creditTimelife = (TextView) view.findViewById(R.id.student_profile_credit_timelife);

        totalComment = (TextView) view.findViewById(R.id.student_profile_total_comment);
        totalFreeCredit = (TextView) view.findViewById(R.id.student_profile_total_free_credit);
        totalQuestionPending = (TextView) view.findViewById(R.id.student_profile_total_question_pending);
        totalQuestionDiscuss = (TextView) view.findViewById(R.id.student_profile_total_question_discuss);
        totalQuestionComplete = (TextView) view.findViewById(R.id.student_profile_total_question_complete);
        totalTransactionPending = (TextView) view.findViewById(R.id.student_profile_total_transaction_pending);
        totalTransactionSuccess = (TextView) view.findViewById(R.id.student_profile_total_transaction_success);
        totalTransactionCanceled = (TextView) view.findViewById(R.id.student_profile_total_transaction_canceled);

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getContext()).getiAPIRequest();
        iBaseResponse = this;

        //    OBJECT
        dataAuth = ((ActivityStudentDetail) getActivity()).dataAuth;

        if (dataAuth.getPhoto().length() > 0) {
            Picasso.with(getContext()).load(Global.ASSETS_URL + "student" + "/" + dataAuth.getPhoto())
                    .placeholder(R.drawable.operator)
                    .fit()
                    .centerCrop()
                    .into(photo);

            avatarLayout.setVisibility(View.GONE);
            photo.setVisibility(View.VISIBLE);
        } else {
            gradientDrawable = (GradientDrawable) avatarLayout.getBackground();
            gradientDrawable.setColor(Tool.getInstance(getContext()).getAvatarColor(dataAuth.getId()));

            avatarInitial.setText(Tool.getInstance(getContext()).getInitialName(dataAuth.getName()));

            avatarLayout.setVisibility(View.VISIBLE);
            photo.setVisibility(View.GONE);
        }

        name.setText(dataAuth.getName());
        joinDate.setText(Tool.getInstance(getContext()).convertToDateTimeFormat(0, dataAuth.getJoinDate()));
        email.setText(dataAuth.getEmail());
        phone.setText(dataAuth.getPhone());
        deviceId.setText(dataAuth.getDeviceId().isEmpty() ? "-" : dataAuth.getDeviceId());
        age.setText(dataAuth.getAge() == -1 ? "-" : dataAuth.getAge() + " tahun");
        address.setText(dataAuth.getAddress().isEmpty() ? "-" : dataAuth.getAddress());
    }

    private void fetchProfile() {
        if (Connectivity.isConnected(getContext())) {
            iAPIRequest.loadStudentProfile(String.valueOf(dataAuth.getId())).enqueue(new Callback<ModelStudent>() {
                @Override
                public void onResponse(Call<ModelStudent> call, retrofit2.Response<ModelStudent> response) {
                    if (response.code() == 200) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<ModelStudent> call, Throwable t) {
                    iBaseResponse.onFailure(t.getMessage());
                    if (t instanceof SocketTimeoutException) {
                        fetchProfile();
                    }
                }
            });
        } else {
            ((ActivityStudentDetail) getActivity()).showNoInternetNotification(new ActivityStudentDetail.INoInternet() {
                @Override
                public void onRetry() {
                    fetchProfile();
                }
            });
        }
    }

    @Override
    public void onSuccess(Response response) {
        student = (ModelStudent) response;
        if (student != null) {
            dataStudent = student.getProfile();

            membershipCode.setText(dataStudent.getMembershipCode().isEmpty() ? "-" : dataStudent.getMembershipCode());
            school.setText(dataStudent.getSchool().isEmpty() ? "-" : dataStudent.getSchool());

            if (dataStudent.isCreditExpired()) {
                credit.setText("-");
                creditTimelife.setVisibility(View.GONE);
            } else {
                credit.setText(String.valueOf(dataStudent.getCredit()));

                creditTimelifeText = Tool.getInstance(getContext()).convertToDateTimeFormat(0, dataStudent.getCreditTimelife());
                creditTimelife.setText(creditTimelifeText);
                creditTimelife.setVisibility(View.VISIBLE);
            }

            totalComment.setText(String.valueOf(dataStudent.getTotalComment()));
            totalFreeCredit.setText(String.valueOf(dataStudent.getTotalFreeCredit()));
            totalQuestionPending.setText(String.valueOf(dataStudent.getTotalQuestionPending()));
            totalQuestionDiscuss.setText(String.valueOf(dataStudent.getTotalQuestionDiscuss()));
            totalQuestionComplete.setText(String.valueOf(dataStudent.getTotalQuestionComplete()));
            totalTransactionPending.setText(String.valueOf(dataStudent.getTotalTransactionPending()));
            totalTransactionSuccess.setText(String.valueOf(dataStudent.getTotalTransactionSuccess()));
            totalTransactionCanceled.setText(String.valueOf(dataStudent.getTotalTransactionCanceled()));

            loadingContainer.setVisibility(View.GONE);
            profileContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFailure(String message) {
        if (this.isVisible()) {
            final Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            Tool.getInstance(getContext()).resizeToast(toast);
            toast.show();
        }
    }
}