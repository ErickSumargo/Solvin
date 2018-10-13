package id.solvinap.dev.view.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import id.solvinap.dev.R;
import id.solvinap.dev.server.data.DataQuestion;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.view.fragment.FragmentCategory;
import id.solvinap.dev.view.fragment.FragmentQuestionEditingSheet;
import id.solvinap.dev.view.widget.CustomAlertDialog;
import id.solvinap.dev.view.widget.CustomViewPager;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 2/2/2017.
 */

public class ActivityEditQuestion extends AppCompatActivity {
    //    VIEW
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private LinearLayout tabStrip;
    private ViewGroup tabChild;
    private View tabView;

    //    HELPER
    private SectionsPagerAdapter sectionsPagerAdapter;
    private CustomViewPager customViewPager;

    private Typeface customFont;
    private InputMethodManager inputMethodManager;

    //    OBJECT
    private FragmentQuestionEditingSheet questionSheet;
    private FragmentCategory category;

    public DataQuestion dataQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        init();
        setEvent();
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return category;
                case 1:
                    return questionSheet;
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
                    return "KATEGORI";
                case 1:
                    return "PERTANYAAN";
            }
            return null;
        }
    }

    public void init() {
        //    VIEW
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        customViewPager = (CustomViewPager) findViewById(R.id.container);
        customViewPager.setAdapter(sectionsPagerAdapter);
        customViewPager.setPagingEnabled(false);

        category = new FragmentCategory();
        questionSheet = new FragmentQuestionEditingSheet();
        customFont = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.otf");
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(customViewPager);
        tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        tabStrip.setBackgroundColor(ContextCompat.getColor(getApplication(), R.color.lightGray));

        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(false);
            tabChild = (ViewGroup) tabStrip.getChildAt(i);

            for (int j = 0; j < tabChild.getChildCount(); j++) {
                tabView = tabChild.getChildAt(j);
                if (tabView instanceof TextView) {
                    ((TextView) tabView).setTypeface(customFont);
                }
            }
        }

        //    OBJECT
        dataQuestion = (DataQuestion) getIntent().getSerializableExtra(Global.PREFERENCES_INTENT_EXTRA);
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                if (customViewPager.getCurrentItem() == 0) {
                    showConfirmation();
                } else {
                    tabLayout.getTabAt(0).select();
                }
            }
        });
    }

    private void showConfirmation() {
        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(ActivityEditQuestion.this);
        customAlertDialog.setTitle("Konfirmasi");
        customAlertDialog.setMessage("Anda akan membatalkan kegiatan mengedit pertanyaan?");
        customAlertDialog.setPositiveButton("Ya", new CustomAlertDialog.OnPositiveClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();
                onBackPressed();
            }
        });
        customAlertDialog.setNegativeButton("Tidak", new CustomAlertDialog.OnNegativeClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (customViewPager.getCurrentItem() == 0) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                showConfirmation();

                return true;
            }
        } else if (customViewPager.getCurrentItem() == 1) {
            if (keyCode == KeyEvent.KEYCODE_BACK && questionSheet.solvinKeyboard.showSolvinKeyboard) {
                questionSheet.adjustHiddenKeyboard();
                questionSheet.textFormat.hide();
                questionSheet.textFormatShown = false;

                return true;
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                tabLayout.getTabAt(0).select();
                questionSheet.textFormat.hide();
                questionSheet.textFormatShown = false;

                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}