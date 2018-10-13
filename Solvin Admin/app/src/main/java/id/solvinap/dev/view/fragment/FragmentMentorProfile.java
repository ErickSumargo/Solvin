package id.solvinap.dev.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.solvinap.dev.R;
import id.solvinap.dev.server.api.Connection;
import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataAuth;
import id.solvinap.dev.server.data.DataMentor;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelMentor;
import id.solvinap.dev.view.activity.ActivityMentorDetail;
import id.solvinap.dev.view.helper.Tool;
import com.squareup.picasso.Picasso;

import java.net.SocketTimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Erick Sumargo on 2/3/2017.
 */

public class FragmentMentorProfile extends Fragment implements IBaseResponse {
    //    VIEW
    private View view;
    private View loadingContainer;
    private LinearLayout profileContainer;
    private CircleImageView photo;
    private TextView name, joinDate;
    private AutoCompleteTextView email, phone, deviceId, mentorshipCode, age, address, workplace;
    private TextView totalBalance, totalBalanceBonus, totalBalanceRedeemed, totalBestSolution, totalSolution,
            totalRedeemBalancePending, totalRedeemBalanceSuccess, totalRedeemBalanceCanceled,
            totalComment;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    OBJECT
    private ModelMentor mentor;
    private DataAuth dataAuth;
    private DataMentor dataMentor;

    //    VARIABLE
    private String balanceValue, balanceBonusValue, balanceRedeemedValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_mentor_profile, container, false);

        init();
        fetchProfile();

        return view;
    }

    private void init() {
        //    VIEW
        loadingContainer = view.findViewById(R.id.loading_view_container);
        profileContainer = (LinearLayout) view.findViewById(R.id.mentor_profile_container);
        photo = (CircleImageView) view.findViewById(R.id.mentor_profile_photo);
        name = (TextView) view.findViewById(R.id.mentor_profile_name);
        joinDate = (TextView) view.findViewById(R.id.mentor_profile_join_date);
        email = (AutoCompleteTextView) view.findViewById(R.id.mentor_profile_email);
        phone = (AutoCompleteTextView) view.findViewById(R.id.mentor_profile_phone);
        deviceId = (AutoCompleteTextView) view.findViewById(R.id.mentor_profile_device_id);
        mentorshipCode = (AutoCompleteTextView) view.findViewById(R.id.mentor_profile_mentorship_code);
        age = (AutoCompleteTextView) view.findViewById(R.id.mentor_profile_age);
        address = (AutoCompleteTextView) view.findViewById(R.id.mentor_profile_address);
        workplace = (AutoCompleteTextView) view.findViewById(R.id.mentor_profile_workplace);

        totalBalance = (TextView) view.findViewById(R.id.mentor_profile_total_balance);
        totalBalanceBonus = (TextView) view.findViewById(R.id.mentor_profile_total_balance_bonus);
        totalBalanceRedeemed = (TextView) view.findViewById(R.id.mentor_profile_total_balance_redeemed);
        totalBestSolution = (TextView) view.findViewById(R.id.mentor_profile_total_best_solution);
        totalSolution = (TextView) view.findViewById(R.id.mentor_profile_total_solution);
        totalRedeemBalancePending = (TextView) view.findViewById(R.id.mentor_profile_total_redeem_balance_pending);
        totalRedeemBalanceSuccess = (TextView) view.findViewById(R.id.mentor_profile_total_redeem_balance_success);
        totalRedeemBalanceCanceled = (TextView) view.findViewById(R.id.mentor_profile_total_redeem_balance_canceled);
        totalComment = (TextView) view.findViewById(R.id.mentor_profile_total_comment);

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getContext()).getiAPIRequest();
        iBaseResponse = this;

        //    OBJECT
        dataAuth = ((ActivityMentorDetail) getActivity()).dataAuth;

        Picasso.with(getContext()).load(Global.ASSETS_URL + "mentor" + "/" + dataAuth.getPhoto())
                .placeholder(R.drawable.operator)
                .fit()
                .centerCrop()
                .into(photo);

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
            iAPIRequest.loadMentorProfile(String.valueOf(dataAuth.getId())).enqueue(new Callback<ModelMentor>() {
                @Override
                public void onResponse(Call<ModelMentor> call, retrofit2.Response<ModelMentor> response) {
                    if (response.code() == 200) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<ModelMentor> call, Throwable t) {
                    iBaseResponse.onFailure(t.getMessage());
                    if (t instanceof SocketTimeoutException) {
                        fetchProfile();
                    }
                }
            });
        } else {
            ((ActivityMentorDetail) getActivity()).showNoInternetNotification(new ActivityMentorDetail.INoInternet() {
                @Override
                public void onRetry() {
                    fetchProfile();
                }
            });
        }
    }

    @Override
    public void onSuccess(Response response) {
        mentor = (ModelMentor) response;
        if (mentor != null) {
            dataMentor = mentor.getProfile();

            mentorshipCode.setText(dataMentor.getMentorshipCode().isEmpty() ? "-" : dataMentor.getMentorshipCode());
            workplace.setText(dataMentor.getWorkplace().isEmpty() ? "-" : dataMentor.getWorkplace());

            balanceValue = Tool.getInstance(getContext()).convertRpCurrency(dataMentor.getTotalBalance());
            balanceBonusValue = Tool.getInstance(getContext()).convertRpCurrency(dataMentor.getTotalBalanceBonus());
            balanceRedeemedValue = Tool.getInstance(getContext()).convertRpCurrency(dataMentor.getTotalBalanceRedeemed());

            totalBalance.setText(balanceValue.substring(4, balanceValue.length()));
            totalBalanceBonus.setText(balanceBonusValue.substring(4, balanceBonusValue.length()));
            totalBalanceRedeemed.setText(balanceRedeemedValue.substring(4, balanceRedeemedValue.length()));

            totalBestSolution.setText(String.valueOf(dataMentor.getTotalBestSolution()));
            totalSolution.setText(String.valueOf(dataMentor.getTotalSolution()));

            totalRedeemBalancePending.setText(String.valueOf(dataMentor.getTotalRedeemBalancePending()));
            totalRedeemBalanceSuccess.setText(String.valueOf(dataMentor.getTotalRedeemBalanceSuccess()));
            totalRedeemBalanceCanceled.setText(String.valueOf(dataMentor.getTotalRedeemBalanceCanceled()));

            totalComment.setText(String.valueOf(dataMentor.getTotalComment()));

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