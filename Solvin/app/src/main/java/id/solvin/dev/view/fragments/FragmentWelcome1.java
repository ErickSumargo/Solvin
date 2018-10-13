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
public class FragmentWelcome1 extends Fragment {
    private View view;
    private CircleIndicator indicator1;

    public FragmentWelcome1() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_welcome1, container, false);
        init();

        return view;
    }

    private void init() {
        indicator1 = (CircleIndicator) view.findViewById(R.id.welcome1_indicator);
        indicator1.setViewPager(((ActivityWelcome) getActivity()).viewPager);
    }
}