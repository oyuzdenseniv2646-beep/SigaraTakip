package com.cano.sigaratracker;

import android.app.*;
import android.content.*;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationHelper {

    public static final String CHANNEL_ID = "sigara_tracker_v3";
    public static final int NOTIFICATION_ID = 777;

    private static String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
    }

    public static void createChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sigara Takip",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setDescription("Günlük sigara sayacı");
            channel.setShowBadge(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    public static void show(Context context) {

        createChannel(context);

        SharedPreferences prefs =
                context.getSharedPreferences(
                        "sigara_data",
                        Context.MODE_PRIVATE
                );

        int count = prefs.getInt("count_" + today(), 0);

        Intent openIntent = new Intent(context, MainActivity.class);

        PendingIntent openPending = PendingIntent.getActivity(
                context,
                10,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE
        );

        Intent addIntent =
                new Intent(context, NotificationReceiver.class);
        addIntent.setAction("ADD");

        PendingIntent addPending = PendingIntent.getBroadcast(
                context,
                11,
                addIntent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE
        );

        Intent undoIntent =
                new Intent(context, NotificationReceiver.class);
        undoIntent.setAction("UNDO");

        PendingIntent undoPending = PendingIntent.getBroadcast(
                context,
                12,
                undoIntent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE
        );

        Notification.Builder builder;
        Notification.Builder publicBuilder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, CHANNEL_ID);
            publicBuilder = new Notification.Builder(context, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(context);
            publicBuilder = new Notification.Builder(context);
        }

        // Kilit ekranında gösterilecek açık sürüm
        Notification publicNotification = publicBuilder
                .setSmallIcon(android.R.drawable.ic_menu_edit)
                .setContentTitle("Bugün: " + count + " sigara")
                .setContentText("")
                .setContentIntent(openPending)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setShowWhen(false)
                .build();

        // Normal bildirim
        builder
                .setSmallIcon(android.R.drawable.ic_menu_edit)
                .setContentTitle("Bugün: " + count + " sigara")
                .setContentText("")
                .setContentIntent(openPending)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setPublicVersion(publicNotification)
                .setShowWhen(false)
                .setCategory(Notification.CATEGORY_STATUS)
                .addAction(
                        android.R.drawable.ic_input_add,
                        "+1 İçtim",
                        addPending
                )
                .addAction(
                        android.R.drawable.ic_menu_revert,
                        "Geri Al",
                        undoPending
                );

        NotificationManager manager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE
                );

        manager.notify(NOTIFICATION_ID, builder.build());
    }
}
