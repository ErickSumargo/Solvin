package id.solvin.dev.view.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.Error;
import id.solvin.dev.model.basic.Question;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.Solution;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.interfaces.OnErrors;
import id.solvin.dev.presenter.QuestionPresenter;
import id.solvin.dev.view.adapters.RoleViewAdapter;
import id.solvin.dev.view.fragments.FragmentSolvinKeyboard;
import id.solvin.dev.view.fragments.FragmentTextFormat;
import id.solvin.dev.view.widget.ClassCheatSheet;
import id.solvin.dev.view.widget.CustomAlertDialog;
import id.solvin.dev.view.widget.CustomProgressDialog;
import id.solvin.dev.view.widget.ClassRichEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.onegravity.rteditor.effects.Effects;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.media.CamcorderProfile.get;

/**
 * Created by Erick Sumargo on 9/3/2016.
 */
public class ActivityCommentSheet extends AppCompatActivity implements IBaseResponse, OnErrors {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    private static OnCommentSent mCommentSent;

    public interface OnCommentSent {
        void ISentConfirmedCreated();

        void ISentConfirmedEditted();
    }

    public void setCommentSent(OnCommentSent listener) {
        mCommentSent = listener;
    }

    public void sentConfirmedCreated() {
        if (mCommentSent != null) {
            mCommentSent.ISentConfirmedCreated();
        }
    }

    public void sentConfirmedEditted() {
        if (mCommentSent != null) {
            mCommentSent.ISentConfirmedEditted();
        }
    }

    private WindowManager windowManager;
    private Point point;
    private CoordinatorLayout.LayoutParams layoutParams;
    private InputMethodManager inputMethodManager;

    private FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction fragmentTransaction;
    private FragmentSolvinKeyboard solvinKeyboard;
    private FragmentTextFormat textFormat;
    private Bundle properties;

    private Toolbar toolbar;
    private View imageDivider, emptyView;
    private RecyclerView roleView;
    private RecyclerView.LayoutManager roleLayoutManager;
    private CoordinatorLayout commentSheet;
    private RelativeLayout imageContainer, imageForeground;
    private ScrollView commentLayout;
    private ClassRichEditText sheet;
    private ImageView image;
    private TextView uploadStatus;
    private ProgressBar imageProgress;
    private ImageButton imageRemove, textFormatIcon;
    private LinearLayout commentTargetNoteContainer;
    private Toast toast;
    private Snackbar snackbar;

    private RTApi rtApi;
    private RTManager rtManager;
    private ClipboardManager clipboardManager;
    private ClipData clipData;
    private Intent intent;
    private TypedValue typedValue;
    private CustomAlertDialog customAlertDialog;
    private AlertDialog.Builder commentTargetBuilder;
    private AlertDialog commentTargetDialog;

    private ClassRichEditText tempRT;

    private ClassApplicationTool applicationTool;
    private CustomProgressDialog customProgressDialog;

    private List<Auth> roleList;
    private RoleViewAdapter roleViewAdapter;

    private Bitmap bitmap, COMMENT_IMAGE;
    private Uri uri;
    private ByteArrayOutputStream byteArrayOutputStream;
    private static File mediaStorageDir, mediaFile;

    private int height, cursorPoint, selectionStart, selectionEnd,
            selectionStartStore, selectionEndStore, selectionStartPaste, selectionEndPaste;
    private int COMMENT_MODE;
    private static final int REQUEST_EXTERNAL_STORAGE = 1, CAPTURE_IMAGE = 0, PICK_IMAGE = 1;

    private boolean textFormatShown = false;

    private boolean boldActived = false, italicActived = false, underlineActived = false,
            strikeThroughActived = false, highlightActived = false;

    private List<Integer> activedStyleList;
    private List<Auth> checkedList;

    private CharSequence COMMENT_CONTENT;
    private static String[] PERMISSION_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String IMAGE_DIRECTORY_NAME = "Solvin";
    private static String timeStamp;
    private String pasteText, selectedHTML;
    ;
    private SpannableStringBuilder spannableStringBuilder;

    private Question question;
    private QuestionPresenter questionPresenter;
    private int COMMENT_POSITION = -1;

    public ActivityCommentSheet() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_sheet);

        rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
        rtManager = new RTManager(rtApi, savedInstanceState);
        init();
        setEvent();
    }

    private void init() {
        questionPresenter = new QuestionPresenter(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        windowManager = this.getWindow().getWindowManager();
        point = new Point();
        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);

        windowManager.getDefaultDisplay().getSize(point);
        height = point.y;

        solvinKeyboard = new FragmentSolvinKeyboard();
        textFormat = new FragmentTextFormat();
        applicationTool = new ClassApplicationTool(getApplicationContext());

        commentSheet = (CoordinatorLayout) findViewById(R.id.comment_sheet_layout);
        commentLayout = (ScrollView) findViewById(R.id.comment_layout);
        sheet = (ClassRichEditText) findViewById(R.id.comment_sheet);
        image = (ImageView) findViewById(R.id.comment_image);
        uploadStatus = (TextView) findViewById(R.id.comment_upload_status);
        imageProgress = (ProgressBar) findViewById(R.id.comment_image_progress);
        imageRemove = (ImageButton) findViewById(R.id.comment_image_remove);
        imageContainer = (RelativeLayout) findViewById(R.id.comment_image_container);
        imageForeground = (RelativeLayout) findViewById(R.id.comment_image_foreground);
        imageDivider = findViewById(R.id.comment_image_divider);
        emptyView = findViewById(R.id.empty_view);

        tempRT = (ClassRichEditText) findViewById(R.id.temp_rt);

        sheet.requestFocus();

        rtManager.registerEditor(sheet, true);
        rtManager.registerEditor(tempRT, true);

        selectionStart = selectionEnd = -1;
        selectionStartStore = selectionEndStore = -1;
        selectionStartPaste = selectionEndPaste = -1;

        activedStyleList = new ArrayList<>();

        adjustView(height / 12);
        attachTextFormatView();

        COMMENT_MODE = getIntent().getExtras().getInt("COMMENT_MODE");
        COMMENT_POSITION = getIntent().getExtras().getInt(Global.get().key().QUESTION_DATA_POSITION);

        question = (Question) getIntent().getSerializableExtra(Global.get().key().QUESTION_DATA);

        if (COMMENT_MODE == 1) {
            setTitle("Edit Komentar");
            if (question.getComments().get(COMMENT_POSITION).getContent().length() > 0) {
                sheet.setRichTextEditing(true, question.getComments().get(COMMENT_POSITION).getContent());
                sheet.setText(applicationTool.loadTextStyle(sheet.getText()));
            } else {
                sheet.setText("");
            }
            sheet.setSelection(sheet.length());

            if (!TextUtils.isEmpty(question.getComments().get(COMMENT_POSITION).getImage())) {
                Picasso.with(getApplicationContext()).load(Global.get().getAssetURL(question.getComments().get(COMMENT_POSITION).getImage(), "comment"))
                        .placeholder(R.drawable.image_placeholder)
                        .fit()
                        .centerCrop()
                        .into(image);

                image.setVisibility(View.VISIBLE);
                imageDivider.setVisibility(View.VISIBLE);
                imageProgress.setVisibility(View.INVISIBLE);
                uploadStatus.setVisibility(View.INVISIBLE);
                imageContainer.setVisibility(View.VISIBLE);
                imageForeground.setVisibility(View.VISIBLE);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        commentLayout.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 250);
            }

            emptyView.getLayoutParams().height = addExtraMargin(20);
        }

        attachSolvinKeyboardView(COMMENT_MODE);
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (COMMENT_MODE == 0) {
                    if (sheet.getText().length() != 0 || bitmap != null) {
                        showConfirmation();
                    } else {
                        onBackPressed();
                    }
                } else {
                    showConfirmation();
                }
            }
        });
        textFormat.setOnTextFormatStyle(new FragmentTextFormat.OnTextFormatStyle() {
            @Override
            public void setBold(boolean enabled) {
                if (enabled) {
                    sheet.applyEffect(Effects.BOLD, true);
                } else {
                    sheet.applyEffect(Effects.BOLD, false);
                }
                boldActived = enabled;
            }

            @Override
            public void setItalic(boolean enabled) {
                if (enabled) {
                    sheet.applyEffect(Effects.ITALIC, true);
                } else {
                    sheet.applyEffect(Effects.ITALIC, false);
                }
                italicActived = enabled;
            }

            @Override
            public void setUnderline(boolean enabled) {
                if (enabled) {
                    sheet.applyEffect(Effects.UNDERLINE, true);
                } else {
                    sheet.applyEffect(Effects.UNDERLINE, false);
                }
                underlineActived = enabled;
            }

            @Override
            public void setStrikeThrough(boolean enabled) {
                if (enabled) {
                    sheet.applyEffect(Effects.STRIKETHROUGH, true);
                } else {
                    sheet.applyEffect(Effects.STRIKETHROUGH, false);
                }
                strikeThroughActived = enabled;
            }

            @Override
            public void setHighlight(boolean enabled) {
                if (enabled) {
                    sheet.applyEffect(Effects.FONTCOLOR, getResources().getColor(R.color.colorPrimary));
                } else {
                    sheet.applyEffect(Effects.FONTCOLOR, null);
                }
                highlightActived = enabled;
            }
        });

        solvinKeyboard.setKeyboardTask(new FragmentSolvinKeyboard.OnKeyboardTask() {
            @Override
            public void ITypeCharacter(SpannableStringBuilder character) {
                typeCharacter(character);
            }

            @Override
            public void IAdjustView(int newHeight) {
                adjustView(newHeight);
            }

            @Override
            public void ICharacterDeletion(int limit, int n) {
                characterDeletion(limit, n);
            }

            @Override
            public boolean IIsTextSelected() {
                return IsTextSelected();
            }

            @Override
            public void IStartMediaActivity(int ACTIVITY_CODE) {
                startMediaActivity(ACTIVITY_CODE);
            }

            @Override
            public void IBackToActivity() {
                if (COMMENT_MODE == 0) {
                    setDataRole();
                    if (roleList.size() == 1) {
                        if (Session.with(getApplicationContext()).getAuth().getAuth_type().equals("mentor")) {
                            showDialogCommentTarget();
                        } else {
                            if (roleList.get(0).getId() == Session.with(getApplicationContext()).getAuth().getId()) {
                                showConfirmationSendDialog();
                            } else {
                                showDialogCommentTarget();
                            }
                        }
                    } else {
                        showDialogCommentTarget();
                    }
                } else {
                    showConfirmationSendDialog();
                }
            }
        });

        commentSheet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!solvinKeyboard.showAndroidKeyboard) {
                    touchSheetLayout();
                } else {
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                return false;
            }
        });

        sheet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchSheetLayout();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    hideSoftKeyboard();
                }
                return false;
            }
        });

        sheet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (bitmap == null || question.getComments().get(COMMENT_POSITION).getImage().isEmpty()) {
                    if (s.length() != 0 && !s.toString().matches("[\\n\\r]+")) {
                        solvinKeyboard.button_send.setEnabled(true);
                        solvinKeyboard.button_send.setAlpha(1f);
                    } else {
                        solvinKeyboard.button_send.setEnabled(false);
                        solvinKeyboard.button_send.setAlpha(0.25f);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sheet.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                hideSoftKeyboard();
            }
        });

        sheet.setOnOverridingContextMenu(new ClassRichEditText.OnOverridingContextMenu() {
            @Override
            public void onCut() {
                storeText();
                removeSelectedText();
            }

            @Override
            public void onCopy() {
                storeText();
            }

            @Override
            public void onPaste() {
                if (selectionStartStore == -1 && selectionEndStore == -1) {
                    if (clipboardManager.hasPrimaryClip()) {
                        pasteText = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                        tempRT.setRichTextEditing(true, pasteText);
                        tempRT.setText(applicationTool.loadTextStyle(tempRT.getText()));

                        if (sheet.hasSelection()) {
                            selectionStartPaste = sheet.getSelectionStart();
                            selectionEndPaste = sheet.getSelectionEnd();

                            spannableStringBuilder = new SpannableStringBuilder(sheet.getText());
                            spannableStringBuilder.replace(selectionStartPaste, selectionEndPaste, tempRT.getText());

                            sheet.setText(spannableStringBuilder);
                            sheet.setSelection(selectionStartPaste + tempRT.getText().length());
                        } else {
                            sheet.getText().insert(sheet.getSelectionStart(), tempRT.getText());
                        }
                    }
                } else {
                    if (sheet.hasSelection()) {
                        selectionStartPaste = sheet.getSelectionStart();
                        selectionEndPaste = sheet.getSelectionEnd();

                        removeSelectedText();
                        loadText();
                        sheet.setSelection(selectionStartPaste + (selectionEndStore - selectionStartStore));
                    } else {
                        loadText();
                    }
                }
            }
        });

        imageForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplication(), ActivityFullScreen.class);
                if (uri != null) {
                    intent.putExtra("uri", uri);
                } else {
                    intent.putExtra("image", question.getComments().get(COMMENT_POSITION).getImage());
                    intent.putExtra("category", "comment");
                }
                startActivity(intent);
            }
        });

        imageRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog = new CustomAlertDialog(ActivityCommentSheet.this);
                customAlertDialog.setTitle("Konfirmasi");
                customAlertDialog.setMessage("Anda akan menghapus gambar yang dipilih?");
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

                        toast = Toast.makeText(ActivityCommentSheet.this, "Gambar Dihapus", Toast.LENGTH_SHORT);
                        applicationTool.resizeToast(toast);
                        toast.show();

                        imageDivider.setVisibility(View.GONE);
                        imageProgress.setVisibility(View.GONE);
                        imageContainer.setVisibility(View.GONE);
                        imageForeground.setVisibility(View.GONE);
                        uploadStatus.setVisibility(View.GONE);

                        uri = null;
                        bitmap = null;
                        image.setImageDrawable(null);
                        emptyView.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                                , 0, getResources().getDisplayMetrics());

                        if (sheet.length() == 0) {
                            solvinKeyboard.button_send.setEnabled(false);
                            solvinKeyboard.button_send.setAlpha(0.25f);
                        } else {
                            solvinKeyboard.button_send.setEnabled(true);
                            solvinKeyboard.button_send.setAlpha(1f);
                        }
                    }
                });
            }
        });

        verifyStoragePermission(ActivityCommentSheet.this);
    }

    private void attachSolvinKeyboardView(int COMMENT_MODE) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (COMMENT_MODE == 1) {
            setButtonSendEnabled();
        }
        fragmentTransaction.add(R.id.solvin_keyboard_layout, solvinKeyboard);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    private void setButtonSendEnabled() {
        properties = new Bundle();
        properties.putBoolean("enabled", true);
        properties.putFloat("alpha", 1f);

        solvinKeyboard.setArguments(properties);
    }

    private void attachTextFormatView() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.text_format_layout, textFormat);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    private void showDialogCommentTarget() {
        commentTargetBuilder = new AlertDialog.Builder(ActivityCommentSheet.this);
        commentTargetBuilder.setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        commentTargetBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        commentTargetDialog = commentTargetBuilder.create();
        commentTargetDialog.setView(commentTargetDialog.getLayoutInflater().inflate(R.layout.activity_comment_target, null));
        commentTargetDialog.setCanceledOnTouchOutside(false);
        commentTargetDialog.show();
        applicationTool.resizeAlertDialog(commentTargetDialog);

        commentTargetDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationSendDialog();
            }
        });

        commentTargetDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentTargetDialog.dismiss();
            }
        });

        roleView = (RecyclerView) commentTargetDialog.findViewById(R.id.role_view);
        commentTargetNoteContainer = (LinearLayout) commentTargetDialog.findViewById(R.id.comment_target_note_container);

        roleLayoutManager = new LinearLayoutManager(this);
        roleLayoutManager.setAutoMeasureEnabled(true);

        roleViewAdapter = new RoleViewAdapter(roleList, getApplicationContext());
        roleView.setLayoutManager(roleLayoutManager);
        roleView.setAdapter(roleViewAdapter);
        roleView.setNestedScrollingEnabled(false);

        roleViewAdapter.getCheckedList(new RoleViewAdapter.OnSetCheckedList() {
            @Override
            public void IGetCheckedList(List<Auth> checkedList) {
                ActivityCommentSheet.this.checkedList = new ArrayList<>(checkedList);
            }
        });
    }

    private void showConfirmationSendDialog() {
        customAlertDialog = new CustomAlertDialog(ActivityCommentSheet.this);
        customAlertDialog.setTitle("Konfirmasi");
        if (COMMENT_MODE == 0) {
            customAlertDialog.setMessage("Anda akan menyampaikan komentar yang telah dibuat?");
        } else {
            customAlertDialog.setMessage("Anda akan menyampaikan komentar yang telah diedit?");
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
                if (commentTargetDialog != null) {
                    commentTargetDialog.dismiss();
                }
                customAlertDialog.dismiss();
                hideIMM();

                setCommentTarget();
                sendComment();
            }
        });
    }

    private void setCommentTarget() {
        if (COMMENT_MODE == 0) {
            final List<Auth> tempList = new ArrayList<>(checkedList);
            for (int i = 0; i < tempList.size(); i++) {
                if (tempList.get(i) == null) {
                    checkedList.remove(tempList.get(i));
                }
            }
        }
    }

    private void sendComment() {
        customProgressDialog = new CustomProgressDialog(ActivityCommentSheet.this);
        customProgressDialog.setMessage("Menyampaikan komentar...");

        if (Connectivity.isConnected(getApplicationContext())) {
            if (COMMENT_MODE == 0) {
                questionPresenter.doAddComment(question, checkedList, uri, bitmap, sheet,
                        applicationTool, byteArrayOutputStream, getApplicationContext());
            } else {
                questionPresenter.doEditComment(question.getComments().get(COMMENT_POSITION)
                        , uri, image, bitmap, sheet, applicationTool,
                        byteArrayOutputStream, getApplicationContext());
            }
        } else {
            customProgressDialog.dismiss();

            showNotificationNetwork(new INoInternet() {
                @Override
                public void onRetry() {
                    sendComment();
                }
            });
        }
    }

    private void showNotificationNetwork(final INoInternet iNoInternet) {
        snackbar = Snackbar.make(commentSheet, getResources().getString(R.string.text_no_internet), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Coba Lagi", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNoInternet.onRetry();
            }
        });
        applicationTool.resizeSnackBarWithCallBack(snackbar);
        snackbar.show();
    }

    private void setDataRole() {
        checkedList = new ArrayList<>();
        roleList = new ArrayList<>();
        roleList.add(question.getStudent());

        for (Solution j : question.getSolutions()) {
            roleList.add(j.getMentor());
        }
    }

    private void startMediaActivity(int ACTIVITY_CODE) {
        if (ACTIVITY_CODE == CAPTURE_IMAGE) {
            startCamera();
        } else if (ACTIVITY_CODE == PICK_IMAGE) {
            startGallery();
        }
    }

    private void startCamera() {
        uri = getOutputMediaFileUri();
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    private void startGallery() {
        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CAPTURE_IMAGE) {
            toast = Toast.makeText(ActivityCommentSheet.this, "Gambar Terambil", Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
            toast.show();

            bitmap = applicationTool.adjustBitmap(applicationTool.resizeBitmap(uri), uri);
            setImpactImageUploaded();
        } else if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            toast = Toast.makeText(ActivityCommentSheet.this, "Gambar Terpilih", Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
            toast.show();

            uri = data.getData();
            bitmap = applicationTool.adjustBitmap(applicationTool.resizeBitmap(uri), uri);

            setImpactImageUploaded();
        }
    }

    private void setImpactImageUploaded() {
        solvinKeyboard.button_send.setEnabled(false);
        solvinKeyboard.button_send.setAlpha(0.25f);

        imageDivider.setVisibility(View.VISIBLE);
        imageProgress.setVisibility(View.VISIBLE);
        imageProgress.setProgress(0);

        imageContainer.setVisibility(View.VISIBLE);
        imageForeground.setVisibility(View.VISIBLE);
        imageForeground.setEnabled(false);

        uploadStatus.setVisibility(View.GONE);

        emptyView.getLayoutParams().height = addExtraMargin(20);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                commentLayout.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 250);

        new CompressImage().execute();                                              //COMPRESS IMAGE
    }

    public int addExtraMargin(int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, getResources().getDisplayMetrics());
    }

    private void touchSheetLayout() {
        solvinKeyboard.touchSheetLayout();
    }

    private void hideSoftKeyboard() {
        if (!solvinKeyboard.showAndroidKeyboard) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    inputMethodManager.hideSoftInputFromWindow(commentLayout.getWindowToken(), 0);
                }
            }, 0);
        }
    }

    private void hideIMM() {
        inputMethodManager.hideSoftInputFromWindow(commentLayout.getWindowToken(), 0);
    }

    private void storeText() {
        selectionStartStore = sheet.getSelectionStart();
        selectionEndStore = sheet.getSelectionEnd();

        clipData = ClipData.newPlainText("Text", sheet.getText().subSequence(selectionStartStore, selectionEndStore));
        clipboardManager.setPrimaryClip(clipData);

        spannableStringBuilder = new SpannableStringBuilder(sheet.getText());
        selectedHTML = applicationTool.storeHTMLText(spannableStringBuilder, selectionStartStore, selectionEndStore);

        toast = Toast.makeText(ActivityCommentSheet.this, "Teks Tersalin", Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();
    }

    private void loadText() {
        removeTextFormatStyle();

        tempRT.setRichTextEditing(true, selectedHTML);
        tempRT.setText(applicationTool.loadTextStyle(tempRT.getText()));
        sheet.getText().insert(sheet.getSelectionStart(), tempRT.getText());

        getTextFormatStyleBack();
    }

    private void removeTextFormatStyle() {
        activedStyleList.clear();
        if (boldActived) {
            sheet.applyEffect(Effects.BOLD, false);
            activedStyleList.add(0);
        }
        if (italicActived) {
            sheet.applyEffect(Effects.ITALIC, false);
            activedStyleList.add(1);
        }
        if (underlineActived) {
            sheet.applyEffect(Effects.UNDERLINE, false);
            activedStyleList.add(2);
        }
        if (strikeThroughActived) {
            sheet.applyEffect(Effects.STRIKETHROUGH, false);
            activedStyleList.add(3);
        }
        if (highlightActived) {
            sheet.applyEffect(Effects.FONTCOLOR, null);
            activedStyleList.add(4);
        }
    }

    private void getTextFormatStyleBack() {
        for (int i = 0; i < activedStyleList.size(); i++) {
            final int index = activedStyleList.get(i);
            if (index == 0) {
                sheet.applyEffect(Effects.BOLD, true);
            } else if (index == 1) {
                sheet.applyEffect(Effects.ITALIC, true);
            } else if (index == 2) {
                sheet.applyEffect(Effects.UNDERLINE, true);
            } else if (index == 3) {
                sheet.applyEffect(Effects.STRIKETHROUGH, true);
            } else if (index == 4) {
                sheet.applyEffect(Effects.FONTCOLOR, getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    private void typeCharacter(SpannableStringBuilder character) {
        sheet.getText().insert(sheet.getSelectionStart(), character);
        if (character.charAt(0) == '\u221A') {
            sheet.setSelection(sheet.getSelectionStart() - 1);
        }
    }

    private void adjustView(int newHeight) {
        layoutParams = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, (int) getResources().getDimension(R.dimen.action_bar_default_height),
                0, newHeight);
        commentLayout.setLayoutParams(layoutParams);
    }

    private void adjustHiddenKeyboard() {
        solvinKeyboard.adjustHiddenKeyboard();
    }

    private boolean IsTextSelected() {
        if (sheet.hasSelection()) {
            removeSelectedText();
            return true;
        } else {
            return false;
        }
    }

    private void removeSelectedText() {
        selectionStart = sheet.getSelectionStart();
        selectionEnd = sheet.getSelectionEnd();

        spannableStringBuilder = new SpannableStringBuilder(sheet.getText());
        spannableStringBuilder.replace(selectionStart, selectionEnd, "");

        sheet.setText(spannableStringBuilder);
        sheet.setSelection(selectionStart);
    }

    private void characterDeletion(int limit, int n) {
        spannableStringBuilder = new SpannableStringBuilder(sheet.getText());
        if (sheet.getSelectionStart() == sheet.getText().length()) {
            if (sheet.getText().length() > limit) {
                if (sheet.getSelectionStart() > 0) {
                    cursorPoint = sheet.getSelectionStart() - n;
                    spannableStringBuilder.replace(cursorPoint, cursorPoint + n, "");

                    sheet.setText(spannableStringBuilder);
                    sheet.setSelection(cursorPoint);
                }
            } else {
                if (sheet.getText().length() > 0) {
                    spannableStringBuilder.replace(0, sheet.getSelectionStart(), "");
                    sheet.setText(spannableStringBuilder);
                }
            }
        } else {
            if (sheet.getSelectionStart() > limit) {
                if (sheet.getSelectionStart() > 0) {
                    cursorPoint = sheet.getSelectionStart() - n;
                    spannableStringBuilder.replace(cursorPoint, cursorPoint + n, "");

                    sheet.setText(spannableStringBuilder);
                    sheet.setSelection(cursorPoint);
                }
            } else {
                if (sheet.getSelectionStart() > 0) {
                    if (sheet.getText().length() > 0) {
                        spannableStringBuilder.replace(0, sheet.getSelectionStart(), "");
                        sheet.setText(spannableStringBuilder);
                    }
                }
            }
        }
    }

    private static void verifyStoragePermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    solvinKeyboard.button_camera.setEnabled(false);
                    solvinKeyboard.button_camera.setAlpha(0.25f);

                    solvinKeyboard.button_upload.setEnabled(false);
                    solvinKeyboard.button_upload.setAlpha(0.25f);
                } else {
                    solvinKeyboard.button_camera.setEnabled(true);
                    solvinKeyboard.button_camera.setAlpha(1f);

                    solvinKeyboard.button_upload.setEnabled(true);
                    solvinKeyboard.button_upload.setAlpha(1f);
                }
            }
        }
    }

    private Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "Camera");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Failed to create" + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");
        return mediaFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        rtManager.onDestroy(isFinishing());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && solvinKeyboard.showSolvinKeyboard) {
            adjustHiddenKeyboard();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (COMMENT_MODE == 0) {
                if (sheet.getText().length() != 0 || bitmap != null) {
                    showConfirmation();
                } else {
                    onBackPressed();
                }
            } else {
                showConfirmation();
            }

            textFormat.hide();
            textFormatShown = false;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showConfirmation() {
        customAlertDialog = new CustomAlertDialog(ActivityCommentSheet.this);
        customAlertDialog.setTitle("Konfirmasi");
        if (COMMENT_MODE == 0) {
            customAlertDialog.setMessage("Anda akan membatalkan kegiatan menyampaikan komentar?");
        } else {
            customAlertDialog.setMessage("Anda akan membatalkan kegiatan mengedit komentar?");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.text_format, menu);

        typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarItemBackground, typedValue, true);

        textFormatIcon = (ImageButton) menu.findItem(R.id.action_text_format).getActionView();
        textFormatIcon.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.action_button_default_width), ViewGroup.LayoutParams.MATCH_PARENT));
        textFormatIcon.setImageResource(R.drawable.ic_format_color_text_light);
        textFormatIcon.setBackgroundResource(typedValue.resourceId);
        textFormatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!textFormatShown) {
                    textFormat.show();
                    textFormatShown = true;
                } else {
                    textFormat.hide();
                    textFormatShown = false;
                }
            }
        });
        ClassCheatSheet.setup(textFormatIcon, getString(R.string.action_text_format));

        return true;
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
        setResult(RESULT_OK);

        if (COMMENT_MODE == 0) {
            sentConfirmedCreated();
        } else {
            sentConfirmedEditted();
        }
        finish();
    }

    @Override
    public void onFailure(String message) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }

        toast = Toast.makeText(ActivityCommentSheet.this, message, Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();
    }

    @Override
    public void onError(List<Error> errorList) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }

        for (Error error : errorList) {
            if (error.getType() == Error.ERROR_CREATE_COMMENT || error.getType() == Error.ERROR_EDIT_COMMENT) {
                customAlertDialog = new CustomAlertDialog(ActivityCommentSheet.this);
                customAlertDialog.setTitle("Pesan");
                customAlertDialog.setMessage(error.getMessage());
                customAlertDialog.setNegativeButton("", null);
                customAlertDialog.setPositiveButton("Tutup", new CustomAlertDialog.OnPositiveClickListener() {
                    @Override
                    public void onClick() {
                        customAlertDialog.dismiss();
                    }
                });
                break;
            }
        }
    }

    class CompressImage extends AsyncTask<Void, Integer, Void> {
        private int compressQuality, target, min, progress;

        @Override
        protected void onPreExecute() {
            byteArrayOutputStream = new ByteArrayOutputStream();

            compressQuality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, byteArrayOutputStream);

            target = applicationTool.targetCompress;
            min = byteArrayOutputStream.size() / 1024;
            progress = min - target;
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

                    progress = byteArrayOutputStream.size() / 1024 - target;
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
            imageProgress.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            image.setImageBitmap(bitmap);

            toast = Toast.makeText(ActivityCommentSheet.this, "Terunggah", Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
            toast.show();

            solvinKeyboard.button_send.setEnabled(true);
            solvinKeyboard.button_send.setAlpha(1f);

            imageForeground.setEnabled(true);
            uploadStatus.setVisibility(View.VISIBLE);
        }
    }
}