package id.solvinap.dev.view.fragment;

import android.os.Build;
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
import id.solvinap.dev.server.data.DataStudent;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelStudentList;
import id.solvinap.dev.view.activity.MainActivity;
import id.solvinap.dev.view.adapter.StudentFeedViewAdapter;
import id.solvinap.dev.view.helper.Tool;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Erick Sumargo on 1/31/2017.
 */

public class FragmentStudentFeed extends Fragment implements IBaseResponse {
    //    VIEW
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView studentFeedView;

    private View nullView;
    private ImageView nullViewImage;
    private TextView nullViewTitle, nullViewDesc;

    //    HELPER
    private LinearLayoutManager layoutManager;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    private ModelStudentList student;
    private List<DataStudent> studentList;
    private StudentFeedViewAdapter studentFeedViewAdapter;

    //    VARIABLE
    private int lastId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_student_feed, container, false);

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

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.student_feed_swipe_refresh);
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
        nullViewDesc.setText(getResources().getString(R.string.text_no_student));

        studentFeedView = (RecyclerView) view.findViewById(R.id.student_feed_view);
        layoutManager = new LinearLayoutManager(getActivity());
        studentFeedView.setLayoutManager(layoutManager);

        studentList = new ArrayList<>();
        studentFeedViewAdapter = new StudentFeedViewAdapter(studentFeedView, studentList);
        studentFeedView.setAdapter(studentFeedViewAdapter);

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getContext()).getiAPIRequest();
        iBaseResponse = this;
    }

    private void setEvent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity) getActivity()).getFinanceRecord();
                resetRecyclerViewData();
            }
        });

        studentFeedViewAdapter.setOnCollapseSearchView(new StudentFeedViewAdapter.OnCollapseSearchView() {
            @Override
            public void OnCollapseSearchView() {
                ((FragmentHome) getParentFragment()).searchMenuItem.collapseActionView();
            }
        });

        studentFeedViewAdapter.setOnLoadMoreListener(new StudentFeedViewAdapter.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                fetchPageData();
            }
        });
    }

    public void resetRecyclerViewData() {
        lastId = 0;
        studentFeedViewAdapter.lastAnimatedPosition = -1;

        fetchPageData();
    }

    private void setNullView(boolean visible) {
        if (visible) {
            nullView.setVisibility(View.VISIBLE);
        } else {
            nullView.setVisibility(View.GONE);
        }
    }

    public void fetchPageData() {
        if (lastId == 0) {
            swipeRefreshLayout.setRefreshing(true);
        }
        if (Connectivity.isConnected(getContext())) {
            iAPIRequest.loadStudentList(String.valueOf(lastId)).enqueue(new Callback<ModelStudentList>() {
                @Override
                public void onResponse(Call<ModelStudentList> call, retrofit2.Response<ModelStudentList> response) {
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
                public void onFailure(Call<ModelStudentList> call, Throwable t) {
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
        student = (ModelStudentList) response;
        final int size = student.getStudentList().size();
        if (size > 0) {
            if (lastId == 0) {
                studentList = student.getStudentList();
            } else {
                studentList.addAll(student.getStudentList());
            }
            studentFeedViewAdapter.updateList(studentList);
            studentFeedViewAdapter.setLoaded();

            lastId = studentList.get(studentList.size() - 1).getId();
            setNullView(false);
        } else {
            studentFeedViewAdapter.updateList(studentList);
            if (lastId == 0) {
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
}