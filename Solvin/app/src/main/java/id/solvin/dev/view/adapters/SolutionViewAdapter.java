package id.solvin.dev.view.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Solution;
import id.solvin.dev.view.activities.ActivityFullScreen;
import id.solvin.dev.view.widget.ClassRichEditText;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Erick Sumargo on 11/3/2016.
 */

public class SolutionViewAdapter extends RecyclerView.Adapter<SolutionViewAdapter.ViewHolder> {
    private ClassRichEditText tempRT;
    private ClipboardManager clipboardManager;
    private ClipData clipData;

    private List<Solution> solutionList;
    private Context context;
    private Intent intent;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout solutionImageContainer, imageForeground;
        private View divider;
        private CircleImageView mentorPhoto;
        private ImageButton copy;
        private TextView mentorName, solutionTime, solutionContent, bestVotedFlag;
        private ImageView attachment, solutionImage;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();

            divider = view.findViewById(R.id.solution_divider);
            mentorPhoto = (CircleImageView) view.findViewById(R.id.solution_mentor_photo);
            mentorName = (TextView) view.findViewById(R.id.solution_mentor_name);
            attachment = (ImageView) view.findViewById(R.id.solution_attachment);
            copy = (ImageButton) view.findViewById(R.id.solution_copy);
            solutionTime = (TextView) view.findViewById(R.id.solution_time);
            solutionContent = (TextView) view.findViewById(R.id.solution_content);
            solutionImage = (ImageView) view.findViewById(R.id.solution_image);
            bestVotedFlag = (TextView) view.findViewById(R.id.solution_best_voted_flag);
            solutionImageContainer = (RelativeLayout) view.findViewById(R.id.solution_image_container);
            imageForeground = (RelativeLayout) view.findViewById(R.id.solution_image_foreground);

            clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        }
    }

    public SolutionViewAdapter(List<Solution> solutionList, ClassRichEditText tempRT) {
        this.solutionList = solutionList;
        this.tempRT = tempRT;
    }

    @Override
    public SolutionViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.solution_list, parent, false);

        return new SolutionViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SolutionViewAdapter.ViewHolder holder, int position) {
        final Solution dataSolution = solutionList.get(position);
        if (!dataSolution.getMentor().getPhoto().isEmpty()) {
            Picasso.with(holder.itemView.getContext()).load(Global.get().getAssetURL(dataSolution.getMentor().getPhoto(), "mentor"))
                    .placeholder(R.drawable.operator)
                    .fit()
                    .centerCrop()
                    .into(holder.mentorPhoto);
        }

        holder.mentorName.setText(dataSolution.getMentor().getName());
        holder.attachment.setVisibility(!dataSolution.getImage().isEmpty() ? View.VISIBLE : View.GONE);
        holder.copy.setVisibility(isAuth(dataSolution.getMentor_id(), context) ? View.VISIBLE : View.GONE);

        holder.solutionTime.setText(Global.get()
                .convertDateToFormat(dataSolution.getCreated_at(), "EEE, dd MMM ''yy-HH:mm") + " WIB");
        if (dataSolution.getContent().isEmpty()) {
            holder.solutionContent.setVisibility(View.GONE);
        } else {
            tempRT.setRichTextEditing(true, dataSolution.getContent());
            tempRT.setText(ClassApplicationTool.with(context).loadTextStyle(tempRT.getText()));
            holder.solutionContent.setText(tempRT.getText());

            holder.solutionContent.setVisibility(View.VISIBLE);
        }
        holder.solutionImageContainer.setVisibility(TextUtils.isEmpty(dataSolution.getImage()) ? View.GONE : View.VISIBLE);

        if (!dataSolution.getImage().isEmpty()) {
            holder.solutionImageContainer.setVisibility(View.VISIBLE);

            Picasso.with(holder.itemView.getContext()).load(Global.get().getAssetURL(dataSolution.getImage(), "solution"))
                    .placeholder(R.drawable.image_placeholder)
                    .fit()
                    .centerCrop()
                    .into(holder.solutionImage);
        } else {
            holder.solutionImageContainer.setVisibility(View.GONE);
        }
        if (dataSolution.isBest()) {
            holder.bestVotedFlag.setVisibility(View.VISIBLE);
        } else {
            holder.bestVotedFlag.setVisibility(View.GONE);
        }

        if (position < solutionList.size() - 1) {
            holder.divider.setVisibility(View.VISIBLE);
        } else {
            holder.divider.setVisibility(View.GONE);
        }

        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clipData = ClipData.newPlainText("SOLUTION", dataSolution.getContent());
                clipboardManager.setPrimaryClip(clipData);

                final Toast toast = Toast.makeText(context, "Solusi Tersalin", Toast.LENGTH_SHORT);
                ClassApplicationTool.with(context).resizeToast(toast);
                toast.show();
            }
        });

        holder.imageForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(context, ActivityFullScreen.class);
                intent.putExtra("image", dataSolution.getImage());
                intent.putExtra("category", "solution");
                context.startActivity(intent);
            }
        });
    }

    private boolean isAuth(int id, Context context) {
        return ((id == Session.with(context).getAuth().getId()) && Session.with(context).getAuth().getAuth_type().equals("mentor"));
    }

    @Override
    public int getItemCount() {
        return solutionList.size();
    }
}