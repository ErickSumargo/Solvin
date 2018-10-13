package id.solvin.dev.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Global;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.Notification;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.presenter.NotificationPresenter;
import id.solvin.dev.view.activities.ActivityQuestionDetail;
import id.solvin.dev.view.activities.ActivityTransactionStatus;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

import static android.media.CamcorderProfile.get;

/**
 * Created by Erick Sumargo on 9/1/2016.
 */
public class NotificationViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
    private RecyclerView notificationView;
    private View itemView;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;
    private LinearLayoutManager layoutManager;

    private Activity activity;

    private Context context;
    private Animation animation;

    private GradientDrawable gradientDrawable;

    //    OBJECT
    private List<Notification> notificationList;
    private ClassApplicationTool classApplicationTool;
    //    VARIABLE
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;
    private int totalItemCount, lastVisibleItem, visibleThreshold = 1, lastAnimatedPosition = -1;

    private boolean loading = false;
    private NotificationPresenter presenter;
    private Intent intent;

    public NotificationViewAdapter(RecyclerView notificationView, List<Notification> notificationList, Activity activity) {
        this.notificationView = notificationView;
        this.notificationList = notificationList;
        this.activity = activity;
        classApplicationTool = new ClassApplicationTool(activity.getApplicationContext());
        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) notificationView.getLayoutManager();
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

    public void updateList(List<Notification> notificationList) {
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

        private int subjectPhotoMode, avatarColor;

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
        } else if (viewType == VIEW_PROGRESS) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.footer_loader, parent, false);
            mainHolder = new ProgressViewHolder(itemView);
        }
        return mainHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final Notification dataNotification = notificationList.get(position);
            if (dataNotification.getStatus() == 1) {
                ((ItemViewHolder) holder).card.setCardBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
                ((ItemViewHolder) holder).newFlag.setVisibility(View.GONE);
            } else {
                ((ItemViewHolder) holder).card.setCardBackgroundColor(context.getResources().getColor(R.color.colorNewNotification));
                ((ItemViewHolder) holder).newFlag.setVisibility(View.VISIBLE);
            }

            if (dataNotification.getSender_type().equals(Auth.AUTH_TYPE_STUDENT) || dataNotification.getSender_type().equals(Auth.AUTH_TYPE_MENTOR)) {
                if (dataNotification.getPhoto().isEmpty()) {
                    ((ItemViewHolder) holder).avatarColor = classApplicationTool.getAvatarColor(dataNotification.getSender_id());
                    gradientDrawable = (GradientDrawable) ((ItemViewHolder) holder).avatarLayout.getBackground();
                    gradientDrawable.setColor(((ItemViewHolder) holder).avatarColor);

                    ((ItemViewHolder) holder).avatarInitial.setText(Global.get().getInitialName(dataNotification.getSender_name()));

                    ((ItemViewHolder) holder).avatarLayout.setVisibility(View.VISIBLE);
                    ((ItemViewHolder) holder).subjectPhoto.setVisibility(View.GONE);
                } else {
                    Picasso.with(context).load(Global.get().getAssetURL(dataNotification.getPhoto(), dataNotification.getSender_type()))
                            .placeholder(R.drawable.operator)
                            .fit()
                            .centerCrop()
                            .into(((ItemViewHolder) holder).subjectPhoto);

                    ((ItemViewHolder) holder).avatarLayout.setVisibility(View.GONE);
                    ((ItemViewHolder) holder).subjectPhoto.setVisibility(View.VISIBLE);
                }
            } else {
                ((ItemViewHolder) holder).subjectPhoto.setImageResource(R.drawable.admin_primary);

                ((ItemViewHolder) holder).avatarLayout.setVisibility(View.GONE);
                ((ItemViewHolder) holder).subjectPhoto.setVisibility(View.VISIBLE);

            }
            if (!dataNotification.getType().equals("transaction")) {
                ((ItemViewHolder) holder).contentSummary.setText(String.format(dataNotification.getContent(), dataNotification.getSender_name()));
            } else {
                ((ItemViewHolder) holder).contentSummary.setText(dataNotification.getContent());

            }
            ((ItemViewHolder) holder).time.setText(Global.get()
                    .convertDateToFormat(dataNotification.getCreated_at(), "EEE, dd MMM ''yy-HH:mm") + " WIB");

            ((ItemViewHolder) holder).card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dataNotification.getStatus() == 0) {
                        presenter = new NotificationPresenter(new IBaseResponse() {
                            @Override
                            public void onSuccess(Response response) {
                                dataNotification.setStatus(1);
                                notificationList.set(position, dataNotification);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(String message) {
                                final Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                                ClassApplicationTool.with(context).resizeToast(toast);
                                toast.show();
                            }
                        });

                        presenter.readNotification(dataNotification.getId(), context);
                    }
                    if (dataNotification.getSubject_type().equals("question")) {
                        intent = new Intent(v.getContext(), ActivityQuestionDetail.class);
                        intent.putExtra("question_id", dataNotification.getSubject_id());
                        startActivity(intent);
                    } else if (dataNotification.getSubject_type().equals("transaction")) {
                        intent = new Intent(v.getContext(), ActivityTransactionStatus.class);
                        intent.putExtra("transaction_id", dataNotification.getSubject_id());
                        startActivity(intent);
                    }
                }
            });

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
        return notificationList.size();
    }

    private void startActivity(Intent intent){
        context.startActivity(intent);
    }
}