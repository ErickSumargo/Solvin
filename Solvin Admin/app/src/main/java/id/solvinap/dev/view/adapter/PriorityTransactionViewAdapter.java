package id.solvinap.dev.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataTransaction;
import id.solvinap.dev.server.data.DataTransactionConfirm;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.view.activity.ActivityImageFullScreen;
import id.solvinap.dev.view.helper.Tool;
import id.solvinap.dev.view.widget.CustomAdminPasswordConfirmation;
import id.solvinap.dev.view.widget.CustomAlertDialog;
import id.solvinap.dev.view.widget.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by Erick Sumargo on 2/20/2017.
 */

public class PriorityTransactionViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //    INTERFACE
    public interface OnLoadMoreListener {
        void OnLoadMore();
    }

    public interface OnActionListener {
        void OnAction(String message, int id, int action);
    }

    private static OnLoadMoreListener mLoadMoreListener;

    private static OnActionListener mActionListener;

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public void setOnActionListener(OnActionListener listener) {
        mActionListener = listener;
    }

    public void loadMore() {
        if (mLoadMoreListener != null) {
            mLoadMoreListener.OnLoadMore();
        }
    }

    private void setAction(String message, int id, int action) {
        if (mActionListener != null) {
            mActionListener.OnAction(message, id, action);
        }
    }

    //    VIEW
    private RecyclerView priorityTransactionView;
    private View itemView;
    public CustomProgressDialog customProgressDialog;

    private AlertDialog.Builder transactionConfirmBuilder;
    private AlertDialog transactionConfirmDialog;
    private TextView transactionConfirmCode, transactionConfirmBankAccountOwner,
            transactionConfirmBankName;
    private ImageView transactionConfirmImage;
    private RelativeLayout transactionConfirmImageForeground;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;
    private LinearLayoutManager layoutManager;

    private Context context;
    private Intent intent;
    private Animation animation;

    private GradientDrawable gradientDrawable;
    private TypedValue typedValue;

    //    OBJECT
    private List<DataTransaction> transactionList;

    //    VARIABLE
    private int totalItemCount, lastVisibleItem, visibleThreshold = 1;
    public int lastAnimatedPosition = -1;
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;
    private int[] backgroundResource = {R.drawable.custom_transaction_status_pending,
            R.drawable.custom_transaction_status_actived,
            R.drawable.custom_transaction_status_cancelled};

    private String idCode, uniqueCode, confirmIdCode, confirmUniqueCode;
    private String[] statusType = {"Pending", "Lunas", "Dibatalkan"};

    private boolean loading = false;

    public PriorityTransactionViewAdapter(RecyclerView priorityTransactionView, List<DataTransaction> transactionList) {
        this.priorityTransactionView = priorityTransactionView;
        this.transactionList = transactionList;

        init();
        setEvent();
    }

    private void init() {
        //    HELPER
        layoutManager = (LinearLayoutManager) priorityTransactionView.getLayoutManager();

        typedValue = new TypedValue();
    }

    private void setEvent() {
        priorityTransactionView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadMore();
                    loading = true;

                    transactionList.add(null);
                    notifyDataSetChanged();
                }

            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    public void updateList(List<DataTransaction> transactionList) {
        this.transactionList.clear();
        this.transactionList.addAll(transactionList);

        notifyDataSetChanged();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastAnimatedPosition) {
            animation = AnimationUtils.loadAnimation(context, R.anim.load_feed);
            viewToAnimate.startAnimation(animation);

            lastAnimatedPosition = position;
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout group;
        private RelativeLayout avatarLayout;
        private CircleImageView photo;
        private TextView avatarInitial, name, time, code, packageType, cost, packageValidUntil, status;
        private LinearLayout reject, approve;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);

            group = (LinearLayout) view.findViewById(R.id.priority_transaction_list_group);
            avatarLayout = (RelativeLayout) view.findViewById(R.id.priority_transaction_list_avatar_layout);
            avatarInitial = (TextView) view.findViewById(R.id.priority_transaction_list_avatar_initial);
            photo = (CircleImageView) view.findViewById(R.id.priority_transaction_list_photo);
            name = (TextView) view.findViewById(R.id.priority_transaction_list_name);
            time = (TextView) view.findViewById(R.id.priority_transaction_list_time);
            code = (TextView) view.findViewById(R.id.priority_transaction_list_code);
            packageType = (TextView) view.findViewById(R.id.priority_transaction_list_package_type);
            packageValidUntil = (TextView) view.findViewById(R.id.priority_transaction_list_package_valid_until);
            cost = (TextView) view.findViewById(R.id.priority_transaction_list_cost);
            status = (TextView) view.findViewById(R.id.priority_transaction_list_status);

            reject = (LinearLayout) view.findViewById(R.id.priority_transaction_list_reject);
            approve = (LinearLayout) view.findViewById(R.id.priority_transaction_list_approve);
        }
    }

    private class ProgressViewHolder extends RecyclerView.ViewHolder {
        private CircularProgressBar progressBar;

        public ProgressViewHolder(View view) {
            super(view);
            progressBar = (CircularProgressBar) view.findViewById(R.id.footer_progress_bar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (transactionList.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROGRESS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.priority_transaction_list, parent, false);
            mainHolder = new ItemViewHolder(itemView);
        } else if (viewType == VIEW_PROGRESS) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.footer_loader, parent, false);
            mainHolder = new ProgressViewHolder(itemView);
        }
        return mainHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            final DataTransaction dataTransaction = transactionList.get(position);
            ((ItemViewHolder) holder).group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dataTransaction.getDataTransactionConfirm() != null) {
                        showTransactionConfirmDialog(dataTransaction);
                    }
                }
            });

            if (dataTransaction.getDataStudent().getPhoto().length() > 0) {
                Picasso.with(context).load(Global.ASSETS_URL + "student" + "/" + dataTransaction.getDataStudent().getPhoto())
                        .placeholder(R.drawable.operator)
                        .fit()
                        .centerCrop()
                        .into(((ItemViewHolder) holder).photo);

                ((ItemViewHolder) holder).avatarLayout.setVisibility(View.GONE);
                ((ItemViewHolder) holder).photo.setVisibility(View.VISIBLE);
            } else {
                gradientDrawable = (GradientDrawable) ((ItemViewHolder) holder).avatarLayout.getBackground();
                gradientDrawable.setColor(Tool.getInstance(context).getAvatarColor(dataTransaction.getDataStudent().getId()));

                ((ItemViewHolder) holder).avatarInitial.setText(Tool.getInstance(context).getInitialName(dataTransaction.getDataStudent().getName()));

                ((ItemViewHolder) holder).avatarLayout.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).photo.setVisibility(View.GONE);
            }

            ((ItemViewHolder) holder).name.setText(dataTransaction.getDataStudent().getName());
            ((ItemViewHolder) holder).time.setText(Tool.getInstance(context).convertToDateTimeFormat(0, dataTransaction.getCreatedAt()));

            idCode = String.valueOf(dataTransaction.getId());
            uniqueCode = String.valueOf(dataTransaction.getUniqueCode());
            while (idCode.length() < 3) {
                idCode = "0" + idCode;
            }
            while (uniqueCode.length() < 3) {
                uniqueCode = "0" + uniqueCode;
            }
            ((ItemViewHolder) holder).code.setText("SOL." + idCode + "." + uniqueCode);
            ((ItemViewHolder) holder).packageType.setText(String.valueOf(dataTransaction.getDataPackage().getCredit()) + " Pertanyaan");
            ((ItemViewHolder) holder).cost.setText(Tool.getInstance(context).convertRpCurrency(dataTransaction.getDataPackage().getNominal() + dataTransaction.getUniqueCode()));

            if (dataTransaction.getDataPackage().getActive() == 7) {
                ((ItemViewHolder) holder).packageValidUntil.setText("1 minggu (7 hari)");
            } else if (dataTransaction.getDataPackage().getActive() == 14) {
                ((ItemViewHolder) holder).packageValidUntil.setText("2 minggu (14 hari)");
            } else {
                ((ItemViewHolder) holder).packageValidUntil.setText("1 bulan (30 hari)");
            }

            ((ItemViewHolder) holder).status.setText(statusType[dataTransaction.getStatus()]);
            ((ItemViewHolder) holder).status.setBackgroundResource(backgroundResource[dataTransaction.getStatus()]);

            ((ItemViewHolder) holder).reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CustomAdminPasswordConfirmation adminPasswordConfirmation = new CustomAdminPasswordConfirmation(context, Global.ACTION_PASSWORD);
                    adminPasswordConfirmation.setOnConfirmedListener(new CustomAdminPasswordConfirmation.OnConfirmedListener() {
                        @Override
                        public void OnConfirmed() {
                            showConfirmationDialog(0, "Anda akan menolak permohonan transaksi pembelian paket pertanyaan?",
                                    "Menolak permohonan transaksi...", dataTransaction);
                        }
                    });
                }
            });

            ((ItemViewHolder) holder).approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CustomAdminPasswordConfirmation adminPasswordConfirmation = new CustomAdminPasswordConfirmation(context, Global.ACTION_PASSWORD);
                    adminPasswordConfirmation.setOnConfirmedListener(new CustomAdminPasswordConfirmation.OnConfirmedListener() {
                        @Override
                        public void OnConfirmed() {
                            showConfirmationDialog(1, "Anda akan menyetujui permohonan transaksi pembelian paket pertanyaan?",
                                    "Menyetujui permohonan transaksi...", dataTransaction);
                        }
                    });
                }
            });

            setAnimation(((ItemViewHolder) holder).itemView, position);
        } else if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    private void showTransactionConfirmDialog(DataTransaction dataTransaction) {
        transactionConfirmBuilder = new AlertDialog.Builder(context);
        transactionConfirmDialog = transactionConfirmBuilder.create();
        transactionConfirmDialog.setView(transactionConfirmDialog.getLayoutInflater().inflate(R.layout.activity_transaction_confirm, null));
        transactionConfirmDialog.show();

        transactionConfirmCode = (TextView) transactionConfirmDialog.findViewById(R.id.transaction_confirm_code);
        transactionConfirmBankAccountOwner = (TextView) transactionConfirmDialog.findViewById(R.id.transaction_confirm_bank_account_owner);
        transactionConfirmBankName = (TextView) transactionConfirmDialog.findViewById(R.id.transaction_confirm_bank_name);

        transactionConfirmImageForeground = (RelativeLayout) transactionConfirmDialog.findViewById(R.id.transaction_confirm_image_foreground);
        transactionConfirmImage = (ImageView) transactionConfirmDialog.findViewById(R.id.transaction_confirm_image);

        final DataTransactionConfirm dataTransactionConfirm = dataTransaction.getDataTransactionConfirm();

        confirmIdCode = String.valueOf(dataTransaction.getId());
        confirmUniqueCode = String.valueOf(dataTransaction.getUniqueCode());
        while (confirmIdCode.length() < 3) {
            confirmIdCode = "0" + confirmIdCode;
        }
        while (confirmUniqueCode.length() < 3) {
            confirmUniqueCode = "0" + confirmUniqueCode;
        }
        transactionConfirmCode.setText("SOL." + confirmIdCode + "." + confirmUniqueCode);
        transactionConfirmBankAccountOwner.setText(dataTransactionConfirm.getBankAccountOwner());
        if (dataTransaction.getDataTransactionConfirm().getBankNameOther().length() == 0) {
            transactionConfirmBankName.setText(dataTransactionConfirm.getBankName());
        } else {
            transactionConfirmBankName.setText(dataTransactionConfirm.getBankName() + " (" + dataTransactionConfirm.getBankNameOther() + ")");
        }

        Picasso.with(context).load(Global.ASSETS_URL + "transactionconfirm" + "/" + dataTransactionConfirm.getImage())
                .placeholder(R.drawable.image_placeholder)
                .fit()
                .centerCrop()
                .into(transactionConfirmImage);

        transactionConfirmImageForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(context, ActivityImageFullScreen.class);
                intent.putExtra("image", dataTransactionConfirm.getImage());
                intent.putExtra("category", "transactionconfirm");

                context.startActivity(intent);
            }
        });
    }

    private void showConfirmationDialog(final int action, final String alertDialogMessage, final String progressDialogMessage,
                                        final DataTransaction dataTransaction) {
        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setTitle("Konfirmasi");
        customAlertDialog.setMessage(alertDialogMessage);

        customAlertDialog.setPositiveButton("Ya", new CustomAlertDialog.OnPositiveClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();

                setAction(progressDialogMessage, dataTransaction.getId(), action);
            }
        });

        customAlertDialog.setNegativeButton("Tidak", new CustomAlertDialog.OnNegativeClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();
            }
        });
    }

    public void showCustomProgressDialog(String message) {
        customProgressDialog = new CustomProgressDialog(context);
        customProgressDialog.setMessage(message);
    }

    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).itemView.clearAnimation();
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }
}