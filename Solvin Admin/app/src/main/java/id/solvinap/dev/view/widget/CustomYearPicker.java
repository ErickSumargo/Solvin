package id.solvinap.dev.view.widget;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import id.solvinap.dev.R;
import id.solvinap.dev.view.helper.DataPresentationYear;

import java.util.List;

/**
 * Created by Erick Sumargo on 3/4/2017.
 */

public class CustomYearPicker extends AppCompatActivity {
    //    VIEW
    private AlertDialog.Builder yearPickerBuilder;
    private AlertDialog yearPickerDialog;

    private NumberPicker yearPicker;
    private TextView yearPickerDialogNegative, yearPickerDialogPositive;

    //    HELPER
    private Context context;

    //    OBJECT
    private List<DataPresentationYear> yearList;

    //    VARIABLE
    private int selectedYear;

    //    INTERFACE
    private CustomYearPicker.OnNegativeClickListener mNegativeClickListener;
    private CustomYearPicker.OnPositiveClickListener mPositiveClickListener;

    public interface OnNegativeClickListener {
        void onClick();
    }

    public interface OnPositiveClickListener {
        void onClick(int i);
    }

    public CustomYearPicker(Context context, List<DataPresentationYear> yearList, int selectedYear) {
        this.context = context;
        this.yearList = yearList;
        this.selectedYear = selectedYear;

        init();
        setEvent();
    }

    private void init() {
        yearPickerBuilder = new AlertDialog.Builder(context);
        yearPickerDialog = yearPickerBuilder.create();
        yearPickerDialog.setView(yearPickerDialog.getLayoutInflater().inflate(R.layout.custom_year_picker, null));
        yearPickerDialog.setCanceledOnTouchOutside(true);
        yearPickerDialog.show();

        yearPicker = (NumberPicker) yearPickerDialog.findViewById(R.id.year_picker);
        yearPickerDialogNegative = (TextView) yearPickerDialog.findViewById(R.id.custom_year_picker_negative);
        yearPickerDialogPositive = (TextView) yearPickerDialog.findViewById(R.id.custom_year_picker_positive);

        yearPicker.setMaxValue(Integer.parseInt(yearList.get(0).getYear()));
        yearPicker.setMinValue(Integer.parseInt(yearList.get(yearList.size() - 1).getYear()));
        yearPicker.setValue(selectedYear);
        yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    private void setEvent() {
        yearPickerDialogNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setmNegativeClickListener();
            }
        });

        yearPickerDialogPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (DataPresentationYear year : yearList) {
                    if (Integer.parseInt(year.getYear()) == yearPicker.getValue()) {
                        setmPositiveClickListener(yearList.indexOf(year));
                    }
                }
            }
        });
    }

    public void setOnNegativeClickListener(CustomYearPicker.OnNegativeClickListener listener) {
        mNegativeClickListener = listener;
    }

    public void setOnPositiveClickListener(CustomYearPicker.OnPositiveClickListener listener) {
        mPositiveClickListener = listener;
    }

    private void setmNegativeClickListener() {
        if (mNegativeClickListener != null) {
            mNegativeClickListener.onClick();
        }
    }

    private void setmPositiveClickListener(int i) {
        if (mPositiveClickListener != null) {
            mPositiveClickListener.onClick(i);
        }
    }

    public void dismiss() {
        yearPickerDialog.dismiss();
    }
}