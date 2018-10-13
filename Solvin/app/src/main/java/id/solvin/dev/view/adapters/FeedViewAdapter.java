package id.solvin.dev.view.adapters;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.ConfigApp;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.Question;
import id.solvin.dev.model.basic.Solution;
import id.solvin.dev.view.activities.ActivityQuestionDetail;
import id.solvin.dev.view.activities.MainActivity;
import id.solvin.dev.view.widget.ClassRichEditText;

import static android.R.attr.bitmap;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by Erick Sumargo on 8/31/2016.
 */
public class FeedViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //    INTERFACE
    public interface OnCollapseHomeSearchViewListener {
        void OnCollapseHomeSearchView();
    }

    public interface OnLoadMoreListener {
        void OnLoadMore();
    }

    private static OnCollapseHomeSearchViewListener mCollapseHomeSearchViewListener;

    private static OnLoadMoreListener mLoadMoreListener;

    public void setOnCollapseHomeSearchViewListener(OnCollapseHomeSearchViewListener listener) {
        mCollapseHomeSearchViewListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    private void collapseHomeSearchView() {
        if (mCollapseHomeSearchViewListener != null) {
            mCollapseHomeSearchViewListener.OnCollapseHomeSearchView();
        }
    }

    private void loadMore() {
        if (mLoadMoreListener != null) {
            mLoadMoreListener.OnLoadMore();
        }
    }

    //    VIEW
    private RecyclerView feedView;
    private View itemView;

    private ClassRichEditText tempRT;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;

    private LinearLayoutManager layoutManager;
    private Activity activity;

    private Context context;

    private Animation animation;
    private Intent intent;
    private GradientDrawable gradientDrawable;

    //    OBJECT
    private List<Question> feedList;

    //    VARIABLE
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;

    private int totalItemCount, lastVisibleItem, visibleThreshold = 1;
    public int lastAnimatedPosition = -1;
    private int avatarColor;
    private int[] questionStatusDrawable = {id.solvin.dev.R.drawable.ic_pending, id.solvin.dev.R.drawable.ic_discussion, id.solvin.dev.R.drawable.ic_complete};
    private int[] flagResourceList = {id.solvin.dev.R.drawable.hashtag_mathematics, id.solvin.dev.R.drawable.hashtag_physics, R.drawable.hashtag_chemistry};

    private boolean loading = false;

    /**
     * Firebase Realtime Database
     */
    private boolean related = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferencePertanyaan;

    private android.app.Notification notification;
    private Notification.Builder noticationBuilder;
    private NotificationManager notificationManager;

    public FeedViewAdapter(RecyclerView feedView, List<Question> feedList, ClassRichEditText tempRT, Activity activity) {
        this.feedView = feedView;
        this.feedList = feedList;
        this.tempRT = tempRT;
        this.activity = activity;

        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) feedView.getLayoutManager();
    }

    private void setEvent() {
        feedView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadMore();
                    loading = true;

                    feedList.add(null);
                    notifyDataSetChanged();
                }

            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    public void updateList(List<Question> feedList) {
        this.feedList.clear();
        this.feedList.addAll(feedList);

        notifyDataSetChanged();
    }

    public void updateList(List<Question> feedList, boolean related) {
        this.feedList.clear();
        this.feedList.addAll(feedList);
        this.related = related;
        if (related) {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReferencePertanyaan = firebaseDatabase.getReference("question");
        }
        notifyDataSetChanged();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastAnimatedPosition) {
            animation = AnimationUtils.loadAnimation(context, id.solvin.dev.R.anim.load_feed);
            viewToAnimate.startAnimation(animation);

            lastAnimatedPosition = position;
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private View feedCard;
        private RelativeLayout avatarLayout, mentorContainer;
        private CircleImageView userPhoto, mentorPhoto;
        private ImageView questionAttachment, questionStatus, bestSolutionAttachment;
        private TextView avatarInitial, userName, questionTime, questionSummary,
                questionSubject, questionMaterial, mentorName, bestSolutionTime;

        private SpinKitView solutionRealtimeStatus;

        public ItemViewHolder(View view) {
            super(view);

            context = view.getContext();
            feedCard = view.findViewById(id.solvin.dev.R.id.feed_card);

            avatarLayout = (RelativeLayout) view.findViewById(id.solvin.dev.R.id.feed_avatar_layout);
            mentorContainer = (RelativeLayout) view.findViewById(id.solvin.dev.R.id.feed_mentor_container);

            userPhoto = (CircleImageView) view.findViewById(id.solvin.dev.R.id.feed_user_photo);
            userName = (TextView) view.findViewById(id.solvin.dev.R.id.feed_user_name);
            avatarInitial = (TextView) view.findViewById(id.solvin.dev.R.id.feed_avatar_initial);

            questionTime = (TextView) view.findViewById(id.solvin.dev.R.id.feed_question_time);
            questionSummary = (TextView) view.findViewById(id.solvin.dev.R.id.feed_question_summary);
            questionSubject = (TextView) view.findViewById(id.solvin.dev.R.id.feed_question_subject);
            questionMaterial = (TextView) view.findViewById(id.solvin.dev.R.id.feed_question_material);

            mentorPhoto = (CircleImageView) view.findViewById(id.solvin.dev.R.id.feed_mentor_photo);
            mentorName = (TextView) view.findViewById(id.solvin.dev.R.id.feed_mentor_name);
            bestSolutionTime = (TextView) view.findViewById(id.solvin.dev.R.id.feed_best_solution_time);
            bestSolutionAttachment = (ImageView) view.findViewById(id.solvin.dev.R.id.feed_best_solution_attachment);

            questionAttachment = (ImageView) view.findViewById(id.solvin.dev.R.id.feed_question_attachment);
            questionStatus = (ImageView) view.findViewById(id.solvin.dev.R.id.feed_question_status);
            solutionRealtimeStatus = (SpinKitView) view.findViewById(id.solvin.dev.R.id.feed_loading_indicator_view);
        }
    }

    private class ProgressViewHolder extends RecyclerView.ViewHolder {
        private CircularProgressBar progressBar;

        public ProgressViewHolder(View view) {
            super(view);
            progressBar = (CircularProgressBar) view.findViewById(id.solvin.dev.R.id.footer_progress_bar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (feedList.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROGRESS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    id.solvin.dev.R.layout.feed_list, parent, false);
            mainHolder = new ItemViewHolder(itemView);
        } else if (viewType == VIEW_PROGRESS) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    id.solvin.dev.R.layout.footer_loader, parent, false);
            mainHolder = new ProgressViewHolder(itemView);
        }
        return mainHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final Question question = feedList.get(position);
            final int subjectType = question.getMaterial().getSubject().getId();

            if (question.getStudent().getPhoto().isEmpty()) {
                avatarColor = ClassApplicationTool.with(context).getAvatarColor(question.getStudent().getId());
                gradientDrawable = (GradientDrawable) ((ItemViewHolder) holder).avatarLayout.getBackground();
                gradientDrawable.setColor(avatarColor);

                ((ItemViewHolder) holder).avatarInitial.setText(Global.get().getInitialName(question.getStudent().getName()));

                ((ItemViewHolder) holder).avatarLayout.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).userPhoto.setVisibility(View.GONE);
            } else {
                Picasso.with(context).load(Global.get().getAssetURL(question.getStudent().getPhoto(), Auth.AUTH_TYPE_STUDENT))
                        .placeholder(R.drawable.indonesia)
                        .fit()
                        .centerCrop()
                        .into(((ItemViewHolder) holder).userPhoto);

                ((ItemViewHolder) holder).avatarLayout.setVisibility(View.GONE);
                ((ItemViewHolder) holder).userPhoto.setVisibility(View.VISIBLE);
            }

            ((ItemViewHolder) holder).userName.setText(question.getStudent().getName());

            ((ItemViewHolder) holder).questionTime.setText(Global.get()
                    .convertDateToFormat(question.getCreated_at(), "EEE, dd MMM ''yy-HH:mm") + " WIB");

            if (question.getContent().length() > 0) {
                ((ItemViewHolder) holder).questionSummary.setVisibility(View.VISIBLE);

                tempRT.setRichTextEditing(true, question.getContent());
                tempRT.setText(ClassApplicationTool.with(context).loadTextStyle(tempRT.getText()));
                ((ItemViewHolder) holder).questionSummary.setText(tempRT.getText());
            } else {
                ((ItemViewHolder) holder).questionSummary.setVisibility(View.GONE);
            }

            ((ItemViewHolder) holder).questionSubject.setText(question.getMaterial().getSubject().getName());
            if (question.getOther().isEmpty()) {
                ((ItemViewHolder) holder).questionMaterial.setText(question.getMaterial().getName());
            } else {
                ((ItemViewHolder) holder).questionMaterial.setText(question.getOther());
            }
            if (!question.getImage().isEmpty()) {
                ((ItemViewHolder) holder).questionAttachment.setVisibility(View.VISIBLE);
            } else {
                ((ItemViewHolder) holder).questionAttachment.setVisibility(View.GONE);
            }

            /**
             * Show mentor status == 2
             */
            if (question.getStatus() == 2) {
                for (Solution solution : question.getSolutions()) {
                    if (solution.isBest()) {
                        if (!solution.getMentor().getPhoto().isEmpty()) {
                            Picasso.with(context).load(Global.get().getAssetURL(solution.getMentor().getPhoto(), "mentors"))
                                    .centerCrop()
                                    .placeholder(R.drawable.indonesia)
                                    .fit()
                                    .into(((ItemViewHolder) holder).mentorPhoto);
                        }
                        ((ItemViewHolder) holder).mentorName.setText(solution.getMentor().getName());
                        ((ItemViewHolder) holder).bestSolutionTime.setText(Global.get()
                                .convertDateToFormat(solution.getCreated_at(), "EEE, dd MMM ''yy-HH:mm") + " WIB");
                        ((ItemViewHolder) holder).mentorContainer.setVisibility(View.VISIBLE);

                        if (solution.getImage().isEmpty()) {
                            ((ItemViewHolder) holder).bestSolutionAttachment.setVisibility(View.GONE);
                        } else {
                            ((ItemViewHolder) holder).bestSolutionAttachment.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } else {
                ((ItemViewHolder) holder).mentorContainer.setVisibility(View.GONE);
            }

            ((ItemViewHolder) holder).questionStatus.setImageResource(questionStatusDrawable[question.getStatus()]);

            ((ItemViewHolder) holder).questionSubject.setBackgroundResource(flagResourceList[subjectType - 1]);
            ((ItemViewHolder) holder).questionMaterial.setBackgroundResource(flagResourceList[subjectType - 1]);

            ((ItemViewHolder) holder).feedCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    collapseHomeSearchView();

                    intent = new Intent(context, ActivityQuestionDetail.class);
                    intent.putExtra(ConfigApp.get().KEY_DATA_INTENT, question);
                    activity.startActivityForResult(intent, Global.TRIGGGET_UPDATE);
                }
            });
            setAnimation(holder.itemView, position);
            /**
             * Firebase Realtime database
             */
            ((ItemViewHolder) holder).solutionRealtimeStatus.setVisibility(View.GONE);
            if (related) {
                Auth auth = Session.with(itemView.getContext()).getAuth();
                boolean realtimeDatabase = false;
                if (Session.with(itemView.getContext()).getLoginType() == ConfigApp.get().STUDENT) {
                    if (question.getStudent().getId() == auth.getId()) {
                        realtimeDatabase = true;
                    }
                } else {
                    for (Solution solution : question.getSolutions()) {
                        if (solution.getMentor().getId() == auth.getId()) {
                            realtimeDatabase = true;
                            break;
                        }
                    }
                }
                if (realtimeDatabase) {
                    databaseReferencePertanyaan.child(String.valueOf(question.getId())).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                ((ItemViewHolder) holder).solutionRealtimeStatus.setVisibility(View.VISIBLE);
                            } else {
                                ((ItemViewHolder) holder).solutionRealtimeStatus.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            } else {
                ((ItemViewHolder) holder).solutionRealtimeStatus.setVisibility(View.GONE);
            }

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
        return feedList.size();
    }
}