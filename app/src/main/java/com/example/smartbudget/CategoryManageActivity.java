package com.example.smartbudget;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CategoryManageActivity extends AppCompatActivity {

    private EditText etNewCategory;
    private Button btnAddCategory;
    private ListView listCategory;
    private DBHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_manage);

        etNewCategory = findViewById(R.id.etNewCategory);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        listCategory = findViewById(R.id.listCategory);

        dbHelper = new DBHelper(this);

        loadCategory();

        btnAddCategory.setOnClickListener(v -> {
            String newCategory = etNewCategory.getText().toString().trim();
            if (!newCategory.isEmpty()) {
                addCategory(newCategory);
                etNewCategory.setText("");
                loadCategory();
            }
        });

        listCategory.setOnItemLongClickListener((parent, view, position, id) -> {
            String category = categoryList.get(position);
            if (!category.equals("餐饮") && !category.equals("购物") && !category.equals("交通")
                    && !category.equals("娱乐") && !category.equals("其他")) {
                deleteCategory(category);
                loadCategory();
                Toast.makeText(this, "已删除：" + category, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "默认类别不可删除", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void loadCategory() {
        categoryList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM category", null);
        while (cursor.moveToNext()) {
            categoryList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoryList);
        listCategory.setAdapter(adapter);
    }

    private void addCategory(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insert("category", null, values);
        db.close();
    }

    private void deleteCategory(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("category", "name=?", new String[]{name});
        db.close();
    }
}
