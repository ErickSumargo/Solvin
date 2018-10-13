package id.solvin.dev.view.activities;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Global;
import id.solvin.dev.model.basic.Question;
import id.solvin.dev.view.fragments.FragmentCategory;
import id.solvin.dev.view.fragments.FragmentQuestionSheet;
import id.solvin.dev.view.widget.ClassCustomViewPager;
import id.solvin.dev.view.widget.CustomAlertDialog;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 8/31/2016.
 */
public class ActivityCreateQuestion extends AppCompatActivity {
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ClassCustomViewPager customViewPager;

    private Toolbar toolbar;
    private CustomAlertDialog customAlertDialog;

    private TabLayout tabLayout;
    private LinearLayout tabStrip;
    private ViewGroup tabChild;
    private View tabView;

    public ClassApplicationTool applicationTool;
    private FragmentQuestionSheet questionSheet;
    private FragmentCategory category;
    private Typeface customFont;
    private InputMethodManager inputMethodManager;

    public int QUESTION_MODE;
    public CharSequence QUESTION_CONTENT;

    public Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(id.solvin.dev.R.layout.activity_create_question);

        toolbar = (Toolbar) findViewById(id.solvin.dev.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                if (customViewPager.getCurrentItem() == 0) {
                    if (QUESTION_MODE == 0) {
                        if (questionSheet.sheet.getText().length() != 0 || questionSheet.bitmap != null) {
                            showConfirmation();
                        } else {
                            onBackPressed();
                        }
                    } else {
                        showConfirmation();
                    }
                } else {
                    tabLayout.getTabAt(0).select();
                }
            }
        });

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        customViewPager = (ClassCustomViewPager) findViewById(id.solvin.dev.R.id.container);
        customViewPager.setAdapter(sectionsPagerAdapter);
        customViewPager.setPagingEnabled(false);

        init();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

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
        questionSheet = new FragmentQuestionSheet();
        category = new FragmentCategory();
        customFont = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.otf");
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        tabLayout = (TabLayout) findViewById(id.solvin.dev.R.id.tabs);
        tabLayout.setupWithViewPager(customViewPager);
        tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        tabStrip.setBackgroundColor(ContextCompat.getColor(getApplication(), id.solvin.dev.R.color.lightGray));

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

        applicationTool = new ClassApplicationTool(getApplicationContext());

        QUESTION_MODE = getIntent().getExtras().getInt("QUESTION_MODE");
        if (QUESTION_MODE == 1) {
            setTitle("Edit Pertanyaan");
            question = (Question) getIntent().getSerializableExtra(Global.get().key().QUESTION_DATA);

            QUESTION_CONTENT = getIntent().getExtras().getCharSequence("QUESTION_CONTENT");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(id.solvin.dev.R.menu.empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (customViewPager.getCurrentItem() == 0) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (QUESTION_MODE == 0) {
                    if (questionSheet.sheet.getText().length() != 0 || questionSheet.bitmap != null) {
                        showConfirmation();
                    } else {
                        onBackPressed();
                    }
                } else {
                    showConfirmation();
                }
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

    public void showConfirmation() {
        customAlertDialog = new CustomAlertDialog(ActivityCreateQuestion.this);
        customAlertDialog.setTitle("Konfirmasi");
        if (QUESTION_MODE == 0) {
            customAlertDialog.setMessage("Anda akan membatalkan kegiatan mengajukan pertanyaan?");
        } else {
            customAlertDialog.setMessage("Anda akan membatalkan kegiatan mengedit pertanyaan?");
        }
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
                onBackPressed();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}