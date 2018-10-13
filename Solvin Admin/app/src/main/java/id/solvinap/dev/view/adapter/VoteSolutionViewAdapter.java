package id.solvinap.dev.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataSolution;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.view.activity.ActivityImageFullScreen;
import id.solvinap.dev.view.helper.Tool;
import id.solvinap.dev.view.widget.CustomAdminPasswordConfirmation;
import id.solvinap.dev.view.widget.CustomAlertDialog;
import id.solvinap.dev.view.widget.CustomProgressDialog;
import id.solvinap.dev.view.widget.CustomRichEditText;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Erick Sumargo on 2/20/2017.
 */

public class VoteSolutionViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //    INTERFACE
    public interface OnVoteSolutionListener {
        void OnVoteSolution(String message, int questionId, int solutionId, int mentorId);
    }

    private static OnVoteSolutionListener mVoteSolutionListener;

    public void setOnVoteSolutionListener(OnVoteSolutionListener listener) {
        mVoteSolutionListener = listener;
    }

    private void voteSolution(String message, int questionId, int solutionId, int mentorId) {
        if (mVoteSolutionListener != null) {
            mVoteSolutionListener.OnVoteSolution(message, questionId, solutionId, mentorId);
        }
    }

    //    VIEW
    private View itemView;

    private CustomRichEditText tempRT;
    public CustomProgressDialog customProgressDialog;

    //    HELPER
    private RecyclerView.ViewHolder mainHolder;

    private Context context;
    private Intent intent;

    //    OBJECT
    private List<DataSolution> solutionList;

    //    VARIABLE
    private final static int VIEW_ITEM = 0;
    private int questionId;

    public VoteSolutionViewAdapter(List<DataSolution> solutionList, CustomRichEditText tempRT) {
        this.solutionList = solutionList;
        this.tempRT = tempRT;
    }

    public void addList(List<DataSolution> solutionList, int questionId) {
        this.solutionList.clear();
        this.solutionList.addAll(solutionList);

        this.questionId = questionId;

        notifyDataSetChanged();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView photo;
        private RelativeLayout imageContainer, imageForeground;
        private ImageView attachment, image;
        private ImageButton vote;
        private TextView name, time, content;
        private View divider;

        public ItemViewHolder(View view) {
            super(view);
            context = view.getContext();

            photo = (CircleImageView) view.findViewById(R.id.vote_list_mentor_photo);
            name = (TextView) view.findViewById(R.id.vote_list_mentor_name);
            time = (TextView) view.findViewById(R.id.vote_list_solution_time);
            attachment = (ImageView) view.findViewById(R.id.vote_list_attachment);
            vote = (ImageButton) view.findViewById(R.id.vote_list_best);
            content = (TextView) view.findViewById(R.id.vote_list_solution_content);
            imageContainer = (RelativeLayout) view.findViewById(R.id.vote_list_image_container);
            imageForeground = (RelativeLayout) view.findViewById(R.id.vote_list_image_foreground);
            image = (ImageView) view.findViewById(R.id.vote_list_image);
            divider = view.findViewById(R.id.vote_list_divider);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.vote_solution_list, parent, false);
        mainHolder = new ItemViewHolder(itemView);

        return mainHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final DataSolution dataSolution = solutionList.get(position);

        Picasso.with(context).load(Global.ASSETS_URL + "mentor" + "/" + dataSolution.getDataMentor().getPhoto())
                .placeholder(R.drawable.operator)
                .fit()
                .centerCrop()
                .into(((ItemViewHolder) holder).photo);

        ((ItemViewHolder) holder).name.setText(dataSolution.getDataMentor().getName());
        ((ItemViewHolder) holder).time.setText(Tool.getInstance(context).convertToDateTimeFormat(0, dataSolution.getCreatedAt()));

        if (dataSolution.getImage().length() > 0) {
            ((ItemViewHolder) holder).attachment.setVisibility(View.VISIBLE);
        } else {
            ((ItemViewHolder) holder).attachment.setVisibility(View.GONE);
        }

        if (dataSolution.getContent().length() > 0) {
            tempRT.setRichTextEditing(true, dataSolution.getContent());
            tempRT.setText(Tool.getInstance(context).loadTextStyle(tempRT.getText()));

            ((ItemViewHolder) holder).content.setText(tempRT.getText());
            ((ItemViewHolder) holder).content.setVisibility(View.VISIBLE);
        } else {
            ((ItemViewHolder) holder).content.setVisibility(View.GONE);
        }

        if (dataSolution.getImage().length() > 0) {
            ((ItemViewHolder) holder).imageContainer.setVisibility(View.VISIBLE);

            Picasso.with(context).load(Global.ASSETS_URL + "solution" + "/" + dataSolution.getImage())
                    .placeholder(R.drawable.image_placeholder)
                    .fit()
                    .centerCrop()
                    .into(((ItemViewHolder) holder).image);
        } else {
            ((ItemViewHolder) holder).imageContainer.setVisibility(View.GONE);
        }

        if (holder.getAdapterPosition() == solutionList.size() - 1) {
            ((ItemViewHolder) holder).divider.setVisibility(View.GONE);
        }

        ((ItemViewHolder) holder).vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomAdminPasswordConfirmation adminPasswordConfirmation = new CustomAdminPasswordConfirmation(context, Global.ACTION_PASSWORD);
                adminPasswordConfirmation.setOnConfirmedListener(new CustomAdminPasswordConfirmation.OnConfirmedListener() {
                    @Override
                    public void OnConfirmed() {
                        showConfirmationDialog("Anda akan mem-vote solusi terkait sebagai yang terbaik?", "Vote solusi terbaik...", dataSolution);
                    }
                });
            }
        });

        ((ItemViewHolder) holder).imageForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(context, ActivityImageFullScreen.class);
                intent.putExtra("image", dataSolution.getImage());
                intent.putExtra("category", "solution");

                context.startActivity(intent);
            }
        });
    }

    private void showConfirmationDialog(final String alertDialogMessage, final String progressDialogMessage,
                                        final DataSolution dataSolution) {
        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setTitle("Konfirmasi");
        customAlertDialog.setMessage(alertDialogMessage);

        customAlertDialog.setPositiveButton("Ya", new CustomAlertDialog.OnPositiveClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();

                voteSolution(progressDialogMessage, questionId, dataSolution.getId(), dataSolution.getMentorId());
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
    public int getItemCount() {
        return solutionList.size();
    }
}