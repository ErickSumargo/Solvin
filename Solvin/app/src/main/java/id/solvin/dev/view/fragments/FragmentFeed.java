package id.solvin.dev.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
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
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.Global;
import id.solvin.dev.model.basic.FeedNotification;
import id.solvin.dev.model.basic.Question;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.TransferCategory;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.response.ResponseQuestions;
import id.solvin.dev.presenter.QuestionPresenter;
import id.solvin.dev.view.activities.MainActivity;
import id.solvin.dev.view.adapters.FeedViewAdapter;
import id.solvin.dev.view.widget.ClassRichEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erick Sumargo on 8/31/2016.
 */

public class FragmentFeed extends Fragment implements IBaseResponse {
    //    INTERFACE
    public interface OnClearFeedNotificationListener {
        void OnClearFeedNotification();
    }

    private static OnClearFeedNotificationListener mClearFeedNotification;

    public void setOnClearFeedNotificationListener(OnClearFeedNotificationListener listener) {
        mClearFeedNotification = listener;
    }

    private void clearFeedNotification() {
        if (mClearFeedNotification != null) {
            mClearFeedNotification.OnClearFeedNotification();
        }
    }

    //    VIEW
    private View view;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView feedView;

    public LinearLayout searchNullView;
    private TextView searchNullTitle, searchNullDesc;
    private Toast toast;

    private ClassRichEditText tempRT;

    //    HELPER
    private LinearLayoutManager layoutManager;

    public ClassApplicationTool applicationTool;

    private RTApi rtApi;
    private RTManager rtManager;

    //    OBJECT
    private QuestionPresenter questionPresenter;

    private ResponseQuestions responseQuestions;
    public List<Question> feedList;
    private FeedViewAdapter feedViewAdapter;

    //    FIREBASE
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    //    VARIABLE
    private int lastId = 0;
    private SpannableStringBuilder titleStyled;
    private String materi_id = null, status = null;


    public FragmentFeed() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_feed, container, false);

        rtApi = new RTApi(getContext(), new RTProxyImpl(getActivity()), new RTMediaFactoryImpl(getContext(), true));
        rtManager = new RTManager(rtApi, savedInstanceState);

        init();
        setEvent();
        resetRecyclerViewData();

        return view;
    }

    private void init() {
        //    VIEW
        if (Build.VERSION.SDK_INT >= 21) {
            view.findViewById(R.id.shadow_view).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.shadow_view).setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        if (getActivity().getResources().getBoolean(R.bool.isTablet)) {
            swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        }

        tempRT = (ClassRichEditText) view.findViewById(R.id.temp_rt);
        rtManager.registerEditor(tempRT, true);

        feedView = (RecyclerView) view.findViewById(R.id.feed_view);
        feedList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        feedView.setLayoutManager(layoutManager);

        feedViewAdapter = new FeedViewAdapter(feedView, feedList, tempRT, getActivity());
        feedView.setAdapter(feedViewAdapter);

        searchNullView = (LinearLayout) view.findViewById(R.id.feed_search_null_view);
        searchNullTitle = (TextView) view.findViewById(R.id.feed_search_null_title);
        searchNullDesc = (TextView) view.findViewById(R.id.feed_search_null_desc);

        searchNullView.setVisibility(View.GONE);

        swipeRefreshLayout.setRefreshing(false);

        //    HELPER
        applicationTool = new ClassApplicationTool(getContext());

        //    OBJECT
        questionPresenter = new QuestionPresenter(this);

        //    FIREBASE
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void setEvent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastId = 0;

                feedViewAdapter.lastAnimatedPosition = -1;
                showData(status, materi_id);
            }
        });

        feedViewAdapter.setOnLoadMoreListener(new FeedViewAdapter.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                showData(status, materi_id);
            }
        });

        feedViewAdapter.setOnCollapseHomeSearchViewListener(new FeedViewAdapter.OnCollapseHomeSearchViewListener() {
            @Override
            public void OnCollapseHomeSearchView() {
                ((FragmentHome) getParentFragment()).searchMenuItem.collapseActionView();
            }
        });
    }

    public void resetRecyclerViewData() {
        materi_id = null;
        status = null;
        lastId = 0;

        feedViewAdapter.lastAnimatedPosition = -1;

        swipeRefreshLayout.setRefreshing(true);
        showData(status, materi_id);
    }

    private void setNullView(int mode) {
        if (mode == 0) {
            titleStyled = new SpannableStringBuilder("Feed Kosong");
            searchNullDesc.setText("Belum ada pertanyaan sementara");
        } else {
            titleStyled = new SpannableStringBuilder("Tidak Ada Hasil Pencarian");
            searchNullDesc.setText("Hasil pencarian pertanyaan tidak ditemukan");

            searchNullTitle.setVisibility(View.VISIBLE);
        }
        titleStyled.setSpan(new StyleSpan(Typeface.BOLD), 0, titleStyled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        searchNullTitle.setText(titleStyled);
    }

    public void showData(final String status, final String materi_id) {
        if (Connectivity.isConnected(getContext())) {
            questionPresenter.doLoadFeedData("", "",
                    lastId, status, materi_id, getContext());
        } else {
            ((MainActivity) getActivity()).showNotificationNetwork(
                    new MainActivity.INoInternet() {
                        @Override
                        public void onRetry() {
                            showData(status, materi_id);
                        }
                    }
            );
        }
    }

    @Override
    public void onSuccess(Response response) {
        if (lastId == 0) {
            swipeRefreshLayout.setRefreshing(false);
        }

        responseQuestions = (ResponseQuestions) response;
        final int size = responseQuestions.getData().getQuestions().size();
        if (size > 0) {
            feedView.setVisibility(View.VISIBLE);
            searchNullView.setVisibility(View.GONE);

            if (lastId == 0) {
                feedList = responseQuestions.getData().getQuestions();
            } else {
                feedList.addAll(responseQuestions.getData().getQuestions());
            }
            feedViewAdapter.updateList(feedList, responseQuestions.getData().isRelated());
            feedViewAdapter.setLoaded();

            if (lastId == 0) {
                EventBus.getDefault().post(new FeedNotification(responseQuestions.getData().getMax_id(), true));
                clearFeedNotification();
            } else {
                EventBus.getDefault().post(new FeedNotification(responseQuestions.getData().getMax_id(), false));
            }
            lastId = feedList.get(feedList.size() - 1).getId();
        } else {
            if (lastId == 0) {
                if (materi_id == null) {
                    setNullView(0);
                } else {
                    setNullView(1);
                }
                feedView.setVisibility(View.GONE);
                searchNullView.setVisibility(View.VISIBLE);
            } else {
                feedView.setVisibility(View.VISIBLE);
                searchNullView.setVisibility(View.GONE);

                feedViewAdapter.updateList(feedList);
            }
        }
    }

    @Override
    public void onFailure(String message) {
        if (lastId == 0) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (this.isVisible()) {
            final Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
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
                    feedView.scrollToPosition(0);
                    showData(null, null);

                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Subscribe
    public void onCategoryRecieved(TransferCategory transferCategory) {
        feedList.clear();
        lastId = 0;

        status = transferCategory.getStatus();
        materi_id = String.valueOf(transferCategory.getMaterial());

        feedView.scrollToPosition(0);

        swipeRefreshLayout.setRefreshing(true);
        showData(status, materi_id);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rtManager.onDestroy(isRemoving());
    }
}