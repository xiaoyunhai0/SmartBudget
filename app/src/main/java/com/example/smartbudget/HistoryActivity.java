package com.example.smartbudget;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class HistoryActivity extends AppCompatActivity {

    private ListView listView;
    private DBHelper dbHelper;
    private RecordAdapter adapter;
    private ArrayList<Record> recordList;
    private Button btnMonth, btnYear, btnAll;
    private EditText etSearch;
    private String currentMode = "month"; // 当前筛选模式
    private String currentKeyword = "";   // 当前关键字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listView);
        btnMonth = findViewById(R.id.btnMonth);
        btnYear = findViewById(R.id.btnYear);
        btnAll = findViewById(R.id.btnAll);
        etSearch = findViewById(R.id.etSearch);

        dbHelper = new DBHelper(this);

        btnMonth.setOnClickListener(v -> {
            currentMode = "month";
            loadRecords();
        });
        btnYear.setOnClickListener(v -> {
            currentMode = "year";
            loadRecords();
        });
        btnAll.setOnClickListener(v -> {
            currentMode = "all";
            loadRecords();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentKeyword = s.toString().trim();
                loadRecords();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // 默认显示本月
        loadRecords();
    }

    private void loadRecords() {
        recordList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        String sql;
        ArrayList<String> argsList = new ArrayList<>();

        // 时间筛选
        if ("month".equals(currentMode)) {
            String yearMonth = String.format("%04d-%02d", year, month);
            sql = "SELECT * FROM record WHERE date LIKE ?";
            argsList.add(yearMonth + "%");
        } else if ("year".equals(currentMode)) {
            String yearStr = String.valueOf(year);
            sql = "SELECT * FROM record WHERE date LIKE ?";
            argsList.add(yearStr + "%");
        } else {
            sql = "SELECT * FROM record WHERE 1=1";
        }

        // 关键字筛选
        if (!currentKeyword.isEmpty()) {
            sql += " AND (note LIKE ? OR type LIKE ?)";
            argsList.add("%" + currentKeyword + "%");
            argsList.add("%" + currentKeyword + "%");
        }

        sql += " ORDER BY date DESC";

        String[] args = argsList.toArray(new String[0]);
        Cursor cursor = db.rawQuery(sql, args);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

            recordList.add(new Record(id, amount, type, note, date));
        }
        cursor.close();
        db.close();

        adapter = new RecordAdapter(this, recordList);
        listView.setAdapter(adapter);
    }
}
