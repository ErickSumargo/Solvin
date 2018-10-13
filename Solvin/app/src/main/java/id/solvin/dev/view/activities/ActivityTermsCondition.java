package id.solvin.dev.view.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import id.solvin.dev.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 10/11/2016.
 */
public class ActivityTermsCondition extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView introduction;
    private TextView userPoint_1, userPoint_2, userPoint_3, userPoint_4, userPoint_5, userPoint_6,
            userPoint_7, userPoint_8, userPoint_9, userPoint_10, userPoint_11;
    private TextView mentorPoint_1, mentorPoint_2, mentorPoint_3, mentorPoint_4, mentorPoint_5, mentorPoint_6,
            mentorPoint_7, mentorPoint_8;
    private TextView callUsIntroduction, callUsEmail, callUsContactNumber;
    private TextView latestUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_condition);
        init();
        setEvent();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        introduction = (TextView) findViewById(R.id.terms_condition_introduction);

        userPoint_1 = (TextView) findViewById(R.id.terms_condition_student_point_1);
        userPoint_2 = (TextView) findViewById(R.id.terms_condition_student_point_2);
        userPoint_3 = (TextView) findViewById(R.id.terms_condition_student_point_3);
        userPoint_4 = (TextView) findViewById(R.id.terms_condition_student_point_4);
        userPoint_5 = (TextView) findViewById(R.id.terms_condition_student_point_5);
        userPoint_6 = (TextView) findViewById(R.id.terms_condition_student_point_6);
        userPoint_7 = (TextView) findViewById(R.id.terms_condition_student_point_7);
        userPoint_8 = (TextView) findViewById(R.id.terms_condition_student_point_8);
        userPoint_9 = (TextView) findViewById(R.id.terms_condition_student_point_9);
        userPoint_10 = (TextView) findViewById(R.id.terms_condition_student_point_10);
        userPoint_11 = (TextView) findViewById(R.id.terms_condition_student_point_11);

        mentorPoint_1 = (TextView) findViewById(R.id.terms_condition_mentor_point_1);
        mentorPoint_2 = (TextView) findViewById(R.id.terms_condition_mentor_point_2);
        mentorPoint_3 = (TextView) findViewById(R.id.terms_condition_mentor_point_3);
        mentorPoint_4 = (TextView) findViewById(R.id.terms_condition_mentor_point_4);
        mentorPoint_5 = (TextView) findViewById(R.id.terms_condition_mentor_point_5);
        mentorPoint_6 = (TextView) findViewById(R.id.terms_condition_mentor_point_6);
        mentorPoint_7 = (TextView) findViewById(R.id.terms_condition_mentor_point_7);
        mentorPoint_8 = (TextView) findViewById(R.id.terms_condition_mentor_point_8);

        callUsIntroduction = (TextView) findViewById(R.id.terms_condition_call_us_introduction);
        callUsEmail = (TextView) findViewById(R.id.terms_condition_call_us_email);

        latestUpdate = (TextView) findViewById(R.id.terms_condition_latest_update);

        introduction.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_introduction)));

        userPoint_1.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_student_point_1)));
        userPoint_2.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_student_point_2)));
        userPoint_3.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_student_point_3)));
        userPoint_4.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_student_point_4)));
        userPoint_5.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_student_point_5)));
        userPoint_6.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_student_point_6)));
        userPoint_7.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_student_point_7)));
        userPoint_8.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_student_point_8)));
        userPoint_9.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_student_point_9)));
        userPoint_10.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_student_point_10)));
        userPoint_11.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_student_point_11)));

        mentorPoint_1.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_mentor_point_1)));
        mentorPoint_2.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_mentor_point_2)));
        mentorPoint_3.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_mentor_point_3)));
        mentorPoint_4.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_mentor_point_4)));
        mentorPoint_5.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_mentor_point_5)));
        mentorPoint_6.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_mentor_point_6)));
        mentorPoint_7.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_mentor_point_7)));
        mentorPoint_8.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_mentor_point_8)));

        callUsIntroduction.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_call_us_introduction)));
        callUsEmail.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_call_us_email)));

        latestUpdate.setText(Html.fromHtml(getResources().getString(R.string.terms_condition_latest_update)));
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}