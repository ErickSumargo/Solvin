package id.solvin.dev.view.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.presenter.AuthPresenter;
import id.solvin.dev.view.widget.CustomAlertDialog;
import id.solvin.dev.view.widget.CustomProgressDialog;
import id.solvin.dev.R;
import id.solvin.dev.view.activities.MainActivity;

/**
 * Created by Erick Sumargo on 9/6/2016.
 */
public class FragmentFeedback extends Fragment implements IBaseResponse {
    //    VIEW
    private View view, focus;
    private CoordinatorLayout mainLayout;
    private TextInputLayout titleLayout, contentLayout;
    private TextView sendToLabel, titleLabel, contentLabel;
    private AutoCompleteTextView title, content;
    private Button send;

    private CustomAlertDialog customAlertDialog;
    private CustomProgressDialog customProgressDialog;
    private Toast toast;
    private Snackbar snackbar;

    //    HELPER
    private InputMethodManager inputMethodManager;

    //    OBJECT
    private AuthPresenter authPresenter;

    //    VARIABLE
    private boolean titleValid, contentValid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_feedback, container, false);
        init();
        setEvent();

        return view;
    }

    private void init() {
        //    VIEW
        if (Build.VERSION.SDK_INT < 21) {
            view.findViewById(R.id.shadow_view).setVisibility(View.GONE);
        }

        mainLayout = (CoordinatorLayout) view.findViewById(R.id.feedback_main_layout);
        titleLayout = (TextInputLayout) view.findViewById(R.id.feedback_title_layout);
        contentLayout = (TextInputLayout) view.findViewById(R.id.feedback_content_layout);
        sendToLabel = (TextView) view.findViewById(R.id.feedback_send_to_label);
        titleLabel = (TextView) view.findViewById(R.id.feedback_title_label);
        contentLabel = (TextView) view.findViewById(R.id.feedback_content_label);
        title = (AutoCompleteTextView) view.findViewById(R.id.feedback_title);
        content = (AutoCompleteTextView) view.findViewById(R.id.feedback_content);
        send = (Button) view.findViewById(R.id.feedback_send);

        sendToLabel.setPadding(title.getPaddingLeft(), 0, 0, 0);
        titleLabel.setPadding(title.getPaddingLeft(), 0, 0, 0);
        contentLabel.setPadding(content.getPaddingLeft(), 0, 0, 0);

        focus = null;

        //    HELPER
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        //    OBJECT
        authPresenter = new AuthPresenter(this);

        //    VARIABLE
        titleValid = contentValid = true;
    }

    private void setEvent() {
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!titleValid) {
                    titleLayout.setError(null);
                    titleLayout.setErrorEnabled(false);
                    titleValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!contentValid) {
                    contentLayout.setError(null);
                    contentLayout.setErrorEnabled(false);
                    contentValid = true;
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
                if (titleValid && contentValid) {
                    customAlertDialog = new CustomAlertDialog(getContext());
                    customAlertDialog.setTitle("Konfirmasi");
                    customAlertDialog.setMessage("Anda akan menyampaikan isi feedback yang telah dibuat?");
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
                            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                            sendFeedback();
                        }
                    });
                }
            }
        });
    }

    private void sendFeedback() {
        customProgressDialog = new CustomProgressDialog(getContext());
        customProgressDialog.setMessage("Menyampaikan feedback...");

        if (Connectivity.isConnected(getContext())) {
            authPresenter.sendFeedback(getContext(), title.getText().toString(), content.getText().toString());
        } else {
            customProgressDialog.dismiss();

            ((MainActivity) getActivity()).showNotificationNetwork(
                    new MainActivity.INoInternet() {
                        @Override
                        public void onRetry() {
                            sendFeedback();
                        }
                    }
            );
        }
    }

    private void checkValidity() {
        if (TextUtils.isEmpty(title.getText().toString().trim())) {
            titleLayout.setError("Silahkan isikan subjek dari feedback/keluhan yang ingin disampaikan");
            focus = title;
            titleValid = false;
        } else {
            titleValid = true;
        }

        if (TextUtils.isEmpty(content.getText().toString().trim())) {
            contentLayout.setError("Silahkan sampaikan isi dari feedback/keluhan");
            focus = content;
            contentValid = false;
        } else {
            contentValid = true;
        }
    }

    private void showNotificationFeedbackSent(final String message) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG);
                ((MainActivity) getActivity()).applicationTool.resizeSnackBar(snackbar, 1);
                snackbar.show();
            }
        }, 500);
    }

    @Override
    public void onSuccess(Response response) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }

        if (response.isSuccess()) {
            title.setText("");
            content.setText("");
            title.requestFocus();
            titleValid = contentValid = true;

            showNotificationFeedbackSent(response.getMessage());
        }
    }

    @Override
    public void onFailure(String message) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }

        if (this.isVisible()) {
            toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            ((MainActivity) getActivity()).applicationTool.resizeToast(toast);
            toast.show();
        }
    }
}