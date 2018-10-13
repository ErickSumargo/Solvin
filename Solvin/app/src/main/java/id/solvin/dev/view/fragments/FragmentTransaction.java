package id.solvin.dev.view.fragments;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import id.solvin.dev.R;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.SCrypt;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Bank;
import id.solvin.dev.model.basic.MobileNetwork;
import id.solvin.dev.model.basic.Paket;
import id.solvin.dev.model.basic.Payment;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.Transaction;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.response.ResponseTransaction;
import id.solvin.dev.presenter.TransactionPresenter;
import id.solvin.dev.view.activities.ActivityTransactionStatus;
import id.solvin.dev.view.activities.MainActivity;
import id.solvin.dev.view.adapters.BankViewAdapter;
import id.solvin.dev.view.adapters.MobileNetworkViewAdapter;
import id.solvin.dev.view.adapters.PackageViewAdapter;
import id.solvin.dev.view.widget.CustomAlertDialog;
import id.solvin.dev.view.widget.CustomProgressDialog;

/**
 * Created by Erick Sumargo on 9/3/2016.
 */
public class FragmentTransaction extends Fragment implements IBaseResponse {
    private View view;
    private CoordinatorLayout mainLayout;
    private LinearLayout bankTransferGroup, pulseGroup;
    private android.widget.RadioButton bankTransferRadio, pulseRadio;
    private Button transactionStatus;
    private NestedScrollView viewTransactionMainLayout;
    private CircularProgressBar progressView;
    private Toast toast;

    private android.app.Notification notification;
    private Notification.Builder noticationBuilder;
    private NotificationManager notificationManager;
    private Intent intent;
    private CustomAlertDialog customAlertDialog;
    private CustomProgressDialog customProgressDialog;

    private TransactionPresenter transactionPresenter;

    private RecyclerView packageView, bankView, mobileNetworkView;
    private LinearLayoutManager layoutManager;

    private Payment payment;
    private List<Paket> packageList;
    private List<Bank> bankList;
    private List<MobileNetwork> mobileNetworkList;

    private PackageViewAdapter packageViewAdapter;
    private BankViewAdapter bankViewAdapter;
    private MobileNetworkViewAdapter mobileNetworkViewAdapter;

    private int packageID, bankID, mobileNetworkID;
    private String packageIDText, bankIDText, mobileNetworkIDText, uniqueCodeText;
    private SCrypt sCrypt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_transaction, container, false);
        init();
        setEvent();
        checkTransaction();

        return view;
    }

    private void init() {
        this.transactionPresenter = new TransactionPresenter(this);
        if (Build.VERSION.SDK_INT < 21) {
            view.findViewById(R.id.shadow_view).setVisibility(View.GONE);
        }

        mainLayout = (CoordinatorLayout) view.findViewById(R.id.transaction_main_layout);
        bankTransferGroup = (LinearLayout) view.findViewById(R.id.transaction_bank_transfer_method_group);
        pulseGroup = (LinearLayout) view.findViewById(R.id.transaction_pulse_method_group);

        bankTransferRadio = (android.widget.RadioButton) view.findViewById(R.id.transaction_bank_transfer_radio);
        pulseRadio = (android.widget.RadioButton) view.findViewById(R.id.transaction_pulse_radio);

        packageView = (RecyclerView) view.findViewById(R.id.transaction_package_view);
        packageList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        packageView.setLayoutManager(layoutManager);
        packageView.setNestedScrollingEnabled(false);

        bankView = (RecyclerView) view.findViewById(R.id.transaction_bank_view);
        bankList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        bankView.setLayoutManager(layoutManager);
        bankView.setNestedScrollingEnabled(false);

        mobileNetworkView = (RecyclerView) view.findViewById(R.id.transaction_mobile_network_view);
        mobileNetworkList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        mobileNetworkView.setLayoutManager(layoutManager);
        mobileNetworkView.setNestedScrollingEnabled(false);

        transactionStatus = (Button) view.findViewById(R.id.transaction_status);

        progressView = (CircularProgressBar) view.findViewById(R.id.transaction_progress_view);
        viewTransactionMainLayout = (NestedScrollView) view.findViewById(R.id.main_transaction_layout);

        sCrypt = new SCrypt();
    }

    private void uncheckBankTransferRadio() {
        bankViewAdapter.unselectRadio();
    }

    private void uncheckPulseRadio() {
        mobileNetworkViewAdapter.unselectRadio();
    }

    private void setEvent() {
        bankTransferGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bankView.setVisibility(View.VISIBLE);
                mobileNetworkView.setVisibility(View.GONE);

                bankTransferGroup.setBackgroundResource(R.drawable.custom_radio_selected);
                bankTransferRadio.setChecked(true);

                pulseGroup.setBackgroundResource(R.drawable.custom_background_pressed);
                pulseRadio.setChecked(false);

                bankID = mobileNetworkID = 0;

                uncheckBankTransferRadio();
                uncheckPulseRadio();
                setButtonDisabled();
            }
        });

        pulseGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bankView.setVisibility(View.GONE);
                mobileNetworkView.setVisibility(View.VISIBLE);

                bankTransferGroup.setBackgroundResource(R.drawable.custom_background_pressed);
                bankTransferRadio.setChecked(false);

                pulseGroup.setBackgroundResource(R.drawable.custom_radio_selected);
                pulseRadio.setChecked(true);

                bankID = mobileNetworkID = 0;

                uncheckBankTransferRadio();
                uncheckPulseRadio();
                setButtonDisabled();
            }
        });

        transactionStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog = new CustomAlertDialog(getContext());
                customAlertDialog.setTitle("Konfirmasi");
                customAlertDialog.setMessage("Lanjut ke halaman rincian transfer pembayaran?");
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

                        buyPackage();
                    }
                });
            }
        });
    }

    private void checkTransaction() {
        if (Connectivity.isConnected(getContext())) {
            transactionPresenter.checkTransaction(getContext());
        } else {
            ((MainActivity) getActivity()).showNotificationNetwork(
                    new MainActivity.INoInternet() {
                        @Override
                        public void onRetry() {
                            checkTransaction();
                        }
                    }
            );
        }
    }

    private void buyPackage() {
        customProgressDialog = new CustomProgressDialog(getContext());
        customProgressDialog.setMessage("Memuat rincian transfer pembayaran...");

        if (Connectivity.isConnected(getContext())) {
            try {
                packageIDText = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(packageID)));
                bankIDText = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(bankID)));
                mobileNetworkIDText = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(mobileNetworkID)));
                uniqueCodeText = sCrypt.bytesToHex(sCrypt.encrypt(String.valueOf(new Random().nextInt(900 - 20 + 1) + 20)));
            } catch (Exception e) {
            }
            transactionPresenter.buyPackage(packageIDText, bankIDText, mobileNetworkIDText, uniqueCodeText, getContext());
        } else {
            customProgressDialog.dismiss();

            ((MainActivity) getActivity()).showNotificationNetwork(
                    new MainActivity.INoInternet() {
                        @Override
                        public void onRetry() {
                            buyPackage();
                        }
                    }
            );
        }
    }

    private void createNotification(String created_at) {
        noticationBuilder = new Notification.Builder(getActivity());
        if (Build.VERSION.SDK_INT >= 21) {
            noticationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        }
        if (Build.VERSION.SDK_INT > 15) {
            noticationBuilder.setSmallIcon(R.drawable.ic_solvin_notification)
                    .setContentTitle("Transfer Pembayaran")
                    .setContentText("Harap lakukan pembayaran sebelum deadline waktu: " + Global.get().getExpiredPayment(created_at) + "WIB")
                    .setStyle(new Notification.BigTextStyle(noticationBuilder)
                            .setBigContentTitle("Transfer Pembayaran")
                            .bigText("Harap lakukan pembayaran sebelum deadline waktu: " + Global.get().getExpiredPayment(created_at) + "WIB"));
            notification = noticationBuilder.build();
        }

        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    private void setButtonEnabled() {
        transactionStatus.setEnabled(true);
        transactionStatus.setBackgroundResource(R.drawable.primary_button);
        transactionStatus.setPadding((int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m));
    }

    private void setButtonDisabled() {
        transactionStatus.setEnabled(false);
        transactionStatus.setBackgroundResource(R.drawable.primary_button_disabled);
        transactionStatus.setPadding((int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m),
                (int) getResources().getDimension(R.dimen.default_padding_0m));
    }

    @Override
    public void onSuccess(Response response) {
        switch (response.getTag()) {
            case Response.TAG_PURCHASE_PACKAGE_BUY:
                if (customProgressDialog != null) {
                    customProgressDialog.dismiss();
                }
                intent = new Intent(getActivity(), ActivityTransactionStatus.class);
                intent.putExtra("packageID", packageID);
                intent.putExtra("bankID", bankID);
                intent.putExtra("mobileNetworkID", mobileNetworkID);
                intent.putExtra("transaction", ((ResponseTransaction) response).getData().getTransaction());
                getActivity().startActivityForResult(intent, Global.TRIGGGET_UPDATE);
                createNotification(((ResponseTransaction) response).getData().getTransaction().getCreated_at());
                viewTransactionMainLayout.setVisibility(View.VISIBLE);
                break;
            case Response.TAG_PURCHASE_PACKAGE_CHECK:
                if (response.isSuccess()) {
                    setDataToView();
                } else {
                    progressView.setVisibility(View.GONE);
                    Transaction transaction = ((ResponseTransaction) response).getData().getTransaction();
                    intent = new Intent(getActivity(), ActivityTransactionStatus.class);
                    intent.putExtra("packageID", transaction.getPackageId());
                    intent.putExtra("bankID", transaction.getBank_id());
                    intent.putExtra("mobileNetworkID", transaction.getMobileNetwork_id());
                    intent.putExtra("transaction", transaction);
                    getActivity().startActivityForResult(intent, Global.TRIGGGET_UPDATE);
                }
                break;
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

    private void setDataToView() {
        payment = Session.with(getContext()).getPayment();
        packageList = Session.with(getContext()).getPackage();
        bankList = Session.with(getContext()).getBanks();
        mobileNetworkList = Session.with(getContext()).getMobileNetworks();

        packageViewAdapter = new PackageViewAdapter(packageList);
        packageView.setAdapter(packageViewAdapter);

        bankViewAdapter = new BankViewAdapter(bankList);
        bankView.setAdapter(bankViewAdapter);

        mobileNetworkViewAdapter = new MobileNetworkViewAdapter(mobileNetworkList);
        mobileNetworkView.setAdapter(mobileNetworkViewAdapter);

        packageViewAdapter.getCheckedPackagePosition(new PackageViewAdapter.OnSetCheckedPackagePosition() {
            @Override
            public void IGetCheckedPackagePosition(int position) {
                packageID = position + 1;
                if (bankID != 0 || mobileNetworkID != 0) {
                    setButtonEnabled();
                }
            }
        });

        bankViewAdapter.getCheckedBankPosition(new BankViewAdapter.OnSetCheckedBankPosition() {
            @Override
            public void IGetCheckedBankPosition(int position) {
                bankID = position + 1;
                if (packageID != 0) {
                    setButtonEnabled();
                }
            }
        });

        mobileNetworkViewAdapter.getCheckedMobileNetworkPosition(new MobileNetworkViewAdapter.OnSetCheckedMobileNetworkPosition() {
            @Override
            public void IGetCheckedMobileNetworkPosition(int position) {
                mobileNetworkID = position + 1;
                if (packageID != 0) {
                    setButtonEnabled();
                }
            }
        });

        setButtonDisabled();

        packageID = bankID = mobileNetworkID = 0;
        viewTransactionMainLayout.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == Global.TRIGGGET_UPDATE) {
                ((MainActivity) getActivity()).setFragmentPosition(0);
            }
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Global.TRIGGGET_UPDATE) {
                ((MainActivity) getActivity()).setFragmentPosition(0);
            }
        }
    }
}