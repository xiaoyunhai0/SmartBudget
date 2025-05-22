package com.example.smartbudget;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class UserInfoActivity extends AppCompatActivity {

    private EditText etName, etBudget;
    private Button btnSave;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        etName = findViewById(R.id.etName);
        etBudget = findViewById(R.id.etBudget);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new DBHelper(this);

        loadUserInfo(); // 尝试读取已有用户数据

        btnSave.setOnClickListener(v -> saveUserInfo());
    }

    private void loadUserInfo() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user_info LIMIT 1", null);
        if (cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            etBudget.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("budget"))));
        }
        cursor.close();
    }

    private void saveUserInfo() {
        String name = etName.getText().toString().trim();
        String budgetStr = etBudget.getText().toString().trim();

        if (name.isEmpty() || budgetStr.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        double budget = Double.parseDouble(budgetStr);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM user_info"); // 保证只有一个用户
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("budget", budget);
        long result = db.insert("user_info", null, values);
        if (result != -1) {
            Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }
}
