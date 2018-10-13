package id.solvin.dev.view.adapters;

import android.app.Activity;
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

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.ConfigApp;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.Question;
import id.solvin.dev.model.basic.Solution;
import id.solvin.dev.view.activities.ActivityQuestionDetail;
import id.solvin.dev.view.widget.ClassRichEditText;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

import static android.media.CamcorderProfile.get;

/**
 * Created by Erick Sumargo on 9/2/2016.
 */
public class HistoryQuestionViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
    private RecyclerView historyQuestionView;
    private View itemView;

    private ClassRichEditText tempRT;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;
    private LinearLayoutManager layoutManager;

    private Activity activity;

    private Context context;
    private Animation animation;
    private Intent intent;

    //    OBJECT
    private List<Question> historyList;

    //    VARIABLE
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;
    private int totalItemCount, lastVisibleItem, visibleThreshold = 1;
    public int lastAnimatedPosition = -1;
    private int[] questionStatusDrawable = {R.drawable.ic_pending, R.drawable.ic_discussion, R.drawable.ic_complete};
    private int[] flagResourceList = {R.drawable.hashtag_mathematics, R.drawable.hashtag_physics, R.drawable.hashtag_chemistry};

    private String contentSummary, authType;
    private boolean loading = false;
    /**
     * Firebase Realtime Database
     */
    private boolean related = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceQuestion;

    public HistoryQuestionViewAdapter(RecyclerView historyQuestionView, List<Question> historyList,
                                      ClassRichEditText tempRT, String authType, Activity activity) {
        this.historyQuestionView = historyQuestionView;
        this.historyList = historyList;
        this.tempRT = tempRT;
        this.authType = authType;
        this.activity = activity;

        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) historyQuestionView.getLayoutManager();
    }

    private void setEvent() {
        historyQuestionView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadMore();
                    loading = true;

                    historyList.add(null);
                    notifyDataSetChanged();
                }

            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    public void updateList(List<Question> feedList) {
        this.historyList.clear();
        this.historyList.addAll(feedList);

        notifyDataSetChanged();
    }

    public void updateList(List<Question> feedList, boolean related) {
        this.historyList.clear();
        this.historyList.addAll(feedList);
        this.related = related;
        if (related) {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReferenceQuestion = firebaseDatabase.getReference("question");
        }

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
        private View flag;
        private CardView card;
        private TextView questionDate, questionTime, questionSummary,
                questionSubject, questionMaterial, bestVotedFlag;
        private ImageView questionAttachment, questionStatus;
        private SpinKitView solutionRealtimeStatus;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            flag = view.findViewById(R.id.history_question_flag);
            card = (CardView) view.findViewById(R.id.history_question_card);
            questionDate = (TextView) view.findViewById(R.id.history_question_date);
            questionTime = (TextView) view.findViewById(R.id.history_question_time);
            questionSummary = (TextView) view.findViewById(R.id.history_question_summary);
            questionSubject = (TextView) view.findViewById(R.id.history_question_subject);
            questionMaterial = (TextView) view.findViewById(R.id.history_question_material);
            questionAttachment = (ImageView) view.findViewById(R.id.history_question_attachment);
            questionStatus = (ImageView) view.findViewById(R.id.history_question_status);
            bestVotedFlag = (TextView) view.findViewById(R.id.history_question_best_voted_flag);
            solutionRealtimeStatus = (SpinKitView) view.findViewById(R.id.history_question_loading_indicator_view);
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
        if (historyList.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROGRESS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.history_question_list, parent, false);
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
            final Question question = historyList.get(position);
            final int subjectType = question.getMaterial().getSubject().getId();

            ((ItemViewHolder) holder).questionDate.setText(Global.get().convertDateToFormat(question.getCreated_at(), "EEEE, dd MMMM yyyy"));
            ((ItemViewHolder) holder).questionTime.setText(Global.get().convertDateToFormat(question.getCreated_at(), "HH.mm") + " WIB");

            if (question.getContent().length() > 0) {
                ((ItemViewHolder) holder).questionSummary.setVisibility(View.VISIBLE);

                if (authType.equals("student")) {
                    contentSummary = question.getContent();
                } else {
                    for (int i = 0; i < question.getSolutions().size(); i++) {
                        if (question.getSolutions().get(i).getMentor_id() == Session.with(holder.itemView.getContext()).getAuth().getId()) {
                            contentSummary = question.getSolutions().get(i).getContent();
                        } else {
                            contentSummary = "";
                        }
                    }
                }
                tempRT.setRichTextEditing(true, contentSummary);
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

            ((ItemViewHolder) holder).questionStatus.setImageResource(questionStatusDrawable[question.getStatus()]);

            ((ItemViewHolder) holder).flag.setBackgroundResource(flagResourceList[subjectType - 1]);
            ((ItemViewHolder) holder).questionSubject.setBackgroundResource(flagResourceList[subjectType - 1]);
            ((ItemViewHolder) holder).questionMaterial.setBackgroundResource(flagResourceList[subjectType - 1]);

            Session session = Session.with(holder.itemView.getContext());
            if (session.getLoginType() == ConfigApp.get().MENTOR) {
                for (Solution s : question.getSolutions()) {
                    ((ItemViewHolder) holder).questionAttachment.setVisibility(s.getImage().isEmpty() ? View.GONE : View.VISIBLE);
                    if (s.getMentor().getId() == session.getAuth().getId()) {
                        if (s.isBest()) {
                            ((ItemViewHolder) holder).bestVotedFlag.setVisibility(View.VISIBLE);
                        } else {
                            ((ItemViewHolder) holder).bestVotedFlag.setVisibility(View.GONE);
                        }
                        break;
                    }
                }
            } else {
                ((ItemViewHolder) holder).questionAttachment.setVisibility(question.getImage().isEmpty() ? View.GONE : View.VISIBLE);
                ((ItemViewHolder) holder).bestVotedFlag.setVisibility(View.GONE);
            }

            ((ItemViewHolder) holder).card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(holder.itemView.getContext(), ActivityQuestionDetail.class);
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
                    databaseReferenceQuestion.child(String.valueOf(question.getId())).addValueEventListener(new ValueEventListener() {
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
        return historyList.size();
    }
}