package id.solvin.dev.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.solvin.dev.R;
import id.solvin.dev.view.activities.ActivityWelcome;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Erick Sumargo on 9/3/2016.
 */
public class FragmentWelcome2 extends Fragment {
    private View view;
    private CircleIndicator indicator2;

    public FragmentWelcome2() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_welcome2, container, false);
        init();

        return view;
    }

    private void init() {
        indicator2 = (CircleIndicator) view.findViewById(R.id.welcome2_indicator);
        indicator2.setViewPager(((ActivityWelcome) getActivity()).viewPager);
    }
}