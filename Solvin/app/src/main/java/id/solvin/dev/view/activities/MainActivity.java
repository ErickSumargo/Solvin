package id.solvin.dev.view.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.ConfigApp;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.Mentor;
import id.solvin.dev.model.basic.ProfileBus;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.Student;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.response.ResponseAuth;
import id.solvin.dev.presenter.AuthPresenter;
import id.solvin.dev.view.fragments.FragmentBalanceInfo;
import id.solvin.dev.view.fragments.FragmentFAQ;
import id.solvin.dev.view.fragments.FragmentFeedback;
import id.solvin.dev.view.fragments.FragmentHome;
import id.solvin.dev.view.fragments.FragmentProfile;
import id.solvin.dev.view.fragments.FragmentQuestionSheet;
import id.solvin.dev.view.fragments.FragmentTransaction;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,IBaseResponse {
    private View navigationHeader;
    private CoordinatorLayout rootLayout;
    private RelativeLayout avatarLayout;
    private LinearLayout userCreditStatusContainer, userDateActivationContainer, mentorRecordContainer;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private AppBarLayout appBarLayout;
    public Toolbar toolbar;
    private DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Menu navMenu, menu;
    private MenuItem menuItem;
    private CircleImageView userPhoto;
    private TextView avatarInitial, userName, membershipCode, userCredit, userDateActivation,
            mentorBalanceAmount, mentorTotalSolution;
    private Snackbar snackbar;
    private Toast toast;

    public FloatingActionButton createQuestion;
    private FragmentQuestionSheet questionSheet;
    public ClassApplicationTool applicationTool;

    private Intent intent;
    private Configuration configuration;
    private DisplayMetrics displayMetrics;
    private InputMethodManager inputMethodManager;
    private GradientDrawable gradientDrawable;

    private DrawerLayout.LayoutParams layoutParams;

    private int avatarColor;
    private final int TIME_INTERVAL = 2000;
    private long intervalBackPressed;
    private AuthPresenter authPresenter;
    private Session session;
    private Auth auth;
    private ClassRaterDialog raterDialog;
    private Bundle data = null;

    public Student student;
    public Mentor mentor;
    public boolean isMyProfile = true;

    private ActivityTransactionStatus activityTransactionStatus;

    public interface INoInternet {
        void onRetry();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setEvent();
    }

    @SuppressWarnings("RestrictedApi")
    private void init() {
        authPresenter = new AuthPresenter(this);
        session = Session.with(getApplicationContext());

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_light);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationHeader = navigationView.getHeaderView(0);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (getResources().getBoolean(R.bool.isTablet)) {
            actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu_light);
        }
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        layoutParams = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
        layoutParams.width = (int) (displayMetrics.widthPixels * 0.75f);
        navigationView.setLayoutParams(layoutParams);

        rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
        avatarLayout = (RelativeLayout) navigationHeader.findViewById(R.id.nav_avatar_layout);
        userCreditStatusContainer = (LinearLayout) navigationHeader.findViewById(R.id.nav_user_credit_status_container);
        userDateActivationContainer = (LinearLayout) navigationHeader.findViewById(R.id.nav_user_date_activation_container);
        mentorRecordContainer = (LinearLayout) navigationHeader.findViewById(R.id.nav_mentor_record_container);
        userPhoto = (CircleImageView) navigationHeader.findViewById(R.id.nav_user_photo);
        avatarInitial = (TextView) navigationHeader.findViewById(R.id.nav_avatar_initial);
        userName = (TextView) navigationHeader.findViewById(R.id.nav_user_name);
        membershipCode = (TextView) navigationHeader.findViewById(R.id.nav_membership_code);
        userCredit = (TextView) navigationHeader.findViewById(R.id.nav_user_credit);
        userDateActivation = (TextView) navigationHeader.findViewById(R.id.nav_user_date_activation);
        mentorBalanceAmount = (TextView) navigationHeader.findViewById(R.id.nav_mentor_balance_amount);
        mentorTotalSolution = (TextView) navigationHeader.findViewById(R.id.nav_mentor_total_solution);
        createQuestion = (FloatingActionButton) findViewById(R.id.create_question);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        questionSheet = new FragmentQuestionSheet();
        applicationTool = new ClassApplicationTool(getApplicationContext());
        activityTransactionStatus = new ActivityTransactionStatus();

        setFragmentPosition(0);
        refreshUser();

        tryGetProfile();
    }

    private void setEvent() {
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyProfile) {
                    drawerLayout.openDrawer(GravityCompat.START);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } else {
                    setFragmentPosition(0);
                }
            }
        });

        createQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplication(), ActivityCreateQuestion.class);
                intent.putExtra("QUESTION_MODE", 0);
                startActivityForResult(intent, Global.TRIGGGET_UPDATE);
            }
        });

        questionSheet.setQuestionCreatedSent(new FragmentQuestionSheet.OnQuestionCreatedSent() {
            @Override
            public void ISentConfirmedCreated() {
                showNotification();
            }
        });

        activityTransactionStatus.setOnFinishedListener(new ActivityTransactionStatus.OnFinishedListener() {
            @Override
            public void OnFinished() {
                refreshUser();
            }
        });

        activityTransactionStatus.setOnTransactionCanceledListener(new ActivityTransactionStatus.OnTransactionCanceledListener() {
            @Override
            public void OnTransactionCanceled() {
                showNotificationTransactionCanceled();
            }
        });
    }

    private void tryGetProfile() {
        if (Connectivity.isConnected(getApplicationContext())) {
            authPresenter.doGetProfile(session.getLoginType() == ConfigApp.get().STUDENT ? Auth.AUTH_TYPE_STUDENT : Auth.AUTH_TYPE_MENTOR, String.valueOf(session.getAuth().getId()), getApplicationContext());
        } else {
            showNotificationNetwork(new INoInternet() {
                @Override
                public void onRetry() {
                    tryGetProfile();
                }
            });
        }
    }

    private void showNotificationTransactionCanceled() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(rootLayout, "Transaksi pembayaran berhasil dibatalkan", Snackbar.LENGTH_LONG);
                applicationTool.resizeSnackBar(snackbar, 2);
                snackbar.show();
            }
        }, 500);
    }

    private void showNotification() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(rootLayout, "Pertanyaan telah diajukan", Snackbar.LENGTH_LONG);
                applicationTool.resizeSnackBar(snackbar, 1);
                snackbar.show();
            }
        }, 500);
    }

    public void showNotificationNetwork(final INoInternet iNoInternet) {
        snackbar = Snackbar.make(rootLayout, getResources().getString(R.string.text_no_internet), Snackbar.LENGTH_INDEFINITE);
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
    public void onBackPressed() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (!(fragment instanceof FragmentHome)) {
                setFragmentPosition(0);
            } else {
                if (!((FragmentHome) fragment).searchView.isIconified()) {
                    ((FragmentHome) fragment).searchView.setIconified(true);
                } else {
                    if (intervalBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                        super.onBackPressed();
                        return;
                    } else {
                        toast = Toast.makeText(MainActivity.this, "Tekan tombol kembali lagi untuk keluar", Toast.LENGTH_SHORT);
                        applicationTool.resizeToast(toast);
                        toast.show();
                    }
                    intervalBackPressed = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.empty, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        int id = item.getItemId();
        if (id == R.id.nav_home) {
            if (!(fragment instanceof FragmentHome)) {
                setFragmentPosition(0);
            }
        } else if (id == R.id.nav_profile) {
            if (!(fragment instanceof FragmentProfile)) {
                setFragmentPosition(1);
            }
        } else if (id == R.id.nav_transaction) {
            if (!(fragment instanceof FragmentTransaction)) {
                setFragmentPosition(2);
            }
        } else if (id == R.id.nav_balance_info) {
            if (!(fragment instanceof FragmentBalanceInfo)) {
                setFragmentPosition(3);
            }
        } else if (id == R.id.nav_feedback) {
            if (!(fragment instanceof FragmentFeedback)) {
                setFragmentPosition(4);
            }
        } else if (id == R.id.nav_faq) {
            if (!(fragment instanceof FragmentFAQ)) {
                setFragmentPosition(5);
            }
        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressWarnings("RestrictedApi")
    private void setFragmentView(int id, CharSequence title) {
        if (fragment != null) {
            if (data != null) {
                fragment.setArguments(data);
            }
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_layout, fragment).commit();
            fragmentManager.executePendingTransactions();
            navigationView.setCheckedItem(id);

            setTitle(title);
        }
    }

    @SuppressWarnings("RestrictedApi")
    public void setFragmentWithData(int position, Bundle data) {
        this.data = data;
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        if (position == 0) {
            appBarLayout.setBackgroundResource(R.drawable.color_primary);
            fragment = new FragmentHome();
            menu = navigationView.getMenu();
            menuItem = menu.findItem(R.id.nav_home);
            setFragmentView(R.id.nav_home, menuItem.getTitle());
            if (student != null) {
//                if (student.getCredit() > 0 && !student.isCreditExpired()) {
//                    createQuestion.show();
//                } else {
//                    createQuestion.hide();
//                }
                if (student.getCredit() > 0) {
                    createQuestion.show();
                } else {
                    createQuestion.hide();
                }
            }
        } else if (position == 1) {
            appBarLayout.setBackgroundResource(R.drawable.color_transparent);
            fragment = new FragmentProfile();
            menu = navigationView.getMenu();
            menuItem = menu.findItem(R.id.nav_profile);
            setFragmentView(R.id.nav_profile, menuItem.getTitle());
            createQuestion.hide();
        } else if (position == 2) {
            appBarLayout.setBackgroundResource(R.drawable.color_primary);
            fragment = new FragmentTransaction();
            menu = navigationView.getMenu();
            menuItem = menu.findItem(R.id.nav_transaction);
            setFragmentView(R.id.nav_transaction, menuItem.getTitle());
            createQuestion.hide();
        } else if (position == 3) {
            appBarLayout.setBackgroundResource(R.drawable.color_primary);
            fragment = new FragmentBalanceInfo();
            menu = navigationView.getMenu();
            menuItem = menu.findItem(R.id.nav_balance_info);
            setFragmentView(R.id.nav_balance_info, menuItem.getTitle());
            createQuestion.hide();
        } else if (position == 4) {
            appBarLayout.setBackgroundResource(R.drawable.color_primary);
            fragment = new FragmentFeedback();
            menu = navigationView.getMenu();
            menuItem = menu.findItem(R.id.nav_feedback);
            setFragmentView(R.id.nav_feedback, menuItem.getTitle());
            createQuestion.hide();
            if (snackbar != null) {
                if (snackbar.isShown()) {
                    snackbar.dismiss();
                }
            }
        } else if (position == 5) {
            appBarLayout.setBackgroundResource(R.drawable.color_primary);
            fragment = new FragmentFAQ();
            menu = navigationView.getMenu();
            menuItem = menu.findItem(R.id.nav_faq);
            setFragmentView(R.id.nav_faq, menuItem.getTitle());
            createQuestion.hide();
            if (snackbar != null) {
                if (snackbar.isShown()) {
                    snackbar.dismiss();
                }
            }
        }
    }

    public void setFragmentPosition(int position) {
        setFragmentWithData(position, null);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Global.TRIGGGET_UPDATE:
                    tryGetProfile();
                    break;
            }
        }
    }

    @SuppressWarnings("RestrictedApi")
    private void refreshUser() {
        session = Session.with(getApplicationContext());
        auth = session.getAuth();

        if (session.getLoginType() == ConfigApp.get().STUDENT) {
            student = (Student) auth;
            mentorRecordContainer.setVisibility(View.GONE);
            navMenu = navigationView.getMenu();
            navMenu.findItem(R.id.nav_balance_info).setVisible(false);
            userName.setText(student.getName());
            membershipCode.setText("Kode Membership: " + student.getMember_code());

//            if (!student.isCreditExpired()) {
//                userCredit.setText(String.valueOf(student.getCredit()));
//                userDateActivationContainer.setVisibility(View.VISIBLE);
//                userDateActivation.setText(Global.get().convertDateToFormat(student.getCredit_timelife(), "EEEE, dd MMMM yyyy-HH:mm") + " WIB");
//            } else {
//                userCredit.setText("-");
//                userDateActivationContainer.setVisibility(View.GONE);
//                userDateActivation.setText("-");
//            }
            userCredit.setText(String.valueOf(student.getCredit()));
            userDateActivationContainer.setVisibility(View.GONE);

            avatarInitial.setText(Global.get().getInitialName(student.getName()));
            avatarColor = applicationTool.getAvatarColor(student.getId());
            gradientDrawable = (GradientDrawable) avatarLayout.getBackground();
            gradientDrawable.setColor(avatarColor);

            avatarLayout.setVisibility(View.VISIBLE);
            userPhoto.setVisibility(View.GONE);
            if (Session.with(getApplicationContext()).getLoginType() == ConfigApp.get().STUDENT) {
//                if ((student.getCredit() > 0 && !student.isCreditExpired()) && fragment instanceof FragmentHome) {
//                    createQuestion.show();
//                } else {
//                    createQuestion.hide();
//                }
                if (student.getCredit() > 0 && fragment instanceof FragmentHome) {
                    createQuestion.show();
                } else {
                    createQuestion.hide();
                }
            }
        } else {
            mentor = (Mentor) auth;
            userCreditStatusContainer.setVisibility(View.GONE);
            navMenu = navigationView.getMenu();
            navMenu.findItem(R.id.nav_transaction).setVisible(false);
            userName.setText(mentor.getName());
            membershipCode.setText("ID Mentorship: " + mentor.getMember_code());
            mentorBalanceAmount.setText(applicationTool.convertRpCurrency(mentor.getBalanceAmount()));
            mentorTotalSolution.setText(mentor.getBestCount() + "/" + mentor.getSolutionCount());

            avatarLayout.setVisibility(View.GONE);
            userPhoto.setVisibility(View.VISIBLE);
            createQuestion.setVisibility(View.GONE);
        }
        if (!auth.getPhoto().isEmpty()) {
            avatarLayout.setVisibility(View.GONE);
            userPhoto.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext()).load(Global.get().getAssetURL(auth.getPhoto(), auth.getAuth_type()))
                    .placeholder(R.drawable.operator_light)
                    .fit()
                    .centerCrop()
                    .into(userPhoto);
        }
    }

    @Override
    public void onSuccess(Response response) {
        if (response.getTag() != null || !TextUtils.isEmpty(response.getTag())) {
            session = Session.with(getApplicationContext());
            if (response.getTag().equals(Response.TAG_PROFILE_LOAD)) {
                ResponseAuth responseAuth = (ResponseAuth) response;
                session.createSessionToken(responseAuth.getData().getToken());
                session.createSessionLogin(responseAuth.getData().getAuth());
                refreshUser();
                EventBus.getDefault().post(new ProfileBus());
            }
        }
    }

    @Override
    public void onFailure(String message) {
        toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        raterDialog = new ClassRaterDialog(MainActivity.this);
        raterDialog.showAsNeeded();
    }
}