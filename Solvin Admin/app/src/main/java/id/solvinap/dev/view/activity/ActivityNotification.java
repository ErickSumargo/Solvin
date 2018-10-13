package id.solvinap.dev.view.activity;

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

import id.solvinap.dev.R;
import id.solvinap.dev.server.api.Connection;
import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataNotification;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelNotification;
import id.solvinap.dev.view.adapter.NotificationViewAdapter;
import id.solvinap.dev.view.helper.Tool;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 1/31/2017.
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
    private LinearLayout nullView;
    private TextView nullTitle;
    private View loadingView;

    //    HELPER
    private LinearLayoutManager layoutManager;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    OBJECT
    private ModelNotification notification;
    private List<DataNotification> notificationList;
    private NotificationViewAdapter notificationViewAdapter;

    //    VARIABLE
    private int lastId = 0;
    private SpannableStringBuilder titleStyled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        init();
        setEvent();
        fetchPageData();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = (CoordinatorLayout) findViewById(R.id.notification_main_layout);
        notificationView = (RecyclerView) findViewById(R.id.notification_view);
        layoutManager = new LinearLayoutManager(this);
        notificationView.setLayoutManager(layoutManager);

        notificationList = new ArrayList<>();
        notificationViewAdapter = new NotificationViewAdapter(notificationView, notificationList);
        notificationView.setAdapter(notificationViewAdapter);

        loadingView = findViewById(R.id.loading_view_container);
        nullView = (LinearLayout) findViewById(R.id.notification_null_view);
        nullTitle = (TextView) findViewById(R.id.notification_null_title);

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getApplicationContext()).getiAPIRequest();
        iBaseResponse = this;
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
                fetchPageData();
            }
        });

        notificationViewAdapter.setOnReadStatusListener(new NotificationViewAdapter.OnSetReadStatusListener() {
            @Override
            public void OnSetReadStatus(int id) {
                setReadStatus(id);
            }
        });
    }

    private void setNotificationView() {
        if (notificationList.size() == 0) {
            notificationView.setVisibility(View.GONE);
            nullView.setVisibility(View.VISIBLE);

            titleStyled = new SpannableStringBuilder("Belum ada notifikasi yang masuk");
            titleStyled.setSpan(new StyleSpan(Typeface.BOLD), 0, titleStyled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            nullTitle.setText(titleStyled);
        } else {
            notificationView.setVisibility(View.VISIBLE);
            nullView.setVisibility(View.GONE);
        }
        loadingView.setVisibility(View.GONE);
    }

    private void showNoInternetNotification(final INoInternet iNoInternet) {
        final Snackbar snackbar = Snackbar.make(mainLayout, getResources().getString(R.string.text_no_internet), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Coba Lagi", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNoInternet.onRetry();
            }
        });
        Tool.getInstance(getApplicationContext()).resizeSnackBarWithCallBack(snackbar);
        snackbar.show();
    }

    private void fetchPageData() {
        if (Connectivity.isConnected(getApplicationContext())) {
            iAPIRequest.loadNotificationList(lastId).enqueue(new Callback<ModelNotification>() {
                @Override
                public void onResponse(Call<ModelNotification> call, retrofit2.Response<ModelNotification> response) {
                    if (response.code() == 200) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<ModelNotification> call, Throwable t) {
                    iBaseResponse.onFailure(t.getMessage());
                    if (t instanceof SocketTimeoutException) {
                        fetchPageData();
                    }
                }
            });
        } else {
            showNoInternetNotification(new INoInternet() {
                @Override
                public void onRetry() {
                    fetchPageData();
                }
            });
        }
    }

    private void setReadStatus(final int id) {
        iAPIRequest.setNotificationReadStatus(id).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.code() == 200) {
                    iBaseResponse.onSuccess(response.body());
                } else {
                    iBaseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                iBaseResponse.onFailure(t.getMessage());
                if (t instanceof SocketTimeoutException) {
                    setReadStatus(id);
                }
            }
        });
    }

    @Override
    public void onSuccess(Response response) {
        if (response instanceof ModelNotification) {
            notification = (ModelNotification) response;
            final int size = notification.getData().getNotificationList().size();
            if (size > 0) {
                if (lastId == 0) {
                    notificationList = notification.getData().getNotificationList();
                } else {
                    notificationList.addAll(notification.getData().getNotificationList());
                }
                notificationViewAdapter.updateList(notificationList);
                notificationViewAdapter.setLoaded();

                lastId = notificationList.get(notificationList.size() - 1).getId();
            } else {
                notificationViewAdapter.updateList(notificationList);
            }
            setNotificationView();
        }
    }

    @Override
    public void onFailure(String message) {
        final Toast toast = Toast.makeText(ActivityNotification.this, message, Toast.LENGTH_SHORT);
        Tool.getInstance(getApplicationContext()).resizeToast(toast);
        toast.show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}