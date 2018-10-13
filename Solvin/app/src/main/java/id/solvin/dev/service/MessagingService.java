package id.solvin.dev.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import id.solvin.dev.R;
import id.solvin.dev.helper.ClassApplicationTool;
import id.solvin.dev.helper.Global;
import id.solvin.dev.helper.Session;
import id.solvin.dev.model.basic.Auth;
import id.solvin.dev.model.basic.NotificationBus;
import id.solvin.dev.view.activities.MainActivity;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static android.R.attr.bitmap;
import static android.R.attr.radius;
import static id.solvin.dev.R.menu.solution;

/**
 * Created by edinofri on 16/12/2016.
 */

public class MessagingService extends FirebaseMessagingService {
    private android.app.Notification notification;
    private NotificationManager notificationManager;

    private Uri notificationSound;
    private Vibrator vibrator;
    private Ringtone ringtone;
    private MediaPlayer mediaPlayer;
    private Auth auth;

    private int priority;
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
        auth = Session.with(getApplicationContext()).getAuth();
        if (auth.getId() == Integer.valueOf(remoteMessage.getData().get("auth_id")) &&
                auth.getAuth_type().equals(remoteMessage.getData().get("auth_type"))) {

            pushNotification(remoteMessage);
        }
    }

    public void pushNotification(final RemoteMessage remoteMessage) {
        if (!isAppIsInBackground(getApplicationContext())) {
            EventBus.getDefault().post(new NotificationBus());
        }
        if (Session.with(getApplicationContext()).getSetting(Session.KEY_SETTING_GET_NOTIFICATION)) {
            priority = Integer.parseInt(remoteMessage.getData().get("priority"));

            if (Session.with(getApplicationContext()).getSetting(Session.KEY_SETTING_VIBRATIONN)) {
                playVibrate();
            }
            if (Session.with(getApplicationContext()).getSetting(Session.KEY_SETTING_SOUND)) {
                switch (priority) {
                    case 0:
                        playSound();
                        break;
                    case 1:
                        playUrgentSound();
                        break;
                }
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            final Notification.Builder notificationBuilder = new Notification.Builder(this);
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
                                            .load(Global.get().getAssetURL(remoteMessage.getData().get("sender_photo"), remoteMessage.getData().get("sender_type")))
                                            .get())
                            );
                        } catch (Exception e) {

                        }
                    } else {
                        notificationBuilder.setLargeIcon(
                                getAvatarLayout(Global.get().getInitialName(remoteMessage.getData().get("sender_name")),
                                        ClassApplicationTool.with(getApplicationContext()).getAvatarColor(Integer.parseInt(remoteMessage.getData().get("sender_id"))))
                        );
                    }
                }
                notification = notificationBuilder.build();

                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify((int) System.currentTimeMillis(), notification);
            }
        }
    }

    private void playVibrate() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, -1);
    }

    private void playUrgentSound() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.solvin);
        mediaPlayer.start();
    }

    private void playSound() {
        notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), notificationSound);
        ringtone.play();
    }

    /**
     * Method checks if the app is in background or not
     */
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

//        photo = Bitmap.createBitmap(bitmap.getWidth(),
//                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//
//        paint = new Paint();
//        paint.setAntiAlias(true);
//        rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//
//        canvas = new Canvas(photo);
//        canvas.drawARGB(0, 0, 0, 0);
//        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, rect, rect, paint);
//
//        return photo;
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