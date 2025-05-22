package com.example.smartbudget;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private PieChart pieChart;
    private DBHelper dbHelper;
    private Button btnMonth, btnYear, btnAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        pieChart = findViewById(R.id.pieChart);
        dbHelper = new DBHelper(this);

        btnMonth = findViewById(R.id.btnMonth);
        btnYear = findViewById(R.id.btnYear);
        btnAll = findViewById(R.id.btnAll);

        btnMonth.setOnClickListener(v -> loadChartData("month"));
        btnYear.setOnClickListener(v -> loadChartData("year"));
        btnAll.setOnClickListener(v -> loadChartData("all"));

        // 默认显示本月
        loadChartData("month");
    }

    private void loadChartData(String mode) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        String sql;
        String[] args;
        if ("month".equals(mode)) {
            String yearMonth = String.format("%04d-%02d", year, month);
            sql = "SELECT type, SUM(amount) as total FROM record WHERE date LIKE ? GROUP BY type";
            args = new String[]{yearMonth + "%"};
        } else if ("year".equals(mode)) {
            String yearStr = String.valueOf(year); // 2024
            sql = "SELECT type, SUM(amount) as total FROM record WHERE date LIKE ? GROUP BY type";
            args = new String[]{yearStr + "%"};
        } else {
            sql = "SELECT type, SUM(amount) as total FROM record GROUP BY type";
            args = null;
        }

        Cursor cursor = (args != null) ? db.rawQuery(sql, args) : db.rawQuery(sql, null);

        float sum = 0f;
        while (cursor.moveToNext()) {
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            float total = (float) cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            if (total > 0f) { // 忽略为0的分类
                entries.add(new PieEntry(total, type));
                sum += total;
            }
        }
        cursor.close();
        db.close();

        PieDataSet dataSet = new PieDataSet(entries, "");
        // 配色：高对比度主色
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#2186F5")); // 蓝色
        colors.add(Color.parseColor("#FFB300")); // 橙色
        colors.add(Color.parseColor("#43D19E")); // 绿色
        colors.add(Color.parseColor("#AA66CC")); // 紫
        colors.add(Color.parseColor("#FF5252")); // 红
        colors.add(Color.parseColor("#7E57C2")); // 补色
        dataSet.setColors(colors);

        dataSet.setSliceSpace(3f);          // 扇区间隙
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(15f);
        dataSet.setDrawValues(true);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // 美化PieChart
        pieChart.setUsePercentValues(true); // 显示百分比
        pieChart.setDrawHoleEnabled(true);  // 空心环形
        pieChart.setHoleRadius(62f);
        pieChart.setTransparentCircleRadius(70f);
        pieChart.setCenterTextColor(Color.parseColor("#333333"));
        pieChart.setCenterTextSize(17f);

        // 中心文字：总支出
        String centerTitle;
        if ("month".equals(mode)) {
            centerTitle = "本月支出\n¥" + String.format("%.2f", sum);
        } else if ("year".equals(mode)) {
            centerTitle = "本年支出\n¥" + String.format("%.2f", sum);
        } else {
            centerTitle = "总支出\n¥" + String.format("%.2f", sum);
        }
        pieChart.setCenterText(centerTitle);

        pieChart.setDrawEntryLabels(false); // 不显示扇区label
        pieChart.getDescription().setEnabled(false); // 关掉右下角描述

        // 动画
        pieChart.animateY(900, Easing.EaseInOutQuad);

        // 图例Legend美化
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(14f);
        legend.setFormSize(16f);

        pieChart.invalidate();
    }
}
