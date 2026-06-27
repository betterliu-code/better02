package com.campuslife.app;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * 提醒服务：在后台读取本地公告数据，挑选需要提醒的内容后，
 * 通过广播交给 {@link ReminderReceiver} 弹出系统通知。
 * 体现 Service + 数据读取解析 + Broadcast + Notification 的完整链路。
 */
public class ReminderService extends IntentService {

    public static final String EXTRA_TITLE = "service_title";
    public static final String EXTRA_CONTENT = "service_content";

    public ReminderService() {
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String title;
        String content;

        if (intent != null && intent.hasExtra(EXTRA_TITLE)) {
            // 来自公告列表"设为提醒"的指定内容
            title = intent.getStringExtra(EXTRA_TITLE);
            content = intent.getStringExtra(EXTRA_CONTENT);
        } else {
            // 默认：读取本地 JSON，取一条"课程提醒"类公告作为提醒内容
            title = getString(R.string.menu_remind);
            content = "暂无课程提醒";
            List<Notice> notices = NoticeRepository.loadNotices(this);
            for (Notice notice : notices) {
                if (notice.getCategory() != null && notice.getCategory().contains("课程")) {
                    title = notice.getTitle();
                    content = notice.getTime() + " · " + notice.getContent();
                    break;
                }
            }
        }

        Intent broadcast = new Intent(this, ReminderReceiver.class);
        broadcast.setAction(ReminderReceiver.ACTION_REMIND);
        broadcast.putExtra(ReminderReceiver.EXTRA_TITLE, title);
        broadcast.putExtra(ReminderReceiver.EXTRA_CONTENT, content);
        sendBroadcast(broadcast);
    }
}
