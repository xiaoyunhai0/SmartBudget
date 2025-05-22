package com.example.smartbudget;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EditRecordActivity extends AppCompatActivity {

    private EditText etAmount, etNote;
    private Spinner spType;
    private TextView tvDate;
    private Button btnUpdate;
    private DBHelper dbHelper;

    private int recordId;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);

        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        spType = findViewById(R.id.spType);
        tvDate = findViewById(R.id.tvDate);
        btnUpdate = findViewById(R.id.btnUpdate);

        dbHelper = new DBHelper(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);

        // 获取传入的账单 ID
        recordId = getIntent().getIntExtra("record_id", -1);
        if (recordId != -1) {
            loadRecordFromDatabase();
        }

        tvDate.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(EditRecordActivity.this,
                    (view1, year, month, dayOfMonth) -> {
                        selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        tvDate.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        btnUpdate.setOnClickListener(v -> updateRecord());
    }

    private void loadRecordFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM record WHERE id = ?", new String[]{String.valueOf(recordId)});
        if (cursor.moveToFirst()) {
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
            selectedDate = cursor.getString(cursor.getColumnIndexOrThrow("date"));

            etAmount.setText(String.valueOf(amount));
            etNote.setText(note);
            tvDate.setText(selectedDate);

            // 设置 Spinner 默认选中项
            int index = ((ArrayAdapter) spType.getAdapter()).getPosition(type);
            spType.setSelection(index);
        }
        cursor.close();
        db.close();
    }

    private void updateRecord() {
        String amountStr = etAmount.getText().toString().trim();
        String type = spType.getSelectedItem().toString();
        String note = etNote.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("type", type);
        values.put("note", note);
        values.put("date", selectedDate);

        int result = db.update("record", values, "id = ?", new String[]{String.valueOf(recordId)});
        db.close();

        if (result > 0) {
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
        }
    }
}
