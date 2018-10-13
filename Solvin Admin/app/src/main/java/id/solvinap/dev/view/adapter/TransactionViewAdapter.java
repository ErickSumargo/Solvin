package id.solvinap.dev.view.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataTransaction;
import id.solvinap.dev.view.helper.Tool;

import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by Erick Sumargo on 2/2/2017.
 */

public class TransactionViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
    private RecyclerView transactionView;
    private View itemView;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;
    private LinearLayoutManager layoutManager;

    private Context context;
    private Animation animation;

    //    OBJECT
    private List<DataTransaction> transactionList;

    //    VARIABLE
    private int totalItemCount, lastVisibleItem, visibleThreshold = 1;
    public int lastAnimatedPosition = -1;
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;
    private int[] backgroundResource = {R.drawable.custom_transaction_status_pending,
            R.drawable.custom_transaction_status_actived,
            R.drawable.custom_transaction_status_cancelled};

    private String idCode, uniqueCode;
    private String[] dateParts;
    private String[] statusType = {"Pending", "Lunas", "Dibatalkan"};

    private boolean loading = false;

    public TransactionViewAdapter(RecyclerView transactionView, List<DataTransaction> transactionList) {
        this.transactionView = transactionView;
        this.transactionList = transactionList;

        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) transactionView.getLayoutManager();
    }

    private void setEvent() {
        transactionView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        private TextView day, month, year, code, packageType, cost, packageValidUntil, status;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            day = (TextView) view.findViewById(R.id.transaction_day);
            month = (TextView) view.findViewById(R.id.transaction_month);
            year = (TextView) view.findViewById(R.id.transaction_year);
            code = (TextView) view.findViewById(R.id.transaction_code);
            packageType = (TextView) view.findViewById(R.id.transaction_package_type);
            packageValidUntil = (TextView) view.findViewById(R.id.transaction_package_valid_until);
            cost = (TextView) view.findViewById(R.id.transaction_cost);
            status = (TextView) view.findViewById(R.id.transaction_status);
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
                    R.layout.transaction_list, parent, false);
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
            DataTransaction dataTransaction = transactionList.get(position);

            dateParts = Tool.getInstance(context).convertToDateFormat(1, dataTransaction.getCreatedAt()).split(" ");
            ((ItemViewHolder) holder).day.setText(dateParts[0]);
            ((ItemViewHolder) holder).month.setText(dateParts[1].toUpperCase());
            ((ItemViewHolder) holder).year.setText(dateParts[2]);

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
        return transactionList.size();
    }
}