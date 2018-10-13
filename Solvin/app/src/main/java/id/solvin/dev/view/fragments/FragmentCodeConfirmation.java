package id.solvin.dev.view.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.SCrypt;
import id.solvin.dev.model.basic.Error;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.SMSBus;
import id.solvin.dev.model.basic.Transfer;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.interfaces.OnErrors;
import id.solvin.dev.presenter.AuthPresenter;
import id.solvin.dev.view.widget.CustomProgressDialog;
import id.solvin.dev.R;
import id.solvin.dev.view.activities.ActivityRegistration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import static android.R.id.message;

/**
 * Created by Erick Sumargo on 9/3/2016.
 */
public class FragmentCodeConfirmation extends Fragment implements IBaseResponse, OnErrors {
    private View view;
    public EditText codeField;
    private LinearLayout errorMessageContainer;
    private TextView mobileText, timer, errorMessage, resendCode;
    private Button confirmation;
    private Toast toast;

    public CountDownTimer countDownTimer;
    private CustomProgressDialog customProgressDialog;
    private SpannableStringBuilder spannableStringBuilder;

    private boolean codeValid = true, codeResent = false;
    public boolean isTimerRunning = false;

    private AuthPresenter authPresenter;

    public static String PHONE_NUMBER = "";
    private String encryptedMobile;
    private FragmentMobileConfirmation mobileConfirmation;
    private SCrypt sCrypt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_code_confirmation, container, false);
        init();
        setEvent();

        return view;
    }

    private void init() {
        authPresenter = new AuthPresenter(this);
        codeField = (EditText) view.findViewById(R.id.cconfirmation_code_field);
        errorMessageContainer = (LinearLayout) view.findViewById(R.id.cconfirmation_error_message_container);
        errorMessage = (TextView) view.findViewById(R.id.cconfirmation_error_message);
        mobileText = (TextView) view.findViewById(R.id.cconfirmation_mobile_text);
        timer = (TextView) view.findViewById(R.id.cconfirmation_timer);
        resendCode = (TextView) view.findViewById(R.id.cconfirmation_resend_code);
        confirmation = (Button) view.findViewById(R.id.cconfirmation_confirmation);

        spannableStringBuilder = new SpannableStringBuilder(resendCode.getText());
        spannableStringBuilder.setSpan(new UnderlineSpan(), 0, spannableStringBuilder.length(), 0);
        resendCode.setText(spannableStringBuilder);

        confirmation.setEnabled(false);
        confirmation.setBackgroundResource(R.drawable.primary_button_disabled);
        confirmation.setPadding((int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m));

        mobileConfirmation = new FragmentMobileConfirmation();

        sCrypt = SCrypt.getInstance();
    }

    private void setEvent() {
        codeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    confirmation.setEnabled(true);
                    confirmation.setBackgroundResource(R.drawable.primary_button);
                } else {
                    confirmation.setEnabled(false);
                    confirmation.setBackgroundResource(R.drawable.primary_button_disabled);
                }
                confirmation.setPadding((int) getResources().getDimension(R.dimen.default_padding_0m),
                        (int) getResources().getDimension(R.dimen.default_padding_0m),
                        (int) getResources().getDimension(R.dimen.default_padding_0m),
                        (int) getResources().getDimension(R.dimen.default_padding_0m));
                if (!codeValid) {
                    errorMessageContainer.setVisibility(View.GONE);
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
                try {
                    encryptedMobile = sCrypt.bytesToHex(sCrypt.encrypt(PHONE_NUMBER));
                } catch (Exception e) {
                }
                authPresenter.doRegisterStep1(encryptedMobile, "resend_code", getContext(), FragmentCodeConfirmation.this);

                toast = Toast.makeText(getActivity(), "Kode verifikasi telah dikirimkan", Toast.LENGTH_SHORT);
                ((ActivityRegistration) getActivity()).applicationTool.resizeToast(toast);
                toast.show();

                if (!codeResent) {
                    setCountDownTimer();
                }
            }
        });

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerStep2();
            }
        });

        mobileConfirmation.setOnPassMobileListener(new FragmentMobileConfirmation.OnPassMobile() {
            @Override
            public void OnPassMobile(String mobile) {
                mobileText.setText(mobile);
                PHONE_NUMBER = mobile;
            }
        });
    }

    private void registerStep2() {
        customProgressDialog = new CustomProgressDialog(getContext());
        customProgressDialog.setMessage("Verifikasi kode...");

        if (Connectivity.isConnected(getContext())) {
            try {
                encryptedMobile = sCrypt.bytesToHex(sCrypt.encrypt(PHONE_NUMBER));
            } catch (Exception e) {
            }
            authPresenter.doRegisterStep2(encryptedMobile, codeField.getText().toString(), getContext(), this);
        } else {
            customProgressDialog.dismiss();

            ((ActivityRegistration) getActivity()).showNotificationNetwork(
                    new ActivityRegistration.INoInternet() {
                        @Override
                        public void onRetry() {
                            registerStep2();
                        }
                    }
            );
        }
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

    public void resetCountDownTimer() {
        timer.setVisibility(View.GONE);

        resendCode.setEnabled(true);
        resendCode.setAlpha(1f);
        resendCode.setTextColor(getResources().getColor(R.color.colorPrimary));

        codeResent = false;
        isTimerRunning = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEventSubscribe(Transfer transfer) {
        mobileText.setText(transfer.getText());
        PHONE_NUMBER = transfer.getText();
    }

    @Subscribe
    public void onEventSubscribe(SMSBus smsBus) {
        if (this.isVisible()) {
            if (smsBus.getNumber() != null) {
                codeField.setText(smsBus.getNumber());
                codeField.setSelection(codeField.length());
                registerStep2();
            }
        }
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onSuccess(Response response) {
        if (response.getTag() == null) {
            if (customProgressDialog != null) {
                customProgressDialog.dismiss();
            }
            ((ActivityRegistration) getActivity()).customViewPager.setCurrentItem(2);

            if (isTimerRunning) {
                countDownTimer.cancel();
                resetCountDownTimer();
            }
            codeField.getText().clear();
        } else {
            if (response.getTag().equals("resend_code")) {
            }
        }
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
            if (error.getType() == Error.ERROR_CODE) {
                errorMessage.setText(error.getMessage());
                errorMessageContainer.setVisibility(View.VISIBLE);
                codeValid = false;

                break;
            }
        }
    }
}