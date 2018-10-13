package id.solvinap.dev.view.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import id.solvinap.dev.R;
import id.solvinap.dev.server.api.Connection;
import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataQuestion;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelQuestion;
import id.solvinap.dev.view.activity.ActivityVoteSolution;
import id.solvinap.dev.view.activity.MainActivity;
import id.solvinap.dev.view.adapter.PriorityQuestionViewAdapter;
import id.solvinap.dev.view.helper.Tool;
import id.solvinap.dev.view.widget.CustomRichEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Erick Sumargo on 2/20/2017.
 */

public class FragmentPriorityQuestion extends Fragment implements IBaseResponse {
    //    VIEW
    private View view;
    private CoordinatorLayout mainLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView priorityQuestionView;
    private Snackbar snackbar;

    private CustomRichEditText tempRT;

    private View nullView;
    private ImageView nullViewImage;
    private TextView nullViewTitle, nullViewDesc;

    //    HELPER
    private LinearLayoutManager layoutManager;

    private RTApi rtApi;
    private RTManager rtManager;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    OBJECT
    private ModelQuestion question;
    private List<DataQuestion> questionList;
    private PriorityQuestionViewAdapter priorityQuestionViewAdapter;

    private ActivityVoteSolution voteSolution;

    //    VARIABLE
    public int lastId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_priority_question, container, false);

        rtApi = new RTApi(getContext(), new RTProxyImpl(getActivity()), new RTMediaFactoryImpl(getContext(), true));
        rtManager = new RTManager(rtApi, savedInstanceState);

        init();
        setEvent();
        fetchPageData();

        return view;
    }

    private void init() {
        //    VIEW
        if (Build.VERSION.SDK_INT >= 21) {
            view.findViewById(R.id.shadow_view).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.shadow_view).setVisibility(View.VISIBLE);
        }

        mainLayout = (CoordinatorLayout) view.findViewById(R.id.priority_question_main_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.priority_question_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        if (getActivity().getResources().getBoolean(R.bool.isTablet)) {
            swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        }

        tempRT = (CustomRichEditText) view.findViewById(R.id.temp_rt);
        rtManager.registerEditor(tempRT, true);

        nullView = view.findViewById(R.id.null_view);
        nullViewImage = (ImageView) nullView.findViewById(R.id.null_view_image);
        nullViewTitle = (TextView) nullView.findViewById(R.id.null_view_title);
        nullViewDesc = (TextView) nullView.findViewById(R.id.null_view_desc);

        nullViewImage.setImageResource(R.drawable.ic_item_null);
        nullViewTitle.setText(getResources().getString(R.string.text_no_item));
        nullViewDesc.setText(getResources().getString(R.string.text_no_priority_vote_solution));

        priorityQuestionView = (RecyclerView) view.findViewById(R.id.priority_question_view);
        questionList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        priorityQuestionView.setLayoutManager(layoutManager);

        priorityQuestionViewAdapter = new PriorityQuestionViewAdapter(priorityQuestionView, questionList, tempRT);
        priorityQuestionView.setAdapter(priorityQuestionViewAdapter);

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getContext()).getiAPIRequest();
        iBaseResponse = this;

        //    OBJECT
        voteSolution = new ActivityVoteSolution();
    }

    private void setEvent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetRecyclerViewData();
            }
        });

        priorityQuestionViewAdapter.setOnLoadMoreListener(new PriorityQuestionViewAdapter.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                fetchPageData();
            }
        });

        voteSolution.setOnVotedListener(new ActivityVoteSolution.OnVotedListener() {
            @Override
            public void OnVoted() {
                showVotedNotification();

                lastId = 0;
                questionList.clear();
                fetchPageData();
            }
        });
    }

    public void resetRecyclerViewData() {
        lastId = 0;
        priorityQuestionViewAdapter.lastAnimatedPosition = -1;
        fetchPageData();
    }

    private void setNullView(boolean visible) {
        if (visible) {
            nullView.setVisibility(View.VISIBLE);
        } else {
            nullView.setVisibility(View.GONE);
        }
    }

    private void showVotedNotification() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(mainLayout, "Vote solusi terbaik berhasil", Snackbar.LENGTH_LONG);
                Tool.getInstance(getContext()).resizeSnackBar(snackbar, 1);
                snackbar.show();
            }
        }, 500);
    }

    public void fetchPageData() {
        if (lastId == 0) {
            swipeRefreshLayout.setRefreshing(true);
        }
        if (Connectivity.isConnected(getContext())) {
            iAPIRequest.loadPriorityQuestionList(String.valueOf(lastId)).enqueue(new Callback<ModelQuestion>() {
                @Override
                public void onResponse(Call<ModelQuestion> call, retrofit2.Response<ModelQuestion> response) {
                    if (lastId == 0) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (response.code() == 200) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<ModelQuestion> call, Throwable t) {
                    if (lastId == 0) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    iBaseResponse.onFailure(t.getMessage());
                    if (t instanceof SocketTimeoutException) {
                        fetchPageData();
                    }
                }
            });
        } else {
            ((MainActivity) getActivity()).showNoInternetNotification(new MainActivity.INoInternet() {
                @Override
                public void onRetry() {
                    fetchPageData();
                }
            });
        }
    }

    @Override
    public void onSuccess(Response response) {
        question = (ModelQuestion) response;
        final int size = question.getQuestionList().size();
        if (size > 0) {
            if (lastId == 0) {
                questionList = question.getQuestionList();
            } else {
                questionList.addAll(question.getQuestionList());
            }
            priorityQuestionViewAdapter.updateList(questionList);
            priorityQuestionViewAdapter.setLoaded();

            lastId = questionList.get(questionList.size() - 1).getId();
            setNullView(false);
        } else {
            priorityQuestionViewAdapter.updateList(questionList);
            if (questionList.size() == 0) {
                setNullView(true);
            }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rtManager.onDestroy(isRemoving());
    }
}