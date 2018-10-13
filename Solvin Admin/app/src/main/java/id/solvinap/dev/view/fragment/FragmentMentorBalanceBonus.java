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

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import id.solvinap.dev.R;
import id.solvinap.dev.server.api.Connection;
import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataAuth;
import id.solvinap.dev.server.data.DataBalanceBonus;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelBalanceBonus;
import id.solvinap.dev.view.activity.ActivityMentorDetail;
import id.solvinap.dev.view.adapter.BalanceBonusViewAdapter;
import id.solvinap.dev.view.helper.Tool;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Erick Sumargo on 4/11/2017.
 */

public class FragmentMentorBalanceBonus extends Fragment implements IBaseResponse {
    //    VIEW
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView balanceBonusView;

    private View nullView;
    private ImageView nullViewImage;
    private TextView nullViewTitle, nullViewDesc;

    //    HELPER
    private LinearLayoutManager layoutManager;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    OBJECT
    private ModelBalanceBonus balanceBonus;
    private List<DataBalanceBonus> balanceBonusList;
    private BalanceBonusViewAdapter balanceBonusViewAdapter;

    private DataAuth dataAuth;

    //    VARIABLE
    public int lastId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_mentor_balance_bonus, container, false);

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

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.mentor_balance_bonus_swipe_refresh);
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
        nullViewDesc.setText(getResources().getString(R.string.text_no_history_balance_bonus));

        balanceBonusView = (RecyclerView) view.findViewById(R.id.balance_bonus_view);
        balanceBonusList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        balanceBonusView.setLayoutManager(layoutManager);

        balanceBonusViewAdapter = new BalanceBonusViewAdapter(balanceBonusView, balanceBonusList);
        balanceBonusView.setAdapter(balanceBonusViewAdapter);

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

        balanceBonusViewAdapter.setOnLoadMoreListener(new BalanceBonusViewAdapter.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                fetchPageData();
            }
        });
    }

    public void resetRecyclerViewData() {
        lastId = 0;
        balanceBonusViewAdapter.lastAnimatedPosition = -1;
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
            iAPIRequest.loadBalanceBonusList(String.valueOf(lastId), String.valueOf(dataAuth.getId())).enqueue(new Callback<ModelBalanceBonus>() {
                @Override
                public void onResponse(Call<ModelBalanceBonus> call, retrofit2.Response<ModelBalanceBonus> response) {
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
                public void onFailure(Call<ModelBalanceBonus> call, Throwable t) {
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
        if (response instanceof ModelBalanceBonus) {
            balanceBonus = (ModelBalanceBonus) response;
            final int size = balanceBonus.getBalanceBonusList().size();
            if (size > 0) {
                if (lastId == 0) {
                    balanceBonusList = balanceBonus.getBalanceBonusList();
                } else {
                    balanceBonusList.addAll(balanceBonus.getBalanceBonusList());
                }
                balanceBonusViewAdapter.updateList(balanceBonusList);
                balanceBonusViewAdapter.setLoaded();

                lastId = balanceBonusList.get(balanceBonusList.size() - 1).getId();
                setNullView(false);
            } else {
                balanceBonusViewAdapter.updateList(balanceBonusList);
                if (balanceBonusList.size() == 0) {
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