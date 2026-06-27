package com.campuslife.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

/**
 * 通知工具类：统一创建通知渠道（Android 8.0+ 必须）。
 */
public final class NotificationHelper {

    public static final String CHANNEL_ID = "campus_remind_channel";

    private NotificationHelper() {
    }

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager == null || manager.getNotificationChannel(CHANNEL_ID) != null) {
                return;
            }
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(context.getString(R.string.channel_desc));
            manager.createNotificationChannel(channel);
        }
    }
}
