package id.solvinap.dev.view.fragment;

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

import id.solvinap.dev.R;


/**
 * Created by Erick Sumargo on 2/20/2017.
 */

public class FragmentPriority extends Fragment {
    private View view;
    private TabLayout tabLayout;
    private View tabPriorityVote, tabPriorityTransaction, tabPriorityRedeemBalance;
    private TextView tabPriorityVoteTitle, tabPriorityTransactionTitle, tabPriorityRedeemBalanceTitle;

    //    HELPER
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;

    //    OBJECT
    private FragmentPriorityQuestion priorityQuestion;
    private FragmentPriorityTransaction priorityTransaction;
    private FragmentPriorityRedeemBalance priorityRedeemBalance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_priority, container, false);

        init();
        setEvent();

        return view;
    }

    private void init() {
        //    VIEW
        sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        viewPager = (ViewPager) view.findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setCustomView(R.layout.custom_tab_home);
        tabLayout.getTabAt(1).setCustomView(R.layout.custom_tab_home);
        tabLayout.getTabAt(2).setCustomView(R.layout.custom_tab_home);

        tabPriorityVote = tabLayout.getTabAt(0).getCustomView();
        tabPriorityTransaction = tabLayout.getTabAt(1).getCustomView();
        tabPriorityRedeemBalance = tabLayout.getTabAt(2).getCustomView();

        tabPriorityVoteTitle = (TextView) tabPriorityVote.findViewById(R.id.tab_title);
        tabPriorityVoteTitle.setText(sectionsPagerAdapter.getPageTitle(0));

        tabPriorityTransactionTitle = (TextView) tabPriorityTransaction.findViewById(R.id.tab_title);
        tabPriorityTransactionTitle.setText(sectionsPagerAdapter.getPageTitle(1));

        tabPriorityRedeemBalanceTitle = (TextView) tabPriorityRedeemBalance.findViewById(R.id.tab_title);
        tabPriorityRedeemBalanceTitle.setText(sectionsPagerAdapter.getPageTitle(2));

        viewPager.setOffscreenPageLimit(sectionsPagerAdapter.getCount() - 1);
        viewPager.setCurrentItem(0);

        //    OBJECT
        priorityQuestion = new FragmentPriorityQuestion();
        priorityTransaction = new FragmentPriorityTransaction();
        priorityRedeemBalance = new FragmentPriorityRedeemBalance();
    }

    private void setEvent() {
        tabPriorityVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() == 0) {
                    priorityQuestion.priorityQuestionView.scrollToPosition(0);

                    priorityQuestion.resetRecyclerViewData();
                }
                viewPager.setCurrentItem(0);
            }
        });

        tabPriorityTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() == 1) {
                    priorityTransaction.priorityTransactionView.scrollToPosition(0);

                    priorityTransaction.resetRecyclerViewData();
                }
                viewPager.setCurrentItem(1);
            }
        });

        tabPriorityRedeemBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() == 2) {
                    priorityRedeemBalance.priorityRedeemBalanceView.scrollToPosition(0);

                    priorityRedeemBalance.resetRecyclerViewData();
                }
                viewPager.setCurrentItem(2);
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
                    return priorityQuestion;
                case 1:
                    return priorityTransaction;
                case 2:
                    return priorityRedeemBalance;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PERTANYAAN";
                case 1:
                    return "TRANSAKSI";
                case 2:
                    return "TEBUSAN";
            }
            return null;
        }
    }
}