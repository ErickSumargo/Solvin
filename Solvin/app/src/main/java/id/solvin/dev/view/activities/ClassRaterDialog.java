package id.solvin.dev.view.activities;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;

/**
 * Created by edinofri on 02/01/2017.
 */


public class ClassRaterDialog extends AppCompatActivity {
    //    UI COMPONENT
    private AlertDialog.Builder customBuilder;
    private AlertDialog raterDialog;
    private TextView drop, cancel, rate;

    //    UI HELPER
    private Context context;
    private Intent intent;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public ClassRaterDialog(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(ClassApplicationTool.PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void show() {
        customBuilder = new AlertDialog.Builder(context);
        raterDialog = customBuilder.create();
        raterDialog.setView(raterDialog.getLayoutInflater().inflate(R.layout.rater_dialog, null));
        raterDialog.setCanceledOnTouchOutside(false);
        raterDialog.show();

        init();
        setEvent();
    }

    public void showAsNeeded() {
        if (!sharedPreferences.getBoolean(ClassApplicationTool.FIRST_LAUNCH, false)) {
            editor.putBoolean(ClassApplicationTool.FIRST_LAUNCH, true);
            editor.putLong(ClassApplicationTool.DATE_CURRENT_LAUNCH, System.currentTimeMillis());
            editor.commit();
        } else {
            if (sharedPreferences.getBoolean(ClassApplicationTool.NEXT_PROMPT, true)) {
                if (System.currentTimeMillis() >= sharedPreferences.getLong(ClassApplicationTool.DATE_CURRENT_LAUNCH, 0) +
                        (ClassApplicationTool.SCHEDULE_PROMPT * 24 * 60 * 60 * 1000)) {
                    show();
                }
            }
        }
    }

    private void init() {
        drop = (TextView) raterDialog.findViewById(R.id.rater_dialog_drop_button);
        cancel = (TextView) raterDialog.findViewById(R.id.rater_dialog_cancel_button);
        rate = (TextView) raterDialog.findViewById(R.id.rater_dialog_rate_button);
    }

    private void setEvent() {
        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putBoolean(ClassApplicationTool.NEXT_PROMPT, false);
                editor.commit();

                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getBoolean(ClassApplicationTool.NEXT_PROMPT, true)) {
                    editor.putLong(ClassApplicationTool.DATE_CURRENT_LAUNCH, System.currentTimeMillis());
                    editor.commit();
                }
                dismiss();
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getBoolean(ClassApplicationTool.NEXT_PROMPT, true)) {
                    editor.putBoolean(ClassApplicationTool.NEXT_PROMPT, false);
                    editor.commit();
                }
                dismiss();
                openPlayStore();
            }
        });
    }

    private void dismiss() {
        raterDialog.dismiss();
    }

    private void openPlayStore() {
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=id.solvin.app"));
        context.startActivity(intent);

//        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
//        try {
//            startActivity(intent);
//        } catch (Exception e) {
//            Toast.makeText(context, context.getPackageName(), Toast.LENGTH_SHORT).show();
//        }
    }
}