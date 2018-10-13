package id.solvinap.dev.view.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import id.solvinap.dev.R;
import id.solvinap.dev.server.api.Connection;
import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataCumulativeRecord;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.server.helper.Session;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelCumulativeRecord;
import id.solvinap.dev.view.fragment.FragmentFeedback;
import id.solvinap.dev.view.fragment.FragmentHome;
import id.solvinap.dev.view.fragment.FragmentPriority;
import id.solvinap.dev.view.fragment.FragmentReport;
import id.solvinap.dev.view.helper.Tool;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IBaseResponse {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    //    VIEW
    private Toolbar toolbar;
    private CoordinatorLayout mainLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private DrawerLayout.LayoutParams layoutParams;

    private NavigationView navigationView;
    private View navigationHeader;
    private TextView student, mentor, income, balanceRedeemed, profit;

    private Menu menu;
    private MenuItem menuItem;
    private Toast toast;
    private Snackbar snackbar;

    //    HELPER
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private DisplayMetrics displayMetrics;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    OBJECT
    private ModelCumulativeRecord cumulativeRecord;
    private DataCumulativeRecord dataCumulativeRecord;

    private ActivityStudentDetail studentDetail;
    private ActivityMentorDetail mentorDetail;

    //    VARIABLE
    private final int TIME_INTERVAL = 2000;
    private long intervalBackPressed;

    private int totalStudent, totalMentor;
    private String incomeValue, balanceRedeemedValue, profitValue;
    private long totalIncome, totalBalanceRedeemed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setEvent();
        getFinanceRecord();
    }

    private void init() {
        //    VIEW
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainLayout = (CoordinatorLayout) findViewById(R.id.main_layout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(4).setCheckable(false);
        navigationView.setNavigationItemSelectedListener(this);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        layoutParams = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
        layoutParams.width = (int) (displayMetrics.widthPixels * 0.75f);
        navigationView.setLayoutParams(layoutParams);

        navigationHeader = navigationView.getHeaderView(0);
        student = (TextView) navigationHeader.findViewById(R.id.nav_total_student);
        mentor = (TextView) navigationHeader.findViewById(R.id.nav_total_mentor);
        income = (TextView) navigationHeader.findViewById(R.id.nav_income);
        balanceRedeemed = (TextView) navigationHeader.findViewById(R.id.nav_balance_redeemed);
        profit = (TextView) navigationHeader.findViewById(R.id.nav_profit);

        setFragmentPosition(0);

        //    HELPER
        sharedPreferences = getSharedPreferences(Global.PREFERENCES_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        totalStudent = sharedPreferences.getInt(Global.TOTAL_STUDENT, 0);
        totalMentor = sharedPreferences.getInt(Global.TOTAL_MENTOR, 0);

        totalIncome = sharedPreferences.getLong(Global.TOTAL_INCOME, 0);
        totalBalanceRedeemed = sharedPreferences.getLong(Global.TOTAL_BALANCE_REDEEMED, 0);

        student.setText(String.valueOf(totalStudent));
        mentor.setText(String.valueOf(totalMentor));

        incomeValue = Tool.getInstance(getApplicationContext()).convertRpCurrency(totalIncome);
        income.setText(incomeValue.substring(4, incomeValue.length()));
        balanceRedeemed.setText(Tool.getInstance(getApplicationContext()).convertRpCurrency(totalBalanceRedeemed));
        profit.setText(Tool.getInstance(getApplicationContext()).convertRpCurrency(totalIncome - totalBalanceRedeemed));

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getApplicationContext()).getiAPIRequest();
        iBaseResponse = this;

        //    OBJECT
        studentDetail = new ActivityStudentDetail();
        mentorDetail = new ActivityMentorDetail();
    }

    private void setEvent() {
        studentDetail.setOnReceiveNotificationListener(new ActivityStudentDetail.OnReceiveNotificationListener() {
            @Override
            public void OnReceiveNotification() {
                showAuthBlockedNotification();
            }
        });

        mentorDetail.setOnReceiveNotificationListener(new ActivityMentorDetail.OnReceiveNotificationListener() {
            @Override
            public void OnReceiveNotification() {
                showAuthBlockedNotification();
            }
        });
    }

    private void showAuthBlockedNotification() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(mainLayout, "Pengguna terkait berhasil diblokir", Snackbar.LENGTH_LONG);
                Tool.getInstance(getApplicationContext()).resizeSnackBar(snackbar, 2);
                snackbar.show();
            }
        }, 500);
    }

    @SuppressWarnings("RestrictedApi")
    public void setFragmentPosition(int position) {
        if (position == 0) {
            fragment = new FragmentHome();
            menu = navigationView.getMenu();
            menuItem = menu.findItem(R.id.nav_home);
            setFragmentView(R.id.nav_home, menuItem.getTitle());
        } else if (position == 1) {
            fragment = new FragmentPriority();
            menu = navigationView.getMenu();
            menuItem = menu.findItem(R.id.nav_priority);
            setFragmentView(R.id.nav_priority, menuItem.getTitle());
        } else if (position == 2) {
            fragment = new FragmentReport();
            menu = navigationView.getMenu();
            menuItem = menu.findItem(R.id.nav_report);
            setFragmentView(R.id.nav_report, menuItem.getTitle());
        } else if (position == 3) {
            fragment = new FragmentFeedback();
            menu = navigationView.getMenu();
            menuItem = menu.findItem(R.id.nav_feedback);
            setFragmentView(R.id.nav_feedback, menuItem.getTitle());
        }
    }

    @SuppressWarnings("RestrictedApi")
    private void setFragmentView(int id, CharSequence title) {
        if (fragment != null) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_main, fragment);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();

            navigationView.setCheckedItem(id);
            setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (!(fragment instanceof FragmentHome)) {
                setFragmentPosition(0);
            } else {
                if (intervalBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
                    toast = Toast.makeText(MainActivity.this, "Tekan tombol kembali lagi untuk keluar", Toast.LENGTH_SHORT);
                    Tool.getInstance(getApplicationContext()).resizeToast(toast);
                    toast.show();
                }
                intervalBackPressed = System.currentTimeMillis();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            if (!(fragment instanceof FragmentHome)) {
                setFragmentPosition(0);
            }
        } else if (id == R.id.nav_priority) {
            if (!(fragment instanceof FragmentPriority)) {
                setFragmentPosition(1);
            }
        } else if (id == R.id.nav_report) {
            if (!(fragment instanceof FragmentReport)) {
                setFragmentPosition(2);
            }
        } else if (id == R.id.nav_feedback) {
            if (!(fragment instanceof FragmentFeedback)) {
                setFragmentPosition(3);
            }
        } else if (id == R.id.nav_logout) {
            Session.getInstance(getApplicationContext()).clearLoginData();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showNoInternetNotification(final INoInternet iNoInternet) {
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

    public void getFinanceRecord() {
        if (Connectivity.isConnected(getApplicationContext())) {
            iAPIRequest.getFinanceRecord().enqueue(new Callback<ModelCumulativeRecord>() {
                @Override
                public void onResponse(Call<ModelCumulativeRecord> call, retrofit2.Response<ModelCumulativeRecord> response) {
                    if (response.code() == 200) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<ModelCumulativeRecord> call, Throwable t) {
                    iBaseResponse.onFailure(t.getMessage());
                    if (t instanceof SocketTimeoutException) {
                        getFinanceRecord();
                    }
                }
            });
        } else {
            showNoInternetNotification(new INoInternet() {
                @Override
                public void onRetry() {
                    getFinanceRecord();
                }
            });
        }
    }

    @Override
    public void onSuccess(Response response) {
        cumulativeRecord = (ModelCumulativeRecord) response;
        if (cumulativeRecord != null) {
            dataCumulativeRecord = cumulativeRecord.getDataCumulativeRecord();

            student.setText(String.valueOf(dataCumulativeRecord.getTotalStudent()));
            mentor.setText(String.valueOf(dataCumulativeRecord.getTotalMentor()));

            incomeValue = Tool.getInstance(getApplicationContext()).convertRpCurrency(dataCumulativeRecord.getTotalIncome());
            balanceRedeemedValue = Tool.getInstance(getApplicationContext()).convertRpCurrency(dataCumulativeRecord.getTotalBalanceRedeemed());
            profitValue = Tool.getInstance(getApplicationContext()).convertRpCurrency(dataCumulativeRecord.getTotalIncome() - dataCumulativeRecord.getTotalBalanceRedeemed());

            income.setText(incomeValue.substring(4, incomeValue.length()));
            balanceRedeemed.setText(balanceRedeemedValue.substring(4, balanceRedeemedValue.length()));
            profit.setText(profitValue.substring(4, profitValue.length()));

            editor.putInt(Global.TOTAL_STUDENT, dataCumulativeRecord.getTotalStudent());
            editor.putInt(Global.TOTAL_MENTOR, dataCumulativeRecord.getTotalMentor());

            editor.putLong(Global.TOTAL_INCOME, dataCumulativeRecord.getTotalIncome());
            editor.putLong(Global.TOTAL_BALANCE_REDEEMED, dataCumulativeRecord.getTotalBalanceRedeemed());
            editor.commit();
        }
    }

    @Override
    public void onFailure(String message) {
        final Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
        Tool.getInstance(getApplicationContext()).resizeToast(toast);
        toast.show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}