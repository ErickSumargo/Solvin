package id.solvinap.dev.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.solvinap.dev.R;
import id.solvinap.dev.server.api.Connection;
import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataAuth;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.server.helper.Request;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.view.fragment.FragmentAuthComment;
import id.solvinap.dev.view.fragment.FragmentMentorBalanceBonus;
import id.solvinap.dev.view.fragment.FragmentMentorProfile;
import id.solvinap.dev.view.fragment.FragmentMentorRedeemBalance;
import id.solvinap.dev.view.fragment.FragmentMentorSolution;
import id.solvinap.dev.view.helper.Tool;
import id.solvinap.dev.view.widget.CustomAdminPasswordConfirmation;
import id.solvinap.dev.view.widget.CustomAlertDialog;
import id.solvinap.dev.view.widget.CustomCheatSheet;
import id.solvinap.dev.view.widget.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static id.solvinap.dev.R.menu.mentor;

/**
 * Created by Erick Sumargo on 1/31/2017.
 */

public class ActivityMentorDetail extends AppCompatActivity implements IBaseResponse {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    public interface OnReceiveNotificationListener {
        void OnReceiveNotification();
    }

    private static OnReceiveNotificationListener mReceiveNotificationListener;

    public void setOnReceiveNotificationListener(OnReceiveNotificationListener listener) {
        mReceiveNotificationListener = listener;
    }

    private void sendAuthBlockedNotification() {
        if (mReceiveNotificationListener != null) {
            mReceiveNotificationListener.OnReceiveNotification();
        }
    }

    //    VIEW
    private Toolbar toolbar;
    private CoordinatorLayout mainLayout;
    private TabLayout tabLayout;
    private View tabProfile, tabSolution, tabComment, tabRedeemBalance, tabBalanceBonus;
    private TextView tabProfileTitle, tabSolutionTitle, tabCommentTitle, tabRedeemBalanceTitle, tabBalanceBonusTitle;
    private ImageView tabProfileIcon, tabSolutionIcon, tabCommentIcon, tabRedeemBalanceIcon, tabBalanceBonusIcon,
            changePhotoIcon, blockIcon;
    private ProgressBar photoProgress;
    private CircleImageView photo;
    private Snackbar snackbar;

    private AlertDialog.Builder changePhotoBuilder;
    private AlertDialog changePhotoDialog;
    private CustomProgressDialog customProgressDialog;

    //    HELPER
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;

    private RelativeLayout.LayoutParams relativeLayoutParams;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private TypedValue typedValue;

    private Intent intent;
    private Tool tool;
    private Uri uri;
    private Bitmap bitmap;
    private ByteArrayOutputStream byteArrayOutputStream;
    private File temp;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    //    OBJECT
    private FragmentMentorProfile mentorProfile;
    private FragmentMentorSolution mentorSolution;
    private FragmentAuthComment userComment;
    private FragmentMentorRedeemBalance mentorRedeemBalance;
    private FragmentMentorBalanceBonus mentorBalanceBonus;

    private Map<String, RequestBody> metadata;
    public DataAuth dataAuth;

    //    LOCAL VARIABLE
    private final static int REQUEST_EXTERNAL_STORAGE = 0, PICK_IMAGE = 1;
    private int progressBarRadius;

    private static String[] PERMISSION_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_detail);

        init();
        setEvent();
    }

    private void init() {
        //    VIEW
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = (CoordinatorLayout) findViewById(R.id.mentor_detail_main_layout);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(sectionsPagerAdapter.getCount() - 1);
        viewPager.setCurrentItem(0);
        viewPager.setAdapter(sectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setCustomView(R.layout.custom_tab_detail);
        tabLayout.getTabAt(1).setCustomView(R.layout.custom_tab_detail);
        tabLayout.getTabAt(2).setCustomView(R.layout.custom_tab_detail);
        tabLayout.getTabAt(3).setCustomView(R.layout.custom_tab_detail);
        tabLayout.getTabAt(4).setCustomView(R.layout.custom_tab_detail);

        tabProfile = tabLayout.getTabAt(0).getCustomView();
        tabSolution = tabLayout.getTabAt(1).getCustomView();
        tabComment = tabLayout.getTabAt(2).getCustomView();
        tabRedeemBalance = tabLayout.getTabAt(3).getCustomView();
        tabBalanceBonus = tabLayout.getTabAt(4).getCustomView();

        tabProfileTitle = (TextView) tabProfile.findViewById(R.id.tab_title);
        tabProfileTitle.setText(sectionsPagerAdapter.getPageTitle(0));
        tabProfileIcon = (ImageView) tabProfile.findViewById(R.id.tab_icon);
        tabProfileIcon.setImageResource(R.drawable.ic_account_circle_light);

        tabSolutionTitle = (TextView) tabSolution.findViewById(R.id.tab_title);
        tabSolutionTitle.setText(sectionsPagerAdapter.getPageTitle(1));
        tabSolutionIcon = (ImageView) tabSolution.findViewById(R.id.tab_icon);
        tabSolutionIcon.setImageResource(R.drawable.ic_forum_light);

        tabCommentTitle = (TextView) tabComment.findViewById(R.id.tab_title);
        tabCommentTitle.setText(sectionsPagerAdapter.getPageTitle(2));
        tabCommentIcon = (ImageView) tabComment.findViewById(R.id.tab_icon);
        tabCommentIcon.setImageResource(R.drawable.ic_comment_processing_light);

        tabRedeemBalanceTitle = (TextView) tabRedeemBalance.findViewById(R.id.tab_title);
        tabRedeemBalanceTitle.setText(sectionsPagerAdapter.getPageTitle(3));
        tabRedeemBalanceIcon = (ImageView) tabRedeemBalance.findViewById(R.id.tab_icon);
        tabRedeemBalanceIcon.setImageResource(R.drawable.ic_account_balance_wallet_light);

        tabBalanceBonusTitle = (TextView) tabBalanceBonus.findViewById(R.id.tab_title);
        tabBalanceBonusTitle.setText(sectionsPagerAdapter.getPageTitle(4));
        tabBalanceBonusIcon = (ImageView) tabBalanceBonus.findViewById(R.id.tab_icon);
        tabBalanceBonusIcon.setImageResource(R.drawable.ic_account_balance_wallet_light);

        //    HELPER
        sharedPreferences = getSharedPreferences(Global.PREFERENCES_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(Global.PREFERENCES_AUTH_TYPE, "mentor");
        editor.commit();

        tool = Tool.getInstance(getApplicationContext());

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getApplicationContext()).getiAPIRequest();
        iBaseResponse = this;

        //    OBJECT
        mentorProfile = new FragmentMentorProfile();
        mentorSolution = new FragmentMentorSolution();
        userComment = new FragmentAuthComment();
        mentorRedeemBalance = new FragmentMentorRedeemBalance();
        mentorBalanceBonus = new FragmentMentorBalanceBonus();

        dataAuth = (DataAuth) getIntent().getSerializableExtra(Global.PREFERENCES_INTENT_EXTRA);
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tabSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() == 1) {
                    mentorSolution.solutionView.scrollToPosition(0);

                    mentorSolution.resetRecyclerViewData();
                }
                viewPager.setCurrentItem(1);
            }
        });

        tabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() == 2) {
                    userComment.commentView.scrollToPosition(0);

                    userComment.resetRecyclerViewData();
                }
                viewPager.setCurrentItem(2);
            }
        });

        tabRedeemBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() == 3) {
                    mentorRedeemBalance.redeemBalanceView.scrollToPosition(0);

                    mentorRedeemBalance.resetRecyclerViewData();
                }
                viewPager.setCurrentItem(3);
            }
        });

        tabBalanceBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() == 4) {
                    mentorBalanceBonus.balanceBonusView.scrollToPosition(0);

                    mentorBalanceBonus.resetRecyclerViewData();
                }
                viewPager.setCurrentItem(4);
            }
        });
    }

    private void showChangePhotoDialog() {
        changePhotoBuilder = new AlertDialog.Builder(ActivityMentorDetail.this);
        changePhotoBuilder.setPositiveButton("Ganti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        changePhotoBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        changePhotoDialog = changePhotoBuilder.create();
        changePhotoDialog.setView(changePhotoDialog.getLayoutInflater().inflate(R.layout.activity_change_photo, null));
        changePhotoDialog.setCanceledOnTouchOutside(false);
        changePhotoDialog.show();
        changePhotoDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        tool.resizeAlertDialog(changePhotoDialog);

        photoProgress = (ProgressBar) changePhotoDialog.findViewById(R.id.change_photo_progress);
        photo = (CircleImageView) changePhotoDialog.findViewById(R.id.photo);

        progressBarRadius = (int) getResources().getDimension(R.dimen.photo_mentor_radius) + addRadius(18);
        relativeLayoutParams = new RelativeLayout.LayoutParams(progressBarRadius, progressBarRadius);
        relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        photoProgress.setLayoutParams(relativeLayoutParams);

        Picasso.with(getApplicationContext()).load(Global.ASSETS_URL + "mentor" + "/" + dataAuth.getPhoto())
                .placeholder(R.drawable.operator)
                .fit()
                .centerCrop()
                .into(photo);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        changePhotoDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePhotoDialog.dismiss();
            }
        });

        changePhotoDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomAlertDialog customAlertDialog = new CustomAlertDialog(ActivityMentorDetail.this);
                customAlertDialog.setTitle("Konfirmasi");
                customAlertDialog.setMessage("Anda akan mengganti foto mentor terkait?");
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

                        tryChangePhoto();
                    }
                });
            }
        });

        verifyStoragePermission(ActivityMentorDetail.this);
    }

    private int addRadius(int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, getResources().getDisplayMetrics());
    }

    private static void verifyStoragePermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                photo.setEnabled(false);
                photo.setAlpha(0.25f);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            final Toast toast = Toast.makeText(ActivityMentorDetail.this, "Foto Terpilih", Toast.LENGTH_SHORT);
            tool.resizeToast(toast);
            toast.show();

            uri = data.getData();
            bitmap = tool.adjustBitmap(tool.resizeBitmap(uri), uri);

            photoProgress.setProgress(0);
            photoProgress.setVisibility(View.VISIBLE);

            new CompressImage().execute();
        }
    }

    class CompressImage extends AsyncTask<Void, Integer, Void> {
        private int compressQuality, targetCompress, min, progress;

        @Override
        protected void onPreExecute() {
            byteArrayOutputStream = new ByteArrayOutputStream();

            compressQuality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, byteArrayOutputStream);

            targetCompress = Tool.targetCompress;
            min = byteArrayOutputStream.size() / 1024;
            progress = min - targetCompress;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (progress > 0) {
                while (progress > 0) {
                    try {
                        byteArrayOutputStream.flush();
                        byteArrayOutputStream.reset();
                    } catch (IOException e) {
                    }
                    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, byteArrayOutputStream);
                    compressQuality -= 1;
                    if (compressQuality == 0) {
                        break;
                    }

                    progress = byteArrayOutputStream.size() / 1024 - targetCompress;
                    if (progress < 0) {
                        progress = 0;
                    }
                    publishProgress((int) (((float) (min - progress) / (float) min) * 100));
                }
            } else {
                publishProgress(100);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            photoProgress.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            photo.setImageBitmap(bitmap);
            changePhotoDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);

            final Toast toast = Toast.makeText(ActivityMentorDetail.this, "Terunggah", Toast.LENGTH_SHORT);
            tool.resizeToast(toast);
            toast.show();
        }
    }

    private void tryChangePhoto() {
        customProgressDialog = new CustomProgressDialog(ActivityMentorDetail.this);
        customProgressDialog.setMessage("Mengganti foto...");

        if (Connectivity.isConnected(getApplicationContext())) {
            changePhoto();
        } else {
            customProgressDialog.dismiss();
            showNoInternetNotification(new INoInternet() {
                @Override
                public void onRetry() {
                    tryChangePhoto();
                }
            });
        }
    }

    private void changePhoto() {
        temp = tool.getTempFileImage(byteArrayOutputStream);
        metadata = new HashMap<>();
        metadata.put("id", Request.getInstance().getText(String.valueOf(dataAuth.getId())));
        metadata.put("image\"; filename=\"image.jpg\" ", Request.getInstance().getImage(temp));

        iAPIRequest.changePhoto(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                customProgressDialog.dismiss();
                tool.deleteTempFileImage(temp);

                if (response.code() == 200) {
                    iBaseResponse.onSuccess(response.body());
                } else {
                    iBaseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                customProgressDialog.dismiss();
                tool.deleteTempFileImage(temp);

                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    private void blockMentor() {
        customProgressDialog = new CustomProgressDialog(ActivityMentorDetail.this);
        customProgressDialog.setMessage("Memblokir akun pengguna...");

        if (Connectivity.isConnected(getApplicationContext())) {
            iAPIRequest.blockAuth(String.valueOf(dataAuth.getId()), "mentor").enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    customProgressDialog.dismiss();
                    if (response.code() == 200) {
                        finish();
                        sendAuthBlockedNotification();
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {
                    customProgressDialog.dismiss();

                    iBaseResponse.onFailure(t.getMessage());
                }
            });
        } else {
            customProgressDialog.dismiss();
            showNoInternetNotification(new INoInternet() {
                @Override
                public void onRetry() {
                    blockMentor();
                }
            });
        }
    }

    public void showNoInternetNotification(final INoInternet iNoInternet) {
        final Snackbar snackbar = Snackbar.make(mainLayout, getResources().getString(R.string.text_no_internet), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Coba Lagi", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNoInternet.onRetry();
            }
        });
        tool.resizeSnackBarWithCallBack(snackbar);
        snackbar.show();
    }

    private void showNotificationPhotoChanged() {
        snackbar = Snackbar.make(mainLayout, "Foto mentor berhasil diganti", Snackbar.LENGTH_LONG);
        tool.resizeSnackBar(snackbar, 1);
        snackbar.show();
    }

    @Override
    public void onSuccess(Response response) {
        if (changePhotoDialog != null) {
            changePhotoDialog.dismiss();
            showNotificationPhotoChanged();
        }
    }

    @Override
    public void onFailure(String message) {
        final Toast toast = Toast.makeText(ActivityMentorDetail.this, message, Toast.LENGTH_SHORT);
        tool.resizeToast(toast);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(mentor, menu);

        typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarItemBackground, typedValue, true);

        changePhotoIcon = (ImageButton) menu.findItem(R.id.action_change_photo).getActionView();
        changePhotoIcon.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.action_button_default_width), ViewGroup.LayoutParams.MATCH_PARENT));
        changePhotoIcon.setImageResource(R.drawable.ic_camera_light);
        changePhotoIcon.setBackgroundResource(typedValue.resourceId);
        changePhotoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomAdminPasswordConfirmation adminPasswordConfirmation = new CustomAdminPasswordConfirmation(ActivityMentorDetail.this, Global.ACTION_PASSWORD);
                adminPasswordConfirmation.setOnConfirmedListener(new CustomAdminPasswordConfirmation.OnConfirmedListener() {
                    @Override
                    public void OnConfirmed() {
                        showChangePhotoDialog();
                    }
                });
            }
        });
        CustomCheatSheet.setup(changePhotoIcon, getString(R.string.action_change_photo));

        blockIcon = (ImageButton) menu.findItem(R.id.action_block_user).getActionView();
        blockIcon.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.action_button_default_width), ViewGroup.LayoutParams.MATCH_PARENT));
        blockIcon.setImageResource(R.drawable.ic_block_light);
        blockIcon.setBackgroundResource(typedValue.resourceId);
        blockIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomAdminPasswordConfirmation adminPasswordConfirmation = new CustomAdminPasswordConfirmation(ActivityMentorDetail.this, Global.ACTION_PASSWORD);
                adminPasswordConfirmation.setOnConfirmedListener(new CustomAdminPasswordConfirmation.OnConfirmedListener() {
                    @Override
                    public void OnConfirmed() {
                        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(ActivityMentorDetail.this);
                        customAlertDialog.setTitle("Konfirmasi");
                        customAlertDialog.setMessage("Anda akan memblokir akun pengguna terkait?");
                        customAlertDialog.setPositiveButton("Ya", new CustomAlertDialog.OnPositiveClickListener() {
                            @Override
                            public void onClick() {
                                customAlertDialog.dismiss();

                                blockMentor();
                            }
                        });
                        customAlertDialog.setNegativeButton("Tidak", new CustomAlertDialog.OnNegativeClickListener() {
                            @Override
                            public void onClick() {
                                customAlertDialog.dismiss();
                            }
                        });
                    }
                });
            }
        });
        CustomCheatSheet.setup(blockIcon, getString(R.string.action_block_user));

        return true;
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mentorProfile;
                case 1:
                    return mentorSolution;
                case 2:
                    return userComment;
                case 3:
                    return mentorRedeemBalance;
                case 4:
                    return mentorBalanceBonus;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Profil";
                case 1:
                    return "Solusi";
                case 2:
                    return "Komentar";
                case 3:
                    return "Tebusan";
                case 4:
                    return "Bonus";
            }
            return null;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}