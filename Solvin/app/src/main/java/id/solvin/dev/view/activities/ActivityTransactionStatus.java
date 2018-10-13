package id.solvin.dev.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.ClassMediaChooser;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Bank;
import id.solvin.dev.model.basic.MobileNetwork;
import id.solvin.dev.model.basic.Paket;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.Transaction;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.response.ResponseTransaction;
import id.solvin.dev.presenter.TransactionPresenter;
import id.solvin.dev.view.widget.CustomAlertDialog;
import id.solvin.dev.view.widget.CustomProgressDialog;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.R.attr.data;

/**
 * Created by Erick Sumargo on 9/3/2016.
 */
public class ActivityTransactionStatus extends AppCompatActivity implements IBaseResponse {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    public interface OnFinishedListener {
        void OnFinished();
    }

    public interface OnTransactionCanceledListener {
        void OnTransactionCanceled();
    }

    private static OnFinishedListener mFinishedListener;

    private static OnTransactionCanceledListener mTransactionCanceledListener;

    public void setOnFinishedListener(OnFinishedListener listener) {
        mFinishedListener = listener;
    }

    public void setOnTransactionCanceledListener(OnTransactionCanceledListener listener) {
        mTransactionCanceledListener = listener;
    }

    private void finished() {
        if (mFinishedListener != null) {
            mFinishedListener.OnFinished();
        }
        finish();
    }

    private void showNotificationTransactionCanceled() {
        if (mTransactionCanceledListener != null) {
            mTransactionCanceledListener.OnTransactionCanceled();
        }
    }

    private Toolbar toolbar;
    private View focus;
    private CoordinatorLayout mainLayout;
    private ScrollView contentLayout;
    private CircularProgressBar progressView;
    private RelativeLayout uploadSpace, imageForeground;
    private LinearLayout adviceContainer;
    private TextInputLayout bankAccountUserLayout, mobileNumberUserLayout;
    private AutoCompleteTextView transactionCodeField, bankAccountUserField, mobileNumberUserField, bankFillField;
    private TextView totalCost, transactionCode, transactionStatus, packageType, costPackage, costCode,
            bankAccountOwner, dueDateTransaction, uploadStatus,
            transactionCodeLabel, bankAccountUserLabel, mobileNumberUserLabel, bankSpinnerLabel, uploadStructLabel;
    //    private TextView packageValidUntil;
    private ImageView image, imageRemove;
    private Spinner bankSpinner;
    private ProgressBar imageProgress;
    private CheckBox agreement;
    private Button paymentConfirmation, cancelTransaction, doneTransaction;
    private Toast toast;
    private Snackbar snackbar;

    private AlertDialog.Builder transactionConfirmBuilder;
    private AlertDialog transactionConfirmDialog;

    private ClassApplicationTool applicationTool;
    private CustomAlertDialog customAlertDialog;
    private CustomProgressDialog customProgressDialog;

    private static final int REQUEST_EXTERNAL_STORAGE = 1, CHOOSE_MEDIA = 0;
    private int totalCostValue, costPackageValue, costCodeValue, packageID, bankID, mobileNetworkID;
    private String action;

    private static String[] PERMISSION_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String[] bankList = {"PILIH BANK", "BCA", "BNI", "BTN", "BRI", "CIMB Niaga",
            "Danamon", "Mandiri", "Maybank Indonesia", "Panin", "Permata", "Lainnya"};
    private ArrayAdapter<String> bankListAdapter;
    private String bankAccountUserText, mobileNumberUserText;
    private String idCode, uniqueCode;

    private Intent intent, mediaChooser;
    private Uri uri;
    private ByteArrayOutputStream byteArrayOutputStream;
    private Bitmap bitmap;

    private boolean bankAccountUserValid, mobileNumberUserValid, imageAttached;
    //    private Payment payment;
    private Paket paket;
    private Bank bank;
    private MobileNetwork mobileNetwork;
    private Transaction transaction;
    private TransactionPresenter transactionPresenter;
    private int[] backgroundStatus = {R.drawable.custom_transaction_status_pending, R.drawable.custom_transaction_status_actived, R.drawable.custom_transaction_status_cancelled};
    private String[] strStatus = {"Pending", "Lunas", "Dibatalkan"};
    private List<Bank> banksList;
    private List<MobileNetwork> mobileNetworksList;
    private List<Paket> packagesList;
    private int transaction_id;

    //    Media Transfer
    private ImageView mediaTransferLogo;
    private TextView totalCostLabel, mediaTransferLabel, mediaTransferDetailLabel, mediaTransferDetail;
    private LinearLayout bankAccountOwnerContainer;
    private Button openDialView;

    private LinearLayout bankAccountUserContainer, bankSpinnerContainer, mobileNumberUserContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_status);

        transaction_id = getIntent().getIntExtra("transaction_id", -1);
        transactionPresenter = new TransactionPresenter(this);

        init();
        setEvent();
        if (transaction_id == -1) {
            packageID = getIntent().getExtras().getInt("packageID");
            bankID = getIntent().getExtras().getInt("bankID");
            mobileNetworkID = getIntent().getExtras().getInt("mobileNetworkID");
            transaction = (Transaction) getIntent().getExtras().getSerializable("transaction");

            progressView.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
            setData();
        } else {
            contentLayout.setVisibility(View.GONE);
            tryGetDetailTransaction();
        }
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = (CoordinatorLayout) findViewById(R.id.transaction_status_main_layout);
        contentLayout = (ScrollView) findViewById(R.id.transaction_status_content_layout);
        progressView = (CircularProgressBar) findViewById(R.id.transaction_status_progress_view);
        totalCostLabel = (TextView) findViewById(R.id.status_total_cost_label);
        totalCost = (TextView) findViewById(R.id.status_total_cost);
        transactionCode = (TextView) findViewById(R.id.status_transaction_code);
        transactionStatus = (TextView) findViewById(R.id.status_transaction_status);
        packageType = (TextView) findViewById(R.id.status_package_type);
        costPackage = (TextView) findViewById(R.id.status_cost_package);
        costCode = (TextView) findViewById(R.id.status_cost_code);
//        packageValidUntil = (TextView) findViewById(R.id.status_package_valid_until);
        mediaTransferDetailLabel = (TextView) findViewById(R.id.status_media_transfer_detail_label);
        mediaTransferDetail = (TextView) findViewById(R.id.status_media_transfer_detail);
        bankAccountOwnerContainer = (LinearLayout) findViewById(R.id.status_bank_account_owner_container);
        bankAccountOwner = (TextView) findViewById(R.id.status_bank_account_owner);
        dueDateTransaction = (TextView) findViewById(R.id.status_due_date_transaction);
        mediaTransferLabel = (TextView) findViewById(R.id.status_media_transfer_label);
        mediaTransferLogo = (ImageView) findViewById(R.id.status_media_transfer_logo);
        adviceContainer = (LinearLayout) findViewById(R.id.transaction_status_advice_container);
        openDialView = (Button) findViewById(R.id.status_open_dial_view);
        paymentConfirmation = (Button) findViewById(R.id.status_payment_confirmation);
        cancelTransaction = (Button) findViewById(R.id.status_cancel_transaction);
        doneTransaction = (Button) findViewById(R.id.status_done_transaction);

        applicationTool = new ClassApplicationTool(getApplicationContext());
//        payment = Session.with(getApplicationContext()).getPayment();

        banksList = Session.with(getApplicationContext()).getBanks();
        mobileNetworksList = Session.with(getApplicationContext()).getMobileNetworks();
        packagesList = Session.with(getApplicationContext()).getPackage();

        setResult(Activity.RESULT_OK);
    }

    private void showDialogConfirmation() {
        transactionConfirmBuilder = new AlertDialog.Builder(ActivityTransactionStatus.this);
        transactionConfirmBuilder.setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        transactionConfirmBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        transactionConfirmDialog = transactionConfirmBuilder.create();
        transactionConfirmDialog.setView(transactionConfirmDialog.getLayoutInflater().inflate(R.layout.activity_payment_confirmation, null));
        transactionConfirmDialog.setCanceledOnTouchOutside(false);
        transactionConfirmDialog.show();
        applicationTool.resizeAlertDialog(transactionConfirmDialog);

        transactionConfirmDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        transactionConfirmDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidity();
                if (bankAccountUserValid || mobileNumberUserValid) {
                    customAlertDialog = new CustomAlertDialog(ActivityTransactionStatus.this);
                    customAlertDialog.setTitle("Konfirmasi");
                    customAlertDialog.setMessage("Anda akan mengirimkan informasi konfirmasi pembayaran?");
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

                            sendPaymentConfirmation();
                        }
                    });
                } else {
                    focus.requestFocus();
                }
            }
        });

        transactionConfirmDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelConfirmation();
            }
        });

        transactionConfirmDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    showCancelConfirmation();
                }
                return false;
            }
        });

        focus = null;
        bankAccountUserContainer = (LinearLayout) transactionConfirmDialog.findViewById(R.id.payment_confirmation_bank_account_user_container);
        bankSpinnerContainer = (LinearLayout) transactionConfirmDialog.findViewById(R.id.payment_confirmation_bank_spinner_container);
        mobileNumberUserContainer = (LinearLayout) transactionConfirmDialog.findViewById(R.id.payment_confirmation_mobile_number_user_container);
        transactionCodeField = (AutoCompleteTextView) transactionConfirmDialog.findViewById(R.id.payment_confirmation_transaction_code);
        imageForeground = (RelativeLayout) transactionConfirmDialog.findViewById(R.id.payment_confirmation_image_foreground);
        uploadSpace = (RelativeLayout) transactionConfirmDialog.findViewById(R.id.payment_confirmation_upload_space);
        transactionCodeLabel = (TextView) transactionConfirmDialog.findViewById(R.id.payment_confirmation_transaction_code_label);
        uploadStructLabel = (TextView) transactionConfirmDialog.findViewById(R.id.payment_confirmation_upload_struct_label);
        uploadStatus = (TextView) transactionConfirmDialog.findViewById(R.id.payment_confirmation_upload_status);
        image = (ImageView) transactionConfirmDialog.findViewById(R.id.payment_confirmation_image);
        imageProgress = (ProgressBar) transactionConfirmDialog.findViewById(R.id.payment_confirmation_image_progress);
        imageRemove = (ImageView) transactionConfirmDialog.findViewById(R.id.payment_confirmation_image_remove);
        agreement = (CheckBox) transactionConfirmDialog.findViewById(R.id.payment_confirmation_agreement);

        if (bank != null) {
            mobileNumberUserContainer.setVisibility(View.GONE);

            bankAccountUserLayout = (TextInputLayout) transactionConfirmDialog.findViewById(R.id.payment_confirmation_bank_account_user_layout);
            bankAccountUserField = (AutoCompleteTextView) transactionConfirmDialog.findViewById(R.id.payment_confirmation_bank_account_user);
            bankSpinner = (Spinner) transactionConfirmDialog.findViewById(R.id.payment_confirmation_bank_spinner);
            bankFillField = (AutoCompleteTextView) transactionConfirmDialog.findViewById(R.id.payment_confirmation_bank_fill);
            bankAccountUserLabel = (TextView) transactionConfirmDialog.findViewById(R.id.payment_confirmation_bank_account_user_label);
            bankSpinnerLabel = (TextView) transactionConfirmDialog.findViewById(R.id.payment_confirmation_bank_spinner_label);

            bankListAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, bankList) {
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    convertView = View.inflate(getContext(), R.layout.custom_spinner_dropdown, null);
                    View item = super.getView(position, convertView, parent);
                    if (position == 0) {
                        ((TextView) item).setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                    return item;
                }
            };
            bankSpinner.setAdapter(bankListAdapter);

            uploadStructLabel.setText("Upload Bukti Struk Transfer");
            bankAccountUserLabel.setPadding(bankAccountUserField.getPaddingLeft(), 0, 0, 0);
            bankSpinnerLabel.setPadding(transactionCodeField.getPaddingLeft(), 0, 0, 0);

            bankAccountUserValid = true;
            bankAccountUserField.requestFocus();
            bankAccountUserField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!bankAccountUserValid) {
                        bankAccountUserLayout.setError(null);
                        bankAccountUserLayout.setErrorEnabled(false);
                        bankAccountUserValid = true;
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            bankSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(Spinner parent, View view, int position, long id) {
                    if (bankSpinner.getSelectedItemPosition() == 0) {
                        agreement.setChecked(false);
                        transactionConfirmDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        setAgreementDisabled();
                    } else if (bankSpinner.getSelectedItemPosition() == bankSpinner.getAdapter().getCount() - 1) {
                        agreement.setChecked(false);
                        transactionConfirmDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        setAgreementDisabled();

                        bankFillField.setVisibility(View.VISIBLE);
                        bankFillField.requestFocus();
                    } else {
                        bankFillField.setVisibility(View.GONE);
                        bankFillField.setText("");
                        bankSpinner.requestFocus();
                        if (imageAttached) {
                            setAgreementEnabled();
                        }
                    }
                }
            });

            bankFillField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        if (imageAttached) {
                            setAgreementEnabled();
                        }
                    } else {
                        agreement.setChecked(false);
                        transactionConfirmDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        setAgreementDisabled();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } else if (mobileNetwork != null) {
            bankAccountUserContainer.setVisibility(View.GONE);
            bankSpinnerContainer.setVisibility(View.GONE);

            mobileNumberUserLabel = (TextView) transactionConfirmDialog.findViewById(R.id.payment_confirmation_mobile_number_user_label);
            mobileNumberUserLayout = (TextInputLayout) transactionConfirmDialog.findViewById(R.id.payment_confirmation_mobile_number_user_layout);
            mobileNumberUserField = (AutoCompleteTextView) transactionConfirmDialog.findViewById(R.id.payment_confirmation_mobile_number_user);

            mobileNumberUserValid = true;
            uploadStructLabel.setText("Upload Bukti SMS Transfer Pulsa Berhasil");
            mobileNumberUserLabel.setPadding(mobileNumberUserField.getPaddingLeft(), 0, 0, 0);

            mobileNumberUserField.requestFocus();
            mobileNumberUserField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!mobileNumberUserValid) {
                        mobileNumberUserLayout.setError(null);
                        mobileNumberUserLayout.setErrorEnabled(false);
                        mobileNumberUserValid = true;
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        transactionCodeLabel.setPadding(transactionCodeField.getPaddingLeft(), 0, 0, 0);
        uploadStructLabel.setPadding(transactionCodeField.getPaddingLeft(), 0, 0, 0);
        transactionCodeField.setText(transactionCode.getText());

        setAgreementDisabled();

        bitmap = null;
        image.setImageBitmap(bitmap);
        imageAttached = false;

        imageForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageAttached) {
                    intent = new Intent(getApplication(), ActivityFullScreen.class);
                    intent.putExtra("uri", uri);
                    startActivity(intent);
                } else {
                    mediaChooser = ClassMediaChooser.getPickImageIntent(ActivityTransactionStatus.this);
                    startActivityForResult(mediaChooser, CHOOSE_MEDIA);
                }
            }
        });

        imageRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog = new CustomAlertDialog(ActivityTransactionStatus.this);
                customAlertDialog.setTitle("Konfirmasi");
                customAlertDialog.setMessage("Anda akan menghapus gambar yang dipilih?");
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

                        toast = Toast.makeText(ActivityTransactionStatus.this, "Gambar Dihapus", Toast.LENGTH_SHORT);
                        applicationTool.resizeToast(toast);
                        toast.show();

                        uploadSpace.setVisibility(View.VISIBLE);
                        imageProgress.setVisibility(View.INVISIBLE);
                        uploadStatus.setVisibility(View.GONE);
                        imageRemove.setVisibility(View.INVISIBLE);
                        agreement.setChecked(false);

                        transactionConfirmDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        setAgreementDisabled();

                        imageAttached = false;
                        bitmap = null;
                        image.setImageDrawable(null);
                    }
                });
            }
        });

        agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agreement.isChecked()) {
                    transactionConfirmDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    transactionConfirmDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        openDialView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_DIAL);
                startActivity(intent);
            }
        });

        paymentConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogConfirmation();
                verifyStoragePermission(ActivityTransactionStatus.this);
            }
        });

        cancelTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog = new CustomAlertDialog(ActivityTransactionStatus.this);
                customAlertDialog.setTitle("Konfirmasi");
                customAlertDialog.setMessage("Anda akan membatalkan transaksi pembayaran?");
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

                        setActionTransaction("Membatalkan transaksi pembayaran...", "abort");
                    }
                });
            }
        });

        doneTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActionTransaction("Menyelesaikan transaksi pembayaran...", "done");
            }
        });
    }

    private void tryGetDetailTransaction() {
        if (Connectivity.isConnected(getApplicationContext())) {
            transactionPresenter.detailTransaction(transaction_id, getApplicationContext());
        } else {
            showNotificationNetwork(new INoInternet() {
                @Override
                public void onRetry() {
                    tryGetDetailTransaction();
                }
            });
        }
    }

    private void setData() {
        for (Paket p : packagesList) {
            if (p.getId() == packageID) {
                paket = p;
                break;
            }
        }
        if (bankID != 0) {
            for (Bank b : banksList) {
                if (b.getId() == bankID) {
                    bank = b;
                    break;
                }
            }
        } else if (mobileNetworkID != 0) {
            for (MobileNetwork mn : mobileNetworksList) {
                if (mn.getId() == mobileNetworkID) {
                    mobileNetwork = mn;
                    break;
                }
            }
        }

        if (transaction.getStatus() == 0) {
            cancelTransaction.setVisibility(View.VISIBLE);
            doneTransaction.setVisibility(View.GONE);
        } else {
            cancelTransaction.setVisibility(View.GONE);
            adviceContainer.setVisibility(View.GONE);
            doneTransaction.setVisibility(View.VISIBLE);
        }

        costCodeValue = transaction.getUnique_code();
        costPackageValue = (int) paket.getNominal();
        costPackage.setText(applicationTool.convertRpCurrency((int) paket.getNominal()));
        packageType.setText("Paket " + packagesList.get(packageID - 1).getCredit() + " Pertanyaan");
        totalCostValue = costPackageValue + costCodeValue;
//        if (paket.getActive() == 7) {
//            packageValidUntil.setText("1 minggu (7 hari)");
//        } else if (paket.getActive() == 14) {
//            packageValidUntil.setText("2 minggu (14 hari)");
//        } else if (paket.getActive() == 30) {
//            packageValidUntil.setText("1 bulan (30 hari)");
//        }

        idCode = String.valueOf(transaction.getId());
        uniqueCode = String.valueOf(costCodeValue);
        while (idCode.length() < 3) {
            idCode = "0" + idCode;
        }
        while (uniqueCode.length() < 3) {
            uniqueCode = "0" + uniqueCode;
        }
        totalCost.setText(applicationTool.convertRpCurrency(totalCostValue));
        transactionCode.setText("SOL." + idCode + "." + uniqueCode);
        transactionStatus.setText(strStatus[transaction.getStatus()]);
        transactionStatus.setBackgroundResource(backgroundStatus[transaction.getStatus()]);
        costCode.setText(applicationTool.convertRpCurrency(costCodeValue));
        if (bank != null) {
            totalCostLabel.setText("TOTAL PEMBAYARAN");
            mediaTransferLabel.setText("Rincian Transfer");
            mediaTransferDetailLabel.setText("No. Rekening");
            mediaTransferDetail.setText(bank.getAccountNumber());
            bankAccountOwner.setText(bank.getAccountOwner());

            openDialView.setVisibility(View.GONE);
        } else if (mobileNetwork != null) {
            totalCostLabel.setText("TOTAL TRANSFER PULSA");
            mediaTransferLabel.setText("Rincian Transfer Pulsa");
            mediaTransferDetailLabel.setText("No. Handphone");
            mediaTransferDetail.setText(mobileNetwork.getNumber());
            bankAccountOwnerContainer.setVisibility(View.GONE);
        }
        dueDateTransaction.setText(Global.get().convertDateToFormat(transaction.getPayment_time(), "EEEE, dd MMMM yyyy hh:mm ") + "WIB");//"Senin 1 Januari 2016, 00:00 WIB");

        if (bank != null) {
            if (bankID == 1) {
                mediaTransferLogo.setImageResource(R.drawable.ic_bca);
            } else if (bankID == 2) {
                mediaTransferLogo.setImageResource(R.drawable.ic_bni);
            }
        } else {
            if (mobileNetworkID == 1) {
                mediaTransferLogo.setImageResource(R.drawable.ic_telkomsel);
            } else if (mobileNetworkID == 2) {
                mediaTransferLogo.setImageResource(R.drawable.ic_xl);
            }
        }
    }

    private void setActionTransaction(final String message, final String action) {
        customProgressDialog = new CustomProgressDialog(ActivityTransactionStatus.this);
        customProgressDialog.setMessage(message);

        if (Connectivity.isConnected(getApplicationContext())) {
            this.action = action;
            transactionPresenter.action(transaction.getId(), action, getApplicationContext());
        } else {
            customProgressDialog.dismiss();

            showNotificationNetwork(new INoInternet() {
                @Override
                public void onRetry() {
                    setActionTransaction(message, action);
                }
            });
        }
    }

    private void sendPaymentConfirmation() {
        customProgressDialog = new CustomProgressDialog(ActivityTransactionStatus.this);
        customProgressDialog.setMessage("Mengirimkan informasi konfirmasi pembayaran...");

        if (Connectivity.isConnected(getApplicationContext())) {
            if (bank != null) {
                String strBank = bankSpinner.getSelectedItem().toString();
                String strBankOther = bankFillField.getText().toString();

                transactionPresenter.confirmPackage(transaction, strBank,
                        strBankOther, bankAccountUserField.getText().toString(), "-", uri, bitmap, applicationTool, byteArrayOutputStream, getApplicationContext());
            } else if (mobileNetwork != null) {
                String strMobileNumberUser = mobileNumberUserField.getText().toString();
                transactionPresenter.confirmPackage(transaction, "-", "-", "-", strMobileNumberUser, uri, bitmap, applicationTool, byteArrayOutputStream, getApplicationContext());
            }
        } else {
            customProgressDialog.dismiss();

            toast = Toast.makeText(ActivityTransactionStatus.this, getResources().getString(R.string.text_no_internet), Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
            toast.show();
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

    private void showNotificationConfirmationSent() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(mainLayout, "Informasi konfirmasi pembayaran telah dikirimkan", Snackbar.LENGTH_LONG);
                applicationTool.resizeSnackBar(snackbar, 1);
                snackbar.show();
            }
        }, 500);
    }

    private void checkValidity() {
        if (bank != null) {
            bankAccountUserText = bankAccountUserField.getText().toString().trim();
            if (TextUtils.isEmpty(bankAccountUserText)) {
                bankAccountUserLayout.setError("Masukkan nama pemilik rekening bank");
                focus = bankAccountUserField;
                bankAccountUserValid = false;
            } else {
                bankAccountUserValid = true;
            }
        } else if (mobileNetwork != null) {
            mobileNumberUserText = mobileNumberUserField.getText().toString().trim();
            if (TextUtils.isEmpty(mobileNumberUserText)) {
                mobileNumberUserLayout.setError("Masukkan no. hp pemilik");
                focus = mobileNumberUserField;
                mobileNumberUserValid = false;
            } else if (!isMobileValid(mobileNumberUserText)) {
                mobileNumberUserLayout.setError("Format no. hp salah");
                focus = mobileNumberUserField;
                mobileNumberUserValid = false;
            } else {
                mobileNumberUserValid = true;
            }
        }
    }

    private boolean isMobileValid(String mobile) {
        return (mobile.length() > 9 && mobile.charAt(0) == '0');
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_MEDIA) {
            ClassMediaChooser.setImageFromResult(resultCode, data, ActivityTransactionStatus.this);
            if (ClassMediaChooser.resizedBitmap != null) {
                uri = ClassMediaChooser.uri;
                bitmap = applicationTool.adjustBitmap(ClassMediaChooser.resizedBitmap, uri);

                setImpactImageUploaded();
            }
        }
    }

    private void setImpactImageUploaded() {
        uploadSpace.setVisibility(View.GONE);
        imageProgress.setVisibility(View.VISIBLE);
        imageProgress.setProgress(0);

        imageForeground.setEnabled(false);
        imageRemove.setVisibility(View.VISIBLE);

        new CompressImage().execute();

    }

    private void showCancelConfirmation() {
        customAlertDialog = new CustomAlertDialog(ActivityTransactionStatus.this);
        customAlertDialog.setTitle("Konfirmasi");
        customAlertDialog.setMessage("Anda akan menutup dialog konfirmasi pembayaran?");
        customAlertDialog.setNegativeButton("Tidak", new CustomAlertDialog.OnNegativeClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();
            }
        });
        customAlertDialog.setPositiveButton("Ya", new CustomAlertDialog.OnPositiveClickListener() {
            @Override
            public void onClick() {
                transactionConfirmDialog.dismiss();
                customAlertDialog.dismiss();
            }
        });
    }

    private void setAgreementEnabled() {
        agreement.setEnabled(true);
        agreement.setAlpha(1f);
    }

    private void setAgreementDisabled() {
        agreement.setEnabled(false);
        agreement.setAlpha(0.25f);
    }

    private static void verifyStoragePermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    transactionConfirmDialog.dismiss();
                }
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onSuccess(Response response) {
        switch (response.getTag()) {
            case Response.TAG_CONFIRM_PACKAGE_SUCCESS:
                if (customProgressDialog != null) {
                    customProgressDialog.dismiss();
                }
                transactionConfirmDialog.dismiss();
                showNotificationConfirmationSent();
                break;
            case Response.TAG_PURCHASE_PACKAGE_ACTION:
                if (action.equals("abort")) {
                    showNotificationTransactionCanceled();
                }
                if (customProgressDialog != null) {
                    customProgressDialog.dismiss();
                }
                finished();

                break;
            case Response.TAG_PURCHASE_PACKAGE_DETAIL:
                transaction = ((ResponseTransaction) response).getData().getTransaction();
                packageID = transaction.getPackageId();
                bankID = transaction.getBank_id();
                mobileNetworkID = transaction.getMobileNetwork_id();

                setData();
                progressView.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);

                break;
        }
    }

    @Override
    public void onFailure(String message) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }

        toast = Toast.makeText(ActivityTransactionStatus.this, message, Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();
    }

    class CompressImage extends AsyncTask<Void, Integer, Void> {
        private int compressQuality, target, min, progress;

        @Override
        protected void onPreExecute() {
            byteArrayOutputStream = new ByteArrayOutputStream();

            compressQuality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, byteArrayOutputStream);

            target = applicationTool.targetCompress;
            min = byteArrayOutputStream.size() / 1024;
            progress = min - target;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (progress > 0) {
                while (progress > 0) {
                    try {
                        byteArrayOutputStream.flush();
                        byteArrayOutputStream.reset();
                    } catch (IOException e) {
                    }
                    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, byteArrayOutputStream);
                    compressQuality -= 1;
                    if (compressQuality == 0) {
                        break;
                    }

                    progress = byteArrayOutputStream.size() / 1024 - target;
                    if (progress < 0) {
                        progress = 0;
                    }
                    publishProgress((int) (((float) (min - progress) / (float) min) * 100));
                }
            } else {
                publishProgress(100);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            imageProgress.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            image.setImageBitmap(bitmap);

            toast = Toast.makeText(ActivityTransactionStatus.this, "Terunggah", Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
            toast.show();

            imageForeground.setEnabled(true);
            uploadStatus.setVisibility(View.VISIBLE);

            imageAttached = true;
            if (bank != null) {
                if (bankSpinner.getSelectedItemPosition() > 0 &&
                        bankSpinner.getSelectedItemPosition() < bankSpinner.getAdapter().getCount() - 1) {
                    setAgreementEnabled();
                } else if (bankSpinner.getSelectedItemPosition() == bankSpinner.getAdapter().getCount() - 1) {
                    if (bankFillField.getText().length() > 0) {
                        setAgreementEnabled();
                    }
                }
            } else if (mobileNetwork != null) {
                setAgreementEnabled();
            }
        }
    }
}