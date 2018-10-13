package id.solvinap.dev.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import id.solvinap.dev.R;
import id.solvinap.dev.server.helper.Global;
import id.solvinap.dev.view.activity.MainActivity;
import id.solvinap.dev.view.helper.NotificationBus;
import id.solvinap.dev.view.helper.Tool;

import static android.R.attr.radius;
import static android.R.attr.width;

/**
 * Created by Erick Sumargo on 2/6/2017.
 */

public class MessagingService extends FirebaseMessagingService {
    //    HELPER
    private android.app.Notification notification;
    private Notification.Builder notificationBuilder;
    private NotificationManager notificationManager;

    private Uri notificationSound;
    private Vibrator vibrator;
    private Ringtone ringtone;

    private Intent intent;
    private PendingIntent pendingIntent;

    //    VARIABLE
    private String message;
    private final long[] pattern = {0, 250, 200, 250};

    //    Custom Notification
    private int width, height;
    private int left, top, right, bottom, radius;

    private Bitmap photo, avatarLayout;

    private Canvas canvas;
    private Paint paint;
    private Rect srcRect, dstRect;
//    private Rect rect;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        pushNotification(remoteMessage);
    }

    public void pushNotification(RemoteMessage remoteMessage) {
        if (!isAppIsInBackground(getApplicationContext())) {
            EventBus.getDefault().post(new NotificationBus());
        }

        playVibrate();
        playSound();

        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        notificationBuilder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= 21) {
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        }
        if (Build.VERSION.SDK_INT > 15) {
            message = remoteMessage.getNotification().getBody();
            if (!remoteMessage.getData().get("subject_type").equals("transaction")) {
                message = String.format(message, remoteMessage.getData().get("sender_name"));
            }
            notificationBuilder.setSmallIcon(R.drawable.ic_solvin_notification)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setStyle(new Notification.BigTextStyle(notificationBuilder)
                            .setBigContentTitle(remoteMessage.getNotification().getTitle())
                            .bigText(message)
                    )
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            if (remoteMessage.getData().get("sender_type").equals("admin")) {
                notificationBuilder.setLargeIcon(getPhoto(BitmapFactory.decodeResource(getResources(), R.drawable.operator)));
            } else {
                if (!remoteMessage.getData().get("sender_photo").isEmpty()) {
                    try {
                        notificationBuilder.setLargeIcon(
                                getPhoto(Picasso.with(getApplicationContext())
                                        .load(Global.ASSETS_URL + remoteMessage.getData().get("sender_type") + "/" + remoteMessage.getData().get("sender_photo"))
                                        .get())
                        );
                    } catch (Exception e) {

                    }
                } else {
                    notificationBuilder.setLargeIcon(
                            getAvatarLayout(Tool.getInstance(getApplicationContext()).getInitialName(remoteMessage.getData().get("sender_name")),
                                    Tool.getInstance(getApplicationContext()).getAvatarColor(Integer.parseInt(remoteMessage.getData().get("sender_id"))))
                    );
                }
            }
            notification = notificationBuilder.build();

            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify((int) System.currentTimeMillis(), notification);
        }
    }

    private void playVibrate() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, -1);
    }

    private void playSound() {
        notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), notificationSound);
        ringtone.play();
    }

    public boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        }
        return isInBackground;
    }

    private Bitmap getPhoto(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        if (width > height) {
            left = (width - height) / 2;
            right = left + height;

            srcRect = new Rect(left, 0, right, height);
            dstRect = new Rect(0, 0, height, height);

            radius = height / 2;
            photo = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888);
        } else {
            top = (height - width) / 2;
            bottom = top + width;

            srcRect = new Rect(0, top, width, bottom);
            dstRect = new Rect(0, 0, width, width);

            radius = width / 2;
            photo = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        }
        paint = new Paint();
        paint.setAntiAlias(true);

        canvas = new Canvas(photo);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(radius, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        return photo;
    }

    private Bitmap getAvatarLayout(String initial, int color) {
        return convertDrawableToBitmap(TextDrawable.builder().buildRound(initial, color));
    }

    private Bitmap convertDrawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : 192;
        height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : 192;

        avatarLayout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(avatarLayout);

        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return avatarLayout;
    }
}