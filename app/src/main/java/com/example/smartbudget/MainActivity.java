package com.example.smartbudget;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome, tvTotal, tvBudgetInfo, tvTip;
    private ProgressBar progressBudget;
    private Button btnAdd, btnHistory, btnChart, btnCategory, btnSetBudget;
    private LinearLayout layoutRecent;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvTotal = findViewById(R.id.tvTotal);
        tvBudgetInfo = findViewById(R.id.tvBudgetInfo);
        progressBudget = findViewById(R.id.progressBudget);
        btnAdd = findViewById(R.id.btnAdd);
        btnHistory = findViewById(R.id.btnHistory);
        btnChart = findViewById(R.id.btnChart);
        btnCategory = findViewById(R.id.btnCategory);
        layoutRecent = findViewById(R.id.layoutRecent);
        tvTip = findViewById(R.id.tvTip);
        btnSetBudget = findViewById(R.id.btnSetBudget);

        dbHelper = new DBHelper(this);

        btnSetBudget.setOnClickListener(v -> showSetBudgetDialog());
        Button btnUserInfo = findViewById(R.id.btnUserInfo);
        btnUserInfo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
            startActivity(intent);
        });

        btnAdd.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddRecordActivity.class)));
        btnHistory.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HistoryActivity.class)));
        btnChart.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChartActivity.class)));
        btnCategory.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CategoryManageActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        showUserInfo();
        showTotalExpense();
        showBudgetBar();
        showRecentRecords();
        showRandomTip();
    }

    /*** 设置预算弹窗 ***/
    private void showSetBudgetDialog() {
        EditText etInput = new EditText(this);
        etInput.setHint("请输入本月预算金额");
        etInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        new AlertDialog.Builder(this)
                .setTitle("设置本月预算")
                .setView(etInput)
                .setPositiveButton("确定", (dialog, which) -> {
                    String budgetStr = etInput.getText().toString().trim();
                    if (!budgetStr.isEmpty()) {
                        double budget = Double.parseDouble(budgetStr);
                        saveBudgetToDb(budget);
                        showUserInfo();
                        showBudgetBar();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /*** 保存预算到数据库 ***/
    private void saveBudgetToDb(double budget) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user_info LIMIT 1", null);
        if (cursor.moveToFirst()) {
            db.execSQL("UPDATE user_info SET budget=? WHERE id=?",
                    new Object[]{budget, cursor.getInt(cursor.getColumnIndexOrThrow("id"))});
        } else {
            db.execSQL("INSERT INTO user_info (budget) VALUES (?)", new Object[]{budget});
        }
        cursor.close();
        db.close();
    }

    // 显示欢迎语和预算
    private void showUserInfo() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user_info LIMIT 1", null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            tvWelcome.setText("欢迎你，" + name);
        } else {
            tvWelcome.setText("欢迎使用 SmartBudget！");
        }
        cursor.close();
        db.close();
    }

    // 显示本月总支出
    private void showTotalExpense() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        String yearMonth = String.format("%04d-%02d", year, month);
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM record WHERE date LIKE ?", new String[]{yearMonth + "%"});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        tvTotal.setText("本月总支出：￥" + String.format("%.2f", total));
        cursor.close();
        db.close();
    }

    // 显示预算条与剩余额度
    private void showBudgetBar() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double budget = 0;
        Cursor cursor = db.rawQuery("SELECT budget FROM user_info LIMIT 1", null);
        if (cursor.moveToFirst()) {
            budget = cursor.getDouble(cursor.getColumnIndexOrThrow("budget"));
        }
        cursor.close();

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        String yearMonth = String.format("%04d-%02d", year, month);
        Cursor cursor2 = db.rawQuery("SELECT SUM(amount) FROM record WHERE date LIKE ?", new String[]{yearMonth + "%"});
        double used = 0;
        if (cursor2.moveToFirst()) {
            used = cursor2.getDouble(0);
        }
        cursor2.close();
        db.close();

        double remain = budget - used;
        if (remain < 0) remain = 0;

        tvBudgetInfo.setText("本月预算：￥" + String.format("%.2f", budget)
                + " | 已用：￥" + String.format("%.2f", used)
                + " | 剩余：￥" + String.format("%.2f", remain));

        if (budget > 0) {
            int percent = (int) Math.min(used * 100 / budget, 100);
            progressBudget.setProgress(percent);
            if (used > budget) {
                progressBudget.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFFFF4444)); // 红色
            } else {
                progressBudget.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFFFF9800)); // 橙色
            }
        } else {
            progressBudget.setProgress(0);
            progressBudget.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFFCCCCCC)); // 灰色
        }
    }

    // 显示最近账单摘要（取最近3条）
    private void showRecentRecords() {
        layoutRecent.removeAllViews();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT type, amount, note, date FROM record ORDER BY date DESC LIMIT 3", null);
        int count = 0;
        while (cursor.moveToNext()) {
            String type = cursor.getString(0);
            double amount = cursor.getDouble(1);
            String note = cursor.getString(2);
            String date = cursor.getString(3);

            TextView tv = new TextView(this);
            tv.setText((count + 1) + ". " + type + " ￥" + amount
                    + (note.isEmpty() ? "" : " [" + note + "]")
                    + " - " + date);
            tv.setTextSize(15f);
            tv.setTextColor(0xFF555555);
            layoutRecent.addView(tv);
            count++;
        }
        cursor.close();
        db.close();

        if (count == 0) {
            TextView tv = new TextView(this);
            tv.setText("暂无账单记录");
            tv.setTextSize(15f);
            tv.setTextColor(0xFFAAAAAA);
            layoutRecent.addView(tv);
        }
    }

    // 随机理财小贴士
    private void showRandomTip() {
        String[] tips = {
                "每天省下一笔，就是理财的第一步！",
                "记账是财富管理的起点，坚持每天记录。",
                "合理规划支出，让每一分钱都花得有意义。",
                "设立月度预算，养成良好的消费习惯。",
                "常看账单，发现你的“隐形消费”！",
                "投资自己，才是最划算的支出。"
        };
        Random rand = new Random();
        int idx = rand.nextInt(tips.length);
        tvTip.setText(tips[idx]);
    }
}
