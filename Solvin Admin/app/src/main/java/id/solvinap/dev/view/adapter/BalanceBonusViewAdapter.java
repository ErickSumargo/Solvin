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

import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataBalanceBonus;
import id.solvinap.dev.view.helper.Tool;

/**
 * Created by Erick Sumargo on 4/11/2017.
 */

public class BalanceBonusViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
    private RecyclerView balanceBonusView;
    private View itemView;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;
    private LinearLayoutManager layoutManager;

    private Context context;
    private Animation animation;

    //    OBJECT
    private List<DataBalanceBonus> balanceBonusList;

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

    public BalanceBonusViewAdapter(RecyclerView balanceBonusView, List<DataBalanceBonus> balanceBonusList) {
        this.balanceBonusView = balanceBonusView;
        this.balanceBonusList = balanceBonusList;

        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) balanceBonusView.getLayoutManager();
    }

    private void setEvent() {
        balanceBonusView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadMore();
                    loading = true;

                    balanceBonusList.add(null);
                    notifyDataSetChanged();
                }

            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    public void updateList(List<DataBalanceBonus> balanceBonusList) {
        this.balanceBonusList.clear();
        this.balanceBonusList.addAll(balanceBonusList);

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
        private TextView day, month, year, best, dealPayment, balance;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            day = (TextView) view.findViewById(R.id.balance_bonus_day);
            month = (TextView) view.findViewById(R.id.balance_bonus_month);
            year = (TextView) view.findViewById(R.id.balance_bonus_year);
            best = (TextView) view.findViewById(R.id.balance_bonus_best);
            dealPayment = (TextView) view.findViewById(R.id.balance_bonus_deal_payment);
            balance = (TextView) view.findViewById(R.id.balance_bonus_balance);
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
        if (balanceBonusList.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROGRESS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.balance_bonus_list, parent, false);
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
            DataBalanceBonus dataBalanceBonus = balanceBonusList.get(position);
            dateParts = Tool.getInstance(context).convertToDateFormat(1, dataBalanceBonus.getCreatedAt()).split(" ");

            ((ItemViewHolder) holder).day.setText(dateParts[0]);
            ((ItemViewHolder) holder).month.setText(dateParts[1].toUpperCase());
            ((ItemViewHolder) holder).year.setText(dateParts[2]);

            ((ItemViewHolder) holder).best.setText(String.valueOf(dataBalanceBonus.getCount()));
            ((ItemViewHolder) holder).dealPayment.setText(Tool.getInstance(context).convertRpCurrency(dataBalanceBonus.getDealPayment()));
            ((ItemViewHolder) holder).balance.setText(Tool.getInstance(context).convertRpCurrency(dataBalanceBonus.getBalance()));

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
        return balanceBonusList.size();
    }
}