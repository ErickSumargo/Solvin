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
import id.solvin.dev.model.basic.Notification;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.response.ResponseNotification;
import id.solvin.dev.presenter.NotificationPresenter;
import id.solvin.dev.view.adapters.NotificationViewAdapter;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 9/1/2016.
 */
public class ActivityNotification extends AppCompatActivity implements IBaseResponse {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    //    VIEW
    private Toolbar toolbar;
    private CoordinatorLayout mainLayout;
    private RecyclerView notificationView;

    private LinearLayout loadingView, nullView;
    private TextView nullTitle;
    private Toast toast;
    private Snackbar snackbar;

    //    HELPER
    private LinearLayoutManager layoutManager;
    private ClassApplicationTool applicationTool;

    //    OBJECT
    private List<Notification> notificationList;
    private NotificationViewAdapter notificationViewAdapter;

    //    LOCAL VARIABLE
    private SpannableStringBuilder titleStyled;
    private NotificationPresenter presenter;

    private int lastId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        init();
        setEvent();
        getNotification();
    }

    private void init() {
        presenter = new NotificationPresenter(this);
        //    VIEW
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = (CoordinatorLayout) findViewById(R.id.notification_main_layout);
        notificationView = (RecyclerView) findViewById(R.id.notification_view);
        notificationList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getApplicationContext());
        notificationView.setLayoutManager(layoutManager);

        notificationViewAdapter = new NotificationViewAdapter(notificationView, notificationList, this);
        notificationView.setAdapter(notificationViewAdapter);

        loadingView = (LinearLayout) findViewById(R.id.notification_loading_view);
        nullView = (LinearLayout) findViewById(R.id.notification_null_view);
        nullTitle = (TextView) findViewById(R.id.notification_null_title);
        nullView.setVisibility(View.GONE);

        //    HELPER
        applicationTool = new ClassApplicationTool(getApplicationContext());
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        notificationViewAdapter.setOnLoadMoreListener(new NotificationViewAdapter.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                getNotification();
            }
        });
    }

    private void initNotificationNullView() {
        if (notificationList.size() == 0) {
            notificationView.setVisibility(View.GONE);
            nullView.setVisibility(View.VISIBLE);

            titleStyled = new SpannableStringBuilder("Belum Ada Daftar Notifikasi");
            titleStyled.setSpan(new StyleSpan(Typeface.BOLD), 0, titleStyled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            nullTitle.setText(titleStyled);
        } else {
            notificationView.setVisibility(View.VISIBLE);
            nullView.setVisibility(View.GONE);
        }
    }

    private void getNotification() {
        if (Connectivity.isConnected(getApplicationContext())) {
            presenter.getNotification(lastId, getApplicationContext());
        } else {
            showNotificationNetwork(new INoInternet() {
                @Override
                public void onRetry() {
                    getNotification();
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
        loadingView.setVisibility(View.GONE);

        ResponseNotification responseNotification = (ResponseNotification) response;
        if (responseNotification.getData().getNotifications().size() > 0) {
            if (lastId == 0) {
                notificationList = responseNotification.getData().getNotifications();
            } else {
                notificationList.addAll(responseNotification.getData().getNotifications());
            }
            notificationViewAdapter.updateList(notificationList);
            notificationViewAdapter.setLoaded();

            lastId = notificationList.get(notificationList.size() - 1).getId();
        } else {
            notificationViewAdapter.updateList(notificationList);
        }
        initNotificationNullView();
    }

    @Override
    public void onFailure(String message) {
        toast = Toast.makeText(ActivityNotification.this, message, Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}