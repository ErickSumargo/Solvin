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

public class FragmentPresentationSolution extends Fragment {
    //    VIEW
    private View view;
    private ListView solutionChartView;

    //    OBJECT
    private List<DataPlainReport> reportList;
    private List<String> xLabelList;

    private List<BarEntry> bestSolutionGroup, totalSolutionGroup;
    private List<IBarDataSet> solutionGroup;

    private BarDataSet dataBestSolution, dataTotalSolution;

    private BarData solutionBarData;
    private List<BarData> solutionBarDataList;

    private BarChartViewAdapter barChartViewAdapter;

    //    VARIABLE
    private float barWidth = 0.45f;
    private boolean chartRefresh = true, isVisibleToUser = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_presentation_solution, container, false);

        init();

        return view;
    }

    private void init() {
        //    VIEW
        solutionChartView = (ListView) view.findViewById(R.id.solution_chart_view);
    }

    private void drawChart() {
        solutionBarDataList = new ArrayList<>();
        solutionBarDataList.add(getBarData(0));
        solutionBarDataList.add(getBarData(1));

        barChartViewAdapter = new BarChartViewAdapter(getContext(), solutionBarDataList, xLabelList, 1, false);
        solutionChartView.setAdapter(barChartViewAdapter);
    }

    private BarData getBarData(int subject) {
        bestSolutionGroup = new ArrayList<>();
        totalSolutionGroup = new ArrayList<>();
        solutionGroup = new ArrayList<>();

        int i = 0;
        for (DataPlainReport dataReport : reportList) {
            if (subject == 0) {
                bestSolutionGroup.add(new BarEntry(i, dataReport.getMathematicsBestSolution()));
                totalSolutionGroup.add(new BarEntry(i, dataReport.getMathematicsTotalSolution()));
            } else if (subject == 1) {
                bestSolutionGroup.add(new BarEntry(i, dataReport.getPhysicsBestSolution()));
                totalSolutionGroup.add(new BarEntry(i, dataReport.getPhysicsTotalSolution()));
            }
            i++;
        }

        dataBestSolution = new BarDataSet(bestSolutionGroup, "Terbaik");
        dataBestSolution.setColors(ColorTemplate.MATERIAL_COLORS);

        dataTotalSolution = new BarDataSet(totalSolutionGroup, "Total");
        dataTotalSolution.setColors(ColorTemplate.MATERIAL_COLORS);

        solutionGroup.add(dataBestSolution);
        solutionGroup.add(dataTotalSolution);

        solutionBarData = new BarData(solutionGroup);
        solutionBarData.setBarWidth(barWidth);

        return solutionBarData;
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