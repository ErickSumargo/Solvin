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
import android.widget.Toast;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.model.basic.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by Erick Sumargo on 10/28/2016.
 */

public class HistoryTransactionViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
    private RecyclerView historyTransactionView;
    private View itemView;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;
    private LinearLayoutManager layoutManager;

    private Context context;
    private Animation animation;

    //    OBJECT
    private List<Transaction> historyTransactionList;

    //    VARIABLE
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;
    private int totalItemCount, lastVisibleItem, visibleThreshold = 1, lastAnimatedPosition = -1;
    private int[] backgroundResource = {R.drawable.custom_transaction_status_pending,
            R.drawable.custom_transaction_status_actived,
            R.drawable.custom_transaction_status_cancelled};

    private String idCode, uniqueCode;
    private String[] statusType = {"Pending", "Berhasil", "Dibatalkan"};
    private String[] dateParts;

    private boolean loading = false;

    public HistoryTransactionViewAdapter(RecyclerView historyTransactionView, List<Transaction> historyTransactionList) {
        this.historyTransactionView = historyTransactionView;
        this.historyTransactionList = historyTransactionList;

        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) historyTransactionView.getLayoutManager();
    }

    private void setEvent() {
        historyTransactionView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadMore();
                    loading = true;

                    historyTransactionList.add(null);
                    notifyDataSetChanged();
                }

            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    public void updateList(List<Transaction> historyTransactionList) {
        this.historyTransactionList.clear();
        this.historyTransactionList.addAll(historyTransactionList);

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
        private View card;
        private TextView day, month, year, transactionCode, packageType, totalCost, transactionStatus;
//        private TextView packageValidUntil;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            card = view.findViewById(R.id.history_transaction_card);
            day = (TextView) view.findViewById(R.id.history_transaction_day);
            month = (TextView) view.findViewById(R.id.history_transaction_month);
            year = (TextView) view.findViewById(R.id.history_transaction_year);
            transactionCode = (TextView) view.findViewById(R.id.history_transaction_transaction_code);
            packageType = (TextView) view.findViewById(R.id.history_transaction_package_type);
//            packageValidUntil = (TextView) view.findViewById(R.id.history_transaction_package_valid_until);
            totalCost = (TextView) view.findViewById(R.id.history_transaction_total_cost);
            transactionStatus = (TextView) view.findViewById(R.id.history_transaction_transaction_status);
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
        if (historyTransactionList.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROGRESS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.history_transaction_list, parent, false);
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
            Transaction transaction = historyTransactionList.get(position);

//            try {
//                final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//                final Date date = dateFormat.parse(transaction.getCreated_at());
//                final Date now = dateFormat.parse((Calendar.getInstance()).getTime().toString());
//                Toast.makeText(context, Calendar.getInstance().getTime().toString(), Toast.LENGTH_SHORT).show();
//                if (date.compareTo(now) <= 0) {
//                    ((ItemViewHolder) holder).packageValidUntil.setVisibility(View.GONE);
//                } else {
//                    ((ItemViewHolder) holder).packageValidUntil.setVisibility(View.VISIBLE);
//                }
//            } catch (Exception e) {
//            }

            dateParts = ClassApplicationTool.with(context).convertToDateFormat(1, transaction.getCreated_at()).split(" ");
            ((ItemViewHolder) holder).day.setText(dateParts[0]);
            ((ItemViewHolder) holder).month.setText(dateParts[1].toUpperCase());
            ((ItemViewHolder) holder).year.setText(dateParts[2]);

            idCode = String.valueOf(transaction.getId());
            uniqueCode = String.valueOf(transaction.getUnique_code());
            while (idCode.length() < 3) {
                idCode = "0" + idCode;
            }
            while (uniqueCode.length() < 3) {
                uniqueCode = "0" + uniqueCode;
            }
            ((ItemViewHolder) holder).transactionCode.setText("SOL." + idCode + "." + uniqueCode);
            ((ItemViewHolder) holder).packageType.setText(String.valueOf(transaction.getPackage().getCredit()) + " Pertanyaan");
            ((ItemViewHolder) holder).totalCost.setText(ClassApplicationTool.with(context)
                    .convertRpCurrency((int) transaction.getPackage().getNominal() + transaction.getUnique_code()));

//            if (transaction.getPackage().getActive() == 7) {
//                ((ItemViewHolder) holder).packageValidUntil.setText("1 minggu (7 hari)");
//            } else if (transaction.getPackage().getActive() == 14) {
//                ((ItemViewHolder) holder).packageValidUntil.setText("2 minggu (14 hari)");
//            } else {
//                ((ItemViewHolder) holder).packageValidUntil.setText("1 bulan (30 hari)");
//            }

            ((ItemViewHolder) holder).transactionStatus.setText(statusType[transaction.getStatus()]);
            ((ItemViewHolder) holder).transactionStatus.setBackgroundResource(backgroundResource[transaction.getStatus()]);

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
        return historyTransactionList.size();
    }
}