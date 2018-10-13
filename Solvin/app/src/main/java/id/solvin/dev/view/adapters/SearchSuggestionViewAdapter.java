package id.solvin.dev.view.adapters;


import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Global;
import id.solvin.dev.model.basic.Auth;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Erick Sumargo on 1/8/2017.
 */

public class SearchSuggestionViewAdapter extends android.support.v4.widget.CursorAdapter {
    private CircleImageView userPhoto;
    private RelativeLayout avatarLayout;
    private TextView avatarInitial, userName, userStatus;

    private List<Auth> searchSuggestionList;
    private GradientDrawable gradientDrawable;

    public SearchSuggestionViewAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
    }

    public void updateSearchSuggestionList(List<Auth> searchSuggestionList) {
        this.searchSuggestionList = searchSuggestionList;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.search_suggestion_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        init(view);
        setData(cursor, context);
    }

    private void init(View view) {
        userPhoto = (CircleImageView) view.findViewById(R.id.search_suggestion_user_photo);
        avatarLayout = (RelativeLayout) view.findViewById(R.id.search_suggestion_avatar_layout);
        avatarInitial = (TextView) view.findViewById(R.id.search_suggestion_avatar_initial);
        userName = (TextView) view.findViewById(R.id.search_suggestion_user_name);
        userStatus = (TextView) view.findViewById(R.id.search_suggestion_user_status);
    }

    private void setData(Cursor cursor, Context context) {
        if (searchSuggestionList.size() == cursor.getCount()) {
            Auth dataSearchResult = searchSuggestionList.get(cursor.getPosition());

            userName.setText(dataSearchResult.getName());
            if (dataSearchResult.getAuth_type().equals("student")) {
                if (!dataSearchResult.getPhoto().isEmpty()) {
                    userPhoto.setVisibility(View.VISIBLE);
                    avatarLayout.setVisibility(View.GONE);

                    Picasso.with(context).load(Global.get().getAssetURL(dataSearchResult.getPhoto(), Auth.AUTH_TYPE_STUDENT))
                            .placeholder(R.drawable.operator)
                            .fit()
                            .centerCrop()
                            .into(userPhoto);
                } else {
                    userPhoto.setVisibility(View.GONE);
                    avatarLayout.setVisibility(View.VISIBLE);

                    gradientDrawable = (GradientDrawable) avatarLayout.getBackground();
                    gradientDrawable.setColor(ClassApplicationTool.with(context).getAvatarColor(dataSearchResult.getId()));
                    avatarInitial.setText(Global.get().getInitialName(dataSearchResult.getName()));
                }
                userStatus.setTextColor(context.getResources().getColor(R.color.colorSubHeaderDarker));
                if (dataSearchResult.getQuestion_count() == 0) {
                    userStatus.setText("Belum ada pertanyaan");
                } else {
                    userStatus.setText(dataSearchResult.getQuestion_count() + " Pertanyaan");
                }
            } else {
                userPhoto.setVisibility(View.VISIBLE);
                avatarLayout.setVisibility(View.GONE);

                Picasso.with(context).load(Global.get().getAssetURL(dataSearchResult.getPhoto(), Auth.AUTH_TYPE_MENTOR))
                        .placeholder(R.drawable.operator)
                        .fit()
                        .centerCrop()
                        .into(userPhoto);

                userStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                if (dataSearchResult.getSolution_count() == 0) {
                    userStatus.setText("Belum memiliki solusi");
                } else {
                    userStatus.setText(dataSearchResult.getBest_count() + "/" + dataSearchResult.getSolution_count() + " Solusi");
                }
            }
        }
    }
}