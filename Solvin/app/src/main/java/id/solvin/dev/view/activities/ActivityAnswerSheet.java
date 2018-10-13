package id.solvin.dev.view.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.onegravity.rteditor.api.format.RTFormat;
import com.onegravity.rteditor.effects.Effects;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Connectivity;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.DraftSolution;
import id.solvin.dev.model.basic.Error;
import id.solvin.dev.model.basic.Mentor;
import id.solvin.dev.model.basic.Question;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.interfaces.IBaseResponse;
import id.solvin.dev.model.interfaces.OnErrors;
import id.solvin.dev.presenter.QuestionPresenter;
import id.solvin.dev.realm.helper.RealmDraftSolution;
import id.solvin.dev.realm.table.TableImage;
import id.solvin.dev.view.adapters.DraftSolutionViewAdapter;
import id.solvin.dev.view.fragments.FragmentSolvinKeyboard;
import id.solvin.dev.view.fragments.FragmentTextFormat;
import id.solvin.dev.view.widget.ClassCheatSheet;
import id.solvin.dev.view.widget.ClassRichEditText;
import id.solvin.dev.view.widget.CustomAlertDialog;
import id.solvin.dev.view.widget.CustomProgressDialog;
import io.realm.Realm;
import io.realm.RealmList;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.R.attr.src;

/**
 * Created by Erick Sumargo on 9/3/2016.
 */
public class ActivityAnswerSheet extends AppCompatActivity  implements IBaseResponse, OnErrors {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    private static OnSolutionSent mSolutionSent;

    public interface OnSolutionSent {
        void ISentConfirmedCreated();

        void ISentConfirmedEditted();
    }

    public void setSolutionSent(OnSolutionSent listener) {
        mSolutionSent = listener;
    }

    public void sentConfirmedCreated() {
        if (mSolutionSent != null) {
            mSolutionSent.ISentConfirmedCreated();
        }
    }

    public void sentConfirmedEditted() {
        if (mSolutionSent != null) {
            mSolutionSent.ISentConfirmedEditted();
        }
    }

    private CircleImageView userPhoto;
    private ImageView questionImage, questionAttachment, questionStatus;
    private TextView userName, questionTime, questionContent, questionSubject, questionMaterial;

    private ClassRichEditText tempRT;

    private WindowManager windowManager;
    private Point point;
    private CoordinatorLayout.LayoutParams layoutParams;
    private InputMethodManager inputMethodManager;
    private TypedValue typedValue;

    public FragmentSolvinKeyboard solvinKeyboard;
    private FragmentTextFormat textFormat;

    private Toolbar toolbar;
    private View imageDivider, emptyView;
    private CoordinatorLayout answerSheet;
    private RelativeLayout avatarLayout, questionImageForeground, imageContainer, imageForeground,
            questionCategoryContainer, questionImageContainer;
    private ScrollView answerLayout;
    private ClassRichEditText sheet;
    private ImageView image;
    private TextView avatarInitial, uploadStatus;
    private ProgressBar imageProgress;
    private ImageButton imageRemove, textFormatIcon;
    private Toast toast;
    private Snackbar snackbar;

    private RTApi rtApi;
    private RTManager rtManager;
    private ClipboardManager clipboardManager;
    private ClipData clipData;
    private Intent intent;
    private GradientDrawable gradientDrawable;
    private Bundle properties;

    private ClassApplicationTool applicationTool;
    private CustomAlertDialog customAlertDialog;
    private CustomProgressDialog customProgressDialog;

    private Bitmap bitmap, SOLUTION_IMAGE;
    private Uri uri;
    private ByteArrayOutputStream byteArrayOutputStream;
    private static File mediaStorageDir, mediaFile;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private List<Integer> activedStyleList;

    private int userPhotoMode, avatarColor, height, cursorPoint, selectionStart, selectionEnd,
            selectionStartStore, selectionEndStore, selectionStartPaste, selectionEndPaste;
    private int SOLUTION_MODE;
    private static final int REQUEST_EXTERNAL_STORAGE = 1, CAPTURE_IMAGE = 0, PICK_IMAGE = 1;

    private boolean textFormatShown = false;
    private boolean boldActived = false, italicActived = false, underlineActived = false, highlightActived = false, strikeThroughActived = false;

    private CharSequence SOLUTION_CONTENT;
    private static String[] PERMISSION_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String IMAGE_DIRECTORY_NAME = "Solvin";
    private static String timeStamp;
    private String pasteText, selectedHTML;
    private SpannableStringBuilder spannableStringBuilder;

    private Question question;
    private int questionStatusDrawable[] = {id.solvin.dev.R.drawable.ic_pending, id.solvin.dev.R.drawable.ic_discussion, id.solvin.dev.R.drawable.ic_complete};
    private QuestionPresenter questionPresenter;
    private int SOLUTION_POSITION = -1;
    /**
     * Firebase Auth
     */

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    /**
     * Firebase Realtime Database
     */
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceOnline, databaseReferenceQuestion, databaseReferenceMentor;

    //    REALM
    private Realm realm;
    private RealmDraftSolution realmDraftSolution;

    private DraftSolution openedDraftSolution;
    private List<DraftSolution> draftSolutionList;

    private AlertDialog.Builder newFileBuilder, openFileBuider, saveFileBuilder;
    private AlertDialog openFileDialog, saveFileDialog;
    private CustomAlertDialog newFileDialog;

    private RecyclerView draftSolutionView;
    private DraftSolutionViewAdapter draftSolutionViewAdapter;
    private LinearLayoutManager layoutManager;

    private AutoCompleteTextView titleFile;
    private SimpleDateFormat dateFormat;

    private int draftId;
    private boolean draftOpened = false, draftReset = false;
    private RealmList<TableImage> images;

    private MenuItem openFileMenuItem;
    private View openFileMenuView;
    private ImageButton newFileIcon, openFileIcon, saveFileIcon;
    private LinearLayout draftIndicator;

    public ActivityAnswerSheet() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(id.solvin.dev.R.layout.activity_answer_sheet);
        rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
        rtManager = new RTManager(rtApi, savedInstanceState);

        init();
        setEvent();
    }

    private void init() {
        questionPresenter = new QuestionPresenter(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(id.solvin.dev.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        answerSheet = (CoordinatorLayout) findViewById(id.solvin.dev.R.id.answer_sheet_layout);
        answerLayout = (ScrollView) findViewById(id.solvin.dev.R.id.answer_layout);
        sheet = (ClassRichEditText) findViewById(id.solvin.dev.R.id.answer_sheet);
        image = (ImageView) findViewById(id.solvin.dev.R.id.answer_image);
        uploadStatus = (TextView) findViewById(id.solvin.dev.R.id.answer_upload_status);
        avatarLayout = (RelativeLayout) findViewById(id.solvin.dev.R.id.answer_avatar_layout);
        imageContainer = (RelativeLayout) findViewById(id.solvin.dev.R.id.answer_image_container);
        imageForeground = (RelativeLayout) findViewById(id.solvin.dev.R.id.answer_image_foreground);
        imageProgress = (ProgressBar) findViewById(id.solvin.dev.R.id.answer_image_progress);
        imageRemove = (ImageButton) findViewById(id.solvin.dev.R.id.answer_image_remove);
        imageDivider = findViewById(id.solvin.dev.R.id.answer_image_divider);
        emptyView = findViewById(id.solvin.dev.R.id.empty_view);

        userPhoto = (CircleImageView) findViewById(id.solvin.dev.R.id.answer_user_photo);
        userName = (TextView) findViewById(id.solvin.dev.R.id.answer_user_name);
        avatarInitial = (TextView) findViewById(id.solvin.dev.R.id.answer_avatar_initial);
        questionTime = (TextView) findViewById(id.solvin.dev.R.id.answer_question_time);
        questionAttachment = (ImageView) findViewById(id.solvin.dev.R.id.answer_question_attachment);
        questionStatus = (ImageView) findViewById(id.solvin.dev.R.id.answer_question_status);
        questionContent = (TextView) findViewById(id.solvin.dev.R.id.answer_question_content);
        questionImage = (ImageView) findViewById(id.solvin.dev.R.id.answer_question_image);
        questionSubject = (TextView) findViewById(id.solvin.dev.R.id.answer_question_subject);
        questionMaterial = (TextView) findViewById(id.solvin.dev.R.id.answer_question_material);
        questionCategoryContainer = (RelativeLayout) findViewById(id.solvin.dev.R.id.answer_question_category_container);
        questionImageContainer = (RelativeLayout) findViewById(id.solvin.dev.R.id.answer_question_image_container);
        questionImageForeground = (RelativeLayout) findViewById(id.solvin.dev.R.id.answer_question_image_foreground);

        tempRT = (ClassRichEditText) findViewById(id.solvin.dev.R.id.temp_rt);
        rtManager.registerEditor(tempRT, true);

        windowManager = this.getWindow().getWindowManager();
        point = new Point();
        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);

        applicationTool = new ClassApplicationTool(getApplicationContext());

        windowManager.getDefaultDisplay().getSize(point);
        height = point.y;

        solvinKeyboard = new FragmentSolvinKeyboard();
        textFormat = new FragmentTextFormat();

        sheet.requestFocus();
        rtManager.registerEditor(sheet, true);

        dateFormat = new SimpleDateFormat("EEE, dd MMM ''yy-HH:mm");

        SOLUTION_MODE = getIntent().getExtras().getInt("SOLUTION_MODE");
        SOLUTION_POSITION = getIntent().getExtras().getInt(Global.get().key().QUESTION_DATA_POSITION);

        question = (Question) getIntent().getSerializableExtra(Global.get().key().QUESTION_DATA);

        if (question.getStudent().getPhoto().isEmpty()) {
            avatarColor = ClassApplicationTool.with(getApplicationContext()).getAvatarColor(question.getStudent().getId());
            gradientDrawable = (GradientDrawable) avatarLayout.getBackground();
            gradientDrawable.setColor(avatarColor);

            avatarInitial.setText(Global.get().getInitialName(question.getStudent().getName()));

            avatarLayout.setVisibility(View.VISIBLE);
            userPhoto.setVisibility(View.GONE);
        } else {
            Picasso.with(getApplicationContext()).load(Global.get().getAssetURL(question.getStudent().getPhoto(), Auth.AUTH_TYPE_STUDENT))
                    .placeholder(id.solvin.dev.R.drawable.operator)
                    .fit()
                    .centerCrop()
                    .into(userPhoto);

            avatarLayout.setVisibility(View.GONE);
            userPhoto.setVisibility(View.VISIBLE);
        }
        userName.setText(question.getStudent().getName());
        questionTime.setText(Global.get().convertDateToFormat(question.getCreated_at(), "EEE, dd MMM ''yy-HH:mm") + " WIB");

        questionAttachment.setVisibility(TextUtils.isEmpty(question.getImage()) ? View.GONE : View.VISIBLE);
        questionImageContainer.setVisibility(TextUtils.isEmpty(question.getImage()) ? View.GONE : View.VISIBLE);

        questionStatus.setImageResource(questionStatusDrawable[question.getStatus()]);

        if (!question.getContent().isEmpty()) {
            tempRT.setRichTextEditing(true, question.getContent());
            tempRT.setText(ClassApplicationTool.with(getApplicationContext()).loadTextStyle(tempRT.getText()));

            questionContent.setText(tempRT.getText());
            questionContent.setVisibility(View.VISIBLE);
        } else {
            questionContent.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(question.getImage())) {
            Picasso.with(getApplicationContext()).load(Global.get().getAssetURL(question.getImage(), "question"))
                    .placeholder(id.solvin.dev.R.drawable.image_placeholder)
                    .fit()
                    .centerCrop()
                    .into(questionImage);
            questionImage.setVisibility(View.VISIBLE);
        }

        questionSubject.setText(question.getMaterial().getSubject().getName());
        if (question.getOther().isEmpty()) {
            questionMaterial.setText(question.getMaterial().getName());
        } else {
            questionMaterial.setText(question.getOther());
        }
        if (question.getMaterial().getSubject().getName().equals("Matematika")) {
            questionCategoryContainer.setBackgroundColor(getResources().getColor(R.color.colorMathematics));
        } else if (question.getMaterial().getSubject().getName().equals("Fisika")) {
            questionCategoryContainer.setBackgroundColor(getResources().getColor(R.color.colorPhysics));
        } else {
            questionCategoryContainer.setBackgroundColor(getResources().getColor(R.color.colorChemistry));
        }

        selectionStart = selectionEnd = -1;
        selectionStartStore = selectionEndStore = -1;
        selectionStartPaste = selectionEndPaste = -1;

        activedStyleList = new ArrayList<>();

        adjustView(height / 12);
        attachTextFormatView();

        if (SOLUTION_MODE == 1) {
            setTitle("Edit Solusi");
            if (question.getSolutions().get(SOLUTION_POSITION).getContent().length() > 0) {
                sheet.setRichTextEditing(true, question.getSolutions().get(SOLUTION_POSITION).getContent());
                sheet.setText(applicationTool.loadTextStyle(sheet.getText()));
            } else {
                sheet.setText("");
            }
            sheet.setSelection(sheet.length());

            if (!TextUtils.isEmpty(question.getSolutions().get(SOLUTION_POSITION).getImage())) {
                Picasso.with(getApplicationContext()).load(Global.get().getAssetURL(question.getSolutions().get(SOLUTION_POSITION).getImage(), "solution"))
                        .placeholder(id.solvin.dev.R.drawable.image_placeholder)
                        .fit()
                        .centerCrop()
                        .into(image);
                image.setVisibility(View.VISIBLE);

                imageDivider.setVisibility(View.VISIBLE);
                imageProgress.setVisibility(View.INVISIBLE);
                uploadStatus.setVisibility(View.INVISIBLE);
                imageContainer.setVisibility(View.VISIBLE);
                imageForeground.setVisibility(View.VISIBLE);

                emptyView.getLayoutParams().height = addExtraMargin(20);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        answerLayout.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 250);
            }
        }

        attachSolvinKeyboardView(SOLUTION_MODE);
    }

    private void setEvent() {
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SOLUTION_MODE == 0) {
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
                    sheet.applyEffect(Effects.FONTCOLOR, getResources().getColor(id.solvin.dev.R.color.colorPrimary));
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
                customAlertDialog = new CustomAlertDialog(ActivityAnswerSheet.this);
                customAlertDialog.setTitle("Konfirmasi");
                if (SOLUTION_MODE == 0) {
                    customAlertDialog.setMessage("Anda akan mengajukan solusi yang telah dibuat?");
                } else {
                    customAlertDialog.setMessage("Anda akan mengajukan solusi yang telah diedit?");
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
                        hideIMM();

                        sendSolution();
                    }
                });
            }
        });

        questionImageForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplication(), ActivityFullScreen.class);
                intent.putExtra("image", question.getImage());
                intent.putExtra("category", "question");
                startActivity(intent);
            }
        });

        answerSheet.setOnTouchListener(new View.OnTouchListener() {
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
                if (SOLUTION_MODE == 0) {
                    if (bitmap == null) {
                        if (s.length() != 0 && !s.toString().matches("[\\n\\r]+")) {
                            saveFileIcon.setEnabled(true);
                            saveFileIcon.setAlpha(1f);

                            solvinKeyboard.button_send.setEnabled(true);
                            solvinKeyboard.button_send.setAlpha(1f);
                        } else {
                            saveFileIcon.setEnabled(false);
                            saveFileIcon.setAlpha(0.25f);

                            solvinKeyboard.button_send.setEnabled(false);
                            solvinKeyboard.button_send.setAlpha(0.25f);
                        }
                    }
                } else {
                    if (bitmap == null || question.getSolutions().get(SOLUTION_POSITION).getImage().isEmpty()) {
                        if (s.length() != 0 && !s.toString().matches("[\\n\\r]+")) {
                            if (draftOpened || draftReset) {
                                saveFileIcon.setEnabled(true);
                                saveFileIcon.setAlpha(1f);
                            }
                            solvinKeyboard.button_send.setEnabled(true);
                            solvinKeyboard.button_send.setAlpha(1f);
                        } else {
                            if (draftOpened || draftReset) {
                                saveFileIcon.setEnabled(false);
                                saveFileIcon.setAlpha(0.25f);
                            }
                            if (image.getDrawable() == null) {
                                solvinKeyboard.button_send.setEnabled(false);
                                solvinKeyboard.button_send.setAlpha(0.25f);
                            }
                        }
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
                    if (openedDraftSolution != null) {
                        intent.putExtra("id_index", openedDraftSolution.getId() + "_" + "0");
                    } else {
                        intent.putExtra("image", question.getSolutions().get(SOLUTION_POSITION).getImage());
                        intent.putExtra("category", "solution");
                    }
                }
                startActivity(intent);
            }
        });

        imageRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog = new CustomAlertDialog(ActivityAnswerSheet.this);
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

                        toast = Toast.makeText(ActivityAnswerSheet.this, "Gambar Dihapus", Toast.LENGTH_SHORT);
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
                            if (SOLUTION_MODE == 0) {
                                saveFileIcon.setEnabled(false);
                                saveFileIcon.setAlpha(0.25f);
                            } else {
                                if (draftOpened || draftReset) {
                                    saveFileIcon.setEnabled(false);
                                    saveFileIcon.setAlpha(0.25f);
                                }
                            }

                            solvinKeyboard.button_send.setEnabled(false);
                            solvinKeyboard.button_send.setAlpha(0.25f);
                        } else {
                            if (SOLUTION_MODE == 0) {
                                saveFileIcon.setEnabled(true);
                                saveFileIcon.setAlpha(1f);
                            } else {
                                if (draftOpened || draftReset) {
                                    saveFileIcon.setEnabled(true);
                                    saveFileIcon.setAlpha(1f);
                                }
                            }

                            solvinKeyboard.button_send.setEnabled(true);
                            solvinKeyboard.button_send.setAlpha(1f);
                        }
                    }
                });
            }
        });

        verifyStoragePermission(ActivityAnswerSheet.this);
    }

    private void sendSolution() {
        customProgressDialog = new CustomProgressDialog(ActivityAnswerSheet.this);
        customProgressDialog.setMessage("Mengajukan solusi...");

        if (Connectivity.isConnected(getApplicationContext())) {
            if (SOLUTION_MODE == 0) {
                questionPresenter.doAddSolution(question, uri, bitmap, sheet,
                        applicationTool, byteArrayOutputStream, getApplicationContext(), this);
            } else {
                questionPresenter.doEditSolution(question.getSolutions().get(SOLUTION_POSITION), uri, image, bitmap,
                        sheet, applicationTool, byteArrayOutputStream, getApplicationContext(), this);
            }
        } else {
            customProgressDialog.dismiss();

            showNotificationNetwork(new INoInternet() {
                @Override
                public void onRetry() {
                    sendSolution();
                }
            });
        }
    }

    private void showNotificationNetwork(final INoInternet iNoInternet) {
        snackbar = Snackbar.make(answerSheet, getResources().getString(id.solvin.dev.R.string.text_no_internet), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Coba Lagi", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNoInternet.onRetry();
            }
        });
        applicationTool.resizeSnackBarWithCallBack(snackbar);
        snackbar.show();
    }

    private int addExtraMargin(int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, getResources().getDisplayMetrics());
    }

    private void attachSolvinKeyboardView(int SOLUTION_MODE) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (SOLUTION_MODE == 1) {
            setButtonSendEnabled();
        }
        fragmentTransaction.add(id.solvin.dev.R.id.solvin_keyboard_layout, solvinKeyboard);
        fragmentTransaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
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
        fragmentTransaction.add(id.solvin.dev.R.id.text_format_layout, textFormat);
        fragmentTransaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
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
            if (mediaFile != null) {
                MediaScannerConnection.scanFile(getApplicationContext(),
                        new String[]{mediaFile.getPath()}, new String[]{"image/jpg"}, null);
            }
            toast = Toast.makeText(ActivityAnswerSheet.this, "Gambar Terambil", Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
            toast.show();

            bitmap = applicationTool.adjustBitmap(applicationTool.resizeBitmap(uri), uri);
            setImpactImageUploaded();
        } else if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            toast = Toast.makeText(ActivityAnswerSheet.this, "Gambar Terpilih", Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
            toast.show();

            uri = data.getData();
            bitmap = applicationTool.adjustBitmap(applicationTool.resizeBitmap(uri), uri);

            setImpactImageUploaded();
        }
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
                    inputMethodManager.hideSoftInputFromWindow(answerLayout.getWindowToken(), 0);
                }
            }, 0);
        }
    }

    private void hideIMM() {
        inputMethodManager.hideSoftInputFromWindow(answerLayout.getWindowToken(), 0);
    }

    private void storeText() {
        selectionStartStore = sheet.getSelectionStart();
        selectionEndStore = sheet.getSelectionEnd();

        clipData = ClipData.newPlainText("Text", sheet.getText().subSequence(selectionStartStore, selectionEndStore));
        clipboardManager.setPrimaryClip(clipData);

        spannableStringBuilder = new SpannableStringBuilder(sheet.getText());
        selectedHTML = applicationTool.storeHTMLText(spannableStringBuilder, selectionStartStore, selectionEndStore);

        toast = Toast.makeText(ActivityAnswerSheet.this, "Teks Tersalin", Toast.LENGTH_SHORT);
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
                sheet.applyEffect(Effects.FONTCOLOR, getResources().getColor(id.solvin.dev.R.color.colorPrimary));
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
        layoutParams.setMargins(0, (int) getResources().getDimension(id.solvin.dev.R.dimen.action_bar_default_height),
                0, newHeight);
        answerLayout.setLayoutParams(layoutParams);
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

    private void setImpactImageUploaded() {
        newFileIcon.setEnabled(false);
        newFileIcon.setAlpha(0.25f);

        openFileIcon.setEnabled(false);
        openFileIcon.setAlpha(0.25f);

        saveFileIcon.setEnabled(false);
        saveFileIcon.setAlpha(0.25f);

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
                answerLayout.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 250);

        new CompressImage().execute();                                              //COMPRESS IMAGE
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && solvinKeyboard.showSolvinKeyboard) {
            adjustHiddenKeyboard();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (SOLUTION_MODE == 0) {
                if (sheet.getText().length() != 0 || bitmap != null) {
                    showConfirmation();
                } else {
                    onBackPressed();
                }
            } else {
                showConfirmation();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showConfirmation() {
        customAlertDialog = new CustomAlertDialog(ActivityAnswerSheet.this);
        customAlertDialog.setTitle("Konfirmasi");
        if (SOLUTION_MODE == 0) {
            customAlertDialog.setMessage("Anda akan membatalkan kegiatan mengajukan solusi?");
        } else {
            customAlertDialog.setMessage("Anda akan membatalkan kegiatan mengedit solusi?");
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

    private void showNewFileDialog() {
        customAlertDialog = new CustomAlertDialog(ActivityAnswerSheet.this);
        customAlertDialog.setTitle("Konfirmasi");
        customAlertDialog.setMessage("Pembuatan draft baru tidak akan menyimpan draft aktif saat ini");
        customAlertDialog.setNegativeButton("Batal", new CustomAlertDialog.OnNegativeClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();
            }
        });
        customAlertDialog.setPositiveButton("Lanjut", new CustomAlertDialog.OnPositiveClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();
                openNewFile();

                openedDraftSolution = null;
                draftId = -1;
                draftReset = true;
                draftOpened = false;
                images = new RealmList<>();
                draftIndicator.setVisibility(View.GONE);

                toast = Toast.makeText(ActivityAnswerSheet.this, "Draft Baru", Toast.LENGTH_SHORT);
                applicationTool.resizeToast(toast);
                toast.show();
            }
        });
    }

    private void openNewFile() {
        sheet.setText("");

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

        saveFileIcon.setEnabled(false);
        saveFileIcon.setAlpha(0.25f);

        if (sheet.length() == 0) {
            solvinKeyboard.button_send.setEnabled(false);
            solvinKeyboard.button_send.setAlpha(0.25f);
        } else {
            solvinKeyboard.button_send.setEnabled(true);
            solvinKeyboard.button_send.setAlpha(1f);
        }
    }

    private void showOpenFileDialog() {
        openFileBuider = new AlertDialog.Builder(ActivityAnswerSheet.this);
        openFileBuider.setPositiveButton("Buka", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        openFileBuider.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        openFileDialog = openFileBuider.create();
        openFileDialog.setView(openFileDialog.getLayoutInflater().inflate(R.layout.activity_open_file, null));
        openFileDialog.setCanceledOnTouchOutside(false);
        openFileDialog.show();
        applicationTool.resizeAlertDialog(openFileDialog);

        if (!draftOpened) {
            openFileDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }

        draftSolutionView = (RecyclerView) openFileDialog.findViewById(R.id.draft_solution_view);
        layoutManager = new LinearLayoutManager(this);

        if (openedDraftSolution == null) {
            draftSolutionViewAdapter = new DraftSolutionViewAdapter(realmDraftSolution, 1, draftOpened);
        } else {
            draftSolutionViewAdapter = new DraftSolutionViewAdapter(realmDraftSolution, openedDraftSolution.getId(), draftOpened);
        }

        draftSolutionView.setLayoutManager(layoutManager);
        draftSolutionView.setAdapter(draftSolutionViewAdapter);

        draftSolutionViewAdapter.setOnGetCheckedDraftPosition(new DraftSolutionViewAdapter.OnGetCheckedDraftPosition() {
            @Override
            public void IGetCheckedDraftPosition(int id) {
                draftId = id;
                openFileDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
        });

        draftSolutionViewAdapter.setOnCloseOpenFileDialog(new DraftSolutionViewAdapter.OnCloseOpenFileDialog() {
            @Override
            public void ICloseOpenFileDialog() {
                openFileDialog.dismiss();

                openFileIcon.setEnabled(false);
                openFileIcon.setAlpha(0.25f);
            }
        });

        openFileDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileDialog.dismiss();
            }
        });

        openFileDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileDialog.dismiss();
                openFile();
            }
        });
    }

    private void openFile() {
        if (openedDraftSolution != null && openedDraftSolution.getId() == draftId) {
            toast = Toast.makeText(ActivityAnswerSheet.this, "Draft Sedang Dibuka", Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
            toast.show();
        } else {
            openNewFile();

            saveFileIcon.setEnabled(true);
            saveFileIcon.setAlpha(1f);

            openedDraftSolution = realmDraftSolution.find(draftId);
            sheet.setRichTextEditing(true, openedDraftSolution.getContent());
            sheet.setText(applicationTool.loadTextStyle(sheet.getText()));
            sheet.setSelection(sheet.length());

            if (openedDraftSolution.getImages().size() > 0) {
                final byte[] bytes = Base64.decode(openedDraftSolution.getImages().get(0).getImage(), 0);

                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(bitmap);

                image.setVisibility(View.VISIBLE);

                imageDivider.setVisibility(View.VISIBLE);
                imageProgress.setVisibility(View.INVISIBLE);
                uploadStatus.setVisibility(View.INVISIBLE);
                imageContainer.setVisibility(View.VISIBLE);
                imageForeground.setVisibility(View.VISIBLE);

                emptyView.getLayoutParams().height = addExtraMargin(20);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        answerLayout.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 250);
            }
            draftOpened = true;
            draftReset = true;
            draftIndicator.setVisibility(View.VISIBLE);

            toast = Toast.makeText(ActivityAnswerSheet.this, "Draft Terbuka", Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
            toast.show();
        }
    }

    private void showSaveFileDialog() {
        saveFileBuilder = new AlertDialog.Builder(ActivityAnswerSheet.this);
        saveFileBuilder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        saveFileBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        saveFileDialog = saveFileBuilder.create();
        saveFileDialog.setView(saveFileDialog.getLayoutInflater().inflate(R.layout.activity_save_file, null));
        saveFileDialog.setCanceledOnTouchOutside(false);
        saveFileDialog.show();
        applicationTool.resizeAlertDialog(saveFileDialog);

        saveFileDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        titleFile = (AutoCompleteTextView) saveFileDialog.findViewById(R.id.save_file_title);

        titleFile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    saveFileDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    saveFileDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        saveFileDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFileDialog.dismiss();
            }
        });

        saveFileDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFileDialog.dismiss();
                saveNewFile();
            }
        });
    }

    private void saveNewFile() {
        customProgressDialog = new CustomProgressDialog(ActivityAnswerSheet.this);
        customProgressDialog.setMessage("Menyimpan solusi...");

        DraftSolution draftSolution = new DraftSolution();
        draftSolution.setId(realmDraftSolution.getId());
        draftSolution.setTitle(titleFile.getText().toString().trim());
        draftSolution.setContent(sheet.getText(RTFormat.HTML));

        final TableImage meta = new TableImage();
        images = new RealmList<>();
        if (bitmap != null) {
            meta.setDraft_id(draftSolution.getId());
            meta.setImage(Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
            images.add(meta);
        }
        draftSolution.setImages(images);

        draftSolution.setUpdated_at(dateFormat.format(Calendar.getInstance().getTime()));

        realmDraftSolution.saveNew(draftSolution);
        openedDraftSolution = draftSolution;

        draftOpened = true;

        if (!openFileIcon.isEnabled()) {
            openFileIcon.setEnabled(true);
            openFileIcon.setAlpha(1f);
        }
        draftIndicator.setVisibility(View.VISIBLE);

        customProgressDialog.dismiss();

        toast = Toast.makeText(ActivityAnswerSheet.this, "Solusi Tersimpan", Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();
    }

    private void saveFile() {
        DraftSolution draftSolution = new DraftSolution();
        draftSolution.setId(openedDraftSolution.getId());
        draftSolution.setTitle(openedDraftSolution.getTitle());
        draftSolution.setContent(sheet.getText(RTFormat.HTML));

        final TableImage meta = new TableImage();
        images = new RealmList<>();
        if (bitmap != null) {
            meta.setDraft_id(draftSolution.getId());
            meta.setImage(byteArrayOutputStream != null ?
                    Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
                    : openedDraftSolution.getImages().get(0).getImage());
            images.add(meta);
        }
        draftSolution.setImages(images);
        draftSolution.setUpdated_at(dateFormat.format(Calendar.getInstance().getTime()));
        realmDraftSolution.save(draftSolution);

        toast = Toast.makeText(ActivityAnswerSheet.this, "Solusi Tersimpan", Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.solution, menu);

        realm = Realm.getDefaultInstance();
        realmDraftSolution = new RealmDraftSolution(realm);
        if (realmDraftSolution.retrieve().size() == 0) {
            draftSolutionList = new ArrayList<>();
        } else {
            draftSolutionList = realmDraftSolution.retrieve();
        }
        images = new RealmList<>();

        typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarItemBackground, typedValue, true);

        newFileIcon = (ImageButton) menu.findItem(R.id.action_new_file).getActionView();
        newFileIcon.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(id.solvin.dev.R.dimen.action_button_default_width), ViewGroup.LayoutParams.MATCH_PARENT));
        newFileIcon.setImageResource(R.drawable.ic_plus_light);
        newFileIcon.setBackgroundResource(typedValue.resourceId);
        newFileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (draftOpened) {
                    showNewFileDialog();
                } else {
                    draftReset = true;
                    openNewFile();
                }
            }
        });
        ClassCheatSheet.setup(newFileIcon, getString(R.string.action_new_file));

        openFileMenuItem = menu.findItem(R.id.action_open_file);
        openFileMenuView = MenuItemCompat.getActionView(openFileMenuItem);
        openFileIcon = (ImageButton) openFileMenuView.findViewById(R.id.open_file_icon);
        draftIndicator = (LinearLayout) openFileMenuView.findViewById(R.id.draft_indicator);
        openFileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOpenFileDialog();
            }
        });
        ClassCheatSheet.setup(openFileIcon, getString(R.string.action_open_file));

        if (draftSolutionList.size() == 0) {
            openFileIcon.setEnabled(false);
            openFileIcon.setAlpha(0.25f);
        } else {
            openFileIcon.setEnabled(true);
            openFileIcon.setAlpha(1f);
        }

        saveFileIcon = (ImageButton) menu.findItem(R.id.action_save_file).getActionView();
        saveFileIcon.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(id.solvin.dev.R.dimen.action_button_default_width), ViewGroup.LayoutParams.MATCH_PARENT));
        saveFileIcon.setImageResource(R.drawable.ic_content_save_light);
        saveFileIcon.setBackgroundResource(typedValue.resourceId);
        saveFileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!draftOpened) {
                    showSaveFileDialog();
                } else {
                    saveFile();
                }
            }
        });
        saveFileIcon.setEnabled(false);
        saveFileIcon.setAlpha(0.25f);
        ClassCheatSheet.setup(saveFileIcon, getString(R.string.action_save_file));

        textFormatIcon = (ImageButton) menu.findItem(id.solvin.dev.R.id.action_text_format).getActionView();
        textFormatIcon.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(id.solvin.dev.R.dimen.action_button_default_width), ViewGroup.LayoutParams.MATCH_PARENT));
        textFormatIcon.setImageResource(id.solvin.dev.R.drawable.ic_format_color_text_light);
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
        ClassCheatSheet.setup(textFormatIcon, getString(id.solvin.dev.R.string.action_text_format));

        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        firebaseLogout();
        super.onDestroy();
        rtManager.onDestroy(isFinishing());
    }

    @Override
    protected void onStop() {
        firebaseLogout();

        super.onStop();
    }

    @Override
    public void onSuccess(Response response) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }
        setResult(RESULT_OK);
        finish();

        if (SOLUTION_MODE == 0) {
            sentConfirmedCreated();
        } else {
            sentConfirmedEditted();
        }
    }

    @Override
    public void onFailure(String message) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }

        toast = Toast.makeText(ActivityAnswerSheet.this, message, Toast.LENGTH_SHORT);
        applicationTool.resizeToast(toast);
        toast.show();
    }

    @Override
    public void onError(List<Error> errorList) {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }

        for (Error error : errorList) {
            if (error.getType() == Error.ERROR_CREATE_SOLUTION || error.getType() == Error.ERROR_EDIT_SOLUTION) {
                customAlertDialog = new CustomAlertDialog(ActivityAnswerSheet.this);
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

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
        firebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Mentor mentor = (Mentor) Session.with(getApplicationContext()).getAuth();
                    databaseReferenceOnline = firebaseDatabase.getReference("online");
                    databaseReferenceOnline.child(String.valueOf(mentor.getId())).setValue(mentor.getName());

                    databaseReferenceQuestion = firebaseDatabase.getReference("question")
                            .child(String.valueOf(question.getId()));

                    databaseReferenceMentor = databaseReferenceQuestion
                            .child("mentor")
                            .child(String.valueOf(mentor.getId()));
                    databaseReferenceMentor.child("image").setValue(mentor.getPhoto());
                    databaseReferenceMentor.child("name").setValue(mentor.getName());
                }
            }
        });
    }

    private void firebaseLogout() {
        if (Connectivity.isConnected(getApplicationContext())) {
            if (databaseReferenceOnline != null) {
                databaseReferenceOnline
                        .child(String.valueOf(Session.with(getApplicationContext()).getAuth().getId()))
                        .removeValue();
                databaseReferenceMentor.removeValue();
                firebaseAuth.signOut();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseLogout();
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
//            image.setImageBitmap(bitmap);

            toast = Toast.makeText(ActivityAnswerSheet.this, "Terunggah", Toast.LENGTH_SHORT);
            applicationTool.resizeToast(toast);
            toast.show();

            newFileIcon.setEnabled(true);
            newFileIcon.setAlpha(1f);

            openFileIcon.setEnabled(true);
            openFileIcon.setAlpha(1f);

            if (SOLUTION_MODE == 0) {
                saveFileIcon.setEnabled(true);
                saveFileIcon.setAlpha(1f);
            } else {
                if (draftOpened || draftReset) {
                    saveFileIcon.setEnabled(true);
                    saveFileIcon.setAlpha(1f);
                }
            }

            solvinKeyboard.button_send.setEnabled(true);
            solvinKeyboard.button_send.setAlpha(1f);

            imageForeground.setEnabled(true);
            uploadStatus.setVisibility(View.VISIBLE);

            addWatermark(bitmap);
        }
    }

    private void addWatermark(Bitmap bitmap) {
        Bitmap watermarked = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

        Canvas canvas = new Canvas(watermarked);
        Paint paint = new Paint();
        paint.setAlpha(255);
        paint.setAntiAlias(true);

        canvas.drawBitmap(bitmap, 0, 0, null);

        Bitmap waterMark = BitmapFactory.decodeResource(getResources(), R.drawable.watermark);
        canvas.drawBitmap(waterMark, 0, canvas.getHeight() - waterMark.getHeight(), paint);

        image.setImageBitmap(watermarked);
    }
}