package id.solvinap.dev.view.fragment;

import android.content.Context;
import android.content.SharedPreferences;
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
import id.solvinap.dev.server.data.DataComment;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelComment;
import id.solvinap.dev.view.activity.ActivityCommentEditingSheet;
import id.solvinap.dev.view.activity.ActivityMentorDetail;
import id.solvinap.dev.view.activity.ActivityStudentDetail;
import id.solvinap.dev.view.adapter.CommentViewAdapter;
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
 * Created by Erick Sumargo on 2/2/2017.
 */

public class FragmentAuthComment extends Fragment implements IBaseResponse {
    //    VIEW
    private View view;
    private CoordinatorLayout mainLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView commentView;
    private Snackbar snackbar;

    private CustomRichEditText tempRT;

    private View nullView;
    private ImageView nullViewImage;
    private TextView nullViewTitle, nullViewDesc;

    //    HELPER
    private LinearLayoutManager layoutManager;

    private RTApi rtApi;
    private RTManager rtManager;

    private SharedPreferences sharedPreferences;

    //    OBJECT
    private ActivityCommentEditingSheet commentEditingSheet;

    private ModelComment comment;
    private List<DataComment> commentList;
    private CommentViewAdapter commentViewAdapter;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    VARIABLE
    public int lastId = 0, authDetailInstance, authId;
    private String authType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_comment, container, false);

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

        mainLayout = (CoordinatorLayout) view.findViewById(R.id.comment_main_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.comment_swipe_refresh);
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
        nullViewDesc.setText(getResources().getString(R.string.text_no_comment) +
                (getActivity() instanceof ActivityStudentDetail ? " murid" : " mentor") + " sementara");

        commentView = (RecyclerView) view.findViewById(R.id.comment_view);
        commentList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        commentView.setLayoutManager(layoutManager);

        commentViewAdapter = new CommentViewAdapter(commentView, commentList, tempRT);
        commentView.setAdapter(commentViewAdapter);

        //    HELPER
        sharedPreferences = getContext().getSharedPreferences(Global.PREFERENCES_NAME, Context.MODE_PRIVATE);

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getContext()).getiAPIRequest();
        iBaseResponse = this;

        //    OBJECT
        commentEditingSheet = new ActivityCommentEditingSheet();
    }

    private void setEvent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetRecyclerViewData();
            }
        });

        commentViewAdapter.setOnLoadMoreListener(new CommentViewAdapter.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                fetchPageData();
            }
        });

        commentViewAdapter.setOnBlockCommentListener(new CommentViewAdapter.OnBlockCommentListener() {
            @Override
            public void OnBlock(String message, int id) {
                blockComment(message, id);
            }
        });

        commentEditingSheet.setCommentSent(new ActivityCommentEditingSheet.OnCommentSent() {
            @Override
            public void ISentConfirmedEditted() {
                showNotificationCommentEditted();

                lastId = 0;
                fetchPageData();
            }
        });
    }

    public void resetRecyclerViewData() {
        lastId = 0;
        commentViewAdapter.lastAnimatedPosition = -1;
        fetchPageData();
    }

    private void setNullView(boolean visible) {
        if (visible) {
            nullView.setVisibility(View.VISIBLE);
        } else {
            nullView.setVisibility(View.GONE);
        }
    }

    private void showNotificationCommentEditted() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(mainLayout, "Komentar telah berhasil diedit", Snackbar.LENGTH_LONG);
                Tool.getInstance(getContext()).resizeSnackBar(snackbar, 1);
                snackbar.show();
            }
        }, 500);
    }

    public void showNotificationCommentBlocked() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(mainLayout, "Komentar telah berhasil diblokir", Snackbar.LENGTH_LONG);
                Tool.getInstance(getContext()).resizeSnackBar(snackbar, 2);
                snackbar.show();
            }
        }, 500);
    }

    public void fetchPageData() {
        if (lastId == 0) {
            swipeRefreshLayout.setRefreshing(true);
        }
        authDetailInstance = getActivity() instanceof ActivityStudentDetail ? 0 : 1;
        authId = authDetailInstance == 0 ?
                ((ActivityStudentDetail) getActivity()).dataAuth.getId() :
                ((ActivityMentorDetail) getActivity()).dataAuth.getId();
        authType = sharedPreferences.getString(Global.PREFERENCES_AUTH_TYPE, null);
        if (authType == null) {
            authType = authDetailInstance == 0 ? "student" : "mentor";
        }
        if (Connectivity.isConnected(getContext())) {
            iAPIRequest.loadCommentList(String.valueOf(lastId), String.valueOf(authId), authType)
                    .enqueue(new Callback<ModelComment>() {
                        @Override
                        public void onResponse(Call<ModelComment> call, retrofit2.Response<ModelComment> response) {
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
                        public void onFailure(Call<ModelComment> call, Throwable t) {
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
            if (getActivity() instanceof ActivityStudentDetail) {
                ((ActivityStudentDetail) getActivity()).showNoInternetNotification(new ActivityStudentDetail.INoInternet() {
                    @Override
                    public void onRetry() {
                        fetchPageData();
                    }
                });
            } else {
                ((ActivityMentorDetail) getActivity()).showNoInternetNotification(new ActivityMentorDetail.INoInternet() {
                    @Override
                    public void onRetry() {
                        fetchPageData();
                    }
                });
            }
        }
    }

    private void blockComment(final String message, final int id) {
        commentViewAdapter.showCustomProgressDialog(message);
        if (Connectivity.isConnected(getContext())) {
            iAPIRequest.blockComment(String.valueOf(id)).enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    commentViewAdapter.customProgressDialog.dismiss();

                    if (response.code() == 200) {
                        if (authDetailInstance == 0) {
                            showNotificationCommentBlocked();
                        } else {
                            showNotificationCommentBlocked();
                        }

                        lastId = 0;
                        fetchPageData();
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {
                    commentViewAdapter.customProgressDialog.dismiss();

                    iBaseResponse.onFailure(t.getMessage());
                }
            });
        } else {
            commentViewAdapter.customProgressDialog.dismiss();
            if (getActivity() instanceof ActivityStudentDetail) {
                ((ActivityStudentDetail) getActivity()).showNoInternetNotification(new ActivityStudentDetail.INoInternet() {
                    @Override
                    public void onRetry() {
                        blockComment(message, id);
                    }
                });
            } else {
                ((ActivityMentorDetail) getActivity()).showNoInternetNotification(new ActivityMentorDetail.INoInternet() {
                    @Override
                    public void onRetry() {
                        blockComment(message, id);
                    }
                });
            }
        }
    }

    @Override
    public void onSuccess(Response response) {
        comment = (ModelComment) response;
        final int size = comment.getCommentList().size();
        if (size > 0) {
            if (lastId == 0) {
                commentList = comment.getCommentList();
            } else {
                commentList.addAll(comment.getCommentList());
            }
            commentViewAdapter.updateList(commentList);
            commentViewAdapter.setLoaded();

            lastId = commentList.get(commentList.size() - 1).getId();
            setNullView(false);
        } else {
            commentViewAdapter.updateList(commentList);
            if (commentList.size() == 0) {
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