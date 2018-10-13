package id.solvin.dev.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Erick Sumargo on 9/29/2016.
 */
public class ClassMediaChooser {
    private static final String TAG = "MediaPicker";
    private static final String IMAGE_DIRECTORY_NAME = "Solvin";
    private static String timeStamp, packageName;
    public static Bitmap resizedBitmap;

    private static Context context;
    private static Intent mediaChooser, camera, gallery, target;
    private static List<Intent> mediaList;
    private static List<ResolveInfo> resolveInfoList;
    public static Uri uri;
    private static File mediaStorageDir, mediaFile;

    private static ImageLoader imageLoader;

    public static Intent getPickImageIntent(Context context) {
        mediaChooser = null;
        mediaList = new ArrayList<>();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

        gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        uri = getOutputMediaFileUri();
        camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        mediaList = addIntentsToList(context, mediaList, camera);
        mediaList = addIntentsToList(context, mediaList, gallery);

        if (mediaList.size() > 0) {
            mediaChooser = Intent.createChooser(mediaList.remove(mediaList.size() - 1),
                    "Pilih Media");
            mediaChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, mediaList.toArray(new Parcelable[]{}));
        }

        return mediaChooser;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> mediaList, Intent intent) {
        resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            packageName = resolveInfo.activityInfo.packageName;
            target = new Intent(intent);
            target.setPackage(packageName);
            mediaList.add(target);

            Log.d(TAG, "Intent: " + intent.getAction() + " package: " + packageName);
        }
        return mediaList;
    }

    public static void setImageFromResult(int resultCode, Intent data, Context context) {
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                if (mediaFile != null) {
                    MediaScannerConnection.scanFile(context,
                            new String[]{mediaFile.getPath()}, new String[]{"image/jpg"}, null);
                }
                resizedBitmap = imageLoader.loadImageSync(uri.toString());
            } else {
                uri = data.getData();
                resizedBitmap = imageLoader.loadImageSync(uri.toString());
            }
        } else {
            resizedBitmap = null;
        }
    }

    private static Uri getOutputMediaFileUri() {
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
}
