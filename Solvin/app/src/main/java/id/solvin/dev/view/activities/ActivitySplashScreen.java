package id.solvin.dev.view.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.presenter.QuestionPresenter;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 10/11/2016.
 */
public class ActivitySplashScreen extends AppCompatActivity  implements IBaseResponse {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    private Toast toast;
    private CoordinatorLayout mainLayout;
    private Intent intent;
    private static final int SPLASH_TIME_OUT = 1000;
    private QuestionPresenter questionPresenter;

    private Configuration configuration;
    private DisplayMetrics displayMetrics;
    private ClassApplicationTool applicationTool;

    private Locale locale;
    private final static String language = "id";

    private static final int REQUEST_PERMISSIONS = 0;
    private String[] PERMISSIONS = {
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocaleLanguage();
        adjustFontScale();
        setContentView(R.layout.activity_splash_screen);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/SourceSansPro-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mainLayout = (CoordinatorLayout) findViewById(R.id.splash_screen_main_layout);
        questionPresenter = new QuestionPresenter(this);

        applicationTool = new ClassApplicationTool(getApplicationContext());
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(ActivitySplashScreen.this, PERMISSIONS, REQUEST_PERMISSIONS);
        } else {
            tryGetData();
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                tryGetData();
                break;
            }
        }
    }

    private void tryGetData() {
        if (Connectivity.isConnected(getApplicationContext())) {
            questionPresenter.doGetData(getApplicationContext());
        } else {
            showNoInternetNotification(new INoInternet() {
                @Override
                public void onRetry() {
                    tryGetData();
                }
            });
        }
    }

    private void showNoInternetNotification(final INoInternet iNoInternet) {
        final Snackbar snackbar = Snackbar.make(mainLayout, getResources().getString(R.string.text_no_internet), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Coba Lagi", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNoInternet.onRetry();
            }
        });
        applicationTool.resizeSnackBarWithCallBack(snackbar);
        snackbar.show();
    }

    private void setEvent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Session Redirect to Welcome or Login Activity */
                Session.with(getApplicationContext()).checkSignIn();
                /* Conditional for init() and setEvent() - Reduce error */
                if (Session.with(getApplicationContext()).isSignIn()) {
                    intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    private void setLocaleLanguage() {
        locale = new Locale(language);
        Locale.setDefault(locale);
        configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private void adjustFontScale() {
        configuration = getResources().getConfiguration();
        if (configuration.fontScale != 1) {
            configuration.fontScale = 1;
            displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            displayMetrics.scaledDensity = configuration.fontScale * displayMetrics.density;
            getApplicationContext().getResources().updateConfiguration(configuration, displayMetrics);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onSuccess(Response response) {
        setEvent();
    }

    @Override
    public void onFailure(String message) {
        toast = Toast.makeText(ActivitySplashScreen.this, message, Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();

        tryGetData();
    }
}