package com.example.smartbudget;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class AddRecordActivity extends AppCompatActivity {

    private EditText etAmount, etNote;
    private Spinner spType;
    private TextView tvDate;
    private Button btnSave, btnManageCategory;
    private DBHelper dbHelper;

    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        spType = findViewById(R.id.spType);
        tvDate = findViewById(R.id.tvDate);
        btnSave = findViewById(R.id.btnSave);
        btnManageCategory = findViewById(R.id.btnManageCategory); // 新增的按钮

        dbHelper = new DBHelper(this);

        // 加载类别列表到下拉框
        loadCategoryToSpinner();

        // 管理类别按钮，点击跳转类别管理页
        btnManageCategory.setOnClickListener(v -> {
            Intent intent = new Intent(AddRecordActivity.this, CategoryManageActivity.class);
            startActivity(intent);
        });

        // 设置默认日期为今天
        Calendar calendar = Calendar.getInstance();
        selectedDate = formatDate(calendar);
        tvDate.setText(selectedDate);

        // 日期选择器
        tvDate.setOnClickListener(view -> {
            DatePickerDialog dialog = new DatePickerDialog(AddRecordActivity.this,
                    (view1, year, month, dayOfMonth) -> {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, dayOfMonth);
                        selectedDate = formatDate(c);
                        tvDate.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        // 保存按钮
        btnSave.setOnClickListener(v -> saveRecord());
    }

    // 页面返回时自动刷新类别列表
    @Override
    protected void onResume() {
        super.onResume();
        loadCategoryToSpinner();
    }

    // 动态加载类别到Spinner
    private void loadCategoryToSpinner() {
        ArrayList<String> categoryList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM category", null);
        while (cursor.moveToNext()) {
            categoryList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);
    }

    private String formatDate(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // 一定要两位数！
        return String.format("%04d-%02d-%02d", year, month, day);
    }


    private void saveRecord() {
        String amountStr = etAmount.getText().toString().trim();
        String type = spType.getSelectedItem() != null ? spType.getSelectedItem().toString() : "";
        String note = etNote.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        if (type.isEmpty()) {
            Toast.makeText(this, "请选择类别", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("type", type);
        values.put("note", note);
        values.put("date", selectedDate);

        long result = db.insert("record", null, values);
        db.close();

        if (result != -1) {
            Toast.makeText(this, "添加成功！", Toast.LENGTH_SHORT).show();
            finish();  // 关闭页面，返回主页
        } else {
            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
        }
    }
}
