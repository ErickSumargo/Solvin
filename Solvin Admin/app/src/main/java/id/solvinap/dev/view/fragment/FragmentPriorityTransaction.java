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
import id.solvinap.dev.server.data.DataTransaction;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelTransaction;
import id.solvinap.dev.view.activity.MainActivity;
import id.solvinap.dev.view.adapter.PriorityTransactionViewAdapter;
import id.solvinap.dev.view.helper.Tool;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Erick Sumargo on 2/20/2017.
 */

public class FragmentPriorityTransaction extends Fragment implements IBaseResponse {
    //    VIEW
    private View view;
    private CoordinatorLayout mainLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView priorityTransactionView;
    private Snackbar snackbar;

    private View nullView;
    private ImageView nullViewImage;
    private TextView nullViewTitle, nullViewDesc;

    //    HELPER
    private LinearLayoutManager layoutManager;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    OBJECT
    private ModelTransaction transaction;
    private List<DataTransaction> transactionList;
    private PriorityTransactionViewAdapter priorityTransactionViewAdapter;

    //    VARIABLE
    public int lastId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_priority_transaction, container, false);

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

        mainLayout = (CoordinatorLayout) view.findViewById(R.id.priority_transaction_main_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.priority_transaction_swipe_refresh);
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
        nullViewDesc.setText(getResources().getString(R.string.text_no_priority_transaction));

        priorityTransactionView = (RecyclerView) view.findViewById(R.id.priority_transaction_view);
        transactionList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        priorityTransactionView.setLayoutManager(layoutManager);

        priorityTransactionViewAdapter = new PriorityTransactionViewAdapter(priorityTransactionView, transactionList);
        priorityTransactionView.setAdapter(priorityTransactionViewAdapter);

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

        priorityTransactionViewAdapter.setOnLoadMoreListener(new PriorityTransactionViewAdapter.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                fetchPageData();
            }
        });

        priorityTransactionViewAdapter.setOnActionListener(new PriorityTransactionViewAdapter.OnActionListener() {
            @Override
            public void OnAction(String message, int id, int action) {
                setAction(message, id, action);
            }
        });
    }

    public void resetRecyclerViewData() {
        lastId = 0;
        priorityTransactionViewAdapter.lastAnimatedPosition = -1;
        fetchPageData();
    }

    private void setNullView(boolean visible) {
        if (visible) {
            nullView.setVisibility(View.VISIBLE);
        } else {
            nullView.setVisibility(View.GONE);
        }
    }

    private void showActionExecutedNotification(final int action) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (action == 0) {
                    snackbar = Snackbar.make(mainLayout, "Permohonan transaksi berhasil ditolak", Snackbar.LENGTH_LONG);
                    Tool.getInstance(getContext()).resizeSnackBar(snackbar, 2);
                } else if (action == 1) {
                    snackbar = Snackbar.make(mainLayout, "Permohonan transaksi berhasil disetujui", Snackbar.LENGTH_LONG);
                    Tool.getInstance(getContext()).resizeSnackBar(snackbar, 1);
                }
                snackbar.show();
            }
        }, 500);
    }

    public void fetchPageData() {
        if (lastId == 0) {
            swipeRefreshLayout.setRefreshing(true);
        }
        if (Connectivity.isConnected(getContext())) {
            iAPIRequest.loadPriorityTransactionList(String.valueOf(lastId)).enqueue(new Callback<ModelTransaction>() {
                @Override
                public void onResponse(Call<ModelTransaction> call, retrofit2.Response<ModelTransaction> response) {
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
                public void onFailure(Call<ModelTransaction> call, Throwable t) {
                    if (lastId == 0) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    iBaseResponse.onFailure(t.getMessage());
                    if(t instanceof SocketTimeoutException){
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

    private void setAction(final String message, final int id, final int action) {
        priorityTransactionViewAdapter.showCustomProgressDialog(message);
        if (Connectivity.isConnected(getContext())) {
            iAPIRequest.setTransactionAction(String.valueOf(id), String.valueOf(action)).enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    priorityTransactionViewAdapter.customProgressDialog.dismiss();
                    if (response.code() == 200) {
                        showActionExecutedNotification(action);

                        lastId = 0;
                        transactionList.clear();
                        fetchPageData();
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {
                    priorityTransactionViewAdapter.customProgressDialog.dismiss();

                    iBaseResponse.onFailure(t.getMessage());
                }
            });
        } else {
            priorityTransactionViewAdapter.customProgressDialog.dismiss();
            ((MainActivity) getActivity()).showNoInternetNotification(new MainActivity.INoInternet() {
                @Override
                public void onRetry() {
                    setAction(message, id, action);
                }
            });
        }
    }

    @Override
    public void onSuccess(Response response) {
        transaction = (ModelTransaction) response;
        final int size = transaction.getTransactionList().size();
        if (size > 0) {
            if (lastId == 0) {
                transactionList = transaction.getTransactionList();
            } else {
                transactionList.addAll(transaction.getTransactionList());
            }
            priorityTransactionViewAdapter.updateList(transactionList);
            priorityTransactionViewAdapter.setLoaded();

            lastId = transactionList.get(transactionList.size() - 1).getId();
            setNullView(false);
        } else {
            priorityTransactionViewAdapter.updateList(transactionList);
            if (transactionList.size() == 0) {
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