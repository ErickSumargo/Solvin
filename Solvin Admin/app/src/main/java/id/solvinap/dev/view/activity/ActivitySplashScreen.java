package id.solvinap.dev.view.activity;

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


import id.solvinap.dev.R;
import id.solvinap.dev.server.api.Connection;
import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.helper.Session;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelPrimary;
import id.solvinap.dev.view.helper.Tool;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 2/14/2017.
 */

public class ActivitySplashScreen extends AppCompatActivity implements IBaseResponse {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    //    VIEW
    private CoordinatorLayout mainLayout;

    //    HELPER
    private Locale locale;
    private Configuration configuration;
    private DisplayMetrics displayMetrics;

    private Intent intent;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    VARIABLE
    private final static int SPLASH_TIME_OUT = 1000;
    private final static String language = "id";

    private static final int REQUEST_PERMISSIONS = 0;
    private String[] PERMISSIONS = {
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

        init();
    }

    private void init() {
        //    VIEW
        mainLayout = (CoordinatorLayout) findViewById(R.id.splash_screen_main_layout);

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getApplicationContext()).getiAPIRequest();
        iBaseResponse = this;

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(ActivitySplashScreen.this, PERMISSIONS, REQUEST_PERMISSIONS);
        } else {
            fetchPrimaryData();
        }
    }

    private void setEvent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Session.getInstance(getApplicationContext()).loggedIn()) {
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                } else {
                    intent = new Intent(getApplicationContext(), ActivityLogin.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
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
                fetchPrimaryData();
                break;
            }
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
        Tool.getInstance(getApplicationContext()).resizeSnackBarWithCallBack(snackbar);
        snackbar.show();
    }

    private void fetchPrimaryData() {
        if (Connectivity.isConnected(getApplicationContext())) {
            iAPIRequest.loadPrimaryData().enqueue(new Callback<ModelPrimary>() {
                @Override
                public void onResponse(Call<ModelPrimary> call, retrofit2.Response<ModelPrimary> response) {
                    if (response.code() == 200) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<ModelPrimary> call, Throwable t) {
                    iBaseResponse.onFailure(t.getMessage());
                }
            });
        } else {
            showNoInternetNotification(new INoInternet() {
                @Override
                public void onRetry() {
                    fetchPrimaryData();
                }
            });
        }
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
        Session.getInstance(getApplicationContext()).saveDataPrimary(((ModelPrimary) response).getData());
        setEvent();
    }

    @Override
    public void onFailure(String message) {
        final Toast toast = Toast.makeText(ActivitySplashScreen.this, message, Toast.LENGTH_SHORT);
        Tool.getInstance(getApplicationContext()).resizeToast(toast);
        toast.show();

        fetchPrimaryData();
    }
}