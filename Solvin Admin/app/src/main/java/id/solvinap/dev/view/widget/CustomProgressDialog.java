package id.solvinap.dev.view.widget;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import id.solvinap.dev.R;


/**
 * Created by Erick Sumargo on 2/1/2017.
 */

public class CustomProgressDialog extends AppCompatActivity {
    //    UI COMPONENT
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private TextView progressDialogMessage;

    public CustomProgressDialog(Context context) {
        builder = new AlertDialog.Builder(context);
        alertDialog = builder.create();
        alertDialog.setView(alertDialog.getLayoutInflater().inflate(R.layout.custom_progress_dialog, null));
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void setMessage(String message) {
        progressDialogMessage = (TextView) alertDialog.findViewById(R.id.custom_progress_dialog_message);
        progressDialogMessage.setText(message);
    }

    public void dismiss() {
        alertDialog.dismiss();
    }
}