package com.campuslife.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.List;

/**
 * 首页 Fragment：展示标题、Banner 图、功能图标与最新公告。
 * 公告 / 信息登记入口会切换底部导航对应的标签页。
 */
public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindMenu(view);
        bindLatestNotice(view);
    }

    private void bindMenu(@NonNull View view) {
        view.findViewById(R.id.menuNotice).setOnClickListener(v -> switchTab(R.id.nav_notice));
        view.findViewById(R.id.menuProfile).setOnClickListener(v -> switchTab(R.id.nav_profile));
        view.findViewById(R.id.cardLatestNotice).setOnClickListener(v -> switchTab(R.id.nav_notice));

        view.findViewById(R.id.menuRemind).setOnClickListener(v -> {
            // 启动服务：读取本地数据 -> 广播 -> 通知
            requireContext().startService(new Intent(requireContext(), ReminderService.class));
            Toast.makeText(requireContext(), "已开启课程提醒，请查看通知栏", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.menuAbout).setOnClickListener(v -> showAbout());
    }

    private void switchTab(int menuItemId) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectTab(menuItemId);
        }
    }

    private void bindLatestNotice(@NonNull View view) {
        TextView tvLatestCategory = view.findViewById(R.id.tvLatestCategory);
        TextView tvLatestTitle = view.findViewById(R.id.tvLatestTitle);
        TextView tvLatestLocation = view.findViewById(R.id.tvLatestLocation);
        TextView tvLatestTime = view.findViewById(R.id.tvLatestTime);

        List<Notice> notices = NoticeRepository.loadNotices(requireContext());
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
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.menu_about)
                .setMessage("校园生活助手 APP\n版本 1.0\n\n一站式校园服务应用，提供校园公告浏览、"
                        + "个人信息登记与本地保存、课程/活动提醒等功能。\n\n《APP开发技术》课程结课设计作品。")
                .setPositiveButton("知道了", null)
                .show();
    }
}
