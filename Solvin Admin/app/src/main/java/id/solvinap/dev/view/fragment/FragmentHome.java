package id.solvinap.dev.view.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
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
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import id.solvinap.dev.R;
import id.solvinap.dev.server.api.Connection;
import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataAuth;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.server.helper.Session;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelAuth;
import id.solvinap.dev.server.model.ModelNotification;
import id.solvinap.dev.view.activity.ActivityMentorDetail;
import id.solvinap.dev.view.activity.ActivityNotification;
import id.solvinap.dev.view.activity.ActivityStudentDetail;
import id.solvinap.dev.view.activity.MainActivity;
import id.solvinap.dev.view.adapter.SearchSuggestionViewAdapter;
import id.solvinap.dev.view.helper.NotificationBus;
import id.solvinap.dev.view.helper.Tool;
import id.solvinap.dev.view.widget.CustomCheatSheet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Erick Sumargo on 1/31/2017.
 */

public class FragmentHome extends Fragment implements IBaseResponse {
    //    VIEW
    private View view;
    private TabLayout tabLayout;
    private View tabStudentFeed, tabMentorFeed;
    private TextView tabStudentFeedTitle, tabMentorFeedTitle;

    public MenuItem searchMenuItem;
    private SearchManager searchManager;
    public SearchView searchView;
    private AutoCompleteTextView searchTextView;
    private ImageView searchIcon, closeIcon;
    private Field mCursorDrawableRes;

    public MenuItem notificationMenuItem;
    private View notificationMenuView;
    public LinearLayout badgeCounterContainer;
    public TextView badgeCounter;
    private ImageButton notificationIcon;

    //    HELPER
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private Intent intent;

    private Timer timer;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    OBJECT
    private MatrixCursor cursor;

    private FragmentStudentFeed studentFeed;
    private FragmentMentorFeed mentorFeed;

    private ModelAuth auth;
    private DataAuth dataAuth;
    private List<DataAuth> searchSuggestionList;
    private SearchSuggestionViewAdapter searchSuggestionViewAdapter;

    //    VARIABLE
    private int notificationCount;
    private final long DELAY = 300;
    private boolean searching = false;
    private String queryText = "";

    private final static String[] columns = new String[]
            {"_id", "name", "photo", "auth_type", "total_question", "total_best_solution", "total_solution"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home, container, false);
        setHasOptionsMenu(true);

        init();
        setEvent();

        return view;
    }

    private void init() {
        //    VIEW
        sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        viewPager = (ViewPager) view.findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setCustomView(R.layout.custom_tab_home);
        tabLayout.getTabAt(1).setCustomView(R.layout.custom_tab_home);

        tabStudentFeed = tabLayout.getTabAt(0).getCustomView();
        tabMentorFeed = tabLayout.getTabAt(1).getCustomView();

        tabStudentFeedTitle = (TextView) tabStudentFeed.findViewById(R.id.tab_title);
        tabStudentFeedTitle.setText(sectionsPagerAdapter.getPageTitle(0));

        tabMentorFeedTitle = (TextView) tabMentorFeed.findViewById(R.id.tab_title);
        tabMentorFeedTitle.setText(sectionsPagerAdapter.getPageTitle(1));

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getContext()).getiAPIRequest();
        iBaseResponse = this;

        //    HELPER
        timer = new Timer();

        //    OBJECT
        studentFeed = new FragmentStudentFeed();
        mentorFeed = new FragmentMentorFeed();
    }

    private void setEvent() {
        tabStudentFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() == 0) {
                    ((MainActivity) getActivity()).getFinanceRecord();

                    studentFeed.studentFeedView.scrollToPosition(0);
                    studentFeed.resetRecyclerViewData();
                }
                viewPager.setCurrentItem(0);
            }
        });

        tabMentorFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() == 1) {
                    ((MainActivity) getActivity()).getFinanceRecord();

                    mentorFeed.mentorFeedView.scrollToPosition(0);
                    mentorFeed.resetRecyclerViewData();
                }
                viewPager.setCurrentItem(1);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);

        /*-----------------------------------------Search-----------------------------------------*/

        searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint(getString(R.string.action_search));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_18rs));
        searchIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        searchIcon.setImageResource(R.drawable.ic_search_light);
        closeIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeIcon.setImageResource(R.drawable.ic_close_light);
        if (getActivity().getResources().getBoolean(R.bool.isTablet)) {
            searchMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            CustomCheatSheet.setup(searchIcon, getString(R.string.action_search));
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
                                    getSearchSuggestion(newText);
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
                if (searchSuggestionList.get(position).getAuthType().equals("student")) {
                    intent = new Intent(getContext(), ActivityStudentDetail.class);
                } else {
                    intent = new Intent(getContext(), ActivityMentorDetail.class);
                }
                intent.putExtra(Global.PREFERENCES_INTENT_EXTRA, searchSuggestionList.get(position));
                startActivity(intent);

                searchMenuItem.collapseActionView();

                return true;
            }
        });

        /*----------------------------------------------------------------------------------------*/

        /*-------------------------------------Notification---------------------------------------*/

        notificationMenuItem = menu.findItem(R.id.action_notification);
        notificationMenuView = MenuItemCompat.getActionView(notificationMenuItem);
        notificationIcon = (ImageButton) notificationMenuView.findViewById(R.id.notification_icon);
        badgeCounterContainer = (LinearLayout) notificationMenuView.findViewById(R.id.badge_counter_container);
        badgeCounterContainer.setVisibility(View.GONE);
        badgeCounter = (TextView) notificationMenuView.findViewById(R.id.badge_counter);
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchView.isIconified()) {
                    searchMenuItem.collapseActionView();
                }
                intent = new Intent(getActivity(), ActivityNotification.class);
                startActivity(intent);
            }
        });
        CustomCheatSheet.setup(notificationIcon, getString(R.string.action_notification));

        /*----------------------------------------------------------------------------------------*/

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateSearchSuggestionList(List<DataAuth> authList) {
        searchSuggestionViewAdapter.updateSearchSuggestionList(authList);
        searchSuggestionViewAdapter.swapCursor(getCursorData());
        searchSuggestionViewAdapter.notifyDataSetChanged();
    }

    private MatrixCursor getCursorData() {
        cursor = new MatrixCursor(columns);
        for (int i = 0; i < searchSuggestionList.size(); i++) {
            dataAuth = searchSuggestionList.get(i);
            cursor.addRow(new Object[]{i, dataAuth.getName(), dataAuth.getPhoto(), dataAuth.getAuthType(),
                    dataAuth.getAuthTotalQuestion(), dataAuth.getAuthTotalBestSolution(), dataAuth.getAuthTotalSolution()});
        }
        return cursor;
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return studentFeed;
                case 1:
                    return mentorFeed;
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
                    return "Murid";
                case 1:
                    return "Mentor";
            }
            return null;
        }
    }

    private void refreshBadgeCount() {
        if (badgeCounter != null) {
            notificationCount = Session.getInstance(getContext()).getNotificationCount();

            badgeCounterContainer.setVisibility(notificationCount > 0 ? View.VISIBLE : View.GONE);
            badgeCounter.setText(String.valueOf(notificationCount));
        }
    }

    private void getSearchSuggestion(String query) {
        iAPIRequest.search(query).enqueue(new Callback<ModelAuth>() {
            @Override
            public void onResponse(Call<ModelAuth> call, retrofit2.Response<ModelAuth> response) {
                if (response.code() == 200) {
                    iBaseResponse.onSuccess(response.body());
                } else {
                    iBaseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ModelAuth> call, Throwable t) {
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    public void getNotificationCount() {
        if (Connectivity.isConnected(getContext())) {
            iAPIRequest.getNotificationCount().enqueue(new Callback<ModelNotification>() {
                @Override
                public void onResponse(Call<ModelNotification> call, retrofit2.Response<ModelNotification> response) {
                    if (response.code() == 200) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<ModelNotification> call, Throwable t) {
                    iBaseResponse.onFailure(t.getMessage());
                    if (t instanceof SocketTimeoutException) {
                        getNotificationCount();
                    }
                }
            });
        } else {
            ((MainActivity) getActivity()).showNoInternetNotification(new MainActivity.INoInternet() {
                @Override
                public void onRetry() {
                    getNotificationCount();
                }
            });
        }
    }

    @Override
    public void onSuccess(Response response) {
        if (response instanceof ModelAuth) {
            auth = (ModelAuth) response;
            searchSuggestionList = auth.getAuthList();
            if (!queryText.isEmpty()) {
                updateSearchSuggestionList(searchSuggestionList);
            }
            searching = false;
        } else if (response instanceof ModelNotification) {
            Session.getInstance(getContext()).saveNotificationCount(((ModelNotification) response).getData().getCount());
            refreshBadgeCount();
        }
    }

    @Override
    public void onFailure(String message) {
        final Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        Tool.getInstance(getContext()).resizeToast(toast);
        toast.show();

        if (searchSuggestionList != null) {
            searchSuggestionList.clear();
        }
        searching = false;
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

    @Override
    public void onResume() {
        super.onResume();
        getNotificationCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshBadgeCount(NotificationBus notificationBus) {
        getNotificationCount();
    }
}