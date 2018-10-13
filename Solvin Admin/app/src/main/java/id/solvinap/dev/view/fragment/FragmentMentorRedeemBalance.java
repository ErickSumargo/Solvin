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
import id.solvinap.dev.server.data.DataAuth;
import id.solvinap.dev.server.data.DataRedeemBalance;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelRedeemBalance;
import id.solvinap.dev.view.activity.ActivityMentorDetail;
import id.solvinap.dev.view.adapter.RedeemBalanceViewAdapter;
import id.solvinap.dev.view.helper.Tool;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Erick Sumargo on 2/3/2017.
 */

public class FragmentMentorRedeemBalance extends Fragment implements IBaseResponse {
    //    VIEW
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView redeemBalanceView;

    private View nullView;
    private ImageView nullViewImage;
    private TextView nullViewTitle, nullViewDesc;

    //    HELPER
    private LinearLayoutManager layoutManager;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    OBJECT
    private ModelRedeemBalance redeemBalance;
    private List<DataRedeemBalance> redeemBalanceList;
    private RedeemBalanceViewAdapter redeemBalanceViewAdapter;

    private DataAuth dataAuth;

    //    VARIABLE
    public int lastId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_mentor_redeem_balance, container, false);

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

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.mentor_redeem_balance_swipe_refresh);
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
        nullViewDesc.setText(getResources().getString(R.string.text_no_history_redeem_balance));

        redeemBalanceView = (RecyclerView) view.findViewById(R.id.redeem_balance_view);
        redeemBalanceList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        redeemBalanceView.setLayoutManager(layoutManager);

        redeemBalanceViewAdapter = new RedeemBalanceViewAdapter(redeemBalanceView, redeemBalanceList);
        redeemBalanceView.setAdapter(redeemBalanceViewAdapter);

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getContext()).getiAPIRequest();
        iBaseResponse = this;

        //    OBJECT
        dataAuth = ((ActivityMentorDetail) getActivity()).dataAuth;
    }

    private void setEvent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetRecyclerViewData();
            }
        });

        redeemBalanceViewAdapter.setOnLoadMoreListener(new RedeemBalanceViewAdapter.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                fetchPageData();
            }
        });
    }

    public void resetRecyclerViewData() {
        lastId = 0;
        redeemBalanceViewAdapter.lastAnimatedPosition = -1;
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
            iAPIRequest.loadRedeemBalanceList(String.valueOf(lastId), String.valueOf(dataAuth.getId())).enqueue(new Callback<ModelRedeemBalance>() {
                @Override
                public void onResponse(Call<ModelRedeemBalance> call, retrofit2.Response<ModelRedeemBalance> response) {
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
                public void onFailure(Call<ModelRedeemBalance> call, Throwable t) {
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

    @Override
    public void onSuccess(Response response) {
        if (response instanceof ModelRedeemBalance) {
            redeemBalance = (ModelRedeemBalance) response;
            final int size = redeemBalance.getRedeemBalanceList().size();
            if (size > 0) {
                if (lastId == 0) {
                    redeemBalanceList = redeemBalance.getRedeemBalanceList();
                } else {
                    redeemBalanceList.addAll(redeemBalance.getRedeemBalanceList());
                }
                redeemBalanceViewAdapter.updateList(redeemBalanceList);
                redeemBalanceViewAdapter.setLoaded();

                lastId = redeemBalanceList.get(redeemBalanceList.size() - 1).getId();
                setNullView(false);
            } else {
                redeemBalanceViewAdapter.updateList(redeemBalanceList);
                if (redeemBalanceList.size() == 0) {
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