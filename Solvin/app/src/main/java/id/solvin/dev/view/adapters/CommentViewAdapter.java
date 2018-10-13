package id.solvin.dev.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.ConfigApp;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.Comment;
import id.solvin.dev.model.basic.Question;
import id.solvin.dev.view.activities.ActivityCommentSheet;
import id.solvin.dev.view.activities.ActivityFullScreen;
import id.solvin.dev.view.widget.ClassRichEditText;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Erick Sumargo on 9/3/2016.
 */
public class CommentViewAdapter extends RecyclerView.Adapter<CommentViewAdapter.ViewHolder> {
    private ClassRichEditText tempRT;

    private Context context;
    private List<Comment> commentList;
    private Intent intent;
    private GradientDrawable gradientDrawable;
    private Question question;
    private Activity activity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View divider;
        private RelativeLayout avatarLayout, commentImageContainer, imageForeground;
        private CircleImageView userPhoto;
        private ImageButton commentEdit;
        private ImageView attachment, commentImage;
        private TextView avatarInitial, userName, commentTime, commentContent;

        private int userPhotoMode, avatarColor;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();

            divider = view.findViewById(id.solvin.dev.R.id.comment_divider);
            avatarLayout = (RelativeLayout) view.findViewById(id.solvin.dev.R.id.comment_avatar_layout);
            userPhoto = (CircleImageView) view.findViewById(id.solvin.dev.R.id.comment_user_photo);
            userName = (TextView) view.findViewById(id.solvin.dev.R.id.comment_user_name);
            avatarInitial = (TextView) view.findViewById(id.solvin.dev.R.id.comment_avatar_initial);
            commentTime = (TextView) view.findViewById(id.solvin.dev.R.id.comment_time);
            attachment = (ImageView) view.findViewById(R.id.comment_attachment);
            commentEdit = (ImageButton) view.findViewById(id.solvin.dev.R.id.comment_edit);
            commentContent = (TextView) view.findViewById(id.solvin.dev.R.id.comment_content);
            commentImage = (ImageView) view.findViewById(id.solvin.dev.R.id.comment_image);
            commentImageContainer = (RelativeLayout) view.findViewById(id.solvin.dev.R.id.comment_image_container);
            imageForeground = (RelativeLayout) view.findViewById(id.solvin.dev.R.id.comment_image_foreground);
        }
    }

    public CommentViewAdapter(Question question, ClassRichEditText tempRT, Activity activity) {
        this.question = question;
        this.commentList = this.question.getComments();
        this.tempRT = tempRT;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(id.solvin.dev.R.layout.comment_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Comment dataComment = commentList.get(position);
        ClassApplicationTool applicationTool = ClassApplicationTool.with(context);
        if (!dataComment.getAuth().getPhoto().isEmpty()) {
            Picasso.with(holder.itemView.getContext())
                    .load(Global.get().getAssetURL(dataComment.getAuth().getPhoto(), dataComment.getAuth_type()))
                    .placeholder(id.solvin.dev.R.drawable.operator)
                    .fit()
                    .centerCrop()
                    .into(holder.userPhoto);

            holder.avatarLayout.setVisibility(View.GONE);
            holder.userPhoto.setVisibility(View.VISIBLE);
        } else {
            holder.avatarInitial.setText(Global.get().getInitialName(dataComment.getAuth().getName()));
            holder.avatarColor = applicationTool.getAvatarColor(dataComment.getAuth().getId());
            gradientDrawable = (GradientDrawable) holder.avatarLayout.getBackground();
            gradientDrawable.setColor(holder.avatarColor);

            holder.avatarLayout.setVisibility(View.VISIBLE);
            holder.userPhoto.setVisibility(View.GONE);
        }

        holder.userName.setText(dataComment.getAuth().getName());
        holder.commentTime.setText(Global.get().convertDateToFormat(dataComment.getCreated_at(), "EEE, dd MMM ''yy-HH:mm") + " WIB");

        if (dataComment.getContent().isEmpty()) {
            holder.commentContent.setVisibility(View.GONE);
        } else {
            tempRT.setRichTextEditing(true, dataComment.getContent());
            tempRT.setText(ClassApplicationTool.with(context).loadTextStyle(tempRT.getText()));
            holder.commentContent.setText(tempRT.getText());

            holder.commentContent.setVisibility(View.VISIBLE);
        }

        holder.commentImageContainer.setVisibility(TextUtils.isEmpty(dataComment.getImage()) ? View.GONE : View.VISIBLE);

        if (!dataComment.getImage().isEmpty()) {
            holder.imageForeground.setVisibility(View.VISIBLE);

            Picasso.with(holder.itemView.getContext()).load(Global.get().getAssetURL(dataComment.getImage(), "comment"))
                    .placeholder(id.solvin.dev.R.drawable.image_placeholder)
                    .fit()
                    .centerCrop()
                    .into(holder.commentImage);
        } else {
            holder.imageForeground.setVisibility(View.GONE);
        }

        holder.attachment.setVisibility(!dataComment.getImage().isEmpty() ? View.VISIBLE : View.GONE);
        holder.commentEdit.setVisibility(isAuth(dataComment.getAuth_id(), dataComment.getAuth_type(), holder.itemView.getContext()) ?
                View.VISIBLE : View.GONE);

        if (position < commentList.size() - 1) {
            holder.divider.setVisibility(View.VISIBLE);
        } else {
            holder.divider.setVisibility(View.GONE);
        }

        holder.imageForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(context, ActivityFullScreen.class);
                intent.putExtra("image", dataComment.getImage());
                intent.putExtra("category", "comment");
                context.startActivity(intent);
            }
        });

        holder.commentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, ActivityCommentSheet.class);
                intent.putExtra("COMMENT_MODE", 1);     /*Editing Activity*/
                intent.putExtra("COMMENT_CONTENT", holder.commentContent.getText());
                intent.putExtra(Global.get().key().QUESTION_DATA_POSITION, position);
                intent.putExtra(Global.get().key().QUESTION_DATA, question);
                activity.startActivityForResult(intent, Global.TRIGGGET_UPDATE);
            }
        });
    }

    private boolean isAuth(int id, String auth_type, Context context) {
        String session_auth_type = Session.with(context).getLoginType() == ConfigApp.get().STUDENT ? Auth.AUTH_TYPE_STUDENT : Auth.AUTH_TYPE_MENTOR;
        if (session_auth_type.equals(auth_type)) {
            return id == Session.with(context).getAuth().getId();
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}