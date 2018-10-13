package id.solvin.dev.view.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.view.fragments.FragmentCodeConfirmation;
import id.solvin.dev.view.fragments.FragmentMobileConfirmation;
import id.solvin.dev.view.fragments.FragmentSignUp;
import id.solvin.dev.view.widget.ClassCustomViewPager;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 9/12/2016.
 */
public class ActivityRegistration extends AppCompatActivity {
    //    INTERFACE
    public interface INoInternet{
        void onRetry();
    }

    private CoordinatorLayout mainLayout;
    private SectionsPagerAdapter sectionsPagerAdapter;
    public ClassCustomViewPager customViewPager;
    public ClassApplicationTool applicationTool;
    private Snackbar snackbar;

    private FragmentMobileConfirmation fragmentMobileConfirmation;
    private FragmentCodeConfirmation fragmentCodeConfirmation;
    private FragmentSignUp fragmentSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();
    }

    private void init() {
        mainLayout = (CoordinatorLayout) findViewById(R.id.registration_main_layout);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        customViewPager = (ClassCustomViewPager) findViewById(R.id.container);
        customViewPager.setAdapter(sectionsPagerAdapter);
        customViewPager.setPagingEnabled(false);

        applicationTool = new ClassApplicationTool(getApplicationContext());

        fragmentMobileConfirmation = new FragmentMobileConfirmation();
        fragmentCodeConfirmation = new FragmentCodeConfirmation();
        fragmentSignUp = new FragmentSignUp();
    }

    public void showNotificationNetwork(final INoInternet iNoInternet) {
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

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return fragmentMobileConfirmation;
                case 1:
                    return fragmentCodeConfirmation;
                case 2:
                    return fragmentSignUp;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (customViewPager.getCurrentItem() == 0) {
                onBackPressed();
                return true;
            } else if (customViewPager.getCurrentItem() == 1) {
                customViewPager.setCurrentItem(0);
                if (fragmentCodeConfirmation.isTimerRunning) {
                    fragmentCodeConfirmation.countDownTimer.cancel();
                    fragmentCodeConfirmation.resetCountDownTimer();
                }
                fragmentCodeConfirmation.codeField.getText().clear();
                return true;
            } else if (customViewPager.getCurrentItem() == 2) {
                customViewPager.setCurrentItem(0);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}