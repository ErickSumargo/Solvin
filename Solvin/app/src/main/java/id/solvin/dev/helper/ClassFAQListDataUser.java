package id.solvin.dev.helper;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;

import id.solvin.dev.R;

import java.util.ArrayList;

/**
 * Created by Erick Sumargo on 9/6/2016.
 */
public class ClassFAQListDataUser {
    private Context context;
    private static ArrayList<ClassFAQGroup> groupList;
    private static ArrayList<ClassFAQChild> childList;

    private static ClassFAQGroup group;
    private static ClassFAQChild child;

    private static Spanned[] title;
    private static Spanned[] content;

    public ClassFAQListDataUser(Context context) {
        this.context = context;

        title = new Spanned[]{Html.fromHtml(context.getResources().getString(R.string.faq_student_title1)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_title2)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_title3)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_title4)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_title5)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_title6)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_title7)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_title8)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_title9)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_title10)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_title11)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_title12)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_title13)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_title14)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_title15))};

        content = new Spanned[]{Html.fromHtml(context.getResources().getString(R.string.faq_student_content1)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_content2)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_content3)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_content4)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_content5)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_content6)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_content7)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_content8)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_content9)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_content10)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_content11)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_content12)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_content13)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_content14)),
                Html.fromHtml(context.getResources().getString(R.string.faq_student_content15))};
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