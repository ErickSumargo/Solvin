package id.solvin.dev.view.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
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
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.Transaction;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.response.ResponseTransactions;
import id.solvin.dev.presenter.TransactionPresenter;
import id.solvin.dev.view.adapters.HistoryTransactionViewAdapter;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 10/28/2016.
 */

public class ActivityHistoryTransaction extends AppCompatActivity implements IBaseResponse {
    //    INTERFACE
    public interface INoInternet{
        void onRetry();
    }

    //    VIEW
    private Toolbar toolbar;
    private CoordinatorLayout mainLayout;
    private RecyclerView historyTransactionView;
    private LinearLayout loadingView, nullView;
    private TextView nullTitle;
    private Toast toast;
    private Snackbar snackbar;

    //    HELPER
    private LinearLayoutManager layoutManager;
    private ClassApplicationTool applicationTool;

    //    OBJECT
    private TransactionPresenter transactionPresenter;
    private ResponseTransactions responseTransactions;

    private List<Transaction> historyTransactionList;
    private HistoryTransactionViewAdapter historyTransactionViewAdapter;

    //    LOCAL VARIABLE
    private int lastId = 0;
    private SpannableStringBuilder titleStyled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_transaction);

        init();
        setEvent();
        fetchPageData();
    }

    private void init() {
        //    VIEW
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = (CoordinatorLayout) findViewById(R.id.history_transaction_main_layout);
        historyTransactionView = (RecyclerView) findViewById(R.id.history_transaction_view);
        historyTransactionList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        historyTransactionView.setLayoutManager(layoutManager);

        historyTransactionViewAdapter = new HistoryTransactionViewAdapter(historyTransactionView, historyTransactionList);
        historyTransactionView.setAdapter(historyTransactionViewAdapter);

        loadingView = (LinearLayout) findViewById(R.id.history_transaction_loading_view);

        nullView = (LinearLayout) findViewById(R.id.history_transaction_null_view);
        nullTitle = (TextView) findViewById(R.id.history_transaction_null_title);

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

        historyTransactionViewAdapter.setOnLoadMoreListener(new HistoryTransactionViewAdapter.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                fetchPageData();
            }
        });
    }

    private void setResultView() {
        loadingView.setVisibility(View.GONE);

        if (historyTransactionList.size() == 0) {
            historyTransactionView.setVisibility(View.GONE);
            nullView.setVisibility(View.VISIBLE);

            titleStyled = new SpannableStringBuilder("Belum Ada Histori Transaksi");
            titleStyled.setSpan(new StyleSpan(Typeface.BOLD), 0, titleStyled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            nullTitle.setText(titleStyled);
        } else {
            historyTransactionView.setVisibility(View.VISIBLE);
            nullView.setVisibility(View.GONE);
        }
    }

    private void fetchPageData() {
        if (Connectivity.isConnected(getApplicationContext())) {
            transactionPresenter.getTransactionHistory(lastId, getApplicationContext());
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
        responseTransactions = (ResponseTransactions) response;
        final int size = responseTransactions.getDataTransaction().getTransactions().size();
        if (size > 0) {
            if (lastId == 0) {
                historyTransactionList = responseTransactions.getDataTransaction().getTransactions();
            } else {
                historyTransactionList.addAll(responseTransactions.getDataTransaction().getTransactions());
            }
            historyTransactionViewAdapter.updateList(historyTransactionList);
            historyTransactionViewAdapter.setLoaded();

            lastId = historyTransactionList.get(historyTransactionList.size() - 1).getId();
        } else {
            historyTransactionViewAdapter.updateList(historyTransactionList);
        }
        setResultView();
    }

    @Override
    public void onFailure(String message) {
        toast = Toast.makeText(ActivityHistoryTransaction.this, message, Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();

        loadingView.setVisibility(View.GONE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}