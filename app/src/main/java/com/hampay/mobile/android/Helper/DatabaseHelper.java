package com.hampay.mobile.android.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hampay.mobile.android.model.RecentPay;
import com.hampay.mobile.android.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amir on 6/13/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    // Logcat tag
    private static final String LOG = DatabaseHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "hampay";

    // Table Names
    private static final String TABLE_RECENT_PAY = "recent_pay";

    // Recent Pay Table - column names
    private static final String KEY_ID = "id";
    private static final String KEY_STATUS = "status";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_MESSAGE = "message";

    // Todo table create statement
    private static final String CREATE_TABLE_RECENT_PAY = "CREATE TABLE "
            + TABLE_RECENT_PAY + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_STATUS + " TEXT,"
            + KEY_NAME + " TEXT,"
            + KEY_PHONE + " TEXT,"
            + KEY_MESSAGE + " TEXT"
            + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_RECENT_PAY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_PAY);

        // create new tables
        onCreate(db);
    }

    public long createRecentPAy(RecentPay recentPay) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, recentPay.getStatus());
        values.put(KEY_NAME, recentPay.getName());
        values.put(KEY_MESSAGE, recentPay.getMessage());
        values.put(KEY_PHONE, recentPay.getPhone());

        // insert row
        long recent_pay_id = db.insert(TABLE_RECENT_PAY, null, values);

        return recent_pay_id;
    }


    public RecentPay getRecentPay(String phone_no) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_RECENT_PAY + " WHERE "
                + KEY_PHONE + " = '" + phone_no + "'";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        RecentPay recentPay = new RecentPay();
        recentPay.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        recentPay.setStatus((c.getString(c.getColumnIndex(KEY_STATUS))));
        recentPay.setName(c.getString(c.getColumnIndex(KEY_NAME)));
        recentPay.setPhone(c.getString(c.getColumnIndex(KEY_PHONE)));
        recentPay.setMessage(c.getString(c.getColumnIndex(KEY_MESSAGE)));

        return recentPay;
    }

    public boolean getExistRecentPay(String phone_no) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_RECENT_PAY + " WHERE "
                + KEY_PHONE + " = '" + phone_no + "'";
        Log.e(LOG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0)
            return true;
        else
            return false;

    }


    public int updateRecentPay(RecentPay recentPay) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE, recentPay.getMessage());
        return db.update(TABLE_RECENT_PAY, values, KEY_PHONE + " = ?",
                new String[] { recentPay.getPhone() });
    }

    public List<RecentPay> getAllRecentPays() {
        List<RecentPay> recentPays = new ArrayList<RecentPay>();
        String selectQuery = "SELECT  * FROM " + TABLE_RECENT_PAY + " LIMIT " + Constants.RECENT_PAY_TO_ONE_LIMIT;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                RecentPay recentPay = new RecentPay();
                recentPay.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                recentPay.setStatus((c.getString(c.getColumnIndex(KEY_STATUS))));
                recentPay.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                recentPay.setPhone(c.getString(c.getColumnIndex(KEY_PHONE)));
                recentPay.setMessage(c.getString(c.getColumnIndex(KEY_MESSAGE)));

                // adding to todo list
                recentPays.add(recentPay);
            } while (c.moveToNext());
        }

        return recentPays;
    }

}
