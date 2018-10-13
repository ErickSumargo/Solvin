package id.solvinap.dev.view.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaScannerConnection;
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
import android.support.v7.app.AppCompatActivity;
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

import id.solvinap.dev.R;
import id.solvinap.dev.server.api.Connection;
import id.solvinap.dev.server.api.Response;
import id.solvinap.dev.server.data.DataComment;
import id.solvinap.dev.server.helper.Connectivity;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.server.helper.Request;
import id.solvinap.dev.server.interfaces.IAPIRequest;
import id.solvinap.dev.server.interfaces.IBaseResponse;
import id.solvinap.dev.view.helper.Tool;
import id.solvinap.dev.view.widget.CustomAlertDialog;
import id.solvinap.dev.view.widget.CustomCheatSheet;
import id.solvinap.dev.view.widget.CustomProgressDialog;
import id.solvinap.dev.view.widget.CustomRichEditText;
import id.solvinap.dev.view.widget.CustomSolvinKeyboard;
import id.solvinap.dev.view.widget.CustomTextFormat;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 2/2/2017.
 */

public class ActivityCommentEditingSheet extends AppCompatActivity implements IBaseResponse {
    //    INTERFACE
    public interface INoInternet {
        void onRetry();
    }

    public interface OnCommentSent {
        void ISentConfirmedEditted();
    }

    private static OnCommentSent mCommentSent;

    public void setCommentSent(OnCommentSent listener) {
        mCommentSent = listener;
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
    private CustomSolvinKeyboard solvinKeyboard;
    private CustomTextFormat textFormat;
    private Bundle properties;

    private Toolbar toolbar;
    private View imageDivider, emptyView;
    private CoordinatorLayout commentSheet;
    private RelativeLayout imageContainer, imageForeground;
    private ScrollView commentLayout;
    private CustomRichEditText sheet;
    private ImageView image;
    private TextView uploadStatus;
    private ProgressBar imageProgress;
    private ImageButton imageRemove, textFormatIcon;
    private Toast toast;

    private RTApi rtApi;
    private RTManager rtManager;
    private ClipboardManager clipboardManager;
    private ClipData clipData;
    private Intent intent;
    private TypedValue typedValue;
    private CustomProgressDialog customProgressDialog;

    private Bitmap bitmap;
    private Uri uri;
    private static File mediaStorageDir, mediaFile;

    private int height, cursorPoint, selectionStart, selectionEnd,
            selectionStartStore, selectionEndStore, selectionStartPaste, selectionEndPaste;
    private static final int REQUEST_EXTERNAL_STORAGE = 1, CAPTURE_IMAGE = 0, PICK_IMAGE = 1;

    private boolean textFormatShown = false;
    private boolean boldActived = false, italicActived = false, underlineActived = false,
            strikeThroughActived = false, highlightActived = false;

    private List<Integer> activedStyleList;

    private static String[] PERMISSION_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String IMAGE_DIRECTORY_NAME = "Solvin";
    private static String timeStamp;
    private String pasteText, selectedHTML;

    private SpannableStringBuilder spannableStringBuilder;

    //    API CONNECTION
    private IBaseResponse iBaseResponse;
    private IAPIRequest iAPIRequest;

    private Tool tool;
    private File temp;
    private ByteArrayOutputStream byteArrayOutputStream;
    private Map<String, RequestBody> metadata;
    private DataComment dataComment;

    public ActivityCommentEditingSheet() {
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        windowManager = this.getWindow().getWindowManager();
        point = new Point();
        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);

        windowManager.getDefaultDisplay().getSize(point);
        height = point.y;

        solvinKeyboard = new CustomSolvinKeyboard();
        textFormat = new CustomTextFormat();

        commentSheet = (CoordinatorLayout) findViewById(R.id.comment_sheet_layout);
        commentLayout = (ScrollView) findViewById(R.id.comment_layout);
        sheet = (CustomRichEditText) findViewById(R.id.comment_sheet);
        image = (ImageView) findViewById(R.id.comment_image);
        uploadStatus = (TextView) findViewById(R.id.comment_upload_status);
        imageProgress = (ProgressBar) findViewById(R.id.comment_image_progress);
        imageRemove = (ImageButton) findViewById(R.id.comment_image_remove);
        imageContainer = (RelativeLayout) findViewById(R.id.comment_image_container);
        imageForeground = (RelativeLayout) findViewById(R.id.comment_image_foreground);
        imageDivider = findViewById(R.id.comment_image_divider);
        emptyView = findViewById(R.id.empty_view);

        sheet.requestFocus();

        rtManager.registerEditor(sheet, true);

        selectionStart = selectionEnd = -1;
        selectionStartStore = selectionEndStore = -1;
        selectionStartPaste = selectionEndPaste = -1;

        activedStyleList = new ArrayList<>();

        adjustView(height / 12);
        attachTextFormatView();

        iBaseResponse = this;
        iAPIRequest = Connection.getInstance(getApplicationContext()).getiAPIRequest();

        tool = new Tool(getApplicationContext());
        dataComment = (DataComment) getIntent().getSerializableExtra(Global.PREFERENCES_INTENT_EXTRA);

        if (dataComment.getContent().length() > 0) {
            sheet.setRichTextEditing(true, dataComment.getContent());
            sheet.setText(tool.loadTextStyle(sheet.getText()));
        } else {
            sheet.setText("");
        }
        sheet.setSelection(sheet.length());

        if (dataComment.getImage().length() > 0) {
            Picasso.with(getApplicationContext()).load(Global.ASSETS_URL + "comment" + "/" + dataComment.getImage())
                    .placeholder(R.drawable.image_placeholder)
                    .fit()
                    .centerCrop()
                    .into(image);

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
                    commentLayout.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }, 250);
        }
        attachSolvinKeyboardView();
    }

    private void tryEditComment() {
        customProgressDialog = new CustomProgressDialog(ActivityCommentEditingSheet.this);
        customProgressDialog.setMessage("Mengajukan komentar...");
        if (Connectivity.isConnected(getApplicationContext())) {
            editComment();
        } else {
            customProgressDialog.dismiss();
            showNoInternetNotification(new INoInternet() {
                @Override
                public void onRetry() {
                    tryEditComment();
                }
            });
        }
    }

    private void editComment() {
        metadata = new HashMap<>();
        metadata.put("id", Request.getInstance().getText(String.valueOf(dataComment.getId())));
        metadata.put("auth_id", Request.getInstance().getText(String.valueOf(dataComment.getAuthId())));
        metadata.put("auth_type", Request.getInstance().getText(String.valueOf(dataComment.getAuthType())));
        metadata.put("content", Request.getInstance().getText(
                TextUtils.isEmpty(sheet.toString()) ? "" : sheet.getText(RTFormat.HTML)));

        if (uri != null && bitmap != null) {
            temp = tool.getTempFileImage(byteArrayOutputStream);
            metadata.put("image\"; filename=\"image.jpg\" ", Request.getInstance().getImage(temp));
            metadata.put("change_image", Request.getInstance().getText("true"));
        } else {
            if (dataComment.getImage().length() > 0 && image.getDrawable() == null) {
                metadata.put("change_image", Request.getInstance().getText("true"));
            } else {
                metadata.put("change_image", Request.getInstance().getText("false"));
            }
        }
        iAPIRequest.editComment(metadata).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (uri != null && bitmap != null) {
                    tool.deleteTempFileImage(temp);
                }
                if (response.code() == 200) {
                    iBaseResponse.onSuccess(response.body());
                } else {
                    iBaseResponse.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                if (uri != null && bitmap != null) {
                    tool.deleteTempFileImage(temp);
                }
                iBaseResponse.onFailure(t.getMessage());
            }
        });
    }

    private void showNoInternetNotification(final INoInternet iNoInternet) {
        final Snackbar snackbar = Snackbar.make(commentSheet, getResources().getString(R.string.text_no_internet), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Coba Lagi", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNoInternet.onRetry();
            }
        });
        tool.resizeSnackBarWithCallBack(snackbar);
        snackbar.show();
    }

    @Override
    public void onSuccess(Response response) {
        customProgressDialog.dismiss();
        hideIMM();
        finish();

        sentConfirmedEditted();
    }

    @Override
    public void onFailure(String message) {
        customProgressDialog.dismiss();
        hideIMM();

        final Toast toast = Toast.makeText(ActivityCommentEditingSheet.this, message, Toast.LENGTH_SHORT);
        tool.resizeToast(toast);
        toast.show();
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmation();
            }
        });

        textFormat.setOnTextFormatStyle(new CustomTextFormat.OnTextFormatStyle() {
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

        solvinKeyboard.setKeyboardTask(new CustomSolvinKeyboard.OnKeyboardTask() {
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
                showConfirmationSendDialog();
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
                if (bitmap == null || dataComment.getImage().isEmpty()) {
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

        sheet.setOnOverridingContextMenu(new CustomRichEditText.OnOverridingContextMenu() {
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
                        if (sheet.hasSelection()) {
                            selectionStartPaste = sheet.getSelectionStart();
                            selectionEndPaste = sheet.getSelectionEnd();

                            spannableStringBuilder = new SpannableStringBuilder(sheet.getText());
                            spannableStringBuilder.replace(selectionStartPaste, selectionEndPaste, pasteText);

                            sheet.setText(spannableStringBuilder);
                            sheet.setSelection(selectionStartPaste);
                        } else {
                            sheet.getText().insert(sheet.getSelectionStart(), pasteText);
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
                intent = new Intent(getApplication(), ActivityImageFullScreen.class);
                if (uri != null) {
                    intent.putExtra("uri", uri);
                } else {
                    intent.putExtra("image", dataComment.getImage());
                    intent.putExtra("category", "comment");
                }
                startActivity(intent);
            }
        });

        imageRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomAlertDialog customAlertDialog = new CustomAlertDialog(ActivityCommentEditingSheet.this);
                customAlertDialog.setTitle("Konfirmasi");
                customAlertDialog.setMessage("Anda yakin ingin menghapus gambar yang dipilih?");
                customAlertDialog.setPositiveButton("Ya", new CustomAlertDialog.OnPositiveClickListener() {
                    @Override
                    public void onClick() {
                        customAlertDialog.dismiss();

                        toast = Toast.makeText(ActivityCommentEditingSheet.this, "Gambar Dihapus", Toast.LENGTH_SHORT);
                        tool.resizeToast(toast);
                        toast.show();

                        imageProgress.setVisibility(View.GONE);
                        imageDivider.setVisibility(View.GONE);
                        imageContainer.setVisibility(View.GONE);
                        imageForeground.setVisibility(View.GONE);
                        uploadStatus.setVisibility(View.GONE);

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
                customAlertDialog.setNegativeButton("Tidak", new CustomAlertDialog.OnNegativeClickListener() {
                    @Override
                    public void onClick() {
                        customAlertDialog.dismiss();
                    }
                });
            }
        });

        verifyStoragePermission(ActivityCommentEditingSheet.this);
    }

    private void showConfirmationSendDialog() {
        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(ActivityCommentEditingSheet.this);
        customAlertDialog.setTitle("Konfirmasi");
        customAlertDialog.setMessage("Anda akan mengajukan komentar yang telah diedit?");
        customAlertDialog.setPositiveButton("Ya", new CustomAlertDialog.OnPositiveClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();

                tryEditComment();
            }
        });
        customAlertDialog.setNegativeButton("Tidak", new CustomAlertDialog.OnNegativeClickListener() {
            @Override
            public void onClick() {
                customAlertDialog.dismiss();
            }
        });
    }

    private void attachSolvinKeyboardView() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        setButtonSendEnabled();

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

            toast = Toast.makeText(ActivityCommentEditingSheet.this, "Gambar Terambil", Toast.LENGTH_SHORT);
            tool.resizeToast(toast);
            toast.show();

            bitmap = tool.adjustBitmap(tool.resizeBitmap(uri), uri);

            setImpactImageUploaded();
        } else if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            toast = Toast.makeText(ActivityCommentEditingSheet.this, "Gambar Terpilih", Toast.LENGTH_SHORT);
            tool.resizeToast(toast);
            toast.show();

            uri = data.getData();
            bitmap = tool.adjustBitmap(tool.resizeBitmap(uri), uri);

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
        selectedHTML =tool.storeHTMLText(spannableStringBuilder, selectionStartStore, selectionEndStore);

        toast = Toast.makeText(ActivityCommentEditingSheet.this, "Teks Tersalin", Toast.LENGTH_SHORT);
        tool.resizeToast(toast);
        toast.show();
    }

    private void loadText() {
        removeTextFormatStyle();
        sheet.getText().insert(sheet.getSelectionStart(), tool.loadHTMLStyle(selectedHTML));
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

    private int addExtraMargin(int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, getResources().getDisplayMetrics());
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (solvinKeyboard.showSolvinKeyboard) {
                adjustHiddenKeyboard();
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
        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(ActivityCommentEditingSheet.this);
        customAlertDialog.setTitle("Konfirmasi");
        customAlertDialog.setMessage("Anda akan membatalkan kegiatan mengedit komentar?");
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
        CustomCheatSheet.setup(textFormatIcon, getString(R.string.action_text_format));

        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    class CompressImage extends AsyncTask<Void, Integer, Void> {
        private int compressQuality, target, min, progress;

        @Override
        protected void onPreExecute() {
            byteArrayOutputStream = new ByteArrayOutputStream();

            compressQuality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, byteArrayOutputStream);

            target = tool.targetContentCompress;
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

            solvinKeyboard.button_send.setEnabled(true);
            solvinKeyboard.button_send.setAlpha(1f);

            imageForeground.setEnabled(true);
            uploadStatus.setVisibility(View.VISIBLE);
        }
    }
}