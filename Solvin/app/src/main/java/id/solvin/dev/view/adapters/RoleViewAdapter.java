package id.solvin.dev.view.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rey.material.widget.CheckBox;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Auth;

/**
 * Created by Erick Sumargo on 1/3/2017.
 */

public class RoleViewAdapter extends RecyclerView.Adapter<RoleViewAdapter.ViewHolder> {
    private static OnSetCheckedList mSetCheckedList;

    public interface OnSetCheckedList {
        void IGetCheckedList(List<Auth> checkedList);
    }

    public void getCheckedList(OnSetCheckedList listener) {
        mSetCheckedList = listener;
    }

    public void setCheckedList(List<Auth> checkedList) {
        if (mSetCheckedList != null) {
            mSetCheckedList.IGetCheckedList(checkedList);
        }
    }

    private Context context;
    private Auth auth;
    private ClassApplicationTool applicationTool;
    private GradientDrawable gradientDrawable;
    private List<Auth> roleList;
    private List<Auth> checkedList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout group, avatarLayout;
        private CheckBox checkBoxGroup;
        private CircleImageView subjectPhoto;
        private TextView avatarInitial, subjectName, subjectStatus;

        private int avatarColor;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();

            group = (RelativeLayout) view.findViewById(R.id.role_group);
            avatarLayout = (RelativeLayout) view.findViewById(R.id.role_avatar_layout);
            checkBoxGroup = (CheckBox) view.findViewById(R.id.role_checkbox_group);
            subjectPhoto = (CircleImageView) view.findViewById(R.id.role_subject_photo);
            avatarInitial = (TextView) view.findViewById(R.id.role_avatar_initial);
            subjectName = (TextView) view.findViewById(R.id.role_subject_name);
            subjectStatus = (TextView) view.findViewById(R.id.role_subject_status);

            applicationTool = new ClassApplicationTool(view.getContext());
        }
    }

    public RoleViewAdapter(List<Auth> roleList, Context context) {
        this.roleList = roleList;
        checkedList = new ArrayList<>();
        for (int i = 0; i < roleList.size(); i++) {
            checkedList.add(null);
        }
        auth = Session.with(context).getAuth();
    }

    @Override
    public RoleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.role_list, parent, false);

        return new RoleViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RoleViewAdapter.ViewHolder holder, final int position) {
        final Auth dataRole = roleList.get(position);
        if (position == 0) {
            if (dataRole.getId() == auth.getId() && auth.getAuth_type().equals(Auth.AUTH_TYPE_STUDENT)) {
                holder.group.setVisibility(View.GONE);
            } else {
                holder.group.setVisibility(View.VISIBLE);
            }

            dataRole.setAuth_type(Auth.AUTH_TYPE_STUDENT);
            if (dataRole.getPhoto().isEmpty()) {
                holder.avatarColor = applicationTool.getAvatarColor(dataRole.getId());
                gradientDrawable = (GradientDrawable) holder.avatarLayout.getBackground();
                gradientDrawable.setColor(holder.avatarColor);

                holder.avatarInitial.setText(Global.get().getInitialName(dataRole.getName()));

                holder.avatarLayout.setVisibility(View.VISIBLE);
                holder.subjectPhoto.setVisibility(View.GONE);
            } else {
                Picasso.with(context).load(Global.get().getAssetURL(dataRole.getPhoto(), Auth.AUTH_TYPE_STUDENT))
                        .placeholder(R.drawable.operator)
                        .fit()
                        .centerCrop()
                        .into(holder.subjectPhoto);

                holder.avatarLayout.setVisibility(View.GONE);
                holder.subjectPhoto.setVisibility(View.VISIBLE);
            }
            holder.subjectName.setText(dataRole.getName());
            holder.subjectStatus.setText("Pengaju Pertanyaan");
        } else {
            if (dataRole.getId() == auth.getId() && auth.getAuth_type().equals(Auth.AUTH_TYPE_MENTOR)) {
                holder.group.setVisibility(View.GONE);
            } else {
                holder.group.setVisibility(View.VISIBLE);
            }

            dataRole.setAuth_type(Auth.AUTH_TYPE_MENTOR);
            Picasso.with(context).load(Global.get().getAssetURL(dataRole.getPhoto(), Auth.AUTH_TYPE_MENTOR))
                    .placeholder(R.drawable.operator)
                    .fit()
                    .centerCrop()
                    .into(holder.subjectPhoto);

            holder.subjectName.setText(dataRole.getName());
            holder.subjectStatus.setText("Pengaju Solusi-" + String.valueOf(holder.getAdapterPosition()));

            holder.subjectPhoto.setVisibility(View.VISIBLE);
            holder.avatarLayout.setVisibility(View.GONE);
        }

        holder.checkBoxGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    holder.group.setBackgroundResource(R.drawable.custom_background_borderless_selected);
                    holder.checkBoxGroup.setChecked(true);
                    checkedList.set(holder.getAdapterPosition(), dataRole);
                } else {
                    holder.group.setBackgroundResource(R.drawable.custom_background_borderless);
                    holder.checkBoxGroup.setChecked(false);
                    checkedList.set(holder.getAdapterPosition(), null);
                }
                setCheckedList(checkedList);
            }
        });

        holder.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.checkBoxGroup.isChecked()) {
                    holder.group.setBackgroundResource(R.drawable.custom_background_borderless_selected);
                    holder.checkBoxGroup.setChecked(true);
                    checkedList.set(holder.getAdapterPosition(), dataRole);
                } else {
                    holder.group.setBackgroundResource(R.drawable.custom_background_borderless);
                    holder.checkBoxGroup.setChecked(false);
                    checkedList.set(holder.getAdapterPosition(), null);
                }
                setCheckedList(checkedList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return roleList.size();
    }
}