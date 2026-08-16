package com.campuslife.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * 公告列表适配器：将 {@link Notice} 数据绑定到列表项，并处理"设为提醒"点击事件。
 */
public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {

    /** 点击"设为提醒"的回调接口。 */
    public interface OnRemindClickListener {
        void onRemind(Notice notice);
    }

    private final List<Notice> notices;
    private final OnRemindClickListener listener;

    public NoticeAdapter(List<Notice> notices, OnRemindClickListener listener) {
        this.notices = notices;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notice, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        Notice notice = notices.get(position);
        holder.tvCategory.setText(notice.getCategory());
        holder.tvTitle.setText(notice.getTitle());
        holder.tvContent.setText(notice.getContent());
        holder.tvLocation.setText(notice.getLocation());
        holder.tvTime.setText(notice.getTime());
        holder.btnRemind.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemind(notice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notices.size();
    }

    static class NoticeViewHolder extends RecyclerView.ViewHolder {
        final TextView tvCategory;
        final TextView tvTitle;
        final TextView tvContent;
        final TextView tvLocation;
        final TextView tvTime;
        final Button btnRemind;

        NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnRemind = itemView.findViewById(R.id.btnRemind);
        }
    }
}
