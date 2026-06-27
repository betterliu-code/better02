package com.campuslife.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 个人信息登记页：实现输入、提交交互，并使用 SharedPreferences
 * 进行多字段本地保存与下次打开自动回显。
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String PREF_NAME = "campus_profile";
    private static final String KEY_NAME = "name";
    private static final String KEY_CLASS = "class";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_SERVICE = "service_type";
    private static final String KEY_NOTE = "note";

    private EditText etName;
    private EditText etClass;
    private EditText etPhone;
    private Spinner spServiceType;
    private EditText etNote;
    private TextView tvResult;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.profile_title);
        }

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        etName = findViewById(R.id.etName);
        etClass = findViewById(R.id.etClass);
        etPhone = findViewById(R.id.etPhone);
        spServiceType = findViewById(R.id.spServiceType);
        etNote = findViewById(R.id.etNote);
        tvResult = findViewById(R.id.tvResult);

        Button btnSave = findViewById(R.id.btnSave);
        Button btnQuery = findViewById(R.id.btnQuery);
        Button btnClear = findViewById(R.id.btnClear);

        btnSave.setOnClickListener(v -> saveProfile());
        btnQuery.setOnClickListener(v -> showSavedProfile());
        btnClear.setOnClickListener(v -> clearProfile());

        // APP 再次打开时自动读取上次保存的信息并回显
        restoreProfile();
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            etName.setError("请输入姓名");
            etName.requestFocus();
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_CLASS, etClass.getText().toString().trim());
        editor.putString(KEY_PHONE, etPhone.getText().toString().trim());
        editor.putString(KEY_SERVICE, getSelectedServiceType());
        editor.putString(KEY_NOTE, etNote.getText().toString().trim());
        editor.apply();

        Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
        showSavedProfile();
    }

    /** 将已保存的信息回填到输入控件中。 */
    private void restoreProfile() {
        if (!prefs.contains(KEY_NAME)) {
            tvResult.setText(R.string.no_record);
            return;
        }
        etName.setText(prefs.getString(KEY_NAME, ""));
        etClass.setText(prefs.getString(KEY_CLASS, ""));
        etPhone.setText(prefs.getString(KEY_PHONE, ""));
        etNote.setText(prefs.getString(KEY_NOTE, ""));
        selectServiceType(prefs.getString(KEY_SERVICE, ""));
        showSavedProfile();
    }

    /** 在回显区域展示已保存的完整信息。 */
    private void showSavedProfile() {
        if (!prefs.contains(KEY_NAME)) {
            tvResult.setText(R.string.no_record);
            return;
        }
        String result = "姓名：" + prefs.getString(KEY_NAME, "") + "\n"
                + "班级：" + emptyToDash(prefs.getString(KEY_CLASS, "")) + "\n"
                + "联系电话：" + emptyToDash(prefs.getString(KEY_PHONE, "")) + "\n"
                + "服务类型：" + emptyToDash(prefs.getString(KEY_SERVICE, "")) + "\n"
                + "备注：" + emptyToDash(prefs.getString(KEY_NOTE, ""));
        tvResult.setText(result);
    }

    private void clearProfile() {
        prefs.edit().clear().apply();
        etName.setText("");
        etClass.setText("");
        etPhone.setText("");
        etNote.setText("");
        spServiceType.setSelection(0);
        tvResult.setText(R.string.no_record);
        Toast.makeText(this, "已清空保存的信息", Toast.LENGTH_SHORT).show();
    }

    private String getSelectedServiceType() {
        Object item = spServiceType.getSelectedItem();
        return item == null ? "" : item.toString();
    }

    private void selectServiceType(String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) spServiceType.getAdapter();
        if (adapter == null) {
            return;
        }
        for (int i = 0; i < adapter.getCount(); i++) {
            Object item = adapter.getItem(i);
            if (item != null && value.equals(item.toString())) {
                spServiceType.setSelection(i);
                return;
            }
        }
    }

    private String emptyToDash(String value) {
        return TextUtils.isEmpty(value) ? "—" : value;
    }
}
