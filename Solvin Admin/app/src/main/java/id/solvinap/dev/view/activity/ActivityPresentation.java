package id.solvinap.dev.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataPlainReport;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.view.fragment.FragmentPresentationComment;
import id.solvinap.dev.view.fragment.FragmentPresentationFinancial;
import id.solvinap.dev.view.fragment.FragmentPresentationOther;
import id.solvinap.dev.view.fragment.FragmentPresentationQuestion;
import id.solvinap.dev.view.fragment.FragmentPresentationRedeemBalance;
import id.solvinap.dev.view.fragment.FragmentPresentationSolution;
import id.solvinap.dev.view.fragment.FragmentPresentationTransaction;
import id.solvinap.dev.view.fragment.FragmentPresentationUser;
import id.solvinap.dev.view.helper.DataPresentationYear;
import id.solvinap.dev.view.helper.PresentationBus;
import id.solvinap.dev.view.widget.CustomCheatSheet;
import id.solvinap.dev.view.widget.CustomYearPicker;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 2/24/2017.
 */

public class ActivityPresentation extends AppCompatActivity {
    //    VIEW
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private View tabPresentation;
    private TextView tabPresentationTitle;

    private ImageButton yearIcon;

    //    HELPER
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;

    private TypedValue typedValue;

    //    OBJECT
    public List<DataPlainReport> reportList, selectedReportList, tempReportList;
    private List<List<DataPlainReport>> groupedReportList;
    private List<String> xLabelList;

    private List<DataPresentationYear> yearList;
    private ArrayAdapter<DataPresentationYear> presentationYearAdapter;

    //    VARIABLE
    private int tempYear = 0, selectedYear = -1;
    private String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);

        init();
        setEvent();
    }

    private void init() {
        //    VIEW
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(sectionsPagerAdapter.getCount() - 1);
        viewPager.setCurrentItem(0);
        viewPager.setAdapter(sectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
            tabLayout.getTabAt(i).setCustomView(R.layout.custom_tab_detail);
            tabPresentation = tabLayout.getTabAt(i).getCustomView();

            tabPresentationTitle = (TextView) tabPresentation.findViewById(R.id.tab_title);
            tabPresentationTitle.setText(sectionsPagerAdapter.getPageTitle(i));
        }

        //    OBJECT
        reportList = (List<DataPlainReport>) getIntent().getSerializableExtra(Global.PREFERENCES_INTENT_EXTRA);
        tempReportList = new ArrayList<>();
        groupedReportList = new ArrayList<>();

        tempYear = reportList.get(0).getYear();
        for (DataPlainReport dataReport : reportList) {
            if (dataReport.getYear() == tempYear) {
                tempReportList.add(dataReport);
            } else {
                groupedReportList.add(tempReportList);
                tempReportList = new ArrayList<>();
                tempReportList.add(dataReport);

                tempYear = dataReport.getYear();
            }
        }
        groupedReportList.add(tempReportList);
        Collections.reverse(groupedReportList);

        yearList = new ArrayList<>();
        for (List<DataPlainReport> reportList : groupedReportList) {
            yearList.add(new DataPresentationYear(String.valueOf(reportList.get(0).getYear())));
        }
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void postSelectedYearReport(int i){
        selectedReportList = groupedReportList.get(i);
        xLabelList = new ArrayList<>();
        for (DataPlainReport dataReport : selectedReportList) {
            xLabelList.add(months[dataReport.getMonth() - 1]);
        }
        EventBus.getDefault().post(new PresentationBus(selectedReportList, xLabelList));
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentPresentationUser();
                case 1:
                    return new FragmentPresentationFinancial();
                case 2:
                    return new FragmentPresentationQuestion();
                case 3:
                    return new FragmentPresentationSolution();
                case 4:
                    return new FragmentPresentationComment();
                case 5:
                    return new FragmentPresentationTransaction();
                case 6:
                    return new FragmentPresentationRedeemBalance();
                case 7:
                    return new FragmentPresentationOther();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Pengguna";
                case 1:
                    return "Finansial";
                case 2:
                    return "Pertanyaan";
                case 3:
                    return "Solusi";
                case 4:
                    return "Komentar";
                case 5:
                    return "Transaksi";
                case 6:
                    return "Tebus Saldo";
                case 7:
                    return "Lainnya";
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.presentation, menu);

        postSelectedYearReport(0);

        typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarItemBackground, typedValue, true);

        yearIcon = (ImageButton) menu.findItem(R.id.action_select_year).getActionView();
        yearIcon.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.action_button_default_width), ViewGroup.LayoutParams.MATCH_PARENT));
        yearIcon.setImageResource(R.drawable.ic_calendar_range_light);
        yearIcon.setBackgroundResource(typedValue.resourceId);
        yearIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedYear == -1) {
                    selectedYear = Integer.parseInt(yearList.get(0).getYear());
                }
                final CustomYearPicker customYearPicker = new CustomYearPicker(ActivityPresentation.this, yearList, selectedYear);
                customYearPicker.setOnNegativeClickListener(new CustomYearPicker.OnNegativeClickListener() {
                    @Override
                    public void onClick() {
                        customYearPicker.dismiss();
                    }
                });
                customYearPicker.setOnPositiveClickListener(new CustomYearPicker.OnPositiveClickListener() {
                    @Override
                    public void onClick(int i) {
                        customYearPicker.dismiss();

                        selectedYear = Integer.parseInt(yearList.get(i).getYear());
                        postSelectedYearReport(i);

                    }
                });
            }
        });
        CustomCheatSheet.setup(yearIcon, getString(R.string.action_select_year));

        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}