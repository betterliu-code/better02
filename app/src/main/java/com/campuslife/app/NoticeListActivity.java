package com.campuslife.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * 校园公告列表页：读取本地 JSON 数据并用 RecyclerView 展示，
 * 点击"设为提醒"会通过 Service/广播触发系统通知。
 */
public class NoticeListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.notice_title);
        }

        RecyclerView recyclerView = findViewById(R.id.rvNotices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Notice> notices = NoticeRepository.loadNotices(this);
        NoticeAdapter adapter = new NoticeAdapter(notices, this::scheduleReminder);
        recyclerView.setAdapter(adapter);

        if (notices.isEmpty()) {
            Toast.makeText(this, "暂无公告数据", Toast.LENGTH_SHORT).show();
        }
    }

    /** 将选中公告交给提醒服务，最终弹出系统通知。 */
    private void scheduleReminder(Notice notice) {
        Intent intent = new Intent(this, ReminderService.class);
        intent.putExtra(ReminderService.EXTRA_TITLE, notice.getTitle());
        intent.putExtra(ReminderService.EXTRA_CONTENT,
                notice.getTime() + " · " + notice.getContent());
        startService(intent);
        Toast.makeText(this, "已为《" + notice.getTitle() + "》设置提醒", Toast.LENGTH_SHORT).show();
    }
}
