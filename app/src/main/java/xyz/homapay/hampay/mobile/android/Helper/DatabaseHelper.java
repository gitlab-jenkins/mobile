package xyz.homapay.hampay.mobile.android.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.mobile.android.model.SyncPspResult;

/**
 * Created by amir on 6/13/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    // Logcat tag
    private static final String LOG = DatabaseHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "hampay";

    // Table Names
    private static final String TABLE_VIEWED_PAYMENT_REQUEST = "viewed_payment_request";
    private static final String TABLE_VIEWED_PURCHASE_REQUEST = "viewed_purchase_request";
    private static final String TABLE_SYNC_PSP_RESULT = "sync_psp_result";

    // Viewed Payment Request
    private static final String KEY_VIEWED_PAYMENT_REQUEST_ID = "payment_id";
    private static final String KEY_VIEWED_PAYMENT_REQUEST_CODE = "payment_code";

    // Viewed Purchase Request
    private static final String KEY_VIEWED_PURCHASE_REQUEST_ID = "purchase_id";
    private static final String KEY_VIEWED_PURCHASE_REQUEST_CODE = "purchase_code";

    // Sync PSP Result
    private static final String KEY_SYNC_PSP_ID = "id";
    private static final String KEY_SYNC_PSP_RESPONSE_CODE = "response_code";
    private static final String KEY_SYNC_PSP_PRODUCT_CODE = "product_code";
    private static final String KEY_SYNC_PSP_SWTRACE = "swtrace";
    private static final String KEY_SYNC_PSP_TYPE = "type";
    private static final String KEY_SYNC_PSP_TIME_STAMP = "timestamp";
    private static final String KEY_SYNC_PSP_STATUS = "status";
    private static final String KEY_SYNC_PSP_CARD_ID = "card_id";
    private static final String KEY_SYNC_PSP_NAME = "psp_name";


    // Sync PSP result table create statement
    private static final String CREATE_TABLE_SYNC_PSP_RESULT = "CREATE TABLE "
            + TABLE_SYNC_PSP_RESULT + "("
            + KEY_SYNC_PSP_ID + " INTEGER PRIMARY KEY,"
            + KEY_SYNC_PSP_RESPONSE_CODE + " TEXT,"
            + KEY_SYNC_PSP_PRODUCT_CODE + " TEXT,"
            + KEY_SYNC_PSP_SWTRACE + " TEXT,"
            + KEY_SYNC_PSP_TYPE + " TEXT,"
            + KEY_SYNC_PSP_TIME_STAMP + " INTEGER,"
            + KEY_SYNC_PSP_STATUS + " INTEGER,"
            + KEY_SYNC_PSP_CARD_ID + " TEXT,"
            + KEY_SYNC_PSP_NAME + " TEXT"
            + ")";


    //Viewed payment request table create statement
    private static final String CREATE_TABLE_VIEWED_PAYMENT_REQUEST = "CREATE TABLE "
            + TABLE_VIEWED_PAYMENT_REQUEST + "("
            + KEY_VIEWED_PAYMENT_REQUEST_ID + " INTEGER PRIMARY KEY,"
            + KEY_VIEWED_PAYMENT_REQUEST_CODE + " TEXT"
            + ")";

    private static final String CREATE_TABLE_VIEWED_PURCHASE_REQUEST = "CREATE TABLE "
            + TABLE_VIEWED_PURCHASE_REQUEST + "("
            + KEY_VIEWED_PURCHASE_REQUEST_ID + " INTEGER PRIMARY KEY,"
            + KEY_VIEWED_PURCHASE_REQUEST_CODE + " TEXT"
            + ")";

    Context context;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_VIEWED_PAYMENT_REQUEST);
        db.execSQL(CREATE_TABLE_VIEWED_PURCHASE_REQUEST);
        db.execSQL(CREATE_TABLE_SYNC_PSP_RESULT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYNC_PSP_RESULT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIEWED_PAYMENT_REQUEST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIEWED_PURCHASE_REQUEST);
        onCreate(db);
    }



    public void deleteAllDataBase(){
        context.deleteDatabase(DATABASE_NAME);
    }

    public long createViewedPaymentRequest(String paymentCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VIEWED_PAYMENT_REQUEST_CODE, paymentCode);
        long viewed_payment_request_id = db.insert(TABLE_VIEWED_PAYMENT_REQUEST, null, values);
        return viewed_payment_request_id;
    }

    public boolean checkPaymentRequest(String paymentCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_VIEWED_PAYMENT_REQUEST + " WHERE "
                + KEY_VIEWED_PAYMENT_REQUEST_CODE + " = '" + paymentCode + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount() > 0;
    }

    public long createViewedPurchaseRequest(String purchaseCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VIEWED_PURCHASE_REQUEST_CODE, purchaseCode);
        long viewed_purchase_request_id = db.insert(TABLE_VIEWED_PURCHASE_REQUEST, null, values);
        return viewed_purchase_request_id;
    }

    public boolean checkPurchaseRequest(String purchaseCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_VIEWED_PURCHASE_REQUEST + " WHERE "
                + KEY_VIEWED_PURCHASE_REQUEST_CODE + " = '" + purchaseCode + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount() > 0;
    }

    public long createSyncPspResult(SyncPspResult syncPspResult) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SYNC_PSP_RESPONSE_CODE, syncPspResult.getResponseCode());
        values.put(KEY_SYNC_PSP_PRODUCT_CODE, syncPspResult.getProductCode());
        values.put(KEY_SYNC_PSP_SWTRACE, syncPspResult.getSwTrace());
        values.put(KEY_SYNC_PSP_TYPE, syncPspResult.getType());
        values.put(KEY_SYNC_PSP_TIME_STAMP, syncPspResult.getTimestamp());
        values.put(KEY_SYNC_PSP_STATUS, syncPspResult.getStatus());
        values.put(KEY_SYNC_PSP_CARD_ID, syncPspResult.getCardId());
        values.put(KEY_SYNC_PSP_NAME, syncPspResult.getPspName());
        long sync_psp_result_id = db.insert(TABLE_SYNC_PSP_RESULT, null, values);
        return sync_psp_result_id;
    }

    public List<SyncPspResult> allSyncPspResult() {
        List<SyncPspResult> syncPspResults = new ArrayList<SyncPspResult>();
        String selectQuery = "SELECT  * FROM " + TABLE_SYNC_PSP_RESULT + " WHERE "
                + KEY_SYNC_PSP_STATUS + " = " + 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                SyncPspResult syncPspResult = new SyncPspResult();
                syncPspResult.setId(cursor.getInt(cursor.getColumnIndex(KEY_SYNC_PSP_ID)));
                syncPspResult.setResponseCode(cursor.getString(cursor.getColumnIndex(KEY_SYNC_PSP_RESPONSE_CODE)));
                syncPspResult.setProductCode(cursor.getString(cursor.getColumnIndex(KEY_SYNC_PSP_PRODUCT_CODE)));
                syncPspResult.setSwTrace(cursor.getString(cursor.getColumnIndex(KEY_SYNC_PSP_SWTRACE)));
                syncPspResult.setType(cursor.getString(cursor.getColumnIndex(KEY_SYNC_PSP_TYPE)));
                syncPspResult.setTimestamp(cursor.getLong(cursor.getColumnIndex(KEY_SYNC_PSP_TIME_STAMP)));
                syncPspResult.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_SYNC_PSP_STATUS)));
                syncPspResult.setCardId(cursor.getString(cursor.getColumnIndex(KEY_SYNC_PSP_CARD_ID)));
                syncPspResult.setPspName(cursor.getString(cursor.getColumnIndex(KEY_SYNC_PSP_NAME)));
                syncPspResults.add(syncPspResult);
            } while (cursor.moveToNext());
        }
        return syncPspResults;
    }

    public int syncPspResult(String productCode) {
        SQLiteDatabase db = this.getWritableDatabase();

        final SQLiteStatement statement = db.compileStatement("UPDATE " + TABLE_SYNC_PSP_RESULT
                + " SET " + KEY_SYNC_PSP_STATUS + "=?"
                + " WHERE " + KEY_SYNC_PSP_PRODUCT_CODE + "=?");

        statement.bindLong(1, 1);
        statement.bindString(2, productCode);

        if (Build.VERSION.SDK_INT >= 11) {
            return statement.executeUpdateDelete();
        }else {
            statement.execute();
            return 1;
        }
    }

}
