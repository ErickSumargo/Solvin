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
import id.solvinap.dev.server.data.DataQuestion;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.view.activity.ActivityVoteSolution;
import id.solvinap.dev.view.helper.Tool;
import id.solvinap.dev.view.widget.CustomProgressDialog;
import id.solvinap.dev.view.widget.CustomRichEditText;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by Erick Sumargo on 2/20/2017.
 */

public class PriorityQuestionViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
    private RecyclerView priorityQuestionView;
    private View itemView;

    private CustomRichEditText tempRT;
    public CustomProgressDialog customProgressDialog;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;
    private LinearLayoutManager layoutManager;

    private Context context;
    private Animation animation;
    private Intent intent;

    private GradientDrawable gradientDrawable;

    //    OBJECT
    private List<DataQuestion> questionList;

    //    VARIABLE
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;
    private int totalItemCount, lastVisibleItem, visibleThreshold = 1;
    public int lastAnimatedPosition = -1;
    private int subjectType;
    private int[] statusResourceList = {R.drawable.ic_pending, R.drawable.ic_discussion, R.drawable.ic_complete};
    private int[] flagResourceList = {R.drawable.hashtag_mathematics, R.drawable.hashtag_physics};

    private boolean loading = false;

    public PriorityQuestionViewAdapter(RecyclerView priorityQuestionView, List<DataQuestion> questionList, CustomRichEditText tempRT) {
        this.priorityQuestionView = priorityQuestionView;
        this.questionList = questionList;
        this.tempRT = tempRT;

        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) priorityQuestionView.getLayoutManager();
    }

    private void setEvent() {
        priorityQuestionView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadMore();
                    loading = true;

                    questionList.add(null);
                    notifyDataSetChanged();
                }

            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    public void updateList(List<DataQuestion> questionList) {
        this.questionList.clear();
        this.questionList.addAll(questionList);

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
        private RelativeLayout avatarLayout;
        private CircleImageView photo;
        private ImageView attachment, status;
        private TextView avatarInitial, name, time, summary, subject, material;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            cardView = (CardView) view.findViewById(R.id.priority_question_list_card);
            avatarLayout = (RelativeLayout) view.findViewById(R.id.priority_question_list_avatar_layout);
            avatarInitial = (TextView) view.findViewById(R.id.priority_question_list_avatar_initial);
            photo = (CircleImageView) view.findViewById(R.id.priority_question_list_photo);
            name = (TextView) view.findViewById(R.id.priority_question_list_name);
            time = (TextView) view.findViewById(R.id.priority_question_list_time);
            attachment = (ImageView) view.findViewById(R.id.priority_question_list_attachment);
            status = (ImageView) view.findViewById(R.id.priority_question_list_status);
            summary = (TextView) view.findViewById(R.id.priority_question_list_summary);
            subject = (TextView) view.findViewById(R.id.priority_question_list_subject);
            material = (TextView) view.findViewById(R.id.priority_question_list_material);
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
        if (questionList.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROGRESS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.priority_question_list, parent, false);
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
            final DataQuestion dataQuestion = questionList.get(position);
            subjectType = dataQuestion.getDataMaterial().getDataSubject().getId();

            if (dataQuestion.getDataStudent().getPhoto().length() > 0) {
                Picasso.with(context).load(Global.ASSETS_URL + "student" + "/" + dataQuestion.getDataStudent().getPhoto())
                        .placeholder(R.drawable.operator)
                        .fit()
                        .centerCrop()
                        .into(((ItemViewHolder) holder).photo);

                ((ItemViewHolder) holder).avatarLayout.setVisibility(View.GONE);
                ((ItemViewHolder) holder).photo.setVisibility(View.VISIBLE);
            } else {
                gradientDrawable = (GradientDrawable) ((ItemViewHolder) holder).avatarLayout.getBackground();
                gradientDrawable.setColor(Tool.getInstance(context).getAvatarColor(dataQuestion.getDataStudent().getId()));

                ((ItemViewHolder) holder).avatarInitial.setText(Tool.getInstance(context).getInitialName(dataQuestion.getDataStudent().getName()));

                ((ItemViewHolder) holder).avatarLayout.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).photo.setVisibility(View.GONE);
            }

            ((ItemViewHolder) holder).name.setText(dataQuestion.getDataStudent().getName());
            ((ItemViewHolder) holder).time.setText(Tool.getInstance(context).convertToDateTimeFormat(0, dataQuestion.getCreatedAt()));

            if (dataQuestion.getImage().length() > 0) {
                ((ItemViewHolder) holder).attachment.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).attachment.setImageResource(R.drawable.ic_attach_file_dark);
            } else {
                ((ItemViewHolder) holder).attachment.setVisibility(View.GONE);
            }

            ((ItemViewHolder) holder).status.setImageResource(statusResourceList[dataQuestion.getStatus()]);

            if (dataQuestion.getContent().length() > 0) {
                tempRT.setRichTextEditing(true, dataQuestion.getContent());
                tempRT.setText(Tool.getInstance(context).loadTextStyle(tempRT.getText()));

                ((ItemViewHolder) holder).summary.setText(tempRT.getText());
                ((ItemViewHolder) holder).summary.setVisibility(View.VISIBLE);
            } else {
                ((ItemViewHolder) holder).summary.setVisibility(View.GONE);
            }

            ((ItemViewHolder) holder).subject.setBackgroundResource(flagResourceList[subjectType - 1]);
            ((ItemViewHolder) holder).material.setBackgroundResource(flagResourceList[subjectType - 1]);

            ((ItemViewHolder) holder).subject.setText(dataQuestion.getDataMaterial().getDataSubject().getName());
            ((ItemViewHolder) holder).material.setText(dataQuestion.getDataMaterial().getName());

            ((ItemViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent = new Intent(context, ActivityVoteSolution.class);
                    intent.putExtra(Global.PREFERENCES_INTENT_EXTRA, dataQuestion);
                    context.startActivity(intent);
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
        return questionList.size();
    }
}