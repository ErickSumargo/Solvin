package id.solvin.dev.view.widget;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Erick Sumargo on 3/8/2017.
 */

public class CustomAlertDialog extends AppCompatActivity {
    //    INTERFACE
    private CustomAlertDialog.OnNegativeClickListener mNegativeClickListener;
    private CustomAlertDialog.OnPositiveClickListener mPositiveClickListener;

    public interface OnNegativeClickListener {
        void onClick();
    }

    public interface OnPositiveClickListener {
        void onClick();
    }

    private void setOnNegativeClickListener() {
        if (mNegativeClickListener != null) {
            mNegativeClickListener.onClick();
        }
    }

    private void setOnPositiveClickListener() {
        if (mPositiveClickListener != null) {
            mPositiveClickListener.onClick();
        }
    }

    //    VIEW
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private TextView alertDialogTitle, alertDialogMessage, alertDialogNegative, alertDialogPositive;

    //    HELPER
    private Context context;

    public CustomAlertDialog(Context context) {
        this.context = context;

        init();
        setEvent();
    }

    private void init() {
        builder = new AlertDialog.Builder(context);
        alertDialog = builder.create();
        alertDialog.setView(alertDialog.getLayoutInflater().inflate(id.solvin.dev.R.layout.custom_alert_dialog, null));
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        alertDialogTitle = (TextView) alertDialog.findViewById(id.solvin.dev.R.id.custom_alert_dialog_title);
        alertDialogMessage = (TextView) alertDialog.findViewById(id.solvin.dev.R.id.custom_alert_dialog_message);
        alertDialogNegative = (TextView) alertDialog.findViewById(id.solvin.dev.R.id.custom_alert_dialog_negative);
        alertDialogPositive = (TextView) alertDialog.findViewById(id.solvin.dev.R.id.custom_alert_dialog_positive);
    }

    private void setEvent() {
        alertDialogNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnNegativeClickListener();
            }
        });

        alertDialogPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnPositiveClickListener();
            }
        });
    }

    public void setTitle(String title) {
        alertDialogTitle.setText(title);
    }

    public void setMessage(String message) {
        alertDialogMessage.setText(message);
    }

    public void setNegativeButton(String negative, CustomAlertDialog.OnNegativeClickListener listener) {
        if (listener == null) {
            alertDialogNegative.setVisibility(View.GONE);
        } else {
            alertDialogNegative.setText(negative);
            alertDialogNegative.setVisibility(View.VISIBLE);
            mNegativeClickListener = listener;
        }
    }

    public void setPositiveButton(String positive, CustomAlertDialog.OnPositiveClickListener listener) {
        alertDialogPositive.setText(positive);
        mPositiveClickListener = listener;
    }

    public void dismiss() {
        alertDialog.dismiss();
    }
}