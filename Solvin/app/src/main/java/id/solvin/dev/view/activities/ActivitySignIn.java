package id.solvin.dev.view.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.SCrypt;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Error;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.interfaces.OnErrors;
import id.solvin.dev.model.response.ResponseAuth;
import id.solvin.dev.presenter.AuthPresenter;
import id.solvin.dev.view.widget.CustomProgressDialog;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 9/3/2016.
 */
public class ActivitySignIn extends AppCompatActivity  implements IBaseResponse,OnErrors {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    private View focus;
    private CoordinatorLayout mainLayout;
    private TextInputLayout emailLayout, passwordLayout;
    private AutoCompleteTextView email, password;
    private Button signIn;
    private ImageButton togglePassword;
    private TextView forgotPassword, signUp;
    private Toast toast;
    private Intent intent;
    private Snackbar snackbar;

    private ClassApplicationTool applicationTool;
    private CustomProgressDialog customProgressDialog;

    private ActivityResetPassword resetPassword;

    private int passwordPaddingRight, cursorPoint;
    private String emailText, passwordText, deviceID, encryptedPassword;
    private boolean visibilityOff, emailValid, passwordValid;

    private ProgressDialog progressDialog;

    private AuthPresenter authPresenter;
    private SCrypt sCrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
        setEvent();
    }

    private void init() {
        authPresenter = new AuthPresenter(this);
        focus = null;
        mainLayout = (CoordinatorLayout) findViewById(R.id.sign_in_main_layout);
        emailLayout = (TextInputLayout) findViewById(R.id.sign_in_email_layout);
        passwordLayout = (TextInputLayout) findViewById(R.id.sign_in_password_layout);
        email = (AutoCompleteTextView) findViewById(R.id.sign_in_email);
        password = (AutoCompleteTextView) findViewById(R.id.sign_in_password);
        signIn = (Button) findViewById(R.id.sign_in);
        togglePassword = (ImageButton) findViewById(R.id.sign_in_toggle_password);
        forgotPassword = (TextView) findViewById(R.id.sign_in_forgot_password);
        signUp = (TextView) findViewById(R.id.sign_in_sign_up);

        resetPassword = new ActivityResetPassword();
        applicationTool = new ClassApplicationTool(getApplicationContext());

        togglePassword.setVisibility(View.GONE);

        passwordPaddingRight = password.getPaddingRight();
        visibilityOff = emailValid = passwordValid = true;

        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        sCrypt = SCrypt.getInstance();
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

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidity();
                if (emailValid && passwordValid) {
                    trySignIn();
                } else {
                    focus.requestFocus();
                    setFocusError();
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ActivitySignIn.this, ActivityResetPassword.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ActivitySignIn.this, ActivityRegistration.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        resetPassword.setPasswordChangedConfirmed(new ActivityResetPassword.OnPasswordChanged() {
            @Override
            public void IPasswordChangedConfirmed() {
                showNotification();
            }
        });
    }

    private void trySignIn() {
        customProgressDialog = new CustomProgressDialog(ActivitySignIn.this);
        customProgressDialog.setMessage("Masuk...");

        if (Connectivity.isConnected(getApplicationContext())) {
            try {
                encryptedPassword = sCrypt.bytesToHex(sCrypt.encrypt(passwordText));
            } catch (Exception e) {
            }
            authPresenter.doLogin(emailText, encryptedPassword, deviceID, getApplicationContext(), this);
        } else {
            customProgressDialog.dismiss();

            showNotificationNetwork(new INoInternet() {
                @Override
                public void onRetry() {
                    trySignIn();
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

    private void setFocusError() {
        focus.requestFocus();
        if (!passwordValid) {
            password.setPadding(password.getPaddingLeft(), password.getPaddingTop(), email.getPaddingRight(), password.getPaddingBottom());
            togglePassword.setVisibility(View.GONE);
        }
    }

    private void showNotification() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Snackbar snackbar = Snackbar.make(mainLayout, "Password baru telah berhasil diganti", Snackbar.LENGTH_LONG);
                applicationTool.resizeSnackBar(snackbar, 1);
                snackbar.show();
            }
        }, 500);
    }

    private void checkValidity() {
        emailText = email.getText().toString().trim();
        passwordText = password.getText().toString().trim();

        if (TextUtils.isEmpty(passwordText)) {
            passwordLayout.setError("Isikan password aktif anda");
            focus = password;
            passwordValid = false;
        } else {
            passwordValid = true;
        }

        if (TextUtils.isEmpty(emailText)) {
            emailLayout.setError("Isikan email terdaftar anda");
            focus = email;
            emailValid = false;
            passwordValid = false;
        } else if (!isEmailValid(emailText)) {
            emailLayout.setError("Format email tidak valid");
            focus = email;
            emailValid = false;
            passwordValid = false;
        } else {
            emailValid = true;
        }
    }

    private boolean isEmailValid(String email) {
        return email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onSuccess(Response response) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }
        ResponseAuth responseAuth = (ResponseAuth) response;

        Session.with(getApplicationContext()).createSessionLogin(responseAuth.getData().getAuth());
        Session.with(getApplicationContext()).createSessionToken(responseAuth.getData().getToken());

        intent = new Intent(ActivitySignIn.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onFailure(String message) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }

        toast = Toast.makeText(ActivitySignIn.this, message, Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();
    }

    @Override
    public void onError(List<Error> errorList) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }

        for (Error error : errorList) {
            if (error.getType() == Error.ERROR_EMAIL) {
                emailLayout.setError(error.getMessage());
                focus = email;
                emailValid = false;
                passwordValid = false;

                setFocusError();
                break;
            }
            if (error.getType() == Error.ERROR_ACTIVE_PASSWORD) {
                passwordLayout.setError(error.getMessage());
                focus = password;
                passwordValid = false;

                setFocusError();
                break;
            }
        }
    }
}