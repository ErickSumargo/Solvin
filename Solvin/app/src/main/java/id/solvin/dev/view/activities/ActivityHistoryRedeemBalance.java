package id.solvin.dev.view.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.model.basic.RedeemBalance;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.response.ResponseRedeemBalance;
import id.solvin.dev.presenter.TransactionPresenter;
import id.solvin.dev.view.adapters.HistoryRedeemBalanceViewAdapter;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 1/19/2017.
 */

public class ActivityHistoryRedeemBalance extends AppCompatActivity implements IBaseResponse {
    //    INTERFACE
    public interface INoInternet{
        void onRetry();
    }

    //    VIEW
    private Toolbar toolbar;
    private CoordinatorLayout mainLayout;
    private RecyclerView historyRedeemBalanceView;
    private LinearLayout loadingView, nullView;
    private TextView nullTitle, nullDesc;
    private Toast toast;
    private Snackbar snackbar;

    //    HELPER
    private LinearLayoutManager layoutManager;
    private ClassApplicationTool applicationTool;

    //    OBJECT
    private TransactionPresenter transactionPresenter;
    private ResponseRedeemBalance responseRedeemBalance;

    private List<RedeemBalance> historyRedeemBalanceList;
    private HistoryRedeemBalanceViewAdapter historyRedeemBalanceViewAdapter;

    //    LOCAL VARIABLE
    private int lastId = 0;
    private SpannableStringBuilder titleStyled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_redeem_balance);

        init();
        setEvent();
        fetchPageData();
    }

    private void init() {
        //    VIEW
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = (CoordinatorLayout) findViewById(R.id.history_redeem_balance_main_layout);
        historyRedeemBalanceView = (RecyclerView) findViewById(R.id.history_redeem_balance_view);
        historyRedeemBalanceList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        historyRedeemBalanceView.setLayoutManager(layoutManager);

        historyRedeemBalanceViewAdapter = new HistoryRedeemBalanceViewAdapter(historyRedeemBalanceView, historyRedeemBalanceList);
        historyRedeemBalanceView.setAdapter(historyRedeemBalanceViewAdapter);

        loadingView = (LinearLayout) findViewById(R.id.history_redeem_balance_loading_view);

        nullView = (LinearLayout) findViewById(R.id.history_redeem_balance_null_view);
        nullTitle = (TextView) findViewById(R.id.history_redeem_balance_null_title);
        nullDesc = (TextView) findViewById(R.id.history_redeem_balance_null_desc);

        nullView.setVisibility(View.GONE);

        //    HELPER
        applicationTool = new ClassApplicationTool(getApplicationContext());

        //    OBJECT
        transactionPresenter = new TransactionPresenter(this);
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        historyRedeemBalanceViewAdapter.setOnLoadMoreListener(new HistoryRedeemBalanceViewAdapter.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                fetchPageData();
            }
        });
    }

    private void setResultView() {
        loadingView.setVisibility(View.GONE);

        if (historyRedeemBalanceList.size() == 0) {
            historyRedeemBalanceView.setVisibility(View.GONE);
            nullView.setVisibility(View.VISIBLE);

            titleStyled = new SpannableStringBuilder("Belum Ada Histori Tebusan");
            titleStyled.setSpan(new StyleSpan(Typeface.BOLD), 0, titleStyled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            nullTitle.setText(titleStyled);

            titleStyled = new SpannableStringBuilder("Anda dapat menebus saldo yang dimiliki melalui menu Informasi Saldo");
            titleStyled.setSpan(new StyleSpan(Typeface.BOLD), titleStyled.length() - 15, titleStyled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            nullDesc.setText(titleStyled);
        } else {
            historyRedeemBalanceView.setVisibility(View.VISIBLE);
        }
    }

    private void fetchPageData() {
        if (Connectivity.isConnected(getApplicationContext())) {
            transactionPresenter.getRedeemBalanceHistory(lastId, getApplicationContext());
        } else {
            showNotificationNetwork(new INoInternet() {
                @Override
                public void onRetry() {
                    fetchPageData();
                }
            });
        }
    }

    private void showNotificationNetwork(final INoInternet iNoInternet) {
        snackbar = Snackbar.make(mainLayout, getResources().getString(R.string.text_no_internet), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Coba Lagi", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNoInternet.onRetry();
            }
        });
        applicationTool.resizeSnackBarWithCallBack(snackbar);
        snackbar.show();
    }

    @Override
    public void onSuccess(Response response) {
        responseRedeemBalance = (ResponseRedeemBalance) response;
        final int size = responseRedeemBalance.getData().getRedeemBalance().size();
        if (size > 0) {
            if (lastId == 0) {
                historyRedeemBalanceList = responseRedeemBalance.getData().getRedeemBalance();
            } else {
                historyRedeemBalanceList.addAll(responseRedeemBalance.getData().getRedeemBalance());
            }
            historyRedeemBalanceViewAdapter.updateList(historyRedeemBalanceList);
            historyRedeemBalanceViewAdapter.setLoaded();

            lastId = historyRedeemBalanceList.get(historyRedeemBalanceList.size() - 1).getId();
        } else {
            historyRedeemBalanceViewAdapter.updateList(historyRedeemBalanceList);
        }
        setResultView();
    }

    @Override
    public void onFailure(String message) {
        toast = Toast.makeText(ActivityHistoryRedeemBalance.this, message, Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();

        loadingView.setVisibility(View.GONE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}