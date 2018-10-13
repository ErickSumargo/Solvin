package id.solvin.dev.view.widget;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Erick Sumargo on 11/25/2016.
 */

public class CustomProgressDialog extends AppCompatActivity {
    private AlertDialog.Builder customBuilder;
    private AlertDialog customAlertDialog;
    private TextView progressDialogMessage;

    public CustomProgressDialog(Context context) {
        customBuilder = new AlertDialog.Builder(context);
        customAlertDialog = customBuilder.create();
        customAlertDialog.setView(customAlertDialog.getLayoutInflater().inflate(id.solvin.dev.R.layout.custom_progress_dialog, null));
        customAlertDialog.setCancelable(false);
        customAlertDialog.setCanceledOnTouchOutside(false);
        customAlertDialog.show();
    }

    public void setMessage(String message) {
        progressDialogMessage = (TextView) customAlertDialog.findViewById(id.solvin.dev.R.id.custom_progress_dialog_message);
        progressDialogMessage.setText(message);
    }

    public void dismiss() {
        customAlertDialog.dismiss();
    }
}