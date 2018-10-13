package id.solvinap.dev.view.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import id.solvinap.dev.R;
import id.solvinap.dev.view.helper.Tool;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.List;

/**
 * Created by Erick Sumargo on 3/2/2017.
 */

public class BarChartViewAdapter extends ArrayAdapter<BarData> {
    //    HELPER
    private Context context;
    private ViewHolder holder;

    private XAxis xAxis;
    private AudioManager audioManager;

    //    OBJECT
    private List<String> xLabelList;

    //    VARIABLE
    private int barStyle;
    private boolean singular;
    private float barSpace = 0.02f, groupSpace = 0.06f;

    public BarChartViewAdapter(Context context, List<BarData> barDataList, List<String> xLabelList, int barStyle, boolean singular) {
        super(context, 0, barDataList);

        this.context = context;
        this.xLabelList = xLabelList;

        this.barStyle = barStyle;
        this.singular = singular;
    }

    private class ViewHolder {
        public BarChart barChart;
        public HorizontalBarChart horizontalBarChart;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BarData barData = getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();

            if (barStyle == 0) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.bar_chart_item, null);
                holder.barChart = (BarChart) convertView.findViewById(R.id.bar_chart);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.horizontal_bar_chart_item, null);
                holder.horizontalBarChart = (HorizontalBarChart) convertView.findViewById(R.id.horizontal_bar_chart);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        setXAxisProperties(holder);
        if (barStyle == 0) {
            holder.barChart.getDescription().setEnabled(false);
            holder.barChart.setDoubleTapToZoomEnabled(false);
            holder.barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    playClickSound();

                    final Toast toast = Toast.makeText(context, String.valueOf(e.getY()), Toast.LENGTH_SHORT);
                    Tool.getInstance(getContext()).resizeToast(toast);
                    toast.show();
                }

                @Override
                public void onNothingSelected() {

                }
            });

            holder.barChart.setData(barData);
            if (!singular) {
                holder.barChart.groupBars(-1f, groupSpace, barSpace);
            }
            holder.barChart.invalidate();
            holder.barChart.animateY(1500, Easing.EasingOption.EaseOutQuad);
        } else {
            holder.horizontalBarChart.getDescription().setEnabled(false);
            holder.horizontalBarChart.setDoubleTapToZoomEnabled(false);
            holder.horizontalBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    playClickSound();

                    final Toast toast = Toast.makeText(context, String.valueOf(e.getY()), Toast.LENGTH_SHORT);
                    Tool.getInstance(getContext()).resizeToast(toast);
                    toast.show();
                }

                @Override
                public void onNothingSelected() {

                }
            });
            holder.horizontalBarChart.setData(barData);
            if (!singular) {
                holder.horizontalBarChart.groupBars(0f, groupSpace, barSpace);
            }
            holder.horizontalBarChart.invalidate();
            holder.horizontalBarChart.animateY(1500, Easing.EasingOption.EaseOutQuad);
        }

        return convertView;
    }

    private void setXAxisProperties(ViewHolder holder) {
        if (barStyle == 0) {
            xAxis = holder.barChart.getXAxis();
        } else {
            xAxis = holder.horizontalBarChart.getXAxis();
        }
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(context.getResources().getColor(R.color.colorHeader));
        xAxis.setTextSize(10f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int x = (int) value;
                if (x < 0) {
                    x = 0;
                } else if (x >= xLabelList.size()) {
                    x = xLabelList.size() - 1;
                }
                return xLabelList.get(x);
            }
        });
    }

    private void playClickSound() {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, 1.0f);
    }
}