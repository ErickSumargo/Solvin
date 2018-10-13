package id.solvinap.dev.view.helper;

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

import id.solvinap.dev.R;
import id.solvinap.dev.view.widget.CustomTypefaceSpan;
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
import java.util.Random;

/**
 * Created by Erick Sumargo on 1/31/2017.
 */

public class Tool {
    //    UI COMPONENT
    private Button snackAction;
    private TextView alertDialogTitle, alertDialogMessage, toastMessage, snackMessage;

    //    UI HELPER
    private Context context;
    private Cursor cursor;
    private DecimalFormat RpFormat;
    private DecimalFormatSymbols RpFormatSymbols;
    private ImageLoader imageLoader;
    private ExifInterface exifInterface;
    private Matrix matrix;
    private LinearLayout.LayoutParams alertDialogParams;

    //    LOCAL OBJECT
    private Bitmap adjustedBitmap;
    private Random random;
    private SpannableStringBuilder selectedSpannableString, specialSpannableChar;
    private Typeface specialFont;
    private SubscriptSpan[] subSpan;
    private SuperscriptSpan[] superSpan;

    private File tempDirectory, temp;
    private ByteArrayOutputStream byteArrayOutputStream;

    private Date timestamp;
    private SimpleDateFormat timestampFormat, dateTimeFormat, dateFormat, timeFormat;
    private String date, time;

    //    LOCAL VARIABLE
    private int orientation, degrees, indexPath;
    private int colors[];
    public final static int targetCompress = 1024, targetContentCompress = 2048;
    private String RpCurrency, resultPath, selectedHTML, initial;
    private String[] nameParts;
    private byte[] byteArray;

    public Tool(Context context) {
        this.context = context;
    }

    public static Tool getInstance(Context context) {
        return new Tool(context);
    }

    public void resizeAlertDialog(AlertDialog alertDialog) {
        alertDialogParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        alertDialogParams.setMargins(
                (int) context.getResources().getDimension(R.dimen.alert_dialog_default_extra_margin),
                (int) context.getResources().getDimension(R.dimen.alert_dialog_default_extra_margin),
                (int) context.getResources().getDimension(R.dimen.alert_dialog_default_extra_margin),
                (int) context.getResources().getDimension(R.dimen.alert_dialog_default_extra_margin));

        alertDialogTitle = (TextView) alertDialog.findViewById(R.id.alertTitle);
        if (alertDialogTitle != null) {
            alertDialogTitle.setLayoutParams(alertDialogParams);
            alertDialogTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_20rs));
        }

        alertDialogMessage = (TextView) alertDialog.findViewById(android.R.id.message);
        if (alertDialogMessage != null) {
            alertDialogMessage.setLayoutParams(alertDialogParams);
            alertDialogMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_16rs));
        }

        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setLayoutParams(alertDialogParams);
        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_14rs));

        alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setLayoutParams(alertDialogParams);
        alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_14rs));
    }

    public void resizeToast(Toast toast) {
        toastMessage = (TextView) ((ViewGroup) toast.getView()).getChildAt(0);
        toastMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_14rs));
    }

    public void resizeSnackBar(Snackbar snackbar, int mode) {
        snackMessage = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_14rs));
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

        snackMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_14rs));
        snackAction.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_14rs));
    }

    public int getAvatarColor(int id) {
        colors = context.getResources().getIntArray(R.array.material_color);
        return colors[id % colors.length];
    }

    public String getInitialName(String name) {
        initial = "";
        nameParts = name.split(" ");
        if (nameParts.length == 1) {
            initial += Character.toUpperCase(nameParts[0].charAt(0));
        } else {
            for (String namePart : nameParts) {
                initial += Character.toUpperCase(namePart.charAt(0));
                if (initial.length() == 2) {
                    break;
                }
            }
        }
        return initial;
    }

    public String convertRpCurrency(long nominal) {
        RpFormatSymbols = new DecimalFormatSymbols();
        RpFormatSymbols.setCurrencySymbol("Rp. ");
        RpFormatSymbols.setGroupingSeparator('.');

        RpFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        RpFormat.setDecimalFormatSymbols(RpFormatSymbols);
        RpFormat.setMaximumFractionDigits(0);

        RpCurrency = RpFormat.format(nominal);
        RpCurrency = RpCurrency.substring(0, RpCurrency.length()) + ",-";

        return RpCurrency;
    }

    public String convertToDateTimeFormat(int mode, String dateTime) {
        timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (mode == 0) {
            dateTimeFormat = new SimpleDateFormat("EEE, dd MMM ''yy-HH:mm");
        } else if (mode == 1) {
            dateTimeFormat = new SimpleDateFormat("dd MMM yyyy-HH:mm");
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
        } else {
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

    public String convertToTimeFormat(String dateTime) {
        timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeFormat = new SimpleDateFormat("HH:mm");
        try {
            timestamp = timestampFormat.parse(dateTime);
            time = timeFormat.format(timestamp);

            return time + " WIB";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String storeHTMLText(SpannableStringBuilder spannableStringBuilder, int start, int end) {
        selectedSpannableString = new SpannableStringBuilder(spannableStringBuilder.subSequence(start, end));
        selectedHTML = Html.toHtml(selectedSpannableString);

        return selectedHTML;
    }

    public SpannableStringBuilder loadHTMLStyle(String selectedHTML) {
        selectedSpannableString = new SpannableStringBuilder(Html.fromHtml(selectedHTML));
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
        selectedSpannableString.replace(selectedSpannableString.length() - 2, selectedSpannableString.length(), "");
        return selectedSpannableString;
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
}