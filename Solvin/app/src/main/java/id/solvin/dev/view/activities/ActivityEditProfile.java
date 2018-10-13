package id.solvin.dev.view.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.ConfigApp;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.SCrypt;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.Error;
import id.solvin.dev.model.basic.Mentor;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.Student;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.interfaces.OnErrors;
import id.solvin.dev.presenter.AuthPresenter;
import id.solvin.dev.view.widget.ClassCheatSheet;
import id.solvin.dev.view.widget.CustomAlertDialog;
import id.solvin.dev.view.widget.CustomProgressDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 9/2/2016.
 */
public class ActivityEditProfile extends AppCompatActivity implements IBaseResponse,OnErrors {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    private static OnProfileUpdated mProfileUpdated;

    public interface OnProfileUpdated {
        void OnProfileUpdated();
    }

    public void setProfileUpdated(OnProfileUpdated listener) {
        mProfileUpdated = listener;
    }

    public void sendNotificationProfileUpdated() {
        if (mProfileUpdated != null) {
            mProfileUpdated.OnProfileUpdated();
        }
    }

    private Toolbar toolbar;
    private View focus;
    private CoordinatorLayout mainLayout;
    private RelativeLayout avatarLayout;
    private CircleImageView userPhoto;
    private ProgressBar userPhotoProgress;
    private TextView avatarInitial, nameLabel, membershipCodeLabel, joinedTimeLabel;
    private TextInputLayout emailLayout, nameLayout, passwordLayout, mobileLayout;
    private AutoCompleteTextView email, name, password, mobile, birthDate, address, school, workPlace;
    private ImageButton changePhotoIcon, deletePhotoIcon, editPassword;
    private AlertDialog.Builder editPasswordBuilder;
    private AlertDialog editPasswordDialog;
    private Button save;
    private Toast toast;
    private Snackbar snackbar;

    private TextInputLayout activedPasswordLayout, newPasswordLayout, confirmNewPasswordLayout;
    private AutoCompleteTextView activedPassword, newPassword, confirmNewPassword;
    private ImageButton toggleActivedPassword, toggleNewPassword, toggleConfirmNewPassword;

    private RelativeLayout.LayoutParams relativeLayoutParams;

    private TypedValue typedValue;
    private ClassApplicationTool applicationTool;
    private CustomAlertDialog customAlertDialog;
    private CustomProgressDialog customProgressDialog;

    private int avatarColor, joinedTime = 3;
    private final int PICK_IMAGE = 1;
    private int passwordPaddingRight, cursorPoint, mYear, mMonth, mDay;
    private int activedPasswordPaddingRight, newPasswordPaddingRight, confirmNewPasswordPaddingRight;

    private String emailText, nameText, mobileText;
    private String activedPasswordText, newPasswordText, confirmNewPasswordText;

    private boolean emailValid, nameValid, mobileValid;
    private boolean activedPasswordValid, newPasswordValid, confirmNewPasswordValid;
    private boolean activedPasswordVisible, newPasswordVisible, confirmNewPasswordVisible;

    private Bitmap bitmap;
    private Uri uri;
    private ByteArrayOutputStream byteArrayOutputStream;

    private Intent intent;
    private GradientDrawable gradientDrawable;
    private Auth auth;

    private AuthPresenter authPresenter;
    private int progressBarRadius;
    private String encryptedActivedPassword, encryptedNewPassword, encryptedPhone;
    private boolean isDelete = false;

    private SCrypt sCrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();
        setEvent();
    }

    private void init() {
        authPresenter = new AuthPresenter(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = (CoordinatorLayout) findViewById(R.id.edit_profile_main_layout);
        avatarLayout = (RelativeLayout) findViewById(R.id.edit_profile_avatar_layout);
        userPhoto = (CircleImageView) findViewById(R.id.edit_profile_user_photo);
        userPhotoProgress = (ProgressBar) findViewById(R.id.edit_profile_user_photo_progress);
        avatarInitial = (TextView) findViewById(R.id.edit_profile_avatar_initial);
        nameLabel = (TextView) findViewById(R.id.edit_profile_name_label);
        membershipCodeLabel = (TextView) findViewById(R.id.edit_profile_membership_code_label);
        joinedTimeLabel = (TextView) findViewById(R.id.edit_profile_joined_time_label);

        emailLayout = (TextInputLayout) findViewById(R.id.edit_profile_email_layout);
        nameLayout = (TextInputLayout) findViewById(R.id.edit_profile_name_layout);
        passwordLayout = (TextInputLayout) findViewById(R.id.edit_profile_password_layout);
        mobileLayout = (TextInputLayout) findViewById(R.id.edit_profile_mobile_layout);

        email = (AutoCompleteTextView) findViewById(R.id.edit_profile_email);
        name = (AutoCompleteTextView) findViewById(R.id.edit_profile_name);
        password = (AutoCompleteTextView) findViewById(R.id.edit_profile_password);
        mobile = (AutoCompleteTextView) findViewById(R.id.edit_profile_mobile);
        birthDate = (AutoCompleteTextView) findViewById(R.id.edit_profile_birthdate);
        address = (AutoCompleteTextView) findViewById(R.id.edit_profile_address);
        school = (AutoCompleteTextView) findViewById(R.id.edit_profile_school);
        workPlace = (AutoCompleteTextView) findViewById(R.id.edit_profile_workplace);

        editPassword = (ImageButton) findViewById(R.id.edit_profile_edit_password);
        save = (Button) findViewById(R.id.edit_profile_save);

        progressBarRadius = (int) getResources().getDimension(R.dimen.circle_image_view_big_radius) + addRadius(18);
        relativeLayoutParams = new RelativeLayout.LayoutParams(progressBarRadius, progressBarRadius);
        relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        userPhotoProgress.setLayoutParams(relativeLayoutParams);

        applicationTool = new ClassApplicationTool(getApplicationContext());

        birthDate.setInputType(InputType.TYPE_NULL);
        mYear = Calendar.getInstance().get(Calendar.YEAR);
        mMonth = Calendar.getInstance().get(Calendar.MONTH);
        mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        passwordPaddingRight = password.getPaddingRight();
        emailValid = nameValid = mobileValid = true;

        sCrypt = SCrypt.getInstance();

        refreshAuth();
    }

    private void refreshAuth() {
        Session session = Session.with(getApplicationContext());
        auth = session.getAuth();
        nameLabel.setText(session.getAuth().getName());
        membershipCodeLabel.setText("Kode Membership: " + session.getAuth().getMember_code());
        joinedTimeLabel.setText("Bergabung sejak " + session.getAuth().getJoin_time());

        email.setText(session.getAuth().getEmail());
        email.setSelection(email.getText().length());
        name.setText(session.getAuth().getName());
        mobile.setText(session.getAuth().getPhone());

        if (session.getAuth().getAge() != -1) {
            final String[] dateParts = session.getAuth().getBirth().split("-");
            birthDate.setText(dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0]);
        } else {
            birthDate.setText("");
        }
        address.setText(session.getAuth().getAddress());

        avatarColor = ClassApplicationTool.with(this).getAvatarColor(session.getAuth().getId());

        if (session.getLoginType() == ConfigApp.get().STUDENT) {
            Student student = (Student) auth;

            gradientDrawable = (GradientDrawable) avatarLayout.getBackground();
            gradientDrawable.setColor(avatarColor);
            avatarInitial.setText(Global.get().getInitialName(student.getName()));
            avatarLayout.setVisibility(View.VISIBLE);
            userPhoto.setVisibility(View.GONE);

            school.setVisibility(View.VISIBLE);
            workPlace.setVisibility(View.GONE);
            school.setText(student.getSchool());
        } else {
            Mentor mentor = (Mentor) auth;

            avatarLayout.setVisibility(View.GONE);
            userPhoto.setVisibility(View.VISIBLE);

            school.setVisibility(View.GONE);
            workPlace.setVisibility(View.VISIBLE);
            workPlace.setText(mentor.getWorkplace());
        }
        if (!auth.getPhoto().isEmpty()) {
            avatarLayout.setVisibility(View.GONE);
            userPhoto.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext()).load(Global.get().getAssetURL(auth.getPhoto(), auth.getAuth_type()))
                    .placeholder(R.drawable.operator)
                    .fit()
                    .centerCrop()
                    .into(userPhoto);
        }
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmation();
            }
        });

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplication(), ActivityFullScreen.class);
                if (uri != null) {
                    intent.putExtra("uri", uri);
                } else {
                    intent.putExtra("image", auth.getPhoto());
                    intent.putExtra("category", auth.getAuth_type());
                }
                startActivity(intent);
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!emailValid) {
                    emailLayout.setError(null);
                    emailLayout.setErrorEnabled(false);
                    emailValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!nameValid) {
                    nameLayout.setError(null);
                    nameLayout.setErrorEnabled(false);
                    nameValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditPasswordDialog();
            }
        });

        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mobileValid) {
                    mobileLayout.setError(null);
                    mobileLayout.setErrorEnabled(false);
                    mobileValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        birthDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDialog(0);
                return false;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidity();
                if (emailValid && nameValid && mobileValid) {
                    customAlertDialog = new CustomAlertDialog(ActivityEditProfile.this);
                    customAlertDialog.setTitle("Konfirmasi");
                    customAlertDialog.setMessage("Anda akan menyimpan hasil pembaruan informasi profil?");
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

                            updateProfile();
                        }
                    });
                } else {
                    focus.requestFocus();
                }
            }
        });
    }

    private void updateProfile() {
        customProgressDialog = new CustomProgressDialog(ActivityEditProfile.this);
        customProgressDialog.setMessage("Menyimpan hasil pembaruan profil...");

        if (Connectivity.isConnected(getApplicationContext())) {
            try {
                encryptedPhone = sCrypt.bytesToHex(sCrypt.encrypt(mobileText));
            } catch (Exception e) {
            }
            authPresenter.updateProfile(getApplicationContext(), applicationTool,
                    isDelete, uri, byteArrayOutputStream, ActivityEditProfile.this,
                    emailText,
                    nameText,
                    encryptedPhone,
                    birthDate.length() == 0 ? "0000-00-00" : birthDate.getText().toString(),
                    address.getText().toString(),
                    Session.with(getApplicationContext()).getLoginType() == ConfigApp.get().STUDENT ?
                            school.getText().toString() : workPlace.getText().toString());
        } else {
            customProgressDialog.dismiss();

            showNotificationNetwork(new INoInternet() {
                @Override
                public void onRetry() {
                    updateProfile();
                }
            });
        }
    }

    private void changePassword() {
        customProgressDialog = new CustomProgressDialog(ActivityEditProfile.this);
        customProgressDialog.setMessage("Menggantikan password aktif...");

        if (Connectivity.isConnected(getApplicationContext())) {
            authPresenter.changePassword(getApplication(), activedPassword.getText().toString(),
                    newPassword.getText().toString(),
                    confirmNewPassword.getText().toString(), ActivityEditProfile.this);
        } else {
            customProgressDialog.dismiss();

            toast = Toast.makeText(ActivityEditProfile.this, getResources().getString(R.string.text_no_internet), Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
            toast.show();
        }
    }

    private void showNotificationNetwork(final INoInternet iNoInternet) {
        snackbar = Snackbar.make(mainLayout, getResources().getString(R.string.text_no_internet), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Coba Lagi", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNoInternet.onRetry();
            }
        });
        applicationTool.resizeSnackBarWithCallBack(snackbar);
        snackbar.show();
    }

    private void checkValidity() {
        emailText = email.getText().toString().trim();
        nameText = name.getText().toString().trim();
        mobileText = mobile.getText().toString().trim();

        if (TextUtils.isEmpty(emailText)) {
            emailLayout.setError("Field email harus diisikan");
            focus = email;
            emailValid = false;
        } else if (!isEmailValid(emailText)) {
            emailLayout.setError("Alamat email yang dimasukkan tidak valid");
            focus = email;
            emailValid = false;
        }

        if (TextUtils.isEmpty(nameText)) {
            nameLayout.setError("Field nama lengkap harus diisikan");
            focus = name;
            nameValid = false;
        } else if (!isNameValid(nameText)) {
            nameLayout.setError("Nama lengkap tidak dapat mengandung karakter khusus");
            focus = name;
            nameValid = false;
        }

        if (TextUtils.isEmpty(mobileText)) {
            mobileLayout.setError("Field no. hp lengkap harus diisikan");
            focus = mobile;
            mobileValid = false;
        } else if (!isMobileValid(mobileText)) {
            mobileLayout.setError("Format no. hp salah");
            focus = mobile;
            mobileValid = false;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            toast = Toast.makeText(ActivityEditProfile.this, "Foto Terpilih", Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
            toast.show();

            uri = data.getData();
            bitmap = applicationTool.adjustBitmap(applicationTool.resizeBitmap(uri), uri);

            setImpactPhotoUploaded();
        }
    }

    private void setImpactPhotoUploaded() {
        avatarLayout.setVisibility(View.GONE);
        userPhoto.setVisibility(View.VISIBLE);

        userPhotoProgress.setProgress(0);
        userPhotoProgress.setVisibility(View.VISIBLE);

        new CompressImage().execute();
    }

    class CompressImage extends AsyncTask<Void, Integer, Void> {
        private int compressQuality, targetCompress, min, progress;

        @Override
        protected void onPreExecute() {
            byteArrayOutputStream = new ByteArrayOutputStream();

            compressQuality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, byteArrayOutputStream);

            targetCompress = applicationTool.targetCompress;
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
            userPhotoProgress.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            userPhoto.setImageBitmap(bitmap);
            isDelete = false;

            toast = Toast.makeText(ActivityEditProfile.this, "Terunggah", Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
            toast.show();

            deletePhotoIcon.setEnabled(true);
            deletePhotoIcon.setAlpha(1f);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        mDateSetListener, mYear, mMonth, mDay);
                datePickerDialog.updateDate(2000, 0, 1);
                return datePickerDialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;

            updateDisplay();
        }
    };

    private void updateDisplay() {
        birthDate.setText(
                new StringBuilder().
                        append(mDay).append("-").append(mMonth + 1).append("-").append(mYear).append(" ")
        );
    }

    private boolean isEmailValid(String email) {
        return email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    }

    private boolean isNameValid(String name) {
        return name.matches("^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}");
    }

    private boolean isMobileValid(String mobile) {
        return (mobile.length() > 9 && mobile.charAt(0) == '0');
    }

    private void showEditPasswordDialog() {
        editPasswordBuilder = new AlertDialog.Builder(ActivityEditProfile.this);
        editPasswordBuilder.setPositiveButton("Ganti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        editPasswordBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        editPasswordDialog = editPasswordBuilder.create();
        editPasswordDialog.setView(editPasswordDialog.getLayoutInflater().inflate(R.layout.activity_edit_password, null));
        editPasswordDialog.setCanceledOnTouchOutside(false);
        editPasswordDialog.show();
        applicationTool.resizeAlertDialog(editPasswordDialog);

        activedPasswordLayout = (TextInputLayout) editPasswordDialog.findViewById(R.id.edit_password_actived_password_layout);
        newPasswordLayout = (TextInputLayout) editPasswordDialog.findViewById(R.id.edit_password_new_password_layout);
        confirmNewPasswordLayout = (TextInputLayout) editPasswordDialog.findViewById(R.id.edit_password_confirm_new_password_layout);

        activedPassword = (AutoCompleteTextView) editPasswordDialog.findViewById(R.id.edit_password_actived_password);
        newPassword = (AutoCompleteTextView) editPasswordDialog.findViewById(R.id.edit_password_new_password);
        confirmNewPassword = (AutoCompleteTextView) editPasswordDialog.findViewById(R.id.edit_password_confirm_new_password);

        toggleActivedPassword = (ImageButton) editPasswordDialog.findViewById(R.id.edit_profile_toggle_actived_password);
        toggleNewPassword = (ImageButton) editPasswordDialog.findViewById(R.id.edit_profile_toggle_new_password);
        toggleConfirmNewPassword = (ImageButton) editPasswordDialog.findViewById(R.id.edit_profile_toggle_confirm_new_password);

        toggleActivedPassword.setVisibility(View.GONE);
        toggleNewPassword.setVisibility(View.GONE);
        toggleConfirmNewPassword.setVisibility(View.GONE);

        activedPasswordPaddingRight = activedPassword.getPaddingRight();
        newPasswordPaddingRight = newPassword.getPaddingRight();
        confirmNewPasswordPaddingRight = confirmNewPassword.getPaddingRight();

        activedPasswordValid = newPasswordValid = confirmNewPasswordValid = false;
        activedPasswordVisible = newPasswordVisible = confirmNewPasswordVisible = false;

        activedPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!activedPasswordValid) {
                    activedPasswordLayout.setError(null);
                    activedPasswordLayout.setErrorEnabled(false);
                    activedPasswordValid = true;
                }
                if (s.length() != 0) {
                    toggleActivedPassword.setVisibility(View.VISIBLE);
                    activedPassword.setPadding(activedPassword.getPaddingLeft(), activedPassword.getPaddingTop(),
                            activedPasswordPaddingRight, activedPassword.getPaddingBottom());
                } else {
                    toggleActivedPassword.setVisibility(View.GONE);
                    activedPassword.setPadding(activedPassword.getPaddingLeft(), activedPassword.getPaddingTop(),
                            email.getPaddingRight(), activedPassword.getPaddingBottom());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!newPasswordValid) {
                    newPasswordLayout.setError(null);
                    newPasswordLayout.setErrorEnabled(false);
                    newPasswordValid = true;
                }
                if (s.length() != 0) {
                    toggleNewPassword.setVisibility(View.VISIBLE);
                    newPassword.setPadding(newPassword.getPaddingLeft(), newPassword.getPaddingTop(),
                            newPasswordPaddingRight, newPassword.getPaddingBottom());
                } else {
                    toggleNewPassword.setVisibility(View.GONE);
                    newPassword.setPadding(newPassword.getPaddingLeft(), newPassword.getPaddingTop(),
                            email.getPaddingRight(), newPassword.getPaddingBottom());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!confirmNewPasswordValid) {
                    confirmNewPasswordLayout.setError(null);
                    confirmNewPasswordLayout.setErrorEnabled(false);
                    confirmNewPasswordValid = true;
                }
                if (s.length() != 0) {
                    toggleConfirmNewPassword.setVisibility(View.VISIBLE);
                    confirmNewPassword.setPadding(confirmNewPassword.getPaddingLeft(), confirmNewPassword.getPaddingTop(),
                            confirmNewPasswordPaddingRight, confirmNewPassword.getPaddingBottom());
                } else {
                    toggleConfirmNewPassword.setVisibility(View.GONE);
                    confirmNewPassword.setPadding(confirmNewPassword.getPaddingLeft(), confirmNewPassword.getPaddingTop(),
                            email.getPaddingRight(), confirmNewPassword.getPaddingBottom());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        toggleActivedPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursorPoint = activedPassword.getSelectionStart();
                if (activedPasswordVisible) {
                    activedPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    activedPassword.setSelection(cursorPoint);
                    toggleActivedPassword.setImageResource(R.drawable.ic_visibility);

                    activedPasswordVisible = false;
                } else {
                    activedPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    activedPassword.setSelection(cursorPoint);
                    toggleActivedPassword.setImageResource(R.drawable.ic_visibility_off);

                    activedPasswordVisible = true;
                }
            }
        });

        toggleNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursorPoint = newPassword.getSelectionStart();
                if (newPasswordVisible) {
                    newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    newPassword.setSelection(cursorPoint);
                    toggleNewPassword.setImageResource(R.drawable.ic_visibility);

                    newPasswordVisible = false;
                } else {
                    newPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    newPassword.setSelection(cursorPoint);
                    toggleNewPassword.setImageResource(R.drawable.ic_visibility_off);

                    newPasswordVisible = true;
                }
            }
        });

        toggleConfirmNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursorPoint = confirmNewPassword.getSelectionStart();
                if (confirmNewPasswordVisible) {
                    confirmNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmNewPassword.setSelection(cursorPoint);
                    toggleConfirmNewPassword.setImageResource(R.drawable.ic_visibility);

                    confirmNewPasswordVisible = false;
                } else {
                    confirmNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmNewPassword.setSelection(cursorPoint);
                    toggleConfirmNewPassword.setImageResource(R.drawable.ic_visibility_off);

                    confirmNewPasswordVisible = true;
                }
            }
        });

        editPasswordDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPasswordDialog.dismiss();
            }
        });

        editPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPasswordValidity();
                if (activedPasswordValid && newPasswordValid && confirmNewPasswordValid) {
                    customAlertDialog = new CustomAlertDialog(ActivityEditProfile.this);
                    customAlertDialog.setTitle("Konfirmasi");
                    customAlertDialog.setMessage("Anda akan menggantikan password aktif anda?");
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

                            changePassword();
                        }
                    });
                } else {
                    focus.requestFocus();
                    if (!activedPasswordValid) {
                        toggleActivedPassword.setVisibility(View.GONE);
                        activedPassword.setPadding(activedPassword.getPaddingLeft(), activedPassword.getPaddingTop(),
                                email.getPaddingRight(), activedPassword.getPaddingBottom());

                        newPasswordLayout.setError(null);
                        newPasswordLayout.setErrorEnabled(false);
                        newPasswordValid = true;

                        confirmNewPasswordLayout.setError(null);
                        confirmNewPasswordLayout.setErrorEnabled(false);
                        confirmNewPasswordValid = true;
                    }
                    if (!newPasswordValid) {
                        toggleNewPassword.setVisibility(View.GONE);
                        newPassword.setPadding(newPassword.getPaddingLeft(), newPassword.getPaddingTop(),
                                email.getPaddingRight(), newPassword.getPaddingBottom());

                        confirmNewPasswordLayout.setError(null);
                        confirmNewPasswordLayout.setErrorEnabled(false);
                        confirmNewPasswordValid = true;
                    }
                    if (!confirmNewPasswordValid) {
                        toggleConfirmNewPassword.setVisibility(View.GONE);
                        confirmNewPassword.setPadding(confirmNewPassword.getPaddingLeft(), confirmNewPassword.getPaddingTop(),
                                email.getPaddingRight(), confirmNewPassword.getPaddingBottom());
                    }
                }
            }
        });
    }

    private void updateActivedPassword() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Snackbar snackbar = Snackbar.make(mainLayout, "Password aktif telah berhasil diperbarui", Snackbar.LENGTH_LONG);
                applicationTool.resizeSnackBar(snackbar, 1);
                snackbar.show();
            }
        }, 500);
    }

    private void checkPasswordValidity() {
        activedPasswordText = activedPassword.getText().toString().trim();
        newPasswordText = newPassword.getText().toString().trim();
        confirmNewPasswordText = confirmNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(activedPasswordText)) {
            activedPasswordLayout.setError("Isikan password aktif anda");
            focus = activedPasswordLayout;
            activedPasswordValid = false;
        } else {
            activedPasswordValid = true;
        }

        if (!confirmNewPasswordText.equals(newPasswordText) && activedPasswordValid) {
            confirmNewPasswordLayout.setError("Hasil konfirmasi tidak sesuai");
            focus = confirmNewPasswordLayout;
            confirmNewPasswordValid = false;
        } else {
            confirmNewPasswordValid = true;
        }

        if (TextUtils.isEmpty(newPasswordText) && activedPasswordValid) {
            newPasswordLayout.setError("Isikan password baru anda");
            focus = newPasswordLayout;
            newPasswordValid = false;
        } else if (newPasswordText.length() < 6 && activedPasswordValid) {
            newPasswordLayout.setError("Panjang password baru harus terdiri dari minimal 6 karakter");
            focus = newPasswordLayout;
            newPasswordValid = false;
        } else {
            newPasswordValid = true;
        }
    }

    private void showConfirmation() {
        customAlertDialog = new CustomAlertDialog(ActivityEditProfile.this);
        customAlertDialog.setTitle("Konfirmasi");
        customAlertDialog.setMessage("Anda akan membatalkan kegiatan mengedit profil?");
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
                finish();
            }
        });
    }

    private int addRadius(int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, getResources().getDisplayMetrics());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showConfirmation();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Session.with(getApplicationContext()).getLoginType() != ConfigApp.get().MENTOR) {
            getMenuInflater().inflate(R.menu.edit_profile, menu);
            typedValue = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.actionBarItemBackground, typedValue, true);

            changePhotoIcon = (ImageButton) menu.findItem(R.id.action_change_photo).getActionView();
            changePhotoIcon.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.action_button_default_width), ViewGroup.LayoutParams.MATCH_PARENT));
            changePhotoIcon.setImageResource(R.drawable.ic_camera_light);
            changePhotoIcon.setBackgroundResource(typedValue.resourceId);
            changePhotoIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE);
                }
            });
            ClassCheatSheet.setup(changePhotoIcon, "Ganti Foto");

            deletePhotoIcon = (ImageButton) menu.findItem(R.id.action_delete_photo).getActionView();
            deletePhotoIcon.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.action_button_default_width), ViewGroup.LayoutParams.MATCH_PARENT));
            deletePhotoIcon.setImageResource(R.drawable.ic_delete_light);
            deletePhotoIcon.setBackgroundResource(typedValue.resourceId);
            if (!auth.getPhoto().isEmpty()) {
                deletePhotoIcon.setEnabled(true);
                deletePhotoIcon.setAlpha(1f);
            } else {
                deletePhotoIcon.setEnabled(false);
                deletePhotoIcon.setAlpha(0.25f);
            }
            deletePhotoIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customAlertDialog = new CustomAlertDialog(ActivityEditProfile.this);
                    customAlertDialog.setTitle("Konfirmasi");
                    customAlertDialog.setMessage("Anda akan menghapus foto profil?");
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

                            deletePhoto();
                            deletePhotoIcon.setEnabled(false);
                            deletePhotoIcon.setAlpha(0.25f);
                            isDelete = true;
                        }
                    });
                }
            });
            ClassCheatSheet.setup(deletePhotoIcon, "Hapus Foto");

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onSuccess(Response response) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }
        if (response.getTag().equals(Response.TAG_PROFILE_UPDATE)) {
            setResult(RESULT_OK);

            finish();
            sendNotificationProfileUpdated();
        } else if (response.getTag().equals(Response.TAG_UPDATE_PASSWORD)) {
            if (editPasswordDialog != null) {
                editPasswordDialog.dismiss();
            }
            updateActivedPassword();
        }
    }

    @Override
    public void onFailure(String message) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }
        toast = Toast.makeText(ActivityEditProfile.this, message, Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();
    }

    private void deletePhoto() {
        toast = Toast.makeText(ActivityEditProfile.this, "Foto Telah Dihapus", Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();

        uri = null;
        bitmap = null;

        avatarLayout.setVisibility(View.VISIBLE);
        userPhoto.setVisibility(View.GONE);
        userPhoto.setImageBitmap(bitmap);
        userPhotoProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onError(List<Error> errorList) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }
        for (Error error : errorList) {
            if (error.getType() == Error.ERROR_EMAIL) {
                emailLayout.setError(error.getMessage());
                focus = email;
                emailValid = false;

                focus.requestFocus();
                break;
            }
            if (error.getType() == Error.ERROR_ACTIVE_PASSWORD) {
                activedPasswordLayout.setError("Password yang dimasukkan tidak sesuai dengan password aktif anda saat ini");
                focus = activedPasswordLayout;
                activedPasswordValid = false;

                focus.requestFocus();
                toggleActivedPassword.setVisibility(View.GONE);
                activedPassword.setPadding(activedPassword.getPaddingLeft(), activedPassword.getPaddingTop(),
                        email.getPaddingRight(), activedPassword.getPaddingBottom());

                newPasswordLayout.setError(null);
                newPasswordLayout.setErrorEnabled(false);
                newPasswordValid = true;

                confirmNewPasswordLayout.setError(null);
                confirmNewPasswordLayout.setErrorEnabled(false);
                confirmNewPasswordValid = true;

                break;
            }
        }
    }
}