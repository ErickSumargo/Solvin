package id.solvin.dev.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.R;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.SCrypt;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Error;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.interfaces.OnErrors;
import id.solvin.dev.model.response.ResponseAuth;
import id.solvin.dev.presenter.AuthPresenter;
import id.solvin.dev.view.activities.ActivityRegistration;
import id.solvin.dev.view.activities.MainActivity;
import id.solvin.dev.view.widget.CustomProgressDialog;

import java.util.List;

import static id.solvin.dev.view.fragments.FragmentCodeConfirmation.PHONE_NUMBER;

/**
 * Created by Erick Sumargo on 9/3/2016.
 */
public class FragmentSignUp extends Fragment implements IBaseResponse, OnErrors {
    private View view, focus;
    private CoordinatorLayout mainLayout;
    private TextInputLayout emailLayout, nameLayout, passwordLayout, membershipCodeLayout;
    private AutoCompleteTextView email, name, password, membershipCode;
    private TextView membershipCodeMessage;
    private Button signUp;
    private ImageButton togglePassword;
    private Toast toast;

    private Intent intent;
    private CustomProgressDialog customProgressDialog;

    private int passwordPaddingRight, cursorPoint;
    private String emailText, nameText, passwordText, deviceID, membershipCodeText;
    private boolean visibilityOff, emailValid, nameValid, passwordValid, membershipCodeValid;

    private String phoneText, encryptedPassword, encryptedPhone;
    private AuthPresenter authPresenter;

    private SCrypt sCrypt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_sign_up, container, false);
        init();
        setEvent();

        return view;
    }

    private void init() {
        authPresenter = new AuthPresenter(this);
        focus = null;
        mainLayout = (CoordinatorLayout) view.findViewById(R.id.sign_up_main_layout);
        emailLayout = (TextInputLayout) view.findViewById(R.id.sign_up_email_layout);
        nameLayout = (TextInputLayout) view.findViewById(R.id.sign_up_name_layout);
        passwordLayout = (TextInputLayout) view.findViewById(R.id.sign_up_password_layout);
        membershipCodeLayout = (TextInputLayout) view.findViewById(R.id.sign_up_membership_code_layout);

        email = (AutoCompleteTextView) view.findViewById(R.id.sign_up_email);
        name = (AutoCompleteTextView) view.findViewById(R.id.sign_up_name);
        password = (AutoCompleteTextView) view.findViewById(R.id.sign_up_password);
        membershipCode = (AutoCompleteTextView) view.findViewById(R.id.sign_up_membership_code);
        membershipCodeMessage = (TextView) view.findViewById(R.id.sign_up_membership_code_message);
        signUp = (Button) view.findViewById(R.id.sign_up);
        togglePassword = (ImageButton) view.findViewById(R.id.sign_up_toggle_password);

        membershipCode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        togglePassword.setVisibility(View.GONE);

        passwordPaddingRight = password.getPaddingRight();
        visibilityOff = emailValid = nameValid = passwordValid = membershipCodeValid = true;
        phoneText = PHONE_NUMBER;

        deviceID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

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

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!nameValid) {
                    nameLayout.setError(null);
                    nameLayout.setErrorEnabled(false);
                    nameValid = true;
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

        membershipCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!membershipCodeValid) {
                    membershipCodeLayout.setError(null);
                    membershipCodeLayout.setErrorEnabled(false);

                    membershipCodeValid = true;
                    membershipCodeMessage.setVisibility(View.VISIBLE);
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

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidity();
                if (emailValid && nameValid && passwordValid && membershipCodeValid) {
                    registerStep3();
                } else {
                    focus.requestFocus();
                    if (!passwordValid) {
                        password.setPadding(password.getPaddingLeft(), password.getPaddingTop(), email.getPaddingRight(), password.getPaddingBottom());
                        togglePassword.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void registerStep3() {
        customProgressDialog = new CustomProgressDialog(getContext());
        customProgressDialog.setMessage("Membuat akun...");

        if (Connectivity.isConnected(getContext())) {
            try {
                encryptedPhone = sCrypt.bytesToHex(sCrypt.encrypt(phoneText));
                encryptedPassword = sCrypt.bytesToHex(sCrypt.encrypt(passwordText));
            } catch (Exception e) {
            }
            authPresenter.doRegisterStep3(encryptedPhone, nameText, emailText, encryptedPassword, membershipCodeText, deviceID, getContext(), this);
        } else {
            customProgressDialog.dismiss();

            ((ActivityRegistration) getActivity()).showNotificationNetwork(
                    new ActivityRegistration.INoInternet() {
                        @Override
                        public void onRetry() {
                            registerStep3();
                        }
                    }
            );
        }
    }

    private void checkValidity() {
        emailText = email.getText().toString().trim();
        nameText = name.getText().toString().trim();
        passwordText = password.getText().toString().trim();
        membershipCodeText = membershipCode.getText().toString().trim();

        if (TextUtils.isEmpty(passwordText)) {
            passwordLayout.setError("Field password harus diisikan");
            focus = password;
            passwordValid = false;
        } else if (!isPasswordValid(passwordText)) {
            passwordLayout.setError("Panjang password harus terdiri dari minimal 6 karakter");
            focus = password;
            passwordValid = false;
        } else {
            passwordValid = true;
        }

        if (TextUtils.isEmpty(nameText)) {
            nameLayout.setError("Field nama lengkap harus diisikan");
            focus = name;
            nameValid = false;
        } else if (!isNameValid(nameText)) {
            nameLayout.setError("Nama lengkap tidak dapat mengandung karakter khusus");
            focus = name;
            nameValid = false;
        } else {
            nameValid = true;
        }

        if (TextUtils.isEmpty(emailText)) {
            emailLayout.setError("Field email harus diisikan");
            focus = email;
            emailValid = false;
        } else if (!isEmailValid(emailText)) {
            emailLayout.setError("Alamat email yang dimasukkan tidak valid");
            focus = email;
            emailValid = false;
        } else {
            emailValid = true;
        }
    }

    private boolean isEmailValid(String email) {
        return email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    }

    private boolean isNameValid(String name) {
        return name.matches("^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    @Override
    public void onSuccess(Response response) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }

        ResponseAuth responseAuth = (ResponseAuth) response;
        Session.with(getContext()).createSessionLogin(responseAuth.getData().getAuth());
        Session.with(getContext()).createSessionToken(responseAuth.getData().getToken());

        intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onFailure(String message) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }

        if (this.isVisible()) {
            toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            ((ActivityRegistration) getActivity()).applicationTool.resizeToast(toast);
            toast.show();
        }
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

                focus.requestFocus();
                break;
            } else if (error.getType() == Error.ERROR_MEMBER_CODE) {
                membershipCodeLayout.setError(error.getMessage());
                focus = membershipCode;

                membershipCodeValid = false;
                membershipCodeMessage.setVisibility(View.GONE);

                focus.requestFocus();
                break;
            }
        }
    }
}