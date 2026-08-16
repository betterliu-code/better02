package com.campuslife.app;

/**
 * 校园公告数据模型，对应 assets/notices.json 中的一条记录。
 */
public class Notice {

    private final int id;
    private final String category;
    private final String title;
    private final String location;
    private final String time;
    private final String content;

    public Notice(int id, String category, String title, String location, String time, String content) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.location = location;
        this.time = time;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }
}
