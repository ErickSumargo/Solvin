package id.solvin.dev.view.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.R;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.SCrypt;
import id.solvin.dev.model.basic.MonthlyBalance;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.response.ResponseMonthlyBalance;
import id.solvin.dev.model.response.ResponseRedeemBalance;
import id.solvin.dev.presenter.TransactionPresenter;
import id.solvin.dev.view.activities.MainActivity;
import id.solvin.dev.view.adapters.BalanceInfoDetailAdapter;
import id.solvin.dev.view.widget.CustomAlertDialog;
import id.solvin.dev.view.widget.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by Erick Sumargo on 1/18/2017.
 */

public class FragmentBalanceInfo extends Fragment implements IBaseResponse {
    private View view, focus;
    private CoordinatorLayout mainLayout;
    private NestedScrollView container;
    private RecyclerView balanceInfoDetailView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView balanceAmount, balanceTotal;
    private LinearLayout detailContainer;
    private CircularProgressBar loadingView;
    private Button redeemBalance;
    private AlertDialog.Builder redeemBalanceBuilder;
    private AlertDialog redeemBalanceDialog;
    private Toast toast;

    private TextInputLayout securityCodeLayout, nominalLayout;
    private AutoCompleteTextView securityCode, nominal;
    private TextView securityCodeMessage, nominalMessage;

    private CustomAlertDialog customAlertDialog;
    private CustomProgressDialog customProgressDialog;

    private List<MonthlyBalance> balanceInfoDetailList;
    private BalanceInfoDetailAdapter balanceInfoDetailAdapter;

    private String securityCodeText, nominalText;
    private String encryptedSecurityCode, balance;
    private boolean securityCodeValid = false, nominalValid = false;

    private TransactionPresenter transactionPresenter;

    private int page = 1;
    private ResponseRedeemBalance responseRedeemBalance;
    private ResponseMonthlyBalance responseMonthlyBalance;

    private SCrypt sCrypt;

    public FragmentBalanceInfo() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_balance_info, container, false);

        init();
        setEvent();
        getSummaryRedeem();

        return view;
    }

    private void init() {
        mainLayout = (CoordinatorLayout) view.findViewById(R.id.balance_info_main_layout);
        container = (NestedScrollView) view.findViewById(R.id.balance_info_container);
        balanceInfoDetailView = (RecyclerView) view.findViewById(R.id.balance_info_detail_view);
        balanceInfoDetailList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        balanceInfoDetailView.setLayoutManager(layoutManager);
        balanceInfoDetailView.setNestedScrollingEnabled(false);

        balanceInfoDetailAdapter = new BalanceInfoDetailAdapter(balanceInfoDetailView, balanceInfoDetailList);
        balanceInfoDetailView.setAdapter(balanceInfoDetailAdapter);

        balanceAmount = (TextView) view.findViewById(R.id.balance_info_balance_amount);
        balanceTotal = (TextView) view.findViewById(R.id.balance_info_balance_total);
        redeemBalance = (Button) view.findViewById(R.id.balance_info_redeem_balance);

        loadingView = (CircularProgressBar) view.findViewById(R.id.balance_info_progress_view);
        detailContainer = (LinearLayout) view.findViewById(R.id.balance_info_detail_container);

        transactionPresenter = new TransactionPresenter(this);
        sCrypt = SCrypt.getInstance();
    }

    private void setEvent() {
        redeemBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRedeemBalanceDialog();
            }
        });

        balanceInfoDetailAdapter.setOnLoadMoreListener(new BalanceInfoDetailAdapter.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                fetchPageData();
            }
        });
    }

    private void showRedeemBalanceDialog() {
        redeemBalanceBuilder = new AlertDialog.Builder(getActivity());
        redeemBalanceBuilder.setPositiveButton("Tebus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        redeemBalanceBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        redeemBalanceDialog = redeemBalanceBuilder.create();
        redeemBalanceDialog.setView(redeemBalanceDialog.getLayoutInflater().inflate(R.layout.activity_redeem_balance, null));
        redeemBalanceDialog.setCanceledOnTouchOutside(false);
        redeemBalanceDialog.show();
        ((MainActivity) getActivity()).applicationTool.resizeAlertDialog(redeemBalanceDialog);

        securityCodeLayout = (TextInputLayout) redeemBalanceDialog.findViewById(R.id.redeem_balance_security_code_layout);
        nominalLayout = (TextInputLayout) redeemBalanceDialog.findViewById(R.id.redeem_balance_nominal_layout);
        securityCode = (AutoCompleteTextView) redeemBalanceDialog.findViewById(R.id.redeem_balance_security_code);
        nominal = (AutoCompleteTextView) redeemBalanceDialog.findViewById(R.id.redeem_balance_nominal);
        securityCodeMessage = (TextView) redeemBalanceDialog.findViewById(R.id.redeem_balance_security_code_message);
        nominalMessage = (TextView) redeemBalanceDialog.findViewById(R.id.redeem_balance_nominal_message);

        securityCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!securityCodeValid) {
                    securityCodeLayout.setError(null);
                    securityCodeLayout.setErrorEnabled(false);
                    securityCodeMessage.setVisibility(View.VISIBLE);
                    securityCodeValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        nominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!nominalValid) {
                    nominalLayout.setError(null);
                    nominalLayout.setErrorEnabled(false);
                    nominalMessage.setVisibility(View.VISIBLE);
                    nominalValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        redeemBalanceDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redeemBalanceDialog.dismiss();
            }
        });

        redeemBalanceDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidity();
                if (securityCodeValid && nominalValid) {
                    customAlertDialog = new CustomAlertDialog(getContext());
                    customAlertDialog.setTitle("Konfirmasi");
                    customAlertDialog.setMessage("Anda akan mengajukan permohonan penebusan saldo?");
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

                            tryRedeemBalance();
                        }
                    });
                } else {
                    focus.requestFocus();
                }
            }
        });
    }

    private void getSummaryRedeem() {
        if (Connectivity.isConnected(getContext())) {
            transactionPresenter.getSummaryRedeem(getContext());
        } else {
            ((MainActivity) getActivity()).showNotificationNetwork(
                    new MainActivity.INoInternet() {
                        @Override
                        public void onRetry() {
                            getSummaryRedeem();
                        }
                    }
            );
        }
    }

    private void fetchPageData() {
        if (Connectivity.isConnected(getContext())) {
            transactionPresenter.getMonthlyBalance(page, getContext());
        } else {
            ((MainActivity) getActivity()).showNotificationNetwork(
                    new MainActivity.INoInternet() {
                        @Override
                        public void onRetry() {
                            fetchPageData();
                        }
                    }
            );
        }
    }

    private void tryRedeemBalance() {
        customProgressDialog = new CustomProgressDialog(getContext());
        customProgressDialog.setMessage("Mengajukan permohonan penebusan saldo...");

        if (Connectivity.isConnected(getContext())) {
            try {
                encryptedSecurityCode = sCrypt.bytesToHex(sCrypt.encrypt(securityCodeText));
                balance = sCrypt.bytesToHex(sCrypt.encrypt((nominalText)));
            } catch (Exception e) {
            }
            transactionPresenter.redeemBalance(encryptedSecurityCode, balance, customProgressDialog, getContext());
        } else {
            customProgressDialog.dismiss();

            toast = Toast.makeText(getActivity(), getResources().getString(R.string.text_no_internet), Toast.LENGTH_SHORT);
            ((MainActivity) getActivity()).applicationTool.resizeToast(toast);
            toast.show();
        }
    }

    private void showNotificationRequestSent(final String message) {
        customProgressDialog.dismiss();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Snackbar snackbar = Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG);
                ((MainActivity) getActivity()).applicationTool.resizeSnackBar(snackbar, 1);
                snackbar.show();
            }
        }, 500);
    }

    private void checkValidity() {
        securityCodeText = securityCode.getText().toString().trim();
        nominalText = nominal.getText().toString().trim();

        if (TextUtils.isEmpty(securityCodeText)) {
            securityCodeMessage.setVisibility(View.GONE);
            securityCodeLayout.setError("Harap masukkan kode pengaman yang anda miliki");
            focus = securityCodeLayout;
            securityCodeValid = false;
        } else {
            securityCodeValid = true;
        }

        if (TextUtils.isEmpty(nominalText) && securityCodeValid) {
            nominalMessage.setVisibility(View.GONE);
            nominalLayout.setError("Harap masukkan nominal yang ingin anda tebus");
            focus = nominalLayout;
            nominalValid = false;
        } else if (!TextUtils.isEmpty(nominalText)) {
            if (Integer.parseInt(nominalText) < 20000 && securityCodeValid) {
                nominalMessage.setVisibility(View.GONE);
                nominalLayout.setError("Nominal yang ditebus minimal Rp. 20.000,-");
                focus = nominalLayout;
                nominalValid = false;
            } else if (Integer.parseInt(nominalText) > ((MainActivity) getActivity()).mentor.getBalanceAmount() && securityCodeValid) {
                nominalMessage.setVisibility(View.GONE);
                nominalLayout.setError("Nominal tebusan harus <= dari sisa saldo yang anda punya");
                focus = nominalLayout;
                nominalValid = false;
            } else {
                nominalValid = true;
            }
        }
    }

    @Override
    public void onSuccess(Response response) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }
        if (response instanceof ResponseRedeemBalance) {
            responseRedeemBalance = (ResponseRedeemBalance) response;
            balanceAmount.setText(((MainActivity) getActivity()).applicationTool.convertRpCurrency(
                    responseRedeemBalance.getData().getTotalBalance() + responseRedeemBalance.getData().getBalanceBonus() - responseRedeemBalance.getData().getBalanceRedeemed())
            );
            balanceTotal.setText(((MainActivity) getActivity()).applicationTool.convertRpCurrency(
                    responseRedeemBalance.getData().getTotalBalance() + responseRedeemBalance.getData().getBalanceBonus()));

            fetchPageData();

            container.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);

        } else if (response instanceof ResponseMonthlyBalance) {
            responseMonthlyBalance = (ResponseMonthlyBalance) response;
            final int size = responseMonthlyBalance.getMonthlyBalanceList().size();
            if (size > 0) {
                if (page == 1) {
                    balanceInfoDetailList = responseMonthlyBalance.getMonthlyBalanceList();
                } else {
                    balanceInfoDetailList.addAll(responseMonthlyBalance.getMonthlyBalanceList());
                }
                balanceInfoDetailAdapter.updateList(balanceInfoDetailList);
                balanceInfoDetailAdapter.setLoaded();

                page++;
            } else {
                balanceInfoDetailAdapter.updateList(balanceInfoDetailList);
            }
            if (page == 2 && balanceInfoDetailList.size() > 0) {
                detailContainer.setVisibility(View.VISIBLE);
            }
        } else {
            if (response.isSuccess()) {
                redeemBalanceDialog.dismiss();
                showNotificationRequestSent(response.getMessage());
            } else {
                securityCodeMessage.setVisibility(View.GONE);
                securityCodeLayout.setError("Kode pengaman anda salah. Silahkan coba lagi");
                focus = securityCodeLayout;
                securityCodeValid = false;

                focus.requestFocus();
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
            ((MainActivity) getActivity()).applicationTool.resizeToast(toast);
            toast.show();
        }
    }
}