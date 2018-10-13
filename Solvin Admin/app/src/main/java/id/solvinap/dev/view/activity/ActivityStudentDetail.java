package id.solvinap.dev.view.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import id.solvinap.dev.R;
import id.solvinap.dev.server.api.Connection;
import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataAuth;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.view.fragment.FragmentAuthComment;
import id.solvinap.dev.view.fragment.FragmentStudentProfile;
import id.solvinap.dev.view.fragment.FragmentStudentQuestion;
import id.solvinap.dev.view.fragment.FragmentStudentTransaction;
import id.solvinap.dev.view.helper.Tool;
import id.solvinap.dev.view.widget.CustomAdminPasswordConfirmation;
import id.solvinap.dev.view.widget.CustomAlertDialog;
import id.solvinap.dev.view.widget.CustomCheatSheet;
import id.solvinap.dev.view.widget.CustomProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 1/31/2017.
 */

public class ActivityStudentDetail extends AppCompatActivity implements IBaseResponse {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    public interface OnReceiveNotificationListener {
        void OnReceiveNotification();
    }

    private static OnReceiveNotificationListener mReceiveNotificationListener;

    public void setOnReceiveNotificationListener(OnReceiveNotificationListener listener) {
        mReceiveNotificationListener = listener;
    }

    private void sendAuthBlockedNotification() {
        if (mReceiveNotificationListener != null) {
            mReceiveNotificationListener.OnReceiveNotification();
        }
    }

    //    VIEW
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CoordinatorLayout mainLayout;
    private View tabProfile, tabQuestion, tabComment, tabTransaction;
    private TextView tabProfileTitle, tabQuestionTitle, tabCommentTitle, tabTransactionTitle;
    private ImageView tabProfileIcon, tabQuestionIcon, tabCommentIcon, tabTransactionIcon;

    private ImageButton blockIcon;
    private CustomProgressDialog customProgressDialog;

    //    HELPER
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private TypedValue typedValue;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    OBJECT
    private FragmentStudentProfile studentProfile;
    private FragmentStudentQuestion studentQuestion;
    private FragmentAuthComment userComment;
    private FragmentStudentTransaction studentTransaction;

    public DataAuth dataAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        init();
        setEvent();
    }

    private void init() {
        //    VIEW
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = (CoordinatorLayout) findViewById(R.id.student_detail_main_layout);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(sectionsPagerAdapter.getCount() - 1);
        viewPager.setCurrentItem(0);
        viewPager.setAdapter(sectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setCustomView(R.layout.custom_tab_detail);
        tabLayout.getTabAt(1).setCustomView(R.layout.custom_tab_detail);
        tabLayout.getTabAt(2).setCustomView(R.layout.custom_tab_detail);
        tabLayout.getTabAt(3).setCustomView(R.layout.custom_tab_detail);

        tabProfile = tabLayout.getTabAt(0).getCustomView();
        tabQuestion = tabLayout.getTabAt(1).getCustomView();
        tabComment = tabLayout.getTabAt(2).getCustomView();
        tabTransaction = tabLayout.getTabAt(3).getCustomView();

        tabProfileTitle = (TextView) tabProfile.findViewById(R.id.tab_title);
        tabProfileTitle.setText(sectionsPagerAdapter.getPageTitle(0));
        tabProfileIcon = (ImageView) tabProfile.findViewById(R.id.tab_icon);
        tabProfileIcon.setImageResource(R.drawable.ic_account_circle_light);

        tabQuestionTitle = (TextView) tabQuestion.findViewById(R.id.tab_title);
        tabQuestionTitle.setText(sectionsPagerAdapter.getPageTitle(1));
        tabQuestionIcon = (ImageView) tabQuestion.findViewById(R.id.tab_icon);
        tabQuestionIcon.setImageResource(R.drawable.ic_forum_light);

        tabCommentTitle = (TextView) tabComment.findViewById(R.id.tab_title);
        tabCommentTitle.setText(sectionsPagerAdapter.getPageTitle(2));
        tabCommentIcon = (ImageView) tabComment.findViewById(R.id.tab_icon);
        tabCommentIcon.setImageResource(R.drawable.ic_comment_processing_light);

        tabTransactionTitle = (TextView) tabTransaction.findViewById(R.id.tab_title);
        tabTransactionTitle.setText(sectionsPagerAdapter.getPageTitle(3));
        tabTransactionIcon = (ImageView) tabTransaction.findViewById(R.id.tab_icon);
        tabTransactionIcon.setImageResource(R.drawable.ic_shopping_cart_light);

        //    HELPER
        sharedPreferences = getApplicationContext().getSharedPreferences(Global.PREFERENCES_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(Global.PREFERENCES_AUTH_TYPE, "student");
        editor.commit();

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getApplicationContext()).getiAPIRequest();
        iBaseResponse = this;

        //    OBJECT
        studentProfile = new FragmentStudentProfile();
        studentQuestion = new FragmentStudentQuestion();
        userComment = new FragmentAuthComment();
        studentTransaction = new FragmentStudentTransaction();

        dataAuth = (DataAuth) getIntent().getSerializableExtra(Global.PREFERENCES_INTENT_EXTRA);
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tabQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() == 1) {
                    studentQuestion.questionView.scrollToPosition(0);

                    studentQuestion.resetRecyclerViewData();
                }
                viewPager.setCurrentItem(1);
            }
        });

        tabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() == 2) {
                    userComment.commentView.scrollToPosition(0);

                    userComment.resetRecyclerViewData();
                }
                viewPager.setCurrentItem(2);
            }
        });

        tabTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() == 3) {
                    studentTransaction.transactionView.scrollToPosition(0);

                    studentTransaction.resetRecyclerViewData();
                }
                viewPager.setCurrentItem(3);
            }
        });
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return studentProfile;
                case 1:
                    return studentQuestion;
                case 2:
                    return userComment;
                case 3:
                    return studentTransaction;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Profil";
                case 1:
                    return "Pertanyaan";
                case 2:
                    return "Komentar";
                case 3:
                    return "Transaksi";
            }
            return null;
        }
    }

    private void blockStudent() {
        customProgressDialog = new CustomProgressDialog(ActivityStudentDetail.this);
        customProgressDialog.setMessage("Memblokir akun pengguna...");

        if (Connectivity.isConnected(getApplicationContext())) {
            iAPIRequest.blockAuth(String.valueOf(dataAuth.getId()), "student").enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    customProgressDialog.dismiss();
                    if (response.code() == 200) {
                        finish();
                        sendAuthBlockedNotification();
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {
                    customProgressDialog.dismiss();

                    iBaseResponse.onFailure(t.getMessage());
                }
            });
        } else {
            customProgressDialog.dismiss();
            showNoInternetNotification(new INoInternet() {
                @Override
                public void onRetry() {
                    blockStudent();
                }
            });
        }
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

    @Override
    public void onSuccess(Response response) {

    }

    @Override
    public void onFailure(String message) {
        final Toast toast = Toast.makeText(ActivityStudentDetail.this, message, Toast.LENGTH_SHORT);
        Tool.getInstance(getApplicationContext()).resizeToast(toast);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (dataAuth.getActive() == 1) {
            getMenuInflater().inflate(R.menu.student, menu);

            typedValue = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.actionBarItemBackground, typedValue, true);

            blockIcon = (ImageButton) menu.findItem(R.id.action_block_user).getActionView();
            blockIcon.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.action_button_default_width), ViewGroup.LayoutParams.MATCH_PARENT));
            blockIcon.setImageResource(R.drawable.ic_block_light);
            blockIcon.setBackgroundResource(typedValue.resourceId);
            blockIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CustomAdminPasswordConfirmation adminPasswordConfirmation = new CustomAdminPasswordConfirmation(ActivityStudentDetail.this, Global.ACTION_PASSWORD);
                    adminPasswordConfirmation.setOnConfirmedListener(new CustomAdminPasswordConfirmation.OnConfirmedListener() {
                        @Override
                        public void OnConfirmed() {
                            final CustomAlertDialog customAlertDialog = new CustomAlertDialog(ActivityStudentDetail.this);
                            customAlertDialog.setTitle("Konfirmasi");
                            customAlertDialog.setMessage("Anda akan memblokir akun pengguna terkait?");
                            customAlertDialog.setPositiveButton("Ya", new CustomAlertDialog.OnPositiveClickListener() {
                                @Override
                                public void onClick() {
                                    customAlertDialog.dismiss();

                                    blockStudent();
                                }
                            });
                            customAlertDialog.setNegativeButton("Tidak", new CustomAlertDialog.OnNegativeClickListener() {
                                @Override
                                public void onClick() {
                                    customAlertDialog.dismiss();
                                }
                            });
                        }
                    });
                }
            });
            CustomCheatSheet.setup(blockIcon, getString(R.string.action_block_user));
        }
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}