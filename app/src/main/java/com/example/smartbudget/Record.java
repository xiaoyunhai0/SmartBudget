package com.example.smartbudget;

public class Record {
    public int id;
    public double amount;
    public String type;
    public String note;
    public String date;

    public Record(int id, double amount, String type, String note, String date) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.note = note;
        this.date = date;
    }
}
