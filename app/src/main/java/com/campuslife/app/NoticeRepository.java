package com.campuslife.app;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 公告数据仓库：读取本地 assets/notices.json 并解析为 {@link Notice} 列表。
 * 体现"本地 JSON 数据读取 + 解析 + 页面展示"的服务扩展功能。
 */
public class NoticeRepository {

    private static final String TAG = "NoticeRepository";
    private static final String FILE_NAME = "notices.json";

    private NoticeRepository() {
    }

    /**
     * 从 assets 读取并解析公告数据。解析失败时返回空列表，保证页面不会闪退。
     */
    public static List<Notice> loadNotices(Context context) {
        List<Notice> result = new ArrayList<>();
        String json = readAssetFile(context, FILE_NAME);
        if (json == null) {
            return result;
        }
        try {
            JSONObject root = new JSONObject(json);
            JSONArray array = root.getJSONArray("notices");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                result.add(new Notice(
                        obj.optInt("id", i),
                        obj.optString("category"),
                        obj.optString("title"),
                        obj.optString("location"),
                        obj.optString("time"),
                        obj.optString("content")
                ));
            }
        } catch (Exception e) {
            Log.e(TAG, "解析公告 JSON 失败", e);
        }
        return result;
    }

    private static String readAssetFile(Context context, String fileName) {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = context.getAssets().open(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            Log.e(TAG, "读取 assets 文件失败: " + fileName, e);
            return null;
        }
    }
}
