package id.solvinap.dev.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataAuth;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.view.helper.Tool;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Erick Sumargo on 2/2/2017.
 */

public class SearchSuggestionViewAdapter extends android.support.v4.widget.CursorAdapter {
    //    VIEW
    private CircleImageView photo;
    private RelativeLayout avatarLayout;
    private TextView avatarInitial, name, record;

    //    HELPER
    private GradientDrawable gradientDrawable;

    //    OBJECT
    private List<DataAuth> searchSuggestionList;

    //    VARIABLE
    private String authType;

    public SearchSuggestionViewAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
    }

    public void updateSearchSuggestionList(List<DataAuth> searchSuggestionList) {
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
        photo = (CircleImageView) view.findViewById(R.id.search_suggestion_photo);
        avatarLayout = (RelativeLayout) view.findViewById(R.id.search_suggestion_avatar_layout);
        avatarInitial = (TextView) view.findViewById(R.id.search_suggestion_avatar_initial);
        name = (TextView) view.findViewById(R.id.search_suggestion_name);
        record = (TextView) view.findViewById(R.id.search_suggestion_record);
    }

    private void setData(Cursor cursor, Context context) {
        if (searchSuggestionList.size() == cursor.getCount()) {
            final DataAuth dataAuth = searchSuggestionList.get(cursor.getPosition());
            authType = dataAuth.getAuthType();

            name.setText(dataAuth.getName());

            if (authType.equals("student")) {
                if (dataAuth.getPhoto().length() > 0) {
                    Picasso.with(context).load(Global.ASSETS_URL + "student" + "/" + dataAuth.getPhoto())
                            .placeholder(R.drawable.operator)
                            .fit()
                            .centerCrop()
                            .into(photo);

                    avatarLayout.setVisibility(View.GONE);
                    photo.setVisibility(View.VISIBLE);
                } else {
                    gradientDrawable = (GradientDrawable) avatarLayout.getBackground();
                    gradientDrawable.setColor(Tool.getInstance(context).getAvatarColor(dataAuth.getId()));

                    avatarInitial.setText(Tool.getInstance(context).getInitialName(dataAuth.getName()));

                    avatarLayout.setVisibility(View.VISIBLE);
                    photo.setVisibility(View.GONE);
                }

                record.setText(dataAuth.getAuthTotalQuestion() == 0 ? "Belum ada pertanyaan" : dataAuth.getAuthTotalQuestion() + " Pertanyaan");
                record.setTextColor(context.getResources().getColor(R.color.colorSubHeaderDarker));
            } else {
                Picasso.with(context).load(Global.ASSETS_URL + "mentor" + "/" + dataAuth.getPhoto())
                        .placeholder(R.drawable.operator)
                        .fit()
                        .centerCrop()
                        .into(photo);

                avatarLayout.setVisibility(View.GONE);
                photo.setVisibility(View.VISIBLE);

                record.setText(dataAuth.getAuthTotalSolution() == 0 ? "Belum memiliki solusi" : dataAuth.getAuthTotalBestSolution() + "/" + dataAuth.getAuthTotalSolution() + " Solusi");
                record.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
        }
    }
}