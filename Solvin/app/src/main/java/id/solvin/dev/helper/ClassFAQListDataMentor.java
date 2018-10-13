package id.solvin.dev.helper;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;

import java.util.ArrayList;

/**
 * Created by Erick Sumargo on 10/9/2016.
 */
public class ClassFAQListDataMentor {
    private Context context;
    private static ArrayList<ClassFAQGroup> groupList;
    private static ArrayList<ClassFAQChild> childList;

    private static ClassFAQGroup group;
    private static ClassFAQChild child;

    private static Spanned[] title;
    private static Spanned[] content;

    public ClassFAQListDataMentor(Context context) {
        this.context = context;

        title = new Spanned[]{Html.fromHtml(context.getResources().getString(id.solvin.dev.R.string.faq_mentor_title1)),
                Html.fromHtml(context.getResources().getString(id.solvin.dev.R.string.faq_mentor_title2)),
                Html.fromHtml(context.getResources().getString(id.solvin.dev.R.string.faq_mentor_title3)),
                Html.fromHtml(context.getResources().getString(id.solvin.dev.R.string.faq_mentor_title4)),
                Html.fromHtml(context.getResources().getString(id.solvin.dev.R.string.faq_mentor_title5)),
                Html.fromHtml(context.getResources().getString(id.solvin.dev.R.string.faq_mentor_title6)),
                Html.fromHtml(context.getResources().getString(id.solvin.dev.R.string.faq_mentor_title7))};

        content = new Spanned[]{Html.fromHtml(context.getResources().getString(id.solvin.dev.R.string.faq_mentor_content1)),
                Html.fromHtml(context.getResources().getString(id.solvin.dev.R.string.faq_mentor_content2)),
                Html.fromHtml(context.getResources().getString(id.solvin.dev.R.string.faq_mentor_content3)),
                Html.fromHtml(context.getResources().getString(id.solvin.dev.R.string.faq_mentor_content4)),
                Html.fromHtml(context.getResources().getString(id.solvin.dev.R.string.faq_mentor_content5)),
                Html.fromHtml(context.getResources().getString(id.solvin.dev.R.string.faq_mentor_content6)),
                Html.fromHtml(context.getResources().getString(id.solvin.dev.R.string.faq_mentor_content7))};
    }

    public static ArrayList<ClassFAQGroup> getData() {
        groupList = new ArrayList<ClassFAQGroup>();
        for (int i = 0; i < title.length; i++) {
            childList = new ArrayList<ClassFAQChild>();
            group = new ClassFAQGroup();
            child = new ClassFAQChild();

            group.setName(title[i]);
            child.setName(content[i]);
            childList.add(child);
            group.setItems(childList);
            groupList.add(group);
        }
        return groupList;
    }
}