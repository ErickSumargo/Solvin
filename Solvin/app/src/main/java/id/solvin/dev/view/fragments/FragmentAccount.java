package id.solvin.dev.view.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import id.solvin.dev.helper.ConfigApp;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.Mentor;
import id.solvin.dev.model.basic.ProfileBus;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.Student;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.response.ResponseAuth;
import id.solvin.dev.presenter.AuthPresenter;
import id.solvin.dev.view.activities.ActivityEditProfile;
import id.solvin.dev.view.activities.ActivityHistoryRedeemBalance;
import id.solvin.dev.view.activities.ActivityHistoryTransaction;
import id.solvin.dev.view.activities.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Erick Sumargo on 9/8/2016.
 */
public class FragmentAccount extends Fragment implements IBaseResponse {
    private View view;
    private CoordinatorLayout mainLayout;
    private AutoCompleteTextView email, name, age, address, school, workPlace;
    private NestedScrollView accountContainer;
    private LinearLayout loadingView;
    private TextInputLayout schoolContainer, workPlaceContainer;
    private Button editProfile, historyTransaction, historyRedeemBalance;
    private Toast toast;

    private Intent intent;
    private Session session;
    private AuthPresenter authPresenter;
    private Auth auth;
    private boolean isMyProfile;

    private ActivityEditProfile activityEditProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(id.solvin.dev.R.layout.activity_account, container, false);

        init();
        setEvent();
        loadData();

        return view;
    }

    private void loadData() {
        if (Connectivity.isConnected(getContext())) {
            if (isMyProfile) {
                authPresenter.doGetProfile(session.getLoginType() == 0 ? Auth.AUTH_TYPE_STUDENT : Auth.AUTH_TYPE_MENTOR, String.valueOf(session.getAuth().getId()), getContext());
            } else {
                authPresenter.doGetProfile(auth.getAuth_type(), String.valueOf(auth.getId()), getContext());
            }
        } else {
            ((MainActivity) getActivity()).showNotificationNetwork(
                    new MainActivity.INoInternet() {
                        @Override
                        public void onRetry() {
                            loadData();
                        }
                    }
            );
        }
    }

    private void init() {
        authPresenter = new AuthPresenter(this);
        session = Session.with(getContext());
        if (Build.VERSION.SDK_INT >= 21) {
            view.findViewById(id.solvin.dev.R.id.shadow_view).setVisibility(View.GONE);
        } else {
            view.findViewById(id.solvin.dev.R.id.shadow_view).setVisibility(View.VISIBLE);
        }
        mainLayout = (CoordinatorLayout) view.findViewById(id.solvin.dev.R.id.account_main_layout);
        accountContainer = (NestedScrollView) view.findViewById(id.solvin.dev.R.id.account_container);
        email = (AutoCompleteTextView) view.findViewById(id.solvin.dev.R.id.account_email);
        name = (AutoCompleteTextView) view.findViewById(id.solvin.dev.R.id.account_name);
        age = (AutoCompleteTextView) view.findViewById(id.solvin.dev.R.id.account_age);
        address = (AutoCompleteTextView) view.findViewById(id.solvin.dev.R.id.account_address);
        school = (AutoCompleteTextView) view.findViewById(id.solvin.dev.R.id.account_school);
        workPlace = (AutoCompleteTextView) view.findViewById(id.solvin.dev.R.id.account_workplace);

        schoolContainer = (TextInputLayout) view.findViewById(id.solvin.dev.R.id.account_school_container);
        workPlaceContainer = (TextInputLayout) view.findViewById(id.solvin.dev.R.id.account_workplace_container);

        editProfile = (Button) view.findViewById(id.solvin.dev.R.id.account_edit_profile);
        historyTransaction = (Button) view.findViewById(id.solvin.dev.R.id.account_history_transaction);
        historyRedeemBalance = (Button) view.findViewById(id.solvin.dev.R.id.account_history_redeem_balance);

        loadingView = (LinearLayout) view.findViewById(id.solvin.dev.R.id.account_loading_view);

        accountContainer.setVisibility(View.GONE);

        activityEditProfile = new ActivityEditProfile();

        isMyProfile = getArguments().getBoolean("is.my.profile", true);
        auth = (Auth) getArguments().getSerializable("user");

        if (isMyProfile) {
            loadMyProfile();
        }
        setVisibility();
    }

    private void setVisibility() {
        if (isMyProfile) {
            editProfile.setVisibility(View.VISIBLE);
            if (auth.getAuth_type().equals(Auth.AUTH_TYPE_STUDENT)) {
                historyTransaction.setVisibility(View.VISIBLE);
            } else {
                historyRedeemBalance.setVisibility(View.VISIBLE);
            }
        } else {
            editProfile.setVisibility(View.GONE);
            historyRedeemBalance.setVisibility(View.GONE);
            historyTransaction.setVisibility(View.GONE);
        }
    }

    private void setEvent() {
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity().getApplicationContext(), ActivityEditProfile.class);
                getActivity().startActivityForResult(intent, Global.TRIGGGET_UPDATE);
            }
        });

        historyTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity().getApplicationContext(), ActivityHistoryTransaction.class);
                startActivity(intent);
            }
        });

        historyRedeemBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity().getApplicationContext(), ActivityHistoryRedeemBalance.class);
                startActivity(intent);
            }
        });

        activityEditProfile.setProfileUpdated(new ActivityEditProfile.OnProfileUpdated() {
            @Override
            public void OnProfileUpdated() {
                showNotificationProfileUpdated();
            }
        });
    }

    private void showNotificationProfileUpdated() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Snackbar snackbar = Snackbar.make(mainLayout, "Profil berhasil diperbarui", Snackbar.LENGTH_LONG);
                ((MainActivity) getActivity()).applicationTool.resizeSnackBar(snackbar, 1);
                snackbar.show();
            }
        }, 500);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadData();
    }

    @Override
    public void onSuccess(Response response) {
        if (response.getTag().equals(Response.TAG_PROFILE_LOAD)) {
            ResponseAuth responseAuth = (ResponseAuth) response;
            if (isMyProfile) {
                session = Session.with(getContext());
                session.createSessionToken(responseAuth.getData().getToken());
                session.createSessionLogin(responseAuth.getData().getAuth());
                loadMyProfile();
            } else {
                auth = responseAuth.getData().getAuth();
                refreshUserProfile();
            }
        }
        loadingView.setVisibility(View.GONE);
        accountContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(String message) {
        if (this.isVisible()) {
            toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            ((MainActivity) getActivity()).applicationTool.resizeToast(toast);
            toast.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onRefreshSession(ProfileBus profileBus) {
        session = Session.with(getContext());
        loadMyProfile();
    }

    private void loadMyProfile() {
        auth = session.getAuth();
        email.setText(auth.getEmail());
        name.setText(auth.getName());
        age.setText(auth.getAge() == -1 ? "-" : auth.getAge() + " tahun");
        address.setText(auth.getAddress().isEmpty() ? "-" : auth.getAddress());
        if (session.getLoginType() == ConfigApp.get().STUDENT) {
            final String schoolText = ((Student) auth).getSchool();
            school.setText(schoolText.isEmpty() ? "-" : schoolText);

            workPlaceContainer.setVisibility(View.GONE);
            historyRedeemBalance.setVisibility(View.GONE);
        } else {
            final String workPlaceText = ((Mentor) auth).getWorkplace();
            workPlace.setText(workPlaceText.isEmpty() ? "-" : workPlaceText);

            schoolContainer.setVisibility(View.GONE);
            historyTransaction.setVisibility(View.GONE);
        }
    }

    private void refreshUserProfile() {
        email.setText(auth.getEmail());
        name.setText(auth.getName());
        age.setText(auth.getAge() == -1 ? "-" : auth.getAge() + " tahun");
        address.setText(auth.getAddress().isEmpty() ? "-" : auth.getAddress());
        if (auth.getAuth_type().equals(Auth.AUTH_TYPE_STUDENT)) {
            final String schoolText = Student.parseToStudent(auth).getSchool();
            school.setText(schoolText.isEmpty() ? "-" : schoolText);

            workPlaceContainer.setVisibility(View.GONE);
            historyRedeemBalance.setVisibility(View.GONE);
        } else {
            final String workPlaceText = Mentor.parseToMentor(auth).getWorkplace();
            workPlace.setText(workPlaceText.isEmpty() ? "-" : workPlaceText);

            schoolContainer.setVisibility(View.GONE);
            historyTransaction.setVisibility(View.GONE);
        }
    }
}