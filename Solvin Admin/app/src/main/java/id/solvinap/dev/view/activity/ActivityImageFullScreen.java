package id.solvinap.dev.view.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import id.solvinap.dev.R;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.view.helper.Tool;
import id.solvinap.dev.view.widget.CustomTouchImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Erick Sumargo on 2/1/2017.
 */

public class ActivityImageFullScreen extends AppCompatActivity {
    //    UI COMPONENT
    private CustomTouchImageView imageView;
    private LinearLayout saveForeground;

    //    LOCAL OBJECT
    private Bundle extras;
    private Uri uri;
    private Bitmap bitmap;

    private String image, category;

    private Tool tool;

    private File fileImage;
    private FileOutputStream fileOutputStream;
    private static File mediaStorageDir, mediaFile;
    private static String timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_image_full_screen);

        imageView = (CustomTouchImageView) findViewById(R.id.full_screen_image);
        saveForeground = (LinearLayout) findViewById(R.id.full_screen_save_foreground);
        tool = new Tool(getApplicationContext());

        extras = getIntent().getExtras();
        if (extras.getParcelable("uri") != null) {
            uri = extras.getParcelable("uri");

            bitmap = tool.resizeBitmap(uri);
            imageView.setImageBitmap(tool.adjustBitmap(bitmap, uri));
            saveForeground.setVisibility(View.VISIBLE);
        } else {
            image = extras.getString("image");
            category = extras.getString("category");

            Picasso.with(getApplicationContext())
                    .load(Global.ASSETS_URL + category + "/" + image)
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

                final Toast toast = Toast.makeText(ActivityImageFullScreen.this, "Gambar Tersimpan", Toast.LENGTH_SHORT);
                tool.resizeToast(toast);
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
                "Solvin AP");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Solvin AP", "Failed to create Solvin directory");
                return null;
            }
        }
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");
        return mediaFile;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}