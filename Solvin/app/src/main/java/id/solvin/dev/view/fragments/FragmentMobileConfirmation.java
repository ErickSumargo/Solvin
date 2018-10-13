package id.solvin.dev.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.R;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.SCrypt;
import id.solvin.dev.model.basic.Error;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.Transfer;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.interfaces.OnErrors;
import id.solvin.dev.presenter.AuthPresenter;
import id.solvin.dev.view.activities.ActivityPrivacyPolicy;
import id.solvin.dev.view.activities.ActivityRegistration;
import id.solvin.dev.view.activities.ActivityTermsCondition;
import id.solvin.dev.view.widget.CustomProgressDialog;
import id.solvin.dev.view.widget.CustomAlertDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Erick Sumargo on 9/3/2016.
 */
public class FragmentMobileConfirmation extends Fragment implements IBaseResponse,OnErrors {
    //    INTERFACE
    public interface OnPassMobile {
        void OnPassMobile(String mobile);
    }

    private static OnPassMobile mPassMobile;

    public void setOnPassMobileListener(OnPassMobile listener) {
        mPassMobile = listener;
    }

    private void passMobile(String mobile) {
        if (mPassMobile != null) {
            mPassMobile.OnPassMobile(mobile);
        }
    }

    private View view;
    private CoordinatorLayout mainLayout;
    private LinearLayout errorMessageContainer;
    private EditText mobileField;
    private TextView textTerms, errorMessage;
    private Button send;
    private Intent intent;
    private Toast toast;

    private CustomAlertDialog customAlertDialog;
    private CustomProgressDialog customProgressDialog;

    private SpannableStringBuilder linkTerms;
    private ClickableSpan clickableTermsConditionSpan, clickablePrivacyPolicySpan;

    private boolean mobileValid = true;
    private static final int REQUEST_SMS_PERMISSIONS = 0;
    private String mobileText, encryptedMobile;
    private String[] SMS_PERMISSIONS = {
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.READ_SMS};

    private AuthPresenter authPresenter;
    private SCrypt sCrypt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_mobile_confirmation, container, false);
        init();
        setEvent();

        return view;
    }

    private void init() {
        authPresenter = new AuthPresenter(this);

        mainLayout = (CoordinatorLayout) view.findViewById(R.id.mconfirmation_main_layout);
        errorMessageContainer = (LinearLayout) view.findViewById(R.id.mconfirmation_error_message_container);
        mobileField = (EditText) view.findViewById(R.id.mconfirmation_mobile_field);
        textTerms = (TextView) view.findViewById(R.id.mconfirmation_text_terms);
        errorMessage = (TextView) view.findViewById(R.id.mconfirmation_error_message);
        send = (Button) view.findViewById(R.id.mconfirmation_send);

        textTerms.setText(Html.fromHtml(getString(R.string.text_terms)));
        linkTerms = new SpannableStringBuilder(textTerms.getText());
        clickableTermsConditionSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                intent = new Intent(getActivity(), ActivityTermsCondition.class);
                startActivity(intent);
            }
        };
        clickablePrivacyPolicySpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                intent = new Intent(getActivity(), ActivityPrivacyPolicy.class);
                startActivity(intent);
            }
        };
        linkTerms.setSpan(clickableTermsConditionSpan, 25, 43, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        linkTerms.setSpan(clickablePrivacyPolicySpan, 84, 101, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textTerms.setText(linkTerms);
        textTerms.setMovementMethod(LinkMovementMethod.getInstance());

        send.setEnabled(false);
        send.setBackgroundResource(R.drawable.primary_button_disabled);
        send.setPadding((int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m));

        sCrypt = SCrypt.getInstance();
    }

    private void setEvent() {
        mobileField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    send.setEnabled(true);
                    send.setBackgroundResource(R.drawable.primary_button);
                } else {
                    send.setEnabled(false);
                    send.setBackgroundResource(R.drawable.primary_button_disabled);
                }
                send.setPadding((int) getResources().getDimension(R.dimen.default_padding_0m),
                        (int) getResources().getDimension(R.dimen.default_padding_0m),
                        (int) getResources().getDimension(R.dimen.default_padding_0m),
                        (int) getResources().getDimension(R.dimen.default_padding_0m));
                if (!mobileValid) {
                    mobileValid = true;
                    errorMessageContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidity();
                if (mobileValid) {
                    if (!hasPermissions(getContext(), SMS_PERMISSIONS)) {
                        requestPermissions(SMS_PERMISSIONS, REQUEST_SMS_PERMISSIONS);
                    } else {
                        processRequest();
                    }
                }
            }
        });
    }

    private void processRequest() {
        customAlertDialog = new CustomAlertDialog(getContext());
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

                registerStep1();
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

    private void registerStep1() {
        customProgressDialog = new CustomProgressDialog(getContext());
        customProgressDialog.setMessage("Memeriksa...");

        if (Connectivity.isConnected(getContext())) {
            try {
                encryptedMobile = sCrypt.bytesToHex(sCrypt.encrypt(mobileText));
            } catch (Exception e) {
            }
            authPresenter.doRegisterStep1(encryptedMobile, "", getContext(), this);
        } else {
            customProgressDialog.dismiss();

            ((ActivityRegistration) getActivity()).showNotificationNetwork(
                    new ActivityRegistration.INoInternet() {
                        @Override
                        public void onRetry() {
                            registerStep1();
                        }
                    }
            );
        }
    }

    private void checkValidity() {
        mobileText = mobileField.getText().toString().trim();
        if (!isMobileValid(mobileText)) {
            errorMessage.setText("Format no. hp salah");
            errorMessageContainer.setVisibility(View.VISIBLE);

            mobileValid = false;
        }
    }

    private boolean isMobileValid(String mobile) {
        return (mobile.length() > 9 && mobile.charAt(0) == '0');
    }

    @Override
    public void onSuccess(Response response) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }
        EventBus.getDefault().post(new Transfer(mobileField.getText().toString()));
        passMobile(mobileField.getText().toString());
        if (response.getMessage().equals("skip-1-2")) {
            ((ActivityRegistration) getActivity()).customViewPager.setCurrentItem(2);
        } else {
            ((ActivityRegistration) getActivity()).customViewPager.setCurrentItem(1);
        }
        mobileField.getText().clear();
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
            if (error.getType() == Error.ERROR_MOBILE) {
                errorMessage.setText(error.getMessage());
                errorMessageContainer.setVisibility(View.VISIBLE);
                mobileValid = false;

                break;
            }
        }
    }
}