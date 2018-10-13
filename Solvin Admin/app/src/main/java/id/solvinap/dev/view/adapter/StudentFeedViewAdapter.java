package id.solvinap.dev.view.adapter;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataStudent;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.view.activity.ActivityStudentDetail;
import id.solvinap.dev.view.helper.Tool;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by Erick Sumargo on 2/1/2017.
 */

public class StudentFeedViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
    private RecyclerView studentFeedView;
    private View itemView;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;
    private LinearLayoutManager layoutManager;

    private Context context;
    private Animation animation;
    private Intent intent;

    private GradientDrawable gradientDrawable;

    //    OBJECT
    private List<DataStudent> studentList;

    //    VARIABLE
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;
    private int totalItemCount, lastVisibleItem, visibleThreshold = 1, previousTotal = -1;
    public int lastAnimatedPosition = -1;

    private boolean loading = false, connected = true;

    public StudentFeedViewAdapter(RecyclerView studentFeedView, List<DataStudent> studentList) {
        this.studentFeedView = studentFeedView;
        this.studentList = studentList;

        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) studentFeedView.getLayoutManager();
    }

    private void setEvent() {
        studentFeedView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (connected) {
                        studentList.add(null);
                        notifyDataSetChanged();
                    }

                    loading = true;
                    loadMore();
                }
            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void updateList(List<DataStudent> dataStudentList) {
        this.studentList.clear();
        this.studentList.addAll(dataStudentList);

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (studentList.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROGRESS;
        }
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
        private TextView avatarInitial, name, joinDate, credit, totalQuestion, totalComment, totalTransaction;
        private ImageView blocked;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            cardView = (CardView) view.findViewById(R.id.student_list_card);
            avatarLayout = (RelativeLayout) view.findViewById(R.id.student_list_avatar_layout);
            avatarInitial = (TextView) view.findViewById(R.id.student_list_avatar_initial);
            photo = (CircleImageView) view.findViewById(R.id.student_list_photo);
            name = (TextView) view.findViewById(R.id.student_list_name);
            joinDate = (TextView) view.findViewById(R.id.student_list_join_date);
            credit = (TextView) view.findViewById(R.id.student_list_credit);
            totalQuestion = (TextView) view.findViewById(R.id.student_list_total_question);
            totalComment = (TextView) view.findViewById(R.id.student_list_total_comment);
            totalTransaction = (TextView) view.findViewById(R.id.student_list_total_transaction);
            blocked = (ImageView) view.findViewById(R.id.student_list_blocked);
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.student_list, parent, false);
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
            final DataStudent dataStudent = studentList.get(position);

            if (dataStudent.getPhoto().length() > 0) {
                Picasso.with(context).load(Global.ASSETS_URL + "student" + "/" + dataStudent.getPhoto())
                        .placeholder(R.drawable.operator)
                        .fit()
                        .centerCrop()
                        .into(((ItemViewHolder) holder).photo);

                ((ItemViewHolder) holder).avatarLayout.setVisibility(View.GONE);
                ((ItemViewHolder) holder).photo.setVisibility(View.VISIBLE);
            } else {
                gradientDrawable = (GradientDrawable) ((ItemViewHolder) holder).avatarLayout.getBackground();
                gradientDrawable.setColor(Tool.getInstance(context).getAvatarColor(dataStudent.getId()));

                ((ItemViewHolder) holder).avatarInitial.setText(Tool.getInstance(context).getInitialName(dataStudent.getName()));

                ((ItemViewHolder) holder).avatarLayout.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).photo.setVisibility(View.GONE);
            }

            ((ItemViewHolder) holder).name.setText(dataStudent.getName());
            ((ItemViewHolder) holder).joinDate.setText(Tool.getInstance(context).convertToDateTimeFormat(0, dataStudent.getJoinDate()));
            ((ItemViewHolder) holder).blocked.setVisibility(dataStudent.getActive() == 1 ? View.GONE : View.VISIBLE);

            ((ItemViewHolder) holder).credit.setText(dataStudent.isCreditExpired() ? "- SKS" : dataStudent.getCredit() + " SKS");
            ((ItemViewHolder) holder).totalQuestion.setText(String.valueOf(dataStudent.getTotalQuestion()));
            ((ItemViewHolder) holder).totalComment.setText(String.valueOf(dataStudent.getTotalComment()));
            ((ItemViewHolder) holder).totalTransaction.setText(String.valueOf(dataStudent.getTotalTransaction()));

            ((ItemViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent = new Intent(context, ActivityStudentDetail.class);
                    intent.putExtra(Global.PREFERENCES_INTENT_EXTRA, dataStudent);
                    context.startActivity(intent);

                    collapseSearchView();
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
        return studentList.size();
    }
}