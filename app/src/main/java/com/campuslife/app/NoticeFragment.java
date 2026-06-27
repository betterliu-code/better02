package com.campuslife.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * 校园公告 Fragment：读取本地 JSON 数据并用 RecyclerView 展示，
 * 点击"设为提醒"会通过 Service/广播触发系统通知。
 */
public class NoticeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rvNotices);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Notice> notices = NoticeRepository.loadNotices(requireContext());
        NoticeAdapter adapter = new NoticeAdapter(notices, this::scheduleReminder);
        recyclerView.setAdapter(adapter);

        if (notices.isEmpty()) {
            Toast.makeText(requireContext(), "暂无公告数据", Toast.LENGTH_SHORT).show();
        }
    }

    /** 将选中公告交给提醒服务，最终弹出系统通知。 */
    private void scheduleReminder(Notice notice) {
        Intent intent = new Intent(requireContext(), ReminderService.class);
        intent.putExtra(ReminderService.EXTRA_TITLE, notice.getTitle());
        intent.putExtra(ReminderService.EXTRA_CONTENT,
                notice.getTime() + " · " + notice.getContent());
        requireContext().startService(intent);
        Toast.makeText(requireContext(), "已为《" + notice.getTitle() + "》设置提醒",
                Toast.LENGTH_SHORT).show();
    }
}
