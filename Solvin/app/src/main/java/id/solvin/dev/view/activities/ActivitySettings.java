package id.solvin.dev.view.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Session;
import id.solvin.dev.view.widget.CustomAlertDialog;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 9/25/2016.
 */
public class ActivitySettings extends AppCompatActivity {
    private Toolbar toolbar;
    private LinearLayout notificationReceiveContainer, notificationSoundContainer, notificationVibrationContainer,
            infoPrivacyPolicyContainer, infoTermsConditionContainer, infoRateUsContainer, infoLogOutContainer;
    private SwitchCompat notificationReceiveSwitch, notificationSoundSwitch, notificationVibrationSwitch;

    private ClassApplicationTool applicationTool;
    private CustomAlertDialog customAlertDialog;
    private ClassRaterDialog raterDialog;

    private Uri notification;
    private MediaPlayer mediaPlayer;
    private Ringtone ringtone;
    private Vibrator vibrator;

    private Intent intent;

    private final long[] pattern = {0, 250, 200, 250};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        setEvent();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notificationReceiveContainer = (LinearLayout) findViewById(R.id.settings_notification_receive_container);
        notificationSoundContainer = (LinearLayout) findViewById(R.id.settings_notification_sound_container);
        notificationVibrationContainer = (LinearLayout) findViewById(R.id.settings_notification_vibration_container);
        infoPrivacyPolicyContainer = (LinearLayout) findViewById(R.id.settings_info_privacy_policy_container);
        infoTermsConditionContainer = (LinearLayout) findViewById(R.id.settings_info_terms_condition_container);
        infoRateUsContainer = (LinearLayout) findViewById(R.id.settings_info_rate_us_container);
        infoLogOutContainer = (LinearLayout) findViewById(R.id.settings_info_log_out_container);

        notificationReceiveSwitch = (SwitchCompat) findViewById(R.id.settings_notification_receive_switch);
        notificationSoundSwitch = (SwitchCompat) findViewById(R.id.settings_notification_sound_switch);
        notificationVibrationSwitch = (SwitchCompat) findViewById(R.id.settings_notification_vibration_switch);

        applicationTool = new ClassApplicationTool(getApplicationContext());
        raterDialog = new ClassRaterDialog(ActivitySettings.this);

        notificationReceiveSwitch.setChecked(Session.with(getApplicationContext()).getSetting(Session.KEY_SETTING_GET_NOTIFICATION));
        notificationInit(!notificationReceiveSwitch.isChecked());
        notificationSoundSwitch.setChecked(Session.with(getApplicationContext()).getSetting(Session.KEY_SETTING_SOUND));
        notificationVibrationSwitch.setChecked(Session.with(getApplicationContext()).getSetting(Session.KEY_SETTING_VIBRATIONN));

    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        notificationReceiveContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationInit(notificationReceiveSwitch.isChecked());
                if (notificationReceiveSwitch.isChecked()) {
                    notificationReceiveSwitch.setChecked(false);
                } else {
                    notificationReceiveSwitch.setChecked(true);
                }

                saveSettings();
            }
        });

        notificationSoundContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundInit();
                saveSettings();
            }
        });

        notificationVibrationContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrationInit();
                saveSettings();
            }


        });

        infoPrivacyPolicyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplication(), ActivityPrivacyPolicy.class);
                startActivity(intent);
            }
        });

        infoTermsConditionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplication(), ActivityTermsCondition.class);
                startActivity(intent);
            }
        });

        infoRateUsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                raterDialog.show();
            }
        });

        infoLogOutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationLogOutDialog();
            }
        });
    }

    private void notificationInit(boolean isChecked) {
        if (isChecked) {
            notificationSoundSwitch.setEnabled(false);
            notificationVibrationSwitch.setEnabled(false);
        } else {
            notificationSoundSwitch.setEnabled(true);
            notificationVibrationSwitch.setEnabled(true);
        }
    }

    private void soundInit() {
        if (notificationReceiveSwitch.isChecked()) {
            if (notificationSoundSwitch.isChecked()) {
                notificationSoundSwitch.setChecked(false);
            } else {
                notificationSoundSwitch.setChecked(true);
                playSound();
            }
        }
    }

    private void vibrationInit() {
        if (notificationReceiveSwitch.isChecked()) {
            if (notificationVibrationSwitch.isChecked()) {
                notificationVibrationSwitch.setChecked(false);
            } else {
                notificationVibrationSwitch.setChecked(true);
                if (notificationSoundSwitch.isChecked()) {
                    playSound();
                }
                playVibrate();
            }
        }
    }

    private void playSound() {
//        Custom ringtone
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.solvin);
        mediaPlayer.start();

//        Default ringtone
        /*
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
        ringtone.play();
        */
    }

    private void playVibrate() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, -1);
    }

    private void showConfirmationLogOutDialog() {
        customAlertDialog = new CustomAlertDialog(ActivitySettings.this);
        customAlertDialog.setTitle("Konfirmasi");
        customAlertDialog.setMessage("Anda yakin ingin keluar?");
        customAlertDialog.setNegativeButton("Tidak", new CustomAlertDialog.OnNegativeClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();
            }
        });
        customAlertDialog.setPositiveButton("Ya", new CustomAlertDialog.OnPositiveClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();
                Session.with(getApplicationContext()).clearLoginSession();
            }
        });
    }

    private void saveSettings() {
        Session.with(getApplicationContext()).setSettings(notificationReceiveSwitch.isChecked(),
                notificationSoundSwitch.isChecked(), notificationVibrationSwitch.isChecked());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}