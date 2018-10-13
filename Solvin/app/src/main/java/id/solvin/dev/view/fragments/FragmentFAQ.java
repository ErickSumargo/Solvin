package id.solvin.dev.view.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import id.solvin.dev.R;

/**
 * Created by Erick Sumargo on 9/1/2016.
 */
public class FragmentFAQ extends Fragment {
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    private View view, tabFAQUser, tabFAQMentor;
    private TabLayout tabLayout;
    private TextView tabFAQUserTitle, tabFAQMentorTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_faq, container, false);
        init();
        setEvent();

        return view;
    }

    private void init() {
        sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        viewPager = (ViewPager) view.findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setCustomView(R.layout.custom_tab_faq);
        tabLayout.getTabAt(1).setCustomView(R.layout.custom_tab_faq);

        tabFAQUser = tabLayout.getTabAt(0).getCustomView();
        tabFAQMentor = tabLayout.getTabAt(1).getCustomView();

        tabFAQUserTitle = (TextView) tabFAQUser.findViewById(R.id.tab_title);
        tabFAQUserTitle.setText(sectionsPagerAdapter.getPageTitle(0));
        tabFAQUserTitle.setTextColor(getResources().getColor(R.color.colorPrimary));

        tabFAQMentorTitle = (TextView) tabFAQMentor.findViewById(R.id.tab_title);
        tabFAQMentorTitle.setText(sectionsPagerAdapter.getPageTitle(1));
    }

    private void setEvent() {
        tabFAQUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabLayout.getTabAt(0).select();
                tabFAQUserTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
                tabFAQMentorTitle.setTextColor(getResources().getColor(R.color.colorSubHeader));
            }
        });

        tabFAQMentor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabLayout.getTabAt(1).select();
                tabFAQUserTitle.setTextColor(getResources().getColor(R.color.colorSubHeader));
                tabFAQMentorTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentFAQUser();
                case 1:
                    return new FragmentFAQMentor();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "MURID";
                case 1:
                    return "MENTOR";
            }
            return null;
        }
    }
}