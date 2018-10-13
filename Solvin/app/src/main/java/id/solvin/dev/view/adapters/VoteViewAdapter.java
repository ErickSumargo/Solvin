package id.solvin.dev.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import id.solvin.dev.R;
import id.solvin.dev.helper.Global;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.Solution;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Erick Sumargo on 11/6/2016.
 */

public class VoteViewAdapter extends RecyclerView.Adapter<VoteViewAdapter.ViewHolder> {
    private static OnSetVotedPosition mSetVotedPosition;

    public interface OnSetVotedPosition {
        void IGetVotedPosition(int position);
    }

    public void getVotedPosition(OnSetVotedPosition listener) {
        mSetVotedPosition = listener;
    }

    public void setVotedPosition(int position) {
        if (mSetVotedPosition != null) {
            mSetVotedPosition.IGetVotedPosition(position);
        }
    }

    private Context context;
    private List<Solution> voteList;
    private RelativeLayout clickedGroup, lastClickedGroup = null;
    private RadioGroup lastCheckedRadioGroup;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout group;
        private RadioGroup radioGroup;
        private RadioButton radio;
        private CircleImageView mentorPhoto;
        private TextView mentorName, mentorRecord;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();

            group = (RelativeLayout) view.findViewById(R.id.vote_group);
            radioGroup = (RadioGroup) view.findViewById(R.id.vote_radio_group);
            radio = (RadioButton) view.findViewById(R.id.vote_radio);

            mentorPhoto = (CircleImageView) view.findViewById(R.id.vote_mentor_photo);
            mentorName = (TextView) view.findViewById(R.id.vote_mentor_name);
            mentorRecord = (TextView) view.findViewById(R.id.vote_mentor_record);
        }
    }

    public VoteViewAdapter(List<Solution> voteList) {
        this.voteList = voteList;
    }

    @Override
    public VoteViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vote_list, parent, false);

        return new VoteViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VoteViewAdapter.ViewHolder holder, int position) {
        Solution dataVote = voteList.get(position);

        Picasso.with(context).load(Global.get().getAssetURL(dataVote.getMentor().getPhoto(), Auth.AUTH_TYPE_MENTOR))
                .placeholder(R.drawable.operator)
                .fit()
                .centerCrop()
                .into(holder.mentorPhoto);

        holder.mentorName.setText(dataVote.getMentor().getName());
        holder.mentorRecord.setText(dataVote.getMentor().getBestCount() + "/" + dataVote.getMentor().getSolutionCount() + " Solusi");//dataVote.getMentorRecord());

        holder.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedGroup = (RelativeLayout) v;
                holder.group.setBackgroundResource(R.drawable.custom_vote_background_selected);
                holder.radio.setChecked(true);
                if (lastClickedGroup != null && lastClickedGroup != holder.group) {
                    lastClickedGroup.setBackgroundResource(R.drawable.custom_vote_background_pressed);
                    lastCheckedRadioGroup.clearCheck();
                }
                lastClickedGroup = clickedGroup;
                lastCheckedRadioGroup = holder.radioGroup;

                setVotedPosition(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return voteList.size();
    }
}