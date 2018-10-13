package id.solvin.dev.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassFAQChild;
import id.solvin.dev.helper.ClassFAQGroup;
import id.solvin.dev.view.widget.ClassAnimatedExpandableListView;

import java.util.ArrayList;

/**
 * Created by Erick Sumargo on 9/1/2016.
 */
public class FAQViewAdapter extends ClassAnimatedExpandableListView.AnimatedExpandableListAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<ClassFAQGroup> groupList;
    private ClassFAQGroup group;
    private ClassFAQChild child;

    private TextView title, content;

    public FAQViewAdapter(Context context, ArrayList<ClassFAQGroup> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupList.get(groupPosition).getItems().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        child = (ClassFAQChild) getChild(groupPosition, childPosition);
        if(convertView == null) {
            layoutInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.faq_item, null);
        }

        content = (TextView) convertView.findViewById(R.id.faq_content);
        content.setText(child.getName());

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return groupList.get(groupPosition).getItems().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        group = (ClassFAQGroup) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.faq_group, null);
        }
        title = (TextView) convertView.findViewById(R.id.faq_title);
        title.setText(group.getName());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}