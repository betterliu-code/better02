package com.campuslife.app;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * 提醒广播接收者：收到 {@link #ACTION_REMIND} 广播后弹出系统通知。
 * 体现 BroadcastReceiver + Notification 的服务提醒能力。
 */
public class ReminderReceiver extends BroadcastReceiver {

    public static final String ACTION_REMIND = "com.campuslife.app.ACTION_REMIND";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_CONTENT = "extra_content";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !ACTION_REMIND.equals(intent.getAction())) {
            return;
        }
        String title = intent.getStringExtra(EXTRA_TITLE);
        String content = intent.getStringExtra(EXTRA_CONTENT);
        if (title == null) {
            title = context.getString(R.string.menu_remind);
        }
        if (content == null) {
            content = "";
        }

        NotificationHelper.createChannel(context);

        Intent openIntent = new Intent(context, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, openIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_remind)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        try {
            NotificationManagerCompat.from(context).notify((int) System.currentTimeMillis(), builder.build());
        } catch (SecurityException e) {
            // 未授予通知权限时忽略，避免崩溃
        }
    }
}
