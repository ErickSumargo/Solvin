package id.solvinap.dev.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataMentor;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.view.activity.ActivityMentorDetail;
import id.solvinap.dev.view.helper.Tool;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

import static android.media.CamcorderProfile.get;

/**
 * Created by Erick Sumargo on 2/1/2017.
 */

public class MentorFeedViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //    INTERFACE
    public interface OnCollapseSearchView {
        void OnCollapseSearchView();
    }

    public interface OnLoadMoreListener {
        void OnLoadMore();
    }

    private static OnCollapseSearchView mCollapseSearchView;
    private static OnLoadMoreListener mLoadMoreListener;

    public void setOnCollapseSearchView(OnCollapseSearchView listener) {
        mCollapseSearchView = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    private void collapseSearchView() {
        if (mCollapseSearchView != null) {
            mCollapseSearchView.OnCollapseSearchView();
        }
    }

    private void loadMore() {
        if (mLoadMoreListener != null) {
            mLoadMoreListener.OnLoadMore();
        }
    }

    //    VIEW
    private RecyclerView mentorFeedView;
    private View itemView;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;
    private LinearLayoutManager layoutManager;

    private Context context;
    private Animation animation;
    private Intent intent;

    //    OBJECT
    private List<DataMentor> mentorList;

    //    VARIABLE
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;
    private int totalItemCount, lastVisibleItem, visibleThreshold = 1;
    public int lastAnimatedPosition = -1;

    private boolean loading = false;

    public MentorFeedViewAdapter(RecyclerView mentorFeedView, List<DataMentor> mentorList) {
        this.mentorFeedView = mentorFeedView;
        this.mentorList = mentorList;

        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) mentorFeedView.getLayoutManager();
    }

    private void setEvent() {
        mentorFeedView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadMore();
                    loading = true;

                    mentorList.add(null);
                    notifyDataSetChanged();
                }
            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    public void updateList(List<DataMentor> mentorList) {
        this.mentorList.clear();
        this.mentorList.addAll(mentorList);

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
        private CardView cardView;
        private CircleImageView photo;
        private TextView name, joinDate, totalBalance, totalBest_Solution, totalComment, totalRedeemBalance;
        private ImageView blocked;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            cardView = (CardView) view.findViewById(R.id.mentor_list_card);
            photo = (CircleImageView) view.findViewById(R.id.mentor_list_photo);
            name = (TextView) view.findViewById(R.id.mentor_list_name);
            joinDate = (TextView) view.findViewById(R.id.mentor_list_join_date);
            blocked = (ImageView) view.findViewById(R.id.mentor_list_blocked);
            totalBalance = (TextView) view.findViewById(R.id.mentor_list_total_balance);
            totalBest_Solution = (TextView) view.findViewById(R.id.mentor_list_total_best__solution);
            totalComment = (TextView) view.findViewById(R.id.mentor_list_total_comment);
            totalRedeemBalance = (TextView) view.findViewById(R.id.mentor_list_total_redeem_balance);
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
        if (mentorList.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROGRESS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.mentor_list, parent, false);
            mainHolder = new ItemViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.footer_loader, parent, false);
            mainHolder = new ProgressViewHolder(itemView);
        }
        return mainHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            final DataMentor dataMentor = mentorList.get(position);

            Picasso.with(context).load(Global.ASSETS_URL + "mentor" + "/" + dataMentor.getPhoto())
                    .placeholder(R.drawable.operator)
                    .fit()
                    .centerCrop()
                    .into(((ItemViewHolder) holder).photo);

            ((ItemViewHolder) holder).name.setText(dataMentor.getName());
            ((ItemViewHolder) holder).joinDate.setText(Tool.getInstance(context).convertToDateTimeFormat(0, dataMentor.getJoinDate()));
            ((ItemViewHolder) holder).blocked.setVisibility(dataMentor.getActive() == 1 ? View.GONE : View.VISIBLE);

            ((ItemViewHolder) holder).totalBalance.setText(Tool.getInstance(context).convertRpCurrency(dataMentor.getTotalBalance() + dataMentor.getTotalBalanceBonus()));
            ((ItemViewHolder) holder).totalBest_Solution.setText(dataMentor.getTotalBestSolution() + "/" + dataMentor.getTotalSolution());
            ((ItemViewHolder) holder).totalComment.setText(String.valueOf(dataMentor.getTotalComment()));
            ((ItemViewHolder) holder).totalRedeemBalance.setText(String.valueOf(dataMentor.getTotalRedeemBalance()));

            ((ItemViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent = new Intent(context, ActivityMentorDetail.class);
                    intent.putExtra(Global.PREFERENCES_INTENT_EXTRA, dataMentor);
                    context.startActivity(intent);

                    collapseSearchView();
                }
            });

            setAnimation(((ItemViewHolder) holder).itemView, position);
        } else {
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
        return mentorList.size();
    }
}