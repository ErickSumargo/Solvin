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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Erick Sumargo on 3/3/2017.
 */

public class FragmentPresentationRedeemBalance extends Fragment {
    //    VIEW
    private View view;
    private ListView redeemBalanceChartView;

    //    OBJECT
    private List<DataPlainReport> reportList;
    private List<String> xLabelList;

    private List<BarEntry> redeemBalancePendingGroup, redeemBalanceSuccessGroup, redeemBalanceCanceledGroup;
    private List<IBarDataSet> redeemBalanceGroup;

    private BarDataSet dataRedeemBalancePending, dataRedeemBalanceSuccess, dataRedeemBalanceCanceled;

    private BarData redeemBalanceBarData;
    private List<BarData> redeemBalanceBarDataList;

    private BarChartViewAdapter barChartViewAdapter;

    //    VARIABLE
    private float barWidth = 0.45f;
    private boolean chartRefresh = true, isVisibleToUser = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_presentation_redeem_balance, container, false);

        init();

        return view;
    }

    private void init() {
        //    VIEW
        redeemBalanceChartView = (ListView) view.findViewById(R.id.redeem_balance_chart_view);
    }

    private void drawChart() {
        redeemBalanceBarDataList = new ArrayList<>();
        redeemBalanceBarDataList.add(getRedeemBalanceBarData());

        barChartViewAdapter = new BarChartViewAdapter(getContext(), redeemBalanceBarDataList, xLabelList, 1, false);
        redeemBalanceChartView.setAdapter(barChartViewAdapter);
    }

    private BarData getRedeemBalanceBarData() {
        redeemBalancePendingGroup = new ArrayList<>();
        redeemBalanceSuccessGroup = new ArrayList<>();
        redeemBalanceCanceledGroup = new ArrayList<>();
        redeemBalanceGroup = new ArrayList<>();

        int i = 0;
        for (DataPlainReport dataReport : reportList) {
            redeemBalancePendingGroup.add(new BarEntry(i, dataReport.getRedeemBalancePending()));
            redeemBalanceSuccessGroup.add(new BarEntry(i, dataReport.getRedeemBalanceSuccess()));
            redeemBalanceCanceledGroup.add(new BarEntry(i, dataReport.getRedeemBalanceCanceled()));

            i++;
        }

        dataRedeemBalancePending = new BarDataSet(redeemBalancePendingGroup, "Pending");
        dataRedeemBalancePending.setColors(ColorTemplate.MATERIAL_COLORS);

        dataRedeemBalanceSuccess = new BarDataSet(redeemBalanceSuccessGroup, "Berhasil");
        dataRedeemBalanceSuccess.setColors(ColorTemplate.MATERIAL_COLORS);

        dataRedeemBalanceCanceled = new BarDataSet(redeemBalanceCanceledGroup, "Ditolak");
        dataRedeemBalanceCanceled.setColors(ColorTemplate.MATERIAL_COLORS);

        redeemBalanceGroup.add(dataRedeemBalancePending);
        redeemBalanceGroup.add(dataRedeemBalanceSuccess);
        redeemBalanceGroup.add(dataRedeemBalanceCanceled);

        redeemBalanceBarData = new BarData(redeemBalanceGroup);
        redeemBalanceBarData.setBarWidth(barWidth);

        return redeemBalanceBarData;
    }

    @Subscribe
    public void onEvent(PresentationBus presentationBus) {
        reportList = new ArrayList<>(presentationBus.getReportList());
        Collections.reverse(reportList);

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