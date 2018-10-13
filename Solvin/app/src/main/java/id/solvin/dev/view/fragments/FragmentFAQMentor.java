package id.solvin.dev.view.fragments;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import id.solvin.dev.helper.ClassFAQGroup;
import id.solvin.dev.helper.ClassFAQListDataMentor;
import id.solvin.dev.view.widget.ClassAnimatedExpandableListView;
import id.solvin.dev.view.adapters.FAQViewAdapter;

import java.util.ArrayList;

/**
 * Created by Erick Sumargo on 10/9/2016.
 */
public class FragmentFAQMentor extends Fragment {
    private View view;
    private ClassAnimatedExpandableListView FAQView;
    private ClassFAQListDataMentor classFAQListDataMentor;
    private ExpandableListAdapter FAQListAdapter;
    private ArrayList<ClassFAQGroup> FAQListDetail;

    private AudioManager audioManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(id.solvin.dev.R.layout.activity_faq_mentor, container, false);
        init();

        return view;
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= 21) {
            view.findViewById(id.solvin.dev.R.id.shadow_view).setVisibility(View.GONE);
        } else {
            view.findViewById(id.solvin.dev.R.id.shadow_view).setVisibility(View.VISIBLE);
        }

        FAQView = (ClassAnimatedExpandableListView) view.findViewById(id.solvin.dev.R.id.faq_mentor_view);
        classFAQListDataMentor = new ClassFAQListDataMentor(getContext());
        FAQListDetail = classFAQListDataMentor.getData();
        FAQListAdapter = new FAQViewAdapter(getActivity(), FAQListDetail);
        FAQView.setAdapter(FAQListAdapter);

        FAQView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                playClickSound();
                if (FAQView.isGroupExpanded(groupPosition)) {
                    FAQView.collapseGroupWithAnimation(groupPosition);
                } else {
                    FAQView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });
    }

    private void playClickSound() {
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, 1.0f);
    }
}
