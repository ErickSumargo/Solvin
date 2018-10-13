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

/**
 * Created by Erick Sumargo on 3/3/2017.
 */

public class FragmentPresentationComment extends Fragment {
    //    VIEW
    private View view;
    private ListView commentChartView;

    //    OBJECT
    private List<DataPlainReport> reportList;
    private List<String> xLabelList;

    private List<BarEntry> commentEntryList;
    private BarDataSet dataComment;

    private BarData commentBarData;
    private List<BarData> commentBarDataList;

    private BarChartViewAdapter barChartViewAdapter;

    //    VARIABLE
    private float barWidth = 0.45f;
    private boolean chartRefresh = true, isVisibleToUser = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_presentation_comment, container, false);

        init();

        return view;
    }

    private void init() {
        //    VIEW
        commentChartView = (ListView) view.findViewById(R.id.comment_chart_view);
    }

    private void drawChart() {
        commentBarDataList = new ArrayList<>();
        commentBarDataList.add(getCommentBarData(0));
        commentBarDataList.add(getCommentBarData(1));

        barChartViewAdapter = new BarChartViewAdapter(getContext(), commentBarDataList, xLabelList, 0, true);
        commentChartView.setAdapter(barChartViewAdapter);
    }

    private BarData getCommentBarData(int subject) {
        commentEntryList = new ArrayList<>();

        int i = 0;
        for (DataPlainReport dataReport : reportList) {
            if (subject == 0) {
                commentEntryList.add(new BarEntry(i, dataReport.getMathematicsComment()));
            } else if (subject == 1) {
                commentEntryList.add(new BarEntry(i, dataReport.getPhysicsComment()));
            }
            i++;
        }

        dataComment = new BarDataSet(commentEntryList, "Komentar");
        dataComment.setColors(ColorTemplate.MATERIAL_COLORS);

        commentBarData = new BarData(dataComment);
        commentBarData.setBarWidth(barWidth);

        return commentBarData;
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