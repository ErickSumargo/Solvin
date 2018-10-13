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
import java.util.List;

/**
 * Created by Erick Sumargo on 2/24/2017.
 */

public class FragmentPresentationFinancial extends Fragment {
    //    VIEW
    private View view;
    private ListView financialChartView;

    //    OBJECT
    private List<DataPlainReport> reportList;
    private List<String> xLabelList;

    private List<BarEntry> incomeGroup, balanceRedeemedGroup, profitGroup;
    private List<IBarDataSet> financialGroup;

    private BarDataSet dataIncome, dataBalanceRedeemed, dataProfit;

    private BarData financialBarData;
    private List<BarData> financialBarDataList;

    private BarChartViewAdapter barChartViewAdapter;

    //    VARIABLE
    private float barWidth = 0.45f;
    private boolean chartRefresh = true, isVisibleToUser = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_presentation_financial, container, false);

        init();

        return view;
    }

    private void init() {
        //    VIEW
        financialChartView = (ListView) view.findViewById(R.id.financial_chart_view);
    }

    private void drawChart() {
        financialBarDataList = new ArrayList<>();
        financialBarDataList.add(getFinancialBarData(0));
        financialBarDataList.add(getFinancialBarData(1));
        financialBarDataList.add(getFinancialBarData(2));

        barChartViewAdapter = new BarChartViewAdapter(getContext(), financialBarDataList, xLabelList, 0, true);
        financialChartView.setAdapter(barChartViewAdapter);
    }

    private BarData getFinancialBarData(int type) {
        incomeGroup = new ArrayList<>();
        balanceRedeemedGroup = new ArrayList<>();
        profitGroup = new ArrayList<>();
        financialGroup = new ArrayList<>();

        int i = 0;
        for (DataPlainReport dataReport : reportList) {
            if (type == 0) {
                incomeGroup.add(new BarEntry(i, dataReport.getIncome()));
            } else if (type == 1) {
                balanceRedeemedGroup.add(new BarEntry(i, dataReport.getBalanceRedeemed()));
            } else if (type == 2) {
                profitGroup.add(new BarEntry(i, dataReport.getIncome() - dataReport.getBalanceRedeemed()));
            }
            i++;
        }

        dataIncome = new BarDataSet(incomeGroup, "Pemasukan");
        dataIncome.setColors(ColorTemplate.MATERIAL_COLORS);

        dataBalanceRedeemed = new BarDataSet(balanceRedeemedGroup, "Saldo Tebusan");
        dataBalanceRedeemed.setColors(ColorTemplate.MATERIAL_COLORS);

        dataProfit = new BarDataSet(profitGroup, "Profit");
        dataProfit.setColors(ColorTemplate.MATERIAL_COLORS);

        financialGroup.add(dataIncome);
        financialGroup.add(dataBalanceRedeemed);
        financialGroup.add(dataProfit);

        financialBarData = new BarData(financialGroup);
        financialBarData.setBarWidth(barWidth);

        return financialBarData;
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