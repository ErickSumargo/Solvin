package id.solvinap.dev.view.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import id.solvinap.dev.R;
import id.solvinap.dev.view.helper.Tool;

/**
 * Created by Erick Sumargo on 2/3/2017.
 */

public class CustomAdminPasswordConfirmation extends AppCompatActivity {
    //    UI COMPONENT
    private AlertDialog.Builder customBuilder;
    private AlertDialog customAlertDialog;
    private TextInputLayout passwordLayout;
    private AutoCompleteTextView password, passwordEmpty;
    private ImageButton togglePassword;
    private Toast toast;
    private View focus;

    //    UI HELPER
    private Context context;

    //    LOCAL VARIABLE
    private int passwordPaddingRight, cursorPoint;
    private String actionPassword, passwordText;
    private boolean passwordVisible, actionPasswordValid;

    //    INTERFACE
    private OnConfirmedListener mConfirmedListener;

    public interface OnConfirmedListener {
        void OnConfirmed();
    }

    public void setOnConfirmedListener(OnConfirmedListener listener) {
        mConfirmedListener = listener;
    }

    private void confirmed() {
        if (mConfirmedListener != null) {
            mConfirmedListener.OnConfirmed();
        }
    }

    public CustomAdminPasswordConfirmation(Context context, String actionPassword) {
        this.context = context;
        this.actionPassword = actionPassword;

        init();
        setEvent();
    }

    private void init() {
        customBuilder = new AlertDialog.Builder(context);
        customBuilder.setPositiveButton("Konfirmasi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        customBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        customAlertDialog = customBuilder.create();
        customAlertDialog.setView(customAlertDialog.getLayoutInflater().inflate(R.layout.admin_password_confirmation, null));
        customAlertDialog.setCanceledOnTouchOutside(true);
        customAlertDialog.show();
        Tool.getInstance(context).resizeAlertDialog(customAlertDialog);

        passwordLayout = (TextInputLayout) customAlertDialog.findViewById(R.id.admin_password_confirmation_layout);
        password = (AutoCompleteTextView) customAlertDialog.findViewById(R.id.admin_password);
        passwordEmpty = (AutoCompleteTextView) customAlertDialog.findViewById(R.id.admin_password_empty);
        togglePassword = (ImageButton) customAlertDialog.findViewById(R.id.admin_toggle_password);

        togglePassword.setVisibility(View.GONE);
        passwordPaddingRight = password.getPaddingRight();

        actionPasswordValid = passwordVisible = false;
    }

    private void setEvent() {
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!actionPasswordValid) {
                    passwordLayout.setError(null);
                    passwordLayout.setErrorEnabled(false);
                    actionPasswordValid = true;
                }
                if (charSequence.length() != 0) {
                    togglePassword.setVisibility(View.VISIBLE);
                    password.setPadding(password.getPaddingLeft(), password.getPaddingTop(),
                            passwordPaddingRight, password.getPaddingBottom());
                } else {
                    togglePassword.setVisibility(View.GONE);
                    password.setPadding(password.getPaddingLeft(), password.getPaddingTop(),
                            passwordEmpty.getPaddingRight(), password.getPaddingBottom());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        togglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursorPoint = password.getSelectionStart();
                if (passwordVisible) {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    password.setSelection(cursorPoint);
                    togglePassword.setImageResource(R.drawable.ic_visibility);

                    passwordVisible = false;
                } else {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    password.setSelection(cursorPoint);
                    togglePassword.setImageResource(R.drawable.ic_visibility_off);

                    passwordVisible = true;
                }
            }
        });

        customAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customAlertDialog.dismiss();
            }
        });

        customAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidity();
                if (actionPasswordValid) {
                    customAlertDialog.dismiss();
                    confirmed();
                } else {
                    focus.requestFocus();
                    togglePassword.setVisibility(View.GONE);
                    password.setPadding(password.getPaddingLeft(), password.getPaddingTop(),
                            passwordEmpty.getPaddingRight(), password.getPaddingBottom());
                }
            }
        });
    }

    private void checkValidity() {
        passwordText = password.getText().toString().trim();
        if (TextUtils.isEmpty(passwordText)) {
            passwordLayout.setError("Harap masukkan password admin");
            focus = passwordLayout;
            actionPasswordValid = false;
        } else if (!passwordText.equals(actionPassword)) {
            passwordLayout.setError("Password admin salah, silahkan coba lagi");
            focus = passwordLayout;
            actionPasswordValid = false;
        } else {
            actionPasswordValid = true;
        }
    }

    public void dismiss() {
        customAlertDialog.dismiss();
    }
}