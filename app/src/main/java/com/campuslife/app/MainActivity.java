package com.campuslife.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/**
 * 首页：展示标题、Banner 图、功能图标与最新公告，并作为各功能模块的入口。
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvLatestCategory;
    private TextView tvLatestTitle;
    private TextView tvLatestLocation;
    private TextView tvLatestTime;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                // 用户拒绝时不影响其它功能，提醒功能会在发送通知时安全降级
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationHelper.createChannel(this);
        requestNotificationPermissionIfNeeded();

        bindMenu();
        bindLatestNotice();
    }

    private void bindMenu() {
        findViewById(R.id.menuNotice).setOnClickListener(v ->
                startActivity(new Intent(this, NoticeListActivity.class)));

        findViewById(R.id.menuProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        findViewById(R.id.menuRemind).setOnClickListener(v -> {
            // 启动服务：读取本地数据 -> 广播 -> 通知
            startService(new Intent(this, ReminderService.class));
            Toast.makeText(this, "已开启课程提醒，请查看通知栏", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.menuAbout).setOnClickListener(v -> showAbout());

        View latestCard = findViewById(R.id.cardLatestNotice);
        latestCard.setOnClickListener(v ->
                startActivity(new Intent(this, NoticeListActivity.class)));
    }

    private void bindLatestNotice() {
        tvLatestCategory = findViewById(R.id.tvLatestCategory);
        tvLatestTitle = findViewById(R.id.tvLatestTitle);
        tvLatestLocation = findViewById(R.id.tvLatestLocation);
        tvLatestTime = findViewById(R.id.tvLatestTime);

        List<Notice> notices = NoticeRepository.loadNotices(this);
        if (notices.isEmpty()) {
            tvLatestTitle.setText("暂无公告");
            return;
        }
        Notice latest = notices.get(0);
        tvLatestCategory.setText(latest.getCategory());
        tvLatestTitle.setText(latest.getTitle());
        tvLatestLocation.setText(latest.getLocation());
        tvLatestTime.setText(latest.getTime());
    }

    private void showAbout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.menu_about)
                .setMessage("校园生活助手 APP\n版本 1.0\n\n一站式校园服务应用，提供校园公告浏览、"
                        + "个人信息登记与本地保存、课程/活动提醒等功能。\n\n《APP开发技术》课程结课设计作品。")
                .setPositiveButton("知道了", null)
                .show();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}
