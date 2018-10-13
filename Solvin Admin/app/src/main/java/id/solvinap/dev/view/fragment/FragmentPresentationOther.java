package id.solvinap.dev.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataPlainReport;
import id.solvinap.dev.view.adapter.BarChartViewAdapter;
import id.solvinap.dev.view.helper.PresentationBus;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;

/**
 * Created by Erick Sumargo on 3/3/2017.
 */

public class FragmentPresentationOther extends Fragment {
    //    VIEW
    private View view;
    private ListView otherChartView;

    //    OBJECT
    private List<DataPlainReport> reportList;
    private List<String> xLabelList;

    private List<BarEntry> feedbackEntryList, freeCreditEntryList, balanceBonusEntryList;
    private BarDataSet dataFeedback, dataFreeCredit, dataBalanceBonus;

    private BarData otherBarData;
    private List<BarData> otherBarDataList;

    private BarChartViewAdapter barChartViewAdapter;

    //    VARIABLE
    private float barWidth = 0.45f;
    private boolean chartRefresh = true, isVisibleToUser = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_presentation_other, container, false);

        init();

        return view;
    }

    private void init() {
        //    VIEW
        otherChartView = (ListView) view.findViewById(R.id.other_chart_view);
    }

    private void drawChart() {
        otherBarDataList = new ArrayList<>();
        otherBarDataList.add(getOtherBarData(0));
        otherBarDataList.add(getOtherBarData(1));
        otherBarDataList.add(getOtherBarData(2));

        barChartViewAdapter = new BarChartViewAdapter(getContext(), otherBarDataList, xLabelList, 0, true);
        otherChartView.setAdapter(barChartViewAdapter);
    }

    private BarData getOtherBarData(int type) {
        feedbackEntryList = new ArrayList<>();
        freeCreditEntryList = new ArrayList<>();
        balanceBonusEntryList = new ArrayList<>();

        int i = 0;
        for (DataPlainReport dataReport : reportList) {
            if (type == 0) {
                feedbackEntryList.add(new BarEntry(i, dataReport.getFeedback()));
            } else if (type == 1) {
                freeCreditEntryList.add(new BarEntry(i, dataReport.getFreeCredit()));
            } else if (type == 2) {
                balanceBonusEntryList.add(new BarEntry(i, dataReport.getBalanceBonus()));
            }
            i++;
        }
        if (type == 0) {
            dataFeedback = new BarDataSet(feedbackEntryList, "Feedback");
            dataFeedback.setColors(ColorTemplate.MATERIAL_COLORS);

            otherBarData = new BarData(dataFeedback);
        } else if (type == 1) {
            dataFreeCredit = new BarDataSet(freeCreditEntryList, "Bonus SKT (Kode Membership)");
            dataFreeCredit.setColors(ColorTemplate.MATERIAL_COLORS);

            otherBarData = new BarData(dataFreeCredit);
        } else if (type == 2) {
            dataBalanceBonus = new BarDataSet(balanceBonusEntryList, "Bonus Saldo");
            dataBalanceBonus.setColors(ColorTemplate.MATERIAL_COLORS);

            otherBarData = new BarData(dataBalanceBonus);
        }
        otherBarData.setBarWidth(barWidth);

        return otherBarData;
    }

    @Subscribe
    public void onEvent(PresentationBus presentationBus) {
        reportList = new ArrayList<>(presentationBus.getReportList());
        xLabelList = new ArrayList<>(presentationBus.getXLabelList());

        chartRefresh = true;
        if (isVisibleToUser) {
            drawChart();
            chartRefresh = false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;

        if (chartRefresh && isVisibleToUser) {
            drawChart();
            chartRefresh = false;
        }
    }
}