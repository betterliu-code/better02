package com.campuslife.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 个人信息登记 Fragment：实现输入、提交交互，并使用 SharedPreferences
 * 进行多字段本地保存与下次打开自动回显。
 */
public class ProfileFragment extends Fragment {

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        etName = view.findViewById(R.id.etName);
        etClass = view.findViewById(R.id.etClass);
        etPhone = view.findViewById(R.id.etPhone);
        spServiceType = view.findViewById(R.id.spServiceType);
        etNote = view.findViewById(R.id.etNote);
        tvResult = view.findViewById(R.id.tvResult);

        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnQuery = view.findViewById(R.id.btnQuery);
        Button btnClear = view.findViewById(R.id.btnClear);

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

        Toast.makeText(requireContext(), R.string.save_success, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(requireContext(), "已清空保存的信息", Toast.LENGTH_SHORT).show();
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
