package id.solvin.dev.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import id.solvin.dev.view.activities.ActivityRegistration;
import id.solvin.dev.view.activities.ActivitySignIn;
import id.solvin.dev.view.activities.ActivityWelcome;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Erick Sumargo on 9/3/2016.
 */
public class FragmentWelcome3 extends Fragment {
    private View view;
    private CircleIndicator indicator3;
    private Button registration, signIn;

    private Intent intent;

    public FragmentWelcome3() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(id.solvin.dev.R.layout.activity_welcome3, container, false);
        init();
        setEvent();

        return view;
    }

    private void init() {
        indicator3 = (CircleIndicator) view.findViewById(id.solvin.dev.R.id.welcome3_indicator);
        registration = (Button) view.findViewById(id.solvin.dev.R.id.welcome3_registration);
        signIn = (Button) view.findViewById(id.solvin.dev.R.id.welcome3_sign_in);

        indicator3.setViewPager(((ActivityWelcome) getActivity()).viewPager);
    }

    private void setEvent() {
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), ActivityRegistration.class);
                startActivity(intent);
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), ActivitySignIn.class);
                startActivity(intent);
            }
        });
    }
}