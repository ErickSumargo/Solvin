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
 * Created by Erick Sumargo on 10/9/2016.
 */
public class ActivityPrivacyPolicy extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView introduction, informationTypeIntroduction;
    private TextView definitionSystem, definitionApplication, definitionService, definitionWe, definitionUser;
    private TextView informationTypePoint_a, informationTypePoint_b, informationTypePoint_c;
    private TextView protectingInformationPart_1, protectingInformationPart_2;
    private TextView usingInformationPart_1, usingInformationPart_2, usingInformationPoint_a, usingInformationPoint_b,
            usingInformationPoint_c;
    private TextView violenceConsequencesIntroduction, violenceConsequencesPoint_a, violenceConsequencesPoint_b,
            violenceConsequencesPoint_c, violenceConsequencesDescription_1, violenceConsequencesDescription_2,
            violenceConsequencesDescription_3;
    private TextView callUsIntroduction, callUsEmail, callUsContactNumber;
    private TextView latestUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        init();
        setEvent();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        introduction = (TextView) findViewById(R.id.privacy_policy_introduction);
        informationTypeIntroduction = (TextView) findViewById(R.id.privacy_policy_information_type_introduction);

        definitionSystem = (TextView) findViewById(R.id.privacy_policy_definition_system);
        definitionApplication = (TextView) findViewById(R.id.privacy_policy_definition_application);
        definitionService = (TextView) findViewById(R.id.privacy_policy_definition_service);
        definitionWe = (TextView) findViewById(R.id.privacy_policy_definition_we);
        definitionUser = (TextView) findViewById(R.id.privacy_policy_definition_user);

        informationTypePoint_a = (TextView) findViewById(R.id.privacy_policy_information_type_point_a);
        informationTypePoint_b = (TextView) findViewById(R.id.privacy_policy_information_type_point_b);
        informationTypePoint_c = (TextView) findViewById(R.id.privacy_policy_information_type_point_c);

        protectingInformationPart_1 = (TextView) findViewById(R.id.privacy_policy_protecting_information_part_1);
        protectingInformationPart_2 = (TextView) findViewById(R.id.privacy_policy_protecting_information_part_2);

        usingInformationPart_1 = (TextView) findViewById(R.id.privacy_policy_using_information_part_1);
        usingInformationPart_2 = (TextView) findViewById(R.id.privacy_policy_using_information_part_2);
        usingInformationPoint_a = (TextView) findViewById(R.id.privacy_policy_using_information_point_a);
        usingInformationPoint_b = (TextView) findViewById(R.id.privacy_policy_using_information_point_b);
        usingInformationPoint_c = (TextView) findViewById(R.id.privacy_policy_using_information_point_c);

        violenceConsequencesIntroduction = (TextView) findViewById(R.id.privacy_policy_violation_consequences_introduction);
        violenceConsequencesPoint_a = (TextView) findViewById(R.id.privacy_policy_violation_consequences_point_a);
        violenceConsequencesPoint_b = (TextView) findViewById(R.id.privacy_policy_violation_consequences_point_b);
        violenceConsequencesPoint_c = (TextView) findViewById(R.id.privacy_policy_violation_consequences_point_c);
        violenceConsequencesDescription_1 = (TextView) findViewById(R.id.privacy_policy_violation_consequences_description_1);
        violenceConsequencesDescription_2 = (TextView) findViewById(R.id.privacy_policy_violation_consequences_description_2);
        violenceConsequencesDescription_3 = (TextView) findViewById(R.id.privacy_policy_violation_consequences_description_3);

        callUsIntroduction = (TextView) findViewById(R.id.privacy_policy_call_us_introduction);
        callUsEmail = (TextView) findViewById(R.id.privacy_policy_call_us_email);

        latestUpdate = (TextView) findViewById(R.id.privacy_policy_latest_update);

        introduction.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_introduction)));

        definitionSystem.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_definition_system)));
        definitionApplication.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_definition_application)));
        definitionService.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_definition_service)));
        definitionWe.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_definition_we)));
        definitionUser.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_definition_user)));

        informationTypeIntroduction.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_information_type_introduction)));
        informationTypePoint_a.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_information_type_point_a)));
        informationTypePoint_b.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_information_type_point_b)));
        informationTypePoint_c.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_information_type_point_c)));

        protectingInformationPart_1.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_protecting_information_part_1)));
        protectingInformationPart_2.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_protecting_information_part_2)));

        usingInformationPart_1.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_using_information_part_1)));
        usingInformationPart_2.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_using_information_part_2)));
        usingInformationPoint_a.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_using_information_point_a)));
        usingInformationPoint_b.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_using_information_point_b)));
        usingInformationPoint_c.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_using_information_point_c)));

        violenceConsequencesIntroduction.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_violation_consequences_introduction)));
        violenceConsequencesPoint_a.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_violation_consequences_point_a)));
        violenceConsequencesPoint_b.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_violation_consequences_point_b)));
        violenceConsequencesPoint_c.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_violation_consequences_point_c)));
        violenceConsequencesDescription_1.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_violation_consequences_description_1)));
        violenceConsequencesDescription_2.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_violation_consequences_description_2)));
        violenceConsequencesDescription_3.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_violation_consequences_description_3)));

        callUsIntroduction.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_call_us_introduction)));
        callUsEmail.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_call_us_email)));

        latestUpdate.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_latest_update)));
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