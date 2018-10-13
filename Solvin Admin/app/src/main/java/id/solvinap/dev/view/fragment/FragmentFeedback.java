package id.solvinap.dev.view.fragment;

import android.os.Bundle;
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
import id.solvinap.dev.server.data.DataFeedback;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelFeedback;
import id.solvinap.dev.view.activity.MainActivity;
import id.solvinap.dev.view.adapter.FeedbackViewAdapter;
import id.solvinap.dev.view.helper.Tool;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Erick Sumargo on 2/6/2017.
 */

public class FragmentFeedback extends Fragment implements IBaseResponse {
    //    VIEW
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView feedbackView;

    private View nullView;
    private ImageView nullViewImage;
    private TextView nullViewTitle, nullViewDesc;

    //    HELPER
    private LinearLayoutManager layoutManager;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    OBJECT
    private Map<String, Integer> metadata;

    private ModelFeedback feedback;
    private List<DataFeedback> feedbackList;
    private FeedbackViewAdapter feedbackViewAdapter;

    //    VARIABLE
    private int lastId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_feedback, container, false);

        init();
        setEvent();
        fetchPageData();

        return view;
    }

    private void init() {
        //    VIEW
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.feedback_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        if (getActivity().getResources().getBoolean(R.bool.isTablet)) {
            swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        }

        nullView = view.findViewById(R.id.null_view);
        nullViewImage = (ImageView) nullView.findViewById(R.id.null_view_image);
        nullViewTitle = (TextView) nullView.findViewById(R.id.null_view_title);
        nullViewDesc = (TextView) nullView.findViewById(R.id.null_view_desc);

        nullViewImage.setImageResource(R.drawable.ic_item_null);
        nullViewTitle.setText(getResources().getString(R.string.text_no_item));
        nullViewDesc.setText(getResources().getString(R.string.text_no_feedback));

        feedbackView = (RecyclerView) view.findViewById(R.id.feedback_view);
        feedbackList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        feedbackView.setLayoutManager(layoutManager);

        feedbackViewAdapter = new FeedbackViewAdapter(feedbackView, feedbackList);
        feedbackView.setAdapter(feedbackViewAdapter);

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getContext()).getiAPIRequest();
        iBaseResponse = this;
    }

    private void setEvent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetRecyclerViewData();
            }
        });

        feedbackViewAdapter.setOnLoadMoreListener(new FeedbackViewAdapter.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                fetchPageData();
            }
        });

        feedbackViewAdapter.setOnReadStatusListener(new FeedbackViewAdapter.OnSetReadStatusListener() {
            @Override
            public void OnSetReadStatus(int id) {
                setReadStatus(id);
            }
        });
    }

    public void resetRecyclerViewData() {
        lastId = 0;
        feedbackViewAdapter.lastAnimatedPosition = -1;
        fetchPageData();
    }

    private void setNullView(boolean visible) {
        if (visible) {
            nullView.setVisibility(View.VISIBLE);
        } else {
            nullView.setVisibility(View.GONE);
        }
    }

    private void fetchPageData() {
        if (lastId == 0) {
            swipeRefreshLayout.setRefreshing(true);
        }
        if (Connectivity.isConnected(getContext())) {
            iAPIRequest.loadFeedbackList(String.valueOf(lastId)).enqueue(new Callback<ModelFeedback>() {
                @Override
                public void onResponse(Call<ModelFeedback> call, retrofit2.Response<ModelFeedback> response) {
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
                public void onFailure(Call<ModelFeedback> call, Throwable t) {
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

    private void setReadStatus(final int id) {
        metadata = new HashMap<>();
        metadata.put("id", id);

        iAPIRequest.setFeedbackReadStatus(metadata).enqueue(new Callback<Response>() {
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
                if (t instanceof SocketTimeoutException) {
                    setReadStatus(id);
                }
            }
        });
    }

    @Override
    public void onSuccess(Response response) {
        if (response instanceof ModelFeedback) {
            feedback = (ModelFeedback) response;
            final int size = feedback.getFeedbackList().size();
            if (size > 0) {
                if (lastId == 0) {
                    feedbackList = feedback.getFeedbackList();
                } else {
                    feedbackList.addAll(feedback.getFeedbackList());
                }
                feedbackViewAdapter.updateList(feedbackList);
                feedbackViewAdapter.setLoaded();

                lastId = feedbackList.get(feedbackList.size() - 1).getId();
                setNullView(false);
            } else {
                feedbackViewAdapter.updateList(feedbackList);
                if (lastId == 0) {
                    setNullView(true);
                }
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
}