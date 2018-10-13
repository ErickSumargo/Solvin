package id.solvin.dev.view.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.ConfigApp;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.FeedNotification;
import id.solvin.dev.model.basic.NotificationBus;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.response.ResponseNotification;
import id.solvin.dev.model.response.ResponseSearch;
import id.solvin.dev.presenter.AuthPresenter;
import id.solvin.dev.presenter.NotificationPresenter;
import id.solvin.dev.view.activities.ActivityNotification;
import id.solvin.dev.view.adapters.SearchSuggestionViewAdapter;
import id.solvin.dev.view.widget.ClassCheatSheet;
import id.solvin.dev.R;
import id.solvin.dev.view.activities.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Erick Sumargo on 8/31/2016.
 */
public class FragmentHome extends Fragment implements IBaseResponse {
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    private View view, tabFeed, tabFilter, notificationMenuView;
    private LinearLayout badgeCounterContainer;
    private TabLayout tabLayout;
    private TextView tabFeedTitle, tabFilterTitle, feedNotification, badgeCounter;
    private ImageView tabFeedIcon, tabFilterIcon, searchIcon, closeIcon;
    private ImageButton notificationIcon;
    private Toast toast;

    private AppBarLayout.LayoutParams layoutParams;
    private ClassApplicationTool applicationTool;

    public MenuItem searchMenuItem, notificationMenuItem;
    private SearchManager searchManager;
    public SearchView searchView;
    private AutoCompleteTextView searchTextView;
    private Field mCursorDrawableRes;

    private List<Auth> searchSuggestionList;
    private SearchSuggestionViewAdapter searchSuggestionViewAdapter;
    private MatrixCursor cursor;

    private FragmentFeed fragmentFeed;
    public FragmentFilter fragmentFilter;
    private Intent intent;
    private Menu menu;

    private Timer timer;
    private int tempTotalFeed, diffFeed = 0;
    private final long DELAY = 300;
    private String queryText = "";
    private String[] columns = new String[]{"_id", "name", "photo", "auth_type", "question_count", "best_count", "solution_count"};

    //    VARIABLE
    private boolean searching = false;
    private Auth auth;
    private AuthPresenter authPresenter;
    private NotificationPresenter notificationPresenter;

    public FragmentHome() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home, container, false);
        setHasOptionsMenu(true);
        init();
        setEvent();

        return view;
    }

    private void init() {
        authPresenter = new AuthPresenter(this);
        notificationPresenter = new NotificationPresenter(this);

        sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        viewPager = (ViewPager) view.findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setCustomView(R.layout.custom_tab);
        tabLayout.getTabAt(1).setCustomView(R.layout.custom_tab);

        tabFeed = tabLayout.getTabAt(0).getCustomView();
        tabFilter = tabLayout.getTabAt(1).getCustomView();

        tabFeedTitle = (TextView) tabFeed.findViewById(R.id.tab_title);
        tabFeedIcon = (ImageView) tabFeed.findViewById(R.id.tab_icon);
        tabFeedTitle.setText(sectionsPagerAdapter.getPageTitle(0));
        tabFeedIcon.setImageResource(R.drawable.ic_forum_light);

        tabFilterTitle = (TextView) tabFilter.findViewById(R.id.tab_title);
        tabFilterIcon = (ImageView) tabFilter.findViewById(R.id.tab_icon);
        tabFilterTitle.setText(sectionsPagerAdapter.getPageTitle(1));
        tabFilterIcon.setImageResource(R.drawable.ic_filter_list_light);

        feedNotification = (TextView) tabFeed.findViewById(R.id.feed_notification);

        layoutParams = (AppBarLayout.LayoutParams) tabLayout.getLayoutParams();
        applicationTool = new ClassApplicationTool(getContext());

        timer = new Timer();

        auth = Session.with(getContext()).getAuth();

        fragmentFeed = new FragmentFeed();
        fragmentFilter = new FragmentFilter();
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
                        if (fragmentFeed.feedList.size() == 0) {
                            disableScrollFlagsEffect();
                        } else {
                            enableScrollFlagsEffect();
                        }
                        if (Session.with(getContext()).getLoginType() == ConfigApp.get().STUDENT) {
//                            if (((MainActivity) getActivity()).student.getCredit() > 0
//                                    && !((MainActivity) getActivity()).student.isCreditExpired()) {
//                                ((MainActivity) getActivity()).createQuestion.show();
//                            } else {
//                                ((MainActivity) getActivity()).createQuestion.hide();
//                            }
                            if (((MainActivity) getActivity()).student.getCredit() > 0) {
                                ((MainActivity) getActivity()).createQuestion.show();
                            } else {
                                ((MainActivity) getActivity()).createQuestion.hide();
                            }
                        }
                        break;
                    case 1:
                        disableScrollFlagsEffect();
                        ((MainActivity) getActivity()).createQuestion.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        tabFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 0) {
                    fragmentFeed.feedView.scrollToPosition(0);
                    fragmentFeed.resetRecyclerViewData();

                    clearFeedNotification();
                }
                viewPager.setCurrentItem(0);
            }
        });

        tabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabLayout.getTabAt(1).select();
            }
        });

        fragmentFeed.setOnClearFeedNotificationListener(new FragmentFeed.OnClearFeedNotificationListener() {
            @Override
            public void OnClearFeedNotification() {
                clearFeedNotification();
            }
        });
    }

    private void showFeedNotification(int totalFeed) {
        if (totalFeed - tempTotalFeed > 0) {
            diffFeed += totalFeed - tempTotalFeed;
            feedNotification.setText("+" + diffFeed);
            feedNotification.setVisibility(View.VISIBLE);

            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.feed_notification_up);
            feedNotification.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    disableScrollFlagsEffect();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    enableScrollFlagsEffect();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            tempTotalFeed = totalFeed;
        }
    }

    private void hideFeedNotification() {
        final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.feed_notification_down);
        feedNotification.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                feedNotification.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void clearFeedNotification() {
        diffFeed = 0;
        hideFeedNotification();
    }

    private void enableScrollFlagsEffect() {
        layoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS |
                AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        tabLayout.setLayoutParams(layoutParams);
    }

    private void disableScrollFlagsEffect() {
        layoutParams.setScrollFlags(0);
        tabLayout.setLayoutParams(layoutParams);
    }

    @Override
    public void onSuccess(Response response) {
        switch (response.getTag()) {
            case Response.TAG_SEARCH_DATA:
                searchSuggestionList = ((ResponseSearch) response).getData();
                if (!queryText.isEmpty()) {
                    updateSearchSuggestionList(searchSuggestionList);
                }

                searching = false;
                break;
            case Response.TAG_COUNT_NOTIFICATION:
                Session.with(getContext()).saveCountNotification(((ResponseNotification) response).getData().getCount());
                refreshBadgeCount();
                break;
        }
    }

    @Override
    public void onFailure(String message) {
        if (this.isVisible()) {
            toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
            toast.show();

            if (searching) {
                if (searchSuggestionList != null) {
                    searchSuggestionList.clear();
                }
                searching = false;
            }
        }
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return fragmentFeed;
                case 1:
                    return fragmentFilter;
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
                    return "FEED";
                case 1:
                    return "FILTER";
            }
            return null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        this.menu = menu;

        /*----------------------------------------Search Menu----------------------------------------*/

        searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint("Cari anggota...");
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.default_font_size_18m));
        searchIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        searchIcon.setImageResource(R.drawable.ic_search_light);
        closeIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeIcon.setImageResource(R.drawable.ic_close_light);
        if (getActivity().getResources().getBoolean(R.bool.isTablet)) {
            searchMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            ClassCheatSheet.setup(searchIcon, getString(R.string.action_search));
        }

        try {
            mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.custom_cursor);
        } catch (Exception e) {
        }

        searchSuggestionList = new ArrayList<>();
        searchSuggestionViewAdapter = new SearchSuggestionViewAdapter(getContext(), getCursorData());
        searchView.setSuggestionsAdapter(searchSuggestionViewAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMenuItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                queryText = newText;
                timer.cancel();
                if (!newText.isEmpty()) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (newText.length() > 0) {
                                if (!searching) {
                                    searching = true;
                                    authPresenter.doSearch(newText, getContext());
                                }
                            }
                        }
                    }, DELAY);
                } else {
                    searchSuggestionList.clear();
                    updateSearchSuggestionList(searchSuggestionList);
                }
                return true;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Auth _auth = searchSuggestionList.get(position);
                searchMenuItem.collapseActionView();
                if (_auth.getId() == auth.getId()
                        && _auth.getAuth_type().equals(auth.getAuth_type())) {
                    ((MainActivity) getActivity()).setFragmentPosition(1);
                } else {
                    Bundle data = new Bundle();
                    data.putSerializable("user", _auth);
                    ((MainActivity) getActivity()).setFragmentWithData(1, data);
                }
                return false;
            }
        });


        /*-------------------------------------------------------------------------------------------*/

        /*----------------------------------------ActivityNotification Menu----------------------------------------*/

        notificationMenuItem = menu.findItem(R.id.action_notification);
        notificationMenuView = MenuItemCompat.getActionView(notificationMenuItem);
        notificationIcon = (ImageButton) notificationMenuView.findViewById(R.id.notification_icon);
        badgeCounterContainer = (LinearLayout) notificationMenuView.findViewById(R.id.badge_counter_container);
        badgeCounterContainer.setVisibility(View.GONE);
        badgeCounter = (TextView) notificationMenuView.findViewById(R.id.badge_counter);
        refreshBadgeCount();
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO when notif avaible
                intent = new Intent(getActivity(), ActivityNotification.class);
                startActivity(intent);
            }
        });
        ClassCheatSheet.setup(notificationIcon, getString(R.string.action_notification));

        /*-------------------------------------------------------------------------------------------*/

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateSearchSuggestionList(List<Auth> authList) {
        searchSuggestionViewAdapter.updateSearchSuggestionList(authList);
        searchSuggestionViewAdapter.swapCursor(getCursorData());
        searchSuggestionViewAdapter.notifyDataSetChanged();
    }

    private MatrixCursor getCursorData() {
        cursor = new MatrixCursor(columns);
        for (int i = 0; i < searchSuggestionList.size(); i++) {
            Auth dataSearchSuggestion = searchSuggestionList.get(i);
            cursor.addRow(new Object[]{i, dataSearchSuggestion.getName(), dataSearchSuggestion.getPhoto(),
                    dataSearchSuggestion.getAuth_type(), dataSearchSuggestion.getQuestion_count(),
                    dataSearchSuggestion.getBest_count(), dataSearchSuggestion.getSolution_count()});
        }
        return cursor;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(FeedNotification feedNotification) {
        if (feedNotification.isFirst()) {
            if (feedNotification.getTotalFeed() != -1) {
                tempTotalFeed = feedNotification.getTotalFeed();
            } else {
                tempTotalFeed = -1;
            }
        } else {
            if (feedNotification.getTotalFeed() != -1) {
                showFeedNotification(feedNotification.getTotalFeed());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshBagdeCount(NotificationBus notificationBus) {
        notificationPresenter.getCount(getContext());
    }


    public void refreshBadgeCount() {
        if (badgeCounter != null) {
            int count_notification = Session.with(getContext()).getCountNotification();
            badgeCounterContainer.setVisibility(count_notification > 0 ? View.VISIBLE : View.GONE);
            badgeCounter.setText(String.valueOf(count_notification));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        notificationPresenter.getCount(getContext());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragmentFeed.onActivityResult(requestCode, resultCode, data);
    }
}