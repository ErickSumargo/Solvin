package id.solvinap.dev.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import id.solvinap.dev.R;
import id.solvinap.dev.server.api.Connection;
import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.helper.Session;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelAdmin;
import id.solvinap.dev.view.helper.SCrypt;
import id.solvinap.dev.view.helper.Tool;
import id.solvinap.dev.view.widget.CustomProgressDialog;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 2/6/2017.
 */

public class ActivityLogin extends AppCompatActivity implements IBaseResponse {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    //    VIEW
    private View focus;
    private CoordinatorLayout mainLayout;
    private TextInputLayout emailLayout, passwordLayout;
    private AutoCompleteTextView email, password;
    private Button login;
    private ImageButton togglePassword;

    private CustomProgressDialog customProgressDialog;

    //    HELPER
    private Intent intent;
    private SCrypt sCrypt;

    //    OBJECT
    private Map<String, String> metadata;
    private ModelAdmin modelAuth;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    VARIABLE
    private int passwordPaddingRight, cursorPoint;
    private String emailText, passwordText,
            encryptedEmail, encryptedPassword;
    private boolean visibilityOff, emailValid, passwordValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/SourceSansPro-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        init();
        setEvent();
    }

    private void init() {
        //    VIEW
        focus = null;
        mainLayout = (CoordinatorLayout) findViewById(R.id.login_main_layout);
        emailLayout = (TextInputLayout) findViewById(R.id.login_email_layout);
        passwordLayout = (TextInputLayout) findViewById(R.id.login_password_layout);
        email = (AutoCompleteTextView) findViewById(R.id.login_email);
        password = (AutoCompleteTextView) findViewById(R.id.login_password);
        login = (Button) findViewById(R.id.login);
        togglePassword = (ImageButton) findViewById(R.id.login_toggle_password);

        togglePassword.setVisibility(View.GONE);

        //    HELPER
        sCrypt = SCrypt.getInstance();

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getApplicationContext()).getiAPIRequest();
        iBaseResponse = this;

        //    VARIABLE
        passwordPaddingRight = password.getPaddingRight();
        visibilityOff = emailValid = passwordValid = true;
    }

    private void setEvent() {
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!emailValid) {
                    emailLayout.setError(null);
                    emailLayout.setErrorEnabled(false);
                    emailValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!passwordValid) {
                    passwordLayout.setError(null);
                    passwordLayout.setErrorEnabled(false);
                    passwordValid = true;
                }
                if (s.length() != 0) {
                    togglePassword.setVisibility(View.VISIBLE);
                    password.setPadding(password.getPaddingLeft(), password.getPaddingTop(), passwordPaddingRight, password.getPaddingBottom());
                } else {
                    togglePassword.setVisibility(View.GONE);
                    password.setPadding(password.getPaddingLeft(), password.getPaddingTop(), email.getPaddingRight(), password.getPaddingBottom());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        togglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursorPoint = password.getSelectionStart();
                if (visibilityOff) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    password.setSelection(cursorPoint);
                    togglePassword.setImageResource(R.drawable.ic_visibility_off);

                    visibilityOff = false;
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    password.setSelection(cursorPoint);
                    togglePassword.setImageResource(R.drawable.ic_visibility);

                    visibilityOff = true;
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidity();
                if (emailValid && passwordValid) {
                    tryLogin();
                } else {
                    setFocusError();
                }
            }
        });
    }

    private void checkValidity() {
        emailText = email.getText().toString().trim();
        passwordText = password.getText().toString().trim();

        if (TextUtils.isEmpty(passwordText)) {
            passwordLayout.setError("Isikan password aktif admin");
            focus = password;
            passwordValid = false;
        } else {
            passwordValid = true;
        }

        if (TextUtils.isEmpty(emailText)) {
            emailLayout.setError("Isikan email terdaftar admin");
            focus = email;
            emailValid = false;
            passwordValid = false;
        } else if (!isEmailFormatValid(emailText)) {
            emailLayout.setError("Format email tidak valid");
            focus = email;
            emailValid = false;
            passwordValid = false;
        } else {
            emailValid = true;
        }
    }

    private boolean isEmailFormatValid(String email) {
        return email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    }

    private void setFocusError() {
        focus.requestFocus();
        if (!passwordValid) {
            password.setPadding(password.getPaddingLeft(), password.getPaddingTop(), email.getPaddingRight(), password.getPaddingBottom());
            togglePassword.setVisibility(View.GONE);
        }
    }

    private void tryLogin() {
        customProgressDialog = new CustomProgressDialog(ActivityLogin.this);
        customProgressDialog.setMessage("Masuk...");

        if (Connectivity.isConnected(getApplicationContext())) {
            try {
                encryptedEmail = sCrypt.bytesToHex(sCrypt.encrypt(emailText));
                encryptedPassword = sCrypt.bytesToHex(sCrypt.encrypt(passwordText));
            } catch (Exception e) {
            }
            login();
        } else {
            customProgressDialog.dismiss();
            showNoInternetNotification(new INoInternet() {
                @Override
                public void onRetry() {
                    tryLogin();
                }
            });
        }
    }

    private void login() {
        metadata = new HashMap<>();
        metadata.put("email", encryptedEmail);
        metadata.put("password", encryptedPassword);
        metadata.put("firebase_token", FirebaseInstanceId.getInstance().getToken());

        iAPIRequest.login(metadata).enqueue(new Callback<ModelAdmin>() {
            @Override
            public void onResponse(Call<ModelAdmin> call, retrofit2.Response<ModelAdmin> response) {
                customProgressDialog.dismiss();
                if (response.code() == 200) {
                    iBaseResponse.onSuccess(response.body());
                } else {
                    iBaseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ModelAdmin> call, Throwable t) {
                customProgressDialog.dismiss();
                iBaseResponse.onFailure(t.getMessage());
            }
        });
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

    @Override
    public void onSuccess(Response response) {
        modelAuth = (ModelAdmin) response;
        if (modelAuth.getResultData() == null) {
            iBaseResponse.onFailure(response.getMessage());
        } else {
            if (!modelAuth.getResultData().isEmailValid()) {
                emailLayout.setError(modelAuth.getMessage());
                focus = email;
                emailValid = false;
                passwordValid = false;

                setFocusError();
            } else {
                if (!modelAuth.getResultData().isPasswordValid()) {
                    passwordLayout.setError(modelAuth.getMessage());
                    focus = password;
                    passwordValid = false;

                    setFocusError();
                } else {
                    Session.getInstance(getApplicationContext()).saveLoginData(modelAuth.getResultData().getDataAdmin());
                    Session.getInstance(getApplicationContext()).saveToken(modelAuth.getResultData().getToken());

                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onFailure(String message) {
        final Toast toast = Toast.makeText(ActivityLogin.this, message, Toast.LENGTH_SHORT);
        Tool.getInstance(getApplicationContext()).resizeToast(toast);
        toast.show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}