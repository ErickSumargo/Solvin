package id.solvinap.dev.view.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;


import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataNotification;
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

public class NotificationViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
    private RecyclerView notificationView;
    private View itemView;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;
    private LinearLayoutManager layoutManager;

    private Context context;

    private GradientDrawable gradientDrawable;
    private Animation animation;

    //    OBJECT
    private List<DataNotification> notificationList;
    private List<Integer> readPosition;

    //    VARIABLE
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;
    private int totalItemCount, lastVisibleItem, visibleThreshold = 1;
    public int lastAnimatedPosition = -1;

    private boolean loading = false;

    public NotificationViewAdapter(RecyclerView notificationView, List<DataNotification> notificationList) {
        this.notificationView = notificationView;
        this.notificationList = notificationList;

        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) notificationView.getLayoutManager();

        readPosition = new ArrayList<>();
    }

    private void setEvent() {
        notificationView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadMore();
                    loading = true;

                    notificationList.add(null);
                    notifyDataSetChanged();
                }
            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    public void updateList(List<DataNotification> notificationList) {
        this.notificationList.clear();
        this.notificationList.addAll(notificationList);

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
        private CardView card;
        private RelativeLayout avatarLayout;
        private CircleImageView subjectPhoto;
        private TextView newFlag, avatarInitial, contentSummary, time;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            card = (CardView) view.findViewById(R.id.notification_card);
            avatarLayout = (RelativeLayout) view.findViewById(R.id.notification_avatar_layout);

            subjectPhoto = (CircleImageView) view.findViewById(R.id.notification_subject_photo);
            newFlag = (TextView) view.findViewById(R.id.notification_new_flag);
            avatarInitial = (TextView) view.findViewById(R.id.notification_avatar_initial);
            contentSummary = (TextView) view.findViewById(R.id.notification_content_summary);
            time = (TextView) view.findViewById(R.id.notification_time);
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
        if (notificationList.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROGRESS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.notification_list, parent, false);
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
            final DataNotification dataNotification = notificationList.get(position);

            if (dataNotification.getStatus() == 0) {
                if (readPosition.contains(dataNotification.getId())) {
                    ((ItemViewHolder) holder).card.setCardBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
                    ((ItemViewHolder) holder).newFlag.setVisibility(View.GONE);
                } else {
                    ((ItemViewHolder) holder).card.setCardBackgroundColor(context.getResources().getColor(R.color.colorNewNotification));
                    ((ItemViewHolder) holder).newFlag.setVisibility(View.VISIBLE);
                }
            } else {
                ((ItemViewHolder) holder).card.setCardBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
                ((ItemViewHolder) holder).newFlag.setVisibility(View.GONE);
            }

            if (dataNotification.getPhoto().isEmpty()) {
                gradientDrawable = (GradientDrawable) ((ItemViewHolder) holder).avatarLayout.getBackground();
                gradientDrawable.setColor(Tool.getInstance(context).getAvatarColor(dataNotification.getSender_id()));

                ((ItemViewHolder) holder).avatarInitial.setText(Tool.getInstance(context).getInitialName(dataNotification.getSender_name()));

                ((ItemViewHolder) holder).avatarLayout.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).subjectPhoto.setVisibility(View.GONE);
            } else {
                Picasso.with(context).load(Global.ASSETS_URL + dataNotification.getSender_type() + "/" + dataNotification.getPhoto())
                        .placeholder(R.drawable.operator)
                        .fit()
                        .centerCrop()
                        .into(((ItemViewHolder) holder).subjectPhoto);

                ((ItemViewHolder) holder).avatarLayout.setVisibility(View.GONE);
                ((ItemViewHolder) holder).subjectPhoto.setVisibility(View.VISIBLE);
            }

            ((ItemViewHolder) holder).contentSummary.setText(String.format(dataNotification.getContent(), dataNotification.getSender_name()));
            ((ItemViewHolder) holder).time.setText(Tool.getInstance(context).convertToDateTimeFormat(0, dataNotification.getCreated_at()));

            ((ItemViewHolder) holder).card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dataNotification.getStatus() == 0) {
                        if (!readPosition.contains(dataNotification.getId())) {
                            setReadStatus(dataNotification.getId());
                            readPosition.add(dataNotification.getId());
                        }

                        ((ItemViewHolder) holder).card.setCardBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
                        ((ItemViewHolder) holder).newFlag.setVisibility(View.GONE);
                    }
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
        return notificationList.size();
    }
}