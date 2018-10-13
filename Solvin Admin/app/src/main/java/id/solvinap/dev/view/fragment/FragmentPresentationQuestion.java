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
 * Created by Erick Sumargo on 2/24/2017.
 */

public class FragmentPresentationQuestion extends Fragment {
    //    VIEW
    private View view;
    private ListView questionChartView;

    //    OBJECT
    private List<DataPlainReport> reportList;
    private List<String> xLabelList;

    private List<BarEntry> questionPendingGroup, questionDiscussGroup, questionCompleteGroup;
    private List<IBarDataSet> questionGroup;

    private BarDataSet dataQuestionPending, dataQuestionDiscuss, dataQuestionComplete;

    private BarData questionBarData;
    private List<BarData> questionBarDataList;

    private BarChartViewAdapter barChartViewAdapter;

    //    VARIABLE
    private float barWidth = 0.45f;
    private boolean chartRefresh = true, isVisibleToUser = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_presentation_question, container, false);

        init();

        return view;
    }

    private void init() {
        //    VIEW
        questionChartView = (ListView) view.findViewById(R.id.question_chart_view);
    }

    private void drawChart() {
        questionBarDataList = new ArrayList<>();
        questionBarDataList.add(getQuestionBarData(0));
        questionBarDataList.add(getQuestionBarData(1));

        barChartViewAdapter = new BarChartViewAdapter(getContext(), questionBarDataList, xLabelList, 1, false);
        questionChartView.setAdapter(barChartViewAdapter);
    }

    private BarData getQuestionBarData(int subject) {
        questionPendingGroup = new ArrayList<>();
        questionDiscussGroup = new ArrayList<>();
        questionCompleteGroup = new ArrayList<>();
        questionGroup = new ArrayList<>();

        int i = 0;
        for (DataPlainReport dataReport : reportList) {
            if (subject == 0) {
                questionPendingGroup.add(new BarEntry(i, dataReport.getMathematicsQuestionPending()));
                questionDiscussGroup.add(new BarEntry(i, dataReport.getMathematicsQuestionDiscuss()));
                questionCompleteGroup.add(new BarEntry(i, dataReport.getMathematicsQuestionComplete()));
            } else if (subject == 1) {
                questionPendingGroup.add(new BarEntry(i, dataReport.getPhysicsQuestionPending()));
                questionDiscussGroup.add(new BarEntry(i, dataReport.getPhysicsQuestionDiscuss()));
                questionCompleteGroup.add(new BarEntry(i, dataReport.getPhysicsQuestionComplete()));
            }
            i++;
        }

        dataQuestionPending = new BarDataSet(questionPendingGroup, "Pending");
        dataQuestionPending.setColors(ColorTemplate.MATERIAL_COLORS);

        dataQuestionDiscuss = new BarDataSet(questionDiscussGroup, "Diskusi");
        dataQuestionDiscuss.setColors(ColorTemplate.MATERIAL_COLORS);

        dataQuestionComplete = new BarDataSet(questionCompleteGroup, "Selesai");
        dataQuestionComplete.setColors(ColorTemplate.MATERIAL_COLORS);

        questionGroup.add(dataQuestionComplete);
        questionGroup.add(dataQuestionDiscuss);
        questionGroup.add(dataQuestionPending);

        questionBarData = new BarData(questionGroup);
        questionBarData.setBarWidth(barWidth);

        return questionBarData;
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