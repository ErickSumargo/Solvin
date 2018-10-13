package id.solvinap.dev.view.adapter;

import android.content.Context;
import android.content.Intent;
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
import id.solvinap.dev.server.data.DataSolution;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.view.activity.ActivitySolutionEditingSheet;
import id.solvinap.dev.view.helper.Tool;
import id.solvinap.dev.view.widget.CustomAdminPasswordConfirmation;
import id.solvinap.dev.view.widget.CustomAlertDialog;
import id.solvinap.dev.view.widget.CustomProgressDialog;
import id.solvinap.dev.view.widget.CustomRichEditText;

import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by Erick Sumargo on 2/3/2017.
 */

public class SolutionViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //    INTERFACE
    public interface OnLoadMoreListener {
        void OnLoadMore();
    }

    public interface OnBlockSolutionListener {
        void OnBlock(String message, int id);
    }

    private static OnLoadMoreListener mLoadMoreListener;
    private static OnBlockSolutionListener mBlockSolutionListener;

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public void setOnBlockSolutionListener(OnBlockSolutionListener listener) {
        mBlockSolutionListener = listener;
    }

    private void loadMore() {
        if (mLoadMoreListener != null) {
            mLoadMoreListener.OnLoadMore();
        }
    }

    private void blockSolution(String message, int id) {
        if (mBlockSolutionListener != null) {
            mBlockSolutionListener.OnBlock(message, id);
        }
    }

    //    VIEW
    private RecyclerView solutionView;
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
    private List<DataSolution> solutionList;

    //    VARIABLE
    private final static int VIEW_ITEM = 0, VIEW_PROGRESS = 1;
    private int totalItemCount, lastVisibleItem, visibleThreshold = 1;
    public int lastAnimatedPosition = -1;
    private int subjectType;
    private int[] flagResourceList = {R.drawable.hashtag_mathematics, R.drawable.hashtag_physics};

    private boolean loading = false;

    public SolutionViewAdapter(RecyclerView solutionView, List<DataSolution> solutionList, CustomRichEditText tempRT) {
        this.solutionView = solutionView;
        this.solutionList = solutionList;
        this.tempRT = tempRT;

        init();
        setEvent();
    }

    private void init() {
        layoutManager = (LinearLayoutManager) solutionView.getLayoutManager();
    }

    private void setEvent() {
        solutionView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadMore();
                    loading = true;

                    solutionList.add(null);
                    notifyDataSetChanged();
                }

            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    public void updateList(List<DataSolution> solutionList) {
        this.solutionList.clear();
        this.solutionList.addAll(solutionList);

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
        private View flagBackground;
        private ImageView attachment;
        private TextView bestVotedFlag, time, date, summary, subject, material;
        private LinearLayout actionContainer, block, edit;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            flagBackground = view.findViewById(R.id.solution_flag_background);
            time = (TextView) view.findViewById(R.id.solution_time);
            date = (TextView) view.findViewById(R.id.solution_date);
            attachment = (ImageView) view.findViewById(R.id.solution_attachment);
            bestVotedFlag = (TextView) view.findViewById(R.id.solution_best_voted_flag);
            summary = (TextView) view.findViewById(R.id.solution_summary);
            subject = (TextView) view.findViewById(R.id.solution_subject);
            material = (TextView) view.findViewById(R.id.solution_material);

            actionContainer = (LinearLayout) view.findViewById(R.id.solution_action_container);
            block = (LinearLayout) view.findViewById(R.id.solution_block);
            edit = (LinearLayout) view.findViewById(R.id.solution_edit);
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
        if (solutionList.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROGRESS;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.solution_list, parent, false);
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
            final DataSolution dataSolution = solutionList.get(position);
            subjectType = dataSolution.getDataQuestion().getDataMaterial().getDataSubject().getId();

            if (subjectType == 1) {
                ((ItemViewHolder) holder).flagBackground.setBackgroundColor(context.getResources().getColor(R.color.colorMathematics));
            } else {
                ((ItemViewHolder) holder).flagBackground.setBackgroundColor(context.getResources().getColor(R.color.colorPhysics));
            }

            ((ItemViewHolder) holder).time.setText(Tool.getInstance(context).convertToTimeFormat(dataSolution.getCreatedAt()));
            ((ItemViewHolder) holder).date.setText(Tool.getInstance(context).convertToDateFormat(0, dataSolution.getCreatedAt()));

            if (dataSolution.getImage().length() > 0) {
                ((ItemViewHolder) holder).attachment.setVisibility(View.VISIBLE);
            } else {
                ((ItemViewHolder) holder).attachment.setVisibility(View.GONE);
            }

            if (dataSolution.getBest() == 0) {
                ((ItemViewHolder) holder).bestVotedFlag.setVisibility(View.GONE);
            } else {
                ((ItemViewHolder) holder).bestVotedFlag.setVisibility(View.VISIBLE);
            }

            if (dataSolution.getContent().length() > 0) {
                tempRT.setRichTextEditing(true, dataSolution.getContent());
                tempRT.setText(Tool.getInstance(context).loadTextStyle(tempRT.getText()));

                ((ItemViewHolder) holder).summary.setText(tempRT.getText());
                ((ItemViewHolder) holder).summary.setVisibility(View.VISIBLE);
            } else {
                ((ItemViewHolder) holder).summary.setVisibility(View.GONE);
            }

            ((ItemViewHolder) holder).subject.setBackgroundResource(flagResourceList[subjectType - 1]);
            ((ItemViewHolder) holder).material.setBackgroundResource(flagResourceList[subjectType - 1]);

            ((ItemViewHolder) holder).subject.setText(dataSolution.getDataQuestion().getDataMaterial().getDataSubject().getName());
            ((ItemViewHolder) holder).material.setText(dataSolution.getDataQuestion().getDataMaterial().getName());

            ((ItemViewHolder) holder).actionContainer.setVisibility(dataSolution.getActive() == 1 ? View.VISIBLE : View.GONE);

            ((ItemViewHolder) holder).block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CustomAdminPasswordConfirmation adminPasswordConfirmation = new CustomAdminPasswordConfirmation(context, Global.ACTION_PASSWORD);
                    adminPasswordConfirmation.setOnConfirmedListener(new CustomAdminPasswordConfirmation.OnConfirmedListener() {
                        @Override
                        public void OnConfirmed() {
                            showConfirmationDialog(0, "Anda akan memblokir solusi terkait?", "Memblokir solusi...", dataSolution);
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
                            showConfirmationDialog(1, "Anda akan mengedit konten dari solusi terkait?", "", dataSolution);
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
                                        final String progressDialogMessage, final DataSolution dataSolution) {
        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setTitle("Konfirmasi");
        customAlertDialog.setMessage(alertDialogMessage);
        customAlertDialog.setPositiveButton("Ya", new CustomAlertDialog.OnPositiveClickListener() {
            @Override
            public void onClick() {
                if (mode == 0) {
                    customAlertDialog.dismiss();

                    blockSolution(progressDialogMessage, dataSolution.getId());
                } else if (mode == 1) {
                    customAlertDialog.dismiss();

                    intent = new Intent(context, ActivitySolutionEditingSheet.class);
                    intent.putExtra(Global.PREFERENCES_INTENT_EXTRA, dataSolution);
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
        return solutionList.size();
    }
}