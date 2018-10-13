package id.solvin.dev.view.adapters;

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

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.model.basic.RedeemBalance;

import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by Erick Sumargo on 1/19/2017.
 */

public class HistoryRedeemBalanceViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //    INTERFACE
    public interface OnLoadMoreListener {
        void OnLoadMore();
    }

    private static OnLoadMoreListener mLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    private void loadMore() {
        if (mLoadMoreListener != null) {
            mLoadMoreListener.OnLoadMore();
        }
    }

    //    VIEW
    private RecyclerView historyRedeemBalanceView;
    private View itemView;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;
    private LinearLayoutManager layoutManager;

    private Context context;
    private Animation animation;

    //    OBJECT
    private List<RedeemBalance> historyRedeemBalanceList;

    //    VARIABLE
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;
    private int totalItemCount, lastVisibleItem, visibleThreshold = 1, lastAnimatedPosition = -1;
    private int[] backgroundResource = {R.drawable.custom_transaction_status_pending,
            R.drawable.custom_transaction_status_actived,
            R.drawable.custom_transaction_status_cancelled};

    private String[] statusType = {"Pending", "Berhasil", "Ditolak"};
    private String[] dateParts;

    private boolean loading = false;

    public HistoryRedeemBalanceViewAdapter(RecyclerView historyRedeemBalanceView, List<RedeemBalance> historyRedeemBalanceList) {
        this.historyRedeemBalanceView = historyRedeemBalanceView;
        this.historyRedeemBalanceList = historyRedeemBalanceList;

        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) historyRedeemBalanceView.getLayoutManager();
    }

    private void setEvent() {
        historyRedeemBalanceView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadMore();
                    loading = true;

                    historyRedeemBalanceList.add(null);
                    notifyDataSetChanged();
                }

            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    public void updateList(List<RedeemBalance> historyRedeemBalanceList) {
        this.historyRedeemBalanceList.clear();
        this.historyRedeemBalanceList.addAll(historyRedeemBalanceList);

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
        private LinearLayout dateApprovedContainer;
        private TextView day, month, year, code, balance, status, actionResultLabel, dateApproved;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            dateApprovedContainer = (LinearLayout) view.findViewById(R.id.history_redeem_balance_date_approved_container);
            day = (TextView) view.findViewById(R.id.history_redeem_balance_day);
            month = (TextView) view.findViewById(R.id.history_redeem_balance_month);
            year = (TextView) view.findViewById(R.id.history_redeem_balance_year);
            code = (TextView) view.findViewById(R.id.history_redeem_balance_code);
            balance = (TextView) view.findViewById(R.id.history_redeem_balance_balance);
            status = (TextView) view.findViewById(R.id.history_redeem_balance_status);
            actionResultLabel = (TextView) view.findViewById(R.id.history_redeem_balance_action_result_label);
            dateApproved = (TextView) view.findViewById(R.id.history_redeem_balance_date_approved);
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
        if (historyRedeemBalanceList.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROGRESS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.history_redeem_balance_list, parent, false);
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
            RedeemBalance redeemBalance = historyRedeemBalanceList.get(position);

            dateParts = ClassApplicationTool.with(context).convertToDateFormat(1, redeemBalance.getCreatedAt()).split(" ");
            ((ItemViewHolder) holder).day.setText(dateParts[0]);
            ((ItemViewHolder) holder).month.setText(dateParts[1].toUpperCase());
            ((ItemViewHolder) holder).year.setText(dateParts[2]);

            ((ItemViewHolder) holder).code.setText(redeemBalance.getRedeemCode());
            ((ItemViewHolder) holder).balance.setText(ClassApplicationTool.with(context).convertRpCurrency(redeemBalance.getBalance()));

            ((ItemViewHolder) holder).status.setText(statusType[redeemBalance.getStatus()]);
            ((ItemViewHolder) holder).status.setBackgroundResource(backgroundResource[redeemBalance.getStatus()]);

            if (redeemBalance.getStatus() == 1) {
                ((ItemViewHolder) holder).actionResultLabel.setText("Waktu Penyetujuan");
                ((ItemViewHolder) holder).dateApprovedContainer.setVisibility(View.VISIBLE);
            } else if (redeemBalance.getStatus() == 2) {
                ((ItemViewHolder) holder).actionResultLabel.setText("Waktu Penolakan");
                ((ItemViewHolder) holder).dateApprovedContainer.setVisibility(View.VISIBLE);
            } else {
                ((ItemViewHolder) holder).dateApprovedContainer.setVisibility(View.GONE);
            }
            ((ItemViewHolder) holder).dateApproved.setText(ClassApplicationTool.with(context)
                    .convertToDateTimeFormat(1, redeemBalance.getDateAgreement()));

            setAnimation(holder.itemView, position);
        } else if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).itemView.clearAnimation();
        }
    }

    @Override
    public int getItemCount() {
        return historyRedeemBalanceList.size();
    }
}