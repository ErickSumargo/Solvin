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

public class FragmentPresentationTransaction extends Fragment {
    //    VIEW
    private View view;
    private ListView transactionChartView;

    //    OBJECT
    private List<DataPlainReport> reportList;
    private List<String> xLabelList;

    private List<BarEntry> transactionPendingGroup, transactionSuccessGroup, transactionCanceledGroup;
    private List<IBarDataSet> transactionGroup;

    private BarDataSet dataTransactionPending, dataTransactionSuccess, dataTransactionCanceled;

    private BarData transactionBarData;
    private List<BarData> transactionBarDataList;

    private BarChartViewAdapter barChartViewAdapter;

    //    VARIABLE
    private float barWidth = 0.45f;
    private boolean chartRefresh = true, isVisibleToUser = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_presentation_transaction, container, false);

        init();

        return view;
    }

    private void init() {
        //    VIEW
        transactionChartView = (ListView) view.findViewById(R.id.transaction_chart_view);
    }

    private void drawChart() {
        transactionBarDataList = new ArrayList<>();
        transactionBarDataList.add(getTransactionBarData(0));
        transactionBarDataList.add(getTransactionBarData(1));
        transactionBarDataList.add(getTransactionBarData(2));

        barChartViewAdapter = new BarChartViewAdapter(getContext(), transactionBarDataList, xLabelList, 1, false);
        transactionChartView.setAdapter(barChartViewAdapter);
    }

    private BarData getTransactionBarData(int packageType) {
        transactionPendingGroup = new ArrayList<>();
        transactionSuccessGroup = new ArrayList<>();
        transactionCanceledGroup = new ArrayList<>();
        transactionGroup = new ArrayList<>();

        int i = 0;
        for (DataPlainReport dataReport : reportList) {
            if (packageType == 0) {
                transactionPendingGroup.add(new BarEntry(i, dataReport.getTransactionPackage1Pending()));
                transactionSuccessGroup.add(new BarEntry(i, dataReport.getTransactionPackage1Success()));
                transactionCanceledGroup.add(new BarEntry(i, dataReport.getTransactionPackage1Canceled()));
            } else if (packageType == 1) {
                transactionPendingGroup.add(new BarEntry(i, dataReport.getTransactionPackage2Pending()));
                transactionSuccessGroup.add(new BarEntry(i, dataReport.getTransactionPackage2Success()));
                transactionCanceledGroup.add(new BarEntry(i, dataReport.getTransactionPackage2Canceled()));
            } else if (packageType == 2) {
                transactionPendingGroup.add(new BarEntry(i, dataReport.getTransactionPackage3Pending()));
                transactionSuccessGroup.add(new BarEntry(i, dataReport.getTransactionPackage3Success()));
                transactionCanceledGroup.add(new BarEntry(i, dataReport.getTransactionPackage3Canceled()));
            }
            i++;
        }

        dataTransactionPending = new BarDataSet(transactionPendingGroup, "Pending");
        dataTransactionPending.setColors(ColorTemplate.MATERIAL_COLORS);

        dataTransactionSuccess = new BarDataSet(transactionSuccessGroup, "Lunas");
        dataTransactionSuccess.setColors(ColorTemplate.MATERIAL_COLORS);

        dataTransactionCanceled = new BarDataSet(transactionCanceledGroup, "Dibatalkan");
        dataTransactionCanceled.setColors(ColorTemplate.MATERIAL_COLORS);

        transactionGroup.add(dataTransactionPending);
        transactionGroup.add(dataTransactionSuccess);
        transactionGroup.add(dataTransactionCanceled);

        transactionBarData = new BarData(transactionGroup);
        transactionBarData.setBarWidth(barWidth);

        return transactionBarData;
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