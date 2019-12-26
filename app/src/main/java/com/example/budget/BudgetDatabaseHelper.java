package com.example.budget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BudgetDatabaseHelper extends SQLiteOpenHelper {

    public BudgetDatabaseHelper(Context context) {
        super(context, "budget", null, 18);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sqlPaymentinToday = "CREATE TABLE paymenttable ("
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "payment TEXT, "
                + "paymentname TEXT, "
                + "paymentdate TEXT);";


        String sqlPaymentinMonth = "CREATE TABLE monthlypaymenttable ("
                + "mID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "mpayment TEXT, "
                + "mpaymentdate TEXT);";


        String sqlPaymentinProfile = "CREATE TABLE profilepaymenttable ("
                + "pID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "ppayment TEXT, "
                + "ppaymentname TEXT);";

        Log.d("BudgetDatabaseHelper", "SQL" + sqlPaymentinToday);
        Log.d("BudgetDatabaseHelper", "SQL" + sqlPaymentinMonth);
        Log.d("BudgetDatabaseHelper", "SQL" + sqlPaymentinProfile);

        db.execSQL(sqlPaymentinToday);
        db.execSQL(sqlPaymentinMonth);
        db.execSQL(sqlPaymentinProfile);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insertPaymentOfTodayPage(String payment, String paymentName, String paymentDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues paymentValues = new ContentValues();

        paymentValues.put("payment", payment);
        paymentValues.put("paymentname", paymentName);
        paymentValues.put("paymentdate", paymentDate);

        long l = db.insert("paymenttable", null, paymentValues);


        db.close();
    }

    public List<PaymentDataOfTodayPage> getAllTodayPagePaymentData() {

        List<PaymentDataOfTodayPage> paymentDataOfTodayPageList = new ArrayList<PaymentDataOfTodayPage>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery( "select * from paymenttable", null);
        while(cursor.moveToNext()) {
            paymentDataOfTodayPageList.add(new PaymentDataOfTodayPage(cursor.getString(1), cursor.getString(2), cursor.getString(3)));
        }
        //items are collected as id - 1,2,3,..- in database
        //list that will displayed collect items from old to new
        //but this is not usable so I reverse paymentDataOfTodayPageList collection.
        Collections.reverse(paymentDataOfTodayPageList);
        return paymentDataOfTodayPageList;
    }

    public void clearTodayPagePaymentTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("paymenttable", null, null);
        db.close();
    }

    public void deleteRowOfTodayPagePaymentTable(String payment, String paymentname, String paymentdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("" +
                "DELETE FROM " + "paymenttable" +
                " WHERE " + "payment" + "= '" + payment + "'" +
                " and " + "paymentname" + "= '" + paymentname + "'" +
                " and " + "paymentdate" + "= '" + paymentdate +
                "'");
        db.close();
    }

    public void insertPaymentOfMonthPage(String mpayment, String mpaymentDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues mpaymentValues = new ContentValues();

        mpaymentValues.put("mpayment", mpayment);
        //mpaymentValues.put("mpaymentName", mpaymentName);
        mpaymentValues.put("mpaymentdate", mpaymentDate);

        long l = db.insert("monthlypaymenttable", null, mpaymentValues);

        db.close();
    }

    public List<PaymentDataOfMonthPage> getAllMonthPagePaymentData() {
        List<PaymentDataOfMonthPage> paymentDataOfMonthPageList = new ArrayList<PaymentDataOfMonthPage>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor mcursor = db.rawQuery("select * from monthlypaymenttable", null);
        while(mcursor.moveToNext()) {
            paymentDataOfMonthPageList.add(new PaymentDataOfMonthPage(mcursor.getString(1), mcursor.getString(2)));
        }

        Collections.reverse(paymentDataOfMonthPageList);
        return paymentDataOfMonthPageList;
    }

    public void clearMonthPagePaymentTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("monthlypaymenttable", null, null);
        db.close();
    }

    public void deleteRowOfMonthPagePaymentTable(String mpayment, String mpaymentdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("" +
                "DELETE FROM " + "monthlypaymenttable" +
                " WHERE " + "mpayment" + "= '" + mpayment + "'" +
                " and " + "mpaymentdate" + "= '" + mpaymentdate +
                "'");
        db.close();
    }

    public void insertPaymentOfProfilePage(String ppayment, String ppaymentName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues ppaymentValues = new ContentValues();

        ppaymentValues.put("ppayment", ppayment);
        ppaymentValues.put("ppaymentname", ppaymentName);

        long l = db.insert("profilepaymenttable", null, ppaymentValues);

        db.close();
    }

    public List<PaymentDataOfProfilePage> getAllProfilePagePaymentData() {
        List<PaymentDataOfProfilePage> paymentDataOfProfilePageList = new ArrayList<PaymentDataOfProfilePage>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor pcursor = db.rawQuery("select * from profilepaymenttable", null);
        while(pcursor.moveToNext()) {
            paymentDataOfProfilePageList.add(new PaymentDataOfProfilePage(pcursor.getString(1), pcursor.getString(2)));
        }

        //Collections.reverse(paymentDataOfProfilePageList);
        return paymentDataOfProfilePageList;
    }

    public void clearProfilePagePaymentTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("profilepaymenttable", null, null);
        db.close();
    }

    public void deleteRowOfProfilePagePaymentTable(String ppayment, String ppaymentname) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("" +
                "DELETE FROM " + "profilepaymenttable" +
                " WHERE " + "ppayment" + "= '" + ppayment + "'" +
                " and " + "ppaymentname" + "= '" + ppaymentname +
                "'");
        db.close();
    }

}
