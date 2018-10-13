package id.solvin.dev.helper;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.solvin.dev.R;
import id.solvin.dev.view.widget.CustomTypefaceSpan;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Erick Sumargo on 10/12/2016.
 */
public class ClassApplicationTool {
    private Context context;
    private TextView alertTitle, alertMessage, toastMessage, snackMessage;
    private LinearLayout.LayoutParams alertParams;
    private Button snackAction;

    private ImageLoader imageLoader;

    public static final String PREFERENCES = "PREFERENCES";
    public static final String FIRST_LAUNCH = "FIRST_LAUNCH";
    public static final String DATE_CURRENT_LAUNCH = "DATE_CURRENT_LAUNCH";
    public static final String NEXT_PROMPT = "NEXT_PROMPT";
    public final static int SCHEDULE_PROMPT = 3;    // n hari ke depan

    public static int targetCompress = 1024;
    private int sum, indexPath, orientation, degrees;
    private int colors[];
    private String RpCurrency, resultPath, selectedHTML;
    private SpannableStringBuilder selectedSpannableString, tempStringBuilder, specialSpannableChar;
    private Bitmap adjustedBitmap;

    private Cursor cursor;
    private ExifInterface exifInterface;
    private Matrix matrix;
    private Typeface specialFont;
    private DecimalFormat RpFormat;
    private DecimalFormatSymbols RpFormatSymbols;

    private SubscriptSpan[] subSpan;
    private SuperscriptSpan[] superSpan;

    private Date timestamp;
    private SimpleDateFormat timestampFormat, dateTimeFormat, dateFormat, timeFormat;
    private String date, time;

    private File tempDirectory, temp;
    private ByteArrayOutputStream byteArrayOutputStream;

    public ClassApplicationTool(Context context) {
        this.context = context;
    }

    public String convertRpCurrency(int money) {
        RpFormatSymbols = new DecimalFormatSymbols();
        RpFormatSymbols.setCurrencySymbol("Rp. ");
        RpFormatSymbols.setGroupingSeparator('.');

        RpFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        RpFormat.setDecimalFormatSymbols(RpFormatSymbols);
        RpFormat.setMaximumFractionDigits(0);

        RpCurrency = RpFormat.format(money);
        RpCurrency = RpCurrency.substring(0, RpCurrency.length()) + ",-";

        return RpCurrency;
    }

    public void resizeAlertDialog(AlertDialog alertDialog) {
        alertParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        alertParams.setMargins(
                (int) context.getResources().getDimension(R.dimen.alert_dialog_default_extra_margin),
                (int) context.getResources().getDimension(R.dimen.alert_dialog_default_extra_margin),
                (int) context.getResources().getDimension(R.dimen.alert_dialog_default_extra_margin),
                (int) context.getResources().getDimension(R.dimen.alert_dialog_default_extra_margin));

        alertTitle = (TextView) alertDialog.findViewById(R.id.alertTitle);
        if (alertTitle != null) {
            alertTitle.setLayoutParams(alertParams);
            alertTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.default_font_size_20m));
        }

        alertMessage = (TextView) alertDialog.findViewById(android.R.id.message);
        if (alertMessage != null) {
            alertMessage.setLayoutParams(alertParams);
            alertMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.default_font_size_16m));
        }

        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setLayoutParams(alertParams);
        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.default_font_size_14m));

        alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setLayoutParams(alertParams);
        alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.default_font_size_14m));
    }

    public void resizeToast(Toast toast) {
        toastMessage = (TextView) ((ViewGroup) toast.getView()).getChildAt(0);
        toastMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.default_font_size_14m));
    }

    public void resizeSnackBar(Snackbar snackbar, int mode) {
        snackMessage = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.default_font_size_14m));
        snackMessage.setTextColor(context.getResources().getColor(R.color.white));
        if (mode == 1) {
            snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        } else if (mode == 2) {
            snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.colorCancelSubDark));
        }
    }

    public void resizeSnackBarWithCallBack(Snackbar snackbar) {
        snackbar.setActionTextColor(context.getResources().getColor(R.color.colorPrimary));
        snackMessage = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackAction = (Button) snackbar.getView().findViewById(android.support.design.R.id.snackbar_action);

        snackMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.default_font_size_14m));
        snackAction.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.default_font_size_14m));
    }

    public String storeHTMLText(SpannableStringBuilder spannableStringBuilder, int start, int end) {
        selectedSpannableString = new SpannableStringBuilder(spannableStringBuilder.subSequence(start, end));
        selectedHTML = Html.toHtml(selectedSpannableString);

        return selectedHTML;
    }

    public SpannableStringBuilder loadTextStyle(Editable editableText) {
        selectedSpannableString = new SpannableStringBuilder(editableText);
        specialFont = Typeface.createFromAsset(context.getAssets(), "fonts/Calibri.ttf");
        subSpan = selectedSpannableString.getSpans(0, selectedSpannableString.length(), SubscriptSpan.class);
        superSpan = selectedSpannableString.getSpans(0, selectedSpannableString.length(), SuperscriptSpan.class);

        for (int i = 0; i < selectedSpannableString.length(); i++) {
            if (selectedSpannableString.charAt(i) == '→' || selectedSpannableString.charAt(i) == '↔') {
                specialSpannableChar = new SpannableStringBuilder(selectedSpannableString.subSequence(i, i + 1));
                specialSpannableChar.setSpan(new CustomTypefaceSpan("", specialFont), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                selectedSpannableString.replace(i, i + 1, specialSpannableChar);
            }
        }
        for (int i = 0; i < subSpan.length; i++) {
            selectedSpannableString.setSpan(new RelativeSizeSpan(0.75f), selectedSpannableString.getSpanStart(subSpan[i]),
                    selectedSpannableString.getSpanEnd(subSpan[i]), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (int i = 0; i < superSpan.length; i++) {
            selectedSpannableString.setSpan(new RelativeSizeSpan(0.75f), selectedSpannableString.getSpanStart(superSpan[i]),
                    selectedSpannableString.getSpanEnd(superSpan[i]), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return selectedSpannableString;
    }

    public Bitmap resizeBitmap(Uri uri) {
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

        return imageLoader.loadImageSync(uri.toString());
    }

    public Bitmap adjustBitmap(Bitmap bitmap, Uri uri) {
        adjustedBitmap = bitmap;
        try {
            exifInterface = new ExifInterface(getRealPathUri(uri));
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            degrees = getDegrees(orientation);

            matrix = new Matrix();
            if (degrees != 0f) {
                matrix.preRotate(degrees);
            }
            adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
        }
        return adjustedBitmap;
    }

    public String getRealPathUri(Uri uri) {
        resultPath = null;
        cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            resultPath = uri.getPath();
        } else {
            if (cursor.moveToFirst()) {
                indexPath = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                resultPath = cursor.getString(indexPath);
            }
            cursor.close();
        }
        return resultPath;
    }

    private int getDegrees(int orientation) {
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private File getFileImage() {
        tempDirectory = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "Temp");
        if (!tempDirectory.exists()) {
            if (!tempDirectory.mkdirs()) {
                return null;
            }
        }
        temp = new File(tempDirectory.getPath() + File.separator + "temp" + ".jpg");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(temp);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();

            MediaScannerConnection.scanFile(context, new String[]{temp.getPath()}, new String[]{"image/jpg"}, null);
        } catch (IOException e) {
        }
        return temp;
    }

    private Uri getUriImage() {
        return Uri.fromFile(getFileImage());
    }

    public File getTempFileImage(ByteArrayOutputStream byteArrayOutputStream) {
        this.byteArrayOutputStream = byteArrayOutputStream;
        return new File(getRealPathUri(getUriImage()));
    }

    public void deleteTempFileImage(File temp) {
        try {
            FileUtils.deleteDirectory(tempDirectory);
            MediaScannerConnection.scanFile(context, new String[]{temp.getPath()}, null, null);
        } catch (IOException e) {
        }
    }

    public static ClassApplicationTool with(Context context) {
        return new ClassApplicationTool(context);
    }

    public int getAvatarColor(int id) {
        colors = context.getResources().getIntArray(R.array.material_color);
        return colors[id % colors.length];
    }

    public String convertToDateTimeFormat(int mode, String dateTime) {
        timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (mode == 0) {
            dateTimeFormat = new SimpleDateFormat("EEE, dd MMM ''yy-HH:mm");
        } else if (mode == 1) {
            dateTimeFormat = new SimpleDateFormat("dd MMM yyyy-HH.mm");
        }
        try {
            timestamp = timestampFormat.parse(dateTime);
            final String[] formatParts = dateTimeFormat.format(timestamp).split("-");
            date = formatParts[0];
            time = formatParts[1];

            return String.format("%s pada %s WIB", date, time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String convertToDateFormat(int mode, String dateTime) {
        timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (mode == 0) {
            dateFormat = new SimpleDateFormat("EEEE, dd MMM yyyy");
        } else if (mode == 1) {
            dateFormat = new SimpleDateFormat("dd MMM yyyy");
        }
        try {
            timestamp = timestampFormat.parse(dateTime);
            date = dateFormat.format(timestamp);

            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}