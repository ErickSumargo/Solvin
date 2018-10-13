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
import id.solvinap.dev.view.helper.Tool;

import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by Erick Sumargo on 2/3/2017.
 */

public class RedeemBalanceViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //    INTERFACE
    public interface OnLoadMoreListener {
        void OnLoadMore();
    }

    private static OnLoadMoreListener mLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public void loadMore() {
        if (mLoadMoreListener != null) {
            mLoadMoreListener.OnLoadMore();
        }
    }

    //    VIEW
    private RecyclerView redeemBalanceView;
    private View itemView;

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

    private String[] dateParts;
    private String[] statusType = {"Pending", "Berhasil", "Dibatalkan"};

    private boolean loading = false;

    public RedeemBalanceViewAdapter(RecyclerView redeemBalanceView, List<DataRedeemBalance> redeemBalanceList) {
        this.redeemBalanceView = redeemBalanceView;
        this.redeemBalanceList = redeemBalanceList;

        init();
        setEvent();
    }

    private void init(){
        layoutManager = (LinearLayoutManager) redeemBalanceView.getLayoutManager();
    }

    private void setEvent() {
        redeemBalanceView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        private TextView day, month, year, code, balance, status, actionResultLabel, dateApproved;
        private LinearLayout dateApprovedContainer;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            day = (TextView) view.findViewById(R.id.redeem_balance_day);
            month = (TextView) view.findViewById(R.id.redeem_balance_month);
            year = (TextView) view.findViewById(R.id.redeem_balance_year);
            code = (TextView) view.findViewById(R.id.redeem_balance_code);
            balance = (TextView) view.findViewById(R.id.redeem_balance_balance);
            status = (TextView) view.findViewById(R.id.redeem_balance_status);
            actionResultLabel = (TextView) view.findViewById(R.id.redeem_balance_action_result_label);
            dateApprovedContainer = (LinearLayout) view.findViewById(R.id.redeem_balance_date_approved_container);
            dateApproved = (TextView) view.findViewById(R.id.redeem_balance_date_approved);
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
                    R.layout.redeem_balance_list, parent, false);
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
            DataRedeemBalance dataRedeemBalance = redeemBalanceList.get(position);
            dateParts = Tool.getInstance(context).convertToDateFormat(1, dataRedeemBalance.getCreatedAt()).split(" ");

            ((ItemViewHolder) holder).day.setText(dateParts[0]);
            ((ItemViewHolder) holder).month.setText(dateParts[1].toUpperCase());
            ((ItemViewHolder) holder).year.setText(dateParts[2]);

            ((ItemViewHolder) holder).code.setText(dataRedeemBalance.getRedeemCode());
            ((ItemViewHolder) holder).balance.setText(Tool.getInstance(context).convertRpCurrency(dataRedeemBalance.getBalance()));
            ((ItemViewHolder) holder).status.setText(statusType[dataRedeemBalance.getStatus()]);
            ((ItemViewHolder) holder).status.setBackgroundResource(backgroundResource[dataRedeemBalance.getStatus()]);

            if (dataRedeemBalance.getDateAgreement() == null) {
                ((ItemViewHolder) holder).dateApprovedContainer.setVisibility(View.GONE);
            } else {
                if (dataRedeemBalance.getStatus() == 1) {
                    ((ItemViewHolder) holder).actionResultLabel.setText("Waktu Penyetujuan");
                } else {
                    ((ItemViewHolder) holder).actionResultLabel.setText("Waktu Penolakan");
                }
                ((ItemViewHolder) holder).dateApproved.setText(Tool.getInstance(context).convertToDateTimeFormat(1, dataRedeemBalance.getDateAgreement()));
                ((ItemViewHolder) holder).dateApprovedContainer.setVisibility(View.VISIBLE);
            }

            setAnimation(((ItemViewHolder) holder).itemView, position);
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
        return redeemBalanceList.size();
    }
}