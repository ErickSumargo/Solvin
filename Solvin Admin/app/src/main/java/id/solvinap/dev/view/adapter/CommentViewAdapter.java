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
import android.widget.LinearLayout;
import android.widget.TextView;

import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataComment;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.view.activity.ActivityCommentEditingSheet;
import id.solvinap.dev.view.helper.Tool;
import id.solvinap.dev.view.widget.CustomAdminPasswordConfirmation;
import id.solvinap.dev.view.widget.CustomAlertDialog;
import id.solvinap.dev.view.widget.CustomProgressDialog;
import id.solvinap.dev.view.widget.CustomRichEditText;

import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by Erick Sumargo on 2/2/2017.
 */

public class CommentViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //    INTERFACE
    public interface OnLoadMoreListener {
        void OnLoadMore();
    }

    public interface OnBlockCommentListener {
        void OnBlock(String message, int id);
    }

    private static OnLoadMoreListener mLoadMoreListener;
    private static OnBlockCommentListener mBlockCommentListener;

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public void setOnBlockCommentListener(OnBlockCommentListener listener) {
        mBlockCommentListener = listener;
    }

    private void loadMore() {
        if (mLoadMoreListener != null) {
            mLoadMoreListener.OnLoadMore();
        }
    }

    private void blockComment(String message, int id) {
        if (mBlockCommentListener != null) {
            mBlockCommentListener.OnBlock(message, id);
        }
    }

    //    VIEW
    private RecyclerView commentView;
    private View itemView;

    private CustomRichEditText tempRT;
    public CustomProgressDialog customProgressDialog;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;
    private LinearLayoutManager layoutManager;

    private Context context;
    private Animation animation;
    private Intent intent;

    //    OBJECT
    private List<DataComment> commentList;

    //    VARIABLE
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;
    private int totalItemCount, lastVisibleItem, visibleThreshold = 1;
    public int lastAnimatedPosition = -1;
    private int subjectType;
    private int[] flagResourceList = {R.drawable.hashtag_mathematics, R.drawable.hashtag_physics};

    public boolean loading = false;

    public CommentViewAdapter(RecyclerView commentView, List<DataComment> commentList, CustomRichEditText tempRT) {
        this.commentView = commentView;
        this.commentList = commentList;
        this.tempRT = tempRT;

        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) commentView.getLayoutManager();
    }

    private void setEvent() {
        commentView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadMore();
                    loading = true;

                    commentList.add(null);
                    notifyDataSetChanged();
                }

            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    public void updateList(List<DataComment> commentList) {
        this.commentList.clear();
        this.commentList.addAll(commentList);

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
        private ImageView attachment;
        private TextView time, date, summary, subject, material;
        private LinearLayout actionContainer, block, edit;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            cardView = (CardView) view.findViewById(R.id.comment_card);
            time = (TextView) view.findViewById(R.id.comment_time);
            date = (TextView) view.findViewById(R.id.comment_date);
            attachment = (ImageView) view.findViewById(R.id.comment_attachment);
            summary = (TextView) view.findViewById(R.id.comment_summary);
            subject = (TextView) view.findViewById(R.id.comment_subject);
            material = (TextView) view.findViewById(R.id.comment_material);

            actionContainer = (LinearLayout) view.findViewById(R.id.comment_action_container);
            block = (LinearLayout) view.findViewById(R.id.comment_block);
            edit = (LinearLayout) view.findViewById(R.id.comment_edit);
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
        if (commentList.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROGRESS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.comment_list, parent, false);
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
            final DataComment dataComment = commentList.get(position);
            subjectType = dataComment.getDataQuestion().getDataMaterial().getDataSubject().getId();

            ((ItemViewHolder) holder).time.setText(Tool.getInstance(context).convertToTimeFormat(dataComment.getCreatedAt()));
            ((ItemViewHolder) holder).date.setText(Tool.getInstance(context).convertToDateFormat(0, dataComment.getCreatedAt()));

            if (dataComment.getImage().length() > 0) {
                ((ItemViewHolder) holder).attachment.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).attachment.setImageResource(R.drawable.ic_attach_file_dark);
            } else {
                ((ItemViewHolder) holder).attachment.setVisibility(View.GONE);
            }

            if (dataComment.getContent().length() > 0) {
                tempRT.setRichTextEditing(true, dataComment.getContent());
                tempRT.setText(Tool.getInstance(context).loadTextStyle(tempRT.getText()));

                ((ItemViewHolder) holder).summary.setText(tempRT.getText());
                ((ItemViewHolder) holder).summary.setVisibility(View.VISIBLE);
            } else {
                ((ItemViewHolder) holder).summary.setVisibility(View.GONE);
            }

            ((ItemViewHolder) holder).subject.setBackgroundResource(flagResourceList[subjectType - 1]);
            ((ItemViewHolder) holder).material.setBackgroundResource(flagResourceList[subjectType - 1]);

            ((ItemViewHolder) holder).subject.setText(dataComment.getDataQuestion().getDataMaterial().getDataSubject().getName());
            ((ItemViewHolder) holder).material.setText(dataComment.getDataQuestion().getDataMaterial().getName());

            ((ItemViewHolder) holder).actionContainer.setVisibility(dataComment.getActive() == 1 ? View.VISIBLE : View.GONE);

            ((ItemViewHolder) holder).block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CustomAdminPasswordConfirmation adminPasswordConfirmation = new CustomAdminPasswordConfirmation(context, Global.ACTION_PASSWORD);
                    adminPasswordConfirmation.setOnConfirmedListener(new CustomAdminPasswordConfirmation.OnConfirmedListener() {
                        @Override
                        public void OnConfirmed() {
                            showConfirmationDialog(0, "Anda akan memblokir komentar terkait?",
                                    "Memblokir komentar...", dataComment);
                        }
                    });
                }
            });

            ((ItemViewHolder) holder).edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CustomAdminPasswordConfirmation adminPasswordConfirmation = new CustomAdminPasswordConfirmation(context, Global.ACTION_PASSWORD);
                    adminPasswordConfirmation.setOnConfirmedListener(new CustomAdminPasswordConfirmation.OnConfirmedListener() {
                        @Override
                        public void OnConfirmed() {
                            showConfirmationDialog(1, "Anda akan mengedit konten dari komentar terkait?", "", dataComment);
                        }
                    });
                }
            });

            setAnimation(((ItemViewHolder) holder).itemView, position);
        } else if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    private void showConfirmationDialog(final int mode, final String alertDialogMessage,
                                        final String progressDialogMessage, final DataComment dataComment) {
        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setTitle("Konfirmasi");
        customAlertDialog.setMessage(alertDialogMessage);

        customAlertDialog.setPositiveButton("Ya", new CustomAlertDialog.OnPositiveClickListener() {
            @Override
            public void onClick() {
                if (mode == 0) {
                    customAlertDialog.dismiss();

                    blockComment(progressDialogMessage, dataComment.getId());
                } else {
                    customAlertDialog.dismiss();

                    intent = new Intent(context, ActivityCommentEditingSheet.class);
                    intent.putExtra(Global.PREFERENCES_INTENT_EXTRA, dataComment);
                    context.startActivity(intent);
                }
            }
        });

        customAlertDialog.setNegativeButton("Tidak", new CustomAlertDialog.OnNegativeClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();
            }
        });
    }

    public void showCustomProgressDialog(String message) {
        customProgressDialog = new CustomProgressDialog(context);
        customProgressDialog.setMessage(message);
    }

    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).itemView.clearAnimation();
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}