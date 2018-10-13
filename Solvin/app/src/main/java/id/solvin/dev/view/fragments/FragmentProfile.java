package id.solvin.dev.view.fragments;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.ProfileBus;
import id.solvin.dev.view.widget.ClassCheatSheet;
import id.solvin.dev.R;
import id.solvin.dev.view.activities.ActivitySettings;
import id.solvin.dev.view.activities.MainActivity;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Erick Sumargo on 9/2/2016.
 */
public class FragmentProfile extends Fragment {
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    private View view;
    public CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private View tabAccount, tabHistory;
    private RelativeLayout avatarLayout;
    private CircleImageView userPhoto;
    private TextView avatarInitial, nameLabel, joinedTimeLabel;
    private TextView tabAccountTitle, tabHistoryTitle;
    private ImageView tabAccountIcon, tabHistoryIcon;
    private ImageButton settingsIcon;

    private FragmentAccount fragmentAccount;
    private FragmentHistoryQuestion fragmentHistoryQuestion;

    private AppBarLayout.LayoutParams layoutParams;
    private Intent intent;
    private TypedValue typedValue;
    private GradientDrawable gradientDrawable;

    private int userPhotoMode, avatarColor, joinedTime = 3;
    private Session session;
    private Auth auth;
    private Bundle data;

    public FragmentProfile() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_profile, container, false);

        init();
        setEvent();
        if (((MainActivity) getActivity()).isMyProfile) {
            setHasOptionsMenu(true);
        }

        return view;
    }

    private void init() {
        session = Session.with(getContext());
        data = new Bundle();
        if (getArguments() != null && getArguments().getSerializable("user") != null) {
            auth = (Auth) getArguments().getSerializable("user");
            data.putBoolean("is.my.profile", false);
            ((MainActivity) getActivity()).actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            ((MainActivity) getActivity()).isMyProfile = false;
        } else {
            auth = session.getAuth();
            data.putBoolean("is.my.profile", true);
            ((MainActivity) getActivity()).isMyProfile = true;
        }
        data.putSerializable("user", auth);

        sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        viewPager = (ViewPager) view.findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        avatarLayout = (RelativeLayout) view.findViewById(R.id.profile_avatar_layout);
        userPhoto = (CircleImageView) view.findViewById(R.id.profile_user_photo);
        nameLabel = (TextView) view.findViewById(R.id.profile_name_label);
        avatarInitial = (TextView) view.findViewById(R.id.profile_avatar_initial);
        joinedTimeLabel = (TextView) view.findViewById(R.id.profile_joined_time_label);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setCustomView(R.layout.custom_tab);
        tabLayout.getTabAt(1).setCustomView(R.layout.custom_tab);

        tabAccount = tabLayout.getTabAt(0).getCustomView();
        tabHistory = tabLayout.getTabAt(1).getCustomView();

        tabAccountTitle = (TextView) tabAccount.findViewById(R.id.tab_title);
        tabAccountIcon = (ImageView) tabAccount.findViewById(R.id.tab_icon);
        tabAccountTitle.setText(sectionsPagerAdapter.getPageTitle(0));
        tabAccountIcon.setImageResource(R.drawable.ic_account_circle_light);

        tabHistoryTitle = (TextView) tabHistory.findViewById(R.id.tab_title);
        tabHistoryIcon = (ImageView) tabHistory.findViewById(R.id.tab_icon);
        tabHistoryTitle.setText(sectionsPagerAdapter.getPageTitle(1));
        tabHistoryIcon.setImageResource(R.drawable.ic_history_light);

        fragmentAccount = new FragmentAccount();
        fragmentHistoryQuestion = new FragmentHistoryQuestion();
        fragmentAccount.setArguments(data);
        fragmentHistoryQuestion.setArguments(data);
        layoutParams = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();

        refreshUser();
    }

    private void setEvent() {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        layoutParams.setScrollFlags(0);
                        collapsingToolbarLayout.setLayoutParams(layoutParams);
                        break;
                    case 1:
                        if (fragmentHistoryQuestion.historyQuestionList.size() == 0) {
                            layoutParams.setScrollFlags(0);
                            collapsingToolbarLayout.setLayoutParams(layoutParams);

                        } else {
                            layoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                                    AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED |
                                    AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
                            collapsingToolbarLayout.setLayoutParams(layoutParams);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        tabAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabLayout.getTabAt(0).select();
            }
        });

        tabHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 1) {
                    fragmentHistoryQuestion.historyQuestionView.scrollToPosition(0);
                    fragmentHistoryQuestion.resetRecyclerViewData();
                }
                tabLayout.getTabAt(1).select();
            }
        });
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return fragmentAccount;
                case 1:
                    return fragmentHistoryQuestion;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "INFO";
                case 1:
                    if (auth.getAuth_type().equals("student")) {
                        return "PERTANYAAN";
                    } else {
                        return "SOLUSI";
                    }
            }
            return null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings, menu);
        typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.actionBarItemBackground, typedValue, true);

        settingsIcon = (ImageButton) menu.findItem(R.id.action_settings).getActionView();
        settingsIcon.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.action_button_default_width), ViewGroup.LayoutParams.MATCH_PARENT));
        settingsIcon.setImageResource(R.drawable.ic_settings_light);
        settingsIcon.setBackgroundResource(typedValue.resourceId);

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), ActivitySettings.class);
                startActivity(intent);
            }
        });
        ClassCheatSheet.setup(settingsIcon, getString(R.string.action_settings));

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void refreshUser() {
        nameLabel.setText(auth.getName());
        avatarInitial.setText(Global.get().getInitialName(auth.getName()));
        avatarColor = ClassApplicationTool.with(getContext()).getAvatarColor(auth.getId());
        if (auth.getPhoto().isEmpty()) {
            gradientDrawable = (GradientDrawable) avatarLayout.getBackground();
            gradientDrawable.setColor(avatarColor);

            avatarLayout.setVisibility(View.VISIBLE);
            userPhoto.setVisibility(View.GONE);
        } else {
            avatarLayout.setVisibility(View.GONE);
            userPhoto.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(Global.get().getAssetURL(auth.getPhoto(), auth.getAuth_type()))
                    .placeholder(R.drawable.operator)
                    .fit()
                    .centerCrop()
                    .into(userPhoto);
        }
        joinedTimeLabel.setText(String.format("Bergabung sejak " + auth.getJoin_time()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragmentAccount.onActivityResult(requestCode, resultCode, data);
        fragmentHistoryQuestion.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onRefreshSession(ProfileBus profileBus) {
        auth = session.getAuth();
        refreshUser();
    }
}