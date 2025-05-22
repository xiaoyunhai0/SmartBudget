package com.example.smartbudget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Intent;

import java.util.List;

public class RecordAdapter extends BaseAdapter {

    private Context context;
    private List<Record> recordList;

    public RecordAdapter(Context context, List<Record> recordList) {
        this.context = context;
        this.recordList = recordList;
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int position) {
        return recordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return recordList.get(position).id;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(context).inflate(R.layout.item_record, parent, false);
        TextView tvType = view.findViewById(R.id.tvType);
        TextView tvAmount = view.findViewById(R.id.tvAmount);
        TextView tvNote = view.findViewById(R.id.tvNote);
        TextView tvDate = view.findViewById(R.id.tvDate);
        Button btnDelete = view.findViewById(R.id.btnDelete); // ✅ 找到删除按钮

        Record record = recordList.get(position);

        tvType.setText("类型：" + record.type);
        tvAmount.setText("金额：￥" + record.amount);
        tvNote.setText("备注：" + record.note);
        tvDate.setText("日期：" + record.date);

        view.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditRecordActivity.class);
            intent.putExtra("record_id", record.id); // 传入当前记录的 ID
            context.startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            // 删除逻辑
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("record", "id = ?", new String[]{String.valueOf(record.id)});
            db.close();

            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();

            recordList.remove(position);       // 从列表中移除
            notifyDataSetChanged();            // 刷新 ListView
        });

        return view;
    }

}
