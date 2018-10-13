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

import static android.media.CamcorderProfile.get;

/**
 * Created by Erick Sumargo on 2/24/2017.
 */

public class FragmentPresentationUser extends Fragment {
    //    VIEW
    private View view;
    private ListView userChartView;

    //    OBJECT
    private List<DataPlainReport> reportList;
    private List<String> xLabelList;

    private List<BarEntry> studentEntryList, mentorEntryList;
    private BarDataSet dataStudent, dataMentor;

    private BarData userBarData;
    private List<BarData> userBarDataList;

    private BarChartViewAdapter barChartViewAdapter;

    //    VARIABLE
    private float barWidth = 0.45f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_presentation_user, container, false);

        init();

        return view;
    }

    private void init() {
        //    VIEW
        userChartView = (ListView) view.findViewById(R.id.user_chart_view);
    }

    private void drawChart() {
        userBarDataList = new ArrayList<>();
        userBarDataList.add(getUserBarData(0));
        userBarDataList.add(getUserBarData(1));

        barChartViewAdapter = new BarChartViewAdapter(getContext(), userBarDataList, xLabelList, 0, true);
        userChartView.setAdapter(barChartViewAdapter);
    }

    private BarData getUserBarData(int type) {
        studentEntryList = new ArrayList<>();
        mentorEntryList = new ArrayList<>();

        int i = 0;
        for (DataPlainReport dataReport : reportList) {
            if (type == 0) {
                studentEntryList.add(new BarEntry(i, dataReport.getStudent()));
            } else if (type == 1) {
                mentorEntryList.add(new BarEntry(i, dataReport.getMentor()));
            }
            i++;
        }
        if (type == 0) {
            dataStudent = new BarDataSet(studentEntryList, "Murid");
            dataStudent.setColors(ColorTemplate.MATERIAL_COLORS);

            userBarData = new BarData(dataStudent);
        } else if (type == 1) {
            dataMentor = new BarDataSet(mentorEntryList, "Mentor");
            dataMentor.setColors(ColorTemplate.MATERIAL_COLORS);

            userBarData = new BarData(dataMentor);
        }
        userBarData.setBarWidth(barWidth);

        return userBarData;
    }

    @Subscribe
    public void onEvent(PresentationBus presentationBus) {
        reportList = new ArrayList<>(presentationBus.getReportList());
        xLabelList = new ArrayList<>(presentationBus.getXLabelList());
        drawChart();
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
}