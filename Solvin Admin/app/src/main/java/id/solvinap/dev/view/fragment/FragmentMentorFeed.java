package id.solvinap.dev.view.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
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
import id.solvinap.dev.server.data.DataMentor;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.server.helper.Request;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.server.model.ModelMentorList;
import id.solvinap.dev.server.model.ModelRegisterMentor;
import id.solvinap.dev.view.activity.ActivityImageFullScreen;
import id.solvinap.dev.view.activity.MainActivity;
import id.solvinap.dev.view.adapter.MentorFeedViewAdapter;
import id.solvinap.dev.view.helper.Tool;
import id.solvinap.dev.view.widget.CustomAlertDialog;
import id.solvinap.dev.view.widget.CustomProgressDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Erick Sumargo on 1/31/2017.
 */

public class FragmentMentorFeed extends Fragment implements IBaseResponse {
    //    VIEW
    private View view;
    private CoordinatorLayout mainLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView mentorFeedView;
    private FloatingActionButton registerMentor;
    private Toast toast;

    private View focus;
    private RelativeLayout uploadSpace;
    private LinearLayout photoErrorMessageContainer;
    private TextInputLayout photoLayout, emailLayout, nameLayout, mobileLayout;
    private AutoCompleteTextView email, name, mobile;
    private ProgressBar photoProgress;
    private CircleImageView photo;
    private ImageButton photoRemove;

    private CustomProgressDialog customProgressDialog;
    private Snackbar snackbar;

    private View nullView;
    private ImageView nullViewImage;
    private TextView nullViewTitle, nullViewDesc;

    //    HELPER
    private LinearLayoutManager layoutManager;
    private RelativeLayout.LayoutParams relativeLayoutParams;

    private ClipboardManager clipboardManager;
    private ClipData clipData;

    private Intent intent;

    private Tool tool;
    private Uri uri;
    private Bitmap bitmap;
    private ByteArrayOutputStream byteArrayOutputStream;
    private File temp;

    //    CONNECTION
    private IAPIRequest iAPIRequest;
    private IBaseResponse iBaseResponse;

    private Map<String, RequestBody> metadata;

    private ModelMentorList mentor;
    private List<DataMentor> mentorList;
    private MentorFeedViewAdapter mentorFeedViewAdapter;

    //    VARIABLE
    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private final int PICK_IMAGE = 0;
    private int progressBarRadius;
    public int lastId = 0;

    private String emailText, nameText, mobileText;
    private static String[] PERMISSION_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean emailValid, nameValid, mobileValid, photoAttached;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_mentor_feed, container, false);

        init();
        setEvent();
        fetchPageData();

        return view;
    }

    private void init() {
        //    VIEW
        if (Build.VERSION.SDK_INT >= 21) {
            view.findViewById(R.id.shadow_view).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.shadow_view).setVisibility(View.VISIBLE);
        }

        mainLayout = (CoordinatorLayout) view.findViewById(R.id.mentor_feed_main_layout);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.mentor_feed_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        if (getActivity().getResources().getBoolean(R.bool.isTablet)) {
            swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        }

        registerMentor = (FloatingActionButton) view.findViewById(R.id.register_mentor);

        nullView = view.findViewById(R.id.null_view);
        nullViewImage = (ImageView) nullView.findViewById(R.id.null_view_image);
        nullViewTitle = (TextView) nullView.findViewById(R.id.null_view_title);
        nullViewDesc = (TextView) nullView.findViewById(R.id.null_view_desc);

        nullViewImage.setImageResource(R.drawable.ic_item_null);
        nullViewTitle.setText(getResources().getString(R.string.text_no_item));
        nullViewDesc.setText(getResources().getString(R.string.text_no_mentor));

        mentorFeedView = (RecyclerView) view.findViewById(R.id.mentor_feed_view);
        layoutManager = new LinearLayoutManager(getActivity());
        mentorFeedView.setLayoutManager(layoutManager);

        mentorList = new ArrayList<>();
        mentorFeedViewAdapter = new MentorFeedViewAdapter(mentorFeedView, mentorList);
        mentorFeedView.setAdapter(mentorFeedViewAdapter);

        //    HELPER
        tool = new Tool(getContext());
        clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);

        //    CONNECTION
        iAPIRequest = Connection.getInstance(getContext()).getiAPIRequest();
        iBaseResponse = this;
    }

    private void setEvent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity) getActivity()).getFinanceRecord();
                resetRecyclerViewData();
            }
        });

        mentorFeedViewAdapter.setOnCollapseSearchView(new MentorFeedViewAdapter.OnCollapseSearchView() {
            @Override
            public void OnCollapseSearchView() {
                ((FragmentHome) getParentFragment()).searchMenuItem.collapseActionView();
            }
        });

        mentorFeedViewAdapter.setOnLoadMoreListener(new MentorFeedViewAdapter.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                fetchPageData();
            }
        });

        registerMentor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMentorRegistrationDialog();
            }
        });
    }

    public void resetRecyclerViewData() {
        lastId = 0;
        mentorFeedViewAdapter.lastAnimatedPosition = -1;

        fetchPageData();
    }

    private void setNullView(boolean visible) {
        if (visible) {
            nullView.setVisibility(View.VISIBLE);
        } else {
            nullView.setVisibility(View.GONE);
        }
    }

    private void showMentorRegistrationDialog() {
        final AlertDialog.Builder mentorRegistrationBuilder = new AlertDialog.Builder(getContext());
        mentorRegistrationBuilder.setPositiveButton("Daftar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        mentorRegistrationBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        final AlertDialog mentorRegistrationDialog = mentorRegistrationBuilder.create();
        mentorRegistrationDialog.setView(mentorRegistrationDialog.getLayoutInflater().inflate(R.layout.activity_mentor_registration, null));
        mentorRegistrationDialog.setCanceledOnTouchOutside(false);
        mentorRegistrationDialog.show();
        Tool.getInstance(getContext()).resizeAlertDialog(mentorRegistrationDialog);

        photoLayout = (TextInputLayout) mentorRegistrationDialog.findViewById(R.id.mentor_registration_photo_layout);
        emailLayout = (TextInputLayout) mentorRegistrationDialog.findViewById(R.id.mentor_registration_email_layout);
        nameLayout = (TextInputLayout) mentorRegistrationDialog.findViewById(R.id.mentor_registration_name_layout);
        mobileLayout = (TextInputLayout) mentorRegistrationDialog.findViewById(R.id.mentor_registration_mobile_layout);
        email = (AutoCompleteTextView) mentorRegistrationDialog.findViewById(R.id.mentor_registration_email);
        name = (AutoCompleteTextView) mentorRegistrationDialog.findViewById(R.id.mentor_registration_name);
        mobile = (AutoCompleteTextView) mentorRegistrationDialog.findViewById(R.id.mentor_registration_mobile);

        photoErrorMessageContainer = (LinearLayout) mentorRegistrationDialog.findViewById(R.id.mentor_registration_photo_error_message_container);
        uploadSpace = (RelativeLayout) mentorRegistrationDialog.findViewById(R.id.mentor_registration_upload_space);
        photoProgress = (ProgressBar) mentorRegistrationDialog.findViewById(R.id.mentor_registration_photo_progress);
        photo = (CircleImageView) mentorRegistrationDialog.findViewById(R.id.mentor_registration_photo);
        photoRemove = (ImageButton) mentorRegistrationDialog.findViewById(R.id.mentor_registration_photo_remove);

        progressBarRadius = (int) getResources().getDimension(R.dimen.photo_mentor_radius) + addRadius(18);
        relativeLayoutParams = new RelativeLayout.LayoutParams(progressBarRadius, progressBarRadius);
        relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        photoProgress.setLayoutParams(relativeLayoutParams);

        focus = null;
        emailValid = nameValid = mobileValid = photoAttached = false;

        uploadSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoErrorMessageContainer.setVisibility(View.GONE);
                photoLayout.setError(null);
                photoLayout.setErrorEnabled(false);

                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), ActivityImageFullScreen.class);
                intent.putExtra("uri", uri);
                startActivity(intent);
            }
        });

        photoRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomAlertDialog customAlertDialog = new CustomAlertDialog(getContext());
                customAlertDialog.setTitle("Konfirmasi");
                customAlertDialog.setMessage("Anda akan menghapus foto yang terpilih?");
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

                        uploadSpace.setVisibility(View.VISIBLE);
                        photoProgress.setVisibility(View.INVISIBLE);
                        photoRemove.setVisibility(View.INVISIBLE);

                        photoAttached = false;
                        bitmap = null;
                        photo.setImageBitmap(bitmap);

                        toast = Toast.makeText(getContext(), "Foto Dihapus", Toast.LENGTH_SHORT);
                        Tool.getInstance(getContext()).resizeToast(toast);
                        toast.show();
                    }
                });
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!emailValid) {
                    emailLayout.setError(null);
                    emailLayout.setErrorEnabled(false);
                    emailValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!nameValid) {
                    nameLayout.setError(null);
                    nameLayout.setErrorEnabled(false);
                    nameValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!mobileValid) {
                    mobileLayout.setError(null);
                    mobileLayout.setErrorEnabled(false);
                    mobileValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mentorRegistrationDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mentorRegistrationDialog.dismiss();
            }
        });

        mentorRegistrationDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!photoAttached) {
                    photoErrorMessageContainer.setVisibility(View.VISIBLE);
                    photoLayout.setError("Foto mentor harus diunggah sebagai bagian syarat pendaftaran");
                }
                checkValidity();
                if (photoAttached && emailValid && nameValid && mobileValid) {
                    final CustomAlertDialog customAlertDialog = new CustomAlertDialog(getContext());
                    customAlertDialog.setTitle("Konfirmasi");
                    customAlertDialog.setMessage("Anda akan mendaftarkan mentor dengan informasi pribadi terkait?");
                    customAlertDialog.setNegativeButton("Tidak", new CustomAlertDialog.OnNegativeClickListener() {
                        @Override
                        public void onClick() {
                            customAlertDialog.dismiss();
                        }
                    });
                    customAlertDialog.setPositiveButton("Ya", new CustomAlertDialog.OnPositiveClickListener() {
                        @Override
                        public void onClick() {
                            mentorRegistrationDialog.dismiss();
                            customAlertDialog.dismiss();

                            tryRegisterMentor();
                        }
                    });
                } else {
                    if (focus != null) {
                        focus.requestFocus();
                    }
                }
            }
        });

        verifyStoragePermission(getActivity());
    }

    private void verifyStoragePermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PERMISSION_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                uploadSpace.setEnabled(false);
                uploadSpace.setAlpha(0.25f);
            }
        }
    }

    private void showRegistrationSucceedNotification(final String securityCode) {
        snackbar = Snackbar.make(mainLayout, securityCode, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Salin", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clipData = ClipData.newPlainText(Global.SECURITY_CODE, securityCode);
                clipboardManager.setPrimaryClip(clipData);

                toast = Toast.makeText(getActivity(), "Kode Keamanan Tersalin", Toast.LENGTH_SHORT);
                Tool.getInstance(getContext()).resizeToast(toast);
                toast.show();
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
        tool.resizeSnackBarWithCallBack(snackbar);
        snackbar.show();

        toast = Toast.makeText(getContext(), "Mentor berhasil didaftarkan", Toast.LENGTH_SHORT);
        tool.resizeToast(toast);
        toast.show();
    }

    private void checkValidity() {
        emailText = email.getText().toString().trim();
        nameText = name.getText().toString().trim();
        mobileText = mobile.getText().toString().trim();

        if (TextUtils.isEmpty(mobileText)) {
            mobileLayout.setError("Field no. hp harus diisikan");
            focus = mobile;
            mobileValid = false;
        } else if (!isMobileFormatValid(mobileText)) {
            mobileLayout.setError("Format no. hp salah");
            focus = mobile;
            mobileValid = false;
        }

        if (TextUtils.isEmpty(nameText)) {
            nameLayout.setError("Field nama lengkap harus diisikan");
            focus = name;
            nameValid = false;
        } else if (!isNameFormatValid(nameText)) {
            nameLayout.setError("Nama lengkap tidak dapat mengandung karakter khusus");
            focus = name;
            nameValid = false;
        }

        if (TextUtils.isEmpty(emailText)) {
            emailLayout.setError("Field email harus diisikan");
            focus = email;
            emailValid = false;
        } else if (!isEmailFormatValid(emailText)) {
            emailLayout.setError("Alamat email yang dimasukkan tidak valid");
            focus = email;
            emailValid = false;
        }
    }

    private boolean isEmailFormatValid(String emailText) {
        return emailText.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    }

    private boolean isNameFormatValid(String nameText) {
        return nameText.matches("^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}");
    }

    private boolean isMobileFormatValid(String mobileText) {
        return (mobileText.charAt(0) == '0' && mobile.length() > 9);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK && requestCode == PICK_IMAGE) {
            toast = Toast.makeText(getContext(), "Foto Terpilih", Toast.LENGTH_SHORT);
            tool.resizeToast(toast);
            toast.show();

            uri = data.getData();
            bitmap = tool.adjustBitmap(tool.resizeBitmap(uri), uri);

            setImpactPhotoUploaded();
        }
    }

    private void setImpactPhotoUploaded() {
        uploadSpace.setVisibility(View.GONE);
        photoRemove.setVisibility(View.VISIBLE);

        photoProgress.setProgress(0);
        photoProgress.setVisibility(View.VISIBLE);

        new CompressImage().execute();
    }

    private int addRadius(int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, getResources().getDisplayMetrics());
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
            photoAttached = true;

            toast = Toast.makeText(getContext(), "Terunggah", Toast.LENGTH_SHORT);
            Tool.getInstance(getContext()).resizeToast(toast);
            toast.show();
        }
    }

    public void fetchPageData() {
        if (lastId == 0) {
            swipeRefreshLayout.setRefreshing(true);
        }
        if (Connectivity.isConnected(getContext())) {
            iAPIRequest.loadMentorList(String.valueOf(lastId)).enqueue(new Callback<ModelMentorList>() {
                @Override
                public void onResponse(Call<ModelMentorList> call, retrofit2.Response<ModelMentorList> response) {
                    if (lastId == 0) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (response.code() == 200) {
                        iBaseResponse.onSuccess(response.body());
                    } else {
                        iBaseResponse.onFailure(response.message());
                    }
                }

                @Override
                public void onFailure(Call<ModelMentorList> call, Throwable t) {
                    if (lastId == 0) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    iBaseResponse.onFailure(t.getMessage());
                    if (t instanceof SocketTimeoutException) {
                        fetchPageData();
                    }
                }
            });
        } else {
            ((MainActivity) getActivity()).showNoInternetNotification(new MainActivity.INoInternet() {
                @Override
                public void onRetry() {
                    fetchPageData();
                }
            });
        }
    }

    private void tryRegisterMentor() {
        customProgressDialog = new CustomProgressDialog(getContext());
        customProgressDialog.setMessage("Mendaftarkan mentor...");

        if (Connectivity.isConnected(getContext())) {
            registerMentor();
        } else {
            customProgressDialog.dismiss();
            ((MainActivity) getActivity()).showNoInternetNotification(new MainActivity.INoInternet() {
                @Override
                public void onRetry() {
                    tryRegisterMentor();
                }
            });
        }
    }

    private void registerMentor() {
        temp = tool.getTempFileImage(byteArrayOutputStream);

        metadata = new HashMap<>();
        metadata.put("image\"; filename=\"image.jpg\" ", Request.getInstance().getImage(temp));
        metadata.put("email", Request.getInstance().getText(emailText));
        metadata.put("name", Request.getInstance().getText(nameText));
        metadata.put("phone", Request.getInstance().getText(mobileText));

        iAPIRequest.registerMentor(metadata).enqueue(new Callback<ModelRegisterMentor>() {
            @Override
            public void onResponse(Call<ModelRegisterMentor> call, retrofit2.Response<ModelRegisterMentor> response) {
                customProgressDialog.dismiss();
                tool.deleteTempFileImage(temp);

                if (response.code() == 200) {
                    iBaseResponse.onSuccess(response.body());
                } else {
                    iBaseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ModelRegisterMentor> call, Throwable t) {
                customProgressDialog.dismiss();
                tool.deleteTempFileImage(temp);

                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    @Override
    public void onSuccess(Response response) {
        if (response instanceof ModelMentorList) {
            mentor = (ModelMentorList) response;
            final int size = mentor.getMentorList() == null ? 0 : mentor.getMentorList().size();
            if (size > 0) {
                if (lastId == 0) {
                    mentorList = mentor.getMentorList();
                } else {
                    mentorList.addAll(mentor.getMentorList());
                }
                mentorFeedViewAdapter.updateList(mentorList);
                mentorFeedViewAdapter.setLoaded();

                lastId = mentorList.get(mentorList.size() - 1).getId();
                setNullView(false);
            } else {
                mentorFeedViewAdapter.updateList(mentorList);
                if (mentorList.size() == 0) {
                    setNullView(true);
                }
            }
        } else if (response instanceof ModelRegisterMentor) {
            lastId = 0;
            fetchPageData();
            showRegistrationSucceedNotification(((ModelRegisterMentor) response).getSecurityCode());
        }
    }

    @Override
    public void onFailure(String message) {
        if (this.isVisible()) {
            final Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            Tool.getInstance(getContext()).resizeToast(toast);
            toast.show();
        }
    }
}