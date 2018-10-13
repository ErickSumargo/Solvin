package id.solvin.dev.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.R;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.Question;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.response.ResponseQuestions;
import id.solvin.dev.presenter.QuestionPresenter;
import id.solvin.dev.view.activities.MainActivity;
import id.solvin.dev.view.adapters.HistoryQuestionViewAdapter;
import id.solvin.dev.view.widget.ClassRichEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by Erick Sumargo on 9/2/2016.
 */
public class FragmentHistoryQuestion extends Fragment implements IBaseResponse {
    //    VIEW
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView historyQuestionView;

    private LinearLayout nullView;
    private TextView nullTitle, nullMessage;
    public FloatingActionButton backHome;
    private Toast toast;

    private ClassRichEditText tempRT;

    //    HELPER
    private LinearLayoutManager layoutManager;

    private RTApi rtApi;
    private RTManager rtManager;

    //    OBJECT
    private QuestionPresenter questionPresenter;

    private ResponseQuestions responseQuestions;
    public List<Question> historyQuestionList;
    private HistoryQuestionViewAdapter historyQuestionViewAdapter;

    private Auth auth;
    private boolean isMyProfile;

    //    VARIABLE
    private int lastId = 0;
    private SpannableStringBuilder titleStyled;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_history_question, container, false);

        rtApi = new RTApi(getContext(), new RTProxyImpl(getActivity()), new RTMediaFactoryImpl(getContext(), true));
        rtManager = new RTManager(rtApi, savedInstanceState);

        init();
        setEvent();
        showData(null, null);

        return view;
    }

    private void init() {
        //    VIEW
        if (Build.VERSION.SDK_INT >= 21) {
            view.findViewById(R.id.shadow_view).setVisibility(GONE);
        } else {
            view.findViewById(R.id.shadow_view).setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.history_question_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        if (getActivity().getResources().getBoolean(R.bool.isTablet)) {
            swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        }

        tempRT = (ClassRichEditText) view.findViewById(R.id.temp_rt);
        rtManager.registerEditor(tempRT, true);

        if (getArguments() != null && getArguments().getSerializable("user") != null) {
            auth = (Auth) getArguments().getSerializable("user");
        } else {
            auth = Session.with(getContext()).getAuth();
            auth.setAuth_type(Session.with(getContext()).getLoginType() == 0 ? Auth.AUTH_TYPE_STUDENT : Auth.AUTH_TYPE_MENTOR);
        }
        isMyProfile = getArguments().getBoolean("is.my.profile", true);

        historyQuestionView = (RecyclerView) view.findViewById(R.id.history_question_view);
        historyQuestionList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        historyQuestionView.setLayoutManager(layoutManager);

        historyQuestionViewAdapter = new HistoryQuestionViewAdapter(historyQuestionView, historyQuestionList, tempRT, auth.getAuth_type(), getActivity());
        historyQuestionView.setAdapter(historyQuestionViewAdapter);

        nullView = (LinearLayout) view.findViewById(R.id.history_question_null_view);
        nullTitle = (TextView) view.findViewById(R.id.history_question_null_title);
        nullMessage = (TextView) view.findViewById(R.id.history_question_null_message);
        backHome = (FloatingActionButton) view.findViewById(R.id.history_question_back_home);

        //    OBJECT
        questionPresenter = new QuestionPresenter(this);
    }

    private void setEvent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetRecyclerViewData();
            }
        });

        historyQuestionViewAdapter.setOnLoadMoreListener(new HistoryQuestionViewAdapter.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                showData(null, null);
            }
        });

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void initHistoryView() {
        if (historyQuestionList.size() == 0) {
            historyQuestionView.setVisibility(View.GONE);
            nullView.setVisibility(View.VISIBLE);

            titleStyled = new SpannableStringBuilder("Belum Ada Daftar Aktivitas");
            titleStyled.setSpan(new StyleSpan(Typeface.BOLD), 0, titleStyled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            nullTitle.setText(titleStyled);
            if (isMyProfile) {
                backHome.setVisibility(View.VISIBLE);
                if (auth.getAuth_type().equals("student")) {
                    nullMessage.setText("Mulai ajukan pertanyaan sekarang");
                } else {
                    nullMessage.setText("Segera ajukan solusi untuk tiap pertanyaan yang tersedia");
                }
                nullMessage.setVisibility(View.VISIBLE);
            } else {
                backHome.setVisibility(View.GONE);
                nullMessage.setVisibility(View.GONE);
            }
        } else {
            historyQuestionView.setVisibility(View.VISIBLE);
            nullView.setVisibility(View.GONE);
            backHome.setVisibility(View.GONE);
        }
    }

    public void showData(String status, String materi_id) {
        if (Connectivity.isConnected(getContext())) {
            questionPresenter.doLoadFeedData(
                    auth.getAuth_type(),
                    auth.getId() + "",
                    lastId, status, materi_id, getContext());
        } else {
            ((MainActivity) getActivity()).showNotificationNetwork(
                    new MainActivity.INoInternet() {
                        @Override
                        public void onRetry() {
                            showData(null, null);
                        }
                    }
            );
        }
    }

    public void resetRecyclerViewData() {
        lastId = 0;

        historyQuestionViewAdapter.lastAnimatedPosition = -1;
        swipeRefreshLayout.setRefreshing(true);
        showData(null, null);
    }

    @Override
    public void onSuccess(Response response) {
        if (lastId == 0) {
            swipeRefreshLayout.setRefreshing(false);
        }

        responseQuestions = (ResponseQuestions) response;
        final int size = responseQuestions.getData().getQuestions().size();
        if (size > 0) {
            if (lastId == 0) {
                historyQuestionList = responseQuestions.getData().getQuestions();
            } else {
                historyQuestionList.addAll(responseQuestions.getData().getQuestions());
            }
            historyQuestionViewAdapter.updateList(historyQuestionList, responseQuestions.getData().isRelated());
            historyQuestionViewAdapter.setLoaded();

            lastId = historyQuestionList.get(historyQuestionList.size() - 1).getId();
        } else {
            historyQuestionViewAdapter.updateList(historyQuestionList);
        }
        initHistoryView();
    }

    @Override
    public void onFailure(String message) {
        if (lastId == 0) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (this.isVisible()) {
            toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            ((MainActivity) getActivity()).applicationTool.resizeToast(toast);
            toast.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Global.TRIGGGET_UPDATE:
                    lastId = 0;
                    showData(null, null);
                    break;
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rtManager.onDestroy(isRemoving());
    }
}