package id.solvin.dev.view.widget;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import id.solvin.dev.R;

/**
 * Created by Erick Sumargo on 4/8/2017.
 */

public class CustomOptionDialog extends AppCompatActivity {
    //    INTERFACE
    private OnRenameClickListener mRenameClickListener;
    private OnDeleteClickListener mDeleteClickListener;

    public interface OnRenameClickListener {
        void onClick();
    }

    public interface OnDeleteClickListener {
        void onClick();
    }

    private void setOnRenameClickListener() {
        if (mRenameClickListener != null) {
            mRenameClickListener.onClick();
        }
    }

    private void setOnDeleteClickListener() {
        if (mDeleteClickListener != null) {
            mDeleteClickListener.onClick();
        }
    }

    //    VIEW
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private TextView alertDialogTitle;
    private LinearLayout renameContainer, deleteContainer;

    //    HELPER
    private Context context;

    public CustomOptionDialog(Context context) {
        this.context = context;

        init();
        setEvent();
    }

    private void init() {
        builder = new AlertDialog.Builder(context);
        alertDialog = builder.create();
        alertDialog.setView(alertDialog.getLayoutInflater().inflate(id.solvin.dev.R.layout.custom_option_dialog, null));
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        alertDialogTitle = (TextView) alertDialog.findViewById(R.id.custom_option_dialog_title);
        renameContainer = (LinearLayout) alertDialog.findViewById(R.id.custom_option_dialog_rename_container);
        deleteContainer = (LinearLayout) alertDialog.findViewById(R.id.custom_option_dialog_delete_container);
    }

    private void setEvent() {
        renameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnRenameClickListener();
            }
        });

        deleteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnDeleteClickListener();
            }
        });
    }

    public void setTitle(String title) {
        alertDialogTitle.setText(title);
    }

    public void setOnRenameListener(OnRenameClickListener listener) {
        mRenameClickListener = listener;
    }

    public void setOnDeleteListener(OnDeleteClickListener listener) {
        mDeleteClickListener = listener;
    }

    public void dismiss() {
        alertDialog.dismiss();
    }
}