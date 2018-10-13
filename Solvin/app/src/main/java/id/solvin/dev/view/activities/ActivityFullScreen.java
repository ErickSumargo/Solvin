package id.solvin.dev.view.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Global;
import id.solvin.dev.realm.helper.RealmDraftSolution;
import id.solvin.dev.view.widget.ClassTouchImageView;
import io.realm.Realm;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 9/2/2016.
 */
public class ActivityFullScreen extends AppCompatActivity {
    private ClassTouchImageView imageView;
    private LinearLayout saveForeground;

    private Bundle extras;
    private Uri uri;
    private Bitmap bitmap;

    private String image, category;
    private ClassApplicationTool applicationTool;

    private File fileImage;
    private FileOutputStream fileOutputStream;
    private static File mediaStorageDir, mediaFile;
    private static String timeStamp;

    private Realm realm;
    private RealmDraftSolution realmDraftSolution;

    private int id, index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_full_screen);

        imageView = (ClassTouchImageView) findViewById(R.id.full_screen_image);
        saveForeground = (LinearLayout) findViewById(R.id.full_screen_save_foreground);
        applicationTool = new ClassApplicationTool(getApplicationContext());

        extras = getIntent().getExtras();
        if (extras.getParcelable("uri") != null) {
            uri = extras.getParcelable("uri");

            bitmap = applicationTool.resizeBitmap(uri);
            bitmap = applicationTool.adjustBitmap(bitmap, uri);
            bitmap = addWatermark(bitmap);

            imageView.setImageBitmap(bitmap);
            saveForeground.setVisibility(View.VISIBLE);
        } else if (extras.getString("id_index") != null) {
            id = Integer.parseInt(extras.getString("id_index").split("_")[0]);
            index = Integer.parseInt(extras.getString("id_index").split("_")[1]);

            realm = Realm.getDefaultInstance();
            realmDraftSolution = new RealmDraftSolution(realm);
            final byte[] bytes = Base64.decode(realmDraftSolution.getEncodedImage(id, index), 0);

            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bitmap);
            saveForeground.setVisibility(View.VISIBLE);
        } else {
            image = extras.getString("image");
            category = extras.getString("category");
            Picasso.with(getApplicationContext())
                    .load(Global.get().getAssetURL(image, category))
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            saveForeground.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        saveForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();

                final Toast toast = Toast.makeText(ActivityFullScreen.this, "Gambar Tersimpan", Toast.LENGTH_SHORT);
                applicationTool.resizeToast(toast);
                toast.show();
            }
        });
    }

    private void saveImage() {
        try {
            fileImage = getOutputMediaFile();
            fileOutputStream = new FileOutputStream(fileImage);
            imageView.buildDrawingCache();
            imageView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();

            MediaScannerConnection.scanFile(this, new String[]{mediaFile.getPath()}, new String[]{"image/jpg"}, null);
        } catch (IOException e) {
        }
    }

    private static File getOutputMediaFile() {
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Solvin");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Solvin", "Failed to create Solvin directory");
                return null;
            }
        }
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");
        return mediaFile;
    }

    private Bitmap addWatermark(Bitmap bitmap) {
        Bitmap watermarked = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

        Canvas canvas = new Canvas(watermarked);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setAlpha(128);

        canvas.drawBitmap(bitmap, 0, 0, null);

        Bitmap waterMark = BitmapFactory.decodeResource(getResources(), R.drawable.ic_solvin_primary);
        canvas.drawBitmap(waterMark, (canvas.getWidth() - waterMark.getWidth()) / 2, (canvas.getHeight() - waterMark.getHeight()) / 2, paint);

        return watermarked;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}