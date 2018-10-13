package id.solvin.dev.view.activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.SCrypt;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.SMSBus;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.presenter.AuthPresenter;
import id.solvin.dev.view.widget.CustomAlertDialog;
import id.solvin.dev.view.widget.CustomProgressDialog;
import id.solvin.dev.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 9/19/2016.
 */
public class ActivityResetPassword extends AppCompatActivity implements IBaseResponse {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    private static OnPasswordChanged mPasswordChanged;

    public interface OnPasswordChanged {
        void IPasswordChangedConfirmed();
    }

    public void setPasswordChangedConfirmed(OnPasswordChanged listener) {
        mPasswordChanged = listener;
    }

    private void passwordChangedConfirmed() {
        if (mPasswordChanged != null) {
            mPasswordChanged.IPasswordChangedConfirmed();
        }
    }

    private View focus;
    private CoordinatorLayout mainLayout;
    private LinearLayout mobileContainer, codeContainer, passwordNewContainer;
    private EditText mobileField, codeField;
    private TextInputLayout passwordNewLayout;
    private AutoCompleteTextView passwordNew, empty;
    private TextView resendCode, timer;
    private Button mobileConfirmation, codeConfirmation, resetPassword;
    private ImageButton togglePassword;
    private Toast toast;
    private Snackbar snackbar;

    private ClassApplicationTool applicationTool;
    private CustomAlertDialog customAlertDialog;
    private CustomProgressDialog customProgressDialog;

    private int passwordNewPaddingRight, cursorPoint;
    private String mobileText, passwordText, encryptedPhone, encryptedPassword;
    private SpannableStringBuilder spannableStringBuilder;
    private boolean visibilityOff, passwordValid, codeResent = false;
    private AuthPresenter presenter;
    private String phone, password, token;

    private LinearLayout errorMobileMessageContainer, errorCodeMessageContainer;
    private TextView errorMobileMessage;

    private CountDownTimer countDownTimer;
    private boolean mobileValid = true, codeValid = true;
    private boolean isTimerRunning = false;

    private static final int REQUEST_SMS_PERMISSIONS = 0;
    private String[] SMS_PERMISSIONS = {
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.READ_SMS};

    private SCrypt sCrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        init();
        setEvent();
    }

    private void init() {
        focus = null;
        presenter = new AuthPresenter(this);

        mainLayout = (CoordinatorLayout) findViewById(R.id.reset_password_main_layout);
        mobileContainer = (LinearLayout) findViewById(R.id.reset_password_mobile_container);
        codeContainer = (LinearLayout) findViewById(R.id.reset_password_code_container);
        passwordNewContainer = (LinearLayout) findViewById(R.id.reset_password_new_container);
        mobileField = (EditText) findViewById(R.id.reset_password_mobile_field);
        codeField = (EditText) findViewById(R.id.reset_password_code_field);
        passwordNewLayout = (TextInputLayout) findViewById(R.id.reset_password_new_layout);
        passwordNew = (AutoCompleteTextView) findViewById(R.id.reset_password_new);
        empty = (AutoCompleteTextView) findViewById(R.id.reset_password_empty);
        resendCode = (TextView) findViewById(R.id.reset_password_resend_code);
        timer = (TextView) findViewById(R.id.reset_password_timer);
        mobileConfirmation = (Button) findViewById(R.id.reset_password_mobile_confirmation);
        codeConfirmation = (Button) findViewById(R.id.reset_password_code_confirmation);
        resetPassword = (Button) findViewById(R.id.reset_password);
        togglePassword = (ImageButton) findViewById(R.id.reset_password_toggle_password);

        errorMobileMessageContainer = (LinearLayout) findViewById(R.id.reset_password_error_mobile_message_container);
        errorCodeMessageContainer = (LinearLayout) findViewById(R.id.reset_password_error_code_message_container);
        errorMobileMessage = (TextView) findViewById(R.id.reset_password_error_mobile_message);

        applicationTool = new ClassApplicationTool(getApplicationContext());

        spannableStringBuilder = new SpannableStringBuilder(resendCode.getText());
        spannableStringBuilder.setSpan(new UnderlineSpan(), 0, spannableStringBuilder.length(), 0);
        resendCode.setText(spannableStringBuilder);

        mobileConfirmation.setEnabled(false);
        mobileConfirmation.setBackgroundResource(R.drawable.primary_button_disabled);
        mobileConfirmation.setPadding((int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m));

        codeConfirmation.setEnabled(false);
        codeConfirmation.setBackgroundResource(R.drawable.primary_button_disabled);
        codeConfirmation.setPadding((int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m));

        togglePassword.setVisibility(View.GONE);

        passwordNewPaddingRight = passwordNew.getPaddingRight();
        visibilityOff = passwordValid = true;

        sCrypt = SCrypt.getInstance();
    }

    private void setEvent() {
        passwordNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!passwordValid) {
                    passwordNewLayout.setError(null);
                    passwordNewLayout.setErrorEnabled(false);
                    passwordValid = true;
                }
                if (s.length() != 0) {
                    togglePassword.setVisibility(View.VISIBLE);
                    passwordNew.setPadding(passwordNew.getPaddingLeft(), passwordNew.getPaddingTop(),
                            passwordNewPaddingRight, passwordNew.getPaddingBottom());
                } else {
                    togglePassword.setVisibility(View.GONE);
                    passwordNew.setPadding(passwordNew.getPaddingLeft(), passwordNew.getPaddingTop(),
                            empty.getPaddingRight(), passwordNew.getPaddingBottom());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        togglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursorPoint = passwordNew.getSelectionStart();
                if (visibilityOff) {
                    passwordNew.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordNew.setSelection(cursorPoint);
                    togglePassword.setImageResource(R.drawable.ic_visibility_off);

                    visibilityOff = false;
                } else {
                    passwordNew.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordNew.setSelection(cursorPoint);
                    togglePassword.setImageResource(R.drawable.ic_visibility);

                    visibilityOff = true;
                }
            }
        });

        mobileField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    mobileConfirmation.setEnabled(true);
                    mobileConfirmation.setBackgroundResource(R.drawable.primary_button);
                } else {
                    mobileConfirmation.setEnabled(false);
                    mobileConfirmation.setBackgroundResource(R.drawable.primary_button_disabled);
                }
                mobileConfirmation.setPadding((int) getResources().getDimension(R.dimen.default_padding_0m),
                        (int) getResources().getDimension(R.dimen.default_padding_0m),
                        (int) getResources().getDimension(R.dimen.default_padding_0m),
                        (int) getResources().getDimension(R.dimen.default_padding_0m));
                if (!mobileValid) {
                    errorMobileMessageContainer.setVisibility(View.GONE);
                    mobileValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        codeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    codeConfirmation.setEnabled(true);
                    codeConfirmation.setBackgroundResource(R.drawable.primary_button);
                } else {
                    codeConfirmation.setEnabled(false);
                    codeConfirmation.setBackgroundResource(R.drawable.primary_button_disabled);
                }
                codeConfirmation.setPadding((int) getResources().getDimension(R.dimen.default_padding_0m),
                        (int) getResources().getDimension(R.dimen.default_padding_0m),
                        (int) getResources().getDimension(R.dimen.default_padding_0m),
                        (int) getResources().getDimension(R.dimen.default_padding_0m));
                if (!codeValid) {
                    errorCodeMessageContainer.setVisibility(View.GONE);
                    codeValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast = Toast.makeText(ActivityResetPassword.this, "Kode verifikasi telah dikirimkan", Toast.LENGTH_SHORT);
                applicationTool.resizeToast(toast);
                toast.show();
                try {
                    encryptedPhone = sCrypt.bytesToHex(sCrypt.encrypt(mobileText));
                } catch (Exception e) {
                }
                presenter.resetPasswordStep1(encryptedPhone, getApplicationContext());

                if (!codeResent) {
                    setCountDownTimer();
                }
            }
        });

        mobileConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMobileValidity();
                if (mobileValid) {
                    if (!hasPermissions(getApplicationContext(), SMS_PERMISSIONS)) {
                        ActivityCompat.requestPermissions(ActivityResetPassword.this, SMS_PERMISSIONS, REQUEST_SMS_PERMISSIONS);
                    } else {
                        processRequest();
                    }
                }
            }
        });

        codeConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCodeConfirmation();
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidity();
                if (passwordValid) {
                    customAlertDialog = new CustomAlertDialog(ActivityResetPassword.this);
                    customAlertDialog.setTitle("Konfirmasi");
                    customAlertDialog.setMessage("Anda akan menggantikan password lama dengan yang baru?");
                    customAlertDialog.setNegativeButton("Tidak", new CustomAlertDialog.OnNegativeClickListener() {
                        @Override
                        public void onClick() {
                            customAlertDialog.dismiss();
                        }
                    });
                    customAlertDialog.setPositiveButton("Ya", new CustomAlertDialog.OnPositiveClickListener() {
                        @Override
                        public void onClick() {
                            customAlertDialog.dismiss();

                            tryResetPassword();
                        }
                    });
                } else {
                    focus.requestFocus();
                    if (!passwordValid) {
                        passwordNew.setPadding(passwordNew.getPaddingLeft(), passwordNew.getPaddingTop(),
                                empty.getPaddingRight(), passwordNew.getPaddingBottom());
                        togglePassword.setVisibility(View.GONE);
                    }
                }
            }
        });
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
            case REQUEST_SMS_PERMISSIONS: {
                processRequest();
            }
        }
    }

    private void processRequest() {
        customAlertDialog = new CustomAlertDialog(ActivityResetPassword.this);
        customAlertDialog.setTitle(mobileField.getText().toString());
        customAlertDialog.setMessage("Kode verifikasi akan dikirimkan pada nomor ini via SMS");
        customAlertDialog.setNegativeButton("Batal", new CustomAlertDialog.OnNegativeClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();
            }
        });
        customAlertDialog.setPositiveButton("OK", new CustomAlertDialog.OnPositiveClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();

                sendMobileConfirmation();
            }
        });
    }

    private void sendMobileConfirmation() {
        customProgressDialog = new CustomProgressDialog(ActivityResetPassword.this);
        customProgressDialog.setMessage("Mengirimkan kode verifikasi...");

        if (Connectivity.isConnected(getApplicationContext())) {
            try {
                encryptedPhone = sCrypt.bytesToHex(sCrypt.encrypt(mobileText));
            } catch (Exception e) {
            }
            presenter.resetPasswordStep1(encryptedPhone, getApplicationContext());
        } else {
            customProgressDialog.dismiss();

            showNotificationNetwork(new INoInternet() {
                @Override
                public void onRetry() {
                    sendMobileConfirmation();
                }
            });
        }
    }

    private void sendCodeConfirmation() {
        customProgressDialog = new CustomProgressDialog(ActivityResetPassword.this);
        customProgressDialog.setMessage("Verifikasi kode...");

        if (Connectivity.isConnected(getApplicationContext())) {
//            try {
//                encryptedPhone = sCrypt.bytesToHex(sCrypt.encrypt(mobileText));
//            } catch (Exception e) {
//            }
            presenter.resetPasswordStep2(encryptedPhone, codeField.getText().toString(), getApplicationContext());
        } else {
            customProgressDialog.dismiss();

            showNotificationNetwork(new INoInternet() {
                @Override
                public void onRetry() {
                    sendCodeConfirmation();
                }
            });
        }
    }

    private void tryResetPassword() {
        customProgressDialog = new CustomProgressDialog(ActivityResetPassword.this);
        customProgressDialog.setMessage("Me-reset password...");

        if (Connectivity.isConnected(getApplicationContext())) {
            try {
//                encryptedPhone = sCrypt.bytesToHex(sCrypt.encrypt(mobileText));
                encryptedPassword = sCrypt.bytesToHex(sCrypt.encrypt(passwordText));
            } catch (Exception e) {
            }
            presenter.resetPasswordStep3(encryptedPhone, encryptedPassword, token, getApplicationContext());
        } else {
            customProgressDialog.dismiss();

            showNotificationNetwork(new INoInternet() {
                @Override
                public void onRetry() {
                    tryResetPassword();
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

    private void setCountDownTimer() {
        timer.setVisibility(View.VISIBLE);

        resendCode.setEnabled(false);
        resendCode.setAlpha(0.5f);
        resendCode.setTextColor(getResources().getColor(R.color.colorSubHeader));

        countDownTimer = new CountDownTimer(60500, 1000) {
            int secondsLeft = 60;

            @Override
            public void onTick(long l) {
                secondsLeft--;
                if (secondsLeft < 10) {
                    timer.setText("00:0" + String.valueOf(secondsLeft));
                } else {
                    timer.setText("00:" + String.valueOf(secondsLeft));
                }
                isTimerRunning = true;
            }

            @Override
            public void onFinish() {
                resetCountDownTimer();
            }
        }.start();
    }

    private void checkMobileValidity() {
        mobileText = mobileField.getText().toString().trim();
        if (!isMobileValid(mobileText)) {
            errorMobileMessage.setText("Format no. hp salah");
            errorMobileMessageContainer.setVisibility(View.VISIBLE);

            mobileValid = false;
        }
    }

    private boolean isMobileValid(String mobile) {
        return (mobile.length() > 9 && mobile.charAt(0) == '0');
    }

    private void checkValidity() {
        passwordText = passwordNew.getText().toString().trim();

        if (TextUtils.isEmpty(passwordText)) {
            passwordNewLayout.setError("Field password harus diisikan");
            focus = passwordNew;
            passwordValid = false;
        } else if (!isPasswordValid(passwordText)) {
            passwordNewLayout.setError("Panjang password harus terdiri dari minimal 6 karakter");
            focus = passwordNew;
            passwordValid = false;
        } else {
            passwordValid = true;
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private void resetCountDownTimer() {
        timer.setVisibility(View.GONE);

        resendCode.setEnabled(true);
        resendCode.setAlpha(1f);
        resendCode.setTextColor(getResources().getColor(R.color.colorPrimary));

        codeResent = false;
        isTimerRunning = false;
    }

    private void setErrorMobileMessage(String message) {
        errorMobileMessage.setText(message);
        errorMobileMessageContainer.setVisibility(View.VISIBLE);
    }

    private void setErrorCodeMessage() {
        errorCodeMessageContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isTimerRunning) {
            countDownTimer.cancel();
            resetCountDownTimer();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Subscribe
    public void onEventSubscribe(SMSBus smsBus) {
        if (smsBus.getNumber() != null) {
            codeField.setText(smsBus.getNumber());
            codeField.setSelection(codeField.length());

            sendCodeConfirmation();
        }
    }

    @Override
    public void onSuccess(Response response) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }
        switch (response.getTag()) {
            case Response.TAG_RESET_PASSWORD_STEP_1:
                if (response.isSuccess()) {
                    mobileContainer.setVisibility(View.GONE);
                    codeContainer.setVisibility(View.VISIBLE);

                    codeField.requestFocus();
                } else {
                    mobileValid = false;
                    setErrorMobileMessage(response.getMessage());
                }
                break;
            case Response.TAG_RESET_PASSWORD_STEP_2:
                if (response.isSuccess()) {
                    if (!response.getMessage().equals("invalid")) {
                        token = response.getMessage();
                        codeContainer.setVisibility(View.GONE);
                        passwordNewContainer.setVisibility(View.VISIBLE);

                        passwordNew.requestFocus();
                    } else {
                        codeValid = false;
                        setErrorCodeMessage();
                    }
                }
                break;
            case Response.TAG_RESET_PASSWORD_STEP_3:
                finish();
                passwordChangedConfirmed();
                break;
        }
    }

    @Override
    public void onFailure(String message) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }

        toast = Toast.makeText(ActivityResetPassword.this, message, Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();
    }
}