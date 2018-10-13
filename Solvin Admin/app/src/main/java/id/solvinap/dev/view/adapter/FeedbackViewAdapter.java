package id.solvinap.dev.view.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.transition.TransitionManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;


import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataFeedback;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.view.helper.Tool;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by Erick Sumargo on 2/6/2017.
 */

public class FeedbackViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //    INTERFACE
    public interface OnLoadMoreListener {
        void OnLoadMore();
    }

    public interface OnSetReadStatusListener {
        void OnSetReadStatus(int id);
    }

    private static OnLoadMoreListener mLoadMoreListener;
    private static OnSetReadStatusListener mSetReadStatusListener;

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public void setOnReadStatusListener(OnSetReadStatusListener listener) {
        mSetReadStatusListener = listener;
    }

    private void loadMore() {
        if (mLoadMoreListener != null) {
            mLoadMoreListener.OnLoadMore();
        }
    }

    private void setReadStatus(int id) {
        if (mSetReadStatusListener != null) {
            mSetReadStatusListener.OnSetReadStatus(id);
        }
    }

    //    VIEW
    private RecyclerView feedbackView;
    private View itemView;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;
    private LinearLayoutManager layoutManager;

    private Context context;
    private Animation animation;
    private GradientDrawable gradientDrawable;

    //    OBJECT
    private List<DataFeedback> feedbackList;
    private List<Integer> readPosition, toggledPosition;

    //    VARIABLE
    private int totalItemCount, lastVisibleItem, visibleThreshold = 1, expandedPosition = -1;
    public int lastAnimatedPosition = -1;
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;

    private SpannableStringBuilder boldText;

    private boolean loading = false;

    public FeedbackViewAdapter(RecyclerView feedbackView, List<DataFeedback> feedbackList) {
        this.feedbackView = feedbackView;
        this.feedbackList = feedbackList;

        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) feedbackView.getLayoutManager();

        readPosition = new ArrayList<>();
        toggledPosition = new ArrayList<>();
    }

    private void setEvent() {
        feedbackView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadMore();
                    loading = true;

                    feedbackList.add(null);
                    notifyDataSetChanged();
                }

            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    public void updateList(List<DataFeedback> feedbackList) {
        this.feedbackList.clear();
        this.feedbackList.addAll(feedbackList);

        notifyDataSetChanged();
    }

    private void expandContent(ItemViewHolder holder, DataFeedback dataFeedback) {
        if (holder.newFlag.getVisibility() == View.VISIBLE) {
            holder.newFlag.setVisibility(View.GONE);
            holder.title.setText(dataFeedback.getTitle());
            holder.content.setText(dataFeedback.getContent());
            holder.userName.setText(dataFeedback.getDataAuth().getName());
            holder.time.setText(Tool.getInstance(context).convertToDateTimeFormat(0, dataFeedback.getCreatedAt()));
        }
        holder.content.setMaxLines(Integer.MAX_VALUE);
        TransitionManager.beginDelayedTransition(holder.cardView);
    }

    private void collapseContent(ItemViewHolder holder) {
        holder.content.setMaxLines(3);
    }

    private CharSequence setBoldStyle(String text) {
        boldText = new SpannableStringBuilder(text);
        boldText.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return boldText;
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
        private RelativeLayout avatarLayout;
        private CircleImageView photo;
        private TextView userName, newFlag, avatarInitial, title, content, time;

        private int photoMode, avatarColor;
        private boolean expanded = false;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            cardView = (CardView) view.findViewById(R.id.feedback_card);
            avatarLayout = (RelativeLayout) view.findViewById(R.id.feedback_avatar_layout);
            photo = (CircleImageView) view.findViewById(R.id.feedback_photo);
            userName = (TextView) view.findViewById(R.id.feedback_user_name);
            newFlag = (TextView) view.findViewById(R.id.feedback_new_flag);
            avatarInitial = (TextView) view.findViewById(R.id.feedback_avatar_initial);
            title = (TextView) view.findViewById(R.id.feedback_title);
            content = (TextView) view.findViewById(R.id.feedback_content);
            time = (TextView) view.findViewById(R.id.feedback_time);
        }

        public void clearAnimation() {
            cardView.clearAnimation();
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
        if (feedbackList.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROGRESS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.feedback_list, parent, false);
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
            final DataFeedback dataFeedback = feedbackList.get(position);

            if (dataFeedback.getAuthType().equals("student")) {
                if (dataFeedback.getDataAuth().getPhoto().length() > 0) {
                    Picasso.with(context).load(Global.ASSETS_URL + "student" + "/" + dataFeedback.getDataAuth().getPhoto())
                            .placeholder(R.drawable.operator)
                            .fit()
                            .centerCrop()
                            .into(((ItemViewHolder) holder).photo);

                    ((ItemViewHolder) holder).avatarLayout.setVisibility(View.GONE);
                    ((ItemViewHolder) holder).photo.setVisibility(View.VISIBLE);
                } else {
                    gradientDrawable = (GradientDrawable) ((ItemViewHolder) holder).avatarLayout.getBackground();
                    gradientDrawable.setColor(Tool.getInstance(context).getAvatarColor(dataFeedback.getDataAuth().getId()));

                    ((ItemViewHolder) holder).avatarInitial.setText(Tool.getInstance(context).getInitialName(dataFeedback.getDataAuth().getName()));

                    ((ItemViewHolder) holder).avatarLayout.setVisibility(View.VISIBLE);
                    ((ItemViewHolder) holder).photo.setVisibility(View.GONE);
                }
            } else {
                Picasso.with(context).load(Global.ASSETS_URL + "mentor" + "/" + dataFeedback.getDataAuth().getPhoto())
                        .placeholder(R.drawable.operator)
                        .fit()
                        .centerCrop()
                        .into(((ItemViewHolder) holder).photo);

                ((ItemViewHolder) holder).avatarLayout.setVisibility(View.GONE);
                ((ItemViewHolder) holder).photo.setVisibility(View.VISIBLE);
            }

            if (dataFeedback.getRead() == 1) {
                ((ItemViewHolder) holder).newFlag.setVisibility(View.GONE);
                ((ItemViewHolder) holder).userName.setText(dataFeedback.getDataAuth().getName());
                ((ItemViewHolder) holder).title.setText(dataFeedback.getTitle());
                ((ItemViewHolder) holder).content.setText(dataFeedback.getContent());
                ((ItemViewHolder) holder).time.setText(Tool.getInstance(context).convertToDateTimeFormat(0, dataFeedback.getCreatedAt()));
            } else {
                if (toggledPosition.contains(holder.getAdapterPosition())) {
                    ((ItemViewHolder) holder).newFlag.setVisibility(View.GONE);
                    ((ItemViewHolder) holder).userName.setText(dataFeedback.getDataAuth().getName());
                    ((ItemViewHolder) holder).title.setText(dataFeedback.getTitle());
                    ((ItemViewHolder) holder).content.setText(dataFeedback.getContent());
                    ((ItemViewHolder) holder).time.setText(Tool.getInstance(context).convertToDateTimeFormat(0, dataFeedback.getCreatedAt()));
                } else {
                    ((ItemViewHolder) holder).newFlag.setVisibility(View.VISIBLE);
                    ((ItemViewHolder) holder).userName.setText(setBoldStyle(dataFeedback.getDataAuth().getName()));
                    ((ItemViewHolder) holder).title.setText(setBoldStyle(dataFeedback.getTitle()));
                    ((ItemViewHolder) holder).content.setText(setBoldStyle(dataFeedback.getContent()));
                    ((ItemViewHolder) holder).time.setText(setBoldStyle(Tool.getInstance(context).convertToDateTimeFormat(0, dataFeedback.getCreatedAt())));
                }
            }

            if (holder.getAdapterPosition() == expandedPosition) {
                ((ItemViewHolder) holder).content.setMaxLines(Integer.MAX_VALUE);
            } else {
                ((ItemViewHolder) holder).content.setMaxLines(3);
            }

            ((ItemViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dataFeedback.getRead() == 0) {
                        if (!readPosition.contains(dataFeedback.getId())) {
                            readPosition.add(dataFeedback.getId());
                            setReadStatus(dataFeedback.getId());
                        }
                    }
                    if (!((ItemViewHolder) holder).expanded) {
                        expandContent((ItemViewHolder) holder, dataFeedback);

                        expandedPosition = holder.getAdapterPosition();
                        toggledPosition.add(expandedPosition);
                        ((ItemViewHolder) holder).expanded = true;
                    } else {
                        collapseContent((ItemViewHolder) holder);

                        expandedPosition = -1;
                        ((ItemViewHolder) holder).expanded = false;
                    }
                }
            });

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
        return feedbackList.size();
    }
}