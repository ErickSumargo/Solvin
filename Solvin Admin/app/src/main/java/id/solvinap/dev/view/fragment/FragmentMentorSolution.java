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
import id.solvinap.dev.server.data.DataAuth;
import id.solvinap.dev.server.data.DataSolution;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelSolution;
import id.solvinap.dev.view.activity.ActivityMentorDetail;
import id.solvinap.dev.view.activity.ActivitySolutionEditingSheet;
import id.solvinap.dev.view.adapter.SolutionViewAdapter;
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
 * Created by Erick Sumargo on 2/3/2017.
 */

public class FragmentMentorSolution extends Fragment implements IBaseResponse {
    //    VIEW
    private View view;
    private CoordinatorLayout mainLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView solutionView;
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
    private ActivitySolutionEditingSheet solutionEditingSheet;

    private ModelSolution solution;
    private List<DataSolution> solutionList;
    private SolutionViewAdapter solutionViewAdapter;

    private DataAuth dataAuth;

    //    VARIABLE
    public int lastId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_mentor_solution, container, false);

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

        mainLayout = (CoordinatorLayout) view.findViewById(R.id.mentor_solution_main_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.mentor_solution_swipe_refresh);
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
        nullViewDesc.setText(getResources().getString(R.string.text_no_solution));

        solutionView = (RecyclerView) view.findViewById(R.id.solution_view);
        solutionList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        solutionView.setLayoutManager(layoutManager);

        solutionViewAdapter = new SolutionViewAdapter(solutionView, solutionList, tempRT);
        solutionView.setAdapter(solutionViewAdapter);

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getContext()).getiAPIRequest();
        iBaseResponse = this;

        //    OBJECT
        solutionEditingSheet = new ActivitySolutionEditingSheet();

        dataAuth = ((ActivityMentorDetail) getActivity()).dataAuth;
    }

    private void setEvent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetRecyclerViewData();
            }
        });

        solutionViewAdapter.setOnLoadMoreListener(new SolutionViewAdapter.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                fetchPageData();
            }
        });

        solutionViewAdapter.setOnBlockSolutionListener(new SolutionViewAdapter.OnBlockSolutionListener() {
            @Override
            public void OnBlock(String message, int id) {
                blockSolution(message, id);
            }
        });

        solutionEditingSheet.setSolutionSent(new ActivitySolutionEditingSheet.OnSolutionSent() {
            @Override
            public void ISentConfirmedEditted() {
                showNotificationSolutionEditted();

                lastId = 0;
                fetchPageData();
            }
        });
    }

    public void resetRecyclerViewData() {
        lastId = 0;
        solutionViewAdapter.lastAnimatedPosition = -1;
        fetchPageData();
    }

    private void setNullView(boolean visible) {
        if (visible) {
            nullView.setVisibility(View.VISIBLE);
        } else {
            nullView.setVisibility(View.GONE);
        }
    }

    private void showNotificationSolutionEditted() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(mainLayout, "Solusi telah berhasil diedit", Snackbar.LENGTH_LONG);
                Tool.getInstance(getContext()).resizeSnackBar(snackbar, 1);
                snackbar.show();
            }
        }, 500);
    }

    private void showNotificationSolutionBlocked() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(mainLayout, "Solusi telah berhasil diblokir", Snackbar.LENGTH_LONG);
                Tool.getInstance(getContext()).resizeSnackBar(snackbar, 2);
                snackbar.show();
            }
        }, 500);
    }

    public void fetchPageData() {
        if (lastId == 0) {
            swipeRefreshLayout.setRefreshing(true);
        }
        if (Connectivity.isConnected(getContext())) {
            iAPIRequest.loadSolutionList(String.valueOf(lastId), String.valueOf(dataAuth.getId())).enqueue(new Callback<ModelSolution>() {
                @Override
                public void onResponse(Call<ModelSolution> call, retrofit2.Response<ModelSolution> response) {
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
                public void onFailure(Call<ModelSolution> call, Throwable t) {
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
            ((ActivityMentorDetail) getActivity()).showNoInternetNotification(new ActivityMentorDetail.INoInternet() {
                @Override
                public void onRetry() {
                    fetchPageData();
                }
            });
        }
    }

    private void blockSolution(final String message, final int id) {
        solutionViewAdapter.showCustomProgressDialog(message);
        if (Connectivity.isConnected(getContext())) {
            iAPIRequest.blockSolution(String.valueOf(id)).enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    solutionViewAdapter.customProgressDialog.dismiss();

                    if (response.code() == 200) {
                        showNotificationSolutionBlocked();

                        lastId = 0;
                        fetchPageData();
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {
                    solutionViewAdapter.customProgressDialog.dismiss();

                    iBaseResponse.onFailure(t.getMessage());
                }
            });
        } else {
            solutionViewAdapter.customProgressDialog.dismiss();
            ((ActivityMentorDetail) getActivity()).showNoInternetNotification(new ActivityMentorDetail.INoInternet() {
                @Override
                public void onRetry() {
                    blockSolution(message, id);
                }
            });
        }
    }

    @Override
    public void onSuccess(Response response) {
        solution = (ModelSolution) response;
        final int size = solution.getSolutionList().size();
        if (size > 0) {
            if (lastId == 0) {
                solutionList = solution.getSolutionList();
            } else {
                solutionList.addAll(solution.getSolutionList());
            }
            solutionViewAdapter.updateList(solutionList);
            solutionViewAdapter.setLoaded();

            lastId = solutionList.get(solutionList.size() - 1).getId();
            setNullView(false);
        } else {
            solutionViewAdapter.updateList(solutionList);
            if (solutionList.size() == 0) {
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