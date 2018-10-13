package id.solvin.dev.view.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.model.basic.MonthlyBalance;

import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by Erick Sumargo on 1/18/2017.
 */

public class BalanceInfoDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
    private RecyclerView balanceInfoDetailView;
    private View itemView;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;
    private LinearLayoutManager layoutManager;

    private Context context;
    private Animation animation;

    //    OBJECT
    private List<MonthlyBalance> balanceInfoDetailList;

    //    VARIABLE
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;
    private int totalItemCount, lastVisibleItem, visibleThreshold = 1;
    public int lastAnimatedPosition = -1;

    private boolean loading = false;

    public BalanceInfoDetailAdapter(RecyclerView balanceInfoDetailView, List<MonthlyBalance> balanceInfoDetailList) {
        this.balanceInfoDetailView = balanceInfoDetailView;
        this.balanceInfoDetailList = balanceInfoDetailList;

        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) balanceInfoDetailView.getLayoutManager();
    }

    private void setEvent() {
        balanceInfoDetailView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadMore();
                    loading = true;

                    balanceInfoDetailList.add(null);
                    notifyDataSetChanged();
                }

            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    public void updateList(List<MonthlyBalance> dataStudentList) {
        this.balanceInfoDetailList.clear();
        this.balanceInfoDetailList.addAll(dataStudentList);

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
        private TextView bestSolution, balance, balanceBonus, dateStart, dateEnd;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            bestSolution = (TextView) view.findViewById(R.id.balance_info_detail_best_solution);
            balance = (TextView) view.findViewById(R.id.balance_info_detail_balance);
            balanceBonus = (TextView) view.findViewById(R.id.balance_info_detail_balance_bonus);
            dateStart = (TextView) view.findViewById(R.id.balance_info_detail_date_start);
            dateEnd = (TextView) view.findViewById(R.id.balance_info_detail_date_end);
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
        if (balanceInfoDetailList.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROGRESS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.balance_info_detail_list, parent, false);
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
            MonthlyBalance dataMonthlyBalance = balanceInfoDetailList.get(position);
            ((ItemViewHolder) holder).dateStart.setText(dataMonthlyBalance.getDateStart() + " s/d");
            ((ItemViewHolder) holder).dateEnd.setText(dataMonthlyBalance.getDateEnd());
            ((ItemViewHolder) holder).bestSolution.setText(String.valueOf(dataMonthlyBalance.getBestVote()));
            ((ItemViewHolder) holder).balance.setText(ClassApplicationTool.with(context).convertRpCurrency((int) dataMonthlyBalance.getBalance()));
            ((ItemViewHolder) holder).balanceBonus.setText(ClassApplicationTool.with(context).convertRpCurrency((int) dataMonthlyBalance.getBalanceBonus()));

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
        return balanceInfoDetailList.size();
    }
}