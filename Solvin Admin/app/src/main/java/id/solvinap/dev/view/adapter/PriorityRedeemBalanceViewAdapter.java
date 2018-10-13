package id.solvinap.dev.view.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;


import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataRedeemBalance;
import id.solvinap.dev.server.helper.Global;
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

public class PriorityRedeemBalanceViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //    INTERFACE
    public interface OnLoadMoreListener {
        void OnLoadMore();
    }

    public interface OnActionListener {
        void OnAction(String message, int id, int mentorId, int action);
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

    private void setAction(String message, int id, int mentorId, int action) {
        if (mActionListener != null) {
            mActionListener.OnAction(message, id, mentorId, action);
        }
    }

    //    VIEW
    private RecyclerView priorityRedeemBalanceView;
    private View itemView;

    public CustomProgressDialog customProgressDialog;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;
    private LinearLayoutManager layoutManager;

    private Context context;
    private Animation animation;

    //    OBJECT
    private List<DataRedeemBalance> redeemBalanceList;

    //    VARIABLE
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;
    private int totalItemCount, lastVisibleItem, visibleThreshold = 1;
    public int lastAnimatedPosition = -1;
    private int[] backgroundResource = {R.drawable.custom_transaction_status_pending,
            R.drawable.custom_transaction_status_actived,
            R.drawable.custom_transaction_status_cancelled};

    private String[] statusType = {"Pending", "Berhasil", "Ditolak"};

    private boolean loading = false;

    public PriorityRedeemBalanceViewAdapter(RecyclerView priorityRedeemBalanceView, List<DataRedeemBalance> redeemBalanceList) {
        this.priorityRedeemBalanceView = priorityRedeemBalanceView;
        this.redeemBalanceList = redeemBalanceList;

        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) priorityRedeemBalanceView.getLayoutManager();
    }

    private void setEvent() {
        priorityRedeemBalanceView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadMore();
                    loading = true;

                    redeemBalanceList.add(null);
                    notifyDataSetChanged();
                }

            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    public void updateList(List<DataRedeemBalance> redeemBalanceList) {
        this.redeemBalanceList.clear();
        this.redeemBalanceList.addAll(redeemBalanceList);

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
        private CircleImageView photo;
        private TextView name, time, code, balance, status;
        private LinearLayout reject, approve;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            photo = (CircleImageView) view.findViewById(R.id.priority_redeem_balance_list_photo);
            name = (TextView) view.findViewById(R.id.priority_redeem_balance_list_name);
            time = (TextView) view.findViewById(R.id.priority_redeem_balance_list_time);
            code = (TextView) view.findViewById(R.id.priority_redeem_balance_list_code);
            balance = (TextView) view.findViewById(R.id.priority_redeem_balance_list_balance);
            status = (TextView) view.findViewById(R.id.priority_redeem_balance_list_status);

            reject = (LinearLayout) view.findViewById(R.id.priority_redeem_balance_list_reject);
            approve = (LinearLayout) view.findViewById(R.id.priority_redeem_balance_list_approve);
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
        if (redeemBalanceList.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROGRESS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.priority_redeem_balance_list, parent, false);
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
            final DataRedeemBalance dataRedeemBalance = redeemBalanceList.get(position);

            Picasso.with(context).load(Global.ASSETS_URL + "mentor" + "/" + dataRedeemBalance.getDataMentor().getPhoto())
                    .placeholder(R.drawable.operator)
                    .fit()
                    .centerCrop()
                    .into(((ItemViewHolder) holder).photo);

            ((ItemViewHolder) holder).name.setText(dataRedeemBalance.getDataMentor().getName());
            ((ItemViewHolder) holder).time.setText(Tool.getInstance(context).convertToDateTimeFormat(0, dataRedeemBalance.getCreatedAt()));

            ((ItemViewHolder) holder).code.setText(dataRedeemBalance.getRedeemCode());
            ((ItemViewHolder) holder).balance.setText(Tool.getInstance(context).convertRpCurrency(dataRedeemBalance.getBalance()));
            ((ItemViewHolder) holder).status.setText(statusType[dataRedeemBalance.getStatus()]);
            ((ItemViewHolder) holder).status.setBackgroundResource(backgroundResource[dataRedeemBalance.getStatus()]);

            ((ItemViewHolder) holder).reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CustomAdminPasswordConfirmation adminPasswordConfirmation = new CustomAdminPasswordConfirmation(context, Global.ACTION_PASSWORD);
                    adminPasswordConfirmation.setOnConfirmedListener(new CustomAdminPasswordConfirmation.OnConfirmedListener() {
                        @Override
                        public void OnConfirmed() {
                            showConfirmationDialog(0, "Anda akan menolak permohonan tebusan saldo terkait?",
                                    "Menolak permohonan tebusan saldo...", dataRedeemBalance);
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
                            showConfirmationDialog(1, "Anda akan menyetujui permohonan tebusan saldo terkait?",
                                    "Menyetujui permohonan tebusan saldo...", dataRedeemBalance);
                        }
                    });
                }
            });

            setAnimation(((ItemViewHolder) holder).itemView, position);
        } else if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    private void showConfirmationDialog(final int action, final String alertDialogMessage, final String progressDialogMessage,
                                        final DataRedeemBalance dataRedeemBalance) {
        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setTitle("Konfirmasi");
        customAlertDialog.setMessage(alertDialogMessage);

        customAlertDialog.setPositiveButton("Ya", new CustomAlertDialog.OnPositiveClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();

                setAction(progressDialogMessage, dataRedeemBalance.getId(), dataRedeemBalance.getDataMentor().getId(), action);
            }
        });

        customAlertDialog.setNegativeButton("Tidak", new CustomAlertDialog.OnNegativeClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();
            }
        });
    }

    public void showCustomProgressDialog(String message){
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
        return redeemBalanceList.size();
    }
}